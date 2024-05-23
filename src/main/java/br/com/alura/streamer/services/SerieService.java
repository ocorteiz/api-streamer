package br.com.alura.streamer.services;

import br.com.alura.streamer.dto.EpisodioDTO;
import br.com.alura.streamer.dto.SerieDTO;
import br.com.alura.streamer.model.Categoria;
import br.com.alura.streamer.model.Serie;
import br.com.alura.streamer.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> listSerie(){
        return converteDados(serieRepository.findAll());
    }

    public List<SerieDTO> listTopFiveSerie(){
        return converteDados(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> listLancamentosSerie(){
        return converteDados(serieRepository.lancamentosMaisRecentes());
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public SerieDTO showSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> listEpisodio(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                   .map(e -> new EpisodioDTO(e.getTemporada(),e.getNumeroEpisodio(), e.getTitulo()))
                   .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> listEpisodioPorTemporada(Long id, Long numero) {
        return serieRepository.listarEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDTO(e.getTemporada(),e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> listarSeriesPorCategoria(String nome) {
        Categoria categoria = Categoria.fromPortuguese(nome);
        return converteDados(serieRepository.findByGenero(categoria));
    }
}
