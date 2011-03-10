package se.vgregion.activation.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;

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
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.formbeans.PasswordFormBean;

@Controller
@RequestMapping(value = "VIEW")
public class AccountActivationController {

    private Validator passwordMatchValidator;
    private Validator otpLoginValidator;
    private Validator dominoLoginValidator;

    @Resource
    private AccountService accountService;

    @InitBinder("passwordFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(passwordMatchValidator);
        binder.setValidator(otpLoginValidator);
        binder.setValidator(dominoLoginValidator);
    }

    public AccountActivationController(Validator otpLoginValidator, Validator dominoLoginValidator,
            Validator passwordMatchValidator) {
        this.otpLoginValidator = otpLoginValidator;
        this.passwordMatchValidator = passwordMatchValidator;
        this.dominoLoginValidator = dominoLoginValidator;
    }

    @RenderMapping(params = { "oneTimePassword" })
    public String showPasswordFormForOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
            Model model) {
        return vadlidateAndShowForm(otpLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = { "oneTimePassword" })
    public void activateAccountWithOTP(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
            ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("oneTimePassword", new String[] { passwordFormBean.getOneTimePassword() });
        setNewPassword(passwordFormBean, result, response, model, renderParams);
    }

    @RenderMapping(params = { "vgrId" })
    public String showPasswordFormForDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
            BindingResult result, Model model) {
        return vadlidateAndShowForm(dominoLoginValidator, passwordFormBean, result, model);
    }

    @ActionMapping(params = { "vgrId" })
    public void activateAccountAsDominoUser(@ModelAttribute PasswordFormBean passwordFormBean,
            BindingResult result, ActionResponse response, Model model) {
        Map<String, String[]> renderParams = new HashMap<String, String[]>();
        renderParams.put("vgrId", new String[] { passwordFormBean.getVgrId() });
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
            callSetPassword(passwordFormBean.getPassword());
            response.setRenderParameter("success", "true");
        }
    }

    @RenderMapping(params = { "success" })
    public String success(@ModelAttribute PasswordFormBean passwordFormBean, Model model) {
        model.asMap().clear();
        PublicHash publicHash = new PublicHash(passwordFormBean.getOneTimePassword());
        model.addAttribute("postbackUrl", accountService.getCustomUrl(publicHash));
        return "successForm";
    }

    private void callSetPassword(String newPassword) {
        System.out.println("pw: " + newPassword);
    }
}
