package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.models.ImovelModel;
import com.example.demo.dtos.ImovelInteresseDTO;
import com.example.demo.services.EmailService;
import com.example.demo.services.ImovelService;

import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/imoveis")
public class ImovelController {


    @Autowired
    private ImovelService service;

    @Autowired
    private EmailService emailService;

     @GetMapping()
    public ResponseEntity<List<ImovelModel>> getAll() {
        List<ImovelModel> list = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping(value = "/{id}")
        public ResponseEntity<ImovelModel> find(@PathVariable Integer id) {
        ImovelModel model = service.find(id);
        if(model!=null){
            return ResponseEntity.status(HttpStatus.OK).body(model);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody ImovelModel model) {
        model = service.insert(model);
        // return new ResponseEntity(model, HttpStatus.CREATED);
        URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(model.getId()).toUri();
        return ResponseEntity.created(uri).build();
        }

    @PostMapping(value = "/sabermais/{id}")
    public ResponseEntity<String> saberMais(
            @PathVariable Integer id,
            @RequestBody ImovelInteresseDTO interesse) throws MessagingException {
        if (interesse == null
                || !StringUtils.hasText(interesse.getNome())
                || !StringUtils.hasText(interesse.getTelefone())
                || !StringUtils.hasText(interesse.getEmail())
                || !StringUtils.hasText(interesse.getMensagem())) {
            return ResponseEntity.badRequest().body("Informe nome, telefone, e-mail e mensagem.");
        }

        ImovelModel imovel = service.findWithDetails(id);
        if (imovel == null) {
            return ResponseEntity.notFound().build();
        }

        emailService.enviarEmailComTemplate(
                interesse.getEmail(),
                "Mais informações sobre o imóvel: " + imovel.getTitulo(),
                "email/imovel-interesse",
                Map.of("cliente", interesse, "imovel", imovel));

        return ResponseEntity.ok("Solicitação enviada com sucesso.");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@RequestBody ImovelModel model, @PathVariable Integer id) {
        try {
             model.setId(id);
             model = service.update(model);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
       
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

   
}