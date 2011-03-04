package se.vgregion.activation.controllers;

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

import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.activation.validators.AccountValidator;
import se.vgregion.activation.validators.PasswordMatchValidator;

@Controller
@RequestMapping("VIEW")
public class ActivationController {

    @Resource(type = PasswordMatchValidator.class)
    private Validator passwordValidator;

    @Resource(type = AccountValidator.class)
    private Validator accountValidator;

    @InitBinder("passwordFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(passwordValidator);
        binder.setValidator(accountValidator);
    }

    public ActivationController() {
        System.out.println("ActivationController.ActivationController()");
    }

    @RenderMapping
    public String showOneTimePasswordForm() {
        System.out.println("ActivationController.showOneTimePasswordForm()");
        return "validationForm";
    }

    @RenderMapping(params = { "oneTimePassword" })
    public String showPasswordForm(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
            Model model) {

        // Allways valdate onetime password.
        accountValidator.validate(passwordFormBean, result);
        if (result.hasErrors()) {
            return "errorForm";
        }

        // Workaround to get the errors form validation in actionrequest
        if (model.asMap().get("errors") != null) {
            model.addAttribute("org.springframework.validation.BindingResult.passwordFormBean",
                    model.asMap().get("errors"));
        }

        model.addAttribute("passwordFormBean", passwordFormBean);
        return "passwordForm";
    }

    @ActionMapping("activate")
    public void activate(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
            ActionResponse response, Model model) {

        passwordValidator.validate(passwordFormBean, result);
        if (result.hasErrors()) {
            model.addAttribute("errors", result);
            response.setRenderParameter("oneTimePassword", passwordFormBean.getOneTimePassword());
        } else {
            callSetPassword(passwordFormBean.getPassword());
            response.setRenderParameter("success", "true");
        }
    }

    @RenderMapping(params = { "success" })
    public String success() {
        return "successForm";
    }

    private void callSetPassword(String newPassword) {
        System.out.println("pw: " + newPassword);
    }
}
