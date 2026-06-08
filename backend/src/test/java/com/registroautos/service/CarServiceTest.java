package com.registroautos.service;

import com.registroautos.dto.CarRequest;
import com.registroautos.dto.CarResponse;
import com.registroautos.entity.Car;
import com.registroautos.entity.User;
import com.registroautos.exception.DuplicateResourceException;
import com.registroautos.exception.ResourceNotFoundException;
import com.registroautos.repository.CarRepository;
import com.registroautos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarService carService;

    private User user;
    private Car car;
    private CarRequest carRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("demo@registroautos.com");
        user.setFullName("Usuario Demo");

        car = new Car();
        car.setId(10L);
        car.setUser(user);
        car.setBrand("Chevrolet");
        car.setModel("Spark GT");
        car.setYear(2020);
        car.setPlate("MWK737");
        car.setColor("Rojo");
        car.setPhotoUrl("https://example.com/photo.jpg");

        carRequest = new CarRequest(
                "Chevrolet",
                "Spark GT",
                2020,
                "mwk737",
                "Rojo",
                "https://example.com/photo.jpg"
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllForUser_shouldReturnMappedCars() {
        when(carRepository.findAll(any(Specification.class))).thenReturn(List.of(car));

        List<CarResponse> responses = carService.findAllForUser(1L, "MWK", "Chevrolet", 2020);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).plate()).isEqualTo("MWK737");
        assertThat(responses.get(0).brand()).isEqualTo("Chevrolet");
    }

    @Test
    void findByIdForUser_shouldReturnCarWhenOwned() {
        when(carRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(car));

        CarResponse response = carService.findByIdForUser(1L, 10L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.model()).isEqualTo("Spark GT");
    }

    @Test
    void findByIdForUser_shouldThrowWhenNotFound() {
        when(carRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.findByIdForUser(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Auto no encontrado");
    }

    @Test
    void create_shouldNormalizePlateAndPersistCar() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.existsByUserIdAndPlate(1L, "MWK737")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });

        CarResponse response = carService.create(1L, carRequest);

        assertThat(response.plate()).isEqualTo("MWK737");

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(carCaptor.capture());
        assertThat(carCaptor.getValue().getPlate()).isEqualTo("MWK737");
        assertThat(carCaptor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void create_shouldThrowWhenPlateAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.existsByUserIdAndPlate(1L, "MWK737")).thenReturn(true);

        assertThatThrownBy(() -> carService.create(1L, carRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ya tienes un auto registrado con esa placa");
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.create(1L, carRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void update_shouldUpdateOwnedCar() {
        when(carRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(car));
        when(carRepository.existsByUserIdAndPlateAndIdNot(1L, "ABC123", 10L)).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarRequest updateRequest = new CarRequest("Mazda", "CX-5", 2022, "ABC123", "Negro", null);
        CarResponse response = carService.update(1L, 10L, updateRequest);

        assertThat(response.brand()).isEqualTo("Mazda");
        assertThat(response.plate()).isEqualTo("ABC123");
        assertThat(response.photoUrl()).isNull();
    }

    @Test
    void update_shouldThrowWhenDuplicatePlate() {
        when(carRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(car));
        when(carRepository.existsByUserIdAndPlateAndIdNot(1L, "ABC123", 10L)).thenReturn(true);

        CarRequest updateRequest = new CarRequest("Mazda", "CX-5", 2022, "ABC123", "Negro", null);

        assertThatThrownBy(() -> carService.update(1L, 10L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ya tienes un auto registrado con esa placa");
    }

    @Test
    void delete_shouldRemoveOwnedCar() {
        when(carRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(car));

        carService.delete(1L, 10L);

        verify(carRepository).delete(car);
    }

    @Test
    void delete_shouldThrowWhenCarNotFound() {
        when(carRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.delete(1L, 10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Auto no encontrado");
    }
}
