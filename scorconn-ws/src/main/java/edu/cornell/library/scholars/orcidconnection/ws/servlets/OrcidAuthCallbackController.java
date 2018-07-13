/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.SERVLET_PROCESS_PUSH_REQUEST;
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

import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.State;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.util.ParameterMap;
import edu.cornell.library.scholars.orcidconnection.accesstokens.BogusCache;
import edu.cornell.library.scholars.orcidconnection.data.DbLogger;
import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Severity;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * TODO
 * 
 * If satisfactory, redirect to ProcessPushRequest If declined, show declined
 * page, with option to go to completion. Otherwise, show error page.
 */
@WebServlet(urlPatterns = "/OrcidCallback")
public class OrcidAuthCallbackController extends HttpServlet {
    public static final String ATTRIBUTE_BOGUS_CACHE = "BogusCache";
    private static final Log log = LogFactory
            .getLog(OrcidAuthCallbackController.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            OrcidAuthorizationClient auth = getAuthorizationClient(
                    BogusCache.getCache(req, getLocalId(req)));
            auth.processAuthorizationResponse(new ParameterMap(req));

            AuthorizationStateProgress progress = auth
                    .getProgressById(req.getParameter("state"));

            if (progress.getState() == State.SUCCESS) {
                DbLogger.writeLogEntry(Severity.INFO,
                        "User %s granted authorization: %s", getLocalId(req),
                        progress.getAccessToken());
                RequestDispatcher dispatcher = req.getServletContext()
                        .getNamedDispatcher(SERVLET_PROCESS_PUSH_REQUEST);
                dispatcher.forward(req, resp);
            } else if (progress.getState() == State.DENIED) {
                log.info("User " + getLocalId(req) + " denied authorization.");
                new PageRenderer(req, resp) //
                        .render("/templates/userDeniedAccessPage.twig.html");
            } else {
                log.error("OAuth dance failed. Progress is: " + progress);
                new PageRenderer(req, resp) //
                        .setValue("message",
                                "ORCID Authentication process failed.")
                        .render("/templates/errorPage.twig.html");
            }
        } catch (OrcidClientException e) {
            throw new ServletException(e);
        }
    }
}
