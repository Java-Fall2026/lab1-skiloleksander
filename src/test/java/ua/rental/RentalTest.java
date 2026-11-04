package ua.rental;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.rental.exception.InvalidDataException;
import ua.rental.model.Branch;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;
import ua.rental.model.Customer;
import ua.rental.model.PaymentMethod;
import ua.rental.model.Rental;
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
}
