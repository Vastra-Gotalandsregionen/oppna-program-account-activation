package se.vgregion.activation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.activation.formbeans.InvitePreferencesFormBean;
import se.vgregion.activation.formbeans.InvitePreferencesListFormBean;
import se.vgregion.activation.validators.InvitePreferencesValidator;
import se.vgregion.create.domain.InvitePreferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import java.util.List;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 14:44
 */

@Controller
@RequestMapping(value = "EDIT")
public class InviteEditController {

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Autowired
    private InvitePreferencesValidator validator;

    @RenderMapping
    public String edit(ModelMap model, RenderRequest request) {

        if (!model.containsKey("invitePreferencesListFormBean")) {
            InvitePreferencesListFormBean preferencesListFormBean = new InvitePreferencesListFormBean();
            List<InvitePreferences> all = invitePreferencesService.findAll();
            preferencesListFormBean.setInvitePreferencesList(all);
            model.addAttribute("invitePreferencesListFormBean", preferencesListFormBean);
        }

        String message = request.getParameter("message");
        if (message != null) {
            model.addAttribute("message", message);
        }

        if (model.get("errors") != null) {
            model.addAttribute("org.springframework.validation.BindingResult.passwordFormBean",
                    model.get("errors"));
            return "editPreferencesForm";
        }

        return "editForm";
    }

    @RenderMapping(params = {"action=edit"})
    public String editPreferences(ModelMap modelMap, RenderRequest request,
                                  @ModelAttribute InvitePreferencesFormBean invitePreferencesFormBean) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));
        InvitePreferences preferences = invitePreferencesService.find(id);
        modelMap.addAttribute("invitePreferencesFormBean", preferences);
        return "editPreferencesForm";
    }

    @ActionMapping(params = {"action=save"})
    public void savePreferences(ActionRequest request, ActionResponse response,
                                @ModelAttribute InvitePreferencesFormBean invitePreferencesFormBean,
                                BindingResult errors, Model model) {

        InvitePreferences preferences;
        if (invitePreferencesFormBean.getId() == null) {
            preferences = new InvitePreferences();
        } else {
            preferences = invitePreferencesService.find(invitePreferencesFormBean.getId());
        }
        preferences.setTitle(invitePreferencesFormBean.getTitle());
        preferences.setCustomMessage(invitePreferencesFormBean.getCustomMessage());
        preferences.setCustomUrl(invitePreferencesFormBean.getCustomUrl());

        validator.validate(preferences, errors);

        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
        } else {
            invitePreferencesService.merge(preferences);
            response.setRenderParameter("message", "Sparat!");
        }
    }

    @RenderMapping(params = {"action=remove"})
    public String removePreferences(ModelMap modelMap, RenderRequest request) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));
        InvitePreferences preferences = invitePreferencesService.find(id);
        modelMap.addAttribute("preferences", preferences);
        return "removePreferencesForm";
    }

    @ActionMapping(params = {"action=remove"})
    public void confirmRemovePreferences(ActionRequest request, ActionResponse response) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));

        invitePreferencesService.remove(id);

        response.setRenderParameter("message", "Borttagen!");
    }

    @RenderMapping(params = {"action=add"})
    public String addPreferences(ModelMap modelMap, RenderRequest request) {
        InvitePreferences preferences = new InvitePreferences();
        modelMap.addAttribute("preferences", preferences);
        return "editPreferencesForm";
    }
}
