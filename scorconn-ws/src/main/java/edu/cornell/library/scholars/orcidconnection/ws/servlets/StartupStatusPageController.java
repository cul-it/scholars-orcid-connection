/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.SERVLET_STARTUP_STATUS_PAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.TEMPLATE_STARTUP_STATUS_PAGE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;
import edu.cornell.library.scholars.orcidconnection.ws.utils.StartupStatus;

/**
 * Show the startup status.
 */
@WebServlet(name = SERVLET_STARTUP_STATUS_PAGE, urlPatterns = "/startupStatus")
public class StartupStatusPageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new PageRenderer(req, resp).setStatusCode(500)
                .setValue("status", StartupStatus.getInstance())
                .render(TEMPLATE_STARTUP_STATUS_PAGE);
    }

}
