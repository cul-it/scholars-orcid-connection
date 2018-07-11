/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

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
 * TODO
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
        resp.setContentType(contentType);
        resp.setCharacterEncoding(charset);
        resp.setStatus(statusCode);

        JtwigModel model = JtwigModel.newModel();
        for (String key : values.keySet()) {
            model.with(key, values.get(key));
        }

        classpathTemplate(templatePath, env).render(model,
                resp.getOutputStream());
    }

}
