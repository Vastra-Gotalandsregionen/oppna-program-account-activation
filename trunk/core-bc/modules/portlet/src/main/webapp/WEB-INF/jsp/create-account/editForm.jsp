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
</style>

<c:if test="${not empty message}">
    <div class="portlet-msg-success">${message}</div>
</c:if>

<portlet:renderURL var="renderUrl"/>

<table id="prefsTable">
    <c:forEach var="prefs" items="${invitePreferencesFormBean.invitePreferencesList}">
        <tr>
            <td>${prefs.title}</td>
            <td><a href="<%= renderUrl %>&action=edit&preferencesId=${prefs.id}">Ändra</a></td>
            <td><a href="<%= renderUrl %>&action=remove&preferencesId=${prefs.id}">Ta bort</a></td>
        </tr>
    </c:forEach>
    <tr>
        <td></td>
        <td></td>
        <td><a href="<%= renderUrl %>&action=add">Lägg till</a></td>
    </tr>
</table>

