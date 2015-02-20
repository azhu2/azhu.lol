package league.rest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import league.api.APIConstants;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.azhu.RankedMatch;

/**
 * An updated version of LeagueResource that does the processing on the back-end instead of in js
 * 
 *  Summoner stuff is untouched
 */
@Path("/new/")
public class NewLeagueResource extends LeagueResource{

    public NewLeagueResource(){
        super();
    }
    
    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            List<MatchSummary> history = api.getRankedMatches(summonerId);
            List<RankedMatch> matches = new LinkedList<>();
            for(MatchSummary summary : history){
                long matchId = summary.getMatchId();
                MatchDetail detail = api.getMatchDetail(matchId);
                matches.add(new RankedMatch(detail));
            }
            
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(matches)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
}
