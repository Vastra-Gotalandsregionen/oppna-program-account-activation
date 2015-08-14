# Overview #

The External Organization service are intended as a helper for clients that want to provide the "invite external user" themselves.

The service provides an interface to a central store of hierarchal organization store that has already been used for inviting external users. It is **not** intended as a authoritative list of allowed organizations, but rather intended as a help for the person inviting to cull out minor spelling differences and so on.

This help functionality can be implemented as a write ahead field (i.e. google-search).
It has been used in this way by the "Invite user"-portlet in this project

# Rest interface #

The External Organization service is a RESTful service exposing the following methods (the http method and the relative path is given in the list):
  * GET, /external-org/search?query=`<query-string>`
    * Fetch exact matches and child matches one level down. The whole path for each match are returned. A maximum of 10 hits are returned.
    * `curl --get http://localhost:8080/account-activation-service/external-org/search?query=Red`
> > > ```
["Redpill-Linpro","Redpill-Linpro/JSOL"]```
  * PUT, /external-org/search?organization=`<full/path/org>`
    * Save an hierarchal-path organization. The service makes sure no duplicates are stored. A successful reply only results in a 204 response without any content.
    * `curl --request PUT localhost:8080/account-activation-service/external-org/save?organization=Redpill-Linpro/SAS`