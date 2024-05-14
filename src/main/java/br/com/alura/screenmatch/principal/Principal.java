package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
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
                    3 - Listar Todas as Séries
                    4 - Buscar Série Por titulo
                                    
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
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    listarSeriePorTitulo();
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

    private void buscarEpisodioPorSerie(){
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

    private void listarSeriesBuscadas(){
        serieList = serieRepository.findAll();

        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void listarSeriePorTitulo() {
        System.out.println("Digite o titulo da série: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serieEncontrada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieEncontrada.isPresent()) {
            System.out.println("Série encontrada: \n");
            System.out.println(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada!");
        }
    }
}