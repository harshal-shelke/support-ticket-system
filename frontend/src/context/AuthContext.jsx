import { createContext, useContext, useEffect, useState } from "react";
import {jwtDecode} from "jwt-decode";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);
  const [role, setRole] = useState(null);
  const [email, setEmail] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const t = localStorage.getItem("token");

    if (!t) {
      setLoading(false);
      return;
    }

    try {
      const decoded = jwtDecode(t);
      setToken(t);
      setRole(decoded.role);
      setEmail(decoded.sub);
    } catch {
      localStorage.removeItem("token");
    } finally {
      setLoading(false);
    }
  }, []);

  const login = (jwt) => {
    const decoded = jwtDecode(jwt);
    localStorage.setItem("token", jwt);
    setToken(jwt);
    setRole(decoded.role);
    setEmail(decoded.sub);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setRole(null);
    setEmail(null);
  };

  return (
    <AuthContext.Provider
      value={{ token, role, email, login, logout, loading }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
