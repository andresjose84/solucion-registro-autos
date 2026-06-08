package com.registroautos.mapper;

import com.registroautos.dto.CarResponse;
import com.registroautos.dto.UserResponse;
import com.registroautos.entity.Car;
import com.registroautos.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTest {

    @Test
    void toUserResponse_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setEmail("demo@registroautos.com");
        user.setFullName("Usuario Demo");
        user.setPassword("must-not-leak");

        UserResponse response = EntityMapper.toUserResponse(user);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("demo@registroautos.com");
        assertThat(response.fullName()).isEqualTo("Usuario Demo");
    }

    @Test
    void toCarResponse_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);

        Car car = new Car();
        car.setId(10L);
        car.setUser(user);
        car.setBrand("Chevrolet");
        car.setModel("Spark GT");
        car.setYear(2020);
        car.setPlate("MWK737");
        car.setColor("Rojo");
        car.setPhotoUrl("https://example.com/photo.jpg");

        CarResponse response = EntityMapper.toCarResponse(car);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.brand()).isEqualTo("Chevrolet");
        assertThat(response.model()).isEqualTo("Spark GT");
        assertThat(response.year()).isEqualTo(2020);
        assertThat(response.plate()).isEqualTo("MWK737");
        assertThat(response.color()).isEqualTo("Rojo");
        assertThat(response.photoUrl()).isEqualTo("https://example.com/photo.jpg");
    }
}
