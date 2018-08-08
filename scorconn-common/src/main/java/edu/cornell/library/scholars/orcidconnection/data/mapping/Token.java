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
@Table(name = "Token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // Should be a foreign key Person.OrcidId
    @Column(name = "ORCID_ID", nullable = false, length = 19)
    private String orcidId;

    @Column(name = "ORCID_NAME", nullable = false)
    private String orcidName;

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

    public Token() {
        // Required by Hibernate.
    }

    public Token(String orcidId, String orcidName, String scope,
            String accessToken, String tokenType, String refreshToken,
            long expiresIn, String json) {
        this.orcidId = orcidId;
        this.orcidName = orcidName;
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

    public Token setOrcidId(String orcidId) {
        this.orcidId = orcidId;
        return this;
    }

    public String getOrcidName() {
        return orcidName;
    }

    public Token setOrcidName(String orcidName) {
        this.orcidName = orcidName;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public Token setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Token setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Token setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Token setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public Token setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getJson() {
        return json;
    }

    public Token setJson(String json) {
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
                "AccessToken[id=%s, orcidId=%s, orcidName=%s, scope=%s, "
                        + "accessToken=%s, tokenType=%s, refreshToken=%s, "
                        + "expiresIn=%s, json=%s, timeStamp=%s]",
                id, orcidId, orcidName, scope, accessToken, tokenType,
                refreshToken, expiresIn, json, timeStamp);
    }

}
