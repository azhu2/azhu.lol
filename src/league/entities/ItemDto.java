package league.entities;

import java.util.List;
import java.util.Map;

import league.neo4j.entities.Views;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

@JsonIgnoreProperties(value = {"colloq", "consumeOnFull", "consumed", "depth", "effect", "from", "group",
        "hideFromAll", "inStore", "info", "maps", "requiredChampion", "sanitizedDescription", "specialRecipe",
        "stacks", "tags"})
public class ItemDto{
    private String colloq;
    private boolean consumeOnFull;
    private boolean consumed;
    private int depth;
    private String description;
    private Map<String, String> effect;
    private List<String> from;
    // private GoldDto gold;
    private String group;
    private boolean hideFromAll;
    private int id;
    @JsonView(Views.RestView.class)
    private ImageDto image;
    private boolean inStore;
    private List<String> info;
    private Map<String, Boolean> maps;
    private String name;
    private String plaintext;
    private String requiredChampion;
    // private MetaDataDto rune;
    private String sanitizedDescriptoin;
    private int sepcialRecipe;
    private int stacks;
    // private BasicDataStatasDto stats;
    private List<String> tags;

    public ItemDto(){

    }

    public ItemDto(String description, int id, ImageDto image, String name, String plaintext){
        this.description = description;
        this.id = id;
        this.image = image;
        this.name = name;
        this.plaintext = plaintext;
    }

    public ItemDto(Node node){
        setDescription((String) node.getProperty("description"));
        setId((int) (long) node.getProperty("id"));
        setName((String) node.getProperty("name"));
        if(node.hasProperty("plaintext"))
            setPlaintext((String) node.getProperty("plaintext"));
        else
            setPlaintext("plaintext missing. riot pls");
    }

    public String getColloq(){
        return colloq;
    }

    public void setColloq(String colloq){
        this.colloq = colloq;
    }

    public boolean isConsumeOnFull(){
        return consumeOnFull;
    }

    public void setConsumeOnFull(boolean consumeOnFull){
        this.consumeOnFull = consumeOnFull;
    }

    public boolean isConsumed(){
        return consumed;
    }

    public void setConsumed(boolean consumed){
        this.consumed = consumed;
    }

    public int getDepth(){
        return depth;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Map<String, String> getEffect(){
        return effect;
    }

    public void setEffect(Map<String, String> effect){
        this.effect = effect;
    }

    public List<String> getFrom(){
        return from;
    }

    public void setFrom(List<String> from){
        this.from = from;
    }

    public String getGroup(){
        return group;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public boolean isHideFromAll(){
        return hideFromAll;
    }

    public void setHideFromAll(boolean hideFromAll){
        this.hideFromAll = hideFromAll;
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

    public boolean isInStore(){
        return inStore;
    }

    public void setInStore(boolean inStore){
        this.inStore = inStore;
    }

    public List<String> getInfo(){
        return info;
    }

    public void setInfo(List<String> info){
        this.info = info;
    }

    public Map<String, Boolean> getMaps(){
        return maps;
    }

    public void setMaps(Map<String, Boolean> maps){
        this.maps = maps;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPlaintext(){
        return plaintext;
    }

    public void setPlaintext(String plaintext){
        this.plaintext = plaintext;
    }

    public String getRequiredChampion(){
        return requiredChampion;
    }

    public void setRequiredChampion(String requiredChampion){
        this.requiredChampion = requiredChampion;
    }

    public String getSanitizedDescriptoin(){
        return sanitizedDescriptoin;
    }

    public void setSanitizedDescriptoin(String sanitizedDescriptoin){
        this.sanitizedDescriptoin = sanitizedDescriptoin;
    }

    public int getSepcialRecipe(){
        return sepcialRecipe;
    }

    public void setSepcialRecipe(int sepcialRecipe){
        this.sepcialRecipe = sepcialRecipe;
    }

    public int getStacks(){
        return stacks;
    }

    public void setStacks(int stacks){
        this.stacks = stacks;
    }

    public List<String> getTags(){
        return tags;
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }

    @Override
    public String toString(){
        return "Item " + id + ": " + name;
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
        ItemDto other = (ItemDto) obj;
        if(id != other.id)
            return false;
        return true;
    }

}
