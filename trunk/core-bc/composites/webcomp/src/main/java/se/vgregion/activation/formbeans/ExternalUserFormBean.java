package se.vgregion.activation.formbeans;

import java.io.Serializable;
import java.util.Arrays;

public class ExternalUserFormBean implements Serializable {
    private static final long serialVersionUID = 123423412434333L;

    private String name;
    private String surname;
    private String email;
    private String phone;
    private String[] externStructurePersonDn = {"", "", ""};
    private String userType;
    private String sponsorVgrId;
    private String sponsorFullName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String[] getExternStructurePersonDn() {
        return externStructurePersonDn;
    }

    public void setExternStructurePersonDn(String[] externStructurePersonDn) {
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

    @Override
    public String toString() {
        return "ExternalUserFormBean{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", externStructurePersonDn=" + (externStructurePersonDn == null ? null : Arrays.asList(externStructurePersonDn)) +
                ", userType='" + userType + '\'' +
                ", sponsorVgrId='" + sponsorVgrId + '\'' +
                ", sponsorFullName='" + sponsorFullName + '\'' +
                '}';
    }
}
