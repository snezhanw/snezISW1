import java.util.*;
import java.util.stream.*;

public class SalesStatistics {
    private int totalRecords;
    private double totalRevenue;
    private Map<String, Integer> productQuantities;
    private Map<String, Double> productRevenues;

    public SalesStatistics() {
        this.totalRecords = 0;
        this.totalRevenue = 0.0;
        this.productQuantities = new HashMap<>();
        this.productRevenues = new HashMap<>();
    }

    public void addRecord(SalesRecord record) {
        if (record == null) return;
        totalRecords++;
        double amount = record.getTotalAmount();
        totalRevenue += amount;

        productQuantities.merge(record.getProductName(), record.getQuantity(), Integer::sum);
        productRevenues.merge(record.getProductName(), amount, Double::sum);
    }

    public void merge(SalesStatistics other) {
        if (other == null) return;
        this.totalRecords += other.totalRecords;
        this.totalRevenue += other.totalRevenue;

        other.productQuantities.forEach((product, qty) ->
                this.productQuantities.merge(product, qty, Integer::sum)
        );
        other.productRevenues.forEach((product, rev) ->
                this.productRevenues.merge(product, rev, Double::sum)
        );
    }

    public void printReport() {
        System.out.println("\n=== ОТЧЕТ ПО ПРОДАЖАМ ===");
        System.out.println("Всего записей обработано: " + totalRecords);
        System.out.printf("Общая выручка: %.2f тг\n", totalRevenue);

        System.out.println("\n--- Топ 5 товаров по количеству ---");
        List<Map.Entry<String, Integer>> topByQty = productQuantities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        int rank = 1;
        for (Map.Entry<String, Integer> e : topByQty) {
            System.out.printf("%d. %s: %d шт.\n", rank++, e.getKey(), e.getValue());
        }

        System.out.println("\n--- Топ 5 товаров по выручке ---");
        List<Map.Entry<String, Double>> topByRev = productRevenues.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        rank = 1;
        for (Map.Entry<String, Double> e : topByRev) {
            System.out.printf("%d. %s: %.2f тг\n", rank++, e.getKey(), e.getValue());
        }
    }

    public int getTotalRecords() { return totalRecords; }
    public double getTotalRevenue() { return totalRevenue; }
}
