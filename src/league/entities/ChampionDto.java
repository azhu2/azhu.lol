package league.entities;

import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

import league.entities.ImageDto;
import league.neo4j.entities.Views;

public class ChampionDto{
    private int id;
    @JsonView(Views.RestView.class)
    private ImageDto image;
    private String name;
    private String title;
    private String key;

    public ChampionDto(){
        
    }
    
    public ChampionDto(int id, ImageDto image, String name, String title, String key){
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.title = title;
        this.key = key;
    }

    public ChampionDto(Node node){
        setId((int)(long) node.getProperty("id"));
        setName((String) node.getProperty("name"));
        setTitle((String) node.getProperty("title"));
        setKey((String) node.getProperty("key"));
    }

    @Override
    public String toString(){
        return id + ": Champion " + name;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ChampionDto other = (ChampionDto) obj;
        if(id != other.id)
            return false;
        return true;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public ImageDto getImage(){
        return image;
    }

    public void setImage(ImageDto image){
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

}
