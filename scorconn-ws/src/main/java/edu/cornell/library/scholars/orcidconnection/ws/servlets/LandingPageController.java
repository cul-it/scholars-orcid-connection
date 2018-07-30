/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getOrcidRecordPageUrl;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.setCompletionUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

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

    private static final String PARAMETER_COMPLETION_URL = "completionUrl";

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

        public void doGet() {
            try {
                storeCompletionUrlIfPresent();

                switch (checkAccessTokenStatus()) {
                case VALID:
                    showValidTokenPage();
                    break;
                case INVALID:
                    showInvalidTokenPage();
                    break;
                default: // NONE
                    showNoTokenPage();
                    break;
                }
            } catch (OrcidClientException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void storeCompletionUrlIfPresent() {
            String completionUrl = req.getParameter(PARAMETER_COMPLETION_URL);
            if (StringUtils.isNotEmpty(completionUrl)) {
                setCompletionUrl(req, completionUrl);
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

        private void showNoTokenPage() throws IOException {
            new PageRenderer(req, resp) //
                    .setValue("localId", localId)
                    .setValue("formActionUrl", SERVLET_PROCESS_PUSH_REQUEST)
                    .render(TEMPLATE_LANDING_WITHOUT_TOKEN_PAGE);
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

        private void showValidTokenPage() throws IOException {
            String orcid = accessToken.getOrcid();
            new PageRenderer(req, resp) //
                    .setValue("localId", localId)
                    .setValue("formActionUrl", SERVLET_PROCESS_PUSH_REQUEST)
                    .setValue("orcidId", orcid)
                    .setValue("orcidIdUrl", getOrcidRecordPageUrl(orcid))
                    .render(TEMPLATE_LANDING_WITH_TOKEN_PAGE);
        }
    }
}
