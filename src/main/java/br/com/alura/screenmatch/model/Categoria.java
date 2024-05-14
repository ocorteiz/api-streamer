package br.com.alura.screenmatch.model;

public enum Categoria {

    COMEDIA("Comedy", "Comédia"),
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    ANIMACAO("Animation", "Animação"),
    AVENTURA("Adventure", "Aventura");

    private String categoriaOmdb;
    private String categoriaPortuguese;

    Categoria(String categoriaOmdb, String categoriaPortuguese) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortuguese = categoriaPortuguese;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromPortuguese(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortuguese.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        System.out.println("Nenhuma categoria encontrada para a string fornecida: " + text);
        return null;
    }

}
