<%@ page import="se.vgregion.activation.controllers.AccountActivationController" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<portlet:defineObjects/>


<table>

    <tr>
        <th>VgrId</th>
        <th></th>
    </tr>
    <c:forEach items="${accounts}" var="account">
        <portlet:actionURL var="reinviteUrl">
            <portlet:param name="activationCode" value="${account.activationCode.value}" />
            <portlet:param name="action" value="reinvite" />
        </portlet:actionURL>

        <tr>
            <td>${account.vgrId}</td>
            <td>
                <a href="${reinviteUrl}">Skicka inbjudan</a>
            </td>
        </tr>
    </c:forEach>
</table>


<p>
    <a href="${renderUrl}">Tillbaka</a>
</p>
