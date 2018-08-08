package edu.cornell.library.scholars.orcidconnection.ws;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AccessTokenCache;
import edu.cornell.library.orcidclient.auth.OauthProgress;
import edu.cornell.library.orcidclient.auth.OauthProgressCache;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.context.OrcidClientContextImpl;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper;
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
     * Load the startup parameters.
     * 
     * Initialize the persistence cache, and test it.
     * 
     * Initialize the ORCID context and test the API connections.
     * 
     * Initialize the Scholars link and test it.
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
            StartupStatus
                    .addError("Failed to initialize the runtime properties", e);
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
        try {
            OrcidClientContext.initialize(
                    new OrcidClientContextImpl(RuntimeProperties.getMap()));
            OrcidClientContext occ = OrcidClientContext.getInstance();
            log.info("Context is: " + occ);

            HttpWrapper httpWrapper = new BaseHttpWrapper();
            new OrcidAuthorizationClient(occ, new NullProgressCache(),
                    new NullTokenCache(), httpWrapper).checkConnection();
            new OrcidActionClient(occ, httpWrapper).checkConnection();
        } catch (OrcidClientException e) {
            StartupStatus.addError("Failed to initialize OrcidClientContent",
                    e);
        }
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

    // ----------------------------------------------------------------------
    // Helper classes
    // ----------------------------------------------------------------------

    private static class NullProgressCache implements OauthProgressCache {
        @Override
        public OauthProgress getByID(String arg0) throws OrcidClientException {
            return null;
        }

        @Override
        public OauthProgress getByScope(ApiScope scope)
                throws OrcidClientException {
            return null;
        }

        @Override
        public void store(OauthProgress progress) throws OrcidClientException {
            // Nothing to do.
        }

    }

    private static class NullTokenCache implements AccessTokenCache {
        @Override
        public void addAccessToken(AccessToken token)
                throws OrcidClientException {
            // Nothing to do.
        }

        @Override
        public AccessToken getToken(ApiScope scope)
                throws OrcidClientException {
            return null;
        }

    }

}