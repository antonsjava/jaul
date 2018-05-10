/*
 * Copyright 2015 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.jaul.util;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for mapping exception to RuntimeException and throwing it.
 * @author antons
 */
public class Throw {
    private static Logger log = Logger.getLogger(Throw.class.getName());

    private static void logEx(Throwable t, boolean logMessage, boolean logStackTrace) {
        if(logStackTrace) {
            log.log(Level.WARNING, "Throwing err: " + t, t);
        } else {
            if(logMessage) {
                log.log(Level.WARNING, "Throwing err: " + t);
            }
        }
    }

    /**
     * Mapping exception to IllegalStateException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     * @param logException - true if exception should be logged 
     * @param logStackTrace - true is exception stack trace should be logged
     */
    public static void state(Throwable t, String message, boolean logException, boolean logStackTrace) {
        logEx(t, logException, logStackTrace);
        if(t instanceof RuntimeException) throw (RuntimeException)t;
        if(t instanceof Error) throw (Error)t;
        throw new IllegalStateException(message, t);
    }
    
    /**
     * Mapping exception to IllegalStateException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     */
    public static void state(Throwable t, String message) {
        state(t, message, false, false);
    }
    
    /**
     * Mapping exception to IllegalStateException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     */
    public static void state(Throwable t) {
        state(t, null, false, false);
    }
    
    /**
     * Mapping exception to IllegalArgumentException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     * @param logException - true if exception should be logged 
     * @param logStackTrace - true is exception stack trace should be logged
     */
    public static void argument(Throwable t, String message, boolean logException, boolean logStackTrace) {
        logEx(t, logException, logStackTrace);
        if(t instanceof RuntimeException) throw (RuntimeException)t;
        if(t instanceof Error) throw (Error)t;
        throw new IllegalArgumentException(message, t);
    }
    
    /**
     * Mapping exception to IllegalArgumentException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     */
    public static void argument(Throwable t, String message) {
        state(t, message, false, false);
    }
    
    /**
     * Mapping exception to IllegalArgumentException and throws it. 
     * (Error and RuntimeException are simply rethrown) 
     * @param t exception to be thrown
     * @param message - message for new created exception
     */
    public static void argument(Throwable t) {
        state(t, null, false, false);
    }
    
    
}
