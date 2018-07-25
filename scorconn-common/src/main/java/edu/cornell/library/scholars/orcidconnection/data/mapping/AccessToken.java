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
 */
@Entity
@Table(name = "AccessToken")
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @UpdateTimestamp
    @Column(name = "TIMESTAMP")
    private Date timeStamp;

    public AccessToken() {
        // Required by Hibernate.
    }

    public AccessToken(String orcidId, String scope, String accessToken,
            String tokenType, String refreshToken, long expiresIn,
            String json) {
        this.orcidId = orcidId;
        this.scope = scope;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.json = json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public AccessToken setOrcidId(String orcidId) {
        this.orcidId = orcidId;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public AccessToken setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public AccessToken setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getTokenType() {
        return tokenType;
    }

    public AccessToken setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public AccessToken setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public AccessToken setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getJson() {
        return json;
    }

    public AccessToken setJson(String json) {
        this.json = json;
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
                "AccessToken[id=%s, orcidId=%s, scope=%s, accessToken=%s, "
                        + "tokenType=%s, refreshToken=%s, expiresIn=%s, "
                        + "json=%s, timeStamp=%s]",
                id, orcidId, scope, accessToken, tokenType, refreshToken,
                expiresIn, json, timeStamp);
    }

}
