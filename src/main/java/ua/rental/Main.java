package ua.rental;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.rental.exception.InvalidDataException;
import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.Customer;
import ua.rental.model.Payment;
import ua.rental.model.PaymentMethod;
import ua.rental.model.Rental;
import ua.rental.util.CarLoader;
import ua.rental.util.RentalUtils;

/**
 * Main application entry point to demonstrate file loading, exception handling,
 * mutable state validation, and multi-level SLF4J logging.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Runs the Lab 3 demonstration scenarios.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        log.info("Starting car rental application demonstration");

        // 1. Reading a file containing both valid and invalid rows
        System.out.println("=== 1. Читання файла з коректними й некоректними рядками ===");
        Path validAndInvalidFile = Path.of("data/tests.csv");
        System.out.println("Читання файла: " + validAndInvalidFile);

        List<Car> loadedCars = CarLoader.loadCarsFromFile(validAndInvalidFile);
        System.out.println("Успішно завантажено коректних автомобілів: " + loadedCars.size());
        for (int i = 0; i < loadedCars.size(); i++) {
            Car c = loadedCars.get(i);
            System.out.println((i + 1) + ". " + c.getLicensePlate() + " — " + c.getModel()
                + " (" + c.getYear() + " р., пробіг: " + c.getMileage() + " км, статус: " + c.getStatus() + ")");
        }

        // 2. Attempting to read a non-existent file
        System.out.println("\n=== 2. Спроба прочитати відсутній файл ===");
        Path missingFile = Path.of("data/non_existent_cars.csv");
        System.out.println("Спроба відкрити відсутній файл: " + missingFile);

        List<Car> carsFromMissingFile = CarLoader.loadCarsFromFile(missingFile);
        System.out.println("Програма продовжує роботу після помилки читання файла.");
        System.out.println("Кількість завантажених автомобілів із відсутнього файла: " + carsFromMissingFile.size());

        // 3. Invalid value in a mutable class setter and its handling
        System.out.println("\n=== 3. Некоректне значення в сеттері змінюваного класу Car і його обробка ===");
        Car mutableCar = loadedCars.isEmpty()
            ? Car.of("AA1234BC", "Toyota RAV4", 2021, 45000, CarStatus.AVAILABLE)
            : loadedCars.get(0);

        System.out.println("Початковий стан автомобіля:\n" + mutableCar);

        System.out.println("\n--- Спроба встановити від'ємний пробіг (-150 км) через setMileage ---");
        try {
            mutableCar.setMileage(-150);
        } catch (IllegalArgumentException e) {
            log.warn("Rejected invalid mileage update for car {}: {}", mutableCar.getLicensePlate(), e.getMessage());
            System.out.println("Перехоплено виключення у сеттері setMileage: " + e.getMessage());
        }
        System.out.println("Пробіг автомобіля залишився незмінним: " + mutableCar.getMileage() + " км");

        System.out.println("\n--- Спроба встановити завеликий пробіг (5 000 000 км) через setMileage ---");
        try {
            mutableCar.setMileage(5_000_000);
        } catch (IllegalArgumentException e) {
            log.warn("Rejected out-of-range mileage update for car {}: {}", mutableCar.getLicensePlate(), e.getMessage());
            System.out.println("Перехоплено виключення у сеттері setMileage: " + e.getMessage());
        }
        System.out.println("Пробіг автомобіля залишився незмінним: " + mutableCar.getMileage() + " км");

        System.out.println("\n--- Спроба встановити порожній статус (null) через setStatus ---");
        try {
            mutableCar.setStatus(null);
        } catch (IllegalArgumentException e) {
            log.warn("Rejected null status update for car {}: {}", mutableCar.getLicensePlate(), e.getMessage());
            System.out.println("Перехоплено виключення у сеттері setStatus: " + e.getMessage());
        }
        System.out.println("Статус автомобіля залишився незмінним: " + mutableCar.getStatus());

        System.out.println("\n--- Коректне оновлення пробігу та статусу через сеттери ---");
        mutableCar.setMileage(46500);
        mutableCar.setStatus(CarStatus.RENTED);
        log.info("Updated car {} successfully: mileage={}, status={}",
            mutableCar.getLicensePlate(), mutableCar.getMileage(), mutableCar.getStatus());
        System.out.println("Оновлений стан автомобіля: пробіг = " + mutableCar.getMileage()
            + " км, статус = " + mutableCar.getStatus());

        // 4. Logging at all three levels (INFO, WARN, ERROR)
        System.out.println("\n=== 4. Логування всіх трьох рівнів (INFO, WARN, ERROR) ===");

        // INFO level demonstration: normal domain operation
        Branch branch = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        Customer customer = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        Rental rental = Rental.of(mutableCar, customer, branch, LocalDate.now(), LocalDate.now().plusDays(3));
        Payment payment = new Payment("PAY-1001", rental, 2500.0, LocalDate.now(), PaymentMethod.CREDIT_CARD);

        int rentalDays = RentalUtils.rentalDays(rental);
        int carAge = RentalUtils.carAgeYears(mutableCar);
        double fee = RentalUtils.calculateFee(payment.paymentMethod(), payment.amount());

        log.info("Created rental for customer {} {} on car {} for {} days (car age: {} years, fee: {})",
            customer.firstName(), customer.lastName(), mutableCar.getLicensePlate(),
            rentalDays, carAge, RentalUtils.formatMoney(fee));
        System.out.println("[INFO] Створено оренду на " + rentalDays + " дн., вік авто: " + carAge
            + " р., комісія за оплату: " + RentalUtils.formatMoney(fee));

        // WARN level demonstration: recoverable bad input row handled by caller
        String badCsvLine = "KA1111AA,Nissan Qashqai,2020,forty-thousand,AVAILABLE";
        try {
            CarLoader.parseCarFromLine(badCsvLine, 3);
        } catch (InvalidDataException e) {
            log.warn("Handled checked InvalidDataException while parsing single row: {}", e.getMessage(), e);
            System.out.println("[WARN] Перехоплено checked InvalidDataException: " + e.getMessage()
                + " (причина: " + e.getCause().getClass().getSimpleName() + ")");
        }

        // ERROR level demonstration: failed operation when trying to load from a missing backup path
        Path missingBackupPath = Path.of("data/backup_cars.csv");
        log.error("Backup file {} does not exist; proceeding before triggering loader error demonstration", missingBackupPath);
        CarLoader.loadCarsFromFile(missingBackupPath);
        System.out.println("[ERROR] Зафіксовано помилку доступу до відсутнього резервного файла: " + missingBackupPath);

        log.info("Car rental application demonstration finished");
    }
}
