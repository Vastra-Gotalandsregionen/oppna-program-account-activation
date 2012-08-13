<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="${pageContext.request.contextPath}/css/style.css" type="text/css" rel="stylesheet" />

<jsp:useBean id="externalUserFormBean" type="se.vgregion.activation.formbeans.ExternalUserFormBean" scope="request"/>
<portlet:actionURL var="invite" name="invite" escapeXml="false"/>
<div class="wrapper-div">
    <aui:form action="<%= invite %>" name="create-external-user-form" method="post">
        <aui:fieldset>
            <aui:layout>
                <aui:column columnWidth="50">
                    <aui:select label="inviteTo" name="invitePreferences" cssClass="mandatory-label"
                                onChange="javascript:toggleAttributesDiv(this)"
                            >
                        <aui:option label=""/>
                        <c:forEach items="${invitePreferences}" var="prefs">
                            <aui:option label="${prefs.title}"
                                        selected="${externalUserFormBean.invitePreferences.id eq prefs.id}"
                                        value="${prefs.id}"/>
                        </c:forEach>
                    </aui:select>
                    <form:errors path="externalUserFormBean.invitePreferences" cssClass="portlet-msg-error"/>

                    <aui:input type="text" label="name" cssClass="mandatory-label" inlineField="true" name="name"
                               value="${externalUserFormBean.name}"
                               helpMessage="Namn och efternamn används för att skapa ett unikt inloggnings namn"/>
                    <form:errors path="externalUserFormBean.name" cssClass="portlet-msg-error"/>

                    <aui:input type="text" label="middleName" name="middleName"
                               value="${externalUserFormBean.middleName}"/>

                    <aui:input type="text" label="surname" cssClass="mandatory-label" name="surname"
                               value="${externalUserFormBean.surname}"
                               helpMessage="Dubbla efternam skall separeras med bindestreck, '-'"/>
                    <form:errors path="externalUserFormBean.surname" cssClass="portlet-msg-error"/>

                    <aui:input type="text" label="email" cssClass="mandatory-label" name="email"
                               value="${externalUserFormBean.email}"
                               helpMessage="Epost adress måste vara unik"/>
                    <form:errors path="externalUserFormBean.email" cssClass="portlet-msg-error"/>

                    <aui:input type="text" label="phone" name="phone" value="${externalUserFormBean.phone}"/>
                </aui:column>
                <aui:column columnWidth="50">
                    <aui:input type="text" label="mobile" name="mobile" value="${externalUserFormBean.mobile}"/>

                    <div id="<portlet:namespace />externStructurePersonDnDiv">
                        <aui:input id="externStructurePersonDn" type="text" label="externStructurePersonDn"
                                   cssClass="mandatory-label structure" name="externStructurePersonDn"
                                   value="${externalUserFormBean.externStructurePersonDn}"
                                   helpMessage="Organisationen sparas så att nästa inbjudan går snabbare att fylla i.
                                                Separera med slash,
                                                '/',
                                                ex. Företag/Avdelning/Grupp"/>
                    </div>
                    <form:errors path="externalUserFormBean.externStructurePersonDn" cssClass="portlet-msg-error"/>

                    <aui:input type="text" label="dateLimit" name="dateLimit"
                               value="${externalUserFormBean.dateLimit}"
                               helpMessage="Användarbehörigheten är tidsbegränad. Fyll i datum."
                               cssClass="mandatory-label"/>
                    <form:errors path="externalUserFormBean.dateLimit" cssClass="portlet-msg-error"/>

                    <aui:select name="userType" label="user-type" showEmptyOption="true"
                                helpMessage="Användartyp är valfri">
                        <aui:option
                                label="HSA"
                                value="HSA"
                                selected="${externalUserFormBean.userType eq \"HSA\"}"/>
                        <aui:option
                                label="Kommun"
                                value="Kommun"
                                selected="${externalUserFormBean.userType eq \"Kommun\"}"/>
                        <%--
                                                <aui:option
                                                        label="newUserType"
                                                        value="newUserType"
                                                        selected="${externalUserFormBean.userType eq \"newUserType\"}"/>
                        --%>
                    </aui:select>

                    <aui:field-wrapper label="sponsor-full-name" helpMessage="Den person som bjuder in blir sponsor.
                    Endast VGR-anställda får bjuda in.">
                        <div>${externalUserFormBean.sponsorFullName}</div>
                    </aui:field-wrapper>

                    <aui:input type="hidden" label="sponsor-full-name" name="sponsorFullName"
                               value="${externalUserFormBean.sponsorFullName}"/>

                    <aui:input type="hidden" label="sponsor-vgr-id" name="sponsorVgrId"
                               value="${externalUserFormBean.sponsorVgrId}"/>
                    <form:errors path="externalUserFormBean.sponsorVgrId" cssClass="portlet-msg-error"/>

                    <aui:button-row>
                        <aui:button type="submit" name="submitForm" value="Bjud in" style="margin-top: 14px"/>
                    </aui:button-row>
                </aui:column>
            </aui:layout>


        </aui:fieldset>
    </aui:form>
</div>

<portlet:resourceURL var="resourceUrl" escapeXml="false"/>

<script type="text/javascript">
    function NewDate(str) {
        str = str.split('-');
        var date = new Date();
        date.setUTCFullYear(str[0], str[1] - 1, str[2]);
        date.setUTCHours(0, 0, 0, 0);
        return date;
    }

    AUI().ready('aui-autocomplete',
            'aui-datepicker-select',
            'aui,base',
            'aui-dialog',
            function(A) {

                var dateLimit = NewDate('${externalUserFormBean.dateLimit}');
                if (dateLimit == 'Invalid Date') {
                    dateLimit = new Date(new Date().getTime() + 365 * 24 * 60 * 60 * 1000);
                }

                var datePicker = new A.DatePicker({
                    calendar: {
                        dateFormat: '%Y-%m-%d',
                        dates: [dateLimit],
                        firstDayOfWeek: 1,
                        selectMultipleDates: false
                    },
                    trigger: '#<portlet:namespace/>dateLimit'
                }).render();

                var autoComplete = new A.AutoComplete({
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
