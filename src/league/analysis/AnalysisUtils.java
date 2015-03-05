package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import league.entities.ChampionDto;
import league.entities.azhu.RankedMatchImpl;

public class AnalysisUtils{
    /**
     * Get a list of all matches for a given champion
     */
    public static List<RankedMatchImpl> getChampMatches(Collection<RankedMatchImpl> matchList, long championId){
        List<RankedMatchImpl> matches = new LinkedList<>();
        for(RankedMatchImpl match : matchList)
            if(match.getQueryPlayer().getChampion().getId() == championId)
                matches.add(match);
        return matches;
    }

    /**
     * Get a list of all matches for a given champion
     */
    public static List<RankedMatchImpl> getChampMatches(Collection<RankedMatchImpl> matchList, ChampionDto champion){
        return getChampMatches(matchList, champion.getId());
    }

    /**
     * Separate a list of matches by champion played
     */
    public static Map<ChampionDto, List<RankedMatchImpl>> getChampMatches(Collection<RankedMatchImpl> matchList){
        Map<ChampionDto, List<RankedMatchImpl>> map = new HashMap<>();

        for(RankedMatchImpl match : matchList){
            ChampionDto champ = match.getQueryPlayer().getChampion();
            if(map.keySet().contains(champ))
                map.get(champ).add(match);
            else{
                List<RankedMatchImpl> list = new LinkedList<>();
                list.add(match);
                map.put(champ, list);
            }
        }

        return map;
    }

    public static Map<String, List<RankedMatchImpl>> filterByQueueType(Collection<RankedMatchImpl> matchList){
        Map<String, List<RankedMatchImpl>> filtered = new HashMap<>();

        for(RankedMatchImpl match : matchList){
            String type = match.getQueueType();
            if(filtered.keySet().contains(type))
                filtered.get(type).add(match);
            else{
                List<RankedMatchImpl> list = new LinkedList<>();
                list.add(match);
                filtered.put(type, list);
            }
        }

        return filtered;
    }

    public static SummaryData getSummary(Collection<RankedMatchImpl> matchList){
        SummaryData data = new SummaryData();
        for(RankedMatchImpl match : matchList)
            data.addMatch(match);
        return data;
    }
}
