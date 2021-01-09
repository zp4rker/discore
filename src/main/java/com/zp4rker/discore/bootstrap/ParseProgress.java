package com.zp4rker.discore.bootstrap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zp4rker
 */
public class ParseProgress extends Thread {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private final float size;
    private float count = 0;
    private String string;

    public ParseProgress(int size, String title) {
        this.size = size + 1;
        if (this.size == 0) string = "100%";
        else string = "0%";
        System.out.print("Validating " + title + " dependencies... " + string);
    }

    public void increment() {
        count++;

        int percent = Math.round(count / size * 100);
        String s = percent + "%";

        if (!string.equals(s)) {
            queue.offer(s);
        }

        if (count >= size) {
            queue.offer("end");
        }
    }

    @Override
    public void run() {
        if (size == 0) return;
        try {
            String s;
            while (!(s = queue.take()).equals("end")) {
                System.out.print(new String(new char[string.length()]).replace("\0", "\b"));
                System.out.print(s);
                string = s;
            }

            System.out.println();
        } catch (InterruptedException ignored) {
        }
    }

}
