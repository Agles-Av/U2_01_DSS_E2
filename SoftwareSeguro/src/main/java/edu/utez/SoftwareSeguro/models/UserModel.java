package edu.utez.SoftwareSeguro.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="usuarios")
@NoArgsConstructor
@Setter
@Getter
public class UserModel {
    //nombre completo, correo, teléfono y edad
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(length = 10, nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer edad;

    public UserModel(Long id, String nombre, String apellido, String correo, String telefono, String password, Integer edad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password;
        this.edad = edad;
    }

    public UserModel(String nombre, String apellido, String correo, String telefono, String password, Integer edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.password = password;
        this.edad = edad;
    }

}
