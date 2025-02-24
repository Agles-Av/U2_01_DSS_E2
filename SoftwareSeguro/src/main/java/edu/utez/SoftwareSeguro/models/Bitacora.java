package edu.utez.SoftwareSeguro.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="bitacora")
@NoArgsConstructor
@Setter
@Getter
public class Bitacora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String usuario;

    @Column
    private String accion;

    @Column
    private String tablaAfectada;

    @Lob // Guarda JSON en la base de datos sin convertirlo a String puro
    @Column(columnDefinition = "TEXT")
    private String datosAnteriores;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String datosNuevos;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fecha;

    public Bitacora(Long id, String usuario, String accion, String tablaAfectada, String datosAnteriores, String datosNuevos, LocalDateTime fecha) {
        this.id = id;
        this.usuario = usuario;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.datosAnteriores = datosAnteriores;
        this.datosNuevos = datosNuevos;
        this.fecha = fecha;
    }

    public Bitacora(String usuario, String accion, String tablaAfectada, String datosAnteriores, String datosNuevos, LocalDateTime fecha) {
        this.usuario = usuario;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.datosAnteriores = datosAnteriores;
        this.datosNuevos = datosNuevos;
        this.fecha = fecha;
    }
}
