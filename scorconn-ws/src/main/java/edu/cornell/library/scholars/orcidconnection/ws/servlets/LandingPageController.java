/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getOrcidRecordPageUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Show the landing page.
 * 
 * If the request includes a completion URL, save it so we can return to it at
 * the end of the process.
 */
@WebServlet("/LandingPage")
public class LandingPageController extends HttpServlet
        implements WebServerConstants {
    public static final String SESSION_KEY_FIRST_TIME = "firstTimeAccess";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new ServletCore(req, resp).doGet();
    }

    /** Thread-safe inner class. */
    private static class ServletCore extends AbstractServletCore {
        private enum TokenStatus {
            NONE, INVALID, VALID
        }

        private AccessToken accessToken;

        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            super(req, resp);
        }

        public void doGet() throws IOException, ServletException {
            try {
                switch (checkAccessTokenStatus()) {
                case VALID:
                    goDirectlyToPushRequest();
                    break;
                case INVALID:
                    removeInvalidToken();
                    showInvalidTokenPage();
                    break;
                default: // NONE
                    storeFirstTimeFlag();
                    goDirectlyToPushRequest();
                    break;
                }
            } catch (OrcidClientException e) {
                throw new RuntimeException(e);
            }
        }

        private TokenStatus checkAccessTokenStatus()
                throws OrcidClientException {
            accessToken = getCachedAccessToken();
            if (accessToken == NO_TOKEN) {
                return TokenStatus.NONE;
            } else if (actionClient.isAccessTokenValid(accessToken)) {
                return TokenStatus.VALID;
            } else {
                return TokenStatus.INVALID;
            }
        }

        private void removeInvalidToken() throws OrcidClientException {
            tokenCache.removeAccessToken(accessToken);
        }

        private void goDirectlyToPushRequest()
                throws IOException, ServletException {
            req.getServletContext()
                    .getNamedDispatcher(SERVLET_PROCESS_PUSH_REQUEST)
                    .forward(req, resp);
        }

        private void showInvalidTokenPage() throws IOException {
            String orcid = accessToken.getOrcid();
            new PageRenderer(req, resp) //
                    .setValue("localId", localId)
                    .setValue("formActionUrl", SERVLET_PROCESS_PUSH_REQUEST)
                    .setValue("orcidId", orcid)
                    .setValue("orcidIdUrl", getOrcidRecordPageUrl(orcid))
                    .render(TEMPLATE_LANDING_INVALID_TOKEN_PAGE);
        }

        private void storeFirstTimeFlag() {
            req.getSession().setAttribute(SESSION_KEY_FIRST_TIME, true);
        }
    }
}
