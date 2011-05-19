<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">
    /* A fix to make the autoComplete combobox aligned with the select button */
    .aui-combobox-content .aui-field .aui-field-content {
        margin: 0;
    }
</style>

<jsp:useBean id="externalUserFormBean" type="se.vgregion.activation.formbeans.ExternalUserFormBean" scope="request"/>
<portlet:actionURL var="invite" name="invite" escapeXml="false"/>

<aui:form action="<%= invite %>" method="post">
    <aui:fieldset>
        <aui:select label="inviteTo" name="invitePreferences" onChange="javascript:toggleAttributesDiv(this)">
            <aui:option label=""/>
            <c:forEach items="${invitePreferences}" var="prefs">
                <aui:option label="${prefs.title}" selected="${externalUserFormBean.invitePreferences.id eq prefs.id}"
                            value="${prefs.id}"/>
            </c:forEach>
        </aui:select>
        <form:errors path="externalUserFormBean.invitePreferences" cssClass="portlet-msg-error"/>

        <div id="<portlet:namespace/>attributes">
            <aui:input type="text" label="name" name="name" value="${externalUserFormBean.name}"/>
            <form:errors path="externalUserFormBean.name" cssClass="portlet-msg-error"/>
            <aui:input type="text" label="middleName" name="middleName" value="${externalUserFormBean.middleName}"/>
            <aui:input type="text" label="surname" name="surname" value="${externalUserFormBean.surname}"/>
            <form:errors path="externalUserFormBean.surname" cssClass="portlet-msg-error"/>
            <aui:input type="text" label="email" name="email" value="${externalUserFormBean.email}"/>
            <form:errors path="externalUserFormBean.email" cssClass="portlet-msg-error"/>

            <aui:input type="text" label="phone" name="phone" value="${externalUserFormBean.phone}"/>
            <aui:input type="text" label="mobile" name="mobile" value="${externalUserFormBean.mobile}"/>

            <div id="<portlet:namespace />externStructurePersonDnDiv">
                <aui:input id="externStructurePersonDn" type="text" label="externStructurePersonDn"
                           name="externStructurePersonDn"
                           value="${externalUserFormBean.externStructurePersonDn}"/>
            </div>
            <aui:input type="text" label="user-type" name="userType" value="${externalUserFormBean.userType}"/>

            <div class="aui-field-label"><spring:message code="sponsor-full-name"/></div>
            <div>${externalUserFormBean.sponsorFullName}</div>

            <aui:input type="hidden" label="sponsor-full-name" name="sponsorFullName"
                       value="${externalUserFormBean.sponsorFullName}"/>
            <aui:input type="hidden" label="sponsor-vgr-id" name="sponsorVgrId"
                       value="${externalUserFormBean.sponsorVgrId}"/>
            <form:errors path="externalUserFormBean.sponsorVgrId" cssClass="portlet-msg-error"/>
            <aui:button-row>
                <aui:button type="submit" value="Bjud in"/>
            </aui:button-row>
        </div>
    </aui:fieldset>
</aui:form>

<portlet:resourceURL var="resourceUrl" escapeXml="false"/>

<script type="text/javascript">
    AUI().ready('aui-autocomplete', function(A) {

        var instance = new A.AutoComplete({
            dataSource: function(request) {
                var items = null;
                A.io.request('<%= resourceUrl %>&query=' + A.one('#<portlet:namespace />externStructurePersonDn').get('value'), {
                    cache: false,
                    sync: true,
                    timeout: 1000,
                    dataType: 'json',
                    method: 'get',
                    on: {
                        success: function() {
                            items = this.get('responseData');
                        },
                        failure: function() {
                        }
                    }
                });

                return items;
            },
            dataSourceType: 'Function',
            schemaType: 'json',
            typeAhead: false,
            contentBox: '#<portlet:namespace />externStructurePersonDnDiv',
            input: '#<portlet:namespace />externStructurePersonDn'
        }).render();
    });
</script>