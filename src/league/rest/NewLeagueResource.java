package league.rest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import league.api.APIConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.azhu.RankedMatch;

/**
 * An updated version of LeagueResource that does the processing on the back-end instead of in js
 * 
 * Summoner stuff is untouched
 */
@Path("/new/")
public class NewLeagueResource extends LeagueResource{
    private static NewDatabaseAPIImpl api_new = NewDatabaseAPIImpl.getInstance();

    public NewLeagueResource(){
        super();
    }

    @GET
    @Path("/ranked-match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long matchId, @QueryParam("summonerId") long summonerId) throws ServletException, IOException{
        try{
            RankedMatch match = api_new.getRankedMatch(matchId, summonerId);
            if(match != null)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(match)).build();
            
            MatchDetail detail = api.getMatchDetail(matchId);
            match = new RankedMatch(detail, summonerId);
            api_new.cacheRankedMatch(match);
            
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
            List<MatchSummary> oldApiResults = api.getRankedMatches(summonerId);
            List<RankedMatch> matches = new LinkedList<>();
            for(MatchSummary summary : oldApiResults){
                long matchId = summary.getMatchId();
                RankedMatch match = api_new.getRankedMatch(matchId, summonerId);
                if(match == null){
                    MatchDetail detail = api.getMatchDetail(matchId);
                    match = new RankedMatch(detail, summonerId);
                    api_new.cacheRankedMatch(match);
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
        try{
            List<MatchSummary> oldApiResults = api.getAllRankedMatches(summonerId);
            List<RankedMatch> matches = new LinkedList<>();
            for(MatchSummary summary : oldApiResults){
                long matchId = summary.getMatchId();
                RankedMatch match = api_new.getRankedMatch(matchId, summonerId);
                if(match == null){
                    MatchDetail detail = api.getMatchDetail(matchId);
                    match = new RankedMatch(detail, summonerId);
                    api_new.cacheRankedMatch(match);
                }
                matches.add(match);
            }

            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
}
