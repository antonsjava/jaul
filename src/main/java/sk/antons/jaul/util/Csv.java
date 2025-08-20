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

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CSV files helper
 *
 * @author antons
 */
public class Csv {

    private String encoding = "utf-8";
    private int delimiter = ',';
    private int quote = '"';
    private boolean forceQuotes = false;
    private String nlreplacer = null;

    /**
     * Builder instance.
     * @return builder instance
     */
    public static Csv instance() { return new Csv(); }
    /**
     * Delimiter used. Default value is ','.
     * @param value
     * @return this
     */
    public Csv delimiter(char value) { this.delimiter = value; return this; }
    /**
     * Quote used. Default value is '"'.
     * @param value
     * @return this
     */
    public Csv quote(char value) { this.quote = value; return this; }
    /**
     * Encoding used for stream input and output. Default value is utf-8.
     * @param value
     * @return this
     */
    public Csv encoding(String value) { this.encoding = value; return this; }
    /**
     * Value used to replace new lines. Default value is null.
     * @param value
     * @return this
     */
    public Csv nlreplacer(String value) { this.nlreplacer = value; return this; }
    /**
     * if true all fields in created csv are quoted. Otherwise only necessary quotes
     * are used.
     * @param value
     * @return this
     */
    public Csv forceQuotes(boolean value) { this.forceQuotes = value; return this; }

    /**
     * Raw scanner for reading csv.
     * @param reader
     * @return scanner instance
     */
    public Scanner scanner(Reader reader) { return Scanner.instance(reader, delimiter, quote); }
    /**
     * Raw scanner for reading csv.
     * @param is
     * @return scanner instance
     */
    public Scanner scanner(InputStream is) { return Scanner.instance(toReader(is), delimiter, quote); }

    /**
     * Record iterator for reading csv.
     * @param reader
     * @return iterator instance
     */
    public Iterator<Record> iterator(Reader reader) { return new IteratorImpl(scanner(reader)); }
    /**
     * Record iterator for reading csv.
     * @param is
     * @return iterator instance
     */
    public Iterator<Record> iterator(InputStream is) { return new IteratorImpl(scanner(is)); }

    /**
     * Appender for csv creation.
     * @param appendable
     * @return appender instance
     */
    public Appender appender(Appendable appendable) { return Appender.instance(appendable, delimiter, quote, forceQuotes, nlreplacer); }
    /**
     * Appender for csv creation.
     * @param os
     * @return appender instance
     */
    public Appender appender(OutputStream os) { return Appender.instance(toWriter(os), delimiter, quote, forceQuotes, nlreplacer); }


