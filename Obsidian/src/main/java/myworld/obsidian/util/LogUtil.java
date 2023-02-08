package myworld.obsidian.util;


import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

    public static final String OBSIDIAN_ROOT_LOGGER = "myworld.obsidian";

    public static void initLogging(Handler... handlers){
        var logger = rootLogger();
        for(var handler : handlers){
            logger.addHandler(handler);
        }
    }

    public static Logger rootLogger(){
        return Logger.getLogger(OBSIDIAN_ROOT_LOGGER);
    }

    public static Logger loggerFor(Class<?> cls){
        return Logger.getLogger(cls.getName());
    }

    public static void setLevel(Level level){
        setLevel(rootLogger(), level);
    }

    public static void setLevel(Logger logger, Level level){
        setOutputLevel(logger, level);
        logger.setLevel(level);
    }

    public static void setOutputLevel(Logger logger, Level level){
        Arrays.stream(logger.getHandlers()).forEach(hander -> hander.setLevel(level));
    }

    public static void setLevel(Class<?> cls, Level level){
        setLevel(loggerFor(cls), level);
    }

}
