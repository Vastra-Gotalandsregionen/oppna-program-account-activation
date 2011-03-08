package se.vgregion.activation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping(value = "VIEW")
public class LoginController {

    @RenderMapping
    public String showDefaultPasswordForm(Model model) {
        return "otpLogin";
    }

    @RenderMapping(params = { "loginType=otp" })
    public String showOtpPasswordForm(Model model) {
        return "otpLogin";
    }

    @RenderMapping(params = { "loginType=domino" })
    public String showDominoPasswordForm() {
        return "dominoLogin";
    }

}
