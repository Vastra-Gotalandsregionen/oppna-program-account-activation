<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<portlet:renderURL var="validation" escapeXml="false"/>

<aui:form action="<%= validation %>" method="post">
    <aui:fieldset>
        <aui:input autocomplete="off" label="current-password" type="password" name="oneTimePassword" helpMessage="Skriv in ditt engångslösenord"/>
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit" value="Fortsätt"/>
    </aui:button-row>
</aui:form>