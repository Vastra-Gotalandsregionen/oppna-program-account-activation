<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<p>
  Ditt konto är nu aktiverat. Din inloggning och vgr-id är <b>${passwordFormBean.vgrId}</b>. Återgå till
  <c:choose>
    <c:when test="${empty postbackUrl}"><a href="/">källsystemet</a>.</c:when>
    <c:otherwise><a href="${postbackUrl}">källsystemet</a>.</c:otherwise>
  </c:choose>
</p>
