package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.portal.ActivateUser;
import se.vgregion.portal.ActivateUserResponse;
import se.vgregion.portal.ActivateUserStatusCodeType;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import java.util.*;

@Controller
@RequestMapping(value = "VIEW")
public class AccountActivationController {

    private Validator passwordMatchValidator;
    private Validator accountActivationLoginValidator;
    private Validator dominoLoginValidator;

    @Resource
    private AccountService accountService;

    @Autowired
    private JaxbUtil jaxbUtil;

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

    @RenderMapping(params = {"activationCode"})
    public String showPasswordFormForOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                         Model model) {
        return validateAndShowForm(accountActivationLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = {"activationCode"})
    public void activateAccountWithOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                       ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("activationCode", new String[]{passwordFormBean.getActivationCode()});
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    @RenderMapping(params = {"vgrId"})
    public String showPasswordFormForDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
                                                BindingResult result, Model model) {
        return validateAndShowForm(dominoLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = {"vgrId"})
    public void activateAccountAsDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
                                            BindingResult result, ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("vgrId", new String[]{passwordFormBean.getVgrId()});
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    private String validateAndShowForm(Validator validator, @ModelAttribute PasswordFormBean passwordFormBean,
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
        ActivationCode publicHash = new ActivationCode(passwordFormBean.getActivationCode());
        model.addAttribute("postbackUrl", accountService.getCustomUrl(publicHash));
        return "successForm";
    }

    @RenderMapping(params = {"failure"})
    public String failure(@ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        return "failureForm";
    }

    private void callSetPassword(PasswordFormBean passwordFormBean) throws ActivateUserFailedException, MessageBusException {
        String activationCode = passwordFormBean.getActivationCode();
        ActivationAccount activationAccount = accountService.getAccount(new ActivationCode(activationCode));

        ActivateUser activateUser = new ActivateUser();
        activateUser.setUserId(activationAccount.getVgrId());
        activateUser.setActivationCode(passwordFormBean.getActivationCode());
        activateUser.setUserPassword(passwordFormBean.getPassword());
        activateUser.setUserMail("");

        Message message = new Message();
        message.setPayload(jaxbUtil.marshal(activateUser));
        message.setResponseDestinationName("vgr/account_activation.REPLY");

        String response = (String) MessageBusUtil.sendSynchronousMessage("vgr/account_activation", message, 3000);
        ActivateUserResponse activateUserResponse = jaxbUtil.unmarshal(response);
        if (activateUserResponse.getStatusCode() != ActivateUserStatusCodeType.SUCCESS) {
            throw new ActivateUserFailedException(activateUserResponse.getMessage());
        }

        System.out.println("pw: " + passwordFormBean.getPassword());
    }

}
