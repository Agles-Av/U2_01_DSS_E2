package edu.utez.SoftwareSeguro.controllers.auth;

import edu.utez.SoftwareSeguro.config.ApiResponse;
import edu.utez.SoftwareSeguro.models.Bitacora;
import edu.utez.SoftwareSeguro.models.BitacoraRepository;
import edu.utez.SoftwareSeguro.services.UserService;
import jakarta.persistence.GeneratedValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/bitacora")
@CrossOrigin(origins = {"*"})
public class BitacoraController {

    private final UserService userService;

    public BitacoraController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> obtenerBitacora() {
        return userService.getBitacora();
    }
}
