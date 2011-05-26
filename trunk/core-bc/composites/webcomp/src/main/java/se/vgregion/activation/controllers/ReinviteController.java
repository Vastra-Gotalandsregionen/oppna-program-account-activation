package se.vgregion.activation.controllers;

import com.liferay.portal.model.Account;
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
import se.vgregion.create.domain.InvitePreferences;
import sun.rmi.server.Activation;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping("VIEW")
public class ReinviteController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @RequestMapping
    public String view(Model model) {
        Collection<ActivationAccount> accounts = accountService.getAllValidAccounts();

        List<ReinviteFormBean> reinvites = new ArrayList<ReinviteFormBean>();
        for (ActivationAccount account: accounts) {
            ReinviteFormBean bean = new ReinviteFormBean();
            bean.setActivationCode(account.getActivationCode());
            bean.setVgrId(account.getVgrId());
            InvitePreferences service = invitePreferencesService.findByCustomUrl(account.getCustomUrl());
            if (service != null) {
                bean.setService(service.getTitle());
            } else {
                bean.setService(account.getCustomUrl());
            }
            // TODO
            //fullName
            //email
            //organization
            //sponsor

            reinvites.add(bean);
        }

        model.addAttribute("accounts", reinvites);

        return "reinvite";
    }

    @ActionMapping(params = {"action=reinvite"})
    public void reinvite(@RequestParam("activationCode") String code, ActionRequest request,
                         ActionResponse response, Model model) {
        System.out.println("Reinvite ->" + code);

        model.addAttribute("activationCode", code);
        request.setAttribute("activationCode", code);

        response.setRenderParameter("success", "true");
    }

    @RenderMapping(params = {"success=true"})
    public String success(Model model, PortletRequest request) {
        return "success";
    }
}
