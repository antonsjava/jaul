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
     * @param params parameters for message {} placeholders
     * @return instance of IllegalStateException
     */
    public static RuntimeException wrapState(Throwable t, String message, Object... params) {
        return new IllegalStateException(format(message, params), t);
    }

    /**
     * Wraps given exception as IllegalStateException.
     * @param t exception to be wrapped
     * @param message - message for new created exception
     * @return instance of IllegalStateException
     */
    public static RuntimeException wrapState(Throwable t, String message) {
        return new IllegalStateException(message, t);
    }

    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     * @return instance of IllegalStateException
     */
    public static RuntimeException wrapState(Throwable t) {
        return new IllegalStateException(t);
    }

    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     * @param message - message for new created exception
     * @param params parameters for message {} placeholders
     * @return instance of IllegalStateException
     */
    public static RuntimeException wrapArgument(Throwable t, String message, Object... params) {
        return new IllegalArgumentException(format(message, params), t);
    }

    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     * @param message - message for new created exception
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException wrapArgument(Throwable t, String message) {
        return new IllegalArgumentException(message, t);
    }

    /**
     * Wraps given exception as IllegalArgumentException.
     * @param t exception to be wrapped
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException wrapArgument(Throwable t) {
        return new IllegalArgumentException(t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalStateException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @param params parameters for message {} placeholders
     * @return instance of IllegalStateException
     */
    public static RuntimeException state(Throwable t, String message, Object... params) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalStateException(format(message, params), t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalStateException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @return instance of IllegalStateException
     */
    public static RuntimeException state(Throwable t, String message) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalStateException(message, t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalStateException.
     * @param t exception to be wrapped or returned
     * @return instance of IllegalStateException
     */
    public static RuntimeException state(Throwable t) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalStateException(t);
    }

    /**
     * Factory for IllegalStateException with SLF formatting
     * @param message exceptiom message
     * @param params parameters for message {} placeholders
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException state(String message, Object... params) {
        return new IllegalArgumentException(format(message, params));
    }

    /**
     * Factory for IllegalStateException
     * @param message exceptiom message
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException state(String message) {
        return new IllegalArgumentException(message);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalArgumentException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @param params parameters for message {} placeholders
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException argument(Throwable t, String message, Object... params) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalArgumentException(format(message, params), t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalArgumentException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException argument(Throwable t, String message) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalArgumentException(message, t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as IllegalArgumentException.
     * @param t exception to be wrapped or returned
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException argument(Throwable t) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new IllegalArgumentException(t);
    }

    /**
     * Factory for IllegalArgumentException with SLF formatting
     * @param message exceptiom message
     * @param params parameters for message {} placeholders
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException argument(String message, Object... params) {
        return new IllegalArgumentException(format(message, params));
    }

    /**
     * Factory for IllegalArgumentException
     * @param message exceptiom message
     * @return instance of IllegalArgumentException
     */
    public static RuntimeException argument(String message) {
        return new IllegalArgumentException(message);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as RuntimeException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @param params parameters for message {} placeholders
     * @return instance of RuntimeException
     */
    public static RuntimeException of(Throwable t, String message, Object... params) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new RuntimeException(format(message, params), t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as RuntimeException.
     * @param t exception to be wrapped or returned
     * @param message - message for new created exception
     * @return instance of RuntimeException
     */
    public static RuntimeException of(Throwable t, String message) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new RuntimeException(message, t);
    }

    /**
     * If given exception is RuntimeException returns that exception
     * otherwise wrap it as RuntimeException.
     * @param t exception to be wrapped or returned
     * @return instance of RuntimeException
     */
    public static RuntimeException of(Throwable t) {
        if(t instanceof RuntimeException) return (RuntimeException)t;
        return new RuntimeException(t);
    }

    /**
     * Factory for RuntimeException with SLF formatting
     * @param message exceptiom message
     * @param params parameters for message {} placeholders
     * @return instance of RuntimeException
     */
    public static RuntimeException of(String message, Object... params) {
        return new RuntimeException(format(message, params));
    }

    /**
     * Factory for RuntimeException
     * @param message exceptiom message
     * @return instance of RuntimeException
     */
    public static RuntimeException of(String message) {
        return new RuntimeException(message);
    }


    private static String format(String msg, Object[] params) {
        if(params != null) {
            StringBuilder sb = new StringBuilder();
            int pos = 0;
            for(Object param : params) {
                int newpos = msg.indexOf("{}", pos);
                if(newpos < 0) break;
                sb.append(msg.substring(pos, newpos));
                sb.append(param);
                pos = newpos + 2;
            }
            sb.append(msg.substring(pos));
            msg = sb.toString();
        }
        return msg;
    }
}
