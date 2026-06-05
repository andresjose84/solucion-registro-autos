export interface User {
  id: number;
  email: string;
  fullName: string;
}

export interface Car {
  id: number;
  brand: string;
  model: string;
  year: number;
  plate: string;
  color: string;
  photoUrl: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CarFormData {
  brand: string;
  model: string;
  year: number | '';
  plate: string;
  color: string;
  photoUrl: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface ApiError {
  message: string;
  fieldErrors?: Record<string, string>;
}

export interface CarFilters {
  search: string;
  brand: string;
  year: string;
}
