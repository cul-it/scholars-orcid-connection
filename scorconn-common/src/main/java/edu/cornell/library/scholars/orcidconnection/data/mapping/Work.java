/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data.mapping;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * TODO
 * 
 */
@Entity
@Table(name = "Work")
public class Work {
    @Id
    @Column(name = "SCHOLARS_URI", unique = true, nullable = false, length = 500)
    private String scholarsUri;

    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;
 
    @Column(name = "PUT_CODE", nullable = false)
    private String putCode;
    
    @CreationTimestamp
    @Column(name = "CREATED")
    private Date created;
    
    public String getScholarsUri() {
        return scholarsUri;
    }

    public void setScholarsUri(String scholarsUri) {
        this.scholarsUri = scholarsUri;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public Date getCreated() {
        return created;
    }

}
