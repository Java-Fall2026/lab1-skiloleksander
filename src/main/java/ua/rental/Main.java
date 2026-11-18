package ua.rental;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.rental.exception.DuplicateIdentityException;
import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.Customer;
import ua.rental.repository.GenericRepository;
import ua.rental.util.CarLoader;

/**
 * Main application entry point to demonstrate generic repositories with three domain types,
 * CSV loading with duplicate handling, add vs addIfAbsent behavior, element removal,
 * and multi-level SLF4J logging.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Runs the Lab 4 demonstration scenarios.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        log.info("Starting Lab 4 generic repository demonstration");

        // 1 & 2. Three repositories of different types + populating Car repository from CSV
        System.out.println("=== 1. Створення трьох репозиторіїв різних типів ===");
        GenericRepository<Car> carRepo = new GenericRepository<>(
            "CarRepository",
            car -> car.getLicensePlate()
        );
        GenericRepository<Customer> customerRepo = new GenericRepository<>(
            "CustomerRepository",
            customer -> customer.driverLicense()
        );
        GenericRepository<Branch> branchRepo = new GenericRepository<>(
            "BranchRepository",
            branch -> branch.name()
        );
        System.out.println("Створено репозиторії для Car (змінюваний клас), Customer (record) та Branch (record).");

        System.out.println("\n=== 2. Наповнення репозиторію автомобілів із CSV-файла ===");
        Path csvPath = Path.of("data/tests.csv");
        List<Car> loadedCars = CarLoader.loadCarsFromFile(csvPath);
        System.out.println("Прочитано коректних рядків із CSV-файла (" + csvPath + "): " + loadedCars.size());

        carRepo.addAll(loadedCars);
        System.out.println("Кількість унікальних авто в CarRepository після addAll (дублікат AA1234BC із CSV пропущено): "
            + carRepo.size());

        List<Car> allCars = carRepo.findAll();
        for (int i = 0; i < allCars.size(); i++) {
            Car c = allCars.get(i);
            System.out.println((i + 1) + ". " + c.getLicensePlate() + " — " + c.getModel()
                + " (" + c.getYear() + " р., пробіг: " + c.getMileage() + " км, статус: " + c.getStatus() + ")");
        }

        // Populate Customer and Branch repositories
        customerRepo.add(new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24)));
        customerRepo.add(new Customer("DL123456", "Lesia", "Ukrainka", LocalDate.of(1998, 2, 25)));

        branchRepo.add(new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1"));
        branchRepo.add(new Branch("Lviv-Airport", "Lviv, Liubinska Street 168"));

        System.out.println("\nНаповнено CustomerRepository (розмір: " + customerRepo.size()
            + ") та BranchRepository (розмір: " + branchRepo.size() + ").");

        // Search by identity (found and not found) across all three repositories
        System.out.println("\n=== Пошук за identity у трьох репозиторіях (знайдено і не знайдено) ===");

        Optional<Car> foundCar = carRepo.findByIdentity("AA1234BC");
        System.out.println("[CarRepository] Пошук 'AA1234BC' (наявний): "
            + foundCar.map(c -> c.getModel() + " [" + c.getLicensePlate() + "]").orElse("не знайдено"));
        Optional<Car> missingCar = carRepo.findByIdentity("ZZ9999ZZ");
        System.out.println("[CarRepository] Пошук 'ZZ9999ZZ' (відсутній): "
            + missingCar.map(Car::getModel).orElse("не знайдено (Optional.empty)"));

        Optional<Customer> foundCustomer = customerRepo.findByIdentity("DL987654");
        System.out.println("[CustomerRepository] Пошук 'DL987654' (наявний): "
            + foundCustomer.map(c -> c.firstName() + " " + c.lastName()).orElse("не знайдено"));
        Optional<Customer> missingCustomer = customerRepo.findByIdentity("DL000000");
        System.out.println("[CustomerRepository] Пошук 'DL000000' (відсутній): "
            + missingCustomer.map(Customer::firstName).orElse("не знайдено (Optional.empty)"));

        Optional<Branch> foundBranch = branchRepo.findByIdentity("Kyiv-Central");
        System.out.println("[BranchRepository] Пошук 'Kyiv-Central' (наявний): "
            + foundBranch.map(b -> b.name() + " (" + b.location() + ")").orElse("не знайдено"));
        Optional<Branch> missingBranch = branchRepo.findByIdentity("Odesa-Port");
        System.out.println("[BranchRepository] Пошук 'Odesa-Port' (відсутній): "
            + missingBranch.map(Branch::name).orElse("не знайдено (Optional.empty)"));

        // 3. Adding duplicate via add (with catch) and via addIfAbsent
        System.out.println("\n=== 3. Додавання дубліката через add (із перехопленням) і через addIfAbsent ===");
        Car duplicateCar = Car.of("AA1234BC", "Honda CR-V", 2023, 12000, CarStatus.AVAILABLE);

        System.out.println("Спроба додати дублікат 'AA1234BC' через add(...):");
        try {
            carRepo.add(duplicateCar);
        } catch (DuplicateIdentityException e) {
            System.out.println(" -> Перехоплено DuplicateIdentityException: " + e.getMessage());
        }
        System.out.println(" -> Розмір CarRepository після невдалого add: " + carRepo.size());

        System.out.println("Спроба додати той самий дублікат 'AA1234BC' через addIfAbsent(...):");
        boolean addedDuplicate = carRepo.addIfAbsent(duplicateCar);
        System.out.println(" -> Виключення не кинуто, метод повернув: " + addedDuplicate
            + ", розмір CarRepository: " + carRepo.size());

        Car newCar = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        boolean addedNew = carRepo.addIfAbsent(newCar);
        System.out.println("Додавання нового авто 'CE7592BD' через addIfAbsent(...): повернуто "
            + addedNew + ", розмір CarRepository: " + carRepo.size());

        // 4. Removing an existing and a non-existent element
        System.out.println("\n=== 4. Видалення наявного й відсутнього елемента ===");
        boolean removedExisting = carRepo.removeByIdentity("KA5678CB");
        System.out.println("Видалення наявного авто 'KA5678CB': результат = " + removedExisting
            + ", розмір CarRepository = " + carRepo.size());

        boolean removedMissing = carRepo.removeByIdentity("KA5678CB");
        System.out.println("Повторне видалення відсутнього авто 'KA5678CB': результат = " + removedMissing
            + ", розмір CarRepository = " + carRepo.size());

        // 5. Logging at all three levels (INFO, WARN, ERROR)
        System.out.println("\n=== 5. Демонстрація логування всіх трьох рівнів (INFO, WARN, ERROR) ===");

        // INFO: successful operation logged by repository
        Branch kharkivBranch = new Branch("Kharkiv-East", "Kharkiv, Poltavskyi Shliakh 50");
        branchRepo.add(kharkivBranch);
        System.out.println("[INFO] Успішно додано філію '" + kharkivBranch.name()
            + "' (залоговано на рівні INFO у GenericRepository).");

        // WARN: rejected duplicate or missing element logged by repository
        boolean customerDuplicateAdded = customerRepo.addIfAbsent(
            new Customer("DL987654", "Ivan", "Franko", LocalDate.of(1990, 8, 27))
        );
        System.out.println("[WARN] Відхилено дублікат клієнта 'DL987654' через addIfAbsent (повернуто "
            + customerDuplicateAdded + ", залоговано на рівні WARN у GenericRepository).");

        // ERROR: unswallowed DuplicateIdentityException logged by repository before throwing
        try {
            customerRepo.add(new Customer("DL987654", "Ivan", "Franko", LocalDate.of(1990, 8, 27)));
        } catch (DuplicateIdentityException e) {
            System.out.println("[ERROR] Перехоплено DuplicateIdentityException при спробі add дубліката: "
                + e.getMessage() + " (залоговано на рівні ERROR у GenericRepository).");
        }

        log.info("Lab 4 generic repository demonstration finished");
    }
}
