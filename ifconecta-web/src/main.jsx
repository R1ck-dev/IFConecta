import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './styles/tokens.css';
import './styles/components.css';
import App from './App.jsx';
import { AuthProvider } from './store/AuthContext.jsx';
import { ToastProvider } from './components/ui.jsx';
import { NotificacoesProvider } from './store/NotificacoesContext.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <AuthProvider>
      <ToastProvider>
        <NotificacoesProvider>
          <App />
        </NotificacoesProvider>
      </ToastProvider>
    </AuthProvider>
  </BrowserRouter>
);
