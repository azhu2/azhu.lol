package league.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import league.analysis.RankedAnalysis;
import league.analysis.SummaryData;
import league.api.APIConstants;
import league.api.DynamicLeagueAPIImpl;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueDatabaseAPI;
import league.api.RiotAPIImpl;
import league.api.RiotPlsException;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.azhu.GeneralMatchImpl;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.Summoner;

/**
 * An updated version of LeagueResource that does the processing on the back-end instead of in js
 * 
 * Summoner stuff is untouched
 */
@Path("/new/")
public class NewLeagueResource extends LeagueResource{
    private static NewLeagueDatabaseAPI api = NewDatabaseAPIImpl.getInstance();
    private static RiotAPIImpl api_riot = RiotAPIImpl.getInstance();
    private static DynamicLeagueAPIImpl api_dynamic = DynamicLeagueAPIImpl.getInstance();

    public NewLeagueResource(){
        super();
    }

    @GET
    @Path("/ranked-match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long matchId, @QueryParam("summonerId") long summonerId)
            throws ServletException, IOException{
        try{
            RankedMatchImpl match = (RankedMatchImpl) api.getRankedMatch(matchId, summonerId);
            if(match != null)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(match)).build();

            MatchDetail detail = api_dynamic.getMatchDetail(matchId);
            match = new RankedMatchImpl(detail, summonerId);
            api.cacheRankedMatch(match);

            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(match)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @Override
    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            List<Match> matches = getRankedMatchesHelper(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    private List<Match> getRankedMatchesHelper(long summonerId) throws RiotPlsException{
        List<MatchSummary> oldApiResults = api_riot.getRankedMatches(summonerId);
        if(oldApiResults == null)
            return null;

        List<Match> matches = new LinkedList<>();
        for(MatchSummary summary : oldApiResults){
            long matchId = summary.getMatchId();
            Match match = api.getRankedMatch(matchId, summonerId);
            if(match == null){
                MatchDetail detail = api_dynamic.getMatchDetail(matchId);
                match = new RankedMatchImpl(detail, summonerId, false);
            }
            matches.add(match);

            // Limit page size since apparently doesn't work in api call
            if(matches.size() >= APIConstants.RANKED_PAGE_SIZE)
                break;
        }
        api.cacheRankedMatches(matches);

        return matches;
    }

    @Override
    @GET
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        List<Match> matches = api.getRankedMatchesAll(summonerId);
        return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
    }

    @POST
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheAllRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            int start = 0;
            int count = 0;

            List<MatchSummary> matchPage = null;
            do{
                matchPage = api_riot.getRankedMatches(summonerId, start, APIConstants.MAX_PAGE_SIZE);
                if(matchPage != null)
                    for(MatchSummary matchSummary : matchPage){
                        long matchId = matchSummary.getMatchId();
                        RankedMatchImpl match = (RankedMatchImpl) api.getRankedMatch(matchId, summonerId);
                        if(match == null){
                            MatchDetail detail = api_dynamic.getMatchDetail(matchId);
                            match = new RankedMatchImpl(detail, summonerId, false);
                            api.cacheRankedMatch(match);
                        }
                        count++;
                    }
                start += APIConstants.MAX_PAGE_SIZE;
            } while(matchPage != null);

            UpdateCount countObj = new UpdateCount();
            countObj.count = count;
            if(count != APIConstants.INVALID)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(countObj)).build();
            else
                return Response.status(APIConstants.HTTP_INTERNAL_SERVER_ERROR)
                               .entity("Error caching all ranked matches").build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @Override
    @GET
    @Path("/match-history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistory(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            Set<GameDto> oldApiResults = api_riot.getMatchHistory(summonerId);
            List<GeneralMatchImpl> matches = new LinkedList<>();
            if(oldApiResults != null)
                for(GameDto gameData : oldApiResults){
                    long matchId = gameData.getGameId();
                    GeneralMatchImpl match = (GeneralMatchImpl) api.getGame(matchId, summonerId);
                    if(match == null){
                        match = new GeneralMatchImpl(gameData, summonerId);
                        api.cacheGame(match);
                    }
                    matches.add(match);
                }

            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @Override
    @GET
    @Path("/match-history/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistoryAll(@PathParam("id") long summonerId) throws ServletException, IOException{
        List<Match> matches = api.getGamesAll(summonerId);
        return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
    }

    /**
     * If we search by name, force update
     */
    @GET
    @Path("/summoner/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoner(@PathParam("name") String name) throws ServletException, IOException{
        try{
            SummonerDto summ = api_riot.searchSummoner(name);
            if(summ == null)
                return null;
            League league = api_riot.getLeague(summ.getId());
            Summoner summoner = new Summoner(summ, league);
            api.cacheSummoner(summoner);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoner/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerById(@PathParam("id") long id) throws ServletException, IOException{
        try{
            SummonerDto summ = api_riot.getSummonerFromId(id);
            League league = api_riot.getLeague(summ.getId());
            Summoner summoner = new Summoner(summ, league);
            api.cacheSummoner(summoner);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    /**
     * Batch League lookups here
     */
    @GET
    @Path("/summoners/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoners(@PathParam("ids") String ids) throws ServletException, IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : ids.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<SummonerDto> summoners = api.getSummoners(idList);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoners)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ranked-stats/champions/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedStatsByChampion(@PathParam("id") long summonerId) throws ServletException, IOException{
        List<Match> matches = api.getRankedMatchesAll(summonerId);
        if(matches == null || matches.isEmpty())
            try{
                matches = getRankedMatchesHelper(summonerId);
            } catch(RiotPlsException e){
                return Response.status(APIConstants.HTTP_OK).entity("No ranked data").build();
            }
        Collection<SummaryData> champData = RankedAnalysis.getChampData(matches);
        return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(champData)).build();
    }
}
