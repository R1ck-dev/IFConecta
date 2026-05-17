import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Avatar, Button, Dropdown, MenuItem } from './ui.jsx';
import { Icon } from './icons.jsx';
import { podeComunicar, tipoLabel, useAuth } from '../store/AuthContext.jsx';
import { useNotificacoesBadge } from '../store/NotificacoesContext.jsx';

export function BrandMark({ size = 30 }) {
  return (
    <div className="hdr-brand-mark" style={{ width: size, height: size, fontSize: size * 0.45 }}>
      iC
    </div>
  );
}

export function Header({ onMenuClick, theme, onToggleTheme, onOpenComunicado }) {
  const { me, logout } = useAuth();
  const { naoLidas } = useNotificacoesBadge();
  const navigate = useNavigate();

  if (!me) return null;

  return (
    <header className="hdr" data-screen-label="Header">
      <button className="hdr-icon-btn hdr-menu-btn" onClick={onMenuClick} aria-label="Menu">
        <Icon name="menu" size={20} />
      </button>
      <Link to="/timeline" className="hdr-brand">
        <BrandMark />
        <span><b>IF</b><span style={{ color: 'var(--primary)' }}>Conecta</span></span>
      </Link>

      <div className="hdr-spacer" />

      <div className="hdr-search">
        <span className="hdr-search-icon"><Icon name="search" size={15} /></span>
        <input placeholder="Buscar pessoas, clubes, posts…" />
        <kbd className="hdr-kbd">⌘K</kbd>
      </div>

      {podeComunicar(me) && (
        <Button variant="soft" size="sm" icon={<Icon name="send" size={14} />} onClick={onOpenComunicado}>
          Comunicado
        </Button>
      )}

      <button className="hdr-icon-btn" onClick={onToggleTheme} aria-label="Alternar tema">
        <Icon name={theme === 'dark' ? 'sun' : 'moon'} size={18} />
      </button>

      <button
        className="hdr-icon-btn"
        onClick={() => navigate('/notificacoes')}
        aria-label="Notificações"
      >
        <Icon name="bell" size={18} />
        {naoLidas > 0 && <span className="dot">{naoLidas}</span>}
      </button>

      <Dropdown trigger={
        <button className="hdr-avatar-btn" aria-label="Menu da conta">
          <Avatar name={me.nome} size="md" />
        </button>
      }>
        {({ close }) => (
          <>
            <div className="menu-label">Conta</div>
            <div style={{ padding: '4px 10px 10px', borderBottom: '1px solid var(--divider)', marginBottom: 4 }}>
              <div style={{ fontWeight: 600, fontSize: 13.5 }}>{me.nome}</div>
              <div style={{ fontSize: 11.5, color: 'var(--fg-subtle)' }}>{me.emailAcad}</div>
            </div>
            <MenuItem icon="user" onClick={() => { navigate('/perfil/me'); close(); }}>Meu perfil</MenuItem>
            <MenuItem icon="bookmark" onClick={() => { navigate('/clubes'); close(); }}>Meus clubes</MenuItem>
            <MenuItem icon="settings" onClick={() => close()}>Configurações</MenuItem>
            <div className="menu-sep" />
            <MenuItem icon="logout" danger onClick={() => { logout(); navigate('/login'); close(); }}>Sair</MenuItem>
          </>
        )}
      </Dropdown>
    </header>
  );
}

export function Sidebar({ onClose }) {
  const { me } = useAuth();
  const { naoLidas } = useNotificacoesBadge();
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const isActive = (p) => pathname === p || pathname.startsWith(p + '/');

  if (!me) return null;

  const item = (icon, label, route, count) => (
    <button
      className={`sb-link ${isActive(route) ? 'active' : ''}`}
      onClick={() => { navigate(route); onClose?.(); }}
    >
      <Icon name={icon} size={18} />
      <span>{label}</span>
      {count > 0 && <span className="badge-count">{count}</span>}
    </button>
  );

  return (
    <aside className="sb" aria-label="Navegação principal">
      <div className="sb-section">Feed</div>
      {item('home', 'Timeline', '/timeline')}
      {item('users', 'Clubes', '/clubes')}
      {item('bell', 'Notificações', '/notificacoes', naoLidas)}

      <div className="sb-section">Acadêmico</div>
      {item('bookOpen', 'Minhas turmas', '/academico')}
      {item('graduation', 'Cursos', '/cursos')}

      <div className="sb-section">Você</div>
      {item('user', 'Meu perfil', '/perfil/me')}

      {me.role === 'ADMIN' && (
        <>
          <div className="sb-section">Admin</div>
          {item('shield', 'Painel', '/admin')}
        </>
      )}

      <div className="sb-user">
        <Avatar name={me.nome} size="md" />
        <div style={{ flex: 1, minWidth: 0 }}>
          <div className="sb-user-name" style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{me.nome}</div>
          <div className="sb-user-role">{tipoLabel(me.tipo)}</div>
        </div>
      </div>
    </aside>
  );
}

export function Footer() {
  return (
    <footer className="ftr">
      <span className="ftr-mark">IFConecta</span> — IFSP Campus Salto · v0.1 protótipo · &copy; 2026
    </footer>
  );
}

export function BottomNav() {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const isActive = (p) => pathname === p || pathname.startsWith(p + '/');
  const item = (icon, label, route, dot) => (
    <button className={`bn-item ${isActive(route) ? 'active' : ''}`} onClick={() => navigate(route)}>
      <Icon name={icon} size={20} />
      <span>{label}</span>
      {dot && <span className="dot" />}
    </button>
  );
  return (
    <nav className="bottom-nav" aria-label="Navegação rápida">
      {item('home', 'Início', '/timeline')}
      {item('users', 'Clubes', '/clubes')}
      {item('bell', 'Avisos', '/notificacoes', false)}
      {item('bookOpen', 'Aulas', '/academico')}
      {item('user', 'Você', '/perfil/me')}
    </nav>
  );
}

export function AppShell({ theme, onToggleTheme, children, onOpenComunicado }) {
  const [drawer, setDrawer] = useState(false);
  return (
    <div className={`app-shell theme-anim ${drawer ? 'sb-drawer-open' : ''}`}>
      <div className="app-main">
        <Header
          theme={theme}
          onToggleTheme={onToggleTheme}
          onMenuClick={() => setDrawer(true)}
          onOpenComunicado={onOpenComunicado}
        />
        <div style={{ display: 'flex', flex: 1, minHeight: 0 }}>
          <Sidebar onClose={() => setDrawer(false)} />
          {drawer && <div className="sb-backdrop" onClick={() => setDrawer(false)} />}
          <main style={{ flex: 1, minWidth: 0 }}>
            {children}
            <Footer />
          </main>
        </div>
      </div>
      <BottomNav />
    </div>
  );
}
