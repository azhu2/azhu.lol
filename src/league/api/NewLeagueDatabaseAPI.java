package league.api;

import java.util.List;

import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.GeneralMatchImpl;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.Summoner;

public interface NewLeagueDatabaseAPI extends NewLeagueAPI{
    public void cacheSummoner(Summoner summoner);
    public void cacheRankedMatches(List<RankedMatchImpl> matches);
    public void cacheGame(GeneralMatchImpl match);
    public void cacheChampion(ChampionDto champion);
    public void cacheItem(ItemDto item);
    public void cacheSummonerSpell(SummonerSpellDto spell);
}
