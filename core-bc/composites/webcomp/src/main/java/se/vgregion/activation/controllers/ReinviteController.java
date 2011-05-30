package se.vgregion.activation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.ldapservice.LdapService;
import se.vgregion.ldapservice.LdapUser;

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

    @Autowired
    private AccountService accountService;

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Autowired
    private LdapService ldapService;

    @RequestMapping
    public String view(Model model) {
        Collection<ActivationAccount> accounts = accountService.getAllValidAccounts();

        List<ReinviteFormBean> reinvites = new ArrayList<ReinviteFormBean>();
        for (ActivationAccount account : accounts) {
            ReinviteFormBean bean = mapToReinvite(account);

            reinvites.add(bean);
        }

        model.addAttribute("accounts", reinvites);

        return "reinvite";
    }

    @RenderMapping(params = {"success=true"})
    public String success() {
        return "success";
    }

    @RenderMapping(params = {"error=true"})
    public String error() {
        return "error";
    }

    @ActionMapping(params = {"action=reinvite"})
    public void reinvite(@RequestParam("activationCode") ActivationCode code, ActionRequest request,
                         ActionResponse response, Model model) {
        ReinviteFormBean bean = mapToReinvite(accountService.getAccount(code));

        String userId = lookupP3PInfo(request, PortletRequest.P3PUserInfos.USER_LOGIN_ID);
        if (userId == null) {
            throw new IllegalStateException("Du måste vara inloggad.");
        } else if (userId.startsWith("ex_")) {
            throw new IllegalStateException("Du måste vara anställd för att bjuda in andra.");
        }
        bean.setSponsor(userId);

        model.addAttribute("reinvite", bean);

        if (bean.getActivationCode().getValue().equals("apa")) {
            response.setRenderParameter("error", "true");
        } else {
            response.setRenderParameter("success", "true");
        }

    }

    private ReinviteFormBean mapToReinvite(ActivationAccount account) {
        ReinviteFormBean bean = new ReinviteFormBean();
        bean.setActivationCode(account.getActivationCode());
        bean.setVgrId(account.getVgrId());
        InvitePreferences service = invitePreferencesService.findByCustomUrl(account.getCustomUrl());
        if (service != null) {
            bean.setSystem(service.getTitle());
        } else {
            bean.setSystem(account.getCustomUrl());
        }


        LdapUser ldapUser = ldapService.getLdapUserByUid(account.getVgrId());
        // TODO
        //fullName
        //email
        //organization
        //sponsor
        if (ldapUser != null) {
            bean.setFullName(ldapUser.getAttributeValue("cn"));
            bean.setEmail(ldapUser.getAttributeValue("mail"));
            bean.setOrganization(ldapUser.getAttributeValue("externStructureDN"));
        } else {
            //throw some exception
        }
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
