package com.zp4rker.discore.bootstrap;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zp4rker
 */
public class DownloadCounter extends Thread {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private int count = 0;
    private final int size;

    private String string;

    private final Runnable onComplete;

    public DownloadCounter(int size, Runnable onComplete) {
        this.size = size;
        this.onComplete = onComplete;
        string = "Loading libraries... " + count + "/" + size;
        System.out.print(string);
    }

    public void increment() {
        count++;

        StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[string.length()]).replace("\0", "\b"));
        string = "Loading libraries... " + count + "/" + size;
        if (count < size) {
            sb.append(string);
            queue.offer(sb.toString());
        } else {
            sb.append(string);
            queue.offer(sb.toString());
            queue.offer("end");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String s = queue.take();
                if (s.equals("end")) {
                    System.out.println();
                    System.out.println("Successfully loaded libraries.");
                    break;
                } else {
                    System.out.print(s);
                }
            }

            onComplete.run();
        } catch (InterruptedException ignored) {}
    }
}
