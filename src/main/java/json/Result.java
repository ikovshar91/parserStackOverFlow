package json;


public class Result <T>{
    public T result;
    public Exception exception;

    public Result(T result){
        this.result = result;
    }

    public Result(Exception exception){
        this.exception = exception;
    }
}
