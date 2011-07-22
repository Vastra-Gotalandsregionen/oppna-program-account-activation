<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="${pageContext.request.contextPath}/css/style.css" type="text/css" rel="stylesheet"/>

<portlet:renderURL var="renderUrl"/>
<p>
    Inbjudan av användare <b>${externalUserFormBean.name} ${externalUserFormBean.surname}</b> misslyckades.<br/><br/>
    <spring:message code="${message}" text="${message}" arguments="${messageArguments}"/><br/><br/>
</p>
<p>
    <a class="buttonlink" href="${renderUrl}">Tillbaka</a>
</p>
