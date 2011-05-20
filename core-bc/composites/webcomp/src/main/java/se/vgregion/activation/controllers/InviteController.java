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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.account.services.StructureService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.activation.validators.ExternalUserValidator;
import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.portal.createuser.CreateUser;
import se.vgregion.portal.createuser.CreateUserResponse;
import se.vgregion.portal.createuser.CreateUserStatusCodeType;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("VIEW")
public class InviteController {

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Autowired
    private InvitePreferencesPropertyEditor invitePreferencesPropertyEditor;

    @Autowired
    private ExternalUserValidator externalUserValidator;

    @Autowired
    private JaxbUtil jaxbUtil;

    @Autowired
    private StructureService structureService;

    @InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(InvitePreferences.class, invitePreferencesPropertyEditor);
    }

    @RequestMapping
    public String showExternalUserInvite(@ModelAttribute ExternalUserFormBean externalUserFormBean,
                                         Model model,
                                         PortletRequest req) {
        if (externalUserFormBean.getSponsorVgrId() == null) {
            //Meaning first request
            String userId = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
            if (userId == null) {
                throw new IllegalStateException("Du måste vara inloggad.");
            }
            String sponsorName = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_NAME_GIVEN);
            String sponsorFamily = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_NAME_FAMILY);
            externalUserFormBean.setSponsorVgrId(userId);
            externalUserFormBean.setSponsorFullName(String.format("%s %s", sponsorName, sponsorFamily));
        }

        List<InvitePreferences> invitePreferenceses = (List<InvitePreferences>) invitePreferencesService.findAll();
        model.addAttribute("invitePreferences", invitePreferenceses);

        // Workaround to get the errors form validation in actionrequest
        Errors errors = (Errors) model.asMap().get("errors");
        if (errors != null) {
            model.addAttribute("org.springframework.validation.BindingResult.externalUserFormBean", errors);
        }

        return "externalUserInvite";
    }

    @ResourceMapping
    public void searchStructure(@RequestParam String query, ResourceResponse res) {
        Collection<String> structures = structureService.search(query);
        try {
            OutputStream outputStream = res.getPortletOutputStream();
            res.setContentType("application/json");
            new ObjectMapper().writeValue(outputStream, structures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RenderMapping(params = {"unresponsive"})
    public String unresponsive(@RequestParam(value = "unresponsive") String failureCode, Model model) {
        model.addAttribute("message", failureCode);
        return "inviteTimeout";
    }

    @RenderMapping(params = {"error"})
    public String inviteError(@RequestParam(value = "error") String errorMessage, Model model) {
        model.addAttribute("message", errorMessage);
        return "inviteError";
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
        String loggedInUser = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
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
                response.setRenderParameter("success", "true");
                structureService.storeExternStructurePersonDn(externalUserFormBean.getExternStructurePersonDn());
            } else if (statusCode == CreateUserStatusCodeType.EXISTING_USER) {
                // ?: user already in PK -> no/has outstanding invite ->  invite
                response.setRenderParameter("success", "true");
                structureService.storeExternStructurePersonDn(externalUserFormBean.getExternStructurePersonDn());
            } else if (statusCode == CreateUserStatusCodeType.ERROR){
                // error -> cannot invite
                response.setRenderParameter("error", createUserResponse.getMessage());
            }

        } catch (MessageBusException e) {
            // ?: timeout try again later
            handleMessageBusException(e, response);
//            response.setRenderParameter("inviteTimeout", "true");
//            e.printStackTrace();
        }
    }

    private void handleMessageBusException(MessageBusException e, ActionResponse response) {
        Throwable rootCause = e.getCause();
        if (rootCause instanceof ConnectException) {
            response.setRenderParameter("unresponsive", "connection.failed");
        } else if (rootCause instanceof UnknownHostException) {
            response.setRenderParameter("unresponsive", "host.unknown");
        } else if (e.getMessage().startsWith("No reply received for message")) {
            response.setRenderParameter("unresponsive", "request.timeout");
        } else {
            response.setRenderParameter("unresponsive", "unknown.exception");
        }

    }

    @RenderMapping(params = {"success"})
    public String success() {
        return "success";
    }

    private CreateUserResponse callCreateUser(ExternalUserFormBean externalUserFormBean) throws MessageBusException {
        CreateUser createUser = new CreateUser();
        createUser.setUserFirstName(externalUserFormBean.getName());
        createUser.setUserSurName(externalUserFormBean.getSurname());
        createUser.setUserMail(externalUserFormBean.getEmail());

        Message message = new Message();
        message.setPayload(jaxbUtil.marshal(createUser));

        Object response = MessageBusUtil.sendSynchronousMessage("vgr/account_create", message, 7000);
        if (response instanceof Exception) {
            throw new MessageBusException((Exception) response);
        } else if (response instanceof String) {
            return jaxbUtil.unmarshal((String) response);
        } else {
            throw new MessageBusException("Unknown response type: " + response.getClass());
        }
    }


    private String lookupP3PInfo(PortletRequest req, PortletRequest.P3PUserInfos p3pInfo) {
        Map<String, String> userInfo = (Map<String, String>) req.getAttribute(PortletRequest.USER_INFO);
        String info;
        if (userInfo != null) {
            info = userInfo.get(p3pInfo.toString());
        } else {
            return null;
        }
        return info;
    }
}