package br.com.alura.streamer.dto;

public record EpisodioDTO(
        Integer temporada,
        Integer numeroEpisodio,
        String titulo
) {
}
