package br.com.alura.streamer.services;

import br.com.alura.streamer.dto.SerieDTO;
import br.com.alura.streamer.model.Serie;
import br.com.alura.streamer.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> list(){
        return converteDados(serieRepository.findAll());
    }

    public List<SerieDTO> listTopFive(){
        return converteDados(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> listLancamentos(){
        return converteDados(serieRepository.findTop5ByOrderByEpisodiosDesc());
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }
}
