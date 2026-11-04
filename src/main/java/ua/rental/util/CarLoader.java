package ua.rental.util;

import ua.rental.exception.InvalidDataException;
import ua.rental.model.Car;
import ua.rental.model.CarStatus;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CarLoader {
    private static final Logger log = LoggerFactory.getLogger(CarLoader.class);

    private CarLoader() {}

    public static Car parseCarFromLine(String line, int lineNumber) throws InvalidDataException {
        try {
            RentalUtils.requireNotEmpty(line, "CSV line");
            String[] parts = line.split(",", -1);

            if (parts.length > 5) {
                throw new IllegalArgumentException(
                    "Expected format licensePlate,model,year,mileage,status, got: " + line
                );
            }

            String licensePlate = parts[0].trim();
            String model = parts[1].trim();
            int year = Integer.parseInt(parts[2].trim());
            int mileage = Integer.parseInt(parts[3].trim());
            CarStatus status = CarStatus.valueOf(parts[4].trim());

            return Car.of(licensePlate, model, year, mileage, status);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidDataException("Invalid line: " + line + " at line " + lineNumber, e);
        }
    }

    public static List<Car> loadCarsFromFile(Path path) {
        List<Car> cars = new ArrayList<>();
        if (path == null) {
            log.error("File path cannot be null");
            return cars;
        }

        int lineNumber = 1;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();

            if (header == null) {
                log.warn("File is empty: {}", path);
                return cars;
            }

            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    Car car = parseCarFromLine(line, lineNumber);
                    cars.add(car);
                } catch (InvalidDataException e) {
                    log.warn("Skipping invalid line {}: {}", lineNumber, line, e);
                }
            }
            log.info("Loaded {} cars from {}", cars.size(), path);
        } catch (NoSuchFileException e) {
            log.error("File not found: {}", path, e);
        } catch (IOException e) {
            log.error("Error reading file: {} due to error: {}", path, e.getMessage(), e);
        } finally {
            log.info("Finished loading cars from file: {}", path);
        }
        return cars;
    }
}
