/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

/**
 * TODO
 */
public class PageRenderer {
    private static final EnvironmentConfiguration env = EnvironmentConfigurationBuilder
            .configuration().escape().withInitialEngine("html").and().build();

    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    public PageRenderer(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    public void render(String templatePath, JtwigModel model)
            throws IOException {
        classpathTemplate(templatePath, env).render(model,
                resp.getOutputStream());
    }

}
