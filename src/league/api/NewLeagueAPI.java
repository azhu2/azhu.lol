package league.api;

import java.util.List;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.azhu.Game;
import league.entities.azhu.RankedMatch;
import league.entities.azhu.Summoner;

public interface NewLeagueAPI extends LeagueAPI{
    public void cacheRankedMatch(RankedMatch match);
    public RankedMatch getRankedMatch(long matchId, long summonerId);
    boolean hasRankedMatch(RankedMatch match);
    public Game getGame(long matchId, long summonerId);
    public void cacheGame(Game match);
    boolean hasGame(Game game);
    List<RankedMatch> getRankedMatchesAll(long summonerId);
    List<Game> getGamesAll(long summonerId);
    public Summoner getSummonerNewFromId(long summonerId) throws RiotPlsException;
    public List<Summoner> getSummonersNew(List<Long> summonerIds) throws RiotPlsException;
    public Summoner searchSummonerNew(String summonerName) throws RiotPlsException;
    void cacheSummoner(Summoner summoner);
}
