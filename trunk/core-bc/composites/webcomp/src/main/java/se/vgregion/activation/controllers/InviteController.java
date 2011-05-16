package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.account.services.StructureService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.activation.validators.ExternalUserValidator;
import se.vgregion.portal.CreateUser;
import se.vgregion.portal.CreateUserResponse;
import se.vgregion.portal.CreateUserStatusCodeType;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

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
    public void searchStructure(@RequestParam String query, ResourceResponse res) {
        Set<String> structures = structureService.search(query);
        try {
            OutputStream outputStream = res.getPortletOutputStream();
            res.setContentType("application/json");
            new ObjectMapper().writeValue(outputStream, structures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RenderMapping(params = {"inviteError"})
    public String inviteError() {
        return "inviteError";
    }

    @RenderMapping(params = {"inviteTimeout"})
    public String inviteTimeout() {
        return "inviteTimeout";
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
                       PortletRequest req, ActionResponse response) {
        // validate indata
        String loggedInUser = lookupLoggedin(req);
        externalUserValidator.validateWithLoggedInUser(externalUserFormBean, result, loggedInUser);

        // Workaround to preserve Spring validation errors
        if (result.hasErrors()) {
            model.addAttribute("errors", result);
            return;
        }

        // call Mule
        try {
            CreateUserResponse createUserResponse = callCreateUser(externalUserFormBean);

            CreateUserStatusCodeType statusCode = createUserResponse.getStatusCode();
            if (statusCode == CreateUserStatusCodeType.NEW_USER) {
                // ?: new user -> invite

            } else if (statusCode == CreateUserStatusCodeType.EXISTING_USER) {
                // ?: user already in PK -> no/has outstanding invite ->  invite
            } else {
                // error -> cannot invite
                response.setRenderParameter("inviteError", "true");
            }

        } catch (MessageBusException e) {
            // ?: timeout try again later
            response.setRenderParameter("inviteTimeout", "true");
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

        String response = (String) MessageBusUtil.sendSynchronousMessage("vgr/account_create", message, 7000);
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