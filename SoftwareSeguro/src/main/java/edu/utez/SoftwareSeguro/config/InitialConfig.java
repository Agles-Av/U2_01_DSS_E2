package edu.utez.SoftwareSeguro.config;

import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.models.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Configuration
public class InitialConfig implements CommandLineRunner {

    private final UserRepository UserRepository;
    private final PasswordEncoder encoder;

    public InitialConfig(edu.utez.SoftwareSeguro.models.UserRepository userRepository, PasswordEncoder encoder) {
        UserRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional(rollbackFor = {SQLException.class})
    public void run(String... args) throws Exception {
        UserModel user = getOrSaveUser(new UserModel("Víctor","Barrera Ocampo",
                "cafatofo@gmail.com", "7779876544", encoder.encode("contra123"), 20));
    }


    @Transactional
    public UserModel getOrSaveUser(UserModel user) {
        Optional<UserModel> foundUser = UserRepository.findByCorreo(user.getCorreo());
        return foundUser.orElseGet(() -> UserRepository.saveAndFlush(user));
    }
}
