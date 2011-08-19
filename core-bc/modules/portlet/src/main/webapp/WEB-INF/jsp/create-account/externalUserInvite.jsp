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

    .aui-field-input {
        width: 200px;
        background-image: url(/vgr-theme/images/forms/input_shadow.png);
        background-repeat: no-repeat;
    }

    .mandatory-label span label {
        background: transparent url(${pageContext.request.contextPath}/images/red_asterisk.gif) no-repeat right center;
        display: inline;
        padding-right: 18px;
    }

    .structure span span div div span span .aui-field-input {
        width: 175px;
    }

    .wrapper-div {
        border-color: #AAAAAA;
        border-style: solid;
        border-width: 1px;
        margin-left: 0;
        margin-right: auto;
        padding: 10px;
        width: 500px;
    }

    .taglib-icon-help img {
        margin-bottom: 3px;
    }

    .cnf-title {
        font-weight: bold;
        display: inline-block;
        width: 80px;
        text-align: right;
        padding: 3px;
    }

    .cnf-value {
        padding:  3px;
    }

    .cnf-button {
        text-align: center;
        width: 100%;
    }

</style>

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

<p><a href="${resourceUrl}" class="test-link" title="Bekräfta">Test Link</a></p>

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

                var TPL = '<p><span class="cnf-title">Namn:</span><span class="cnf-value">{fullName}</span><br/>' +
                        '<span class="cnf-title">Epost:</span><span class="cnf-value">{email}</span><br/>' +
                        '<span class="cnf-title">Org:</span><span class="cnf-value">{org}</span><br/>' +
                        '<span class="cnf-title">Inbjuden till:</span><span class="cnf-value">{system}</span></p>' +
                        '<div class="cnf-button">' +
                        '<input type="button"' +
                        'onClick="document.getElementById(\'<portlet:namespace/>create-external-user-form\').submit();"' +
                        'value="Skicka">' +
                        '</p>';

                var documentDialogOptions = {
                    bodyContent: 'Testing body',
                    centered: true,
                    constrain2view: true,
                    destroyOnClose: false,
                    draggable: true,
                    group: 'default',
                    height: 170,
                    modal: true,
                    stack: true,
                    // shim: true,
                    title: 'Dokument',
                    width: 350
                };


                // Event listener - on before documentDialog render
                function onBeforeDocumentDialogRender(e, params) {
                    // Instance is document dialog
                    var instance = this;

                    var fullName = params[0];
                    var email = params[1];
                    var org = params[2];
                    var system = params[3];

                    var contentIFrame = A.substitute(TPL, {
                        fullName: fullName,
                        email: email,
                        org: org,
                        system: system
                    });

                    instance.set('bodyContent', contentIFrame);
                }

                // Event listener - on documentLink click
                function onTestLinkClick(e) {
                    e.halt();

                    var name = document.getElementById('<portlet:namespace/>name').value;
                    var middleName = document.getElementById('<portlet:namespace/>middleName').value;
                    var surname = document.getElementById('<portlet:namespace/>surname').value;
                    var fullName = name + ' ' + (middleName != '' ? (middleName + ' ') : '') + surname;

                    var email = document.getElementById('<portlet:namespace/>email').value;
                    var org = document.getElementById('<portlet:namespace/>externStructurePersonDn').value;

                    var inviteTo = document.getElementById('<portlet:namespace/>invitePreferences');
                    var system = inviteTo.options[inviteTo.selectedIndex].text;

                    createDocumentDialog("Bekräfta inbjudan", fullName, email, org, system);
                }

                // Creates a document dialog
                function createDocumentDialog(dialogTitle, fullName, email, org, system) {

                    var documentDialog = new A.Dialog(
                            A.merge(documentDialogOptions, {
                                title: dialogTitle
                            })
                    );
                    documentDialog.before('render', onBeforeDocumentDialogRender, documentDialog,
                            [fullName, email, org, system]);

                    // On before render listener

                    // Render dialog
                    documentDialog.render();
                }

                // Attach listeners
                A.all('#<portlet:namespace/>submitForm').on('click', onTestLinkClick);

            });
</script>
