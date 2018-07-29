# Acceptance testing for the Scholars-ORCID Connection (SCORconn)

## Web service
* __*TBD*__
* User has authorized already - active AccessToken on file
* User has authorized and rescinded - inactive AccessToken on file
* User has never authorized, authorizes - no AccessToken on file
* User has never authorized, declines to authorize

* User has more than one ORCID ID. Test going back and forth between them in the web service

## Batch process
* __*TBD*__
* User has more than one ORCID ID. Test for good warnings.

## Scholars

#### No connection:

* When 
	* Scholars-ORCID connection web server is not running,
* Do
	* View a Faculty member in Scholars
* Then
	* See the profile page with no sign of an ORCID connection.

### Never authorized:

* When
	* Scholars-ORCID connection web server is running,
	* Faculty Member "A" has never authorized pushing publications to ORCID,
* Do
	* View Faculty Member "A" in Scholars
* Then
	* See the profile page with an invitation to push publications to ORCID.

### Authorized:

* When
	* Scholars-ORCID connection web server is running,
	* Faculty Member "A" has authorized pushing publications to ORCID,
* Do
	* View Faculty Member "A" in Scholars
* Then
	* See a summary of the publications that have been pushed to ORCID.

### Authorization rescinded:
* When
	* Scholars-ORCID connection web server is running,
	* Faculty Member "A" has rescinded permission to push publications to ORCID,
* Do
	* View Faculty Member "A" in Scholars
* Then
	* See a summary of the publications that have been pushed to ORCID.
	* SAME AS "Authorized" CASE.
		
* __*TBD*__
