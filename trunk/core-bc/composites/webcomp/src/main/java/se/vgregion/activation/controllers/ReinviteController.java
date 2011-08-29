package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.AccountService;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.ReinviteFormBean;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.ldapservice.LdapService;
import se.vgregion.ldapservice.LdapUser;
import se.vgregion.portal.inviteuser.InviteUser;
import se.vgregion.portal.inviteuser.InviteUserResponse;
import se.vgregion.portal.inviteuser.InviteUserStatusCodeType;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("VIEW")
public class ReinviteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReinviteController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Autowired
    private LdapService ldapService;

    private JaxbUtil inviteUserJaxbUtil = new JaxbUtil("se.vgregion.portal.inviteuser");

    /**
     * Default handler method when no parameters are passed. Loads current and expired invites.
     * @param model model
     * @param request request
     * @return A view
     */
    @RequestMapping
    public String view(Model model, PortletRequest request) {
        Collection<ActivationAccount> accounts = accountService.getAllValidAccounts();
        List<ReinviteFormBean> reinvites = accountsToReinvites(request, accounts);
        model.addAttribute("accounts", reinvites);

        final int maxDaysOld = 90;
        Collection<ActivationAccount> expiredAccounts = accountService.getExpiredUnusedAccounts(1, maxDaysOld);
        List<ReinviteFormBean> expiredReinvites = accountsToReinvites(request, expiredAccounts);
        model.addAttribute("expiredAccounts", expiredReinvites);

        return "reinvite";
    }

    private List<ReinviteFormBean> accountsToReinvites(PortletRequest request, Collection<ActivationAccount> accounts) {
        List<ReinviteFormBean> reinvites = new ArrayList<ReinviteFormBean>();
        for (ActivationAccount account : accounts) {
            try {
                ReinviteFormBean bean = mapToReinvite(account, request);
                reinvites.add(bean);
            } catch (IllegalArgumentException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return reinvites;
    }

    /**
     * Handler method for when the "success" parameter is set to <code>true</code>.
     * @return A view
     */
    @RenderMapping(params = { "success=true" })
    public String success() {
        return "success";
    }

    /**
     * Handler method for when the "unresponsive" parameter is set.
     * @param failureCode failureCode
     * @param model model
     * @return A view
     */
    @RenderMapping(params = { "unresponsive" })
    public String unresponsive(@RequestParam(value = "unresponsive") String failureCode, Model model) {
        model.addAttribute("message", failureCode);
        return "inviteTimeout";
    }

    /**
     * Handler method for when the "error" parameter is set.
     * @param errorMessage errorMessage
     * @param model model
     * @return A view
     */
    @RenderMapping(params = { "error" })
    public String error(@RequestParam (value = "error") String errorMessage, Model model) {
        model.addAttribute("message", errorMessage);
        return "error";
    }

    /**
     * Makes the reinvite call.
     * @param code code
     * @param request request
     * @param response response
     * @param model model
     */
    @ActionMapping(params = { "action=reinvite" })
    public void reinvite(@RequestParam("activationCode") ActivationCode code, ActionRequest request,
            ActionResponse response, Model model) {
        try {
            ReinviteFormBean bean = mapToReinvite(accountService.getAccount(code), request);

            model.addAttribute("reinvite", bean);

            InviteUser inviteUser = new InviteUser();
            inviteUser.setUserId(bean.getVgrId());
            inviteUser.setCustomURL(bean.getCustomUrl());
            inviteUser.setCustomMessage(bean.getCustomMessage());
            inviteUser.setSystem(bean.getSystem());

            Message message = new Message();
            message.setPayload(inviteUserJaxbUtil.marshal(inviteUser));

            final int timeout = 7000;
            Object inviteResponse = MessageBusUtil.sendSynchronousMessage("vgr/account_invite", message, timeout);

            LOGGER.info(inviteResponse.toString());

            InviteUserResponse inviteUserResponse = inviteUserJaxbUtil.extractResponse(inviteResponse);

            InviteUserStatusCodeType statusCodeInvite = inviteUserResponse.getStatusCode();
            if (statusCodeInvite == InviteUserStatusCodeType.ERROR) {
                // error -> cannot invite
                response.setRenderParameter("error", inviteUserResponse.getMessage());
            } else {
                response.setRenderParameter("success", "true");
            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            response.setRenderParameter("error", ex.getMessage());
        } catch (MessageBusException e) {
            e.printStackTrace();
            ControllerUtil.handleMessageBusException(e, response);
        }

    }

    /**
     * Inactivates a current invite.
     * @param code code
     * @param request request
     * @param response response
     * @param model model
     */
    @ActionMapping(params = { "action=inactivate" })
    public void inactivate(@RequestParam("activationCode") ActivationCode code, ActionRequest request,
            ActionResponse response, Model model) {
        ActivationAccount account = accountService.getAccount(code);
        accountService.inactivate(account);

        model.addAttribute("message", "inactivate.user");
        model.addAttribute("messageArgs", new String[]{account.getVgrId()});
    }

    private ReinviteFormBean mapToReinvite(ActivationAccount account, PortletRequest request) {
        ReinviteFormBean bean = new ReinviteFormBean();
        bean.setActivationCode(account.getActivationCode());
        bean.setVgrId(account.getVgrId());
        InvitePreferences service = invitePreferencesService.findByCustomUrl(account.getCustomUrl());
        if (service != null) {
            bean.setSystem(service.getTitle());
        } else {
            bean.setSystem(account.getCustomUrl());
        }

        bean.setCustomUrl(account.getCustomUrl());
        bean.setCustomMessage(account.getCustomMessage());

        try {
            LdapUser ldapUser = ldapService.getLdapUserByUid(account.getVgrId());
            if (ldapUser != null) {
                bean.setFullName(ldapUser.getAttributeValue("cn"));
                bean.setEmail(ldapUser.getAttributeValue("mail"));

                String[] organisationsArray = ldapUser.getAttributeValues("externalStructurepersonDN");
                String organisations = StringUtils.join(organisationsArray, ", ");
                bean.setOrganization(organisations);

            } else {
                throw new IllegalArgumentException("User with vgrId=" + account.getVgrId() + " does not exist.");
            }
        } catch (Exception e) {
            String warn = "Kan inte verifieras";
            bean.setFullName(warn);
            bean.setEmail(warn);
            bean.setOrganization(warn);
        }

        //set user id
        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
        if (userId == null) {
            throw new IllegalStateException("Du måste vara inloggad.");
        } else if (userId.startsWith("ex_")) {
            throw new IllegalStateException("Du måste vara anställd för att bjuda in andra.");
        }
        bean.setSponsor(userId);

        return bean;
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
