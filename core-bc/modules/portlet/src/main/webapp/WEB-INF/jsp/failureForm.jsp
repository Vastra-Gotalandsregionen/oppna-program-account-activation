<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<p>
  <spring:message code="${message}" text="${message}" />
</p>
<p>
    <a href="<portlet:renderURL/>">Tillbaka</a>
</p>
