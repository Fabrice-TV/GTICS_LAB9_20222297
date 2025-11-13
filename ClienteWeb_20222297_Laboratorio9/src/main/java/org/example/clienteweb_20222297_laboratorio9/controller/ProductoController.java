package org.example.clienteweb_20222297_laboratorio9.controller;

import org.example.clienteweb_20222297_laboratorio9.client.ProductoClient;
import org.example.clienteweb_20222297_laboratorio9.entity.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador web para gestionar las vistas de productos
 * Consume los endpoints de la API REST para mostrar información
 */
@Controller
public class ProductoController {

    private final ProductoClient productoClient;

    public ProductoController(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    /**
     * Muestra la página principal con el listado de todos los productos
     */
    @GetMapping({"/", "/productos"})
    public String index(Model model) {
        List<Producto> productos = productoClient.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        return "index";
    }

    /**
     * Procesa la búsqueda de un producto por ID
     * Muestra el producto encontrado o un mensaje de error
     */
    @GetMapping("/buscar")
    public String buscar(@RequestParam(name = "id", required = false) String idParam, Model model) {
        // Siempre cargar la lista completa de productos
        List<Producto> productos = productoClient.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);

        // Validar que se haya ingresado un ID
        if (idParam == null || idParam.isBlank()) {
            model.addAttribute("errorBusqueda", "Ingrese un ID de producto válido.");
            return "index";
        }

        try {
            Long id = Long.parseLong(idParam);
            productoClient.obtenerProductoPorId(id).ifPresentOrElse(
                    producto -> model.addAttribute("productoEncontrado", producto),
                    () -> model.addAttribute("errorBusqueda", "Producto no encontrado para el ID: " + id)
            );
        } catch (NumberFormatException nfe) {
            model.addAttribute("errorBusqueda", "ID inválido. Debe ser un número.");
        }

        return "index";
    }
}
