import { create } from 'zustand'
import type { User } from '../types/user'

const tokenKey = 'takeout_token'
const userKey = 'takeout_user'

interface AuthState {
  token: string | null
  user: User | null
  setSession: (token: string, user: User) => void
  updateUser: (user: User) => void
  logout: () => void
}

function readUser() {
  const raw = localStorage.getItem(userKey)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as User
  } catch {
    localStorage.removeItem(userKey)
    return null
  }
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem(tokenKey),
  user: readUser(),
  setSession: (token, user) => {
    localStorage.setItem(tokenKey, token)
    localStorage.setItem(userKey, JSON.stringify(user))
    set({ token, user })
  },
  updateUser: (user) => {
    localStorage.setItem(userKey, JSON.stringify(user))
    set({ user })
  },
  logout: () => {
    localStorage.removeItem(tokenKey)
    localStorage.removeItem(userKey)
    set({ token: null, user: null })
  },
}))

export function getStoredToken() {
  return localStorage.getItem(tokenKey)
}

export function clearStoredSession() {
  localStorage.removeItem(tokenKey)
  localStorage.removeItem(userKey)
}
