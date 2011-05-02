<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<jsp:useBean id="externalUserFormBean" type="se.vgregion.activation.formbeans.ExternalUserFormBean" scope="request"/>
<portlet:actionURL var="invite" name="invite" escapeXml="false"/>

<aui:form action="<%= invite %>" method="post">
    <aui:fieldset>
        <aui:input type="text" label="name" name="name" value="${externalUserFormBean.name}"/>
        <aui:input type="text" label="email" name="email" value="${externalUserFormBean.email}"/>
        <aui:input type="text" label="telephone" name="telephone" value="${externalUserFormBean.telephone}"/>
        <aui:input type="text" label="organization" name="structure" value="${externalUserFormBean.structure}"/>
        <aui:input type="text" label="user-type" name="userType" value="${externalUserFormBean.userType}"/>

        <aui:input type="text" label="sponsor" name="sponsor" value="${externalUserFormBean.sponsor}"/>
        <form:errors path="externalUserFormBean.sponsor" cssClass="portlet-msg-error"/>
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit" value="Bjud in"/>
    </aui:button-row>
</aui:form>
