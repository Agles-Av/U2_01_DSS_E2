package edu.utez.SoftwareSeguro.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utez.SoftwareSeguro.config.ApiResponse;
import edu.utez.SoftwareSeguro.models.Bitacora;
import edu.utez.SoftwareSeguro.models.BitacoraRepository;
import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.models.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final BitacoraRepository bitacoraRepository;

    // Expresión regular para validar el correo electrónico
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Expresión regular para validar solo letras sin caracteres especiales ni espacios
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+$");

    // Expresión regular para validar solo números en el teléfono
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$"); // Mínimo 10 y máximo 15 dígitos

    private static final Pattern LASTNAME_PATTERN = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$");

    public UserService(UserRepository userRepository, BitacoraRepository bitacoraRepository) {
        this.userRepository = userRepository;
        this.bitacoraRepository = bitacoraRepository;
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

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> getAll(String quien){
        // 📌 REGISTRAR EN BITÁCORA
        registrarBitacora(quien, "GET", "usuarios", null, "Consulta todos los usuarios");

        return new ResponseEntity<>(new ApiResponse(userRepository.findAll(), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> createUser(UserModel user, String quien) {
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

        // 📌 REGISTRAR EN BITÁCORA
        registrarBitacora(quien, "POST", "usuarios", null, user);

        return new ResponseEntity<>(new ApiResponse(user, HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> updateUser(Long id, UserModel user, String quien){
        Optional<UserModel> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        }

        String validationMessage = validateUser(user);
        if (validationMessage != null) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, validationMessage), HttpStatus.BAD_REQUEST);
        }

        UserModel elUser = existingUser.get();
        UserModel datosAnteriores = elUser; // Convertimos a String para guardar en bitácora

        elUser.setCorreo(user.getCorreo());
        elUser.setNombre(user.getNombre());
        elUser.setApellido(user.getApellido());
        elUser.setEdad(user.getEdad());
        elUser.setTelefono(user.getTelefono());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        elUser.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(elUser);

        // 📌 REGISTRAR EN BITÁCORA
        registrarBitacora(quien, "PUT", "usuarios", datosAnteriores, elUser);

        return new ResponseEntity<>(new ApiResponse(elUser, HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<ApiResponse> deleteUser(Long id, String quien){
        Optional<UserModel> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "Usuario No Encontrado"), HttpStatus.BAD_REQUEST);
        }
        UserModel elUser = existingUser.get();
        String datosAnteriores = elUser.toString(); // Guardamos los datos antes de eliminar
        userRepository.deleteById(id);
        // 📌 REGISTRAR EN BITÁCORA
        registrarBitacora(quien, "DELETE", "usuarios", elUser, null);
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


    private void registrarBitacora(String usuario, String accion, String tablaAfectada, Object datosAnteriores, Object datosNuevos) {
        ObjectMapper objectMapper = new ObjectMapper();
        String datosAntesStr = null;
        String datosNuevosStr = null;

        try {
            if (datosAnteriores != null) {
                datosAntesStr = objectMapper.writeValueAsString(datosAnteriores);
            }
            if (datosNuevos != null) {
                // Si el objeto es un usuario, eliminamos la contraseña antes de guardarlo en bitácora
                if (datosNuevos instanceof UserModel) {
                    UserModel userCopy = new UserModel();
                    UserModel originalUser = (UserModel) datosNuevos;

                    // Copiar los datos, excepto la contraseña
                    userCopy.setId(originalUser.getId());
                    userCopy.setCorreo(originalUser.getCorreo());
                    userCopy.setNombre(originalUser.getNombre());
                    userCopy.setApellido(originalUser.getApellido());
                    userCopy.setTelefono(originalUser.getTelefono());
                    userCopy.setEdad(originalUser.getEdad());

                    datosNuevosStr = objectMapper.writeValueAsString(userCopy);
                } else {
                    datosNuevosStr = objectMapper.writeValueAsString(datosNuevos);
                }
            }
        } catch (Exception e) {
            datosAntesStr = "{}";
            datosNuevosStr = "{}";
        }

        Bitacora bitacora = new Bitacora();
        bitacora.setUsuario(usuario);
        bitacora.setAccion(accion);
        bitacora.setTablaAfectada(tablaAfectada);
        bitacora.setDatosAnteriores(datosAntesStr);
        bitacora.setDatosNuevos(datosNuevosStr);
        bitacora.setFecha(LocalDateTime.now());

        bitacoraRepository.save(bitacora);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getBitacora(){
        List<Bitacora> bitacoras = bitacoraRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();

        List<Bitacora> bitacorasTransformadas = bitacoras.stream().map(bitacora -> {
            try {
                if (bitacora.getDatosNuevos() != null) {
                    JsonNode jsonNode = objectMapper.readTree(bitacora.getDatosNuevos());
                    bitacora.setDatosNuevos(objectMapper.writeValueAsString(jsonNode)); // Convertimos de nuevo a String sin escapes
                }
                if (bitacora.getDatosAnteriores() != null) {
                    JsonNode jsonNode = objectMapper.readTree(bitacora.getDatosAnteriores());
                    bitacora.setDatosAnteriores(objectMapper.writeValueAsString(jsonNode)); // Convertimos de nuevo a String sin escapes
                }
            } catch (Exception e) {
                // Si hay error, dejamos los valores como están
            }
            return bitacora;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(new ApiResponse(bitacorasTransformadas, HttpStatus.OK), HttpStatus.OK);
    }
}
