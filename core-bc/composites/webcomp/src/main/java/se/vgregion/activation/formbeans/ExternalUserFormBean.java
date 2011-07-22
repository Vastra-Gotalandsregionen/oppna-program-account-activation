package se.vgregion.activation.formbeans;

import se.vgregion.create.domain.InvitePreferences;

import java.io.Serializable;
import java.util.Arrays;

public class ExternalUserFormBean implements Serializable {
    private static final long serialVersionUID = 123423412434333L;

    private InvitePreferences invitePreferences;
    private String name;
    private String middleName;
    private String surname;
    private String email;
    private String phone;
    private String mobile;
    private String externStructurePersonDn;
    private String userType;
    private String sponsorVgrId;
    private String sponsorFullName;
    private String dateLimit;
    private String vgrId;

    public InvitePreferences getInvitePreferences() {
        return invitePreferences;
    }

    public void setInvitePreferences(InvitePreferences invitePreferences) {
        this.invitePreferences = invitePreferences;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getExternStructurePersonDn() {
        return externStructurePersonDn;
    }

    public void setExternStructurePersonDn(String externStructurePersonDn) {
        this.externStructurePersonDn = externStructurePersonDn;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSponsorVgrId() {
        return sponsorVgrId;
    }

    public void setSponsorVgrId(String sponsorVgrId) {
        this.sponsorVgrId = sponsorVgrId;
    }

    public String getSponsorFullName() {
        return sponsorFullName;
    }

    public void setSponsorFullName(String sponsorFullName) {
        this.sponsorFullName = sponsorFullName;
    }

    public String getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(String dateLimit) {
        this.dateLimit = dateLimit;
    }

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    @Override
    public String toString() {
        return "ExternalUserFormBean{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", externStructurePersonDn=" + (externStructurePersonDn == null ? null : Arrays.asList(externStructurePersonDn)) +
                ", userType='" + userType + '\'' +
                ", vgrId='" + vgrId + '\'' +
                ", sponsorVgrId='" + sponsorVgrId + '\'' +
                ", sponsorFullName='" + sponsorFullName + '\'' +
                '}';
    }
}
