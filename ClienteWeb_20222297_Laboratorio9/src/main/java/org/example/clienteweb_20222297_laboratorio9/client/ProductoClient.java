package org.example.clienteweb_20222297_laboratorio9.client;

import org.example.clienteweb_20222297_laboratorio9.entity.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoClient {

    private final RestTemplate restTemplate;
    private final String urlBase;

    public ProductoClient(RestTemplate restTemplate, @Value("${api.base-url:http://localhost:8080}") String urlBase) {
        this.restTemplate = restTemplate;
        this.urlBase = urlBase;
    }

    /**
     * Obtiene todos los productos consumiendo el endpoint protegido GET /api/product
     */
    public List<Producto> obtenerTodosLosProductos() {
        try {
            ResponseEntity<List<Producto>> respuesta = restTemplate.exchange(
                    urlBase + "/api/product",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Producto>>() {
                    }
            );
            return respuesta.getBody() != null ? respuesta.getBody() : new ArrayList<>();
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Error 401: Credenciales inválidas para acceder al API");
            throw new RuntimeException("Error de autenticación: Credenciales inválidas", e);
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("Error 403: No tiene permisos para acceder a este recurso");
            throw new RuntimeException("Error de autorización: Sin permisos", e);
        } catch (ResourceAccessException e) {
            System.err.println("Error de conexión: No se puede conectar con el API en " + urlBase);
            throw new RuntimeException("Error de conexión con el API", e);
        } catch (HttpServerErrorException e) {
            System.err.println("Error del servidor: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un producto por ID consumiendo el endpoint protegido GET /api/product/{id}
     */
    public Optional<Producto> obtenerProductoPorId(Long id) {
        try {
            Producto producto = restTemplate.getForObject(urlBase + "/api/product/" + id, Producto.class);
            return Optional.ofNullable(producto);
        } catch (HttpClientErrorException.NotFound notFound) {
            System.out.println("Producto con ID " + id + " no encontrado");
            return Optional.empty();
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Error 401: Credenciales inválidas para acceder al API");
            throw new RuntimeException("Error de autenticación: Credenciales inválidas", e);
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("Error 403: No tiene permisos para acceder a este recurso");
            throw new RuntimeException("Error de autorización: Sin permisos", e);
        } catch (ResourceAccessException e) {
            System.err.println("Error de conexión: No se puede conectar con el API en " + urlBase);
            throw new RuntimeException("Error de conexión con el API", e);
        } catch (Exception e) {
            System.err.println("Error inesperado al buscar producto: " + e.getMessage());
            return Optional.empty();
        }
    }
}
