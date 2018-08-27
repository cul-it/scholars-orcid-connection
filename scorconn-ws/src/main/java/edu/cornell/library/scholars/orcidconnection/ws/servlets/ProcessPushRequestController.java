/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.actions.ApiScope.ACTIVITIES_UPDATE;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_PUSH_PUBS;

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
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.scholars.orcidconnection.PublicationsUpdateProcessor;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;

/**
 * The user has said that they are ready to push the publications.
 * 
 * Do we have an access token? If not, get one.
 * 
 * Is the access token still valid? If not, may we get a new one?
 * 
 * If we have a valid access token, kick off the push process.
 */
@WebServlet(name = SERVLET_PUSH_PUBS, urlPatterns = "/" + SERVLET_PUSH_PUBS)
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
    private static class ServletCore extends AbstractServletCore {
        private AccessToken accessToken;

        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            super(req, resp);
        }

        private void doGet() throws IOException {
            try {
                accessToken = getCachedAccessToken();
                if (!isAccessTokenPresent()) {
                    redirectIntoThreeLeggedOauthDance();
                } else {
                    requestAsynchronousUpdate();
                    redirectToController(SERVLET_ACKNOWLEDGE);
                }
            } catch (OrcidClientException e) {
                throw new RuntimeException(e);
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
                                callback, callback, callback)));
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
    }
}
