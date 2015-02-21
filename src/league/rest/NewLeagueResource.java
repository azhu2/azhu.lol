package league.rest;

import java.io.IOException;
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

import league.api.APIConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.api.RiotAPIImpl;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.azhu.Game;
import league.entities.azhu.RankedMatch;

/**
 * An updated version of LeagueResource that does the processing on the back-end instead of in js
 * 
 * Summoner stuff is untouched
 */
@Path("/new/")
public class NewLeagueResource extends LeagueResource{
    private static NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
    private static RiotAPIImpl api_riot = RiotAPIImpl.getInstance();

    public NewLeagueResource(){
        super();
    }

    @GET
    @Path("/ranked-match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long matchId, @QueryParam("summonerId") long summonerId)
            throws ServletException, IOException{
        try{
            RankedMatch match = api.getRankedMatch(matchId, summonerId);
            if(match != null)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(match)).build();

            MatchDetail detail = api.getMatchDetail(matchId);
            match = new RankedMatch(detail, summonerId);
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
            List<MatchSummary> oldApiResults = api_riot.getRankedMatches(summonerId);
            if(oldApiResults == null)
                return Response.status(APIConstants.HTTP_OK).build();

            List<RankedMatch> matches = new LinkedList<>();
            for(MatchSummary summary : oldApiResults){
                long matchId = summary.getMatchId();
                RankedMatch match = api.getRankedMatch(matchId, summonerId);
                if(match == null){
                    MatchDetail detail = api.getMatchDetail(matchId);
                    match = new RankedMatch(detail, summonerId);
                    api.cacheRankedMatch(match);
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
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        List<RankedMatch> matches = api.getRankedMatchesAll(summonerId);
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
                matchPage = api_riot.getRankedMatches(summonerId, start);
                if(matchPage != null)
                    for(MatchSummary matchSummary : matchPage){
                        long matchId = matchSummary.getMatchId();
                        RankedMatch match = api.getRankedMatch(matchId, summonerId);
                        if(match == null){
                            MatchDetail detail = api.getMatchDetail(matchId);
                            match = new RankedMatch(detail, summonerId);
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
            List<Game> matches = new LinkedList<>();
            for(GameDto gameData : oldApiResults){
                long matchId = gameData.getGameId();
                Game match = api.getGame(matchId, summonerId);
                if(match == null){
                    match = new Game(gameData, summonerId);
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
        List<Game> matches = api.getGamesAll(summonerId);
        return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
    }
}
