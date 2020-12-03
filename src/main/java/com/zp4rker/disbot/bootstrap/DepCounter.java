package com.zp4rker.disbot.bootstrap;

/**
 * @author zp4rker
 */
public class DepCounter {

    private int count = 0;
    private final int size;

    private Runnable onComplete;

    private String string;

    public DepCounter(int size, Runnable onComplete) {
        this.size = size;
        this.onComplete = onComplete;
        string = count + "/" + size;
        System.out.print(string);
    }

    public void increment() {
        count++;
        for (int i = 0; i < string.length(); i++) {
            System.out.print("\b");
        }
        string = count + "/" + size;
        if (count <= size) System.out.print(string);
        if (count == size) {
            System.out.println();
            onComplete.run();
        }
    }

}
