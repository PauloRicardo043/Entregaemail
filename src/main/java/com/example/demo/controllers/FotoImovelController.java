package com.example.demo.controllers;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.FotoImovelModel;
import com.example.demo.services.FotoImovelService;

@RestController
@RequestMapping("/fotos")
public class FotoImovelController {
    @Autowired
    private FotoImovelService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FotoImovelModel> salvar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("imovelId") Integer imovelId) {
        try {
            if (arquivo.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            FotoImovelModel fotoImovelModel = service.salvar(arquivo, imovelId);
            return ResponseEntity.status(HttpStatus.CREATED).body(fotoImovelModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FotoImovelModel> editar(
            @PathVariable Integer id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(service.editar(id, arquivo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/destaque")
    public ResponseEntity<FotoImovelModel> tornarDestaque(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.tornarDestaque(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
