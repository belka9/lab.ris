import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

    public class Slf4jLogger {
        private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLogger.class);

        public static void logInfo(String message) {
            LOGGER.info(message);
        }
        public static void logWarning(String message) {
            LOGGER.warn(message);
        }
        public static void logError(String message) {
            LOGGER.error(message);
        }
    }

