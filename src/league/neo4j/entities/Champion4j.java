package league.neo4j.entities;

import java.io.IOException;
import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.Node;

import league.entities.ChampionDto;
import league.entities.ImageDto;

@JsonIgnoreProperties(value = {"image"})
public class Champion4j extends ChampionDto{
    private String imageString;
    private static Logger log = Logger.getLogger(Champion4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public Champion4j(ChampionDto champion){
        setId(champion.getId());
        setImage(champion.getImage());
        setKey(champion.getKey());
        setName(champion.getName());
        setTitle(champion.getTitle());
    }
    
    public Champion4j(Node node){
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
