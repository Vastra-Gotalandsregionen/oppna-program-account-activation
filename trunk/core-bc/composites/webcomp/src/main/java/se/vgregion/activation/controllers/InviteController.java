package se.vgregion.activation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import se.vgregion.activation.formbeans.ExternalUserFormBean;

@Controller
@RequestMapping("VIEW")
public class InviteController {

    @RequestMapping
    public String showExternalUserInvite(@ModelAttribute ExternalUserFormBean externalUserFormBean, Model model) {
        if (externalUserFormBean == null) {
            model.addAttribute("externalUserFormBean", new ExternalUserFormBean());
        }
        return "externalUserInvite";
    }

    @ActionMapping
    public void inviteWithActivationCode(@ModelAttribute ExternalUserFormBean externalUserFormBean) {
        // validate indata
        // 1: sponsor == logged in user
        // 2: no outstanding invites
        // 3: user already in PK
        // 4: mandatory information provided


    }
}
