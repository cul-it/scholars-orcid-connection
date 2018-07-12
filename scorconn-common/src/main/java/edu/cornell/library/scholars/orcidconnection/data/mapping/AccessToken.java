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
 * TODO
 */
@Entity
@Table(name = "AccessToken")
public class AccessToken {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    // Should be a foreign key Person.OrcidId
    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;

    @Column(name = "SCOPE", nullable = false, length = 50)
    private String scope;
    
    @Column(name = "ACCESS_TOKEN", nullable = false, length = 50)
    private String accessToken;
    
    @Column(name = "TOKEN_TYPE", nullable = false, length = 10)
    private String tokenType;
    
    @Column(name = "REFRESH_TOKEN", nullable = false, length = 50)
    private String refreshToken;
    
    @Column(name = "EXPIRES_IN", nullable = false)
    private long expiresIn;
    
    @Column(name = "JSON", nullable = false, length = 500)
    private String json;
    
    @CreationTimestamp
    @Column(name = "CREATED")
    private Date created;

    public int getId() {
        return id;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Date getCreated() {
        return created;
    }
    
}
