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
 
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "LOCAL_ID", nullable = false, length = 10)
    private String localId;
 
    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;
 
    @Column(name = "ORCID_NAME", nullable = false, length = 50)
    private String orcidName;

    @UpdateTimestamp
    @Column(name = "TIMESTAMP")
    private Date timeStamp;

    public Person() {
        // Required by Hibernate.
    }

    public Person(String localId, String orcidId, String orcidName) {
        this.localId = localId;
        this.orcidId = orcidId;
        this.orcidName = orcidName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalId() {
        return localId;
    }

    public Person setLocalId(String localId) {
        this.localId = localId;
        return this;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public Person setOrcidId(String orcidId) {
        this.orcidId = orcidId;
        return this;
    }

    public String getOrcidName() {
        return orcidName;
    }

    public Person setOrcidName(String orcidName) {
        this.orcidName = orcidName;
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
                "Person[id=%s, localId=%s, orcidId=%s, orcidName=%s, timeStamp=%s]",
                id, localId, orcidId, orcidName, timeStamp);
    }

}
