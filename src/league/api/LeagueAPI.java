package league.api;

import java.util.List;
import java.util.Set;

import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public interface LeagueAPI{
    public ChampionDto getChampFromId(long champId);
    public MatchDetail getMatchDetail(long matchId);
    public Set<GameDto> getMatchHistory(long summonerId);
    public List<MatchSummary> getRankedMatches(long summonerId);
    public SummonerDto getSummonerFromId(long summonerId);
    public SummonerDto searchSummoner(String summonerName);
    public SummonerSpellDto getSummonerSpellFromId(long spellId);
}
