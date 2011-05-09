<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="externalUserFormBean" type="se.vgregion.activation.formbeans.ExternalUserFormBean" scope="request"/>
<portlet:actionURL var="invite" name="invite" escapeXml="false"/>

<aui:form action="<%= invite %>" method="post">
    <aui:fieldset>
        <aui:input type="text" label="name" name="name" value="${externalUserFormBean.name}"/>
        <form:errors path="externalUserFormBean.name" cssClass="portlet-msg-error"/>
        <aui:input type="text" label="surname" name="surname" value="${externalUserFormBean.surname}"/>
        <form:errors path="externalUserFormBean.surname" cssClass="portlet-msg-error"/>
        <aui:input type="text" label="email" name="email" value="${externalUserFormBean.email}"/>
        <form:errors path="externalUserFormBean.email" cssClass="portlet-msg-error"/>

        <aui:input type="text" label="phone" name="phone" value="${externalUserFormBean.phone}"/>
        <div id="<portlet:namespace />externStructurePersonDnDiv">
            <aui:input id="externStructurePersonDn" type="text" label="externStructurePersonDn"
                       name="externStructurePersonDn"
                       value="${externalUserFormBean.externStructurePersonDn}"/>
        </div>
        <aui:input type="text" label="user-type" name="userType" value="${externalUserFormBean.userType}"/>

        <aui:input type="text" label="sponsor-full-name" name="sponsorFullName"
                   value="${externalUserFormBean.sponsorFullName}"/>
        <aui:input type="text" label="sponsor-vgr-id" name="sponsorVgrId" value="${externalUserFormBean.sponsorVgrId}"/>
        <form:errors path="externalUserFormBean.sponsorVgrId" cssClass="portlet-msg-error"/>
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit" value="Bjud in"/>
    </aui:button-row>
</aui:form>

<script type="text/javascript">
    AUI().ready('aui-autocomplete', function(A) {

        var instance = new A.AutoComplete({
            dataSource: [
                ['AL', 'Alabama', 'The Heart of Dixie'],
                ['AK', 'Alaska', 'The Land of the Midnight Sun'],
                ['AZ', 'Arizona', 'The Grand Canyon State']
            ],
            schema: {
                resultFields: ['key', 'name', 'description']
            },
            matchKey: 'name',
            //delimChar: '/',
            typeAhead: false,
            contentBox: '#<portlet:namespace />externStructurePersonDnDiv',
            input: '#<portlet:namespace />externStructurePersonDn'
        }).render();
    });


</script>
