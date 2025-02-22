package edu.utez.SoftwareSeguro.services;

import edu.utez.SoftwareSeguro.config.ApiResponse;
import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.models.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByCorreo(email);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findById(Long id) {
        Optional<UserModel> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(user.get(), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAll(){
        return new ResponseEntity<>(new ApiResponse(userRepository.findAll(), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> createUser(UserModel user){
        Optional<UserModel> existinUser = userRepository.findByCorreo(user.getCorreo());
        if (existinUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario Existente"), HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(new ApiResponse(userRepository.save(user), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> updateUser(Long id, UserModel user){
        Optional<UserModel> existinUser = userRepository.findById(id);
        if (existinUser.isEmpty())
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        UserModel elUser = existinUser.get();
        elUser.setCorreo(user.getCorreo());
        elUser.setNombre(user.getNombre());
        elUser.setApellido(user.getApellido());
        elUser.setEdad(user.getEdad());
        elUser.setTelefono(user.getTelefono());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        elUser.setPassword(encoder.encode(user.getPassword()));

        return new ResponseEntity<>(new ApiResponse(userRepository.save(elUser), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> deleteUser(Long id){
        Optional<UserModel> existinUser = userRepository.findById(id);
        if (existinUser.isEmpty())
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        userRepository.deleteById(id);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, HttpStatus.OK), HttpStatus.OK);
    }
}
