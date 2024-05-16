package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private final List<DadosSerie> dadosSerieList = new ArrayList<>();
    private List<Serie> serieList = new ArrayList<>();
    private Optional<Serie> optionalSerie;
    private SerieRepository serieRepository;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibeMenu() {

        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar Séries Na Web
                    2 - Buscar Episódios de Serie Na Web
                    3 - Listar Todas as Séries Buscadas
                    4 - Listar Série Buscadas Por titulo
                    5 - Listar Série Buscadas Por Ator e Avaliacao
                    6 - Listar Série Buscada Top 5
                    7 - Listar Série Buscadas Por Genero
                    8 - Filtrar Séries Por Temporada e Avaliação
                    9 - Filtrar Episodio Por Trecho
                    10 - Filtrar Episodios Top 5
                    11 - Filtrar Episodios Por Data
                                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();


            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerieNaWeb();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    listarSeriesBuscadasPorTitulo();
                    break;
                case 5:
                    listarSeriesBuscadasPorAtor();
                    break;
                case 6:
                    listarSeriesBuscadasTop5();
                    break;
                case 7:
                    listarSeriesBuscadasPorGenero();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    filtrarEpisodioPorTrecho();
                    break;
                case 10:
                    filtrarEpisodioTop5Epiodios();
                    break;
                case 11:
                    filtrarEpisodioPorDataLancamento();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("\nOpção inválida\n");
            }

        }

    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = null;
        if (dados != null) {
            dadosSerieList.add(dados);
            serie = new Serie(dados);
            System.out.println(serie);
            serieRepository.save(serie);
        }
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para buscar na web: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.has("Response") && jsonObject.get("Response").getAsString().equals("False")) {
            System.out.println("Serie não encontrada");
            return null;
        }

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerieNaWeb() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série acima para buscar ep's na web: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = serieList.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);

        } else {
            System.out.println("Serie não encontrada");
        }

    }

    private void listarSeriesBuscadas() {
        serieList = serieRepository.findAll();

        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void listarSeriesBuscadasPorTitulo() {
        System.out.println("Digite o titulo da série: ");
        var nomeSerie = leitura.nextLine();

        optionalSerie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);
        if (optionalSerie.isPresent()) {
            System.out.println("Série encontrada: \n");
            System.out.println(optionalSerie.get());
        } else {
            System.out.println("Serie não encontrada!");
        }
    }

    private void listarSeriesBuscadasPorAtor() {
        System.out.println("Digite o nome do ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Digite o o valor da avaliação: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> serieEncontraca = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        if (serieEncontraca.isEmpty()) {
            System.out.println("Séries não encontrada");
        } else {
            System.out.println("Séries em que " + nomeAtor + " trabalhou com avaliação maior que " + avaliacao);
            serieEncontraca.stream()
                    .forEach(s -> System.out.println(s.getTitulo() + " - " + s.getAvaliacao()));
        }
    }

    private void listarSeriesBuscadasTop5() {
        List<Serie> seriesTop5 = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("Top 5 Séries: \n");
        seriesTop5
                .forEach(s -> System.out.println(s.getTitulo() + " - " + s.getAvaliacao()));
    }

    private void listarSeriesBuscadasPorGenero() {
        System.out.println("Digite o Gênero/Categoria: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortuguese(nomeGenero);

        List<Serie> serieEncontrada = serieRepository.findByGenero(categoria);
        if (serieEncontrada.isEmpty()) {
            System.out.println("Não existe séries desse genero");

        } else {
            System.out.println("Série encontrada: \n");
            serieEncontrada.stream()
                    .forEach(s -> System.out.println(s.getTitulo() + " - " + s.getAvaliacao()));
        }
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Digite o numero de temporadas: ");
        var temporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Digite a avaliação minima");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> serieOptional = serieRepository.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);
        if (serieOptional.isEmpty()) {
            System.out.println("Série não encontrada!");
        } else {
            System.out.println("Série encontrada: \n");
            serieOptional.stream()
                    .forEach(s -> System.out.println(s.getTitulo() + " - " + s.getAvaliacao()));
        }

    }

    private void filtrarEpisodioPorTrecho() {
        System.out.println("Digite o trecho do episodio: ");
        var trecho = leitura.nextLine();
        List<Episodio> episodiosEncontrado = serieRepository.episodiosPorTrecho(trecho);
        if (episodiosEncontrado.isEmpty()) {
            System.out.println("Série não encontrada!");
        } else {
            System.out.println("Série encontrada: \n");
            episodiosEncontrado.forEach(e -> System.out.println(
                            String.format(
                                    "Serie: %s - Temporada: %s - Episodio %d: %s",
                                    e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo())
                    )
            );
        }
    }

    private void filtrarEpisodioTop5Epiodios() {
        listarSeriesBuscadasPorTitulo();
        if (optionalSerie.isPresent()) {
            Serie serie = optionalSerie.get();
            List<Episodio> episodiosEcontrados = serieRepository.episodiosTop5(serie);
            episodiosEcontrados.forEach(System.out::println);
        }
    }

    private void filtrarEpisodioPorDataLancamento() {
        listarSeriesBuscadasPorTitulo();
        if (optionalSerie.isPresent()) {
            Serie serie = optionalSerie.get();
            System.out.println("Digite a data de lançamento");
            var data = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosEncontrados = serieRepository.episodiosPorDataLancamento(serie, data);
            episodiosEncontrados.forEach(System.out::println);
        }
    }
}