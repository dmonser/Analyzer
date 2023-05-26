package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final int TEXTS_COUNT = 10_000;
    public static final int TEXTS_LENGTH = 100_000;
    public static final String LETTERS = "abc";

    public static BlockingQueue<String> textsToA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsToB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsToC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        new Thread(() -> {
            for (int i = 0; i < TEXTS_COUNT; i++) {
                try {
                    textsToA.put(generateText(LETTERS, TEXTS_LENGTH));
                    textsToB.put(generateText(LETTERS, TEXTS_LENGTH));
                    textsToC.put(generateText(LETTERS, TEXTS_LENGTH));
                    System.out.println("Text generated");
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        Thread countA = new Thread(() -> {
            runThread(textsToA, 'a');
        });
        threads.add(countA);

        Thread countB = new Thread(() -> {
            runThread(textsToB, 'b');
        });
        threads.add(countB);

        Thread countC = new Thread(() -> {
            runThread(textsToC, 'c');
        });
        threads.add(countC);

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void runThread (BlockingQueue<String> queue, char c) {
        String biggestText = "";
        int include = 0;

        for (int i = 0; i < TEXTS_COUNT; i++) {
            String text;
            try {
                text = queue.take();
                System.out.println("                Text processed");
            } catch (InterruptedException e) {
                return;
            }

            int charInText = countChar(text, c);

            if (charInText > include) {
                include = charInText;
                biggestText = text;
            }
        }
        System.out.println("Max include '" + c + "' (" + include + ") in text: \n" + biggestText);
    }

    public static int countChar(String text, char c) {
        int counter = 0;
        char[] chars = text.toCharArray();
        for (char check : chars) {
            if (check == c) {
                counter++;
            }
        }
        return counter;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}