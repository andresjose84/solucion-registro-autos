package com.registroautos.mapper;

import com.registroautos.dto.CarResponse;
import com.registroautos.dto.UserResponse;
import com.registroautos.entity.Car;
import com.registroautos.entity.User;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName());
    }

    public static CarResponse toCarResponse(Car car) {
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getPlate(),
                car.getColor(),
                car.getPhotoUrl(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }
}
