package com.registroautos.repository;

import com.registroautos.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    List<Car> findByUserId(Long userId);

    Optional<Car> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndPlate(Long userId, String plate);

    boolean existsByUserIdAndPlateAndIdNot(Long userId, String plate, Long id);
}
