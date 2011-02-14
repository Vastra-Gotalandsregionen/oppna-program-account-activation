<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<portlet:actionURL var="send" escapeXml="false"/>
<form:form method="POST" commandName="passwordFormBean" action="${send}">

    <form:hidden path="vgrId" id="vgrId" />

    Nytt lösenord: <form:password path="password" id="password" cssClass="text" cssErrorClass="text validation-error" /><br/>

    Upprepa lösenordet: <form:password path="passwordCheck" id="passwordCheck"  cssClass="text" cssErrorClass="text validation-error" /><br/>

</form:form>