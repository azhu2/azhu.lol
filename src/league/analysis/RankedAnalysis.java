package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import league.LeagueConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.entities.ChampionDto;
import league.entities.azhu.Match;

public class RankedAnalysis{

    /** Deals with RankedMatchImpl */
    public static Collection<SummaryData> getChampData(Collection<Match> matchList){
        List<Match> filteredMatches = filterMatches(matchList);
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<Match>> champMatches = AnalysisUtils.getChampMatches(filteredMatches);

        for(ChampionDto champ : champMatches.keySet()){
            List<Match> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getRankedSummary(matches);
            data.setChampion(champ);
            champData.put(champ, data);
        }

        return champData.values();
    }

    private static List<Match> filterMatches(Collection<Match> matchList){
        Map<String, List<Match>> filtered = AnalysisUtils.filterByQueueType(matchList);
        List<Match> soloQueue = filtered.get(LeagueConstants.SOLO_QUEUE_5v5);
        List<Match> ranked5s = filtered.get(LeagueConstants.RANKED_TEAM_5v5);

        if(soloQueue != null && ranked5s != null){
            soloQueue.addAll(filtered.get(LeagueConstants.RANKED_TEAM_5v5));
            return soloQueue;
        }
        if(soloQueue != null)
            return soloQueue;
        return ranked5s;
    }

    /** Deals with RankedMatch4j */
    public static Collection<SummaryData> getChampData(Collection<Match> matchList, long summonerId){
        List<Match> filteredMatches = filterMatches(matchList);
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<Match>> champMatches = AnalysisUtils.getChampMatches(filteredMatches, summonerId);
        
        if(champMatches == null || champMatches.isEmpty())
            return null;
        for(ChampionDto champ : champMatches.keySet()){
            List<Match> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getRankedSummary(matches, summonerId);
            data.setChampion(champ);
            champData.put(champ, data);
        }

        return champData.values();
    }

    public static void main(String[] args){
        NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
        List<Match> matches = api.getRankedMatchesAll(31569637);
        Collection<SummaryData> data = RankedAnalysis.getChampData(matches);
        System.out.println(data);
    }
}
