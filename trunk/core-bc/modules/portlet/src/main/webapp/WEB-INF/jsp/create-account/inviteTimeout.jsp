<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<portlet:renderURL var="renderUrl"/>
<p>
    Det har tagit längre tid att skapa användaren (${externalUserFormBean.name}) än förväntat.<br/>
    Försök igen senare.
</p>
<p>
    <a href="${renderUrl}">Tillbaka</a>
</p>
