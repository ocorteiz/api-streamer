package br.com.alura.streamer.controller;

import br.com.alura.streamer.dto.EpisodioDTO;
import br.com.alura.streamer.dto.SerieDTO;
import br.com.alura.streamer.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    private List<SerieDTO> listSerie(){
        return serieService.listSerie();
    }

    @GetMapping("/five")
    public List<SerieDTO> listTopFiveSerie(){
        return serieService.listTopFiveSerie();
    }
    @GetMapping("/lancamentos")
    public List<SerieDTO> listLancamentosSerie(){
        return serieService.listLancamentosSerie();
    }

    @GetMapping("/{id}")
    public SerieDTO showSerie(@PathVariable Long id){
        return serieService.showSerie(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> listEpisodio(@PathVariable Long id) {
        return serieService.listEpisodio(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> listEpisodioTemporada(@PathVariable Long id, @PathVariable Long numero) {
        return serieService.listEpisodioPorTemporada(id, numero);
    }

    @GetMapping("/categoria/{nome}")
    public List<SerieDTO> listarSeriesPorCategoria(@PathVariable String nome){
        return serieService.listarSeriesPorCategoria(nome);
    }

}
