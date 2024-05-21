package br.com.alura.streamer.convert;

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
