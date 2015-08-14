[[Previous](InstallationAndConfiguration.md)] [[Next](OverviewService.md)]

# Introduction #

The Activation Code Service is an infrastructure ReST service for creating and handeling one time tokens.

# Details #

When a user has been invited, an activation toke are sent to the user in an email. When following the activation link in the email, the token is validated and matched against the invitation. If everything match up the token can be used to set login.

The activation token is constructed in such a way that it isn't possible to find out who is invited by the token alone.
  * The token is only valid for a limited amount of time.
  * It can only be used to activate the person invited.
  * It can only be user one time.

The Activation Code Service are primarily used by the backen system ([Mule ESB](http://smwiki.vgregion.se/index.php/Portal_-_Externa_Anv%C3%A4ndare)).

[[Previous](InstallationAndConfiguration.md)] [[Next](OverviewService.md)]