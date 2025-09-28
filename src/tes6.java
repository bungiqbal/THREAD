import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class tes6 {
    static List<Integer> data = new ArrayList<>();
    static AtomicBoolean found = new AtomicBoolean(false);
    static int target;
    static Thread searchThread;
    static Thread logThread;
    static Thread notifyThread;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        generateRandomData(10); // Buat 100 angka acak
        System.out.println("Data: " + data);

        System.out.print("Masukkan angka yang ingin dicari: ");
        target = scanner.nextInt();

        // Thread A: Pencarian data
        searchThread = new Thread(() -> {
            for (int i = 0; i < data.size(); i++) {
                if (found.get()) break; // Deferred cancellation
                int value = data.get(i);
                System.out.println("Thread A mencari di indeks " + i + ": " + value);
                if (value == target) {
                    found.set(true);
                    System.out.println("Angka ditemukan di indeks " + i);
                    break;
                }
                try { Thread.sleep(50); } catch (InterruptedException e) {
                    System.out.println("Thread A dibatalkan secara asynchronous.");
                    return;
                }
            }
        });

        // Thread B: Logging aktivitas pencarian
        logThread = new Thread(() -> {
            while (!found.get()) {
                System.out.println("Logging: pencarian masih berlangsung...");
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    System.out.println("Thread B dibatalkan.");
                    return;
                }
            }
            System.out.println("Logging: pencarian selesai.");
        });

        // Thread C: Pengiriman notifikasi
        notifyThread = new Thread(() -> {
            while (!found.get()) {
                try { Thread.sleep(300); } catch (InterruptedException e) {
                    System.out.println("Thread C dibatalkan.");
                    return;
                }
            }
            System.out.println("Notifikasi: angka ditemukan!");
        });

        // Jalankan semua thread
        searchThread.start();
        logThread.start();
        notifyThread.start();

        // Simulasi pembatalan asynchronous setelah 2 detik
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!found.get()) {
                    System.out.println("Timeout! Membatalkan semua thread...");
                    searchThread.interrupt(); // Asynchronous cancel
                    logThread.interrupt();
                    notifyThread.interrupt();
                }
            }
        }, 2000);
    }

    static void generateRandomData(int size) {
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            data.add(rand.nextInt(100)); // Angka antara 0–99
        }
    }
}
