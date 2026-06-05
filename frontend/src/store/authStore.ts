import { create } from 'zustand';
import { login as loginApi, register as registerApi } from '../api/cars';
import type { User } from '../types';

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (fullName: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  hydrate: () => void;
}

function loadStoredUser(): User | null {
  const raw = localStorage.getItem('user');
  if (!raw) return null;
  try {
    return JSON.parse(raw) as User;
  } catch {
    return null;
  }
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  user: loadStoredUser(),
  isAuthenticated: !!localStorage.getItem('token'),

  login: async (email, password) => {
    const response = await loginApi(email, password);
    localStorage.setItem('token', response.token);
    localStorage.setItem('user', JSON.stringify(response.user));
    set({ token: response.token, user: response.user, isAuthenticated: true });
  },

  register: async (fullName, email, password) => {
    await registerApi(fullName, email, password);
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ token: null, user: null, isAuthenticated: false });
  },

  hydrate: () => {
    const token = localStorage.getItem('token');
    const user = loadStoredUser();
    set({ token, user, isAuthenticated: !!token });
  },
}));
