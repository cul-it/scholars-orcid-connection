/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data.mapping;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.CreationTimestamp;

/**
 * A record of notable events in the life of the database.
 * 
 * If a message is more than 10,000 characters, it will be truncated.
 */
@Entity
@Table(name = "LogEntry")
public class LogEntry {
    private static final Log log = LogFactory.getLog(LogEntry.class);

    public enum Category {
        INFO, ERROR, ACCESS, PUSHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "CATEGORY", nullable = false, length = 10)
    private Category category;

    @Column(name = "MESSAGE", nullable = false, length = 10000)
    private String message;

    @CreationTimestamp
    @Column(name = "CREATED")
    private Date created;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message.length() <= 10000) {
            this.message = message;
        } else {
            log.warn("Log message truncated: " + message);
            this.message = message.substring(0, 9996) + " ...";
        }
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }
}
