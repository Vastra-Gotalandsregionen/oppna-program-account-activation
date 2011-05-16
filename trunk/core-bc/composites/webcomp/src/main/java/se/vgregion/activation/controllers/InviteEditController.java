package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.persistence.PortletPreferencesFinderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.activation.formbeans.InvitePreferencesFormBean;
import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;

import javax.portlet.RenderRequest;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 14:44
 */

@Controller
@RequestMapping(value = "EDIT")
public class InviteEditController {

    @Autowired
    @Qualifier("invitePreferencesRepository")
    private DefaultJpaRepository<InvitePreferences, Long> invitePreferencesRepository;

    @RenderMapping
    public String edit(ModelMap model, RenderRequest request) {

        if (!model.containsKey("invitePreferencesFormBean")) {
            InvitePreferencesFormBean preferencesFormBean = new InvitePreferencesFormBean();
            preferencesFormBean.setInvitePreferencesList(invitePreferencesRepository.findAll());
            model.addAttribute("invitePreferencesFormBean", preferencesFormBean);
        }

        return "editForm";
    }

}
