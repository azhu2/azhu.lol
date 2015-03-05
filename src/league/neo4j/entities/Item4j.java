package league.neo4j.entities;

import java.io.IOException;
import java.util.logging.Logger;

import league.entities.ImageDto;
import league.entities.ItemDto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.Node;

@JsonIgnoreProperties(value = {"colloq", "consumeOnFull", "consumed", "depth", "effect", "from", "group",
        "hideFromAll", "image", "inStore", "info", "maps", "requiredChampion", "sanitizedDescription", "specialRecipe",
        "stacks", "tags"})
public class Item4j extends ItemDto{
    private String imageString;
    private static Logger log = Logger.getLogger(Item4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public Item4j(ItemDto item){
        setId(item.getId());
        setImage(item.getImage());
        setName(item.getName());
        setDescription(item.getDescription());
        setPlaintext(item.getPlaintext());
    }
    
    public Item4j(Node node){
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
