<%@ page import="se.vgregion.activation.controllers.AccountActivationController" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<style type="text/css">
    #validActivationAccounts tr td, #validActivationAccounts tr th {
        padding: 10px;
    }

    .evenRow {
        background-color: #ccc;
    }
    .oddRow {
        background-color: #eee;
    }

    /*a.buttonlink {
        text-decoration: none;
        color: #34404F;

    }
    .imglink {
        padding-top: 4px;
    }*/
</style>


<table id="validActivationAccounts">

    <tr>
        <th>VgrId</th>
        <th></th>
    </tr>
    <c:forEach items="${accounts}" var="account" varStatus="count">
        <portlet:actionURL var="reinviteUrl">
            <portlet:param name="activationCode" value="${account.activationCode.value}" />
            <portlet:param name="action" value="reinvite" />
        </portlet:actionURL>

        <tr class="${count % 2 == 0 ? 'evenRow' : 'oddRow'}">
            <td>${account.vgrId}</td>
            <td>
                <a href="${reinviteUrl}"><img src="/vgr-theme/images/mail/forward.png" title="Skicka inbjudan"/></a>
            </td>
        </tr>
    </c:forEach>
</table>


<p>
    <a href="${renderUrl}">Tillbaka</a>
</p>
