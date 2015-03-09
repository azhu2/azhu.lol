package league.neo4j.entities;

import java.io.IOException;
import java.util.logging.Logger;

import league.entities.ImageDto;
import league.entities.SummonerSpellDto;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

public class SummonerSpell4j extends SummonerSpellDto{
    @JsonView(Views.Neo4jView.class)
    private String imageString;
    private static Logger log = Logger.getLogger(SummonerSpell4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public SummonerSpell4j(SummonerSpellDto spell){
        setId(spell.getId());
        setImage(spell.getImage());
        setName(spell.getName());
        setDescription(spell.getDescription());
        setSummonerLevel(spell.getSummonerLevel());
        setKey(spell.getKey());
    }
    
    public SummonerSpell4j(Node node){
        super(node);
        imageString = (String) node.getProperty("imageString");
        try{
            setImage(mapper.readValue(imageString, ImageDto.class));
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }
    
    public String getImageString(){
        return imageString;
    }

    public void setImageString(String imageString){
        this.imageString = imageString;
    }

    @Override
    public void setImage(ImageDto image){
        super.setImage(image);
        try{
            imageString = mapper.writeValueAsString(image);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }
}
