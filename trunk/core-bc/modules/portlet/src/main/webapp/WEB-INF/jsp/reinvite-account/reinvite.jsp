<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<style type="text/css">
    .activationAccountsTable tr td {
        padding: 7px;
    }

    .activationAccountsTable tr th {
        padding: 7px;
        background-color: #BBB;
    }

    .activationAccountsTable {
        border: 1px;
        border-style: inset;
        border-color: #666;
        width: 400px;
    }

    .center {
        text-align: center;
    }

    .evenRow {
        background-color: #eee;
    }

    .oddRow {
        background-color: #ccc;
    }

</style>

<c:if test="${not empty message}">
    <span class="portlet-msg-success">
        <spring:message arguments="${messageArgs}" code="${message}" text="${message}"/>
    </span>
</c:if>

<c:if test="${empty accounts}">
    <p>Inga aktiva inbjudningar hittades.</p>
</c:if>
<c:if test="${not empty accounts}">
    <table class="activationAccountsTable">
        <tr>
            <th colspan="8">Pågående inbjudningar</th>
        </tr>
        <tr>
            <th>VgrId</th>
            <th>System</th>
            <th>Namn</th>
            <th>Epost</th>
            <th>Organisation</th>
            <th>Inbjuden av</th>
            <th>Bjud in</th>
            <th>Ta bort</th>
        </tr>
        <c:forEach items="${accounts}" var="account" varStatus="count">
            <portlet:actionURL var="reinviteUrl">
                <portlet:param name="activationCode" value="${account.activationCode.value}"/>
                <portlet:param name="action" value="reinvite"/>
            </portlet:actionURL>
            <portlet:actionURL var="inactivateUrl">
                <portlet:param name="activationCode" value="${account.activationCode.value}"/>
                <portlet:param name="action" value="inactivate"/>
            </portlet:actionURL>

            <tr class="${count.index%2 == 0 ? 'evenRow' : 'oddRow'}">
                <td>${account.vgrId}</td>
                <td>${account.system}</td>
                <td>${account.fullName}</td>
                <td>${account.email}</td>
                <td>${account.organization}</td>
                <td>${account.sponsor}</td>
                <td class="center">
                    <a href="${reinviteUrl}"><img src="/vgr-theme/images/mail/forward.png" title="Skicka inbjudan"/></a>
                </td>
                <td>
                    <a href="${inactivateUrl}"><img src="/vgr-theme/images/application/close.png" title="Ta bort"/></a>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<div style="max-height: 300px; overflow-y: auto; margin-top: 20px;">
    <c:if test="${empty expiredAccounts}">
        <p>Inga utgångna inbjudningar hittades.</p>
    </c:if>
    <c:if test="${not empty expiredAccounts}">
        <table class="activationAccountsTable">
            <tr>
                <th colspan="8">Utgångna inbjudningar</th>
            </tr>
            <tr>
                <th>VgrId</th>
                <th>System</th>
                <th>Namn</th>
                <th>Epost</th>
                <th>Organisation</th>
                <th>Inbjuden av</th>
                <th>Bjud in</th>
                <th>Ta bort</th>
            </tr>
            <c:forEach items="${expiredAccounts}" var="account" varStatus="count">
                <portlet:actionURL var="reinviteUrl">
                    <portlet:param name="activationCode" value="${account.activationCode.value}"/>
                    <portlet:param name="action" value="reinvite"/>
                </portlet:actionURL>
                <portlet:actionURL var="inactivateUrl">
                    <portlet:param name="activationCode" value="${account.activationCode.value}"/>
                    <portlet:param name="action" value="inactivate"/>
                </portlet:actionURL>
                <tr class="${count.index%2 == 0 ? 'evenRow' : 'oddRow'}">
                    <td>${account.vgrId}</td>
                    <td>${account.system}</td>
                    <td>${account.fullName}</td>
                    <td>${account.email}</td>
                    <td>${account.organization}</td>
                    <td>${account.sponsor}</td>
                    <td class="center">
                        <a href="${reinviteUrl}"><img src="/vgr-theme/images/mail/forward.png" title="Skicka inbjudan"/></a>
                    </td>
                    <td>
                        <a href="${inactivateUrl}"><img src="/vgr-theme/images/application/close.png" title="Ta bort"/></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
