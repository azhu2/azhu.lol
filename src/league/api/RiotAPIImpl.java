package league.api;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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

    private static final String API_KEY = SecurityConstants.API_KEY;

    private static final String BASE_URL = "https://na.api.pvp.net";
    private static final String SUMMONER_BYNAME_QUERY = "/api/lol/na/v1.4/summoner/by-name/%s";
    private static final String SUMMONER_QUERY = "/api/lol/na/v1.4/summoner/%s";
    private static final String MATCHHISTORY_QUERY = "/api/lol/na/v1.3/game/by-summoner/%d/recent";
    private static final String RANKED_QUERY = "/api/lol/na/v2.2/matchhistory/%d";
    private static final String CHAMP_QUERY = "/api/lol/static-data/na/v1.2/champion/%d";
    private static final String SUMMONERSPELL_QUERY = "/api/lol/static-data/na/v1.2/summoner-spell/%d";
    private static final String MATCH_QUERY = "/api/lol/na/v2.2/match/%d";

    private static final int MAX_ATTEMPTS = 15;
    private static final long ATTEMPT_INTERVAL = 1000;      // in ms

    private JerseyClient client;
    private ObjectMapper mapper = new ObjectMapper();

    private DatabaseAPIImpl db = DatabaseAPIImpl.getInstance();
    private static RiotAPIImpl _instance = new RiotAPIImpl();

    public static RiotAPIImpl getInstance(){
        return _instance;
    }

    private RiotAPIImpl(){
        System.setProperty("javax.net.ssl.trustStoreType", "JCEKS");
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new JerseyClientBuilder().build();
    }

    @Override
    public SummonerDto searchSummoner(String summonerName) throws RiotPlsException{
        summonerName = summonerName.toLowerCase().replace(" ", "");
        String uri = String.format(buildUri(SUMMONER_BYNAME_QUERY), summonerName);
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity, new TypeReference<Map<String, SummonerDto>>(){
            });
            SummonerDto summoner = map.get(summonerName);
            summoner.setName(summoner.getName().replace(" ", ""));
            db.cacheSummoner(summoner);
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SummonerDto> getSummoners(List<Long> summonerIds) throws RiotPlsException{
        if(summonerIds == null || summonerIds.isEmpty())
            return null;

        String ids = "";
        for(long id : summonerIds)
            ids += id + ",";

        String uri = buildUri(String.format(SUMMONER_QUERY, ids));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<Long, SummonerDto> map = mapper.readValue(entity, new TypeReference<Map<Long, SummonerDto>>(){
            });

            Collection<SummonerDto> summoners = map.values();
            for(SummonerDto summoner : summoners)
                db.cacheSummoner(summoner);

            return new LinkedList<SummonerDto>(summoners);
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public SummonerDto getSummonerFromId(long summonerId) throws RiotPlsException{
        String uri = String.format(buildUri(SUMMONER_QUERY), summonerId);
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity, new TypeReference<Map<String, SummonerDto>>(){
            });
            SummonerDto summoner = map.get(summonerId + "");
            db.cacheSummoner(summoner);
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<MatchSummary> getRankedMatches(long summonerId) throws RiotPlsException{
        String uri = buildUri(String.format(RANKED_QUERY, summonerId));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            PlayerHistory history = mapper.readValue(entity, PlayerHistory.class);
            return history.getMatches();
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<GameDto> getMatchHistory(long summonerId) throws RiotPlsException{
        String uri = buildUri(String.format(MATCHHISTORY_QUERY, summonerId));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            RecentGamesDto history = mapper.readValue(entity, RecentGamesDto.class);
            return history.getGames();
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ChampionDto getChampFromId(long id) throws RiotPlsException{
        String uri = buildUri(String.format(CHAMP_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("includeTimeline", "false");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            ChampionDto champ = mapper.readValue(entity, ChampionDto.class);
            db.cacheChampion(champ);
            return champ;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long id) throws RiotPlsException{
        String uri = buildUri(String.format(SUMMONERSPELL_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("spellData", "image");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            SummonerSpellDto spell = mapper.readValue(entity, SummonerSpellDto.class);
            db.cacheSummonerSpell(spell);
            return spell;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public MatchDetail getMatchDetail(long id) throws RiotPlsException{
        String uri = buildUri(String.format(MATCH_QUERY, id));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

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

    private String getEntity(String uri) throws RiotPlsException{
        return getEntity(uri, null);
    }

    private String retryGetEntity(JerseyWebTarget target, int tries){
        String uri = target.getUri().toString();
        if(tries > MAX_ATTEMPTS){
            log.warning(String.format("Maximum attempts for uri %s failed", uri));
            return null;
        }

        try{
            Thread.sleep(ATTEMPT_INTERVAL);
        } catch(InterruptedException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        Response response = target.request().get();
        int status = response.getStatus();

        if(status == APIConstants.HTTP_OK){
            log.info(String.format("Successful retry for uri %s", uri));
            return response.readEntity(String.class);
        } else if(status == APIConstants.HTTP_RATELIMIT){
            log.warning(String.format("Attempt %d for uri %s failed", tries, uri));
            return retryGetEntity(target, tries + 1);
        } else
            return null;
    }

    private String getEntity(String uri, Map<String, String> params) throws RiotPlsException{
        JerseyWebTarget target = query(uri, params);
        Response response = target.request().get();
        String uriStr = target.getUri().toString();
        int status = response.getStatus();

        if(status == APIConstants.HTTP_OK){
            log.info("Success for uri " + uriStr);
            return response.readEntity(String.class);
        } else if(status == APIConstants.HTTP_RATELIMIT){
            log.warning("429 Ratelimit hit oops | URI: " + uriStr);
            return retryGetEntity(target, 1);
        } else
            throw new RiotPlsException(status, uriStr);
    }

    public class RiotPlsException extends Exception{
        private int status;
        private String message;
        private String uriStr;

        public RiotPlsException(int statusCode, String uri){
            status = statusCode;
            uriStr = uri;
            setMessage();
        }

        private void setMessage(){
            switch(status){
                case APIConstants.HTTP_UNAUTHORIZED:
                    message = "401 Unauthorized - did you forget the API key? | URI: " + uriStr;
                    break;
                case APIConstants.HTTP_NOT_FOUND:
                    message = "404 Not found | URI: " + uriStr;
                    break;
                case APIConstants.HTTP_INTERNAL_SERVER_ERROR:
                    message = "500 Rito pls. They broke something | URI: " + uriStr;
                    break;
                case APIConstants.HTTP_UNAVAILABLE:
                    message = "503 Riot API unavailable | URI: " + uriStr;
                    break;
                default:
                    message = status + " Something else broke | URI: " + uriStr;
                    break;
            }
        }

        @Override
        public String getMessage(){
            return message;
        }

        public int getStatus(){
            return status;
        }
    }

    private JerseyWebTarget query(String uri, Map<String, String> params){
        JerseyWebTarget resource = client.target(uri);

        if(params != null)
            for(String param : params.keySet())
                resource = resource.queryParam(param, params.get(param));

        return resource;
    }

    public static void main(String[] args){
        RiotAPIImpl r = new RiotAPIImpl();
        try{
            System.out.println(r.searchSummoner("Zedenstein"));
            System.out.println(r.getMatchHistory(31569637));
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }
}
