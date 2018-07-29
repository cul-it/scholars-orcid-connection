/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_FAKE_LOGIN_PAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.filters.CheckAuthFilter.ATTRIBUTE_ERROR_MESSAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.filters.CheckAuthFilter.PARAMETER_TARGET_URL;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.PageRenderer;

/**
 * Display a fake login page, in case we are configured without CUWebAuth.
 */
@WebServlet(name = SERVLET_FAKE_LOGIN_PAGE, urlPatterns = "/fakeLogin")
public class FakeLoginPageController extends HttpServlet
        implements WebServerConstants {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new PageRenderer(req, resp)
                .setValue(PARAMETER_TARGET_URL, figureTargetUrl(req))
                .setValue("message", req.getAttribute(ATTRIBUTE_ERROR_MESSAGE))
                .render(TEMPLATE_FAKE_LOGIN_PAGE);
    }

    private Object figureTargetUrl(HttpServletRequest req) {
        String url = "";

        String path = req.getRequestURI();
        if (path != null) {
            url = path;
        }

        String query = req.getQueryString();
        if (!StringUtils.isEmpty(query)) {
            url = url + "?" + query;
        }

        return url;
    }

}
