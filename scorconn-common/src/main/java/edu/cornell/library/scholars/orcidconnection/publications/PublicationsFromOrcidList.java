/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.actions.read.ReadWorksAction;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.orcidclient.orcid_message_2_1.activities.WorkGroup;
import edu.cornell.library.orcidclient.orcid_message_2_1.activities.WorksElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalId;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkSummaryElement;

/**
 * Ask ORCID what Works are in the user's record. Find all the ones that we
 * created, and map their Scholars URI to the ORCID putcode.
 */
public class PublicationsFromOrcidList implements PublicationsFromOrcid {
    private final Map<String, String> scholarsUriToPutCode = new HashMap<>();

    public PublicationsFromOrcidList(AccessToken accessToken)
            throws OrcidClientException {
        WorksElement summaries = createAction().readSummaries(accessToken);

        for (WorkGroup group : summaries.getGroup()) {
            for (WorkSummaryElement work : group.getWorkSummary()) {
                if (isOneOfOurs(work)) {
                    addToMap(work);
                }
            }
        }
    }

    private ReadWorksAction createAction() {
        BaseHttpWrapper httpWrapper = new BaseHttpWrapper();
        OrcidActionClient client = new OrcidActionClient(getOcc(), httpWrapper);
        return client.createReadWorksAction();
    }

    private boolean isOneOfOurs(WorkSummaryElement work) {
        String clientFromContext = getOcc().getClientId();
        String clientFromWork = work.getSource().getSourceClientId().getPath();
        return clientFromWork.equals(clientFromContext);
    }

    private void addToMap(WorkSummaryElement work) throws OrcidClientException {
        BigInteger putCode = work.getPutCode();
        if (putCode == null) {
            throw new OrcidClientException("WorkSummaryElement has no putCode");
        }

        String scholarsUri = getScholarsUri(work);
        if (scholarsUri == null) {
            throw new OrcidClientException(
                    "WorkSummaryElement has no Scholars URI");
        }

        scholarsUriToPutCode.put(scholarsUri, String.valueOf(putCode));
    }

    private String getScholarsUri(WorkSummaryElement work) {
        for (ExternalId id : work.getExternalIds().getExternalId()) {
            if (id.getExternalIdType().equals("other-id")) {
                return id.getExternalIdUrl();
            }
        }
        return null;
    }

    private OrcidClientContext getOcc() {
        return OrcidClientContext.getInstance();
    }

    @Override
    public Set<String> getUris() {
        return scholarsUriToPutCode.keySet();
    }

    @Override
    public boolean hasUri(String scholarsUri) {
        return scholarsUriToPutCode.containsKey(scholarsUri);
    }

    @Override
    public String getPutCode(String scholarsUri) {
        return scholarsUriToPutCode.get(scholarsUri);
    }

}
