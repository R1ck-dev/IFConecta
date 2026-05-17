import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import * as authService from '../services/auth.js';

const TOKEN_KEY = 'ifc-token';
const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [me, setMe] = useState(null);
  const [loading, setLoading] = useState(Boolean(localStorage.getItem(TOKEN_KEY)));

  useEffect(() => {
    if (!token) {
      setMe(null);
      setLoading(false);
      return;
    }
    let cancelled = false;
    setLoading(true);
    authService.getMe()
      .then((profile) => { if (!cancelled) setMe(profile); })
      .catch(() => {
        if (cancelled) return;
        localStorage.removeItem(TOKEN_KEY);
        setToken(null);
        setMe(null);
      })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [token]);

  const login = useCallback(async (email, password) => {
    const { token: novoToken } = await authService.login({ email, password });
    localStorage.setItem(TOKEN_KEY, novoToken);
    setToken(novoToken);
    const profile = await authService.getMe();
    setMe(profile);
    return profile;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    setMe(null);
  }, []);

  const refresh = useCallback(async () => {
    if (!token) return null;
    const profile = await authService.getMe();
    setMe(profile);
    return profile;
  }, [token]);

  const value = { me, token, loading, login, logout, refresh, authed: Boolean(token && me) };

  return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthCtx);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}

export function tipoLabel(tipo) {
  if (tipo === 'PROFESSOR') return 'Professor(a)';
  if (tipo === 'INSTITUCIONAL') return 'Servidor(a)';
  return 'Aluno(a)';
}

export function podeComunicar(me) {
  if (!me) return false;
  return me.tipo === 'PROFESSOR' || me.tipo === 'INSTITUCIONAL';
}
