<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<link href="${pageContext.request.contextPath}/css/style.css" type="text/css" rel="stylesheet" />

<portlet:renderURL var="renderUrl"/>
<p>
    <spring:message code="${message}" text="${message}" arguments="${messageArguments}"/><br/><br/>
</p>
<p>
    <a class="buttonlink" href="${renderUrl}">Tillbaka</a>
</p>
