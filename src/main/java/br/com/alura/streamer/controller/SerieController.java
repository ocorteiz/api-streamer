package br.com.alura.streamer.controller;

import br.com.alura.streamer.dto.SerieDTO;
import br.com.alura.streamer.model.Serie;
import br.com.alura.streamer.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieRepository serieRepository;

    @GetMapping
    private List<SerieDTO> list(){
        return serieRepository.findAll()
                .stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

}
