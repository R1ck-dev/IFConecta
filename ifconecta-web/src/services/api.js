import axios from 'axios';

const baseURL = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api`;

const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ifc-token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    if (status === 401) {
      localStorage.removeItem('ifc-token');
      if (!window.location.pathname.startsWith('/login')) {
        window.location.assign('/login');
      }
    }
    return Promise.reject(error);
  }
);

export default api;

export function extractErrorMessage(error, fallback = 'Algo deu errado. Tente novamente.') {
  const data = error?.response?.data;
  if (!data) return fallback;
  if (typeof data === 'string') return data;
  if (data.erro) return data.erro;
  const firstField = Object.values(data).find((v) => typeof v === 'string');
  return firstField || fallback;
}
