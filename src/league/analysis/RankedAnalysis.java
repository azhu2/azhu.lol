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
        List<Match> queueFiltered = AnalysisUtils.filterByQueueType(matchList).get(LeagueConstants.SOLO_QUEUE_5v5);
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<Match>> champMatches = AnalysisUtils.getChampMatches(queueFiltered);

        for(ChampionDto champ : champMatches.keySet()){
            List<Match> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getSummary(matches);
            data.setChampion(champ);
            champData.put(champ, data);
        }

        return champData.values();
    }

    /** Deals with RankedMatch4j */
    public static Collection<SummaryData> getChampData(Collection<Match> matchList, long summonerId){
        List<Match> queueFiltered = AnalysisUtils.filterByQueueType(matchList).get(LeagueConstants.SOLO_QUEUE_5v5);
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<Match>> champMatches = AnalysisUtils.getChampMatches(queueFiltered, summonerId);

        for(ChampionDto champ : champMatches.keySet()){
            List<Match> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getSummary(matches, summonerId);
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
