import { createContext, useContext, useState } from 'react'
import { authApi } from '../api/services'
import { storage } from '../utils/storage'
import { ROLE_ROUTES } from '../utils/constants'

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(storage.getUser())
  const [role, setRole] = useState(storage.getRole())
  const [loading, setLoading] = useState(false)

  const login = async (email, password) => {
    setLoading(true)

    try {
      const res = await authApi.login({ email, password })

      // ✅ FIX: handle both response formats
      const response = res.data?.data || res.data

      const { accessToken, refreshToken, role } = response

      if (!accessToken) {
        return { success: false, message: 'Invalid login response' }
      }

      // store
      storage.set(accessToken, refreshToken, { email, role })

      setUser({ email })
      setRole(role)

      // redirect
      const redirectTo = ROLE_ROUTES[role] || '/'

      return { success: true, redirectTo }

    } catch (err) {
      console.error("Login error:", err)
      return {
        success: false,
        message: err?.response?.data?.message || 'Login failed'
      }
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    storage.clear()
    setUser(null)
    setRole(null)
  }

  const isAuthenticated = Boolean(user && storage.getAccess())

  return (
    <AuthContext.Provider value={{ user, role, isAuthenticated, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)