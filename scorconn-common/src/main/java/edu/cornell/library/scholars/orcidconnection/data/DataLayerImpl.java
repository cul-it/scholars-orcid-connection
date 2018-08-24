/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Category.ADD;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Category.DELETE;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Table.PERSON;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Table.TOKEN;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Table.WORK;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Token;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;

/**
 * Methods that wrap all interaction with the persistence layer.
 * 
 * Every add or delete includes a log entry. Finds do not.
 */
public class DataLayerImpl extends DataLayer {

    @Override
    public void checkConnection() throws DataLayerException {
        writeToDatabase(s -> {
            // do nothing
        });
    }

    @Override
    public void writeAccessToken(Token accessToken) throws DataLayerException {
        deleteThese(TOKEN, findAccessTokens(accessToken.getOrcidId(),
                accessToken.getScope()));
        addThis(TOKEN, accessToken);
    }

    @Override
    public Token findAccessToken(String orcidId, String scope)
            throws DataLayerException {
        return justOne(findAccessTokens(orcidId, scope));
    }

    private List<Token> findAccessTokens(String orcidId, String scope)
            throws DataLayerException {
        return readFromDatabase(s -> {
            String hql = "FROM Token WHERE orcidId = :orcidId AND scope = :scope";
            Query<Token> query = s.createQuery(hql, Token.class);
            query.setParameter("orcidId", orcidId);
            query.setParameter("scope", scope);
            return query;
        });
    }

    @Override
    public void deleteAccessToken(Token accessToken) throws DataLayerException {
        deleteThese(TOKEN, findAccessTokens(accessToken.getOrcidId(),
                accessToken.getScope()));
    }

    @Override
    public void writePerson(Person person) throws DataLayerException {
        deleteThese(PERSON, findPersons(person.getLocalId()));
        addThis(PERSON, person);
    }

    @Override
    public Person findPerson(String localId) throws DataLayerException {
        return justOne(findPersons(localId));
    }

    private List<Person> findPersons(String localId) throws DataLayerException {
        return readFromDatabase(s -> {
            String hql = "FROM Person WHERE localId = :localId";
            Query<Person> query = s.createQuery(hql, Person.class);
            query.setParameter("localId", localId);
            return query;
        });
    }

    @Override
    public void writeWork(Work work) throws DataLayerException {
        deleteThese(WORK, findWorks(work.getScholarsUri(), work.getOrcidId()));
        addThis(WORK, work);
    }

    @Override
    public Work findWork(String scholarsUri, String orcidId)
            throws DataLayerException {
        return justOne(findWorks(scholarsUri, orcidId));
    }

    private List<Work> findWorks(String scholarsUri, String orcidId)
            throws DataLayerException {
        return readFromDatabase(s -> {
            String hql = "FROM Work WHERE scholarsUri = :scholarsUri AND orcidId = :orcidId";
            Query<Work> query = s.createQuery(hql, Work.class);
            query.setParameter("scholarsUri", scholarsUri);
            query.setParameter("orcidId", orcidId);
            return query;
        });
    }

    @Override
    public List<Work> findWorks(String orcidId) throws DataLayerException {
        return readFromDatabase(s -> {
            String hql = "FROM Work WHERE orcidId = :orcidId";
            Query<Work> query = s.createQuery(hql, Work.class);
            query.setParameter("orcidId", orcidId);
            return query;
        });
    }

    @Override
    public void deleteWork(String scholarsUri, String orcidId)
            throws DataLayerException {
        deleteThese(WORK, findWorks(scholarsUri, orcidId));
    }

    @Override
    public void writeLogEntry(LogEntry logEntry) throws DataLayerException {
        writeToDatabase(s -> {
            s.save(logEntry);
        });
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    private void writeToDatabase(HibernateWriter hw) throws DataLayerException {
        SessionFactory factory = HibernateSessionFactory.getSessionFactory();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            hw.operate(session);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            throw new DataLayerException(e);
        }
    }

    private <T> List<T> readFromDatabase(QueryBuilder<T> qb)
            throws DataLayerException {
        SessionFactory factory = HibernateSessionFactory.getSessionFactory();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query<T> query = qb.assembleQuery(session);

            List<T> results = query.list();
            tx.commit();
            return results;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DataLayerException(e);
        }
    }

    private void addThis(LogEntry.Table table, Object thing)
            throws DataLayerException {
        writeToDatabase(s -> {
            s.save(thing);
            writeLogEntry(new LogEntry(table, ADD, thing.toString()));
        });
    }

    private void deleteThese(LogEntry.Table table, List<?> things)
            throws DataLayerException {
        writeToDatabase(s -> {
            for (Object thing : things) {
                s.remove(thing);
                writeLogEntry(new LogEntry(table, DELETE, thing.toString()));
            }
        });
    }

    private <T> T justOne(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    // ----------------------------------------------------------------------
    // Helper classes
    // ----------------------------------------------------------------------

    interface HibernateWriter {
        void operate(Session session)
                throws HibernateException, DataLayerException;
    }

    interface QueryBuilder<T> {
        Query<T> assembleQuery(Session session) throws HibernateException;
    }
}
