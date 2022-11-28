package com.vivas.campaignx.config;

import java.util.*;

import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;

import com.fasterxml.jackson.annotation.*;
import com.vivas.campaignx.entity.*;

public class UserPrinciple extends User implements UserDetails {

    private final Long userId;
    private final Users users;
    private final Collection<GrantedAuthority> grantedAuthorities;
    private final String groupName;

    public UserPrinciple(Users users, Long userId, String username, String password, Collection<GrantedAuthority> authorities, String groupName) {
        super(username, password, authorities);
        this.userId = userId;
        this.users = users;
        this.grantedAuthorities = authorities;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public Users getUsers() {
        return users;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.users.getPassword();
    }

    @Override
    public String getUsername() {
        return this.users.getUsername();
    }

    public Long getUserId() {
        return this.userId;
    }
}
