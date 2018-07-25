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
import org.hibernate.annotations.UpdateTimestamp;

/**
 * A record of notable events in the life of the database.
 * 
 * If a message is more than 10,000 characters, it will be truncated.
 */
@Entity
@Table(name = "LogEntry")
public class LogEntry {
    private static final Log log = LogFactory.getLog(LogEntry.class);

    public enum Table {
        NONE, ACCESS_TOKEN, PERSON, WORK
    }

    public enum Category {
        INFO, ERROR, ADD, DELETE, UPDATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "TABLE_", nullable = false)
    private Table table;

    @Column(name = "CATEGORY", nullable = false)
    private Category category;

    @Column(name = "MESSAGE", nullable = false, length = 10000)
    private String message;

    @UpdateTimestamp
    @Column(name = "TIMESTAMP")
    private Date timeStamp;

    public LogEntry() {
        // Required by Hibernate.
    }

    public LogEntry(Table table, Category category, String message) {
        super();
        this.table = table;
        this.category = category;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public LogEntry setTable(Table table) {
        this.table = table;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public LogEntry setCategory(Category category) {
        this.category = category;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogEntry setMessage(String message) {
        if (message.length() <= 10000) {
            this.message = message;
        } else {
            log.warn("Log message truncated: " + message);
            this.message = message.substring(0, 9996) + " ...";
        }
        return this;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return String.format(
                "LogEntry[id=%s, table=%s, category=%s, message=%s, timeStamp=%s]",
                id, table, category, message, timeStamp);
    }

}
