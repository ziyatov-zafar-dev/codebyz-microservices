package uz.codebyz.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.codebyz.auth.user.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JwtUser implements UserDetails {

    private final UUID userId;
    private final String username;
    private final UserRole role;
    private Integer tokenVersion;

    public JwtUser(UUID userId, String username, UserRole role,Integer tokenVersion) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.tokenVersion = tokenVersion;
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(Integer tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
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
}
