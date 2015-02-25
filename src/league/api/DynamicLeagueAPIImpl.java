package league.api;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;

public class DynamicLeagueAPIImpl implements LeagueAPI{
    private static Logger log = Logger.getLogger(DynamicLeagueAPIImpl.class.getName());
    private static DynamicLeagueAPIImpl _instance = new DynamicLeagueAPIImpl();

    private RiotAPIImpl riotApi;
    private DatabaseAPIImpl dbApi;

    private DynamicLeagueAPIImpl(){
        riotApi = RiotAPIImpl.getInstance();
        dbApi = DatabaseAPIImpl.getInstance();
    }

    public static DynamicLeagueAPIImpl getInstance(){
        return _instance;
    }

    @Override
    public ChampionDto getChampFromId(long champId) throws RiotPlsException{
        ChampionDto result = dbApi.getChampFromId(champId);
        return result == null ? riotApi.getChampFromId(champId) : result;
    }

    @Override
    public MatchDetail getMatchDetail(long matchId) throws RiotPlsException{
        MatchDetail result = dbApi.getMatchDetail(matchId);
        return result == null ? riotApi.getMatchDetail(matchId) : result;
    }

    @Override
    public Set<GameDto> getMatchHistory(long summonerId){
        Set<GameDto> dbResults = dbApi.getMatchHistory(summonerId);

        if(dbResults == null)
            dbResults = new HashSet<>();
        Set<GameDto> apiResults;
        try{
            apiResults = riotApi.getMatchHistory(summonerId);
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
            return dbResults;
        }
        if(apiResults != null)
            for(GameDto newGame : apiResults)
                if(!dbResults.contains(newGame))
                    dbApi.cacheMatchHistoryMatch(summonerId, newGame);
        if(apiResults != null)
            dbResults.addAll(apiResults);

        return dbResults;
    }

    @Override
    public Set<GameDto> getMatchHistoryAll(long summonerId){
        Set<GameDto> dbResults = dbApi.getMatchHistoryAll(summonerId);

        if(dbResults == null)
            dbResults = new HashSet<>();
        Set<GameDto> apiResults;
        try{
            apiResults = riotApi.getMatchHistoryAll(summonerId);
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
            return dbResults;
        }
        if(apiResults != null)
            for(GameDto newGame : apiResults)
                if(!dbResults.contains(newGame))
                    dbApi.cacheMatchHistoryMatch(summonerId, newGame);
        if(apiResults != null)
            dbResults.addAll(apiResults);

        return dbResults;
    }

    @Override
    public List<MatchSummary> getRankedMatches(long summonerId) throws RiotPlsException{
        try{
            List<MatchSummary> apiResults = riotApi.getRankedMatches(summonerId);
            return apiResults;
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
            return dbApi.getRankedMatches(summonerId);
        }
    }

    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException{
        return riotApi.cacheAllRankedMatches(summonerId);
    }

    @Override
    public List<MatchSummary> getAllRankedMatches(long summonerId) throws RiotPlsException{
        List<MatchSummary> matches = dbApi.getAllRankedMatches(summonerId);
        if(matches == null)
            matches = new LinkedList<>();

        int start = 0;
        boolean apiDone = false;
        List<MatchSummary> matchPage = null;
        do{
            matchPage = riotApi.getRankedMatches(summonerId, start);
            if(matchPage != null)
                for(MatchSummary match : matchPage){
                    if(matches.contains(match)){
                        apiDone = true;
                        break;
                    } else{
                        matches.add(match);
                        dbApi.cacheRankedMatch(summonerId, match);
                    }
                }
            start += APIConstants.RANKED_PAGE_SIZE;
        } while(!apiDone && matchPage != null);

        return matches;
    }

    /**
     * If we directly search for a summoner, make an API call and update the summoner. Fail back on DB if API is down.
     */
    @Override
    public SummonerDto searchSummoner(String summonerName) throws RiotPlsException{
        SummonerDto result = riotApi.searchSummoner(summonerName);
        return result == null ? dbApi.searchSummoner(summonerName) : result;
    }

    @Override
    public SummonerDto getSummonerFromId(long summonerId) throws RiotPlsException{
        SummonerDto result = dbApi.getSummonerFromId(summonerId);
        return result == null ? riotApi.getSummonerFromId(summonerId) : result;
    }

    @Override
    public List<SummonerDto> getSummoners(List<Long> summonerIds){
        List<Long> idsCopy = new LinkedList<>(summonerIds);
        List<SummonerDto> dbResults = dbApi.getSummoners(summonerIds);

        if(dbResults == null)
            dbResults = new LinkedList<>();
        try{
            List<SummonerDto> apiResults = riotApi.getSummoners(summonerIds);
            if(apiResults != null){
                dbResults.addAll(apiResults);

                List<SummonerDto> results = new LinkedList<>();
                for(Long id : idsCopy){
                    results.add(getSummonerFromList(dbResults, id));
                }
                return results;
            }
            return dbResults;
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
            return dbResults;
        }
    }

    private SummonerDto getSummonerFromList(List<SummonerDto> list, long id){
        for(SummonerDto summoner : list)
            if(summoner.getId() == id)
                return summoner;
        return null;
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId) throws RiotPlsException{
        SummonerSpellDto result = dbApi.getSummonerSpellFromId(spellId);
        return result == null ? riotApi.getSummonerSpellFromId(spellId) : result;
    }

    @Override
    public League getLeague(long summonerId) throws RiotPlsException{
        return riotApi.getLeague(summonerId);
    }

    @Override
    public List<League> getLeagues(List<Long> ids) throws RiotPlsException{
        return riotApi.getLeagues(ids);
    }

    @Override
    public void setInifiteRetry(boolean infinite){
        riotApi.setInifiteRetry(infinite);
    }
}
