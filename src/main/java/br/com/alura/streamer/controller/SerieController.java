package br.com.alura.streamer.controller;

import br.com.alura.streamer.dto.SerieDTO;
import br.com.alura.streamer.repository.SerieRepository;
import br.com.alura.streamer.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    private List<SerieDTO> list(){
        return serieService.list();
    }

    @GetMapping("/five")
    public List<SerieDTO> listTopFive(){
        return serieService.listTopFive();
    }
    @GetMapping("/lancamentos")
    public List<SerieDTO> listLancamentos(){
        return serieService.listLancamentos();
    }

}
