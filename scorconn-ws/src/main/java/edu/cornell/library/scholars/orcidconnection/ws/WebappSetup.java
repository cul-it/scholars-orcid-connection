package edu.cornell.library.scholars.orcidconnection.ws;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.context.OrcidClientContextImpl;
import edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.data.DataLayer;
import edu.cornell.library.scholars.orcidconnection.data.DataLayerException;
import edu.cornell.library.scholars.orcidconnection.data.DataLayerImpl;
import edu.cornell.library.scholars.orcidconnection.data.HibernateUtil;
import edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLink;
import edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLinkImpl;
import edu.cornell.library.scholars.orcidconnection.ws.utils.RuntimeProperties;
import edu.cornell.library.scholars.orcidconnection.ws.utils.RuntimeProperties.RuntimePropertiesException;
import edu.cornell.library.scholars.orcidconnection.ws.utils.StartupStatus;

@WebListener
public class WebappSetup implements ServletContextListener {
    private static final Log log = LogFactory.getLog(WebappSetup.class);

    /**
     * TODO BOGUS
     * 
     * <pre>
     * Load the startup parameters
     *    Look for a system property that points to a properties file, or
     *    Look for a JNDI binding that points to a properties file
     * 
     * Test the database connection
     *    Is this just a Hibernate call?
     *    https://howtodoinjava.com/hibernate/hibernate-3-introduction-and-writing-hello-world-application/
     *    https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-maven-example/
     *    http://hibernate.org/orm/documentation/5.3/
     *    http://hibernate.org/orm/documentation/getting-started/
     *    
     *    How can we configure the SessionFactory from a resource in the webapp?
     * 
     * Test the ORCID connection
     *    What can we test? Even if we try something that we know will fail, we can check
     *      that it fails in the expected way.
     *    This should be built into the driver 
     * 
     * Record all of this in the StartupStatus
     * 
     * Create the StartupStatusFilter that will do nothing if things are OK, return 500 otherwise, with informative text.
     * </pre>
     */

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        initializeRuntimeProperties();
        initializePersistenceCache();
        initializeOrcidContext();
        initializeScholarsLink();
        log.info("WebappSetup complete.");
    }

    private void initializeRuntimeProperties() {
        try {
            RuntimeProperties.initialize();
        } catch (RuntimePropertiesException e) {
            StartupStatus.addError("Failed to initialize the runtime properties", e);
        }
    }

    private void initializePersistenceCache() {
        try {
            DataLayer.initialize(new DataLayerImpl());
            DataLayer.instance().checkConnection();
        } catch (DataLayerException e) {
            StartupStatus.addError("Failed to initialize the data layer", e);
        }
    }
    
    private void initializeOrcidContext() {
        Map<Setting, String> settings = convertToSettings(
                RuntimeProperties.getMap());
        try {
            OrcidClientContext.initialize(new OrcidClientContextImpl(settings));
            log.error("BOGUS -- TEST THE CONTEXT!!!");
            log.info("Context is: " + OrcidClientContext.getInstance());
        } catch (OrcidClientException e) {
            StartupStatus.addError("Failed to initialize OrcidClientContent",
                    e);
        }
    }

    private Map<Setting, String> convertToSettings(
            Map<String, String> strings) {
        Map<Setting, String> settings = new HashMap<>();
        for (Setting setting : Setting.values()) {
            String string = setting.toString();
            if (strings.containsKey(string)) {
                settings.put(setting, strings.get(string));
            }
        }
        return settings;
    }

    private void initializeScholarsLink() {
        try {
            ScholarsLink.initialize(new ScholarsLinkImpl(
                    RuntimeProperties.getMap(), new BaseHttpWrapper()));
            ScholarsLink.instance().checkConnection();
        } catch (Exception e) {
            StartupStatus.addError("Failed to initialize the ScholarsLink", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        HibernateUtil.shutdown();
    }

}