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

/**
 * Helper class for converting exception to RuntimeException.
 * @author antons
 */
public class AsRuntimeEx {
    
    /**
     * Wraps given exception as IllegalStateException.
     * @param t exception to be wrapped
     * @param message - message for new created exception
     */
    public static RuntimeException wrapState(Throwable t, String message) {
        return new IllegalStateException(message, t);
    }
    
    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     */
    public static RuntimeException wrapState(Throwable t) {
        return new IllegalStateException(t);
    }
    
    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     * @param message - message for new created exception
     */
    public static RuntimeException wrapArgument(Throwable t, String message) {
        return new IllegalArgumentException(message, t);
    }
    
    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     */
    public static RuntimeException wrapArgument(Throwable t) {
        return new IllegalArgumentException(t);
    }

    /**
     * If given exception is RuntimeException returns that exception 
     * otherwise wrap it as IllegalStateException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     */
    public static RuntimeException state(Throwable t, String message) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        throw new IllegalStateException(message, t);
    }
    
    /**
     * If given exception is RuntimeException returns that exception 
     * otherwise wrap it as IllegalStateException.
     * @param t exception to be wrapped or returned
     */
    public static RuntimeException state(Throwable t) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        throw new IllegalStateException(t);
    }

    /**
     * If given exception is RuntimeException returns that exception 
     * otherwise wrap it as IllegalArgumentException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     */
    public static RuntimeException argument(Throwable t, String message) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        throw new IllegalArgumentException(message, t);
    }
    
    /**
     * If given exception is RuntimeException returns that exception 
     * otherwise wrap it as IllegalArgumentException.
     * @param t exception to be wrapped or returned
     */
    public static RuntimeException argument(Throwable t) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        throw new IllegalArgumentException(t);
    }

    
}
