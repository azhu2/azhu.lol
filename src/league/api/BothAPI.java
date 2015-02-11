package league.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.PlayerHistory;
import league.entities.RecentGamesDto;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

@Deprecated
public class BothAPI{
    private static Logger log = Logger.getLogger(BothAPI.class.getName());
    
    private static final String API_KEY = APIConstants.API_KEY;
    public static final int INVALID = -1;

    private static final String BASE_URL = "https://na.api.pvp.net";
    private static final String SUMMONER_BYNAME_QUERY = "/api/lol/na/v1.4/summoner/by-name/%s";
    private static final String SUMMONER_QUERY = "/api/lol/na/v1.4/summoner/%d";
    private static final String MATCHHISTORY_QUERY = "/api/lol/na/v1.3/game/by-summoner/%d/recent";
    private static final String RANKED_QUERY = "/api/lol/na/v2.2/matchhistory/%d";
    private static final String CHAMP_QUERY = "/api/lol/static-data/na/v1.2/champion/%d";
    private static final String SUMMONERSPELL_QUERY = "/api/lol/static-data/na/v1.2/summoner-spell/%d";
    private static final String MATCH_QUERY = "/api/lol/na/v2.2/match/%d";
    
    private static final String OK = "ok";

    private JerseyClient client;
    private ObjectMapper mapper = new ObjectMapper();

    // DB Stuff
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/lol";
    private static final String USER = APIConstants.DB_USER;
    private static final String PASS = APIConstants.DB_PASS;
    private Connection db;
    
    private static BothAPI _instance = new BothAPI();

    public static BothAPI getInstance(){
        return _instance;
    }
    
    private BothAPI(){
        System.setProperty("javax.net.ssl.trustStoreType", "JCEKS");
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new JerseyClientBuilder().build();

        try{
            Class.forName(JDBC_DRIVER);
            log.info("Connecting to database...");
            db = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch(ClassNotFoundException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public SummonerDto searchSummoner(String summonerName){
        summonerName = summonerName.toLowerCase().replace(" ", "");

        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate FROM summoners WHERE name = '%s'",
                summonerName);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel,
                        revisionDate);
                log.info("Fetched summoner " + summoner + " from db.");
                return summoner;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        String uri = String.format(buildUri(SUMMONER_BYNAME_QUERY), summonerName);
        Response response = query(uri);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return new SummonerDto(INVALID, status, INVALID, INVALID, INVALID);

        try{
            String entity = response.readEntity(String.class);
            Map<String, SummonerDto> map = mapper.readValue(entity,
                new TypeReference<Map<String, SummonerDto>>(){
                });
            SummonerDto summoner = map.get(summonerName);
            addSummonerToDb(summoner);
            return summoner;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public SummonerDto getSummonerFromId(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate FROM summoners WHERE id = %d",
                summonerId);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel,
                        revisionDate);
                log.info("Fetched summoner " + summoner + " from db.");
                return summoner;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        String uri = String.format(buildUri(SUMMONER_QUERY), summonerId);
        Response response = query(uri);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return new SummonerDto(INVALID, status, INVALID, INVALID, INVALID);

        try{
            String entity = response.readEntity(String.class);
            Map<String, SummonerDto> map = mapper.readValue(entity,
                new TypeReference<Map<String, SummonerDto>>(){
                });
            SummonerDto summoner = map.get(summonerId + "");
            addSummonerToDb(summoner);
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private boolean addSummonerToDb(SummonerDto summoner){
        if(summoner.getId() == INVALID)
            return false;
        
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("INSERT INTO summoners VALUES (%d, '%s', %d, %d, %d)",
                summoner.getId(), summoner.getName(), summoner.getProfileIconId(),
                summoner.getSummonerLevel(), summoner.getRevisionDate());
            int updated = stmt.executeUpdate(sql);
            if(updated < 1){
                log.log(Level.WARNING, "Update summoner " + summoner + " failed.");
                return false;
            }
            log.info("Update summoner " + summoner + " success.");
            return true;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public List<MatchSummary> getRankedMatches(long summonerId){
        String uri = buildUri(String.format(RANKED_QUERY, summonerId));
        Response response = query(uri);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return null;

        try{
            String entity = response.readEntity(String.class);
            PlayerHistory history = mapper.readValue(entity, PlayerHistory.class);
            return history.getMatches();
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Set<GameDto> getMatchHistory(long summonerId){
        String uri = buildUri(String.format(MATCHHISTORY_QUERY, summonerId));
        Response response = query(uri);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return null;

        try{
            String entity = response.readEntity(String.class);
            RecentGamesDto history = mapper.readValue(entity, RecentGamesDto.class);
            return history.getGames();
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public ChampionDto getChampFromId(long id){
        String uri = buildUri(String.format(CHAMP_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("includeTimeline", "false");
        Response response = query(uri, params);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return null;

        try{
            String entity = response.readEntity(String.class);
            ChampionDto champ = mapper.readValue(entity, ChampionDto.class);
            return champ;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public SummonerSpellDto getSummonerSpellFromId(long id){
        String uri = buildUri(String.format(SUMMONERSPELL_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("spellData", "image");
        Response response = query(uri, params);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return null;

        try{
            String entity = response.readEntity(String.class);
            SummonerSpellDto champ = mapper.readValue(entity, SummonerSpellDto.class);
            return champ;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public MatchDetail getMatchDetail(long id){
        String uri = buildUri(String.format(MATCH_QUERY, id));
        Response response = query(uri);
        String status = handleResponse(response.getStatus());
        if(status != OK)
            return null;

        try{
            String entity = response.readEntity(String.class);
            MatchDetail match = mapper.readValue(entity, MatchDetail.class);
            return match;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private static String buildUri(String method){
        return BASE_URL + method + "?api_key=" + API_KEY;
    }

    private Response query(String uri){
        return query(uri, null);
    }

    private Response query(String uri, Map<String, String> params){
        JerseyWebTarget resource = client.target(uri);

        if(params != null)
            for(String param : params.keySet())
                resource = resource.queryParam(param, params.get(param));

        Response response = resource.request().get();
        return response;
    }

    private static String handleResponse(int status){
        switch(status){
            case 200:
                return OK;
            case 401:
                return "401 Unauthorized - did you forget the API key?";
            case 404:
                return "404 Not found";
            case 429:
                return "429 Ratelimit hit";
            case 500:
                return "500 Rito pls. They broke something";
            case 503:
                return "503 Riot API unavailable";
            default:
                return status + " Something broke";
        }
    }

    public static void main(String[] args){
        BothAPI r = new BothAPI();
        System.out.println(r.searchSummoner("Zedenstein"));
    }
}
