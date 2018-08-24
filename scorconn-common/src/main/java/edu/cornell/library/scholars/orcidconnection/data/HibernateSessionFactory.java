/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

/**
 * A holder for a singleton session factory.
 */
public abstract class HibernateSessionFactory {
    private static final Log log = LogFactory
            .getLog(HibernateSessionFactory.class);

    // ----------------------------------------------------------------------
    // The factory
    // ----------------------------------------------------------------------

    private static volatile HibernateSessionFactory instance = new HibernateSessionFactoryNotInitialized();

    public static synchronized void initialize(
            HibernateSessionFactory newInstance) {
        if (instance == null
                || instance instanceof HibernateSessionFactoryNotInitialized) {
            instance = newInstance;
            log.debug("initialized: " + instance);
        } else {
            throw new IllegalStateException("Already initialized: " + instance);
        }
    }

    public static SessionFactory getSessionFactory() {
        return instance.getFactory();
    }

    public static void shutdown() {
        instance.closeFactory();
    }

    // ----------------------------------------------------------------------
    // The interface
    // ----------------------------------------------------------------------

    public abstract SessionFactory getFactory();

    public abstract void closeFactory();

    // ----------------------------------------------------------------------
    // The empty implementation
    // ----------------------------------------------------------------------

    private static class HibernateSessionFactoryNotInitialized
            extends HibernateSessionFactory {
        private static final String MESSAGE = "HibernateSessionFactory has not been initialized";

        @Override
        public SessionFactory getFactory() {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void closeFactory() {
            throw new IllegalStateException(MESSAGE);
        }
    }

}
