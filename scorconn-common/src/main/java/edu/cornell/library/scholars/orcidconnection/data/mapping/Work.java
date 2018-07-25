/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data.mapping;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

/**
 * TODO
 * 
 */
@Entity
@Table(name = "Work")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "SCHOLARS_URI", nullable = false, length = 500)
    private String scholarsUri;

    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;

    @Column(name = "HASH", nullable = false)
    private int hash;

    @UpdateTimestamp
    @Column(name = "TIMESTAMP")
    private Date timeStamp;

    public Work() {
        // Required by Hibernate.
    }

    public Work(String scholarsUri, String orcidId, int hash) {
        this.scholarsUri = scholarsUri;
        this.orcidId = orcidId;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScholarsUri() {
        return scholarsUri;
    }

    public Work setScholarsUri(String scholarsUri) {
        this.scholarsUri = scholarsUri;
        return this;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public Work setOrcidId(String orcidId) {
        this.orcidId = orcidId;
        return this;
    }

    public int getHash() {
        return hash;
    }

    public Work setHash(int hash) {
        this.hash = hash;
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
                "Work[id=%s, scholarsUri=%s, orcidId=%s, hash=%s, timeStamp=%s]",
                id, scholarsUri, orcidId, hash, timeStamp);
    }

}
