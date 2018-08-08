/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

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
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * If satisfactory, redirect to ProcessPushRequest If declined, show declined
 * page, with option to go to completion. Otherwise, show error page.
 */
@WebServlet(urlPatterns = "/OrcidCallback")
public class OrcidAuthCallbackController extends HttpServlet
        implements WebServerConstants {
    public static final String ATTRIBUTE_BOGUS_CACHE = "BogusCache";
    private static final Log log = LogFactory
            .getLog(OrcidAuthCallbackController.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            OrcidAuthorizationClient auth = getAuthorizationClient(req);
            auth.processAuthorizationResponse(new ParameterMap(req));

            OauthProgress progress = auth
                    .getProgressById(req.getParameter("state"));

            if (progress.getState() == State.SUCCESS) {
                log.info(String.format("User %s granted authorization.",
                        getLocalId(req)));
                RequestDispatcher dispatcher = req.getServletContext()
                        .getNamedDispatcher(SERVLET_PROCESS_PUSH_REQUEST);
                dispatcher.forward(req, resp);
            } else if (progress.getState() == State.DENIED) {
                log.info("User " + getLocalId(req) + " denied authorization.");
                new PageRenderer(req, resp) //
                        .render(TEMPLATE_USER_DENIED_ACCESS_PAGE);
            } else {
                log.error("OAuth dance failed. Progress is: " + progress);
                new PageRenderer(req, resp) //
                        .setValue("message",
                                "ORCID Authentication process failed.")
                        .render(TEMPLATE_ERROR_PAGE);
            }
        } catch (OrcidClientException e) {
            throw new ServletException(e);
        }
    }
}
