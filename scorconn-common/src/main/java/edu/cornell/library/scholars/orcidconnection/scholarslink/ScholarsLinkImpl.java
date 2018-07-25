/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper.GetRequest;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.library.scholars.orcidconnection.publications.Publication;

/**
 * TODO
 */
public class ScholarsLinkImpl extends ScholarsLink {
    private static final Log log = LogFactory.getLog(ScholarsLinkImpl.class);

    public static final String PROPERTY_SCHOLARS_BASE_URL = "scholarsBaseUrl";

    private final Map<String, String> properties;
    private final HttpWrapper httpWrapper;

    private final String baseUrl;
    private final String requestUrl;

    public ScholarsLinkImpl(Map<String, String> properties,
            HttpWrapper httpWrapper) throws ScholarsLinkException {
        this.properties = properties;
        this.httpWrapper = httpWrapper;

        this.baseUrl = getBaseUrl();
        this.requestUrl = this.baseUrl
                + "/api/dataRequest/listPublicationsForOrcidConnection?localID=";
    }

    private String getBaseUrl() throws ScholarsLinkException {
        String url = properties.get(PROPERTY_SCHOLARS_BASE_URL);
        if (url == null) {
            throw new ScholarsLinkException(
                    "RuntimeProperties must include a value for '"
                            + PROPERTY_SCHOLARS_BASE_URL + "'.");
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    @Override
    public void checkConnection() throws ScholarsLinkException {
        try {
            GetRequest request = httpWrapper
                    .createGetRequest(requestUrl + "bogusNetid");
            request.execute();
        } catch (HttpStatusCodeException | IOException e) {
            throw new ScholarsLinkException(
                    "Scholars is not available at '" + baseUrl + "'");
        }
    }

    @Override
    public List<Publication> getPublications(String localId)
            throws ScholarsLinkException {
        try {
            GetRequest request = httpWrapper
                    .createGetRequest(requestUrl + localId);
            HttpResponse response = request.execute();
            return Publication.fromJson(response.getContentString());
        } catch (HttpStatusCodeException | IOException e) {
            throw new ScholarsLinkException(
                    "Scholars is no longer available at '" + baseUrl + "'");
        }
    }
}
