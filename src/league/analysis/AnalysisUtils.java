package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import league.entities.ChampionDto;
import league.entities.azhu.RankedMatch;

public class AnalysisUtils{
    /**
     * Get a list of all matches for a given champion
     */
    public static List<RankedMatch> getChampMatches(Collection<RankedMatch> matchList, long championId){
        List<RankedMatch> matches = new LinkedList<>();
        for(RankedMatch match : matchList)
            if(match.getQueryPlayer().getChampion().getId() == championId)
                matches.add(match);
        return matches;
    }

    /**
     * Get a list of all matches for a given champion
     */
    public static List<RankedMatch> getChampMatches(Collection<RankedMatch> matchList, ChampionDto champion){
        return getChampMatches(matchList, champion.getId());
    }

    /**
     * Separate a list of matches by champion played
     */
    public static Map<ChampionDto, List<RankedMatch>> getChampMatches(Collection<RankedMatch> matchList){
        Map<ChampionDto, List<RankedMatch>> map = new HashMap<>();

        for(RankedMatch match : matchList){
            ChampionDto champ = match.getQueryPlayer().getChampion();
            if(map.keySet().contains(champ))
                map.get(champ).add(match);
            else{
                List<RankedMatch> list = new LinkedList<>();
                list.add(match);
                map.put(champ, list);
            }
        }

        return map;
    }

    public static Map<String, List<RankedMatch>> filterByQueueType(Collection<RankedMatch> matchList){
        Map<String, List<RankedMatch>> filtered = new HashMap<>();

        for(RankedMatch match : matchList){
            String type = match.getQueueType();
            if(filtered.keySet().contains(type))
                filtered.get(type).add(match);
            else{
                List<RankedMatch> list = new LinkedList<>();
                list.add(match);
                filtered.put(type, list);
            }
        }

        return filtered;
    }

    public static SummaryData getSummary(Collection<RankedMatch> matchList){
        SummaryData data = new SummaryData();
        for(RankedMatch match : matchList)
            data.addMatch(match);
        return data;
    }
}
