package server.api;

import server.util.FileManager;
import server.util.JavaClassLoader;

import java.io.File;

public class ApiHandler {
    private File webRoot;

    public ApiHandler(File webRoot) {
        this.webRoot = webRoot;
    }

    public boolean canHandle(File file) {
        return isApiFile(file) && hasApiResource(file);
    }

    private boolean isApiFile(File file) {
        return FileManager.getFileExtension(file).equals(".api");
    }

    private boolean hasApiResource(File file) {
        String className = FileManager.getFileName(file);
        return true;
    }

    public String handle(File file, String param) {
        String className = FileManager.getFileName(file);
        JavaClassLoader classLoader = new JavaClassLoader();
        Object instance = classLoader.getInstance(className, webRoot);

        if (instance == null)
            return "";

        Object obj = classLoader.invokeMethod(instance, "start", param);
        try {
            return (String) obj;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return "";
        }
    }

}