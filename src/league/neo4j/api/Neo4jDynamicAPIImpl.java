package league.neo4j.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;

public class Neo4jDynamicAPIImpl implements Neo4jAPI{
    private static Neo4jDatabaseAPI api_db = Neo4jDatabaseAPIImpl.getInstance();
    private static Neo4jAPI api_riot = Neo4jRiotAPIImpl.getInstance();
    private static Logger log = Logger.getLogger(Neo4jDynamicAPIImpl.class.getName());

    private static Neo4jDynamicAPIImpl _instance = new Neo4jDynamicAPIImpl();

    public static Neo4jDynamicAPIImpl getInstance(){
        return _instance;
    }
    
    private Neo4jDynamicAPIImpl(){

    }
    
    @Override
    public ChampionDto getChampionFromId(long champId) throws RiotPlsException{
        ChampionDto result = api_db.getChampionFromId(champId);
        if(result != null)
            return result;
        result = api_riot.getChampionFromId(champId);
        if(result != null)
            api_db.cacheChampion(result);
        return result;
    }

    @Override
    public ItemDto getItemFromId(long itemId) throws RiotPlsException{
        ItemDto result = api_db.getItemFromId(itemId);
        if(result != null)
            return result;
        result = api_riot.getItemFromId(itemId);
        if(result != null)
            api_db.cacheItem(result);
        return result;
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId) throws RiotPlsException{
        SummonerSpellDto result = api_db.getSummonerSpellFromId(spellId);
        if(result != null)
            return result;
        result = api_riot.getSummonerSpellFromId(spellId);
        if(result != null)
            api_db.cacheSummonerSpell(result);
        return result;
    }

    @Override
    public Summoner getSummonerFromId(long summonerId) throws RiotPlsException{
        Summoner result = api_db.getSummonerFromId(summonerId);
        if(result != null)
            return result;
        result = api_riot.getSummonerFromId(summonerId);
        if(result != null)
            api_db.cacheSummoner(result);
        return result;
    }

    @Override
    public List<Summoner> getSummoners(List<Long> summonerIds) throws RiotPlsException{
        List<Summoner> dbResults = api_db.getSummoners(summonerIds);

        if(dbResults == null)
            dbResults = new LinkedList<>();
        if(summonerIds.isEmpty())
            return dbResults;

        try{
            List<Summoner> apiResults = api_riot.getSummoners(summonerIds);
            if(apiResults != null){
                for(Summoner apiSummoner : apiResults){
                    dbResults.add(apiSummoner);
                    api_db.cacheSummoner(apiSummoner);
                }
                return dbResults;
            }
            return dbResults;
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
            return dbResults;
        }
    }

    @Override
    public Summoner searchSummoner(String summonerName) throws RiotPlsException{
        Summoner result = api_db.searchSummoner(summonerName);
        if(result != null)
            return result;
        result = api_riot.searchSummoner(summonerName);
        api_db.cacheSummoner(result);
        return result;
    }

    @Override
    public League getLeague(long summonerId) throws RiotPlsException{
        League result = api_db.getLeague(summonerId);
        if(result != null)
            return result;
        result = api_riot.getLeague(summonerId);
        return result;
    }

    @Override
    public List<League> getLeagues(List<Long> idList) throws RiotPlsException{
        return api_riot.getLeagues(idList);
    }

    @Override
    public Match getRankedMatch(long matchId) throws RiotPlsException{
        Match result = api_db.getRankedMatch(matchId);
        if(result != null)
            return result;
        result = api_riot.getRankedMatch(matchId);
        if(result != null)
            api_db.cacheRankedMatch(result);
        return result;
    }

    @Override
    public List<Match> getRankedMatches(long summonerId) throws RiotPlsException{
        List<Match> riotResults = api_riot.getRankedMatches(summonerId);
        for(Match match : riotResults)
            api_db.cacheRankedMatch(match);
        return riotResults;
    }

    @Override
    public List<Match> getAllRankedMatches(long summonerId) throws RiotPlsException{
        List<Match> riotResults = api_riot.getRankedMatches(summonerId);
        for(Match match : riotResults)
            api_db.cacheRankedMatch(match);

        List<Match> dbResults = api_db.getRankedMatches(summonerId);
        return dbResults;
    }

    @Override
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Match getGame(long matchId, long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Match> getMatchHistory(long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Match> getAllGames(long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

}
