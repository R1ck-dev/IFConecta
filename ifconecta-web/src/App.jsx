import React, { useEffect, useState } from 'react';
import {
  Navigate, Outlet, Route, Routes, useLocation, useNavigate,
} from 'react-router-dom';
import { AppShell } from './components/layout.jsx';
import { Icon } from './components/icons.jsx';
import { Button, Empty } from './components/ui.jsx';
import {
  ComunicadoDialog, CreateClubeDialog, CreatePostDialog,
} from './components/modals.jsx';
import { useAuth } from './store/AuthContext.jsx';
import {
  AtivarConvidadoPage, AtivarPage, CadastroPage, LoginPage,
} from './pages/auth.jsx';
import { PostDetailPage, TimelinePage } from './pages/timeline.jsx';
import { ClubeDetailPage, ClubesListPage } from './pages/clubes.jsx';
import { NotificacoesPage } from './pages/notificacoes.jsx';
import { ProfileMePage, ProfilePublicPage } from './pages/perfil.jsx';
import { AcademicoPage } from './pages/academico.jsx';
import { AdminPage } from './pages/admin.jsx';

function useTheme() {
  const [theme, setTheme] = useState(() => {
    try { return localStorage.getItem('ifc-theme') || 'light'; }
    catch { return 'light'; }
  });
  useEffect(() => {
    if (theme === 'dark') document.documentElement.classList.add('dark');
    else document.documentElement.classList.remove('dark');
    try { localStorage.setItem('ifc-theme', theme); } catch {}
  }, [theme]);
  const toggle = () => setTheme((t) => (t === 'dark' ? 'light' : 'dark'));
  return { theme, toggle };
}

function PublicOnly() {
  const { token, loading } = useAuth();
  if (loading) return null;
  if (token) return <Navigate to="/timeline" replace />;
  return <Outlet />;
}

function PrivateRoute() {
  const { token, me, loading } = useAuth();
  const location = useLocation();
  if (loading) return <div style={{ padding: 24, color: 'var(--fg-muted)' }}>Carregando…</div>;
  if (!token || !me) return <Navigate to="/login" replace state={{ from: location }} />;
  return <Outlet />;
}

function AdminRoute() {
  const { me } = useAuth();
  if (me?.role !== 'ADMIN') return <Navigate to="/timeline" replace />;
  return <Outlet />;
}

function AppShellLayout({ theme, onToggleTheme }) {
  const [createPost, setCreatePost] = useState({ open: false, clubeId: null });
  const [createClubeOpen, setCreateClubeOpen] = useState(false);
  const [comunicadoOpen, setComunicadoOpen] = useState(false);
  const [refreshSignal, setRefreshSignal] = useState(0);

  const openCreatePost = (clubeId = null) => setCreatePost({ open: true, clubeId });

  useEffect(() => {
    const onKey = (e) => {
      const t = e.target;
      if (t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.isContentEditable)) return;
      if (e.key === 'c' && !e.metaKey && !e.ctrlKey) {
        e.preventDefault();
        openCreatePost();
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

  const ctx = {
    openCreatePost,
    openCreateClube: () => setCreateClubeOpen(true),
    openComunicado: () => setComunicadoOpen(true),
    refreshSignal,
    bumpRefresh: () => setRefreshSignal((s) => s + 1),
  };

  return (
    <AppShell
      theme={theme}
      onToggleTheme={onToggleTheme}
      onOpenComunicado={ctx.openComunicado}
    >
      <Outlet context={ctx} />

      <button className="fab" onClick={() => openCreatePost()} aria-label="Criar post">
        <Icon name="plus" size={22} />
      </button>

      <CreatePostDialog
        open={createPost.open}
        defaultClubeId={createPost.clubeId}
        onClose={() => setCreatePost({ open: false, clubeId: null })}
        onCreated={ctx.bumpRefresh}
      />
      <CreateClubeDialog
        open={createClubeOpen}
        onClose={() => setCreateClubeOpen(false)}
      />
      <ComunicadoDialog
        open={comunicadoOpen}
        onClose={() => setComunicadoOpen(false)}
      />
    </AppShell>
  );
}

function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="app-content app-content-narrow">
      <Empty
        icon="search"
        title="Página não encontrada"
        message="A rota que você tentou abrir não existe."
        action={<Button onClick={() => navigate('/timeline')}>Voltar à timeline</Button>}
      />
    </div>
  );
}

export default function App() {
  const { theme, toggle } = useTheme();

  return (
    <Routes>
      <Route element={<PublicOnly />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<CadastroPage />} />
      </Route>
      <Route path="/ativar" element={<AtivarPage />} />
      <Route path="/ativar-convidado" element={<AtivarConvidadoPage />} />

      <Route element={<PrivateRoute />}>
        <Route element={<AppShellLayout theme={theme} onToggleTheme={toggle} />}>
          <Route path="/" element={<Navigate to="/timeline" replace />} />
          <Route path="/timeline" element={<TimelinePage />} />
          <Route path="/posts/:id" element={<PostDetailPage />} />
          <Route path="/clubes" element={<ClubesListPage />} />
          <Route path="/clubes/:id" element={<ClubeDetailPage />} />
          <Route path="/notificacoes" element={<NotificacoesPage />} />
          <Route path="/perfil/me" element={<ProfileMePage />} />
          <Route path="/perfil/:id" element={<ProfilePublicPage />} />
          <Route path="/academico" element={<AcademicoPage />} />
          <Route path="/cursos" element={<AcademicoPage />} />
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminPage />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Route>
    </Routes>
  );
}
