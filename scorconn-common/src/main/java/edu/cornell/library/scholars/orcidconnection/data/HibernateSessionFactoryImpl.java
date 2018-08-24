/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Build a factory, using some properties from hibernate.cfg.xml, and some
 * properties from the runtime properties map.
 * 
 * This code is derived from an example at
 * https://docs.jboss.org/hibernate/orm/5.0/quickstart/html/
 */
public class HibernateSessionFactoryImpl extends HibernateSessionFactory {
    public static final String PROPERTY_CONNECTION_URL = "hibernate.connection.url";
    public static final String PROPERTY_CONNECTION_USERNAME = "hibernate.connection.username";
    public static final String PROPERTY_CONNECTION_PASSWORD = "hibernate.connection.password";

    private final SessionFactory factory;

    public HibernateSessionFactoryImpl(Map<String, String> properties) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .configure();
        applySetting(builder, properties, PROPERTY_CONNECTION_URL);
        applySetting(builder, properties, PROPERTY_CONNECTION_USERNAME);
        applySetting(builder, properties, PROPERTY_CONNECTION_PASSWORD);
        StandardServiceRegistry registry = builder.build();

        try {
            factory = new MetadataSources(registry).buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had
            // trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError(e);
        }
    }

    private void applySetting(StandardServiceRegistryBuilder builder,
            Map<String, String> properties, String key) {
        if (properties.containsKey(key)) {
            builder.applySetting(key, properties.get(key));
        } else {
            throw new IllegalStateException("Runtime properties must include "
                    + "a value for '" + key + "'");
        }
    }

    @Override
    public SessionFactory getFactory() {
        return factory;
    }

    @Override
    public void closeFactory() {
        factory.close();
    }

}
