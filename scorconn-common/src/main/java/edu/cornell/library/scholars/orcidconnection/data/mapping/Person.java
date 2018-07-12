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
 
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "LOCAL_ID", unique = true, nullable = false, length = 10)
    private String localId;
 
    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;
 
    @Column(name = "ORCID_NAME", nullable = false, length = 50)
    private String orcidName;

    @CreationTimestamp
    @Column(name = "CREATED")
    private Date created;
    
    public int getId() {
        return id;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

    public String getOrcidName() {
        return orcidName;
    }

    public void setOrcidName(String orcidName) {
        this.orcidName = orcidName;
    }

    public Date getCreated() {
        return created;
    }

}
