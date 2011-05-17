<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<portlet:actionURL var="actionUrl" />
<portlet:renderURL var="renderUrl" />

<aui:form action="<%= actionUrl %>" method="post">
    <aui:input name="preferencesId" value="${invitePreferencesFormBean.id}" type="hidden" />
    <aui:input name="action" value="save" type="hidden" />
    <aui:fieldset>
        <aui:input type="text" label="title" name="title" value="${invitePreferencesFormBean.title}"/>
        <form:errors path="invitePreferencesFormBean.title" cssClass="portlet-msg-error" />
        <aui:input type="text" label="customMessage" name="customMessage" value="${invitePreferencesFormBean.customMessage}"/>
        <aui:input type="text" label="customerUrl" name="customUrl" value="${invitePreferencesFormBean.customUrl}"/>
        <form:errors path="invitePreferencesFormBean.customUrl" cssClass="portlet-msg-error" />
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit" value="Spara"/>
        <a href="<%= renderUrl %>">Avbryt</a>
    </aui:button-row>
</aui:form>
