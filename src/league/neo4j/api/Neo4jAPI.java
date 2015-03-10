package league.neo4j.api;

import java.util.List;
import java.util.Set;

import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;

public interface Neo4jAPI{
    // Static data
    public ChampionDto getChampionFromId(int champId) throws RiotPlsException;
    public ItemDto getItemFromId(int itemId) throws RiotPlsException;
    public SummonerSpellDto getSummonerSpellFromId(int spellId) throws RiotPlsException;
    
    // Summoners
    public Summoner getSummonerFromId(long summonerId) throws RiotPlsException;
    public List<Summoner> getSummoners(List<Long> summonerIds) throws RiotPlsException;
    public Summoner searchSummoner(String summonerName) throws RiotPlsException;
    
    // Leagues
    public League getLeague(long summonerId) throws RiotPlsException;
    public List<League> getLeagues(List<Long> idList) throws RiotPlsException;

    // Ranked matches
    public Match getRankedMatch(long matchId) throws RiotPlsException;
    public List<Long> getRankedMatchIds(long summonerId) throws RiotPlsException;
    public List<Long> getAllRankedMatchIds(long summonerId) throws RiotPlsException;
    public List<Match> getRankedMatches(long summonerId) throws RiotPlsException;
    public List<Match> getAllRankedMatches(long summonerId) throws RiotPlsException;
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException;
    
    // Games
    public Match getGame(long matchId, long summonerId) throws RiotPlsException;
    public Set<Match> getMatchHistory(long summonerId) throws RiotPlsException;
    public Set<Match> getAllGames(long summonerId) throws RiotPlsException;
}
