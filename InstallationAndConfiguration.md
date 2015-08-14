[[Previous](ReinviteUser.md)] [[Next](OverviewActivationCodeService.md)]

# Introduction #

The configuration can basically be divided into three parts:
  * LDAP
  * Camel
  * Database

Backend
  * [Backend documentation](http://smwiki.vgregion.se/index.php/Portal_-_Externa_Anv%C3%A4ndare)![http://oppna-program-account-activation.googlecode.com/svn/wiki/external_link_icon.gif](http://oppna-program-account-activation.googlecode.com/svn/wiki/external_link_icon.gif)

## LDAP ##
The LDAP configuration can be found in webcomp in the ldap-configuration.properties and security.properties files.

## Camel ##
The Camel configuration consists of two parts: the internal messagebus destinations and the external endpoints. In addition, the JMS brokerURL has to be given. The Account Activation portlets communicate with three different server services. Each service is configured separately. The configuration is found in the camel project in the file camel.properties.

Communication with the backend are done over mutual trusted SSL which makes it necessary to exchange public certificates between the backend system and the account activation system. This configuration is handled in security.properties.

## Database ##
The database JNDI connection is configured in the Tomcat server.xml file.

The tables used are:
  * vgr\_activation\_external\_user\_structure (for organization structure)
  * vgr\_activation\_invite\_preferences (for system which users can be invited to)

[[Previous](ReinviteUser.md)] [[Next](OverviewActivationCodeService.md)]