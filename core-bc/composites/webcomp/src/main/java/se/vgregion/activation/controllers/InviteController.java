package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
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
import se.vgregion.portal.inviteuser.InviteUser;
import se.vgregion.portal.inviteuser.InviteUserResponse;
import se.vgregion.portal.inviteuser.InviteUserStatusCodeType;

import javax.portlet.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("externalUserFormBean")
@RequestMapping("VIEW")
public class InviteController {
    Logger logger = LoggerFactory.getLogger(InviteController.class);

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Autowired
    private InvitePreferencesPropertyEditor invitePreferencesPropertyEditor;

    @Autowired
    private ExternalUserValidator externalUserValidator;

    private JaxbUtil createUserJaxbUtil = new JaxbUtil("se.vgregion.portal.createuser");
    private JaxbUtil inviteUserJaxbUtil = new JaxbUtil("se.vgregion.portal.inviteuser");

    @Autowired
    private StructureService structureService;

    @InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(InvitePreferences.class, invitePreferencesPropertyEditor);
    }

    @ModelAttribute("externalUserFormBean")
    public ExternalUserFormBean populate() {
        return new ExternalUserFormBean();
    }

    @RequestMapping
    public String showExternalUserInvite(
            @ModelAttribute("externalUserFormBean") ExternalUserFormBean externalUserFormBean,
            Model model,
            RenderRequest req) {
        if (externalUserFormBean.getSponsorVgrId() == null) {
            //Meaning first request
            String userId = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
            if (userId == null) {
                throw new IllegalStateException("Du måste vara inloggad.");
            } else if (userId.startsWith("ex_")) {
                throw new IllegalStateException("Du måste vara anställd för att bjuda in andra.");
            }
            String sponsorName = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_NAME_GIVEN);
            String sponsorFamily = lookupP3PInfo(req, PortletRequest.P3PUserInfos.USER_NAME_FAMILY);
            externalUserFormBean.setSponsorVgrId(userId);
            externalUserFormBean.setSponsorFullName(String.format("%s %s", sponsorName, sponsorFamily));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            externalUserFormBean.setDateLimit(sdf.format(calendar.getTime()));
        }

        model.addAttribute("externalUserFormBean", externalUserFormBean);

        List<InvitePreferences> invitePreferenceses = invitePreferencesService.findAll();
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

    @RenderMapping(params = {"success"})
    public String success() {
        return "success";
    }

    @RenderMapping(params = {"unresponsive"})
    public String unresponsive(@RequestParam(value = "unresponsive") String failureCode, Model model) {
        model.addAttribute("message", failureCode);
        return "inviteTimeout";
    }

    @RenderMapping(params = {"error"})
    public String inviteError(@RequestParam(value = "error") String errorMessage, Model model) {
        model.addAttribute("message", errorMessage);
        return "error";
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
            BindingResult result, Model model, SessionStatus status,
            ActionRequest req, ActionResponse response) {
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
            if (statusCode == CreateUserStatusCodeType.NEW_USER || statusCode == CreateUserStatusCodeType.EXISTING_USER) {
                structureService.storeExternStructurePersonDn(externalUserFormBean.getExternStructurePersonDn());

                // ?: new user -> invite
                // ?: user already in PK -> no/has outstanding invite ->  invite
                InviteUserResponse inviteUserResponse = callInviteUser(externalUserFormBean, createUserResponse);

                InviteUserStatusCodeType statusCodeInvite = inviteUserResponse.getStatusCode();
                if (statusCodeInvite == InviteUserStatusCodeType.ERROR) {
                    // error -> cannot invite
                    response.setRenderParameter("error", inviteUserResponse.getMessage());
                } else {
                    status.setComplete();
                    response.setRenderParameter("success", "true");
                }
            } else if (statusCode == CreateUserStatusCodeType.ERROR) {
                // error -> cannot create
                status.setComplete();
                response.setRenderParameter("error", createUserResponse.getMessage());
            }

        } catch (MessageBusException e) {
            // ?: timeout try again later
            ControllerUtil.handleMessageBusException(e, response);
        }
    }

    private InviteUserResponse callInviteUser(ExternalUserFormBean externalUserFormBean, CreateUserResponse createUserResponse)
            throws MessageBusException {//invite
        InviteUser inviteUser = new InviteUser();
        inviteUser.setUserId(createUserResponse.getVgrId());
        inviteUser.setCustomURL(externalUserFormBean.getInvitePreferences().getCustomUrl());
        inviteUser.setCustomMessage(externalUserFormBean.getInvitePreferences().getCustomMessage());

        Message message = new Message();
        message.setPayload(inviteUserJaxbUtil.marshal(inviteUser));

        Object response = MessageBusUtil.sendSynchronousMessage
                ("vgr/account_invite", message, 7000);

        logger.info(response.toString());

        return ControllerUtil.extractResponse(response, inviteUserJaxbUtil);
    }

    private CreateUserResponse callCreateUser(ExternalUserFormBean externalUserFormBean) throws MessageBusException {
        CreateUser createUser = new CreateUser();
        createUser.setUserFirstName(externalUserFormBean.getName());
        createUser.setUserMiddleName(externalUserFormBean.getMiddleName());
        createUser.setUserSurName(externalUserFormBean.getSurname());
        createUser.setUserMail(externalUserFormBean.getEmail());
        createUser.setExternStructurepersonDN(externalUserFormBean.getExternStructurePersonDn());
        createUser.setDateLimit(externalUserFormBean.getDateLimit());
        createUser.setUserType(externalUserFormBean.getUserType());
        createUser.setSponsor(externalUserFormBean.getSponsorVgrId());
        createUser.setUserTelephoneNumber(externalUserFormBean.getPhone());
        createUser.setUserMobileTelephoneNumber(externalUserFormBean.getMobile());

        Message message = new Message();
        message.setPayload(createUserJaxbUtil.marshal(createUser));

        logger.info(createUserJaxbUtil.marshal(createUser));

        Object response = MessageBusUtil.sendSynchronousMessage("vgr/account_create", message, 7000);

        return ControllerUtil.extractResponse(response, createUserJaxbUtil);
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