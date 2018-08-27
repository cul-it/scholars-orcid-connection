/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_CALLBACK;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getAuthorizationClient;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.auth.OauthProgress;
import edu.cornell.library.orcidclient.auth.OauthProgress.State;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.util.ParameterMap;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;

/**
 * If satisfactory, redirect to ProcessPushRequest If declined, show declined
 * page, with option to go to completion. Otherwise, show error page.
 */
@WebServlet(name = SERVLET_CALLBACK, urlPatterns = "/" + SERVLET_CALLBACK)
public class OrcidAuthCallbackController extends HttpServlet
        implements WebServerConstants {
    public static final String ATTRIBUTE_BOGUS_CACHE = "BogusCache";
    private static final Log log = LogFactory
            .getLog(OrcidAuthCallbackController.class);

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

        public void doGet() throws IOException, ServletException {
            try {
                OrcidAuthorizationClient auth = getAuthorizationClient(req);
                auth.processAuthorizationResponse(new ParameterMap(req));

                OauthProgress progress = auth
                        .getProgressById(req.getParameter("state"));

                if (progress.getState() == State.SUCCESS) {
                    log.info(String.format("User %s granted authorization.",
                            getLocalId(req)));
                    RequestDispatcher dispatcher = req.getServletContext()
                            .getNamedDispatcher(SERVLET_PUSH_PUBS);
                    dispatcher.forward(req, resp);
                } else if (progress.getState() == State.DENIED) {
                    log.info("User " + getLocalId(req)
                            + " denied authorization.");
                    redirectToController(SERVLET_USER_DENIED);
                } else {
                    log.error("OAuth dance failed. Progress is: " + progress);
                    redirectToErrorPage("ORCID Authentication process failed.");
                }
            } catch (OrcidClientException e) {
                throw new ServletException(e);
            }
        }
    }
}
