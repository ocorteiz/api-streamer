package br.com.alura.streamer.dto;

import br.com.alura.streamer.model.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(
        Long id,
        String titulo,
        Integer totalTemporadas,
        Double avaliacao,
        Categoria genero,
        String atores,
        String poster,
        String sinopse
){

}
