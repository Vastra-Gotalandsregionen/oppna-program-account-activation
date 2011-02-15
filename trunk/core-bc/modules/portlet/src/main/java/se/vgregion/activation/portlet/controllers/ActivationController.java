package se.vgregion.activation.portlet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.activation.domain.ActivationValidationResponse;
import se.vgregion.activation.domain.form.PasswordFormBean;
import se.vgregion.activation.domain.form.ValidationFormBean;

import javax.portlet.ActionResponse;
import java.io.IOException;

@Controller
@RequestMapping("VIEW")
public class ActivationController {

    /**
     * Gatekeeper that validates if accessing password form should be possible.
     *
     * @param userIdentifier - vgrid or email adress, needed for Domino validation,
     *                         not used by #onetime password validation.
     * @param validationToken - #onetime code or Domino user password.
     * @return password form or error form.
     */
    @RenderMapping
    public ModelAndView showPasswordForm(ModelMap model,
                                   @RequestParam(value = "userIdentifier", required = false) String userIdentifier,
                                   @RequestParam(value = "validationToken", required = false) String validationToken) {

        if (validationToken == null) {
            System.out.println("Validation");
            model.addAttribute("validationFormBean", new ValidationFormBean());
            return new ModelAndView("validationForm", model);
        }

        ActivationValidationResponse response = validate(userIdentifier, validationToken);

        if (response.isValid()) {
            System.out.println("Aktivation");
            PasswordFormBean passwordFormBean = new PasswordFormBean();
            passwordFormBean.setVgrId(response.getVgrId());
            model.addAttribute("passwordFormBean", passwordFormBean);

            return new ModelAndView("passwordForm", model);
        } else {
            System.out.println("Error");
            model.addAttribute("errorReason", response.getErrorReason());

            return new ModelAndView("errorForm", model);
        }

    }

    private ActivationValidationResponse validate(String userIdentifier, String activationPassword) {
        ActivationValidationResponse response = new ActivationValidationResponse(true, "susro3", "");

        return response;
    }

    @ActionMapping("validation")
    public void validationAction(@ModelAttribute("validationFormBean") ValidationFormBean validationFormBean,
                                 ActionResponse response,
                                 ModelMap model) throws IOException {

        model.addAttribute("validationToken", validationFormBean.getOnetimeToken());

        response.sendRedirect("test");
    }
}
