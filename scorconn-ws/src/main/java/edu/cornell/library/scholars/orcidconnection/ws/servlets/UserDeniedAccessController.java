/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_USER_DENIED;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Show the startup status.
 */
@WebServlet(name = SERVLET_USER_DENIED, urlPatterns = "/" + SERVLET_USER_DENIED)
public class UserDeniedAccessController extends HttpServlet
        implements WebServerConstants {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new PageRenderer(req, resp) //
                .render(TEMPLATE_USER_DENIED_ACCESS_PAGE);
    }

}
