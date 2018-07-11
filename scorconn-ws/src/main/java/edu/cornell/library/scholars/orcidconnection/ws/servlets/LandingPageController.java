/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.setCompletionUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Show the landing page.
 * 
 * If the request includes a completion URL, save it so we can return to it at
 * the end of the process.
 */
@WebServlet(urlPatterns = "/")
public class LandingPageController extends HttpServlet {

    private static final String PARAMETER_COMPLETION_URL = "completionUrl";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String completionUrl = req.getParameter(PARAMETER_COMPLETION_URL);
        if (StringUtils.isNotEmpty(completionUrl)) {
            setCompletionUrl(req, completionUrl);
        }

        new PageRenderer(req, resp) //
                .setValue("localId", getLocalId(req))
                .render("/templates/landingPage.twig.html");
    }

}
