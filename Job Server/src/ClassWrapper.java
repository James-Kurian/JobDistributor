import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClassWrapper implements Serializable{
    
    private byte[] clazzBytes;
    private String instance;
    private Class<?> clazz;
    private Object loadedInstance;

    public ClassWrapper(Class<?> clazz, Object instance) throws IOException{
        SendableClass classLoader = new SendableClass();
        this.clazzBytes = classLoader.getClassBytes(clazz.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.instance = gson.toJson(instance);
        
    }
    public void loadClazz(){
        SendableClass classLoader = new SendableClass();
        this.clazz = (Class<?>) classLoader.loadClassFromBytes(clazzBytes);
    }

    public void loadInstance(){
        Gson gson = new Gson();
        this.loadedInstance = gson.fromJson(instance, clazz);

    }
    public void freeMemory() {
        clazzBytes = null;
        clazz = null;
        instance = null;
        loadedInstance = null;
        
    }
   

    public Object runMethod(String methodName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodName).invoke(clazz.cast(loadedInstance));
    }
    public Object runMethod(String methodName, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodName).invoke(clazz.cast(loadedInstance), args);
    }

    public Object runStaticMethod(String methodName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodName).invoke(null);
    }
    public Object runStaticMethod(String methodName, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodName).invoke(null, args);
    }


}