package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.UserDTO;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {
    @Autowired 
    private UserRepository repository;

    public List<UserModel> getAllUsers() {
        List<UserModel> lista = repository.findAll();
        return lista;        
    }

    public UserModel find(Integer id){
        Optional<UserModel> user = repository.findById(id);
        return user.orElse(null);
    }

    public UserModel insert(UserModel user){
        return repository.save(user);
    }

    public UserModel update(UserModel user){
        if (user == null || user.getId() == null) {
            return null;
        }
        UserModel usuarioExistente = find(user.getId());
        if (usuarioExistente == null) {
            return null;
        }

        if (user.getNome() != null) {
            usuarioExistente.setNome(user.getNome());
        }
        if (user.getEmail() != null) {
            usuarioExistente.setEmail(user.getEmail());
        }
        if (user.getSenha() != null) {
            usuarioExistente.setSenha(user.getSenha());
        }
        if (user.getTipo() != null) {
            usuarioExistente.setTipo(user.getTipo());
        }

        return repository.save(usuarioExistente);
    }

    public void delete(Integer id){
        repository.deleteById(id);
    }

     public UserModel insert(UserDTO dto) {
        UserModel model = new UserModel();
        model.setNome(dto.getNome());
        model.setEmail(dto.getEmail());
        model.setTipo(dto.getTipo());
        return repository.save(model);
    }

     public UserModel update(UserDTO dto) {
        UserModel model = find(dto.getId());
        try {
            if (model != null) {
                model.setEmail(dto.getNome());
                model.setEmail(dto.getEmail());
                model.setTipo(dto.getTipo());
                return repository.save(model);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
}
