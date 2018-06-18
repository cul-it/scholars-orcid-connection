/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data.mapping;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * A record of notable events in the life of the database.
 */
@Entity
@Table(name = "LogEntry")
public class LogEntry {
    public enum Severity {DEBUG, INFO, WARN, ERROR};
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "SEVERITY", nullable = false, length = 10)
    private Severity severity;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "CREATED")
    private Date created;

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }
}
