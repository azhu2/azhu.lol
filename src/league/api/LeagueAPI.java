package league.api;

import java.util.List;
import java.util.Set;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public interface LeagueAPI{
    public ChampionDto getChampFromId(long champId) throws RiotPlsException;
    public MatchDetail getMatchDetail(long matchId) throws RiotPlsException;   
    public Set<GameDto> getMatchHistory(long summonerId) throws RiotPlsException;   
    public Set<GameDto> getMatchHistoryAll(long summonerId) throws RiotPlsException;
    public List<MatchSummary> getRankedMatches(long summonerId) throws RiotPlsException;
    public SummonerDto getSummonerFromId(long summonerId) throws RiotPlsException;
    public List<SummonerDto> getSummoners(List<Long> summonerIds) throws RiotPlsException;
    public SummonerDto searchSummoner(String summonerName) throws RiotPlsException;
    public SummonerSpellDto getSummonerSpellFromId(long spellId) throws RiotPlsException;
    
    /**
     * @deprecated in favor of {@link NewLeagueAPI.getRankedMatchesAll()}
     */
    public List<MatchSummary> getAllRankedMatches(long summonerId) throws RiotPlsException;
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException;
}
