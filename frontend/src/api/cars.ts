import api from './client';
import type { AuthResponse, Car, CarFormData, User } from '../types';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/login', { email, password });
  return data;
}

export async function register(fullName: string, email: string, password: string): Promise<User> {
  const { data } = await api.post<User>('/auth/register', { fullName, email, password });
  return data;
}

export async function fetchCars(params?: { search?: string; brand?: string; year?: string }): Promise<Car[]> {
  const { data } = await api.get<Car[]>('/cars', { params });
  return data;
}

export async function createCar(car: CarFormData): Promise<Car> {
  const { data } = await api.post<Car>('/cars', {
    ...car,
    year: Number(car.year),
    photoUrl: car.photoUrl || null,
  });
  return data;
}

export async function updateCar(id: number, car: CarFormData): Promise<Car> {
  const { data } = await api.put<Car>(`/cars/${id}`, {
    ...car,
    year: Number(car.year),
    photoUrl: car.photoUrl || null,
  });
  return data;
}

export async function deleteCar(id: number): Promise<void> {
  await api.delete(`/cars/${id}`);
}
