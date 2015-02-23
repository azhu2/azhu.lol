package league.entities;

public class MiniSeriesDto{
    private int losses;
    private String progress;
    private int target;
    private int wins;

    public MiniSeriesDto(int losses, String progress, int target, int wins){
        super();
        this.losses = losses;
        this.progress = progress;
        this.target = target;
        this.wins = wins;
    }

    public MiniSeriesDto(){

    }

    public int getLosses(){
        return losses;
    }

    public void setLosses(int losses){
        this.losses = losses;
    }

    public String getProgress(){
        return progress;
    }

    public void setProgress(String progress){
        this.progress = progress;
    }

    public int getTarget(){
        return target;
    }

    public void setTarget(int target){
        this.target = target;
    }

    public int getWins(){
        return wins;
    }

    public void setWins(int wins){
        this.wins = wins;
    }

}
