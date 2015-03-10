package league.api;


public class RiotPlsException extends Exception{
    private int status;
    private String message;
    private String uriStr;

    public RiotPlsException(int statusCode, String uri, long time){
        status = statusCode;
        uriStr = uri;
        setMessage(time);
    }

    private void setMessage(long time){
        switch(status){
            case APIConstants.HTTP_UNAUTHORIZED:
                message = "401 Unauthorized - did you forget the API key? | URI: " + uriStr + " in " + time + " ms.";
                break;
            case APIConstants.HTTP_NOT_FOUND:
                message = "404 Not found | URI: " + uriStr;
                break;
            case APIConstants.HTTP_INTERNAL_SERVER_ERROR:
                message = "500 Rito pls. They broke something | URI: " + uriStr + " in " + time + " ms.";
                break;
            case APIConstants.HTTP_UNAVAILABLE:
                message = "503 Riot API unavailable | URI: " + uriStr + " in " + time + " ms.";
                break;
            default:
                message = status + " Something else broke | URI: " + uriStr + " in " + time + " ms.";
                break;
        }
    }

    @Override
    public String getMessage(){
        return message;
    }

    public int getStatus(){
        return status;
    }
}