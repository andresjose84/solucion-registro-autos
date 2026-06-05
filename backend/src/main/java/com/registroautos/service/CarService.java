package com.registroautos.service;

import com.registroautos.dto.CarRequest;
import com.registroautos.dto.CarResponse;
import com.registroautos.entity.Car;
import com.registroautos.entity.User;
import com.registroautos.exception.DuplicateResourceException;
import com.registroautos.exception.ResourceNotFoundException;
import com.registroautos.mapper.EntityMapper;
import com.registroautos.repository.CarRepository;
import com.registroautos.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarService(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CarResponse> findAllForUser(Long userId, String search, String brand, Integer year) {
        return carRepository.findAll(CarSpecifications.forUserWithFilters(userId, search, brand, year))
                .stream()
                .map(EntityMapper::toCarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CarResponse findByIdForUser(Long userId, Long carId) {
        Car car = getOwnedCar(userId, carId);
        return EntityMapper.toCarResponse(car);
    }

    @Transactional
    public CarResponse create(Long userId, CarRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String normalizedPlate = normalizePlate(request.plate());

        if (carRepository.existsByUserIdAndPlate(userId, normalizedPlate)) {
            throw new DuplicateResourceException("Ya tienes un auto registrado con esa placa");
        }

        Car car = new Car();
        car.setUser(user);
        applyRequest(car, request, normalizedPlate);

        return EntityMapper.toCarResponse(carRepository.save(car));
    }

    @Transactional
    public CarResponse update(Long userId, Long carId, CarRequest request) {
        Car car = getOwnedCar(userId, carId);
        String normalizedPlate = normalizePlate(request.plate());

        if (carRepository.existsByUserIdAndPlateAndIdNot(userId, normalizedPlate, carId)) {
            throw new DuplicateResourceException("Ya tienes un auto registrado con esa placa");
        }

        applyRequest(car, request, normalizedPlate);
        return EntityMapper.toCarResponse(carRepository.save(car));
    }

    @Transactional
    public void delete(Long userId, Long carId) {
        Car car = getOwnedCar(userId, carId);
        carRepository.delete(car);
    }

    private Car getOwnedCar(Long userId, Long carId) {
        return carRepository.findByIdAndUserId(carId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Auto no encontrado"));
    }

    private void applyRequest(Car car, CarRequest request, String normalizedPlate) {
        car.setBrand(request.brand().trim());
        car.setModel(request.model().trim());
        car.setYear(request.year());
        car.setPlate(normalizedPlate);
        car.setColor(request.color().trim());
        car.setPhotoUrl(request.photoUrl() != null && !request.photoUrl().isBlank()
                ? request.photoUrl().trim()
                : null);
    }

    private String normalizePlate(String plate) {
        return plate.trim().toUpperCase();
    }
}
