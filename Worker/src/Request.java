import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Request implements Serializable{

    private UUID id;
    private transient CompletableFuture<Object> resultStorage = new CompletableFuture<>();
    private Object data;
    private String command;

    public Request(String command, Object data){
        this.id = UUID.randomUUID();
        this.data = data;
        this.command = command;
    }
    public Request(String command){
        this.id = UUID.randomUUID();
        this.command = command;
    }


    public Object waitForResponce() throws InterruptedException, ExecutionException{        
        return resultStorage.get();
    }
    public UUID ID(){
        return id;
    }
    public void setID(UUID id){
        this.id = id;
    }
    public Object getData(){
        return data;
    }
    public void storeData(Object data){
        resultStorage.complete(data);
    }
    public void setData(Object data){
        this.data = data;
    }
    public String getCommand(){
        return command;
    }
}
