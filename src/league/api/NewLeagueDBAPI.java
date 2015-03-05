package league.api;

import java.util.List;

import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.Game;
import league.entities.azhu.RankedMatch;
import league.entities.azhu.Summoner;

public interface NewLeagueDBAPI extends NewLeagueAPI{
    public void cacheSummoner(Summoner summoner);
    public void cacheRankedMatches(List<RankedMatch> matches);
    public void cacheGame(Game match);
    public void cacheChampion(ChampionDto champion);
    public void cacheItem(ItemDto item);
    public void cacheSummonerSpell(SummonerSpellDto spell);
}
