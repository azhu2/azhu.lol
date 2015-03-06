package league.api;

import java.util.List;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;

public interface NewLeagueAPI extends LeagueAPI{
    public void cacheRankedMatch(Match match);
    public Match getRankedMatch(long matchId, long summonerId);
    public boolean hasRankedMatch(Match match);
    public Match getGame(long matchId, long summonerId);
    boolean hasGame(Match game);
    public List<Match> getRankedMatchesAll(long summonerId);
    public List<Match> getGamesAll(long summonerId);
    public Summoner getSummonerNewFromId(long summonerId) throws RiotPlsException;
    public List<Summoner> getSummonersNew(List<Long> summonerIds) throws RiotPlsException;
    public Summoner searchSummonerNew(String summonerName) throws RiotPlsException;
    List<Summoner> getSummonersNew(List<Long> summonerIds, boolean cache) throws RiotPlsException;
}
