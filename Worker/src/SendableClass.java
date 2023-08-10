import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SendableClass extends ClassLoader {
    public Class<?> loadClassFromBytes(byte[] classData) {
        return defineClass(null, classData, 0, classData.length);
    }
    public byte[] getClassBytes(String className) throws IOException {
        String classFileName = className.replace('.', '/') + ".class";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classFileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }
}
