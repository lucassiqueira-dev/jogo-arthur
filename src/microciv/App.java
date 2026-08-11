package microciv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import microciv.society.Civilization;
import microciv.ui.MenuManager;
import microciv.world.Map;
import microciv.world.Structure;
import microciv.world.StructureFactory;
import microciv.world.Tile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class App {
    public static int mapSize = 20;
    public static ArrayList<Structure> structures = new ArrayList<Structure>();
    public static Map mainMap;
    public static boolean gameStarted = false;
    public static int currentTurn = 1;
    public static String lastMessage = "Bem-vindo ao MicroCiv! Defina sua civilização para começar.";

    // Guardamos o nome da civ, líder e população aqui para não dar erro na classe Civilization original
    public static String civName = "Nova Civilização";
    public static String leaderName = "Grande Líder";
    public static int population = 5;

    public static void main(String[] args) throws IOException {
        structures.clear();
        structures.add(StructureFactory.createHouse());         // ID 0
        structures.add(StructureFactory.createFarm());          // ID 1
        structures.add(StructureFactory.createLoggingCamp());   // ID 2
        structures.add(StructureFactory.createQuarry());         // ID 3

        // Leitura da porta dinâmica (Usa PORT do ambiente na nuvem ou 8080 localmente)
        String portEnv = System.getenv("PORT");
        int porta = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

        // Inicialização do servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);

        server.createContext("/api/iniciar", new IniciarHandler());
        server.createContext("/api/estado", new EstadoHandler());
        server.createContext("/api/construir", new ConstruirHandler());
        server.createContext("/api/turno", new TurnoHandler());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        System.out.println("🌐 Servidor do MicroCiv rodando em: http://localhost:" + porta);
        server.start();
    }

    // Handler para iniciar novo jogo
    static class IniciarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            java.util.Map<String, String> params = parseQuery(requestURI.getQuery());

            civName = params.getOrDefault("civ", "Nova Civilização");
            leaderName = params.getOrDefault("leader", "Grande Líder");

            MenuManager.playerCiv = new Civilization();

            // Atribuição direta dos recursos da classe original
            MenuManager.playerCiv.woodResource = 100;
            MenuManager.playerCiv.foodResource = 100;
            MenuManager.playerCiv.stoneResource = 100;

            population = 5;

            // Gera mapa da ilha
            mainMap = new Map(mapSize);
            mainMap.generateIslandMap();

            gameStarted = true;
            currentTurn = 1;
            lastMessage = "Civilização " + civName + " fundada sob o comando de " + leaderName + "!";

            responderJSON(exchange, "{\"sucesso\": true}");
        }
    }

    // Handler para retornar estado completo do jogo
    static class EstadoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"gameStarted\": ").append(gameStarted).append(",");

            if (gameStarted && MenuManager.playerCiv != null) {
                Civilization civ = MenuManager.playerCiv;
                json.append("\"civName\": \"").append(escapar(civName)).append("\",");
                json.append("\"leaderName\": \"").append(escapar(leaderName)).append("\",");
                json.append("\"turn\": ").append(currentTurn).append(",");
                json.append("\"mensagem\": \"").append(escapar(lastMessage)).append("\",");
                json.append("\"recursos\": {");
                json.append("\"madeira\": ").append(civ.woodResource).append(",");
                json.append("\"comida\": ").append(civ.foodResource).append(",");
                json.append("\"pedra\": ").append(civ.stoneResource);
                json.append("},");
                json.append("\"populacao\": ").append(population).append(",");
                json.append("\"tamanho\": ").append(mapSize).append(",");

                // Renderização da Matriz do Mapa
                json.append("\"mapa\": [");
                for (int y = 0; y < mapSize; y++) {
                    json.append("[");
                    for (int x = 0; x < mapSize; x++) {
                        String tipoTerreno = "agua";
                        Tile tile = mainMap.map[x][y];

                        if (tile != null) {
                            if (tile.structure != null) {
                                String nome = tile.structure.name.toLowerCase();
                                if (nome.contains("house") || nome.contains("casa")) tipoTerreno = "casa";
                                else if (nome.contains("farm") || nome.contains("fazenda")) tipoTerreno = "fazenda";
                                else if (nome.contains("logging") || nome.contains("acampamento")) tipoTerreno = "acampamento";
                                else if (nome.contains("quarry") || nome.contains("pedreira")) tipoTerreno = "pedreira";
                                else tipoTerreno = "cidade";
                            } else {
                                switch (tile.terrainId) {
                                    case 1: tipoTerreno = "grama"; break;
                                    case 2: tipoTerreno = "floresta"; break;
                                    case 3: tipoTerreno = "montanha"; break;
                                    default: tipoTerreno = "agua"; break;
                                }
                            }
                        }

                        json.append("\"").append(tipoTerreno).append("\"");
                        if (x < mapSize - 1) json.append(",");
                    }
                    json.append("]");
                    if (y < mapSize - 1) json.append(",");
                }
                json.append("]");
            } else {
                json.append("\"mapa\": []");
            }

            json.append("}");
            responderJSON(exchange, json.toString());
        }
    }

    // Handler para construção (Mantendo a chamada original do Map.java)
    static class ConstruirHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!gameStarted) {
                responderJSON(exchange, "{\"sucesso\": false, \"mensagem\": \"Inicie o jogo primeiro.\"}");
                return;
            }

            URI requestURI = exchange.getRequestURI();
            java.util.Map<String, String> params = parseQuery(requestURI.getQuery());

            int x = Integer.parseInt(params.getOrDefault("x", "0"));
            int y = Integer.parseInt(params.getOrDefault("y", "0"));
            int structIdx = Integer.parseInt(params.getOrDefault("id", "0"));

            boolean sucesso = false;
            String msg = "";

            if (structIdx >= 0 && structIdx < structures.size()) {
                Structure estrutura = structures.get(structIdx);
                
                int madeiraAntes = MenuManager.playerCiv.woodResource;
                int comidaAntes = MenuManager.playerCiv.foodResource;
                int pedraAntes = MenuManager.playerCiv.stoneResource;

                // Executa a construção oficial
                mainMap.build(y, x, estrutura, MenuManager.playerCiv);

                if (mainMap.map[x][y] != null && mainMap.map[x][y].structure == estrutura) {
                    sucesso = true;
                    msg = "Construído com sucesso!";
                } else {
                    if (MenuManager.playerCiv.woodResource == madeiraAntes &&
                        MenuManager.playerCiv.foodResource == comidaAntes &&
                        MenuManager.playerCiv.stoneResource == pedraAntes) {
                        msg = "Não é possível construir aqui (solo incompatível)!";
                    } else {
                        msg = "Recursos insuficientes!";
                    }
                }
            }

            lastMessage = msg;
            responderJSON(exchange, "{\"sucesso\": " + sucesso + ", \"mensagem\": \"" + escapar(msg) + "\"}");
        }
    }

    // Handler para passar o turno
    static class TurnoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!gameStarted) return;

            currentTurn++;
            Civilization civ = MenuManager.playerCiv;

            int ganhoMadeira = 15;
            int ganhoComida = 15;
            int ganhoPedra = 10;

            for (int x = 0; x < mapSize; x++) {
                for (int y = 0; y < mapSize; y++) {
                    Tile t = mainMap.map[x][y];
                    if (t != null && t.structure != null) {
                        String sName = t.structure.name.toLowerCase();
                        if (sName.contains("farm") || sName.contains("fazenda")) ganhoComida += 10;
                        if (sName.contains("logging") || sName.contains("acampamento")) ganhoMadeira += 10;
                        if (sName.contains("quarry") || sName.contains("pedreira")) ganhoPedra += 10;
                        if (sName.contains("house") || sName.contains("casa")) {
                            if (currentTurn % 2 == 0) population += 1;
                        }
                    }
                }
            }

            civ.woodResource += ganhoMadeira;
            civ.foodResource += ganhoComida;
            civ.stoneResource += ganhoPedra;

            lastMessage = "Turno " + currentTurn + " concluído! Produção: +" + ganhoMadeira + " Madeira, +" + ganhoComida + " Comida, +" + ganhoPedra + " Pedra.";
            responderJSON(exchange, "{\"sucesso\": true}");
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File("web" + path);
            if (file.exists() && !file.isDirectory()) {
                byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
                
                String contentType = "text/html; charset=UTF-8";
                if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
                if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                String response = "404 - Arquivo Nao Encontrado";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    private static void responderJSON(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static java.util.Map<String, String> parseQuery(String query) {
        java.util.Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                try {
                    result.put(entry[0], URLDecoder.decode(entry[1], StandardCharsets.UTF_8.name()));
                } catch (Exception e) {
                    result.put(entry[0], entry[1]);
                }
            }
        }
        return result;
    }

    private static String escapar(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", " ");
    }
}