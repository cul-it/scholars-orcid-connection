# Acceptance testing for the Scholars-ORCID Connection (SCORconn)

## Web service

| User has not authorized, authorizes | |
| --- | --- |
| When | ORCID account __does not trust__ Scholars@Cornell |
| When | Connection has no `AccessToken` on file for ORCID account. |
| When | ORCID account has no publications from Scholars@Cornell |
| Go to Connection | See CUWebAuth login page |
| Login with Cornell NetId | See the ORCID signin page |
| Login to ORCID and __grant permission__ | See the page: "Publications transfer in progress"
| Follow the link to the user's ORCID record | See the publications |

| User has not authorized, declines | |
| --- | --- |
| When | ORCID account __does not trust__ Scholars@Cornell |
| When | Connection has no `AccessToken` on file for ORCID account. |
| When | ORCID account has no publications from Scholars@Cornell |
| Go to Connection | See CUWebAuth login page |
| Login with Cornell NetId | See the ORCID signin page |
| Login to ORCID but __don't grant permission__ | See the page: "User has denied access"
| Follow the link to the user's ORCID record | See __no__ publications |

| User has authorized already | |
| --- | --- |
| When | ORCID account __trusts__ Scholars@Cornell |
| When | Connection has an `AccessToken` on file for ORCID account. |
| Go to Connection | See CUWebAuth login page |
| Login with Cornell NetId | See the page: "Publications transfer in progress"
| Follow the link to the user's ORCID record | See the publications |

| User has authorized and rescinded | |
| --- | --- |
| When | ORCID account __does not trust__ Scholars@Cornell |
| When | Connection has an `AccessToken` on file for ORCID account. |
| Go to Connection | See CUWebAuth login page |
| Login with Cornell NetId | See the page: "Permission was rescinded" |
| Click __Go to ORCID__, login to ORCID and __grant permission__ | See the page: "Publications transfer in progress"
| Follow the link to the user's ORCID record | See the publications |

* __*TBD*__
* User has more than one ORCID ID. Test going back and forth between them in the web service
* User deletes Scholars@Cornell source from a pub on ORCID record. Scholars@Cornell will not put it back.

## Batch process
* __*TBD*__
* User has more than one ORCID ID. Test for good warnings.

## Scholars

| No connection | |
| --- | --- |
| When | Scholars-ORCID connection web server is not running |
| View a Faculty member profile in Scholars | See no sign of an ORCID connection. |

| Never authorized | |
| --- | --- |
| When | Scholars-ORCID connection web server is running |
| When | Faculty Member "A" has never authorized pushing publications to ORCID |
| View a Faculty member "A" profile in Scholars | See an invitation to push publications to ORCID. |

| Authorized | |
| --- | --- |
| When | Scholars-ORCID connection web server is running |
| When | Faculty Member "A" has authorized pushing publications to ORCID |
| View a Faculty member "A" profile in Scholars | See a summary of the publications that have been pushed to ORCID. |

| Authorized rescinded | |
| --- | --- |
| When | Scholars-ORCID connection web server is running |
| When | Faculty Member "A" has authorized pushing publications to ORCID, and then rescinded permission |
| View a Faculty member "A" profile in Scholars | __Same as *Authorized* case__ |
		
* __*TBD*__

## Other docs
* [Developer Notes](./ScorconnDeveloperNotes.md)
* [To-do list](./Scorconn_TO_DO.md)
