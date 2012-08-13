package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.ActivateUserFailedException;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.portal.activateuser.ActivateUser;
import se.vgregion.portal.activateuser.ActivateUserResponse;
import se.vgregion.portal.activateuser.ActivateUserStatusCodeType;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "VIEW")
public class AccountActivationController {

    private Validator passwordMatchValidator;
    private Validator passwordStrengthValidator;
    private Validator accountActivationLoginValidator;
    private Validator dominoLoginValidator;

    @Resource
    private AccountService accountService;

    private JaxbUtil jaxbUtil = new JaxbUtil("se.vgregion.portal.activateuser");

    /**
     * Method used by Spring.
     *
     * @param binder binder
     */
    @InitBinder("passwordFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(passwordMatchValidator);
        binder.setValidator(accountActivationLoginValidator);
        binder.setValidator(dominoLoginValidator);
    }

    /**
     * Constructor.
     *
     * @param otpLoginValidator         otpLoginValidator
     * @param dominoLoginValidator      dominoLoginValidator
     * @param passwordMatchValidator    passwordMatchValidator
     * @param passwordStrengthValidator passwordStrengthValidator
     */
    public AccountActivationController(Validator otpLoginValidator, Validator dominoLoginValidator,
                                       Validator passwordMatchValidator, Validator passwordStrengthValidator) {
        this.accountActivationLoginValidator = otpLoginValidator;
        this.passwordMatchValidator = passwordMatchValidator;
        this.passwordStrengthValidator = passwordStrengthValidator;
        this.dominoLoginValidator = dominoLoginValidator;
    }

    /**
     * Handler method.
     *
     * @param passwordFormBean passwordFormBean
     * @param result           result
     * @param model            model
     * @return A view
     */
    @RenderMapping(params = { "activationCode" })
    public String showPasswordFormForOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                         Model model) {
        return validateAndShowForm(accountActivationLoginValidator, passwordFormBean, result, model);
    }

    /**
     * Handler method.
     *
     * @param passwordFormBean passwordFormBean
     * @param result           result
     * @param response         response
     * @param model            model
     */
    @ActionMapping(params = { "activationCode" })
    public void activateAccountWithOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
                                       ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("activationCode", new String[]{passwordFormBean.getActivationCode()});
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    /**
     * Handler method.
     *
     * @param passwordFormBean passwordFormBean
     * @param result           result
     * @param model            model
     * @return A view
     */
    @RenderMapping(params = { "vgrId" })
    public String showPasswordFormForDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
                                                BindingResult result, Model model) {
        return validateAndShowForm(dominoLoginValidator, passwordFormBean, result, model);
    }

    /**
     * Handler method.
     *
     * @param passwordFormBean passwordFormBean
     * @param result           result
     * @param response         response
     * @param model            model
     */
    @ActionMapping(params = { "vgrId" })
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

        passwordStrengthValidator.validate(passwordFormBean, result);
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
                // explicit response failure
                response.setRenderParameter("failure", "unknown.system.error");
                response.setRenderParameter("failureArguments", e.getMessage());
            } catch (MessageBusException e) {
                // timeout
                handleMessageBusException(e, response);
            }
        }
    }

    private void handleMessageBusException(MessageBusException e, ActionResponse response) {
        e.printStackTrace();
        Throwable rootCause = e.getCause();
        if (rootCause instanceof ConnectException) {
            response.setRenderParameter("failure", "connection.failed");
        } else if (rootCause instanceof UnknownHostException) {
            response.setRenderParameter("failure", "host.unknown");
        } else if (e.getMessage().startsWith("No reply received for message")) {
            response.setRenderParameter("failure", "request.timeout");
        } else {
            response.setRenderParameter("failure", "unknown.system.error");
            response.setRenderParameter("failureArguments", e.getMessage());
        }

    }

    /**
     * Handler method.
     *
     * @param passwordFormBean passwordFormBean
     * @param model            model
     * @return A view
     */
    @RenderMapping(params = { "success" })
    public String success(@ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        model.asMap().clear();
        ActivationCode activationCode = new ActivationCode(passwordFormBean.getActivationCode());
        model.addAttribute("postbackUrl", accountService.getCustomUrl(activationCode));
        model.addAttribute("passwordFormBean", passwordFormBean);
        return "successForm";
    }

    /**
     * Handler method.
     *
     * @param failureCode      failureCode
     * @param failureArguments failureArguments
     * @param passwordFormBean passwordFormBean
     * @param model            model
     * @return A view
     */
    @RenderMapping(params = { "failure" })
    public String failure(@RequestParam(value = "failure") String failureCode,
                          @RequestParam(value = "failureArguments", required = false) String failureArguments,
                          @ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        model.addAttribute("message", failureCode);
        model.addAttribute("messageArguments", failureArguments);
        return "failureForm";
    }

    private void callSetPassword(PasswordFormBean passwordFormBean) throws ActivateUserFailedException,
            MessageBusException {
        String activationCode = passwordFormBean.getActivationCode();
        ActivationAccount activationAccount = accountService.getAccount(new ActivationCode(activationCode));

        ActivateUser activateUser = new ActivateUser();
        activateUser.setUserId(activationAccount.getVgrId());
        activateUser.setActivationCode(passwordFormBean.getActivationCode());
        activateUser.setUserPassword(passwordFormBean.getPassword());
//        activateUser.setUserMail("");

        passwordFormBean.setVgrId(activationAccount.getVgrId());

        Message message = new Message();
        message.setPayload(jaxbUtil.marshal(activateUser));

        ActivateUserResponse activateUserResponse;

        final int timeout = 7000;
        Object response = MessageBusUtil.sendSynchronousMessage("vgr/account_activation", message, timeout);
        if (response instanceof Exception) {
            throw new MessageBusException((Exception) response);
        } else if (response instanceof String) {
            activateUserResponse = jaxbUtil.unmarshal((String) response);
        } else {
            throw new MessageBusException("Unknown response type: "
                    + response == null ? "null" : response.getClass().getName());
        }

        if (activateUserResponse.getStatusCode() != ActivateUserStatusCodeType.SUCCESS) {
            throw new ActivateUserFailedException(activateUserResponse.getMessage());
        }
    }

}
