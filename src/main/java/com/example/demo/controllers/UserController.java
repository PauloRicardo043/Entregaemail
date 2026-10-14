package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dtos.UserDTO;
import com.example.demo.models.UserModel;
import com.example.demo.services.UserService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired 
    UserService service;

     @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserModel> listaNormal = service.getAllUsers();
        List<UserDTO> listaDtos = listaNormal.stream().map(usuario -> new UserDTO(usuario))
				.collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(listaDtos);
    }

     @PostMapping
    public ResponseEntity<Void> insert(@RequestBody UserDTO dto) {
        UserModel model = service.insert(dto);
        // return new ResponseEntity(model, HttpStatus.CREATED);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(model.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

     @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> find(@PathVariable Integer id) {
        UserModel model = service.find(id);
        UserDTO dto = new UserDTO(model);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

         @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@RequestBody UserDTO dto, @PathVariable Integer id) {
        dto.setId(id);
        UserModel model = service.update(dto);
        return ResponseEntity.noContent().build();
    }


     @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (service.find(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
