import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class tes3 {
    private static final int ARRAY_SIZE = 100_000;
    private static final int THREAD_COUNT = 4;
    private static final int[] data = new int[ARRAY_SIZE];
    private static final AtomicBoolean found = new AtomicBoolean(false);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan angka yang ingin dicari: ");
        int target = scanner.nextInt();

        // Isi array dengan angka acak
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            data[i] = rand.nextInt(ARRAY_SIZE);
        }

        // Buat dan jalankan thread pencarian
        Thread[] threads = new Thread[THREAD_COUNT];
        int chunkSize = ARRAY_SIZE / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * chunkSize;
            int end = (i == THREAD_COUNT - 1) ? ARRAY_SIZE : start + chunkSize;
            threads[i] = new Thread(new SearchTask(start, end, target, i));
            threads[i].start();
        }

        // Tunggu semua thread selesai
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Thread terganggu: " + e.getMessage());
            }
        }

        if (!found.get()) {
            System.out.println("Angka tidak ditemukan dalam array.");
        }
    }

    static class SearchTask implements Runnable {
        private final int start, end, target, threadId;

        SearchTask(int start, int end, int target, int threadId) {
            this.start = start;
            this.end = end;
            this.target = target;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            for (int i = start; i < end && !found.get(); i++) {
                if (data[i] == target) {
                    found.set(true);
                    System.out.println("Thread " + threadId + " menemukan angka di indeks: " + i);
                    break;
                }
            }
            if (!found.get()) {
                System.out.println("Thread " + threadId + " selesai tanpa menemukan angka.");
            }
        }
    }
}
