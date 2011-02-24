<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<jsp:useBean id="passwordFormBean" type="se.vgregion.activation.domain.form.PasswordFormBean" scope="request" />
<portlet:actionURL var="activate" name="activate" escapeXml="false" />
<spring:hasBindErrors name="passwordFormBean">
  <c:forEach var="err" items="${errors.globalErrors}">
      <font color="red"><c:out value="${err}" /></br></font>
  </c:forEach>
</spring:hasBindErrors>
<aui:form action="<%= activate %>" method="post">
  <aui:fieldset>
    <aui:input type="hidden" name="vgrId" value="${passwordFormBean.vgrId}" />
    <aui:input autocomplete="off" label="new-password" type="password" name="password"
      value="${passwordFormBean.password}" helpMessage="Skriv in ett nytt lösenord"
      suffix="${passwordFormBean.vgrId}" />
    <form:errors path="password" />
    <aui:input autocomplete="off" label="enter-again" type="password" name="passwordCheck"
      value="${passwordFormBean.passwordCheck}" helpMessage="Repetera ditt lösenord" />
  </aui:fieldset>
  <aui:button-row>
    <aui:button type="submit" value="save" />
  </aui:button-row>
</aui:form>
