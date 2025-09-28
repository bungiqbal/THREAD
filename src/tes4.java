import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class tes4 {
    private static final int ARRAY_SIZE = 1_000_000;
    private static final int THREAD_COUNT = 4;
    private static final int[] data = new int[ARRAY_SIZE];

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan angka yang ingin dicari: ");
        int target = scanner.nextInt();

        // Isi array dengan angka acak
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            data[i] = rand.nextInt(ARRAY_SIZE * 2); // Angka acak
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicBoolean found = new AtomicBoolean(false);
        List<Future<Integer>> futures = new ArrayList<>();

        int chunkSize = ARRAY_SIZE / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int start = i * chunkSize;
            final int end = (i == THREAD_COUNT - 1) ? ARRAY_SIZE : start + chunkSize;

            Callable<Integer> task = () -> {
                for (int j = start; j < end && !found.get(); j++) {
                    if (data[j] == target) {
                        found.set(true);
                        System.out.println("Angka ditemukan di indeks: " + j + " oleh thread " + Thread.currentThread().getName());
                        return j;
                    }
                }
                return -1;
            };

            futures.add(executor.submit(task));
        }

        // Tunggu hasil
        for (Future<Integer> future : futures) {
            try {
                int result = future.get();
                if (result != -1) break;
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdownNow(); // Batalkan semua thread yang masih berjalan
        System.out.println("Pencarian selesai.");
    }
}
