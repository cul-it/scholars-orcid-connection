/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry;

/**
 * Write an entry to the permanent log.
 */
public class DbLogger {
    /**
     * Write to the LogEntry table in the database. Provide a message format and
     * args, as for String.format();
     */
    public static void writeLogEntry(LogEntry.Category category,
            String messageFormat, Object... args) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            LogEntry entry = new LogEntry();
            entry.setCategory(category);
            entry.setMessage(String.format(messageFormat, args));
            session.save(entry);

            session.getTransaction().commit();
        }
    }

    private DbLogger() {
        // No need to instantiate it.
    }
}
