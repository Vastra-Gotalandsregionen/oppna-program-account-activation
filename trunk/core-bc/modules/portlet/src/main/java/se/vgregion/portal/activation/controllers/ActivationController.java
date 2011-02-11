package se.vgregion.portal.activation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import model.ActivationValidationResponse;

@Controller
@RequestMapping("VIEW")
public class ActivationController {

    /**
     * Gatekeeper that validates if accessing password form should be possible.
     *
     * @param userIdentifier - vgrid or email adress, needed for Domino validation,
     *                         not used by #onetime password validation.
     * @param activationToken - #onetime code or Domino user password.
     * @return password form or error form.
     */
    @RenderMapping
    public String showPasswordForm(ModelMap model,
                                   @RequestParam(value = "userIdentifier", required = false) String userIdentifier,
                                   @RequestParam(value = "activationToken") String activationToken) {

        ActivationValidationResponse response = validate(userIdentifier, activationToken);

        if (response.isValid()) {
            model.addAttribute("vgrId", response.getVgrId());

            return "passwordForm";
        } else {
            model.addAttribute("errorReason", response.getErrorReason());

            return "errorForm";
        }

    }

    private ActivationValidationResponse validate(String userIdentifier, String activationPassword) {
        ActivationValidationResponse response = new ActivationValidationResponse(true, "susro3", "");

        return response;
    }
}
