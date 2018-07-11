/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.auth.AccessToken;

/**
 * The user has said that they are ready to push the publications.
 * 
 * Do we have an access token? If not, get one.
 * 
 * Is the access token still valid? If not, may we get a new one?
 * 
 * If we have a valid access token, kick off the push process.
 */
@WebServlet(urlPatterns = "/ProcessPushRequest")
public class ProcessPushRequestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        new ServletCore(req, resp).doGet();
    }

    /**
     * Do the work in this inner class, so we can use instance variables and
     * still be thread-safe.
     */
    private static class ServletCore {
        private final HttpServletRequest req;
        private final HttpServletResponse resp;
        private AccessToken accessToken;

        public ServletCore(HttpServletRequest req, HttpServletResponse resp) {
            this.req = req;
            this.resp = resp;
        }

        private void doGet() {
            getAccessTokenFromCache();
            if (!isAccessTokenPresent()) {
                redirectIntoThreeLeggedOauthDance();
            } else if (!isAccessTokenStillValid()) {
                recordAccessTokenNotValid();
                showInvalidTokenPage();
            } else {
                requestAsynchronousUpdate();
                showAcknowledgementPage();
            }
        }

        /**
         * <pre>
         * AuthorizationStateProgressCache.getByScope() -- NEED TO DEVELOP A CACHE
         * </pre>
         */
        private AccessToken getAccessTokenFromCache() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ProcessPushRequestController.getAccessTokenFromCache() not implemented.");

        }

        /**
         * @return
         */
        private boolean isAccessTokenPresent() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ServletCore.isAccessTokenPresent() not implemented.");

        }

        /**
         * @return
         */
        private boolean isAccessTokenStillValid() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ProcessPushRequestController.isAccessTokenStillValid() not implemented.");

        }

        /**
         * 
         */
        private void recordAccessTokenNotValid() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ServletCore.recordAccessTokenNotValid() not implemented.");

        }

        private void showInvalidTokenPage() {
            throw new RuntimeException(
                    "ProcessPushRequestController.ServletCore.showInvalidTokenPage not implemented.");
        }

        /**
         * <pre>
         * return new OrcidAuthorizationClient(occ, WebappCache.getCache(),
         *         new BaseHttpWrapper());
         * 
         * AuthorizationStateProgress progress = authClient
         *         .createProgressObject(scope, callbackUrl(), callbackUrl());
         * 
         * resp.sendRedirect(authClient.buildAuthorizationCall(progress));
         * </pre>
         */
        private void redirectIntoThreeLeggedOauthDance() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ProcessPushRequestController.redirectIntoThreeLeggedOauthDance() not implemented.");

        }

        /**
         * <pre>
         * </pre>
         */
        private void requestAsynchronousUpdate() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ProcessPushRequestController.requestAsynchronousUpdate() not implemented.");

        }

        /**
         * <pre>
         * // new PageRenderer(req,
         * // resp).render("/templates/landingPage.twig.html",
         * // JtwigModel.newModel().with("localId", getLocalId(req)));
         * </pre>
         */
        private void showAcknowledgementPage() {
            // TODO Auto-generated method stub
            throw new RuntimeException(
                    "ProcessPushRequestController.showAcknowledgementPage() not implemented.");

        }
    }
}
