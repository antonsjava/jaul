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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import sk.antons.jaul.Get;

/**
 * Produces shorter string with Throwable stack trace.
 * Stack traces can be huge. Most of the information are not
 * necessary for application debugging.
 * Usually only application project packages are important
 * in stack trace.
 *
 * This class can produce limited stack traces.
 *
 *
 * @author antons
 */
public class Stk {

    private String[] prefixes;
    private int before = 2;
    private int after = 2;

    public Stk(int before, int after, String... prefixes) {
        this.prefixes = prefixes;
        this.before = before;
        this.after = after;
    }

    public static Stk instance(int before, int after, String... prefixes) {
        return new Stk(before, after, prefixes);
    }

    public static Stk instance(String... prefixes) {
        return new Stk(2, 2, prefixes);
    }

    private static Stk tracer = null;

    public static void init(String... prefixes) {
        tracer = Stk.instance(prefixes);
    }

    public static String trace(Throwable t) {
        if(tracer == null) {
            StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            if(Get.size(stElements) > 2) {
                String clname = stElements[2].getClassName();
                int pos = clname.indexOf('.');
                if(pos > -1) {
                    int pos2 = clname.indexOf('.', pos+1);
                    if(pos2 > -1) {
                        clname = clname.substring(0, pos2);
                    } else {
                        clname = clname.substring(0, pos);
                    }
                    tracer = Stk.instance(clname);
                } else {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    return sw.toString();
                }
            } else {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                return sw.toString();
            }
        }
        return tracer.stackTrace(t);
    }

    public String stackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder(1000);
        st(sb, t, true);
        return sb.toString();
    }

    private void st(StringBuilder sb, Throwable t, boolean root) {
        if(t == null) return;
        if(!root) sb.append("\nCaused by: ");
        sb.append(t);
        StackTraceElement[] ste = t.getStackTrace();
        if(ste != null) {
            ArrayList<String> buffer = new ArrayList<String>();
            int must = 0;
            boolean first = true;
            for(StackTraceElement stackTraceElement : ste) {
                String line = stackTraceElement.toString();
                if (first) {
                    first = false;
                    must = after;
                    sb.append("\n\tat ").append(line);
                } else if(stackTraceElement.getClassName().startsWith("camp.xit")) {
                    if(buffer.size() > before) sb.append("\n\tat ...");
                    for(int i = before; i > 0; i--) {
                        int index = buffer.size() - i;
                        if(index < 0) continue;
                        sb.append("\n\tat ").append(buffer.get(index));
                    }
                    buffer.clear();
                    must = after;
                    sb.append("\n\tat ").append(line);
                } else if(must > 0) {
                    must--;
                    sb.append("\n\tat ").append(line);
                } else {
                    buffer.add(line);
                }
            }
            if(buffer.size() > 0) sb.append("\n\tat ...");
        }
        st(sb, t.getCause(), false);
    }

}
