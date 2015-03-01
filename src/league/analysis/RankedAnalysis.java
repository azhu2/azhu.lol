package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.entities.ChampionDto;
import league.entities.azhu.RankedMatch;

public class RankedAnalysis{
    public static Map<ChampionDto, SummaryData> getChampData(Collection<RankedMatch> matchList){
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<RankedMatch>> champMatches = AnalysisUtils.getChampMatches(matchList);
        
        for(ChampionDto champ : champMatches.keySet()){
            List<RankedMatch> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getSummary(matches);
            champData.put(champ, data);
        }
        
        return champData;
    }
    
    public static void main(String[] args){
        NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
        List<RankedMatch> matches = api.getRankedMatchesAll(31569637);
        Map<ChampionDto, SummaryData> data = RankedAnalysis.getChampData(matches);
        System.out.println(data);
    }
}
