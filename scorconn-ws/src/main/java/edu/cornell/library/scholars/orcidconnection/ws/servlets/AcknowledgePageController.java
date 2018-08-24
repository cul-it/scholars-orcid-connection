/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_ACKNOWLEDGE;
import static edu.cornell.library.scholars.orcidconnection.ws.servlets.LandingPageController.SESSION_KEY_FIRST_TIME;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getOrcidRecordPageUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Show either the first-time acknowledgement, or the update acknowledgement, as
 * appropriate.
 */
@WebServlet(urlPatterns = "/" + SERVLET_ACKNOWLEDGE)
public class AcknowledgePageController extends HttpServlet
        implements WebServerConstants {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new ServletCore(req, resp).doGet();
    }

    /** Thread-safe inner class. */
    private static class ServletCore extends AbstractServletCore {
        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            super(req, resp);

        }

        public void doGet() {
            if (req.getSession().getAttribute(SESSION_KEY_FIRST_TIME) != null) {
                showFirstTimeAcknowledgement();
            } else {
                showUpdateAcknowledgement();
            }
        }

        private void showFirstTimeAcknowledgement() {
            try {
                String orcid = getCachedAccessToken().getOrcid();
                new PageRenderer(req, resp) //
                        .setValue("localId", localId) //
                        .setValue("orcidId", orcid)
                        .setValue("orcidIdUrl", getOrcidRecordPageUrl(orcid))
                        .render(TEMPLATE_ACKNOWLEDGE_FIRST_TIME_PAGE);
            } catch (OrcidClientException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void showUpdateAcknowledgement() {
            try {
                String orcid = getCachedAccessToken().getOrcid();
                new PageRenderer(req, resp) //
                        .setValue("localId", localId) //
                        .setValue("orcidId", orcid)
                        .setValue("orcidIdUrl", getOrcidRecordPageUrl(orcid))
                        .render(TEMPLATE_ACKNOWLEDGE_UPDATE_PAGE);
            } catch (OrcidClientException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
