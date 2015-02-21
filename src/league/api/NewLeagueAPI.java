package league.api;

import java.util.List;

import league.entities.azhu.Game;
import league.entities.azhu.RankedMatch;

public interface NewLeagueAPI extends LeagueAPI{
    public void cacheRankedMatch(RankedMatch match);
    public RankedMatch getRankedMatch(long matchId, long summonerId);
    boolean hasRankedMatch(RankedMatch match);
    public Game getGame(long matchId, long summonerId);
    public void cacheGame(Game match);
    boolean hasGame(Game game);
    List<RankedMatch> getRankedMatchesAll(long summonerId);
    List<Game> getGamesAll(long summonerId);
}
