import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    protected static ArrayBlockingQueue<String> abq1 = new ArrayBlockingQueue<String>(100);
    protected static ArrayBlockingQueue<String> abq2 = new ArrayBlockingQueue<String>(100);
    protected static ArrayBlockingQueue<String> abq3 = new ArrayBlockingQueue<String>(100);
    protected static AtomicInteger maxA = new AtomicInteger(0);
    protected static AtomicInteger maxB = new AtomicInteger(0);
    protected static AtomicInteger maxC = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        Thread thread = new Thread(() -> {
           for (int i = 0; i < 10000; i++) {
               String text = generateText("abc", 100000);
               try {
                   abq1.put(text);
                   abq2.put(text);
                   abq3.put(text);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
        thread.start();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = abq1.take();
                    if (text.chars().filter((ch) -> ch == 'a').count() > maxA.get()) {
                        maxA.set((int) text.chars().filter((ch) -> ch == 'a').count());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threads.add(thread1);
        thread1.start();

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = abq2.take();
                    if (text.chars().filter((ch) -> ch == 'b').count() > maxB.get()) {
                        maxB.set((int) text.chars().filter((ch) -> ch == 'b').count());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threads.add(thread2);
        thread2.start();

        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = abq3.take();
                    if (text.chars().filter((ch) -> ch == 'c').count() > maxC.get()) {
                        maxC.set((int) text.chars().filter((ch) -> ch == 'c').count());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threads.add(thread3);
        thread3.start();

        thread.join();
        for(Thread thread4: threads) {
            thread4.join();
        }
        System.out.println("Максимальное кол-во А: " + maxA.get() );
        System.out.println("Максимальное кол-во B: " + maxB.get() );
        System.out.println("Максимальное кол-во C: " + maxC.get() );
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