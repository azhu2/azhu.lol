package league.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import league.api.RiotAPI;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Path("/")
public class LeagueResource{
    private static RiotAPI api = RiotAPI.getInstance();
    private static ObjectMapper mapper = new ObjectMapper();

    public LeagueResource(){
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @GET
    @Path("/summoner/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoner(@PathParam("name") String name) throws ServletException,
            IOException{
        SummonerDto summoner = api.searchSummoner(name);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(summoner)).build();
    }

    @GET
    @Path("/summoner/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerById(@PathParam("id") long id) throws ServletException,
            IOException{
        SummonerDto summoner = api.getSummonerFromId(id);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(summoner)).build();
    }

    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long id) throws ServletException,
            IOException{
        List<MatchSummary> history = api.getRankedMatches(id);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(history)).build();
    }

    @GET
    @Path("/match-history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistory(@PathParam("id") long id) throws ServletException,
            IOException{
        Set<GameDto> history = api.getMatchHistory(id);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(history)).build();
    }

    @GET
    @Path("/champion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChampFromId(@PathParam("id") long id) throws ServletException,
            IOException{
        ChampionDto history = api.getChampFromId(id);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(history)).build();
    }

    @GET
    @Path("/match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long id) throws ServletException,
            IOException{
        MatchDetail history = api.getMatchDetail(id);
        return Response.status(HttpServletResponse.SC_OK)
                       .entity(mapper.writeValueAsString(history)).build();
    }
}
