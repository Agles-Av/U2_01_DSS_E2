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
import java.util.regex.Pattern;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // Expresión regular para validar el correo electrónico
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Expresión regular para validar solo letras sin caracteres especiales ni espacios
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+$");

    // Expresión regular para validar solo números en el teléfono
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$"); // Mínimo 10 y máximo 15 dígitos

    private static final Pattern LASTNAME_PATTERN = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$");

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
    public ResponseEntity<ApiResponse> createUser(UserModel user) {
        // Validaciones antes de guardar
        String validationMessage = validateUser(user);
        if (validationMessage != null) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, validationMessage), HttpStatus.BAD_REQUEST);
        }

        Optional<UserModel> existingUser = userRepository.findByCorreo(user.getCorreo());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario Existente"), HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(new ApiResponse(userRepository.save(user), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> updateUser(Long id, UserModel user){
        Optional<UserModel> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        }

        // Validaciones antes de actualizar
        String validationMessage = validateUser(user);
        if (validationMessage != null) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, validationMessage), HttpStatus.BAD_REQUEST);
        }

        UserModel elUser = existingUser.get();
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


    /**
     * Método para validar los datos del usuario antes de crear o actualizar.
     * @param user Objeto usuario a validar
     * @return Mensaje de error si hay alguna validación incorrecta, o null si todo es válido
     */
    private String validateUser(UserModel user) {
        if (user.getCorreo() == null || !EMAIL_PATTERN.matcher(user.getCorreo()).matches()) {
            return "Correo electrónico inválido.";
        }

        if (user.getNombre() == null || !NAME_PATTERN.matcher(user.getNombre()).matches() || user.getNombre().length() > 50) {
            return "El nombre solo puede contener letras y no debe superar los 50 caracteres.";
        }

        if (user.getApellido() == null || !LASTNAME_PATTERN.matcher(user.getApellido()).matches() || user.getApellido().length() > 100) {
            return "El apellido solo puede contener letras y espacios, y no debe superar los 100 caracteres.";
        }

        if (user.getTelefono() == null || !PHONE_PATTERN.matcher(user.getTelefono()).matches()) {
            return "El teléfono debe contener solo números y tener entre 10 y 15 dígitos.";
        }

        if (user.getEdad() == null || user.getEdad() < 0 || user.getEdad() > 199) {
            return "La edad debe estar entre 0 y 199 años.";
        }

        if (user.getPassword() == null || user.getPassword().length() < 8 || user.getPassword().length() > 20) {
            return "La contraseña debe tener entre 8 y 20 caracteres.";
        }

        return null; // Todo está bien
    }
}
