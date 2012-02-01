<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">
    #prefsTable tr td {
        padding-right: 20px;
    }

    a.buttonlink {
        text-decoration: none;
        color: #34404F;

    }
    .imglink {
        padding-top: 4px;
    }
</style>

<c:if test="${not empty message}">
    <div class="portlet-msg-success">${message}</div>
</c:if>

<portlet:renderURL var="renderUrl"/>

<table id="prefsTable">
    <c:forEach var="prefs" items="${invitePreferencesListFormBean.invitePreferencesList}">
        <tr>
            <td>
                <b>${prefs.title}</b>
            </td>
            <td>
                <a title="Ändra" href="<%= renderUrl %>&action=edit&preferencesId=${prefs.id}">
                    <img class="imglink" src="/vgr-theme/images/dockbar/settings.png" />
                </a>
            </td>
            <td>
                <a title="Ta bort" href="<%= renderUrl %>&action=remove&preferencesId=${prefs.id}">
                    <img class="imglink" src="/vgr-theme/images/common/close.png" />
                </a>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td></td>
        <td></td>
        <td>
            <a title="Lägg till" href="<%= renderUrl %>&action=add">
                <img class="imglink" src="/vgr-theme/images/dock/add_content.png" />
            </a>
        </td>

    </tr>
</table>

