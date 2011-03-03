<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<jsp:useBean id="passwordFormBean" type="se.vgregion.activation.portlet.controllers.formbeans.PasswordFormBean" scope="request"/>
<portlet:actionURL var="activate" name="activate" escapeXml="false"/>

<aui:form action="<%= activate %>" method="post">
    <aui:fieldset>
        <aui:input type="hidden" name="vgrId" value="${passwordFormBean.vgrId}"/>
        <aui:input type="hidden" name="oneTimePassword" value="${passwordFormBean.oneTimePassword}"/>
        <aui:input type="hidden" name="dominoPassword" value="${passwordFormBean.dominoPassword}"/>
        <aui:input autocomplete="off" label="new-password" type="password" name="password"
                   value="${passwordFormBean.password}" helpMessage="Skriv in ett nytt lösenord"/>
        <form:errors path="passwordFormBean.password" />
        <aui:input autocomplete="off" label="enter-again" type="password" name="passwordCheck"
                   value="${passwordFormBean.passwordCheck}" helpMessage="Repetera ditt lösenord"/>
        <form:errors path="passwordFormBean.passwordCheck" />
        
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit" value="save"/>
    </aui:button-row>
</aui:form>
