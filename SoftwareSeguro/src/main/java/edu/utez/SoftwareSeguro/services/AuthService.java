package edu.utez.SoftwareSeguro.services;

import edu.utez.SoftwareSeguro.config.ApiResponse;
import edu.utez.SoftwareSeguro.controllers.auth.dto.SignDto;
import edu.utez.SoftwareSeguro.controllers.auth.dto.SignedDto;
import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.security.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    private final UserService service;
    private final AuthenticationManager manager;
    private final JwtProvider provider;

    public AuthService(UserService service, AuthenticationManager manager, JwtProvider provider) {
        this.service = service;
        this.manager = manager;
        this.provider = provider;
    }

    public ResponseEntity<ApiResponse> signIn(SignDto dto){
        try {
            Optional<UserModel> foundUser = service.findByEmail(dto.getCorreo());
            if (foundUser.isEmpty())
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
            UserModel user = foundUser.get();
            System.out.println("Email: " + dto.getCorreo() + " Password: " + dto.getPassword());
            Authentication auth = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getPassword())
            );
            System.out.println("banana");
            System.out.println(dto);
            System.out.println(dto.getCorreo());
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = provider.generateToken(auth);
            SignedDto signedDto = new SignedDto(token, "Bearer", user);
            return new ResponseEntity<>(
                    new ApiResponse(signedDto, HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            String message = "CredentialsMismatch";
            if (e instanceof DisabledException)
                message = "UserDisabled";
            return new ResponseEntity<>(
                    new ApiResponse(HttpStatus.BAD_REQUEST, true, message),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
