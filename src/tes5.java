import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class tes5 {

    static final int[] data = new int[10];
    static final AtomicBoolean found = new AtomicBoolean(false);
    static final AtomicBoolean cancelRequested = new AtomicBoolean(false);
    static int target;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan angka yang ingin dicari: ");
        target = scanner.nextInt();

        // Isi array dengan angka acak
        Random rand = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] = rand.nextInt(10);
        }

        Thread searchThread = new Thread(new SearchTask(), "Thread-A");
        Thread logThread = new Thread(new LogTask(), "Thread-B");
        Thread notifyThread = new Thread(new NotifyTask(), "Thread-C");

        searchThread.start();
        logThread.start();
        notifyThread.start();
    }

    // Thread A: Pencarian data
    static class SearchTask implements Runnable {
        public void run() {
            for (int i = 0; i < data.length; i++) {
                if (cancelRequested.get()) {
                    System.out.println("Thread-A dibatalkan secara asynchronous.");
                    return;
                }

                if (data[i] == target) {
                    found.set(true);
                    System.out.println("Thread-A: Angka ditemukan di indeks " + i);
                    cancelRequested.set(true); // Asynchronous cancellation
                    return;
                }

                try {
                    Thread.sleep(10); // Simulasi proses
                } catch (InterruptedException e) {
                    System.out.println("Thread-A dibatalkan secara deferred.");
                    return;
                }
            }
            System.out.println("Thread-A: Angka tidak ditemukan.");
        }
    }

    // Thread B: Logging aktivitas
    static class LogTask implements Runnable {
        public void run() {
            while (!found.get() && !cancelRequested.get()) {
                System.out.println("Thread-B: Pencarian masih berlangsung...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Thread-B: Logging dihentikan.");
                    return;
                }
            }
            System.out.println("Thread-B: Logging selesai.");
        }
    }

    // Thread C: Notifikasi
    static class NotifyTask implements Runnable {
        public void run() {
            while (!found.get()) {
                if (cancelRequested.get()) {
                    System.out.println("Thread-C: Tidak ada notifikasi karena pencarian dibatalkan.");
                    return;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    System.out.println("Thread-C: Thread dibatalkan.");
                    return;
                }
            }
            System.out.println("Thread-C: Notifikasi - Angka ditemukan!");
        }
    }
}
