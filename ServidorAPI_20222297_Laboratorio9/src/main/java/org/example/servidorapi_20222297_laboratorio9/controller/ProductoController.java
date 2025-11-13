package org.example.servidorapi_20222297_laboratorio9.controller;

import org.example.servidorapi_20222297_laboratorio9.entity.Producto;
import org.example.servidorapi_20222297_laboratorio9.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductoController {

    private final ProductoRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ProductoController(ProductoRepository productRepository) {
        this.productRepository = productRepository;
    }

    // GET /product - listar todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> listAll() {
        List<Producto> productos = productRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    // GET /product/{id} - obtener producto por id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return productRepository.findById(id)
                .<ResponseEntity<?>>map(producto -> 
                    ResponseEntity.ok(producto)
                )
                .orElseGet(() -> 
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "estado", "error",
                            "mensaje", "Producto con id " + id + " no encontrado"
                        ))
                );
    }

    // POST /product - crear un nuevo producto
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productRepository.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Producto creado exitosamente",
                            "producto", nuevoProducto
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Error al crear el producto: " + e.getMessage()));
        }
    }

    // PUT /product - actualizar un producto
    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizar(@RequestBody Producto productoRecibido) {
        Map<String, Object> rpta = new java.util.HashMap<>();

        // Verificar que el ID sea válido y mayor que 0
        if (productoRecibido.getProductoId() != null && productoRecibido.getProductoId() > 0) {

            // Buscar el producto en la base de datos
            java.util.Optional<Producto> byId = productRepository.findById(productoRecibido.getProductoId());

            if (byId.isPresent()) {
                Producto productoFromDb = byId.get();

                // Actualizar solo los campos que no son null en el producto recibido
                if (productoRecibido.getNombreProducto() != null)
                    productoFromDb.setNombreProducto(productoRecibido.getNombreProducto());

                if (productoRecibido.getPrecioUnidad() != null)
                    productoFromDb.setPrecioUnidad(productoRecibido.getPrecioUnidad());

                if (productoRecibido.getProveedorId() != null)
                    productoFromDb.setProveedorId(productoRecibido.getProveedorId());

                if (productoRecibido.getCategoriaId() != null)
                    productoFromDb.setCategoriaId(productoRecibido.getCategoriaId());

                if (productoRecibido.getCantidadPorUnidad() != null)
                    productoFromDb.setCantidadPorUnidad(productoRecibido.getCantidadPorUnidad());

                // Guardar el producto actualizado
                productRepository.save(productoFromDb);
                rpta.put("resultado", "ok");
                return ResponseEntity.ok(rpta);

            } else {
                rpta.put("resultado", "error");
                rpta.put("mensaje", "El ID del producto enviado no existe");
                return ResponseEntity.badRequest().body(rpta);
            }
        } else {
            rpta.put("resultado", "error");
            rpta.put("mensaje", "Debe enviar un producto con ID");
            return ResponseEntity.badRequest().body(rpta);
        }
    }

    // DELETE /product/{id} - eliminar un producto (con eliminación en cascada de order details)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // Verificar que el producto existe
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "estado", "error",
                        "mensaje", "Producto con id " + id + " no encontrado"
                    ));
        }

        try {
            // Primero eliminar las filas dependientes en OrderDetails
            int deletedDetails = jdbcTemplate.update("DELETE FROM OrderDetails WHERE ProductID = ?", id);

            // Luego eliminar el producto
            productRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "estado", "success",
                            "mensaje", "Producto eliminado exitosamente",
                            "productoId", id,
                            "orderDetailsEliminados", deletedDetails
                    )
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "estado", "error",
                        "mensaje", "No se puede eliminar el producto por restricción referencial",
                        "detalle", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "estado", "error",
                        "mensaje", "Error al eliminar el producto",
                        "detalle", e.getMessage()
                    ));
        }
    }
}