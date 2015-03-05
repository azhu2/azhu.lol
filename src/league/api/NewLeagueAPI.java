package league.api;

import java.util.List;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.azhu.GeneralMatchImpl;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.Summoner;

public interface NewLeagueAPI extends LeagueAPI{
    public void cacheRankedMatch(RankedMatchImpl match);
    public RankedMatchImpl getRankedMatch(long matchId, long summonerId);
    public boolean hasRankedMatch(RankedMatchImpl match);
    public GeneralMatchImpl getGame(long matchId, long summonerId);
    boolean hasGame(GeneralMatchImpl game);
    public List<RankedMatchImpl> getRankedMatchesAll(long summonerId);
    public List<GeneralMatchImpl> getGamesAll(long summonerId);
    public Summoner getSummonerNewFromId(long summonerId) throws RiotPlsException;
    public List<Summoner> getSummonersNew(List<Long> summonerIds) throws RiotPlsException;
    public Summoner searchSummonerNew(String summonerName) throws RiotPlsException;
    List<Summoner> getSummonersNew(List<Long> summonerIds, boolean cache) throws RiotPlsException;
}
