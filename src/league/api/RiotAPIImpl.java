package league.api;

import java.io.IOException;
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

public class RiotAPIImpl implements LeagueAPI{
    private static Logger log = Logger.getLogger(RiotAPIImpl.class.getName());
    
    private static final String API_KEY = APIConstants.API_KEY;

    private static final String BASE_URL = "https://na.api.pvp.net";
    private static final String SUMMONER_BYNAME_QUERY = "/api/lol/na/v1.4/summoner/by-name/%s";
    private static final String SUMMONER_QUERY = "/api/lol/na/v1.4/summoner/%d";
    private static final String MATCHHISTORY_QUERY = "/api/lol/na/v1.3/game/by-summoner/%d/recent";
    private static final String RANKED_QUERY = "/api/lol/na/v2.2/matchhistory/%d";
    private static final String CHAMP_QUERY = "/api/lol/static-data/na/v1.2/champion/%d";
    private static final String SUMMONERSPELL_QUERY = "/api/lol/static-data/na/v1.2/summoner-spell/%d";
    private static final String MATCH_QUERY = "/api/lol/na/v2.2/match/%d";

    private static final int OK = 200;
    
    private JerseyClient client;
    private ObjectMapper mapper = new ObjectMapper();
    
    private static RiotAPIImpl _instance = new RiotAPIImpl();

    public static RiotAPIImpl getInstance(){
        return _instance;
    }
    
    private RiotAPIImpl(){
        System.setProperty("javax.net.ssl.trustStoreType", "JCEKS");
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new JerseyClientBuilder().build();
    }

    public SummonerDto searchSummoner(String summonerName){
        summonerName = summonerName.toLowerCase().replace(" ", "");
        String uri = String.format(buildUri(SUMMONER_BYNAME_QUERY), summonerName);
        String entity = getEntity(uri);

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity,
                new TypeReference<Map<String, SummonerDto>>(){
                });
            SummonerDto summoner = map.get(summonerName);
            return summoner;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public SummonerDto getSummonerFromId(long summonerId){
        String uri = String.format(buildUri(SUMMONER_QUERY), summonerId);
        String entity = getEntity(uri);

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity,
                new TypeReference<Map<String, SummonerDto>>(){
                });
            SummonerDto summoner = map.get(summonerId + "");
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public List<MatchSummary> getRankedMatches(long summonerId){
        String uri = buildUri(String.format(RANKED_QUERY, summonerId));
        String entity = getEntity(uri);

        try{
            PlayerHistory history = mapper.readValue(entity, PlayerHistory.class);
            return history.getMatches();
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Set<GameDto> getMatchHistory(long summonerId){
        String uri = buildUri(String.format(MATCHHISTORY_QUERY, summonerId));
        String entity = getEntity(uri);

        try{
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
        String entity = getEntity(uri, params);

        try{
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
        String entity = getEntity(uri, params);

        try{
            SummonerSpellDto champ = mapper.readValue(entity, SummonerSpellDto.class);
            return champ;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public MatchDetail getMatchDetail(long id){
        String uri = buildUri(String.format(MATCH_QUERY, id));
        String entity = getEntity(uri);

        try{
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

    private String getEntity(String uri){
        return getEntity(uri, null);
    }

    private String getEntity(String uri, Map<String, String> params){
        Response response = query(uri, params);
        int status = response.getStatus();
        if(status != OK)
            return null;
    
        String entity = response.readEntity(String.class);
        return entity;
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
                return "200 OK";
            case 401:
                return "401 Unauthorized - did you forget the API key?";
            case 404:
                return "404 Not found";
            case 429:
                return "429 Ratelimit hit oops";
            case 500:
                return "500 Rito pls. They broke something";
            case 503:
                return "503 Riot API unavailable";
            default:
                return status + " Something else broke";
        }
    }

    public static void main(String[] args){
        RiotAPIImpl r = new RiotAPIImpl();
        System.out.println(r.searchSummoner("Zedenstein"));
        System.out.println(r.getMatchHistory(31569637));
    }
}
