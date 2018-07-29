/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.scholars.orcidconnection.data.DataLayer;
import edu.cornell.library.scholars.orcidconnection.data.DataLayerException;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;
import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;

/**
 * Respond to a request for status. Tell whether we have heard of the person,
 * and if so what we have done for him.
 */
@WebServlet("/personStatus")
public class PersonStatusApiController extends HttpServlet
        implements WebServerConstants {
    private static final Log log = LogFactory
            .getLog(PersonStatusApiController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String jsonString;

        try {
            jsonString = jsonify(determineStatus(req));
        } catch (Exception e) {
            log.warn("Failed to get PersonStatus.", e);
            jsonString = "";
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(jsonString);
    }

    private Map<String, Object> determineStatus(HttpServletRequest req)
            throws DataLayerException {
        String localId = req.getParameter("localId");
        if (localId == null || localId.isEmpty()) {
            return Collections.emptyMap();
        }

        Person person = DataLayer.instance().findPerson(localId);
        if (person == null) {
            return Collections.emptyMap();
        }

        String orcidId = person.getOrcidId();
        String orcidName = person.getOrcidName();
        String orcidPageUrl = OrcidClientContext.getInstance().getApiPublicUrl()
                + orcidId;

        List<Work> works = DataLayer.instance().findWorks(orcidId);
        int count = works.size();
        long lastUpdated = figureMostRecent(works);

        return assembleStatus(orcidId, orcidName, orcidPageUrl, count,
                lastUpdated);
    }

    private String jsonify(Map<String, Object> statusMap)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(statusMap);
    }

    private long figureMostRecent(List<Work> works) {
        long mostRecent = 0L;
        for (Work work : works) {
            long thisDate = work.getTimeStamp().getTime();
            mostRecent = Math.max(mostRecent, thisDate);
        }
        return mostRecent;
    }

    private Map<String, Object> assembleStatus(String orcidId, String orcidName,
            String orcidPageUrl, int count, long lastUpdated) {
        Map<String, Object> map = new HashMap<>();
        map.put("orcid_id", orcidId);
        map.put("orcid_name", orcidName);
        map.put("orcid_page_url", orcidPageUrl);
        map.put("publications_pushed", count);
        map.put("last_update", lastUpdated);
        return map;
    }
}
