package Logging;

import Main.Main;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.Level;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Reem Salah
 */
public class Loggers {

    private static Loggers jscLoggers;

    public static String separator = "--------------------------------------------------------------------------------------------------------";

    private Logger logg = null;

    public Logger getLogg() {
        return logg;
    }

    public void setLogg(Logger logg) {
        this.logg = logg;
    }

   
    public Loggers() {
        this.logg = getLogger();
    }

    /**
     * get new instance from java Logger
     *
     * @param logClass
     * @param loggingPath
     * @return
     */
    public Logger getLogger() {

        Logger logger = null;
        try {
            String pathLogXml = "G:\\log4j2.xml";
            File file = new File(pathLogXml);
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            context.setConfigLocation(file.toURI());
            logger = LogManager.getLogger("Logger");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logger;
    }

    public void logException(String className, String method,String message, Exception exception) {
        StringBuilder entityLog = new StringBuilder();
        entityLog.append("** " + className);
        entityLog.append(" >> " + method);
        entityLog.append(" message : >> " + message);
        if(exception != null){
            entityLog.append(" || ex: " + exception.toString());
        }
        logg.atLevel(Level.ERROR).log(entityLog.toString());
    }

    public void logEndOfMethod(String className, String method, String param) {
        StringBuilder entityLog = new StringBuilder();
        entityLog.append("#logEndOfMethod ");
        entityLog.append("** " + className);
        entityLog.append(" >> " + method);
        entityLog.append(" * param: " + param);
        logg.atLevel(Level.INFO).log(entityLog.toString());

    }

    public void logStartOfMethod(String className, String method, String param) {
        StringBuilder entityLog = new StringBuilder();
        entityLog.append("#logStartOfMethod ");
        entityLog.append("** " + className);
        entityLog.append(" >> " + method);
        entityLog.append(" || param: " + param);
        logg.atLevel(Level.INFO).log(entityLog.toString());
    }


    public void log(String className, String method , String message) {
        StringBuilder entityLog = new StringBuilder();
        entityLog.append("#log ");
        entityLog.append("** " + className);
        entityLog.append(" >> " + method);
        entityLog.append(" || message: " + message);
        logg.atLevel(Level.INFO).log(entityLog.toString());
    }


    public static Loggers getJSCLogger() {
        jscLoggers = new Loggers();
        return jscLoggers;
    }

}
