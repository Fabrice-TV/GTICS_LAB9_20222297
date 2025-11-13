package org.example.servidorapi_20222297_laboratorio9.repository;

import org.example.servidorapi_20222297_laboratorio9.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}