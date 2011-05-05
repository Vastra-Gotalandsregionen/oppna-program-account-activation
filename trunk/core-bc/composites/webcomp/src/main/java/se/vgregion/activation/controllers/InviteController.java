package se.vgregion.activation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.validators.ExternalUserValidator;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("VIEW")
public class InviteController {

    @Autowired
    private ExternalUserValidator externalUserValidator;

    /*@InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(externalUserValidator);
    }*/

    @RequestMapping
    public String showExternalUserInvite(@ModelAttribute ExternalUserFormBean externalUserFormBean,
                                         BindingResult result,
                                         Model model,
                                         PortletRequest req) {
        if (externalUserFormBean.getSponsorVgrId() == null) {
            //Meaning first request
            String userId = lookupLoggedin(req);
            if (userId == null) {
                throw new IllegalStateException("Du måste vara inloggad.");
            }
            externalUserFormBean.setSponsorVgrId(userId);
        }
        // Workaround to get the errors form validation in actionrequest
        Errors errors = (Errors) model.asMap().get("errors");
        if (errors != null) {
            model.addAttribute("org.springframework.validation.BindingResult.externalUserFormBean", errors);
        }

        return "externalUserInvite";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ex.printStackTrace();
        ModelAndView modelAndView = new ModelAndView("externalUserError");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ActionMapping
    public void invite(@ModelAttribute ExternalUserFormBean externalUserFormBean,
                       BindingResult result,
                       Model model,
                       PortletRequest req, ActionResponse resp) {
        // 1: loggedInUser
        // 2: no outstanding invites
        // 3: user already in PK

        // validate indata
        String loggedInUser = lookupLoggedin(req);
        externalUserValidator.validate(externalUserFormBean, result);
        if (result.hasErrors()) {
//            model.addAttribute("org.springframework.validation.BindingResult.externalUserFormBean", result);
            model.addAttribute("errors", result);
            return;
        }

    }

    private String lookupLoggedin(PortletRequest req) {
        Map<String, String> userInfo = (Map<String, String>) req.getAttribute(PortletRequest.USER_INFO);
        String userId;
        if (userInfo != null) {
            userId = userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        } else {
            return null;
        }
        return userId;
    }
}