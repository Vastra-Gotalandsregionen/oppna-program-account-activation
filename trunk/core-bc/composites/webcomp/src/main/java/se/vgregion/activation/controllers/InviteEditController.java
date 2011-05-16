package se.vgregion.activation.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.account.services.repository.InvitePreferencesRepository;
import se.vgregion.account.services.repository.InvitePreferencesRepositoryImpl;
import se.vgregion.activation.formbeans.InvitePreferencesFormBean;
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

    @RenderMapping
    public String edit(ModelMap model, RenderRequest request) {

        if (!model.containsKey("invitePreferencesFormBean")) {
            InvitePreferencesFormBean preferencesFormBean = new InvitePreferencesFormBean();
            preferencesFormBean.setInvitePreferencesList((List<InvitePreferences>) invitePreferencesService.findAll());
            model.addAttribute("invitePreferencesFormBean", preferencesFormBean);
        }

        String message = request.getParameter("message");
        if (message != null) {
            model.addAttribute("message", message);
        }

        return "editForm";
    }

    @RenderMapping(params = {"action=edit"})
    public String editPreferences(ModelMap modelMap, RenderRequest request) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));
        InvitePreferences preferences = invitePreferencesService.find(id);
        modelMap.addAttribute("preferences", preferences);
        return "editPreferencesForm";
    }

    @ActionMapping(params = {"action=save"})
    public void savePreferences(ActionRequest request, ActionResponse response) {
        String preferencesId = request.getParameter("preferencesId");
        String title = request.getParameter("title");
        String customMessage = request.getParameter("customMessage");
        String customUrl = request.getParameter("customUrl");

        InvitePreferences preferences;
        if (StringUtils.isBlank(preferencesId)) {
            preferences = new InvitePreferences();
        } else {
            preferences = invitePreferencesService.find(Long.parseLong(preferencesId));
        }
        preferences.setTitle(title);
        preferences.setCustomMessage(customMessage);
        preferences.setCustomUrl(customUrl);

        invitePreferencesService.merge(preferences);

        response.setRenderParameter("message", "Sparat!");
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
