package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import league.entities.ChampionDto;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;

public class AnalysisUtils{
    /**
     * Separate a list of matches by champion played. Deals with RankedMatchImpl
     */
    public static Map<ChampionDto, List<Match>> getChampMatches(Collection<Match> matchList){
        Map<ChampionDto, List<Match>> map = new HashMap<>();

        for(Match game : matchList){
            RankedMatchImpl match = (RankedMatchImpl) game;
            ChampionDto champ = match.getQueryPlayer().getChampion();
            if(map.keySet().contains(champ))
                map.get(champ).add(match);
            else{
                List<Match> list = new LinkedList<>();
                list.add(match);
                map.put(champ, list);
            }
        }

        return map;
    }

    /** Deals with RankedMatch4j */
    public static Map<ChampionDto, List<Match>> getChampMatches(Collection<Match> matchList, long summonerId){
        Map<ChampionDto, List<Match>> map = new HashMap<>();

        for(Match match : matchList){
            ChampionDto champ = getLookupPlayer(match, summonerId).getChampion();
            for(MatchPlayer player : match.getPlayers()){
                if(player.getSummoner().getId() == summonerId)
                    champ = player.getChampion();
            }
            if(map.keySet().contains(champ))
                map.get(champ).add(match);
            else{
                List<Match> list = new LinkedList<>();
                list.add(match);
                map.put(champ, list);
            }
        }

        return map;
    }

    public static MatchPlayer getLookupPlayer(Match match, long summonerId){
        for(MatchPlayer player : match.getPlayers()){
            if(player.getSummoner().getId() == summonerId)
                return player;
        }
        return null;
    }

    public static Map<String, List<Match>> filterByQueueType(Collection<Match> matchList){
        Map<String, List<Match>> filtered = new HashMap<>();

        for(Match match : matchList){
            String type = match.getQueueType();
            if(filtered.keySet().contains(type))
                filtered.get(type).add(match);
            else{
                List<Match> list = new LinkedList<>();
                list.add(match);
                filtered.put(type, list);
            }
        }

        return filtered;
    }

    /** Deals with RankedMatch4j */
    public static SummaryData getSummary(Collection<Match> matchList, long summonerId){
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addMatch(match, summonerId);
        return data;
    }

    /** Deals with RankedMatchImpl */
    public static SummaryData getSummary(Collection<Match> matchList){
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addMatch(match);
        return data;
    }
}
