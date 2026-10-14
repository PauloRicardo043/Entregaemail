package com.example.demo.services;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.FotoImovelModel;
import com.example.demo.models.ImovelModel;
import com.example.demo.repositories.FotoImovelRepository;
import com.example.demo.repositories.ImovelRepository;

@Service 
public class FotoImovelService {
    @Autowired 
    private FotoImovelRepository fotoImovelRepository;

    @Autowired 
    private ImovelRepository imovelRepository;

    @Value ("${app.upload-dir}")
    private String uploadDir;

    public FotoImovelModel salvar(MultipartFile arquivo, Integer imovelId){
        //1 - procurar se existe o imóvel para associar a foto
        ImovelModel imovel = imovelRepository.findById(imovelId).orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        try{
            //2 - criar o dir de uploads
            Path pasta = Paths.get(uploadDir, String.valueOf(imovelId)); 
                                // ./uploads/1/...
            Files.createDirectories(pasta);

            //3 - renomear e mover o arquivo
            String nomeFinal = gerarNomeArquivo(arquivo);
            Path destino = pasta.resolve(nomeFinal);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            //4 - criar o modelo e salvar
            FotoImovelModel model = new FotoImovelModel();
            model.setCaminho(destino.toString());
            model.setNomeArquivo(nomeFinal);
            model.setImovel(imovel);

            fotoImovelRepository.save(model);

            return model;

        }
        catch (IOException e){
            throw new UncheckedIOException("Não foi possível armazenar a foto.", e);
        }
    }

    public FotoImovelModel editar(Integer id, MultipartFile arquivo) {
        FotoImovelModel foto = buscarFoto(id);
        Path pasta = Paths.get(uploadDir, String.valueOf(foto.getImovel().getId()));
        String nomeFinal = gerarNomeArquivo(arquivo);
        Path destino = pasta.resolve(nomeFinal);

        try {
            Files.createDirectories(pasta);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível substituir a foto.", e);
        }

        String caminhoAnterior = foto.getCaminho();
        foto.setNomeArquivo(nomeFinal);
        foto.setCaminho(destino.toString());
        FotoImovelModel fotoAtualizada = fotoImovelRepository.save(foto);

        if (caminhoAnterior != null) {
            try {
                Files.deleteIfExists(Paths.get(caminhoAnterior));
            } catch (IOException e) {
                throw new UncheckedIOException("A foto foi atualizada, mas o arquivo anterior não pôde ser removido.", e);
            }
        }

        return fotoAtualizada;
    }

    public void excluir(Integer id) {
        FotoImovelModel foto = buscarFoto(id);
        fotoImovelRepository.delete(foto);

        if (foto.getCaminho() != null) {
            try {
                Files.deleteIfExists(Paths.get(foto.getCaminho()));
            } catch (IOException e) {
                throw new UncheckedIOException("A foto foi removida do cadastro, mas o arquivo não pôde ser excluído.", e);
            }
        }
    }

    @Transactional
    public FotoImovelModel tornarDestaque(Integer id) {
        FotoImovelModel fotoDestaque = buscarFoto(id);
        List<FotoImovelModel> fotosDoImovel =
                fotoImovelRepository.findByImovel_Id(fotoDestaque.getImovel().getId());

        for (FotoImovelModel foto : fotosDoImovel) {
            foto.setCapa(foto.getId().equals(id));
        }

        fotoImovelRepository.saveAll(fotosDoImovel);
        return fotoDestaque;
    }

    private FotoImovelModel buscarFoto(Integer id) {
        return fotoImovelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Foto não encontrada."));
    }

    private String gerarNomeArquivo(MultipartFile arquivo) {
        String nomeOriginal = arquivo.getOriginalFilename();
        String nomeLimpo = StringUtils.cleanPath(nomeOriginal == null ? "foto" : nomeOriginal);
        String nomeBase = Paths.get(nomeLimpo).getFileName().toString();
        if (nomeBase.isBlank()) {
            nomeBase = "foto";
        }
        return UUID.randomUUID() + "_" + nomeBase;
    }
}
