package ua.rental.repository;

import ua.rental.model.Branch;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SearchBenchmark {
    private static final int ELEMENT_COUNT = 100_000;
    private static final int WARMUP_ITERATIONS = 3000;
    private static final int MEASUREMENT_RUNS = 25;

    private static <E> Optional<E> linearSearch(
        List<E> items, String identity, IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    private static long measureMapSearchMedianNs(GenericRepository<Branch> repo, String key) {
        long[] times = new long[MEASUREMENT_RUNS];
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            long start = System.nanoTime();
            repo.findByIdentity(key);
            times[i] = System.nanoTime() - start;
        }
        Arrays.sort(times);
        return times[MEASUREMENT_RUNS / 2];
    }

    private static long measureLinearSearchMedianNs(
        List<Branch> list, String key, IdentityExtractor<Branch> extractor) {
        long[] times = new long[MEASUREMENT_RUNS];
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            long start = System.nanoTime();
            linearSearch(list, key, extractor);
            times[i] = System.nanoTime() - start;
        }
        Arrays.sort(times);
        return times[MEASUREMENT_RUNS / 2];
    }

    public static void main(String[] args) {
        System.setProperty("slf4j.provider", "org.slf4j.helpers.NOP_FallbackServiceProvider");

        IdentityExtractor<Branch> extractor = branch -> branch.name();
        GenericRepository<Branch> repo = new GenericRepository<>("BranchRepository", extractor);

        for (int i = 0; i < ELEMENT_COUNT; i++) {
            repo.add(new Branch("BRANCH-" + i, "City-" + (i % 50)));
        }

        List<Branch> list = repo.findAll();

        String firstKey = "BRANCH-0";
        String lastKey = "BRANCH-" + (ELEMENT_COUNT - 1);
        String missingKey = "BRANCH-MISSING";

        // Warmup phase
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            repo.findByIdentity(firstKey);
            repo.findByIdentity(lastKey);
            repo.findByIdentity(missingKey);

            linearSearch(list, firstKey, extractor);
            linearSearch(list, lastKey, extractor);
            linearSearch(list, missingKey, extractor);
        }

        // Measurement phase (median of MEASUREMENT_RUNS runs)
        long mapFirstNs = measureMapSearchMedianNs(repo, firstKey);
        long listFirstNs = measureLinearSearchMedianNs(list, firstKey, extractor);

        long mapLastNs = measureMapSearchMedianNs(repo, lastKey);
        long listLastNs = measureLinearSearchMedianNs(list, lastKey, extractor);

        long mapMissingNs = measureMapSearchMedianNs(repo, missingKey);
        long listMissingNs = measureLinearSearchMedianNs(list, missingKey, extractor);

        System.out.println("\n=== Результати порівняння пошуку в Map та List (100 000 елементів, медіана з "
            + MEASUREMENT_RUNS + " замірів) ===");
        System.out.printf("%-25s | %-18s | %-18s%n", "Сценарій", "Map (нс)", "List (нс)");
        System.out.println("-------------------------------------------------------------------");
        System.out.printf("%-25s | %-18d | %-18d%n", "Перший елемент (BRANCH-0)", mapFirstNs, listFirstNs);
        System.out.printf("%-25s | %-18d | %-18d%n", "Останній (BRANCH-99999)", mapLastNs, listLastNs);
        System.out.printf("%-25s | %-18d | %-18d%n", "Відсутній (BRANCH-MISSING)", mapMissingNs, listMissingNs);
    }
}
