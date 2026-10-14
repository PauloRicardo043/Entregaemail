package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.models.ImovelModel;

public interface  ImovelRepository extends JpaRepository<ImovelModel, Integer>{

    @EntityGraph(attributePaths = {"tipoImovel", "bairro"})
    @Query("SELECT imovel FROM ImovelModel imovel WHERE imovel.id = :id")
    java.util.Optional<ImovelModel> findByIdWithDetails(@Param("id") Integer id);
}
