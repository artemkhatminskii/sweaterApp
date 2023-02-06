package com.example.sweaterApp.Security;

import com.example.sweaterApp.Models.Usr;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UsrDetails implements UserDetails {
    private final Usr usr;

    public UsrDetails(Usr usr) {
        this.usr = usr;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(usr.getRole()));
    }

    @Override
    public String getPassword() {
        return this.usr.getPassword();
    }

    @Override
    public String getUsername() {
        return this.usr.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Usr getUsr() {
        return this.usr;
    }
}
