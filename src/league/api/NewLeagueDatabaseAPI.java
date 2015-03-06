package league.api;

import java.util.List;

import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;

public interface NewLeagueDatabaseAPI extends NewLeagueAPI{
    public void cacheSummoner(Summoner summoner);
    public void cacheRankedMatches(List<Match> matches);
    public void cacheGame(Match match);
    public void cacheChampion(ChampionDto champion);
    public void cacheItem(ItemDto item);
    public void cacheSummonerSpell(SummonerSpellDto spell);
}
