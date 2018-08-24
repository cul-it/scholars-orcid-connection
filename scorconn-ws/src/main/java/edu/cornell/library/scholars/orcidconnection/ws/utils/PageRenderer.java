/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import static edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLink.PROPERTY_SCHOLARS_BASE_URL;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getExitUrl;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;
import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

/**
 * Render a given template, using optional key/value pairs and an optional
 * status code (defaults to 200).
 * 
 * Also make these values available to the template: a link to the main page of
 * scholars, and a link to the profile page that we started from.
 */
public class PageRenderer {
    private static final EnvironmentConfiguration env = EnvironmentConfigurationBuilder
            .configuration().escape().withInitialEngine("html").and().build();

    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    private String contentType = "text/html";
    private String charset = "UTF-8";
    private int statusCode = 200;
    private Map<String, Object> values = new HashMap<>();

    public PageRenderer(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    public PageRenderer setStatusCode(int code) {
        this.statusCode = code;
        return this;
    }

    public PageRenderer setValue(String key, Object value) {
        this.values.put(key, value);
        return this;
    }

    public void render(String templatePath) throws IOException {
        String scholarsLink = RuntimeProperties
                .getValue(PROPERTY_SCHOLARS_BASE_URL);
        String exitLink = getExitUrl(getLocalId(req));

        resp.setContentType(contentType);
        resp.setCharacterEncoding(charset);
        resp.setStatus(statusCode);

        JtwigModel model = JtwigModel.newModel() //
                .with("scholarsLink", scholarsLink) //
                .with("exitLink", exitLink);
        for (String key : values.keySet()) {
            model.with(key, values.get(key));
        }

        classpathTemplate(templatePath, env).render(model,
                resp.getOutputStream());
    }

}
