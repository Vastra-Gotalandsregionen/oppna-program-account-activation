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
 * Controller class for the "EDIT" view.
 * <p/>
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

    /**
     * Default method for the edit mode. Will show the edit form.
     *
     * @param model   model
     * @param request request
     * @return A view
     */
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

    /**
     * Handler method, when the "action" parameter is set to "edit", to show the edit preferences form.
     *
     * @param modelMap                  modelMap
     * @param request                   request
     * @param invitePreferencesFormBean invitePreferencesFormBean
     * @return A view
     */
    @RenderMapping(params = { "action=edit" })
    public String editPreferences(ModelMap modelMap, RenderRequest request,
                                  @ModelAttribute InvitePreferencesFormBean invitePreferencesFormBean) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));
        InvitePreferences preferences = invitePreferencesService.find(id);
        modelMap.addAttribute("invitePreferencesFormBean", preferences);
        return "editPreferencesForm";
    }

    /**
     * Saves an <code>InvitePreferences</code> object.
     *
     * @param request request
     * @param response response
     * @param invitePreferencesFormBean InvitePreferencesFormBean
     * @param errors errors
     * @param model model
     */
    @ActionMapping(params = { "action=save" })
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

    /**
     * Shows a confirmation form to assure an <code>InvitePreferences</code> removal.
     *
     * @param modelMap modelMap
     * @param request request
     * @return A view
     */
    @RenderMapping(params = { "action=remove" })
    public String removePreferences(ModelMap modelMap, RenderRequest request) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));
        InvitePreferences preferences = invitePreferencesService.find(id);
        modelMap.addAttribute("preferences", preferences);
        return "removePreferencesForm";
    }

    /**
     * Removes a given <code>InvitePreferences</code> instance.
     *
     * @param request request
     * @param response response
     */
    @ActionMapping(params = { "action=remove" })
    public void confirmRemovePreferences(ActionRequest request, ActionResponse response) {
        Long id = Long.parseLong(request.getParameter("preferencesId"));

        invitePreferencesService.remove(id);

        response.setRenderParameter("message", "Borttagen!");
    }

    /**
     * Creates a new <code>InvitePreferences</code> object which will be edited.
     *
     * @param modelMap modelMap
     * @param request request
     * @return A view
     */
    @RenderMapping(params = { "action=add" })
    public String addPreferences(ModelMap modelMap, RenderRequest request) {
        InvitePreferences preferences = new InvitePreferences();
        modelMap.addAttribute("preferences", preferences);
        return "editPreferencesForm";
    }
}
