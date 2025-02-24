package edu.utez.SoftwareSeguro.controllers;


import edu.utez.SoftwareSeguro.models.UserModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    //nombre completo, correo, teléfono y edad
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String password;
    private Integer edad;

    public UserModel toEntity(){
        return new UserModel(nombre, apellido, correo, telefono, password, edad);
    }

    public UserDto(Long id, String nombre, String apellido, String correo, String telefono, String password, Integer edad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password;
        this.edad = edad;
    }
}
