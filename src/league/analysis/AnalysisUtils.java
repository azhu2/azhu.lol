package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import league.entities.ChampionDto;
import league.entities.azhu.Match;
import league.entities.azhu.RankedMatchImpl;

public class AnalysisUtils{
    /**
     * Get a list of all matches for a given champion
     */
    public static List<Match> getChampMatches(Collection<Match> matchList, long championId){
        List<Match> matches = new LinkedList<>();
        for(Match game : matchList){
            if(game instanceof RankedMatchImpl){
                RankedMatchImpl match = (RankedMatchImpl) game;
                if(match.getQueryPlayer().getChampion().getId() == championId)
                    matches.add(match);
            }
        }
        return matches;
    }

    /**
     * Get a list of all matches for a given champion
     */
    public static List<Match> getChampMatches(Collection<Match> matchList, ChampionDto champion){
        return getChampMatches(matchList, champion.getId());
    }

    /**
     * Separate a list of matches by champion played
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

    public static SummaryData getSummary(Collection<Match> matchList){
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addMatch(match);
        return data;
    }
}
