package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping(value = "VIEW")
public class LoginController {

    @RenderMapping
    public String showDefaultPasswordForm(Model model) {
        Message msg = new Message();
        msg.setDestinationName("liferay/test");
        msg.setPayload("Apaapapapapapapapa");
        MessageBusUtil.sendMessage("liferay/test", msg);
        System.out.println("Message sent");


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
