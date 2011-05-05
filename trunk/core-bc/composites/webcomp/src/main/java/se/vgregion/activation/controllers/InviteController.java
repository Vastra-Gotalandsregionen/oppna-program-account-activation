package se.vgregion.activation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.validators.ExternalUserValidator;

import javax.portlet.PortletRequest;
import java.util.Map;

@Controller
@RequestMapping("VIEW")
public class InviteController {

    @Autowired
    private ExternalUserValidator externalUserValidator;

    @InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(externalUserValidator);
    }

    @RequestMapping
    public String showExternalUserInvite(@ModelAttribute ExternalUserFormBean externalUserFormBean, Model model,
                                         PortletRequest req) {
        if (externalUserFormBean.getSponsorVgrId() == null) {
            //Meaning first request
            Map<String, String> userInfo = (Map<String, String>) req.getAttribute(PortletRequest.USER_INFO);
            String userId;
            if (userInfo != null) {
                userId = userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
            } else {
                throw new IllegalStateException("Du måste vara inloggad.");
            }
            externalUserFormBean.setSponsorVgrId(userId);
        }
        return "externalUserInvite";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("externalUserError");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ActionMapping
    public void inviteWithActivationCode(@ModelAttribute ExternalUserFormBean externalUserFormBean) {
        // validate indata
        // 1: sponsor == logged in user
        // 2: no outstanding invites
        // 3: user already in PK
        // 4: mandatory information provided

        System.out.println(externalUserFormBean.toString());


    }
}