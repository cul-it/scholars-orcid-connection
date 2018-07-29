# TO DO
* __*Note that this contains todos for the orcid client, as well as for the orcid connection.*__

## Questions for Javed
* ORCID appears to require these elements for a journal article:
	* required: type, title
	* optional: 
		* journal-title, short-description, citation, publication-date, 
		* external-ids, url, contributors, language-code, country
	* __Do we have these for all pubs?__
* Subtitle is an optional field. Do we have those?
* Do we want to add contributors to publications?
	* Can hold ORCID-ID, name, email, role, and flag for FIRST or ADDITIONAL.
* Note: we are only doing Academic Articles, even though Conference Papers are available, and ORCID would accept them.
* IF they delete, what do we do?
* Acceptable level of use.

## Right NOW
* Improve the look of the ack screen and the push screen.
	* Landing page: 
		* look for permission. If found, check that it is still active.
		* If no access, 
			* "When you click the button, you will see the ORCID login.
				* Unless you're alrady logged in
			* "When you have logged in, you will be asked to authorize
		* If inactive access,
			* "Our records indicate that our permission to access ORCID record
			* When you click, you will see the ORCID login...
		* If active access
			* we already have permission, so when you click the button, 
	* Trash the landingPage template, and invalid
* Clean up the muddy cache situation: 
	* OrcidAuthClient should always use a session-based progress cache
	* The progress cache should accept an AccessTokenCache
* Clean up the AuthToken testing
	* Remove the tests from ProcessPushRequestController
	* PersonStatusApiController should look at the AccessToken date as well
	as the Work dates
	* LandingPageController tests and updates
* Create a real persistence cache
* Test pub logic:
	* Delete a pub from ORCID site -- Modify on scholars -- push -- it is ignored
	* Delete from the database -- push -- it is no longer ignored. 
	* Delete from Scholars -- push -- it is deleted from ORCID.
* Add more to the DataDistributor
* Improve Scholars adaptation:
	* Add the configuring mechanism -- base URL of connection.
	* Add the detection call
		* Draw the tab hidden, until we know that the connection works
		* If we have pushed already, show one thing
		* If we have not, show the other.
			* Make the link dynamic
* Implement the API request for status of a user (push date, count, active auth)
* Create the completion URL mechanism.
	* If present on landing, record it in the session
	* If present on terminal pages, offer as a link
* Implement the startup sequence for scorconn-ws
	* Load the startup parameters
	* Test the ORCID connection
* Define the interactions
	* Requests from outside
		* For a given LocalId (netID), what's the most recent push date?
		* Walk through the authorization process for a given LocalId and a given scope
		* Push the pubs for a given LocalId
	* Requests to outside
		* Ask the local system for a person's publications
		* Ask ORCID for current publications
		* Tell ORCID to add/update/delete publications
* Make the templates just a little prettier. Make them more modular.
* Move calls to DbLogger close to where the write to the database is made.

## Dealing with errors - A big question
* What types of errors shold we recover from, when pushing?
	* If the publication is rejected because the date is invalid, we should 
	continue with other pubs.
	* If the publication is rejected based on data syntax, what do we do?
	* Is this a reason to push works individually, or as a group?
	* How do we store the info from the failure?

## Real SOON
* Open the ORCID connection in a pop-up window.
* Get a better date for "last updated". 
	* Store in AccessToken as "VERIFIED" column, with timestamp.
* How does one register with Cornell on ORCID? What does it look like?
* OrcidClientContext -- get rid of the Settings; make it in terms of Strings.
	* Include the Base URL as one of the URLs derived from "sandbox" or "production": https://sandbox.orcid.org
* Rewrite the data layer to use meaningful IDs, and object-oriented updates.
	* [look here.](https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-hibernate)
* Remove the confusion in the purpose of the cache. 
	* Should we add methods for bare get/set of access tokens?
	* Should we create an implementation of the cache that accepts an AccessToken cache?
* Is anyone using the Success URL and Failure URL? Can we eliminate or combine them? Should we add a Declined URL?
* Create a "liveness" call to ORCID
	* context.checkOauthUrls
	* context.checkPublicApi
	* context.checkAuthorizedApi
* Startup status
	* Check SQL, Scholars, ORCID - messages for each
	* Return 500 from filter
* Rigorous checking on builders (most notably fuzzy date?) (required fields in Work?)
* Specify either uri or (path && host) Check received data for details
* Check that the user is who he says he is:
	* NetID from CUWebLogin must match NetID from original call
		* If no original call, skip that
* Guard against multiple associations in Person (one to many? many to many?)
* testWebapp
	* Improve flow of the testWebapp.
		* Any client-based function will present the available tokens along with the function choice, and
		the button won't be enabled until both have been chosen.
		* Any raw function will... ?
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
* Is there some way to improve the AbstractReadAction.Endpoint class? Maybe with a factory method to create a typed Endpoint?
	

## Top level - pushing pubs
* External identifiers
	* We use Scholars URI as the unique identifier for publications. 
	We can add DOI or ISBN, but the Scholars URI is the decider.
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

