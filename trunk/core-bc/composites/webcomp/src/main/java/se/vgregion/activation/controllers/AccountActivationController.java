package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.ActivateUserFailedException;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.portal.ActivateUser;
import se.vgregion.portal.ActivateUserResponse;
import se.vgregion.portal.StatusCode;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@Controller
@RequestMapping(value = "VIEW")
public class AccountActivationController {

    private Validator passwordMatchValidator;
    private Validator accountActivationLoginValidator;
    private Validator dominoLoginValidator;

    @Resource
    private AccountService accountService;

    @InitBinder("passwordFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(passwordMatchValidator);
        binder.setValidator(accountActivationLoginValidator);
        binder.setValidator(dominoLoginValidator);
    }

    public AccountActivationController(Validator otpLoginValidator, Validator dominoLoginValidator,
                                       Validator passwordMatchValidator) {
        this.accountActivationLoginValidator = otpLoginValidator;
        this.passwordMatchValidator = passwordMatchValidator;
        this.dominoLoginValidator = dominoLoginValidator;
    }

    @RenderMapping(params = {"oneTimePassword"})
    public String showPasswordFormForOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                         Model model) {
        return vadlidateAndShowForm(accountActivationLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = {"oneTimePassword"})
    public void activateAccountWithOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                       ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("oneTimePassword", new String[]{passwordFormBean.getOneTimePassword()});
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    @RenderMapping(params = {"vgrId"})
    public String showPasswordFormForDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
                                                BindingResult result, Model model) {
        return vadlidateAndShowForm(dominoLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = {"vgrId"})
    public void activateAccountAsDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
                                            BindingResult result, ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("vgrId", new String[]{passwordFormBean.getVgrId()});
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    private String vadlidateAndShowForm(Validator validator, @ModelAttribute PasswordFormBean passwordFormBean,
                                        BindingResult result, Model model) {

        // Always validate login password.
        validator.validate(passwordFormBean, result);

        if (result.hasErrors()) {
            if ("domino".equals(passwordFormBean.getLoginType())) {
                return "dominoLogin";
            } else {
                return "otpLogin";
            }
        }

        // Workaround to get the errors form validation in actionrequest
        if (model.asMap().get("errors") != null) {
            model.addAttribute("org.springframework.validation.BindingResult.passwordFormBean",
                    model.asMap().get("errors"));
        }
        return "passwordMatchForm";
    }

    private void setNewPassword(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                ActionResponse response, Model model, Map<String, String[]> renderParamters) {

        passwordMatchValidator.validate(passwordFormBean, result);

        if (result.hasErrors()) {
            model.addAttribute("errors", result);
            response.setRenderParameters(renderParamters);
        } else {
            try {
                callSetPassword(passwordFormBean);

                //If callSetPassword does not throw exception
                response.setRenderParameter("success", "true");
            } catch (ActivateUserFailedException e) {
                response.setRenderParameter("failure", "true");
                model.addAttribute("message", e.getMessage());
            } catch (MessageBusException e) {
                response.setRenderParameter("failure", "true");
                model.addAttribute("message", "timeout-message");
            }

        }
    }

    @RenderMapping(params = {"success"})
    public String success(@ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        model.asMap().clear();
        ActivationCode publicHash = new ActivationCode(passwordFormBean.getOneTimePassword());
        model.addAttribute("postbackUrl", accountService.getCustomUrl(publicHash));
        return "successForm";
    }

    @RenderMapping(params = {"failure"})
    public String failure(@ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        return "failureForm";
    }

    private void callSetPassword(PasswordFormBean passwordFormBean) throws ActivateUserFailedException, MessageBusException {
        String activationCode = passwordFormBean.getOneTimePassword();
        ActivationAccount activationAccount = accountService.getAccount(new ActivationCode(activationCode));

        ActivateUser activateUser = new ActivateUser();
        activateUser.setUserId(activationAccount.getVgrId());
        activateUser.setActivationCode(passwordFormBean.getOneTimePassword());
        activateUser.setUserPassword(passwordFormBean.getPassword());

        Message message = new Message();
        message.setPayload(marshal(activateUser));
        message.setResponseDestinationName("vgr/account_activation.REPLY");

        String response = (String) MessageBusUtil.sendSynchronousMessage("vgr/account_activation", message, 3000);
        ActivateUserResponse activateUserResponse = unmarshal(response);
        if (activateUserResponse.getStatusCode() != StatusCode.SUCCESS) {
            throw new ActivateUserFailedException(activateUserResponse.getMessage());
        }

        System.out.println("pw: " + passwordFormBean.getPassword());
    }

    private ActivateUserResponse unmarshal(String xml) {
        try {
            JAXBContext jc = JAXBContext.newInstance("se.vgregion.portal");
            //Create marshaller
            Unmarshaller m = jc.createUnmarshaller();
            //Marshal object into file.
            return (ActivateUserResponse) m.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    private String marshal(ActivateUser activateUser) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jc = JAXBContext.newInstance("se.vgregion.portal");
            //Create marshaller
            Marshaller m = jc.createMarshaller();
            //Marshal object into file.
            m.marshal(activateUser, sw);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
        return sw.toString();
    }
}
