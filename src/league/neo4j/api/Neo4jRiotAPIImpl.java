package league.neo4j.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import league.api.APIConstants;
import league.api.RiotPlsException;
import league.api.SecurityConstants;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ItemDto;
import league.entities.LeagueDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.PlayerHistory;
import league.entities.RecentGamesDto;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;
import league.neo4j.entities.GeneralMatch4j;
import league.neo4j.entities.RankedMatch4j;
import league.neo4j.entities.Views;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

public class Neo4jRiotAPIImpl implements Neo4jAPI{
    private static Logger log = Logger.getLogger(Neo4jRiotAPIImpl.class.getName());

    private static final String API_KEY = SecurityConstants.API_KEY;

    private static final String BASE_URL = "https://na.api.pvp.net";
    private static final String SUMMONER_BYNAME_QUERY = "/api/lol/na/v1.4/summoner/by-name/%s";
    private static final String SUMMONER_QUERY = "/api/lol/na/v1.4/summoner/%s";
    private static final String MATCHHISTORY_QUERY = "/api/lol/na/v1.3/game/by-summoner/%d/recent";
    private static final String RANKED_QUERY = "/api/lol/na/v2.2/matchhistory/%d";
    private static final String CHAMP_QUERY = "/api/lol/static-data/na/v1.2/champion/%d";
    private static final String SUMMONERSPELL_QUERY = "/api/lol/static-data/na/v1.2/summoner-spell/%d";
    private static final String MATCH_QUERY = "/api/lol/na/v2.2/match/%d";
    private static final String LEAGUE_QUERY = "/api/lol/na/v2.5/league/by-summoner/%s/entry";
    private static final String ITEM_QUERY = "/api/lol/static-data/na/v1.2/item/%d";

    private static final int MAX_ATTEMPTS = 15;
    private static final long ATTEMPT_INTERVAL = 1000;      // in ms
    private boolean RETRY_INFINITE = false;

    private JerseyClient client;
    private ObjectMapper mapper = new ObjectMapper();

    protected Neo4jDatabaseAPI db = Neo4jDatabaseAPIImpl.getInstance();
    private static Neo4jRiotAPIImpl _instance = new Neo4jRiotAPIImpl();

    public static Neo4jRiotAPIImpl getInstance(){
        return _instance;
    }

    private Neo4jRiotAPIImpl(){
        System.setProperty("javax.net.ssl.trustStoreType", "JCEKS");
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.getSerializationConfig().setSerializationView(Views.Neo4jView.class);
        client = new JerseyClientBuilder().build();
    }

