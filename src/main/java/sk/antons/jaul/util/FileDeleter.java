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

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Helper class for deleting temporary files with delay.
 *
 * @author antons
 */
public class FileDeleter {

    private static FileDeleter singleton;

    private Duration delay;
    private int max = 0;
    private LinkedList<Item> queue = new LinkedList<>();

    /**
     * Create deleter with specified delay.
     * @param delay
     */
    public FileDeleter(Duration delay) {
        if(delay == null) throw new IllegalArgumentException("delay is mandatory");
        this.delay = delay;
    }

    public static FileDeleter instance(Duration delay) { return new FileDeleter(delay); }
    /**
     * Limit also queue cached files by max number
     * @param value 0 (defualt) is unlimited cache
     * @return this
     */
    public FileDeleter max(int value) { this.max = value; return this; }
    public FileDeleter makeDefault() {
        singleton(this);
        return this;
    }

    /**
     * Returns singleton instance. You can define any instance as default.
     * Otherwise one hour deleter is defined as default.
     *
     * @return Singleton instance
     */
    public static synchronized FileDeleter getDefault() {
        if(singleton == null) singleton = new FileDeleter(Duration.ofHours(1));
        return singleton;
    }

    private static synchronized void singleton(FileDeleter deleter) {
        singleton = deleter;
    }

    /**
     * Refister file to be deleted.
     * @param file
     */
    public synchronized void register(File file) {
        if(file == null) return;
        queue.add(this.new Item(file));
        deleteFiles();
    }

    /**
     * Deletes registered files if file is cached longer than specified delay or max limit is reached.
     * Method is called after each registration. You can call it by yourself to be sure that files
     * are deleted also if no ather are registered.
     */
    public synchronized void deleteFiles() {
        if(queue.size() == 0) return;
        deleteMaxFiles();
        LocalDateTime now = LocalDateTime.now();
        while(true) {
            Item item = queue.peekFirst();
            if(item == null) break;
            if(item.isExpired(now)) {
                item.deleteFile();
                queue.removeFirst();
            } else {
                break;
            }
        }
    }

    private synchronized void deleteMaxFiles() {
        if(max > 0) {
            while(queue.size() > max) {
                Item item = queue.peekFirst();
                if(item == null) break;
                item.deleteFile();
                queue.removeFirst();
            }
        }
    }

    private class Item {
        File file;
        LocalDateTime ttl;

        Item(File f) {
            this.file = f;
            this.ttl = LocalDateTime.now().plusSeconds(FileDeleter.this.delay.getSeconds());
        }

        public boolean isExpired(LocalDateTime now) {
            return now.isAfter(ttl);
        }

        public void deleteFile() {
            if(file != null) {
                try {
                    if(file.exists()) file.delete();
                } catch(Exception e) {
                }
                file = null;
            }
        }
    }

    public static void main(String[] argv) throws Exception {
        FileDeleter deleter = FileDeleter.instance(Duration.ofMinutes(5)).makeDefault();
        for(int i = 0; i < 10; i++) {
            System.out.println(" create file " + i + " cache " + deleter.queue.size());
            File f = new File("/tmp/aaa/temp" + i);
            TextFile.save(f.getAbsolutePath(), "utf-8", "eeee", true);
            FileDeleter.getDefault().register(f);
            Thread.sleep(1000l*60);
        }
        for(int i = 0; i < 10; i++) {
            Thread.sleep(1000l*60);
            System.out.println(" cache " + deleter.queue.size());
            FileDeleter.getDefault().deleteFiles();
        }
    }

}
