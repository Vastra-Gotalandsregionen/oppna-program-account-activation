package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.account.services.StructureService;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.validators.ExternalUserValidator;
import se.vgregion.portal.*;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("VIEW")
public class InviteController {

    @Autowired
    private ExternalUserValidator externalUserValidator;

    @Autowired
    private JaxbUtil jaxbUtil;

    @Autowired
    private StructureService structureService;

    /*@InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(externalUserValidator);
    }*/

    @RequestMapping
    public String showExternalUserInvite(@ModelAttribute ExternalUserFormBean externalUserFormBean,
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

    @ResourceMapping
    public @ResponseBody List<String> searchStructure(String query, PortletResponse res) {
        return structureService.search(query);
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
        // validate indata
        String loggedInUser = lookupLoggedin(req);
        externalUserValidator.validateWithLoggedInUser(externalUserFormBean, result, loggedInUser);
        if (result.hasErrors()) {
//            model.addAttribute("org.springframework.validation.BindingResult.externalUserFormBean", result);
            model.addAttribute("errors", result);
            return;
        }

        // call Mule
        try {
            CreateUserResponse createUserResponse = callCreateUser(externalUserFormBean);

            // ?: new user -> pw + invite
            // ?: user already in PK -> has outstanding invite -> possible reinvite or pw + invite
            // ?: user already in PK -> no outstanding invites -> direct invite

        } catch (MessageBusException e) {
            e.printStackTrace();
        }


    }

    private CreateUserResponse callCreateUser(ExternalUserFormBean externalUserFormBean) throws MessageBusException {
        CreateUser createUser = new CreateUser();
        createUser.setUserName(externalUserFormBean.getName());
        createUser.setUserSurname(externalUserFormBean.getSurname());
        createUser.setUserMail(externalUserFormBean.getEmail());

        Message message = new Message();
        message.setPayload(jaxbUtil.marshal(createUser));
        message.setResponseDestinationName("vgr/account_create.REPLY");

        String response = (String) MessageBusUtil.sendSynchronousMessage("vgr/account_create", message, 5000);
        return jaxbUtil.unmarshal(response);
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