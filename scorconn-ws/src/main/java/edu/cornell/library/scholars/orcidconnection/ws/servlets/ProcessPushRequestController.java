/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.actions.ApiScope.PERSON_UPDATE;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.SERVLET_PROCESS_PUSH_REQUEST;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.TEMPLATE_ACKNOWLEDGE_PUSH_PROCESSING_PAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.TEMPLATE_INVALID_TOKEN_PAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.PublicationsUpdateProcessor;
import edu.cornell.library.scholars.orcidconnection.accesstokens.BogusCache;
import edu.cornell.library.scholars.orcidconnection.data.DbLogger;
import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Severity;
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
public class ProcessPushRequestController extends HttpServlet {
    private static final String SERVLET_URL = "/ProcessPushRequest";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new ServletCore(req, resp).doGet();
    }

    /** Thread-safe inner class */
    private static class ServletCore {
        private final HttpServletRequest req;
        private final HttpServletResponse resp;
        private final OrcidClientContext occ;
        private final BogusCache cache;
        private final OrcidAuthorizationClient authClient;
        private AuthorizationStateProgress progress;
        private AccessToken accessToken;

        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            this.req = req;
            this.resp = resp;
            this.occ = OrcidClientContext.getInstance();
            this.cache = BogusCache.getCache(req, getLocalId(req));
            this.authClient = new OrcidAuthorizationClient(occ, cache,
                    new BaseHttpWrapper());
        }

        private void doGet() throws IOException {
            try {
                getAccessTokenFromCache();
                if (!isAccessTokenPresent()) {
                    redirectIntoThreeLeggedOauthDance();
                } else if (!isAccessTokenStillValid()) {
                    recordAccessTokenNotValid();
                    showInvalidTokenPage();
                } else {
                    requestAsynchronousUpdate();
                    showAcknowledgementPage();
                }
            } catch (OrcidClientException e) {
                throw new RuntimeException("Failed to process the push request",
                        e);
            }
        }

        private void getAccessTokenFromCache() {
            try {
                progress = cache.getByScope(PERSON_UPDATE);
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
                        authClient.createProgressObject(PERSON_UPDATE, callback,
                                callback)));
            } catch (URISyntaxException e) {
                throw new RuntimeException(
                        "Orcid context returned an invalid callback URL", e);
            } catch (OrcidClientException | IOException e) {
                throw new RuntimeException("Failed to start the OAuth dance.",
                        e);
            }
        }

        private boolean isAccessTokenStillValid() throws OrcidClientException {
            OrcidActionClient actionClient = new OrcidActionClient(occ,
                    new BaseHttpWrapper());
            return actionClient.isAccessTokenValid(accessToken);
        }

        private void recordAccessTokenNotValid() throws OrcidClientException {
            DbLogger.writeLogEntry(Severity.INFO,
                    "Access token was not valid: %s", accessToken);
            cache.store(progress.addAccessToken(NO_TOKEN));
        }

        private void showInvalidTokenPage() throws IOException {
            new PageRenderer(req, resp) //
                    .setValue("localId", getLocalId(req))
                    .render(TEMPLATE_INVALID_TOKEN_PAGE);
        }

        private void requestAsynchronousUpdate() {
            new PublicationsUpdateProcessor(getLocalId(req)).run();
        }

        private void showAcknowledgementPage() throws IOException {
            new PageRenderer(req, resp) //
                    .setValue("localId", getLocalId(req))
                    .render(TEMPLATE_ACKNOWLEDGE_PUSH_PROCESSING_PAGE);
        }
    }
}
