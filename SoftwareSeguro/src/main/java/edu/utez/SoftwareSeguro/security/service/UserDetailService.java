package edu.utez.SoftwareSeguro.security.service;

import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.security.entity.UserDetailsImpl;
import edu.utez.SoftwareSeguro.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserDetailService implements UserDetailsService {
    private final UserService service;

    public UserDetailService(UserService service) {
        this.service = service;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> foundUser = service.findByEmail(username);
        if (foundUser.isPresent())
            return UserDetailsImpl.build(foundUser.get());
        throw new UsernameNotFoundException("Usuario no encontrado");
    }
}