    private Reader toReader(InputStream is) {
        try {
            return new InputStreamReader(is, encoding);
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e);
        }
    }

    private Writer toWriter(OutputStream os) {
        try {
            return new OutputStreamWriter(os, encoding);
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e);
        }
    }

    private static class BuffReader {
        int first = 0;
        int second = 0;
        Reader reader;
        private static BuffReader instance(Reader reader) {
            BuffReader rv = new BuffReader();
            rv.reader = reader;
            rv.read();
            rv.read();
            return rv;
        }

        public int read() {
            try {
                int rv = first;
                first = second;
                second = reader.read();
                return rv;
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }
        int first() { return first; }
        int second() { return second; }
    }

    /**
     * Iterate cvs stream and generates field and endofrecord eventd.
     */
    public static class Scanner {
        int delimiter = ',';
        int quote = '"';
        BuffReader reader;
        private Scanner() {}
        public static Scanner instance(Reader reader, int delimiter, int quote) {
            Scanner rv = new Scanner();
            rv.reader = BuffReader.instance(reader);
            rv.delimiter = delimiter;
            rv.quote = quote;
            return rv;
        }

        StringBuilder buff = new StringBuilder(300);
        boolean nextWillBeRecordEnd = false;
        Token lastToken = Token.RECORD_END;

        /**
         * After field event returns current value of field.
         * @return value of field
         */
        public String current() { return buff.toString(); }

        /**
         * Next event
         *
         * FIELD - for field (call current() for value)
         *
         * RECORD_END - end of record
         *
         * null - end of stream.
         *
         * @return
         */
        public Token next() {
            buff.setLength(0);
            if(nextWillBeRecordEnd) {
                nextWillBeRecordEnd = false;
                lastToken = Token.RECORD_END;
                return Token.RECORD_END;
            } else {
                lastToken = Token.FIELD;
            }
            try {
                int c = reader.read();
                if(c < 0) return null;
                if(c == delimiter) {
                    return Token.FIELD;
                } else if(c == quote) {
                    consumeQuoted();
                    return Token.FIELD;
                } else if(c == '\n') {
                    if(lastToken == Token.RECORD_END) {
                        if(reader.first() == -1) {
                            return null;
                        } else {
                            nextWillBeRecordEnd = true;
                            return Token.FIELD;
                        }
                    } else {
                        nextWillBeRecordEnd = true;
                        return Token.FIELD;
                    }
                } else if(c == '\r') {
                    if(lastToken == Token.RECORD_END) {
                        if((reader.first() == '\n') && (reader.first() == -1)) {
                            return null;
                        } else {
                            reader.read();
                            nextWillBeRecordEnd = true;
                            return Token.FIELD;
                        }
                    } else {
                        reader.read();
                        nextWillBeRecordEnd = true;
                        return Token.FIELD;
                    }
                } else {
                    buff.append((char)c);
                    consumeUnquoted();
                    return Token.FIELD;
                }
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }

        private void consumeUnquoted() {
            skipToFieldEnd(true);
        }

        private void consumeQuoted() {
            int c = reader.read();
            while((c != -1)) {
                if(c == quote) {
                    if(reader.first() == quote) {
                        reader.read();
                    } else {
                        break;
                    }
                }
                buff.append((char)c);
                c = reader.read();
            }
            skipToFieldEnd(false);
        }

        private void skipToFieldEnd(boolean store) {
            int c = reader.read();
            while((c != -1)) {
                if(c == delimiter) {
                    break;
                } else if(c == '\n') {
                    nextWillBeRecordEnd = true;
                    if(reader.first == -1) reader.read();
                    break;
                } else if(c == '\r') {
                    if(reader.first == '\n') {
                        nextWillBeRecordEnd = true;
                        reader.read();
                        if(reader.first == -1) reader.read();
                        break;
                    }
                }
                if(store) buff.append((char)c);
                c = reader.read();
            }
            if(c == -1) nextWillBeRecordEnd = true;
        }

        public static enum Token {
            FIELD, RECORD_END;
        }
    }

    /**
     * Simple wrapper for list of strings. Represents one recosrt of csv.
     */
    public static class Record {
        private int row = 0;
        private List<String> fields = new ArrayList<>();
        public Record(int row) { this.row = row;}
        public static Record instance(int row) { return new Record(row); }
        public int row() { return row; }
        public int size() { return fields.size(); }
        public String field(int index) {
            if(index < 0) return null;
            if(index >= fields.size()) return "";
            return fields.get(index);
        }
        public List<String> fields() { return fields; }
        public Record field(String value) {
            if(value == null) value = "";
            fields.add(value);
            return this;
        }
        public Record field(int index, String value) {
            if(value == null) value = "";
            if(index < 0) index = 0;
            while(fields.size() < index) fields.add("");
            fields.add(index, value);
            return this;
        }

        @Override
        public String toString() {
            return "row("+row+"): "+fields.toString();
        }

    }

    private static class IteratorImpl implements java.util.Iterator<Record> {

        private Scanner scanner;
        private Record nextone;
        int row = 0;

        private IteratorImpl(Scanner scanner) {
            this.scanner = scanner;
            nextone = nextRecord();
        }

        private Record nextRecord() {
            Scanner.Token token = scanner.next();
            while(token == Scanner.Token.RECORD_END) token = scanner.next();
            if(token == null) return null;
            Record record = Record.instance(row++);
            while(token == Scanner.Token.FIELD) {
                record.field(scanner.current());
                token = scanner.next();
            }
            return record;
        }

        @Override
        public boolean hasNext() {
            return nextone != null;
        }

        @Override
        public Record next() {
            if(nextone == null) return null;
            Record rv = nextone;
            nextone = nextRecord();
            return rv;
        }

    }

    /**
     * Helper api for building csv data.
     */
    public static class Appender {
        int delimiter = ',';
        int quote = '"';
        boolean forceQuotes = false;
        String nlreplacer = null;
        Appendable appendable;
        private boolean firstInRow = true;
        private Appender() {}
        public static Appender instance(Appendable appendable, int delimiter, int quote, boolean forceQuotes, String nlreplacer) {
            Appender rv = new Appender();
            rv.appendable = appendable;
            rv.delimiter = delimiter;
            rv.quote = quote;
            rv.forceQuotes = forceQuotes;
            rv.nlreplacer = nlreplacer;
            return rv;
        }

        private static String escape(String value) {
            value = value.replace("\"", "\"\"");
            return value;
        }

        private boolean shouldBeQuoted(String value) {
            int len = value.length();
            for(int i = 0; i < len; i++) {
                char c = value.charAt(i);
                if(c == delimiter) return true;
                if(c == quote) return true;
                if(c == '\n') return true;
            }
            return false;
        }

        /**
         * Add all fields of record and finish record.
         * @param value
         * @return this
         */
        public Appender record(Record value) {
            if(value == null) value = Record.instance(0);
            for(String field : value.fields()) {
                field(field);
            }
            recordEnd();
            return this;
        }

        /**
         * Add one field.
         * @param value
         * @return this
         */
        public Appender field(String value) {
            try {
                if(value == null) value = "";
                if(firstInRow) firstInRow = false;
                else appendable.append((char)delimiter);
                if(nlreplacer != null) {
                    value = value.replace("\r\n", nlreplacer);
                    value = value.replace("\r", nlreplacer);
                    value = value.replace("\n", nlreplacer);
                }
                if(forceQuotes || shouldBeQuoted(value)) {
                    appendable.append((char)quote);
                    appendable.append(escape(value));
                    appendable.append((char)quote);
                } else {
                    appendable.append(value);
                }
                return this;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        }

        /**
         * Add end of record (newline).
         * @return this
         */
        public Appender recordEnd() {
            try {
                appendable.append('\n');
                firstInRow = true;
                return this;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        }

        public Appender flush() {
            try {
                if(appendable instanceof Flushable) ((Flushable)appendable).flush();
                return this;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        }

        public Appender close() {
            try {
                if(appendable instanceof Closeable) ((Closeable)appendable).close();
                return this;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e);
            }
        }


    }

    public static void main(String[] argv) throws Exception {
        //Reader r = new FileReader("/home/antons/Downloads/username.csv");
        //CsvScanner consumer = Scanner.instance(r, ';', '"');
        //Reader r = new FileReader("/home/antons/pcards-dev-1706632173966.csv");
        //Scanner consumer = Scanner.instance(r, ',', '"');
        Csv.Scanner consumer = Csv.instance()
            .encoding("cp1250")
            .delimiter(';')
            //.scanner(new FileInputStream("/tmp/aaa/eskn.csv"));
            .scanner(new FileInputStream("/tmp/aaa/odpoved.csv"));

        Scanner.Token token = consumer.next();
        while(token != null) {
            System.out.println(token + " - " + consumer.current());
            token = consumer.next();
        }

        Appender appender = Csv.instance()
            .encoding("utf-8")
            .delimiter(',')
            .forceQuotes(true)
            .appender(new FileOutputStream("/tmp/aaa/odpoved2.csv"));

        Iterator<Record> iter = Csv.instance()
            .encoding("cp1250")
            .delimiter(';')
            .iterator(new FileInputStream("/tmp/aaa/odpoved.csv"));
        while(iter.hasNext()) {
            Record next = iter.next();
            System.out.println(" - " + next);
            appender.record(next);
        }
        appender.flush();
        appender.close();

    }
}
