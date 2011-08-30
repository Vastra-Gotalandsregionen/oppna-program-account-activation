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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceResponse;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteController.class);

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

    /**
     * Method used by Spring to convert between <code>Object</code> and <code>String</code>.
     *
     * @param binder binder
     */
    @InitBinder("externalUserFormBean")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(InvitePreferences.class, invitePreferencesPropertyEditor);
    }

    /**
     * Called by Spring since "externalUserFormBean" is session-scoped.
     * @return A new {@link ExternalUserFormBean}
     */
    @ModelAttribute("externalUserFormBean")
    public ExternalUserFormBean populate() {
        return new ExternalUserFormBean();
    }

    /**
     * Default handler method when no parameters are passed before showing the invite form.
     *
     * @param externalUserFormBean externalUserFormBean
     * @param model                model
     * @param req                  req
     * @return A view
     */
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

    /**
     * Called with AJAX request. Writes JSON to response.
     *
     * @param query query
     * @param res   res
     */
    @ResourceMapping
    public void searchStructure(@RequestParam("query") String query, ResourceResponse res) {
        Collection<String> structures = structureService.search(query);
        try {
            OutputStream outputStream = res.getPortletOutputStream();
            res.setContentType("application/json");
            new ObjectMapper().writeValue(outputStream, structures);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler method that returns a view specified by redirectPage.
     *
     * @param redirectPage redirectPage
     * @return A view
     */
    @RenderMapping(params = { "success" })
    public String success(@RequestParam(value = "success") String redirectPage) {
        return redirectPage;
    }

    /**
     * Handler method for when the "unresponsive" parameter is set.
     *
     * @param externalUserFormBean externalUserFormBean
     * @param failureCode          failureCode
     * @param failureArguments     failureArguments
     * @param model                model
     * @return A view
     */
    @RenderMapping(params = { "unresponsive" })
    public String unresponsive(@ModelAttribute ExternalUserFormBean externalUserFormBean,
                               @RequestParam(value = "unresponsive") String failureCode,
                               @RequestParam(value = "unresponsiveArguments", required = false) String failureArguments,
                               Model model) {
        model.addAttribute("message", failureCode);
        model.addAttribute("messageArguments", failureArguments);
        return "inviteTimeout";
    }

    /**
     * Handler method for when the "error" parameter is set.
     *
     * @param errorMessage   errorMessage
     * @param errorArguments errorArguments
     * @param model          model
     * @return A view
     */
    @RenderMapping(params = { "error" })
    public String inviteError(@RequestParam(value = "error") String errorMessage,
                              @RequestParam(value = "errorArguments", required = false) String errorArguments,
                              Model model) {
        model.addAttribute("message", errorMessage);
        model.addAttribute("messageArguments", errorArguments);
        return "error";
    }

    /**
     * Handler method for when an <code>Exception</code> is thrown.
     *
     * @param ex ex
     * @return A <code>ModelAndView</code>
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ex.printStackTrace();
        ModelAndView modelAndView = new ModelAndView("externalUserError");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    /**
     * This action method makes the call to create the user and, if success, also makes the call to invite the user.
     *
     * @param externalUserFormBean externalUserFormBean
     * @param result               result
     * @param model                model
     * @param status               status
     * @param req                  req
     * @param response             response
     */
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

            //need the vgr id for the confirmation or error page
            externalUserFormBean.setVgrId(createUserResponse.getVgrId());

            CreateUserStatusCodeType statusCode = createUserResponse.getStatusCode();
            if (statusCode == CreateUserStatusCodeType.NEW_USER
                    || statusCode == CreateUserStatusCodeType.EXISTING_EXTERNAL_USER) {
                structureService.storeExternStructurePersonDn(externalUserFormBean.getExternStructurePersonDn());

                // ?: new user -> invite
                // ?: user already in PK -> no/has outstanding invite ->  invite
                InviteUserResponse inviteUserResponse = callInviteUser(externalUserFormBean, createUserResponse);

                InviteUserStatusCodeType statusCodeInvite = inviteUserResponse.getStatusCode();
                if (statusCodeInvite == InviteUserStatusCodeType.ERROR) {
                    // error -> cannot invite
                    response.setRenderParameter("error", "unknown.system.error");
                    response.setRenderParameter("errorArguments", inviteUserResponse.getMessage());
                } else {
                    status.setComplete();
                    if (statusCode == CreateUserStatusCodeType.NEW_USER) {
                        response.setRenderParameter("success", "success");
                    } else if (statusCode == CreateUserStatusCodeType.EXISTING_EXTERNAL_USER) {
                        response.setRenderParameter("success", "reinviteSuccess");
                    }
                }
            } else if (statusCode == CreateUserStatusCodeType.SPONSOR_NOT_AUTHORIZED) {
                response.setRenderParameter("error", "sponsor.not.authorized");
            } else if (statusCode == CreateUserStatusCodeType.EXISTING_INTERNAL_USER) {
                response.setRenderParameter("error", "existing.internal.user");
                response.setRenderParameter("errorArguments", new String[]{externalUserFormBean.getEmail()});
            } else {
                // error -> cannot create
                response.setRenderParameter("error", "unknown.system.error");
                response.setRenderParameter("errorArguments", createUserResponse.getMessage());
            }

        } catch (MessageBusException e) {
            // ?: timeout try again later
            ControllerUtil.handleMessageBusException(e, response);
        }
    }

    private InviteUserResponse callInviteUser(ExternalUserFormBean externalUserFormBean,
                                              CreateUserResponse createUserResponse)
            throws MessageBusException {

        InviteUser inviteUser = new InviteUser();
        inviteUser.setUserId(createUserResponse.getVgrId());
        inviteUser.setCustomURL(externalUserFormBean.getInvitePreferences().getCustomUrl());
        inviteUser.setCustomMessage(externalUserFormBean.getInvitePreferences().getCustomMessage());
        inviteUser.setSystem(externalUserFormBean.getInvitePreferences().getTitle());

        Message message = new Message();
        String payload = inviteUserJaxbUtil.marshal(inviteUser);

        LOGGER.info("Invite user payload: " + payload);

        message.setPayload(payload);

        final int timeout = 7000;
        Object response = MessageBusUtil.sendSynchronousMessage("vgr/account_invite", message, timeout);

        LOGGER.info(response.toString());

        return inviteUserJaxbUtil.extractResponse(response);
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

        LOGGER.info(createUserJaxbUtil.marshal(createUser));

        final int timeout = 7000;
        Object response = MessageBusUtil.sendSynchronousMessage("vgr/account_create", message, timeout);

        return createUserJaxbUtil.extractResponse(response);
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