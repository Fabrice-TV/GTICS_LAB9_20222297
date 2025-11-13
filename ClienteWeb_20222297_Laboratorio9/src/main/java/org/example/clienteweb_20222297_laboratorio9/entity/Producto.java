package org.example.clienteweb_20222297_laboratorio9.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Producto {
    @JsonProperty("productoId")
    private Long id;

    @JsonProperty("nombreProducto")
    private String nombre;

    @JsonProperty("cantidadPorUnidad")
    private String descripcion;

    @JsonProperty("precioUnidad")
    private Double precio;

    public Producto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
