# Developer Notes for the Scholars-ORCID Connection (SCORconn)

## Structure
* __*TBD*__
* Common, ORCID API Client, scorconn-ws, scorconn-cl

## Hibernate for persistence
* __*TBD*__

## Database design

### Created using [QuickDBD](https://www.quickdatabasediagrams.com/)
* ![database design](./QuickDBD-Scholars-ORCID Connection.png)
* [Source file](./database_schema.txt)

### Person table
* Associates a local ID (NetID) with an OrcidId (and name). 
* Record is added when the user logs in to Scorconn and authenticates to ORCID.
* If a local ID maps to more than one OrcidID, log a warning and behave as if it maps to none at all.
	* In the web service, require authentication
	* In the batch process, do not attempt updates
		* Probably, the batch process should begin by scanning the table for multiple mappings, 
		and reporting a list of complaints.
* Otherwise, if there is more than one record for a local ID, use the most recent.

### AccessToken table
* Holds all sorts of information about an access token. 
* Record is added when the user grants permission for a particular scope of access. Multiple records may occur if 
the user rescinds permission, and then grants it again.
* If multiple records exist for an Orcid ID and scope, use the most recent.
* Probably the only required information is ORCID\_ID, SCOPE, ACCESS\_TOKEN, and CREATED. 
* The other fields may be useful for forensics. Orcid recommends that you store the JSON, for example.

### Work table
* _Is this needed at all, except for forensics? Do we want to add multiple records on each update? Will the log handle this type of history?_

### LogEntry table
* Write anything important here.

## The Authorization Filter
* __*TBD*__
* Not hooked up to CUWebAuth? Pretend

## Startup status
* __*TBD*__
* Another filter, how does it work, what does it do? Why is it good? (returns a 500)

## Application flow
* __*TBD*__
* _Is it reasonable to describe all of the paths through the application?_
* _Don't forget the completion URL (optional)_

## Acceptance test cases
### Web service
* User has more than one ORCID ID. Test going back and forth between them in the web service

### Batch process
* User has more than one ORCID ID. Test for good warnings.

----------
----------

# TO DO
* __*Note that this contains todos for the orcid client, as well as for the orcid connection.*__

## Right NOW
* write OrcidActionClient.isAccessTokenValid()
º* Create servlet2:
	* reacts to that button.
	* If no accessToken for the user, get accessToken
		* Create callback servlet that updates the persistence cache or expresses regrets to the user (with optional completion URL)
		* write to the log
	* request servlet3 to push for that user
		* write to the log
* Create servlet3:
	* get the publications from scholars
	* push them to orcid
		* write to the log
* Create a real persistence cache
* Create the completion URL mechanism.
	* If present on landing, record it in the session
	* If present on terminal pages, offer as a link
* Implement the startup sequence for scorconn-ws
	* Load the startup parameters
	* Test the database connection
	* Test the ORCID connection
	* Test the scholars connection
* Define the interactions
	* Requests from outside
		* For a given LocalId (netID), what's the most recent push date?
		* Walk through the authorization process for a given LocalId and a given scope
		* Push the pubs for a given LocalId
	* Requests to outside
		* Ask the local system for a person's publications
		* Ask ORCID for current publications
		* Tell ORCID to add/update/delete publications

## Real SOON
* Remove the confusion in the purpose of the cache. 
	* Should we add methods for bare get/set of access tokens?
	* Should we create an implementation of the cache that accepts an AccessToken cache?
* Create a "liveness" call to ORCID
	* context.checkOauthUrls
	* context.checkPublicApi
	* context.checkAuthorizedApi
* Startup status
	* Check SQL, Scholars, ORCID - messages for each
	* Return 500 from filter
* Rigorous checking on builders (most notably fuzzy date?) (required fields in Work?)
* Specify either uri or (path && host) Check received data for details
* testWebapp
	* Improve structure of webapp: forms, controllers, inheritance, templates, etc.
	* Improve the user experience in the test webapp
		* If we turn down the noise, does it get quiet? LEVEL=Info, Warn?
	* Refactor the test-webapp
* Test the VIVO functionality: 
* Doc
	* Explain enough that one could write the VIVO functionality.
	* Package this with JavaDocs, etc., for an artifact.
* Corner cases:
	* Expired short-term token: WHAT happens?
	* Revoked long-term token: WHAT happens?
* Remove the test-command-line and mock-orcid-server  and Obsolete  areas.
* In WS or CL, beware of multiple ORCID IDs for one NetID.
	* In WS, treat them as is they have no ORCID ID and require them to authenticate.
	* In CL, skip them as if they had no ORCID ID and print a warning. Perhaps start by scanning for them.

## Top level - pushing pubs
* External identifiers
	* We don't have identifiers DOI/ISBN for each document that we push.
	* We can use Scholars URI as a persistent identifier
	* What happens if we have both DOI and Scholars URI
		* Syntactically not a problem, API accepts multiple external identifiers
		* What if the DOI connects with another source that does not possess the Scholars URI?
* Approach to updates
	* Incremental approach:
		* Read all (Scholars-based) pubs from ORCID
		* Construct all pubs from Scholars
		* Delete any that are in ORCID but not in Scholars
		* Add any that are in Scholars but not in ORCID
		* Update any that differ between Scholars and ORCID
		* Ignore any that are the same in both Scholars and ORCID
	* Total approach
		* Delete all Scholars-based pubs from ORCID
		* Add all pubs from Scholars to ORCID
	* Contrast
		* Is this feasible with 100 pubs?
		* How does the notification differ?
		* Do the replacements behave like updates?
			* Still grouped by persistent identifier?
	* Phase 1 total, phase 2 incremental?

## Remember document

* REMEMBER: NOT TIED TO VIVO/SCHOLARS!!
* Architecture
	* Don’t have identifiers (DOI/ISBN) for each document that we push. 
 		* Can we use Scholars URI as a persistent identifier?
	* Is the approach of  “delete it all/push from scratch” practical?
 		* If not, how do we deal with the fact that the Uber-record has changed for a particular document?
* User experience:
	* Give the user an empowering clue, like “push to ORCID”?
 		* That be the top-level button. Then the subsequent info gives more detail
	* Use the ORCID visual style
	* How do they know when it’s complete? Email notification
 		* Tell them notification from ORCID
 		* Show them that it is in progress, then that it is complete
 		* How do we keep track of the background process?
			* “State” includes “last updated”, but also “in progress”
			* What happens if Scholars shuts down during process?
			* In progress indicator stored in the database.
	* Don’t like “Enable updates/Disable updates”
 		* Tim
 		* Does the first link take you to a full screen?
			* When do you log in? – as soon as they click anything
	* CUWebLogin, then check that the netId matches the Scholars page that they came from.
* QUESTIONS:
	* Rescinded permission. What does that look like?
* NEXT:
		* What does a pushed document look like?

## Don't Forget
* Test for an assortment of errors by modifying the form data

