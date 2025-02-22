package edu.utez.SoftwareSeguro.security.entity;

import edu.utez.SoftwareSeguro.models.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private String correo;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String correo, String password, Collection<? extends GrantedAuthority> authorities) {
        this.correo = correo;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetails build (UserModel user) {
        return new UserDetailsImpl(
                user.getCorreo(), user.getPassword(),
                Set.of(new SimpleGrantedAuthority("USER")) // Asignar un permiso genérico
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return correo;
    }
}
