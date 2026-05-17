import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { useAuth } from './AuthContext.jsx';
import * as notificacoesService from '../services/notificacoes.js';

const NotificacoesCtx = createContext({ naoLidas: 0, refresh: async () => {} });

export function NotificacoesProvider({ children }) {
  const { me, token } = useAuth();
  const [naoLidas, setNaoLidas] = useState(0);

  const refresh = useCallback(async () => {
    if (!me || !token) {
      setNaoLidas(0);
      return;
    }
    try {
      const { total } = await notificacoesService.contarNaoLidas();
      setNaoLidas(Number(total) || 0);
    } catch {
      // silencioso: badge é informativo, não vale interromper UX
    }
  }, [me, token]);

  useEffect(() => { refresh(); }, [refresh]);

  useEffect(() => {
    if (!me || !token) return undefined;
    const onFocus = () => { refresh(); };
    window.addEventListener('focus', onFocus);
    return () => window.removeEventListener('focus', onFocus);
  }, [me, token, refresh]);

  return (
    <NotificacoesCtx.Provider value={{ naoLidas, refresh }}>
      {children}
    </NotificacoesCtx.Provider>
  );
}

export function useNotificacoesBadge() {
  return useContext(NotificacoesCtx);
}
