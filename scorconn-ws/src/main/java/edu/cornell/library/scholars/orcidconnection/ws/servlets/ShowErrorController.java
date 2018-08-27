/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Show the startup status.
 */
@WebServlet(name = SERVLET_SHOW_ERROR, urlPatterns = "/" + SERVLET_SHOW_ERROR)
public class ShowErrorController extends HttpServlet
        implements WebServerConstants {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new PageRenderer(req, resp) //
                .setValue("message", getErrorMessage(req))
                .render(TEMPLATE_ERROR_PAGE);
    }

    private String getErrorMessage(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String message = (String) session.getAttribute(ATTRIBUTE_ERROR_MESSAGE);
        session.removeAttribute(ATTRIBUTE_ERROR_MESSAGE);
        return message;
    }

}
