package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_auth", schema = "quora")
@NamedQueries({
        @NamedQuery(name = "userAuthTokenByAccessToken" , query = "select ut from UserAuthTokenEntity ut where ut.access_token = :access_token ")
})
public class UserAuthTokenEntity implements Serializable {


    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Column(name = "ACCESS_TOKEN")
    @NotNull
    private String access_token;

    @Column(name = "EXPIRES_AT")
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(name = "LOGIN_AT")
    private ZonedDateTime login_at;

    @Column(name = "LOGOUT_AT")
    private ZonedDateTime logout_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLogin_at() {
        return login_at;
    }

    public void setLogin_at(ZonedDateTime login_at) {
        this.login_at = login_at;
    }

    public ZonedDateTime getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(ZonedDateTime logout_at) {
        this.logout_at = logout_at;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}

