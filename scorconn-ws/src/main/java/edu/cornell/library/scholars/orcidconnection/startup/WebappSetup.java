package edu.cornell.library.scholars.orcidconnection.startup;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.cornell.library.scholars.orcidconnection.data.HibernateUtil;
import edu.cornell.library.scholars.orcidconnection.data.mapping.AccessToken;
import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;

public class WebappSetup implements ServletContextListener {

    /**
     * TODO
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
    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
     * ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long key = new Date().getTime();
            session.save(createPerson(key));
            session.save(createAccessToken(key));
            session.save(createWork(key));
            session.save(createLogEntry(key));

            session.getTransaction().commit();
        }
        //
        // TODO Auto-generated method stub
        System.out.println("\n\n\nBOGUS TO THE MAX\n\n\n");
    }

    private Person createPerson(long key) {
        Person p = new Person();
        p.setLocalId(("L" + key).substring(0, 10));
        p.setOrcidId("orcid" + key);
        p.setOrcidName("name" + key);
        return p;
    }

    private AccessToken createAccessToken(long key) {
        AccessToken at = new AccessToken();
        at.setAccessToken("accessToken" + key);
        at.setExpiresIn(key);
        at.setJson("json" + key);
        at.setOrcidId("orcid" + key);
        at.setRefreshToken("refreshToken" + key);
        at.setScope("scope" + key);
        at.setTokenType(("type" + key).substring(0, 10));
        return at;
    }

    private Work createWork(long key) {
        Work w = new Work();
        w.setOrcidId("orcid" + key);
        w.setScholarsUri("scholarsUri" + key);
        return w;
    }
    
    private LogEntry createLogEntry(long key) {
        LogEntry le = new LogEntry();
        le.setMessage("Startup smoke test: " + key);
        le.setSeverity(LogEntry.Severity.INFO);
        return le;
    }

    private void testHibernateConnection() {
        HibernateUtil.getSessionFactory().openSession()
                .createQuery("FROM Person").list();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        HibernateUtil.shutdown();
    }

}