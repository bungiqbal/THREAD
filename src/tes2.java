import java.util.Scanner;

class SearchTask implements Runnable {
    private int[] data;
    private int target;
    private volatile boolean found = false; // Flag untuk membatalkan thread lain

    public SearchTask(int[] data, int target) {
        this.data = data;
        this.target = target;
    }

    @Override
    public void run() {
        for (int num : data) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " dibatalkan.");
                return;
            }
            if (num == target) {
                System.out.println(Thread.currentThread().getName() + " menemukan angka " + num);
                found = true;
                break;
            }
        }
    }

    public boolean isFound() {
        return found;
    }
}

class LoggerTask implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("[LOG] Program masih berjalan...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("[LOG] Logging dihentikan.");
                return;
            }
        }
    }
}

class NotificationTask implements Runnable {
    @Override
    public void run() {
        System.out.println("[NOTIF] Angka ditemukan! Mengirim notifikasi...");
    }
}

public class tes2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Thread A: Input manual oleh user
        System.out.print("Masukkan jumlah data array: ");
        int n = scanner.nextInt();
        int[] data = new int[n];
        System.out.println("Isi data array:");
        for (int i = 0; i < n; i++) {
            System.out.print("Data ke-" + (i + 1) + ": ");
            data[i] = scanner.nextInt();
        }

        System.out.print("Masukkan angka yang ingin dicari: ");
        int target = scanner.nextInt();

        SearchTask searchTask = new SearchTask(data, target);
        Thread searchThread = new Thread(searchTask, "Thread-Pencarian");
        Thread loggerThread = new Thread(new LoggerTask(), "Thread-Logger");

        // Jalankan thread
        searchThread.start();
        loggerThread.start();

        // Tunggu hingga pencarian selesai
        while (searchThread.isAlive()) {
            if (searchTask.isFound()) {
                // Batalkan thread logger
                loggerThread.interrupt();

                // Jalankan notifikasi
                new Thread(new NotificationTask(), "Thread-Notif").start();

                break;
            }
        }

        System.out.println("Program selesai.");
    }
}
