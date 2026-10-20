package ua.rental;

import java.time.LocalDate;

import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.Customer;
import ua.rental.model.Payment;
import ua.rental.model.PaymentMethod;
import ua.rental.model.Rental;
import ua.rental.util.RentalUtils;

/**
 * Main application entry point to demonstrate the usage of the rental domain model.
 * Showcases object creation, validation, normalization, and computed properties.
 */
public class Main {

    /**
     * The main method to run the demonstration.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // 1. Record creation and accessing its components
        System.out.println("=== 1. Створення record-об'єктів та звернення до їхніх компонентів ===");
        Branch branch = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        Customer customer = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));

        System.out.println("branch.name(): " + branch.name());
        System.out.println("branch.location(): " + branch.location());
        System.out.println("customer.driverLicense(): " + customer.driverLicense());
        System.out.println("customer.firstName(): " + customer.firstName());
        System.out.println("customer.lastName(): " + customer.lastName());
        System.out.println("customer.birthDate(): " + customer.birthDate());

        // Creating mutable class Car, and records Rental and Payment
        Car car = Car.of("  aa1234bc ", "Toyota RAV4", 2021, 45000, CarStatus.AVAILABLE);
        Rental rental = Rental.of(car, customer, branch, LocalDate.now(), LocalDate.now().plusDays(3));
        Payment payment = new Payment("PAY-1001", rental, 2500.0, LocalDate.now(), PaymentMethod.CREDIT_CARD);

        // 2. Enum: values() iteration, custom constant field, valueOf in try/catch
        System.out.println("\n=== 2. Робота з enum (values, власне поле, valueOf в try/catch) ===");
        System.out.println("--- Перебір констант PaymentMethod через values() та власне поле feePercent ---");
        for (PaymentMethod method : PaymentMethod.values()) {
            System.out.println(method.name() + ": ordinal=" + method.ordinal() + ", feePercent=" + method.getFeePercent() + "%");
        }

        System.out.println("\n--- Доступ до власного поля конкретної константи ---");
        System.out.println("PaymentMethod.CREDIT_CARD.getFeePercent(): " + PaymentMethod.CREDIT_CARD.getFeePercent() + "%");

        System.out.println("\n--- Неправильний рядок у valueOf (try/catch) ---");
        String invalidMethodStr = "BITCOIN";
        try {
            PaymentMethod method = PaymentMethod.valueOf(invalidMethodStr);
            System.out.println("Знайдено спосіб оплати: " + method);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при PaymentMethod.valueOf: " + e.getMessage());
        }

        String invalidStatusStr = "BROKEN";
        try {
            CarStatus status = CarStatus.valueOf(invalidStatusStr);
            System.out.println("Знайдено статус: " + status);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при CarStatus.valueOf: " + e.getMessage());
        }

        // 3. Both switch expressions
        System.out.println("\n=== 3. Робота switch-виразів ===");
        System.out.println("--- Перший switch-вираз: по CarStatus без default (RentalUtils.getCarStatusAction) ---");
        for (CarStatus status : CarStatus.values()) {
            System.out.println(status + ": " + RentalUtils.getCarStatusAction(status));
        }

        System.out.println("\n--- Другий switch-вираз: вибір поведінки розрахунку комісії (RentalUtils.calculateFee) ---");
        double orderAmount = 2500.0;
        System.out.println("Сума до оплати: " + RentalUtils.formatMoney(orderAmount));
        for (PaymentMethod method : PaymentMethod.values()) {
            System.out.println(method + ": комісія " + RentalUtils.formatMoney(RentalUtils.calculateFee(method, orderAmount)));
        }

        // 4. Record created with invalid data -> IllegalArgumentException from compact constructor
        System.out.println("\n=== 4. Створення record з невалідними даними (компактний конструктор) ===");
        try {
            new Customer("DL111111", "Young", "Driver", LocalDate.now().minusYears(16));
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка компактного конструктора Customer (вік менше 18): " + e.getMessage());
        }

        try {
            new Branch("   ", "Kyiv, Khreshchatyk 1");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка компактного конструктора Branch (порожня назва): " + e.getMessage());
        }

        try {
            new Payment("PAY-9999", rental, -250.0, LocalDate.now(), PaymentMethod.CASH);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка компактного конструктора Payment (недодатна сума): " + e.getMessage());
        }

        try {
            Rental.of(car, customer, branch, LocalDate.now().plusDays(5), LocalDate.now().plusDays(2));
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка компактного конструктора Rental (дати переплутані): " + e.getMessage());
        }

        // 5. Two identical record objects: equals and hashCode
        System.out.println("\n=== 5. Порівняння двох однакових record-об'єктів (equals та hashCode) ===");
        Customer customer1 = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        Customer customer2 = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        Customer customer3 = new Customer("DL000001", "Ivan", "Franko", LocalDate.of(1990, 5, 1));

        System.out.println("customer1 == customer2 (різні посилання): " + (customer1 == customer2));
        System.out.println("customer1.equals(customer2) (згенерований equals): " + customer1.equals(customer2));
        System.out.println("customer1.hashCode() == customer2.hashCode() (згенерований hashCode): " + (customer1.hashCode() == customer2.hashCode()));
        System.out.println("customer1.equals(customer3) (різні об'єкти): " + customer1.equals(customer3));

        Branch branch1 = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        Branch branch2 = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        System.out.println("branch1.equals(branch2): " + branch1.equals(branch2));
        System.out.println("branch1.hashCode() == branch2.hashCode(): " + (branch1.hashCode() == branch2.hashCode()));

        // 6. Record printing via generated toString()
        System.out.println("\n=== 6. Вивід record через згенерований toString() ===");
        System.out.println("--- Branch ---");
        System.out.println(branch);

        System.out.println("\n--- Customer ---");
        System.out.println(customer);

        System.out.println("\n--- Rental ---");
        System.out.println(rental);

        System.out.println("\n--- Payment ---");
        System.out.println(payment);

        System.out.println("\n--- Car (клас із власним toString) ---");
        System.out.println(car);

        // Additional: mutable class Car setter and computed methods
        System.out.println("\n=== Спроба зіпсувати коректний об'єкт сеттером ===");
        try {
            car.setMileage(-150);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при виклику setMileage з від'ємним значенням: " + e.getMessage());
        }

        System.out.println("\n=== Виклик обчислюваних методів ===");
        int rentalDays = RentalUtils.rentalDays(rental);
        System.out.println("Кількість днів оренди (RentalUtils.rentalDays): " + rentalDays);

        int carAge = RentalUtils.carAgeYears(car);
        System.out.println("Вік автомобіля в роках (RentalUtils.carAgeYears): " + carAge);

        // The following lines are commented out because they would cause compilation errors:

        // 1. Accessing a package-private class from outside its package
        // ua.rental.util.ValidationHelper.requireNotNull(null, "Test");
        // Compilation error: ua.rental.util.ValidationHelper is not public in ua.rental.util; cannot be accessed from outside package

        // 2. Accessing a private constructor
        // Car invalidCar = new Car("CX123", "Model X", 2021, 1000, CarStatus.AVAILABLE);
        // Compilation error: Car(String, String, int, int, CarStatus) has private access in ua.rental.model.Car.
        // We must use the static factory method Car.of(...) instead.
    }
}
