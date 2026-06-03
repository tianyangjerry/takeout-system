import { apiGet, apiPost, apiPut } from './request'
import type { LoginRequest, LoginResponse, RegisterRequest, User } from '../types/user'

export const authApi = {
  register: (payload: RegisterRequest) => apiPost<User, RegisterRequest>('/auth/register', payload),
  login: (payload: LoginRequest) => apiPost<LoginResponse, LoginRequest>('/auth/login', payload),
  me: () => apiGet<User>('/auth/me'),
  updateMe: (payload: Partial<User>) => apiPut<null, Partial<User>>('/users/me', payload),
}
