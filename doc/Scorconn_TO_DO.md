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

### ORCID client
* Look at their JAXB model: 
	* https://groups.google.com/forum/#!msg/orcid-api-users/U8YyD-Nn4rs/ikS_0Ui1BQAJ
	* https://github.com/ORCID/orcid-conversion-lib
	* https://github.com/ORCID/orcid-conversion-lib/tree/master/orcid-model
* Readme.txt
	* Make it clear that this does not build on the VIVO driver.

### SCORconn

#### Configure Hibernate
* Configure Hibernate from runtime.properties
	* Give Greg the updated file
	* Use the `feature/configure_hibernate` branch, but merge to `develop` before asking Greg to try it.
	* Tell Greg that we need a `scorconn` database.

#### Other
* OauthCallbackController should redirect to status screens, not render them itself.
	* Who else does this apply to? 
	* Should we have a framework?
* Use the C3PO connection pool for Hibernate
	* https://www.mchange.com/projects/c3p0/#hibernate-specific
* Developers Notes
* Modify ProcessPushRequestController to provide more meaningful URL than the callback URL
	* Would the callback controller be able to use those URLs
* Change the flow:
	* New flow:
		* On landing, store return URL
		* If no access token, start the dance.
			* Else, store in session
		* If access token has expired, show landingPageInvalidToken.twig.html
		* On completion, show either "will be pushed" or "will be updated"

	* Landing page takes us directly to ORCID
		* Unless access token has expired, then to landingPageInvalidToken.twig.html
	* Add return links to all pages -- return to Scholars@Cornell profile

* Clean up the AuthToken testing
	* PersonStatusApiController should look at the AccessToken date as well
	as the Work dates
* Test pub logic:
	* Delete a pub from ORCID site -- Modify on scholars -- push -- it is ignored
	* Delete from the database -- push -- it is no longer ignored. 
	* Delete from Scholars -- push -- it is deleted from ORCID.
* Add more to the DataDistributor
* Improve Scholars adaptation:
	* Add the configuring mechanism -- base URL of connection. -- PROPER EXAMPLE
* Create the completion URL mechanism.
	* If present on landing, record it in the session
	* If present on terminal pages, offer as a link
* Should ServletUtils disappear in favor of AbstractServletCore?
* OrcidAuthorizationClient.checkConnection() should become static?
	* If we don't need to instantiate, we don't need any caches -- WebappSetup

## Dealing with errors - A big question
* What types of errors shold we recover from, when pushing?
	* If the publication is rejected because the date is invalid, we should 
	continue with other pubs.
	* If the publication is rejected based on data syntax, what do we do?
	* Is this a reason to push works individually, or as a group?
	* How do we store the info from the failure?

## Real SOON
* OrcidAuthCallbackController should never render a result or even forward - it should always redirect.
* Open the ORCID connection in a pop-up window.
* Get a better date for "last updated". 
	* Store in AccessToken as "VERIFIED" column, with timestamp.
* How does one register with Cornell on ORCID? What does it look like?
* Rewrite the data layer to use meaningful IDs, and object-oriented updates.
	* [look here.](https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-hibernate)
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

## Other docs
* [Developer Notes](./ScorconnDeveloperNotes.md)
* [Acceptance tests](./ScorconnAcceptanceTesting.md)
