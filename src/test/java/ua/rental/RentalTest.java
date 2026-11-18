package ua.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.rental.exception.DuplicateIdentityException;
import ua.rental.exception.InvalidDataException;
import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.Customer;
import ua.rental.model.PaymentMethod;
import ua.rental.model.Rental;
import ua.rental.repository.GenericRepository;
import ua.rental.util.CarLoader;
import ua.rental.util.RentalUtils;

class RentalTest {
    @Test
    @DisplayName("Should create Car successfully with valid parameters")
    void shouldCreateCar() {
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);

        assertEquals("CE7592BD", car.getLicensePlate());
        assertEquals("Toyota Supra", car.getModel());
        assertEquals(2020, car.getYear());
        assertEquals(35000, car.getMileage());
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    @DisplayName("Should parse valid CSV line into Car object")
    void shouldParseCarFromCSV() throws InvalidDataException {
        String csvLine = "AA1234BC,Toyota RAV4,2021,45000,AVAILABLE";
        Car car = CarLoader.parseCarFromLine(csvLine, 2);

        assertNotNull(car);
        assertEquals("AA1234BC", car.getLicensePlate());
        assertEquals("Toyota RAV4", car.getModel());
        assertEquals(2021, car.getYear());
        assertEquals(45000, car.getMileage());
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when mileage is non-numeric")
    void shouldThrowWhenMileageIsNonNumeric() {
        String csvLine = "KA1111AA,Nissan Qashqai,2020,forty-thousand,AVAILABLE";

        InvalidDataException ex = assertThrows(
            InvalidDataException.class,
            () -> CarLoader.parseCarFromLine(csvLine, 3)
        );
        assertInstanceOf(NumberFormatException.class, ex.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when status is invalid")
    void shouldThrowWhenStatusIsInvalid() {
        String csvLine = "BC2222BB,Mazda 6,2019,75000,BROKEN";

        InvalidDataException ex = assertThrows(
            InvalidDataException.class,
            () -> CarLoader.parseCarFromLine(csvLine, 5)
        );
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when year is out of range")
    void shouldThrowWhenYearIsOutOfRange() {
        String csvLine = "AA3333CC,Peugeot 308,1885,50000,AVAILABLE";

        InvalidDataException ex = assertThrows(
            InvalidDataException.class,
            () -> CarLoader.parseCarFromLine(csvLine, 7)
        );
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when wrong count of columns")
    void shouldThrowWhenWrongCountOfColumns() {
        String csvLine = "AI5555EE,Audi A4,2021";

        assertThrows(InvalidDataException.class, () -> CarLoader.parseCarFromLine(csvLine, 11));
    }

    @Test
    @DisplayName("Should correctly calculate car age in years")
    void shouldCalculateCarAge() {
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        int expectedAge = LocalDate.now().getYear() - 2020;

        assertEquals(expectedAge, RentalUtils.carAgeYears(car));
    }

    @Test
    @DisplayName("Should correctly calculate rental days and payment fee")
    void shouldCalculateRentalDaysAndFee() {
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        Branch branch = new Branch("Kyiv-Central", "Kyiv, Vokzalna Square 1");
        Customer customer = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        Rental rental = Rental.of(car, customer, branch, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 6));

        assertEquals(5, RentalUtils.rentalDays(rental));
        assertEquals(37.5, RentalUtils.calculateFee(PaymentMethod.CREDIT_CARD, 2500.0), 0.001);
        assertEquals(55.0, RentalUtils.calculateFee(PaymentMethod.ONLINE, 2500.0), 0.001);
    }

    @Test 
    @DisplayName("Should add and find item by identity")
    void shouldFindItemByIdentity() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        carRepo.add(car);

        assertEquals(Optional.of(car), carRepo.findByIdentity("CE7592BD"));
    }

    @Test 
    @DisplayName("Should return null if identity doesn't exist")
    void shouldReturnNull() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        
        assertEquals(Optional.empty(), carRepo.findByIdentity("CE7592BD"));
    }

    @Test
    @DisplayName("Should throw DuplicateIdentityException with adding same ID")
    void shouldThrowDuplicateIdentityException() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        carRepo.add(car);

        assertThrows(DuplicateIdentityException.class, () -> carRepo.add(car));
    }

    @Test 
    @DisplayName("Should return false when adding duplicate and size doesn't change")
    void shouldReturnFalse() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        carRepo.add(car);
        int size = carRepo.size();
        carRepo.addIfAbsent(car);

        assertFalse(carRepo.addIfAbsent(car));
        assertEquals(size, carRepo.size());
    }

    @Test 
    @DisplayName("Should correctly remove by identity")
    void shouldRemoveByIdentity() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        Car car1 = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        Car car2 = Car.of("KA5678CB", "Skoda Octavia", 2019, 12000, CarStatus.RENTED);

        carRepo.add(car1);
        carRepo.add(car2);
        int initSize = carRepo.size();

        assertTrue(carRepo.removeByIdentity("CE7592BD"));

        int newSize = carRepo.size();

        assertEquals(initSize - 1, newSize);

        assertFalse(carRepo.removeByIdentity("CE7592BD"));
        assertEquals(initSize - 1, carRepo.size());
    }

    @Test 
    @DisplayName("Should throw UnsupportedOperationException")
    void shouldThrowUnsupportedOperationException() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        Car car1 = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        Car car2 = Car.of("KA5678CB", "Skoda Octavia", 2019, 12000, CarStatus.RENTED);

        carRepo.add(car1);
        carRepo.add(car2);

        List<Car> carList = carRepo.findAll();

        assertThrows(UnsupportedOperationException.class, () -> carList.clear());
    }

    @Test 
    @DisplayName("Should work fine with 2 different classes")
    void shouldWorkFine() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());
        GenericRepository<Customer> customerRepo = new GenericRepository<>(
            "CustomerRepository",
            customer -> customer.driverLicense()
        );

        Car car = Car.of("CE7592BD", "Toyota Supra", 2020, 35000, CarStatus.AVAILABLE);
        Customer customer = new Customer("DL987654", "Taras", "Shevchenko", LocalDate.of(1995, 8, 24));
        
        carRepo.add(car);
        customerRepo.add(customer);

        assertEquals(Optional.of(car), carRepo.findByIdentity("CE7592BD"));
        assertEquals(Optional.of(customer), customerRepo.findByIdentity("DL987654"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException in add(null)")
    void shouldThrowInNull() {
        GenericRepository<Car> carRepo = new GenericRepository<>("CarRepository", car -> car.getLicensePlate());

        assertThrows(IllegalArgumentException.class, () -> carRepo.add(null));
    }
}
