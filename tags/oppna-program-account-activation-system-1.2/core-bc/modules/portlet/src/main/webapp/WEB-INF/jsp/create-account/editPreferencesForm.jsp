<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<link href="${pageContext.request.contextPath}/css/style.css" type="text/css" rel="stylesheet" />

<portlet:actionURL var="actionUrl"/>
<portlet:renderURL var="renderUrl"/>

<aui:form action="<%= actionUrl %>" method="post">
    <aui:input name="id" value="${invitePreferencesFormBean.id}" type="hidden"/>
    <aui:input name="action" value="save" type="hidden"/>
    <aui:fieldset>
        <aui:input type="text" size="30" label="title" name="title" value="${invitePreferencesFormBean.title}"/>
        <form:errors path="invitePreferencesFormBean.title" cssClass="portlet-msg-error"/>
        <aui:input type="textarea" columns="30" label="customMessage" name="customMessage"
                   value="${invitePreferencesFormBean
        .customMessage}"/>
        <aui:input type="text" label="customUrl" name="customUrl" value="${invitePreferencesFormBean.customUrl}"/>
        <form:errors path="invitePreferencesFormBean.customUrl" cssClass="portlet-msg-error"/>
    </aui:fieldset>
    <aui:button-row>
        <aui:button name="submitButton" type="submit" value="Spara"/>
        <a class="buttonlink" href="<%= renderUrl %>"><span>Avbryt</span></a>
    </aui:button-row>
</aui:form>
