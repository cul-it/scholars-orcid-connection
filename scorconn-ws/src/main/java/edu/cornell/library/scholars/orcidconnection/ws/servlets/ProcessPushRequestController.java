/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.actions.ApiScope.ACTIVITIES_UPDATE;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_PROCESS_PUSH_REQUEST;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getOrcidRecordPageUrl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.PublicationsUpdateProcessor;
import edu.cornell.library.scholars.orcidconnection.accesstokens.BogusCache;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * The user has said that they are ready to push the publications.
 * 
 * Do we have an access token? If not, get one.
 * 
 * Is the access token still valid? If not, may we get a new one?
 * 
 * If we have a valid access token, kick off the push process.
 */
@WebServlet(name = SERVLET_PROCESS_PUSH_REQUEST, urlPatterns = "/ProcessPushRequest")
public class ProcessPushRequestController extends HttpServlet
        implements WebServerConstants {
    private static final Log log = LogFactory
            .getLog(ProcessPushRequestController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new ServletCore(req, resp).doGet();
    }

    /** Thread-safe inner class */
    private static class ServletCore {
        private final HttpServletRequest req;
        private final HttpServletResponse resp;
        private final String localId;
        private final OrcidClientContext occ;
        private final BogusCache cache;
        private final OrcidAuthorizationClient authClient;
        private AuthorizationStateProgress progress;
        private AccessToken accessToken;

        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            this.req = req;
            this.resp = resp;
            this.localId = getLocalId(req);
            this.occ = OrcidClientContext.getInstance();
            this.cache = BogusCache.getCache(req, localId);
            this.authClient = new OrcidAuthorizationClient(occ, cache,
                    new BaseHttpWrapper());
        }

        private void doGet() throws IOException {
            getAccessTokenFromCache();
            if (!isAccessTokenPresent()) {
                redirectIntoThreeLeggedOauthDance();
            } else {
                requestAsynchronousUpdate();
                showAcknowledgementPage();
            }
        }

        private void getAccessTokenFromCache() {
            try {
                progress = cache.getByScope(ACTIVITIES_UPDATE);
                if (progress == null) {
                    accessToken = NO_TOKEN;
                } else {
                    accessToken = progress.getAccessToken();
                }
            } catch (OrcidClientException e) {
                throw new RuntimeException("Failed to read the cache.", e);
            }
        }

        private boolean isAccessTokenPresent() {
            return (accessToken != null) && (accessToken != NO_TOKEN);
        }

        private void redirectIntoThreeLeggedOauthDance() {
            try {
                URI callback = new URI(occ.getCallbackUrl());
                resp.sendRedirect(authClient.buildAuthorizationCall(
                        authClient.createProgressObject(ACTIVITIES_UPDATE,
                                callback, callback)));
            } catch (URISyntaxException e) {
                throw new RuntimeException(
                        "Orcid context returned an invalid callback URL", e);
            } catch (OrcidClientException | IOException e) {
                throw new RuntimeException("Failed to start the OAuth dance.",
                        e);
            }
        }

        private void requestAsynchronousUpdate() {
            log.info("Web service requests asynchronous update for " + localId);
            new PublicationsUpdateProcessor(localId, accessToken).start();
        }

        private void showAcknowledgementPage() throws IOException {
            String orcid = accessToken.getOrcid();
            new PageRenderer(req, resp) //
                    .setValue("localId", localId)
                    .setValue("orcidId", orcid)
                    .setValue("orcidIdUrl", getOrcidRecordPageUrl(orcid))
                    .render(TEMPLATE_ACKNOWLEDGE_PUSH_PROCESSING_PAGE);
        }
    }
}
