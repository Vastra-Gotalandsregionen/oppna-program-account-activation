package se.vgregion.activation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping(value = "VIEW")
public class LoginController {

    /**
     * Default handler method.
     *
     * @param model model
     * @return A view
     */
    @RenderMapping
    public String showDefaultPasswordForm(Model model) {
        return "otpLogin";
    }

    /**
     * Handler method for OTP login.
     *
     * @param model model
     * @return A view
     */
    @RenderMapping(params = { "loginType=otp" })
    public String showOtpPasswordForm(Model model) {
        return "otpLogin";
    }

    /**
     * Handler method for domino login.
     *
     * @return A view
     */
    @RenderMapping(params = { "loginType=domino" })
    public String showDominoPasswordForm() {
        return "dominoLogin";
    }

}
