package league.api;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public class DynamicLeagueAPIImpl implements LeagueAPI{
    private static DynamicLeagueAPIImpl _instance = new DynamicLeagueAPIImpl();

    private LeagueAPI riotApi;
    private DatabaseAPIImpl dbApi;

    private DynamicLeagueAPIImpl(){
        riotApi = RiotAPIImpl.getInstance();
        dbApi = DatabaseAPIImpl.getInstance();
    }

    public static DynamicLeagueAPIImpl getInstance(){
        return _instance;
    }

    @Override
    public ChampionDto getChampFromId(long champId){
        ChampionDto result = dbApi.getChampFromId(champId);
        return result == null ? riotApi.getChampFromId(champId) : result;
    }

    @Override
    public MatchDetail getMatchDetail(long matchId){
        MatchDetail result = dbApi.getMatchDetail(matchId);
        return result == null ? riotApi.getMatchDetail(matchId) : result;
    }

    @Override
    public Set<GameDto> getMatchHistory(long summonerId){
        Set<GameDto> dbResults = dbApi.getMatchHistory(summonerId);

        if(dbResults == null)
            dbResults = new HashSet<>();
        Set<GameDto> apiResults = riotApi.getMatchHistory(summonerId);
        for(GameDto newGame : apiResults)
            if(!dbResults.contains(newGame))
                dbApi.cacheMatchHistoryMatch(summonerId, newGame);
        if(apiResults != null)
            dbResults.addAll(apiResults);
        
        return dbResults;
    }

    @Override
    public List<MatchSummary> getRankedMatches(long summonerId){
        List<MatchSummary> result = dbApi.getRankedMatches(summonerId);
        return result == null ? riotApi.getRankedMatches(summonerId) : result;
    }

    @Override
    public SummonerDto getSummonerFromId(long summonerId){
        SummonerDto result = dbApi.getSummonerFromId(summonerId);
        return result == null ? riotApi.getSummonerFromId(summonerId) : result;
    }

    @Override
    public List<SummonerDto> getSummoners(List<Long> summonerIds){
        List<SummonerDto> dbResults = dbApi.getSummoners(summonerIds);

        if(dbResults == null)
            dbResults = new LinkedList<>();
        List<SummonerDto> apiResults = riotApi.getSummoners(summonerIds);
        if(apiResults != null)
            dbResults.addAll(apiResults);

        return dbResults;
    }

    @Override
    public SummonerDto searchSummoner(String summonerName){
        SummonerDto result = dbApi.searchSummoner(summonerName);
        return result == null ? riotApi.searchSummoner(summonerName) : result;
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId){
        SummonerSpellDto result = dbApi.getSummonerSpellFromId(spellId);
        return result == null ? riotApi.getSummonerSpellFromId(spellId) : result;
    }
}
