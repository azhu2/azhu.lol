package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import league.LeagueConstants;
import league.entities.ChampionDto;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;

public class AnalysisUtils{
    /**
     * Separate a list of matches by champion played. Deals with RankedMatchImpl
     */
    public static Map<ChampionDto, List<Match>> getChampMatches(Collection<Match> matchList){
        if(matchList == null || matchList.isEmpty())
            return null;
        
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

    /** Deals with Ranked/GeneralMatch4j */
    public static Map<ChampionDto, List<Match>> getChampMatches(Collection<Match> matchList, long summonerId){
        if(matchList == null || matchList.isEmpty())
            return null;
        
        Map<ChampionDto, List<Match>> map = new HashMap<>();

        List<Match> totals = new LinkedList<>();
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
            totals.add(match);
        }
        map.put(null, totals);          // null key for totals

        return map;
    }

    public static MatchPlayer getLookupPlayer(Match match, long summonerId){
        for(MatchPlayer player : match.getPlayers()){
            if(player.getSummoner().getId() == summonerId)
                return player;
        }
        return null;
    }

    /** Filter general matches (combine some groups, ignore others) */
    public static Map<String, List<Match>> filterByCustomQueueType(Collection<Match> matchList){
        if(matchList == null || matchList.isEmpty())
            return null;
        
        Map<String, List<Match>> filtered = new HashMap<>();

        for(Match match : matchList){
            String type = match.getQueueType();
            switch(type){
                case LeagueConstants.SOLO_QUEUE_5v5:
                    continue;
                case LeagueConstants.RANKED_TEAM_5v5:
                    continue;
                case LeagueConstants.RANKED_TEAM_3v3:
                    continue;
                case LeagueConstants.BOT_3v3:
                    continue;
                case LeagueConstants.BOT_5v5:
                    continue;
                case LeagueConstants.CUSTOM:
                    continue;
                case LeagueConstants.TEAM_BUILDER_5v5:
                    type = LeagueConstants.NORMAL_5v5;
                    break;
            }
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
    
    public static Map<String, List<Match>> filterByQueueType(Collection<Match> matchList){
        if(matchList == null || matchList.isEmpty())
            return null;
        
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
    public static SummaryData getRankedSummary(Collection<Match> matchList, long summonerId){
        if(matchList == null || matchList.isEmpty())
            return null;
        
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addRankedMatch4j(match, summonerId);
        return data;
    }

    /** Deals with RankedMatchImpl */
    public static SummaryData getRankedSummary(Collection<Match> matchList){
        if(matchList == null || matchList.isEmpty())
            return null;
        
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addRankedMatchImpl(match);
        return data;
    }
    
    /** Deals with GeneralMatch4j */
    public static SummaryData getGeneralSummary(Collection<Match> matchList, long summonerId){
        if(matchList == null || matchList.isEmpty())
            return null;
        
        SummaryData data = new SummaryData();
        for(Match match : matchList)
            data.addGeneralMatch4j(match, summonerId);
        return data;
    }
}
