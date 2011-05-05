<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<portlet:renderURL var="validation" escapeXml="false"/>

<aui:form action="<%= validation %>" method="post">
  <aui:fieldset>
    <aui:input type="hidden" name="loginType" value="domino" />
    <aui:input label="screen-name" type="text" name="vgrId" helpMessage="Skriv in ditt vgrId"/>
    <form:errors path="passwordFormBean.vgrId" cssClass="portlet-msg-error"/>
    <aui:input label="password" autocomplete="off" type="password" name="dominoPassword" helpMessage="Skriv in ditt lösenord"/>
    <form:errors path="passwordFormBean.dominoPassword"  cssClass="portlet-msg-error"/>
  </aui:fieldset>
  <aui:button-row>
    <aui:button type="submit" value="sign-in"/>
  </aui:button-row>
</aui:form>