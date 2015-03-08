package league.neo4j.api;

import java.util.List;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;

public interface Neo4jAPI{
    // Static data
    public ChampionDto getChampFromId(long champId) throws RiotPlsException;
    public ItemDto getItemFromId(long itemId) throws RiotPlsException;
    public SummonerSpellDto getSummonerSpellFromId(long spellId) throws RiotPlsException;
    
    // Summoners
    public Summoner getSummonerFromId(long summonerId) throws RiotPlsException;
    public List<Summoner> getSummoners(List<Long> summonerIds) throws RiotPlsException;
    public Summoner searchSummoner(String summonerName) throws RiotPlsException;
    
    // Leagues
    public League getLeague(long summonerId) throws RiotPlsException;
    public List<League> getLeagues(List<Long> idList) throws RiotPlsException;

    // Ranked matches
    public Match getRankedMatch(long matchId);
    public List<Match> getAllRankedMatches(long summonerId);
    public void cacheRankedMatch(Match match);
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException;
    
    // Games
    public Match getGame(long matchId, long summonerId);
    public List<Match> getAllGames(long summonerId);
}
