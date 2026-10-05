package ua.rental;

import java.time.LocalDate;

import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.Customer;
import ua.rental.model.Payment;
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
        System.out.println("=== Створення об'єктів конструктором та фабричним методом ===");
        
        // Creating objects using public constructors
        Branch branch = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        Customer customer = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        
        // Creating objects using static factory methods
        Car car = Car.of("  aa1234bc ", "Toyota RAV4", 2021, 45000, " available ");
        Rental rental = Rental.of(car, customer, branch, LocalDate.now(), LocalDate.now().plusDays(3));
        
        // Creating payment with constructor
        Payment payment = new Payment("PAY-1001", rental, 2500.0, LocalDate.now(), " CREDIT_CARD ");

        System.out.println("\n=== Нормалізація в дії ===");
        System.out.println("Введено номер: '  aa1234bc ', статус: ' available '");
        System.out.println("Після нормалізації (Car): номер = '" + car.getLicensePlate() + "', статус = '" + car.getStatus() + "'");
        System.out.println("Введено метод оплати: ' CREDIT_CARD '");
        System.out.println("Після нормалізації (Payment): '" + payment.getPaymentMethod() + "'");

        System.out.println("\n=== Порушення правил (try/catch) ===");
        
        // 1. Violation: Invalid car status
        try {
            Car.of("KA5555KA", "Ford Focus", 2019, 60000, "BROKEN_STATUS");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при створенні Car (неправильний статус): " + e.getMessage());
        }

        // 2. Violation: Customer is underage
        try {
            new Customer("DL111111", "Young", "Driver", LocalDate.now().minusYears(17));
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при створенні Customer (вік менше 18): " + e.getMessage());
        }

        // 3. Violation: Start date is after end date in Rental
        try {
            Rental.of(car, customer, branch, LocalDate.now().plusDays(2), LocalDate.now());
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при створенні Rental (дати переплутані): " + e.getMessage());
        }

        System.out.println("\n=== Спроба зіпсувати коректний об'єкт сеттером ===");
        
        // Attempting to spoil the correctly instantiated 'car' object
        try {
            car.setMileage(-150);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при виклику setMileage з від'ємним значенням: " + e.getMessage());
        }

        System.out.println("\n=== Порівняння об'єктів (==, equals, hashCode) ===");
        
        // Two objects with exactly the same identifier (license plate)
        Car car1 = Car.of("BC7777BC", "Honda Civic", 2020, 20000, "AVAILABLE");
        Car car2 = Car.of("BC7777BC", "Honda Civic", 2020, 20000, "AVAILABLE");
        
        // Object with a different identifier
        Car car3 = Car.of("AI9999AI", "Mazda 3", 2022, 10000, "RENTED");

        System.out.println("car1 == car2 (посилання на різні об'єкти): " + (car1 == car2));
        System.out.println("car1.equals(car2) (однаковий ідентифікатор): " + car1.equals(car2));
        System.out.println("car1.hashCode() == car2.hashCode() (однаковий хеш): " + (car1.hashCode() == car2.hashCode()));
        System.out.println("car1.equals(car3) (різні об'єкти): " + car1.equals(car3));

        System.out.println("\n=== Виклик обчислюваних методів ===");
        
        // Calculating properties using utility class
        int rentalDays = RentalUtils.rentalDays(rental);
        System.out.println("Кількість днів оренди (RentalUtils.rentalDays): " + rentalDays);
        
        int carAge = RentalUtils.carAgeYears(car);
        System.out.println("Вік автомобіля в роках (RentalUtils.carAgeYears): " + carAge);

        System.out.println("\n=== Вивід усіх сутностей через toString() ===");
        System.out.println("--- Branch ---");
        System.out.println(branch);
        
        System.out.println("\n--- Customer ---");
        System.out.println(customer);
        
        System.out.println("\n--- Car ---");
        System.out.println(car);
        
        System.out.println("\n--- Rental ---");
        System.out.println(rental);
        
        System.out.println("\n--- Payment ---");
        System.out.println(payment);
        
        // The following lines are commented out because they would cause compilation errors:
        
        // 1. Accessing a package-private class from outside its package
        // ua.rental.util.ValidationHelper.requireNotNull(null, "Test");
        // Compilation error: ua.rental.util.ValidationHelper is not public in ua.rental.util; cannot be accessed from outside package
        
        // 2. Accessing a private constructor
        // Car invalidCar = new Car("CX123", "Model X", 2021, 1000, "AVAILABLE");
        // Compilation error: Car(String, String, int, int, String) has private access in ua.rental.model.Car.
        // We must use the static factory method Car.of(...) instead.
    }
}
