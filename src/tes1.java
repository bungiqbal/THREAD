import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class tes1 {
    static final int TARGET = 777;
    static final int SIZE = 1_000_000;
    static int[] data = new int[SIZE];
    static AtomicBoolean isCancelled = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Generate random data
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            data[i] = random.nextInt(1000); // range 0-999
        }

        Thread searchThread = new Thread(() -> {
            System.out.println("[Thread A - Search] Mulai pencarian...");
            for (int i = 0; i < SIZE; i++) {
                if (isCancelled.get()) {
                    System.out.println("[Thread A - Search] Dibatalkan!");
                    return;
                }
                if (data[i] == TARGET) {
                    System.out.println("[Thread A - Search] Angka " + TARGET + " ditemukan pada index " + i);
                    isCancelled.set(true); // Async cancellation
                    return;
                }
            }
            System.out.println("[Thread A - Search] Angka tidak ditemukan.");
        });

        Thread loggingThread = new Thread(() -> {
            System.out.println("[Thread B - Logging] Mulai mencatat aktivitas...");
            while (!isCancelled.get()) {
                try {
                    Thread.sleep(200);
                    System.out.println("[Thread B - Logging] Mencatat progress...");
                } catch (InterruptedException e) {
                    System.out.println("[Thread B - Logging] Interrupted!");
                    return;
                }
            }
            System.out.println("[Thread B - Logging] Dibatalkan (Deferred)!");
        });

        Thread notificationThread = new Thread(() -> {
            while (!isCancelled.get()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.println("[Thread C - Notification] Notifikasi: Proses telah selesai/dibatalkan.");
        });

        searchThread.start();
        loggingThread.start();
        notificationThread.start();
    }
}
