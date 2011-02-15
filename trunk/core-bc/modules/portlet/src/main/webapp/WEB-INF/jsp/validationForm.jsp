<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<portlet:actionURL var="onetimeToken" escapeXml="false"/>
<form:form method="POST" commandName="validationFormBean" action="${send}">

    Engångs lösenord: <form:password path="onetimeToken" id="onetimeToken" cssClass="text" cssErrorClass="text validation-error" /><br/>

    <input type="submit" value="Fortsätt"/>

</form:form>