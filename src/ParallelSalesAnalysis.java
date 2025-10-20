import java.util.*;

public class ParallelSalesAnalysis {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== СИСТЕМА ПАРАЛЛЕЛЬНОГО АНАЛИЗА ПРОДАЖ ===");
        System.out.println("Доступно процессоров: " + Runtime.getRuntime().availableProcessors());

        String[] filenames;
        if (args != null && args.length > 0) {
            filenames = args;
        } else {
            filenames = new String[] { "primer1.csv", "primer2.csv", "primer3.csv", "primer4.csv" };
        }

        long parallelTime = processParallel(filenames);

        System.out.println("\n=== РЕЗУЛЬТАТЫ ===");
        System.out.println("Параллельная обработка заняла: " + parallelTime + " мс");

        // БОНУС: можно раскомментировать для сравнения с последовательной обработкой
        // long sequentialTime = processSequential(filenames);
        // System.out.println("Последовательная обработка заняла: " + sequentialTime + " мс");
        // System.out.printf("Ускорение: %.2fx\n", (double) sequentialTime / parallelTime);
    }

    public static long processParallel(String[] filenames) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<FileProcessor> processors = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (String fname : filenames) {
            FileProcessor fp = new FileProcessor(fname);
            processors.add(fp);
            Thread t = new Thread(fp, "Processor-" + fname);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        SalesStatistics finalStats = new SalesStatistics();

        for (FileProcessor fp : processors) {
            if (fp.getErrorMessage() != null) {
                System.err.println("Ошибка в процессе '" + fp.getFilename() + "': " + fp.getErrorMessage());
            }
            for (SalesRecord rec : fp.getResults()) {
                finalStats.addRecord(rec);
            }
        }

        finalStats.printReport();

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static long processSequential(String[] filenames) {
        long startTime = System.currentTimeMillis();

        SalesStatistics stats = new SalesStatistics();
        for (String fname : filenames) {
            FileProcessor fp = new FileProcessor(fname);
            fp.run();
            if (fp.getErrorMessage() != null) {
                System.err.println("Ошибка при последовательной обработке файла '" + fname + "': " + fp.getErrorMessage());
            }
            for (SalesRecord r : fp.getResults()) {
                stats.addRecord(r);
            }
        }

        stats.printReport();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
