import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Scanner;

public class Worker {
    private static Request request;
    private static boolean isClosed;
    private static Job job;
    private static Scanner sc;
    private static DistributableTask<Object, ?, ?> methodCaller;
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalArgumentException {
        sc = new Scanner(System.in);
        System.out.print("Server IP> ");
        String jobDistributorIp = sc.nextLine();
        System.out.print("Server Port> ");
        String _jobDistributorPort = sc.nextLine(); 
        int jobDistributorPort = Integer.parseInt(_jobDistributorPort);
        Socket socket;
        try {
            socket = new Socket(jobDistributorIp, jobDistributorPort);
            ObjectOutputStream output;
            
            output = new ObjectOutputStream(socket.getOutputStream());


            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            System.out.println("The connection to " + jobDistributorIp + ":" + jobDistributorPort + " was succesful.");

            while (!isClosed){                
                request = (Request) input.readObject();
                switch(request.getCommand()) {
                    case "GIVE_CLASS": 
                        byte[] transformBytes = (byte[]) request.getData();
                        SendableClass classLoader = new SendableClass();
                        Class<?> fullClass = classLoader.loadClassFromBytes(transformBytes);
                        methodCaller = (DistributableTask<Object, ?, ?>) fullClass.getDeclaredConstructor().newInstance();
                        Job.setMethodCaller(methodCaller);
                        System.out.println("Transform has been received. Waiting for jobs...\n");
                        break;
                    case "GIVE_JOB":
                        Request jobRequest = request;
                        job = (Job) jobRequest.getData();
                        System.out.println("This computer has been assigned task " + job.getJobID() + ". ");
                        new Thread(() -> {
                            try {
                                job.compute();
                                jobRequest.setData(job);
                                sendObject(jobRequest, output);
                                job.freeMemory();
                                output.flush();
                                System.out.println("Task " + job.getJobID() + " has been completed. Waiting for next task...\n");
                            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                                isClosed = true;
                            }
                        }).start();;

                        break;
                    case "IS_ALIVE":
                        request.setData("ALIVE");
                        sendObject(request, output);
                        break;
                    case "TASK_COMPLETE":
                        isClosed = true;
                        socket.close();
                        System.out.println("All tasks have been complete. Disconnected.");
                        break;
                    default:
                        System.out.println("Server sent an unrecognized command.");
                        break;


                }

            }


        } catch (ClassNotFoundException | IOException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            System.out.println("Connection was aborted");
            isClosed = true;
        }
        
    }
    private static synchronized void sendObject(Request r, ObjectOutputStream out) throws IOException{
        out.writeObject(r);
    }

}
