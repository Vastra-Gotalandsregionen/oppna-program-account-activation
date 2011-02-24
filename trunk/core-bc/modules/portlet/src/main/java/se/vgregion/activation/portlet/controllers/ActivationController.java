package se.vgregion.activation.portlet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.ActivationValidationResponse;
import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.domain.form.PasswordFormBean;
import se.vgregion.activation.domain.form.ValidationFormBean;

import javax.portlet.ActionResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("VIEW")
public class ActivationController {

    @Autowired
    AccountService accountService;


    /**
     * Gatekeeper that validates if accessing password form should be possible.
     *
     * @param userIdentifier  - vgrid or email adress, needed for Domino validation,
     *                        not used by #onetime password validation.
     * @param validationToken - #onetime code or Domino user password.
     * @return password form or error form.
     */
    @RenderMapping
    public ModelAndView showOneTimePasswordForm(ModelMap model, @ModelAttribute("validationFormBean") ValidationFormBean validationFormBean) {
        System.out.println("Validation");

        System.out.println(validationFormBean == null ? "null" : validationFormBean.getOneTimePassword());
        System.out.println(validationFormBean == null ? "null" : validationFormBean.getVgrId());

        System.out.println(model.get("oneTimePassword"));
        System.out.println(model.get("vgrId"));

        model.addAttribute("validationFormBean", new ValidationFormBean());
        return new ModelAndView("validationForm", model);
    }

    @RenderMapping(params = {"oneTimePassword"})
    public ModelAndView showPasswordForm(ModelMap model,
                                         @ModelAttribute("oneTimePassword") String oneTimePassword) {

        PublicHash publicHash = new PublicHash(oneTimePassword);
        String vgrId = accountService.getAccount(publicHash).getVgrId();

        model.addAttribute("vgrId", accountService.getAccount(publicHash).getVgrId());
        return new ModelAndView("passwordForm", model);
    }

    @RenderMapping(params = {"oneTimePassword", "vgrId"})
    public ModelAndView showPasswordForm(ModelMap model,
                                         @RequestParam("oneTimePassword") String oneTimePassword,
                                         @RequestParam("vgrId") String vgrId) {
        System.out.println("b: " + oneTimePassword);

        PublicHash publicHash = new PublicHash(oneTimePassword);
        OneTimePassword account = accountService.getAccount(publicHash);

        if (!validate(vgrId, account, model)) {
            return new ModelAndView("errorForm", model);
        }

        System.out.println("Aktivation");
        PasswordFormBean passwordFormBean = new PasswordFormBean();
        passwordFormBean.setVgrId(vgrId);
        model.addAttribute("passwordFormBean", passwordFormBean);

        return new ModelAndView("passwordForm", model);
    }

    private boolean validate(String vgrId, OneTimePassword account, ModelMap model) {
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
            model.addAttribute("errorMessage", "Activation code has already been used");
            return false;
        }
        return true;
    }

    @ActionMapping("validation")
    public void validationAction(@ModelAttribute("validationFormBean") ValidationFormBean validationFormBean,
                                 ActionResponse response,
                                 ModelMap model) throws IOException {

        String oneTimePassword = validationFormBean.getOneTimePassword();
        System.out.println("a: " + oneTimePassword);
        response.setRenderParameter("oneTimePassword", oneTimePassword);

        PublicHash publicHash = new PublicHash(oneTimePassword);
        OneTimePassword account = accountService.getAccount(publicHash);

        if (account == null) {
            response.setRenderParameter("vgrId", "");
            System.out.println("haloj");
        } else {
            String vgrId = account.getVgrId();
            response.setRenderParameter("vgrId", vgrId);
        }
    }

    @ActionMapping("activate")
    public void activate(@ModelAttribute("passwordFormBean") @Valid PasswordFormBean passwordFormBean,
                         BindingResult result,
                         ActionResponse response) {

        if (result.hasErrors()) {
            for (ObjectError err : result.getAllErrors()) {
                System.out.println(err.toString());
            }
            response.setRenderParameter("password", "true");
            return;
        }

        callSetPassword(passwordFormBean.getPassword());

        response.setRenderParameter("success", "true");
    }

    @RenderMapping(params = {"success"})
    public String success() {
        return "successForm";
    }

    @RenderMapping(params = {"password"})
    public String password() {
        return "passwordForm";
    }

    private void callSetPassword(String newPassword) {
        System.out.println("pw: " + newPassword);
    }
}
