package se.vgregion.activation.controllers;

import com.liferay.portal.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import sun.rmi.server.Activation;

import java.util.Collection;


@Controller
@RequestMapping("VIEW")
public class ReinviteController {

    @Autowired
    private AccountService accountService;

    @RequestMapping
    public String view(Model model) {
        Collection<ActivationAccount> accounts = accountService.getAllValidAccounts();
        for (ActivationAccount aa: accounts) {
            System.out.println(aa.getVgrId());
        }

        model.addAttribute("accounts", accounts);

        return "reinvite";
    }

    @ActionMapping(params = {"action=reinvite"})
    public void reinvite(@RequestParam("activationCode") String code) {
        System.out.println("Reinvite ->" + code);

    }
}
