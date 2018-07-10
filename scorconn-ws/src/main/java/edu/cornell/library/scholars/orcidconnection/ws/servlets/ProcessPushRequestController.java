/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 */
@WebServlet(urlPatterns = "/ProcessPushRequest")
public class ProcessPushRequestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // new PageRenderer(req,
        // resp).render("/templates/landingPage.twig.html",
        // JtwigModel.newModel().with("localId", getLocalId(req)));
        /**
         * <pre>
         * If no accessToken for the user, 
         *    get accessToken
         *    write to the log
         * request servlet3 to push for that user
         *    write to the log
         * redirect to the comp[letion URL, or to the default completion page
         * 
         * 
         *      return new OrcidAuthorizationClient(occ, WebappCache.getCache(),
                new BaseHttpWrapper());

        AuthorizationStateProgress progress = authClient
                .createProgressObject(scope, callbackUrl(), callbackUrl());

        resp.sendRedirect(authClient.buildAuthorizationCall(progress));

         * 
         * 
         * </pre>
         */
        throw new RuntimeException(
                "ProcessPushRequestController.doGet not implemented.");

    }

}
