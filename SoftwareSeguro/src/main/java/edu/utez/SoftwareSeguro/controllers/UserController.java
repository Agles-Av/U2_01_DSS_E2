package edu.utez.SoftwareSeguro.controllers;

import edu.utez.SoftwareSeguro.config.ApiResponse;
import edu.utez.SoftwareSeguro.models.UserModel;
import edu.utez.SoftwareSeguro.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
@CrossOrigin(origins = {"*"})
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{quien}/")
    public ResponseEntity<ApiResponse> getAll(@PathVariable String quien) {
        return service.getAll(quien);}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/{quien}/")
    public ResponseEntity<ApiResponse> create(@RequestBody UserModel user, @PathVariable String quien) {
        return service.createUser(user, quien);
    }

    @PutMapping("/{quien}/{id}")
    public ResponseEntity<ApiResponse> update(@RequestBody UserModel user, @PathVariable Long id, @PathVariable String quien) {
        return service.updateUser(id, user, quien);
    }

    @DeleteMapping("/{quien}/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, @PathVariable String quien) {
        return service.deleteUser(id, quien);
    }

}