    @Override
    public Summoner searchSummoner(String summonerName) throws RiotPlsException{
        summonerName = summonerName.toLowerCase().replace(" ", "");
        String uri = String.format(buildUri(SUMMONER_BYNAME_QUERY), summonerName);
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity, new TypeReference<Map<String, SummonerDto>>(){
            });
            SummonerDto summonerDto = map.get(summonerName);
            Summoner summoner = new Summoner(summonerDto, getLeague(summonerDto.getId()));
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Summoner> getSummoners(List<Long> summonerIds) throws RiotPlsException{
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
            List<Summoner> summoners = new LinkedList<>();

            List<League> leagues = getLeagues(summonerIds);
            for(int i = 0; i < summonerIds.size(); i++){
                long id = summonerIds.get(i);
                SummonerDto summonerDto = map.get(id);
                League league = null;
                if(leagues != null)
                    league = leagues.get(i);
                summoners.add(new Summoner(summonerDto, league));
            }

            return summoners;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Summoner getSummonerFromId(long summonerId) throws RiotPlsException{
        String uri = String.format(buildUri(SUMMONER_QUERY), summonerId);
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<String, SummonerDto> map = mapper.readValue(entity, new TypeReference<Map<String, SummonerDto>>(){
            });
            SummonerDto summonerDto = map.get(summonerId + "");
            Summoner summoner = new Summoner(summonerDto, getLeague(summonerDto.getId()));
            return summoner;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Match getRankedMatch(long matchId) throws RiotPlsException{
        MatchDetail detail = getMatchDetail(matchId);
        Match match = new RankedMatch4j(detail);
        return match;
    }

    private MatchDetail getMatchDetail(long id) throws RiotPlsException{
        String uri = buildUri(String.format(MATCH_QUERY, id));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            MatchDetail match = mapper.readValue(entity, MatchDetail.class);
            // db.cacheMatchDetail(match);
            return match;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public List<Match> getRankedMatches(long summonerId, int startIndex, int page_size) throws RiotPlsException{
        String uri = buildUri(String.format(RANKED_QUERY, summonerId));
        Map<String, String> params = new HashMap<>();
        params.put("beginIndex", startIndex + "");
        params.put("endIndex", (startIndex + page_size) + "");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            PlayerHistory history = mapper.readValue(entity, PlayerHistory.class);
            List<MatchSummary> historyMatches = history.getMatches();

            List<Match> matches = new LinkedList<>();
            for(MatchSummary historyMatch : historyMatches)
                matches.add(getRankedMatch(historyMatch.getMatchId()));
            return matches;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public List<Match> getRankedMatches(long summonerId, int startIndex) throws RiotPlsException{
        return getRankedMatches(summonerId, startIndex, APIConstants.RANKED_PAGE_SIZE);
    }

    private List<Long> getRankedMatchIds(long summonerId, int start) throws RiotPlsException{
        String uri = buildUri(String.format(RANKED_QUERY, summonerId));
        Map<String, String> params = new HashMap<>();
        params.put("beginIndex", start + "");
        params.put("endIndex", (start + APIConstants.MAX_PAGE_SIZE) + "");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            PlayerHistory history = mapper.readValue(entity, PlayerHistory.class);
            List<MatchSummary> historyMatches = history.getMatches();
            if(historyMatches == null || historyMatches.isEmpty())
                return null;

            List<Long> matches = new LinkedList<>();
            for(MatchSummary historyMatch : historyMatches)
                matches.add(historyMatch.getMatchId());
            return matches;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Long> getRankedMatchIds(long summonerId) throws RiotPlsException{
        return getRankedMatchIds(summonerId, 0);
    }

    @Override
    public List<Long> getAllRankedMatchIds(long summonerId) throws RiotPlsException{
        List<Long> ids = new LinkedList<>();
        int start = 0;

        List<Long> matchPage = null;
        do{
            matchPage = getRankedMatchIds(summonerId, start);
            if(matchPage != null)
                ids.addAll(matchPage);
            start += APIConstants.MAX_PAGE_SIZE;
        } while(matchPage != null);

        return ids;
    }

    @Override
    public List<Match> getRankedMatches(long summonerId) throws RiotPlsException{
        return getRankedMatches(summonerId, 0);
    }

    /**
     * @deprecated Should not be used. Read ranked matches one page at a time from the Riot API until you hit a match already
     *             cached
     */
    @Override
    public List<Match> getAllRankedMatches(long summonerId) throws RiotPlsException{
        return null;
    }

    /**
     * Query the API for all ranked matches and cache them in DB
     */
    @Override
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException{
        int start = 0;
        int count = 0;

        List<Long> matchPage = null;
        do{
            matchPage = getRankedMatchIds(summonerId, start);
            if(matchPage != null)
                for(Long matchId : matchPage){
                    if(!db.hasRankedMatch(matchId))
                        db.cacheRankedMatch(getRankedMatch(matchId));
                    count++;
                }
            start += APIConstants.MAX_PAGE_SIZE;
        } while(matchPage != null);

        return count;
    }

    @Override
    public Match getGame(long matchId, long summonerId) throws RiotPlsException{
        // Return null? Shouldn't have lookup by id
        return null;
    }

    /**
     * Can only get one page from the API
     */
    @Override
    public Set<Match> getAllGames(long summonerId) throws RiotPlsException{
        return getMatchHistory(summonerId);
    }

    @Override
    public Set<Match> getMatchHistory(long summonerId) throws RiotPlsException{
        String uri = buildUri(String.format(MATCHHISTORY_QUERY, summonerId));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            RecentGamesDto history = mapper.readValue(entity, RecentGamesDto.class);

            Set<Match> matches = new HashSet<>();
            for(GameDto gameDto : history.getGames()){
                Match game = db.getGame(gameDto.getGameId(), summonerId);
                if(game == null){
                    game = new GeneralMatch4j(gameDto, summonerId);
                    db.cacheGame(game, summonerId);
                }
                matches.add(game);
            }
            return matches;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ChampionDto getChampionFromId(int id) throws RiotPlsException{
        String uri = buildUri(String.format(CHAMP_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("champData", "image");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            ChampionDto champ = mapper.readValue(entity, ChampionDto.class);
            return champ;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(int id) throws RiotPlsException{
        String uri = buildUri(String.format(SUMMONERSPELL_QUERY, id));
        Map<String, String> params = new HashMap<>();
        params.put("spellData", "image");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            SummonerSpellDto spell = mapper.readValue(entity, SummonerSpellDto.class);
            return spell;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ItemDto getItemFromId(int itemId) throws RiotPlsException{
        String uri = buildUri(String.format(ITEM_QUERY, itemId));
        Map<String, String> params = new HashMap<>();
        params.put("itemData", "image");
        String entity = getEntity(uri, params);

        if(entity == null)
            return null;

        try{
            ItemDto item = mapper.readValue(entity, ItemDto.class);
            return item;
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public League getLeague(long summonerId) throws RiotPlsException{
        String uri = buildUri(String.format(LEAGUE_QUERY, summonerId));
        String entity = getEntity(uri);

        if(entity == null)
            return null;

        try{
            Map<Long, List<LeagueDto>> map = mapper.readValue(entity, new TypeReference<Map<Long, List<LeagueDto>>>(){
            });
            LeagueDto league = map.get(summonerId).get(0);
            return new League(league);
        } catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public List<League> getLeagues(List<Long> idList) throws RiotPlsException{
        if(idList == null || idList.isEmpty())
            return null;

        List<Long> idsCopy = new LinkedList<>(idList);
        List<League> leagues = new LinkedList<>();

        List<Long> summonerIds = new LinkedList<>(idList);
        while(!summonerIds.isEmpty()){
            int count = 0;
            String ids = "";
            ListIterator<Long> itr = summonerIds.listIterator();
            while(itr.hasNext()){
                count++;
                if(count > APIConstants.LEAGUES_PAGE_SIZE)
                    break;

                long id = itr.next();
                itr.remove();
                ids += id + ",";
            }

            String uri = buildUri(String.format(LEAGUE_QUERY, ids));
            String entity = getEntity(uri);

            if(entity == null)
                return null;

            try{
                Map<Long, List<LeagueDto>> map = mapper.readValue(entity,
                    new TypeReference<Map<Long, List<LeagueDto>>>(){
                    });

                for(Long summonerId : idsCopy){
                    List<LeagueDto> list = map.get(summonerId);
                    if(list != null)
                        leagues.add(new League(list.get(0)));
                    else
                        leagues.add(new League(new LeagueDto()));
                }

            } catch(IOException e){
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return leagues;
    }

    private static String buildUri(String method){
        return BASE_URL + method + "?api_key=" + API_KEY;
    }

    private String getEntity(String uri) throws RiotPlsException{
        return getEntity(uri, null);
    }

    public void setInfiniteRetry(boolean infinite){
        RETRY_INFINITE = infinite;
    }

    private String retryGetEntity(JerseyWebTarget target, int tries, long start) throws RiotPlsException{
        String uri = target.getUri().toString();
        if(!RETRY_INFINITE && tries > MAX_ATTEMPTS){
            log.warning(String.format("Maximum attempts for uri %s failed", uri));
            throw new RiotPlsException(APIConstants.HTTP_RATELIMIT, uri, System.currentTimeMillis() - start);
        }

        try{
            Thread.sleep(ATTEMPT_INTERVAL);
        } catch(InterruptedException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        Response response = target.request().get();
        int status = response.getStatus();

        if(status == APIConstants.HTTP_OK){
            long time = System.currentTimeMillis() - start;
            log.info(String.format("Successful retry for uri %s in %d ms.", uri, time));
            return response.readEntity(String.class);
        } else if(status == APIConstants.HTTP_RATELIMIT){
            log.warning(String.format("Attempt %d for uri %s failed", tries, uri));
            return retryGetEntity(target, tries + 1, start);
        } else
            throw new RiotPlsException(status, uri, System.currentTimeMillis() - start);
    }

    private String getEntity(String uri, Map<String, String> params) throws RiotPlsException{
        long start = System.currentTimeMillis();
        JerseyWebTarget target = query(uri, params);
        Response response = target.request().get();
        String uriStr = target.getUri().toString();
        int status = response.getStatus();
        long time = System.currentTimeMillis() - start;

        if(status == APIConstants.HTTP_OK){
            log.info("Success for uri " + uriStr + " in " + time + " ms.");
            return response.readEntity(String.class);
        } else if(status == APIConstants.HTTP_RATELIMIT){
            log.warning("429 Ratelimit hit oops | URI: " + uriStr);
            return retryGetEntity(target, 1, start);
        } else if(status == APIConstants.HTTP_NOT_FOUND){
            log.warning("404 not found | URI: " + uriStr);
            return null;
        } else{
            RiotPlsException e = new RiotPlsException(status, uriStr, time);
            log.warning(e.getMessage());
            throw e;
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
        Neo4jRiotAPIImpl r = new Neo4jRiotAPIImpl();
        try{
            System.out.println(r.searchSummoner("Zedenstein"));
            System.out.println(r.getMatchHistory(31569637));
            List<Long> summoners = new LinkedList<>();
            summoners.add(26106125L);
            summoners.add(20389591L);
            System.out.println(r.getLeagues(summoners));
            System.out.println(r.getRankedMatches(31569637));
            System.out.println(r.getRankedMatch(1719650755));
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }
}
