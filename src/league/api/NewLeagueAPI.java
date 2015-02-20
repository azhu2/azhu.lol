package league.api;

import league.entities.azhu.RankedMatch;

public interface NewLeagueAPI extends LeagueAPI{
    public boolean cacheRankedMatch(RankedMatch match);
    public RankedMatch getRankedMatch(long matchId, long summonerId);
}
