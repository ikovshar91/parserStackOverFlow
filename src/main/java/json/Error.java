package json;

public class Error {
    public String message;

    public Error(Throwable t){
        if(t != null){
            message = String.format("Error: %s",t.getMessage());
        } else {
            message = "Server error";
        }
    }
}

