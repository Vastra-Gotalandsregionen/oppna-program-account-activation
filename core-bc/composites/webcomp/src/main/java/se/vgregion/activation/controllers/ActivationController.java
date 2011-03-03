package se.vgregion.activation.controllers;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.activation.formbeans.ValidationFormBean;
import se.vgregion.activation.validators.FieldMatchValidator;

@Controller
@RequestMapping("VIEW")
public class ActivationController {
    private AccountService accountService;

    @Resource(type = FieldMatchValidator.class)
    private Validator validator;

    @Autowired
    public ActivationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @InitBinder("passwordFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RenderMapping
    public ModelAndView showOneTimePasswordForm(ModelMap model,
            @ModelAttribute("validationFormBean") ValidationFormBean validationFormBean) {
        System.out.println("Validation");

        model.addAttribute("validationFormBean", new ValidationFormBean());
        return new ModelAndView("validationForm", model);
    }

    @RenderMapping(params = { "oneTimePassword" })
    public ModelAndView showPasswordForm(@RequestParam("oneTimePassword") String oneTimePassword, ModelMap model) {

        PublicHash publicHash = new PublicHash(oneTimePassword);
        OneTimePassword account = accountService.getAccount(publicHash);
        String vgrId = account.getVgrId();

        // if (vgrId != oneTimePassword.vgrId) then (were to?)
        if (!validateOneTimePassword(vgrId, account, model)) {
            return new ModelAndView("errorForm", model);
        }

        PasswordFormBean passwordFormBean = new PasswordFormBean();
        passwordFormBean.setVgrId(vgrId);
        passwordFormBean.setOneTimePassword(oneTimePassword);

        model.addAttribute("passwordFormBean", passwordFormBean);

        return new ModelAndView("passwordForm", model);
    }

    private boolean validateOneTimePassword(String vgrId, OneTimePassword account, ModelMap model) {
        if (account == null) {
            model.addAttribute("errorMessage", "Activation code does not exist");
            return false;
        } else if (!account.isValid()) {
            model.addAttribute("errorMessage", "Activation code has already been used");
            return false;
        } else if (account.hasExpired()) {
            model.addAttribute("errorMessage", "Activation code has expired");
            return false;
        } else if (!vgrId.equals(account.getVgrId())) {
            model.addAttribute("errorMessage", "Inproper use of Activation code");
            return false;
        }
        return true;
    }

    @ActionMapping("activate")
    public void activate(@ModelAttribute PasswordFormBean passwordFormBean, BindingResult result,
            ActionResponse response, Model model) {

        validator.validate(passwordFormBean, result);

        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : result.getGlobalErrors()) {
                sb.append("global: " + error.getObjectName() + " " + error.getDefaultMessage() + ";");
            }
            for (ObjectError error : result.getFieldErrors()) {
                sb.append("filed: " + error.getObjectName() + " " + error.getDefaultMessage() + ";");
            }
            System.out.println("err: " + sb.toString());
            model.addAttribute("errorMessage", sb.toString());
            model.addAttribute("org.springframework.validation.BindingResult.passwordFormBean", result);
            model.addAttribute("errors", result);

            response.setRenderParameter("oneTimePassword", passwordFormBean.getOneTimePassword());
            response.setRenderParameter("vgrId", passwordFormBean.getVgrId());
        } else {
            callSetPassword(passwordFormBean.getPassword());
            response.setRenderParameter("success", "true");
        }
    }

    @RenderMapping(params = { "success" })
    public String success() {
        return "successForm";
    }

    @RenderMapping(params = { "password" })
    public String password() {
        return "passwordForm";
    }

    private void callSetPassword(String newPassword) {
        System.out.println("pw: " + newPassword);
    }
}
