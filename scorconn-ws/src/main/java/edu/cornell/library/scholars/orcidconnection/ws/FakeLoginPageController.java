/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws;

import static edu.cornell.library.scholars.orcidconnection.ws.CheckAuthFilter.PARAMETER_TARGET_URL;
import static edu.cornell.library.scholars.orcidconnection.ws.CheckAuthFilter.SERVLET_FAKE_LOGIN_PAGE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jtwig.JtwigModel;

/**
 * Display a fake login page, in case we are configured without CUWebAuth.
 */
@WebServlet(name = SERVLET_FAKE_LOGIN_PAGE, urlPatterns = "/fakeLogin")
public class FakeLoginPageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new PageRenderer(req, resp).render("/templates/fakeLoginPage.twig.html",
                JtwigModel.newModel() //
                        .with(PARAMETER_TARGET_URL, figureTargetUrl(req)));
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
