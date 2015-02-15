package league.rest;

import java.io.IOException;
import java.util.LinkedList;
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

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Path("/")
public class LeagueResource{
    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();
    private static ObjectMapper mapper = new ObjectMapper();

    public LeagueResource(){
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @GET
    @Path("/summoner/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoner(@PathParam("name") String name) throws ServletException,
            IOException{
        try{
            SummonerDto summoner = api.searchSummoner(name);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoner/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerById(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            SummonerDto summoner = api.getSummonerFromId(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoners/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoners(@PathParam("ids") String ids) throws ServletException,
            IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : ids.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<SummonerDto> summoners = api.getSummoners(idList);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(summoners)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            List<MatchSummary> history = api.getRankedMatches(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/match-history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistory(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            Set<GameDto> history = api.getMatchHistory(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/champion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChampFromId(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            ChampionDto history = api.getChampFromId(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            MatchDetail history = api.getMatchDetail(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoner-spell/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerSpellFromId(@PathParam("id") long id) throws ServletException,
            IOException{
        try{
            SummonerSpellDto history = api.getSummonerSpellFromId(id);
            return Response.status(HttpServletResponse.SC_OK)
                           .entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
}
