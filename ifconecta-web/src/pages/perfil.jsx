import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  Avatar, Badge, Button, Card, Empty, Skel, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import { EditarPerfilDialog } from '../components/modals.jsx';
import { tipoLabel, useAuth } from '../store/AuthContext.jsx';
import api, { extractErrorMessage } from '../services/api.js';
import * as cursosService from '../services/cursos.js';
import * as clubesService from '../services/clubes.js';

function ProfileHero({ user, isMe, onEditar }) {
  const toast = useToast();
  return (
    <Card style={{ padding: 0 }}>
      <div style={{
        height: 96,
        background: 'linear-gradient(120deg, var(--primary) 0%, oklch(0.6 0.12 calc(var(--primary-h) + 30)) 100%)',
        position: 'relative',
        overflow: 'hidden',
      }}>
        <div style={{ position: 'absolute', inset: 0, opacity: 0.5,
          backgroundImage: 'radial-gradient(circle at 20% 50%, rgba(255,255,255,0.18) 0%, transparent 50%)' }} />
      </div>
      <div style={{ padding: '0 22px 22px', marginTop: -42, display: 'flex', alignItems: 'flex-end', gap: 18, flexWrap: 'wrap' }}>
        <Avatar name={user.nome} size="xl" style={{ border: '4px solid var(--surface)', boxShadow: 'var(--shadow-md)' }} />
        <div style={{ flex: 1, minWidth: 200, paddingBottom: 4 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
            <h1 className="profile-name">{user.nome}</h1>
            <Badge variant={user.tipo === 'PROFESSOR' || user.tipo === 'INSTITUCIONAL' ? 'primary' : 'default'} dot>
              {tipoLabel(user.tipo)}
            </Badge>
            {user.role === 'ADMIN' && <Badge variant="warning"><Icon name="shield" size={10} /> Admin</Badge>}
          </div>
          <div className="profile-meta">
            {isMe && user.emailAcad && <span>{user.emailAcad}</span>}
          </div>
        </div>
        {isMe && (
          <div style={{ display: 'flex', gap: 8, paddingBottom: 4 }}>
            <Button variant="outline" size="sm" icon={<Icon name="edit" size={13} />} onClick={onEditar}>
              Editar perfil
            </Button>
            <Button variant="outline" size="sm" icon={<Icon name="lock" size={13} />} onClick={() => toast.info('Em breve', 'Endpoint de alteração de senha ainda não disponível.')}>
              Alterar senha
            </Button>
          </div>
        )}
      </div>
    </Card>
  );
}

function ProfileInfo({ user, isMe, cursoNome, cursoLoading }) {
  const rows = [];
  if (user.tipo === 'ALUNO') {
    rows.push(['Prontuário', user.prontuario || '—']);
    rows.push(['Curso', cursoLoading ? '…' : (cursoNome || '—')]);
  }
  if (user.tipo === 'PROFESSOR') {
    rows.push(['SIAPE', isMe ? (user.siape || '—') : '••••••']);
    if (user.cursoId) rows.push(['Curso vinculado', cursoLoading ? '…' : (cursoNome || '—')]);
  }
  if (user.tipo === 'INSTITUCIONAL') {
    rows.push(['Setor', user.setor || '—']);
    rows.push(['Cargo', user.cargo || '—']);
  }
  if (isMe) {
    rows.push(['Email', user.emailAcad]);
    rows.push(['Status', user.status || 'Ativo']);
  }
  return (
    <Card>
      <h3 style={{ margin: 0, fontSize: 14.5, fontWeight: 600 }}>Informações</h3>
      <div className="divider" />
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, fontSize: 13.5 }}>
        {rows.map(([k, v]) => (
          <div key={k}>
            <div className="text-subtle text-xs">{k}</div>
            <div className="fw-600" style={{ marginTop: 2 }}>{v}</div>
          </div>
        ))}
      </div>
    </Card>
  );
}

function MeusClubesCard() {
  const [clubes, setClubes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    clubesService.listarMeus()
      .then((data) => { if (!cancelled) setClubes(data); })
      .catch(() => { if (!cancelled) setClubes([]); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, []);

  return (
    <Card>
      <div className="section-hd" style={{ margin: '0 0 12px' }}>
        <h2>Meus clubes</h2>
        <Link to="/clubes" className="more">Ver tudo</Link>
      </div>
      {loading ? (
        <Skel w="100%" h={48} />
      ) : clubes.length === 0 ? (
        <div style={{ fontSize: 13, color: 'var(--fg-muted)' }}>
          Você ainda não participa de nenhum clube. <Link to="/clubes" className="auth-link">Explorar</Link>
        </div>
      ) : (
        <div className="stack-md">
          {clubes.map((c) => (
            <Link key={c.id} to={`/clubes/${c.id}`} style={{ display: 'flex', alignItems: 'center', gap: 11 }}>
              <div style={{
                width: 32, height: 32, borderRadius: 8,
                background: 'linear-gradient(135deg, var(--primary), oklch(0.5 0.14 calc(var(--primary-h) + 60)))',
                color: 'white', display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 13,
              }}>
                {c.nome.split(' ').slice(0, 2).map((w) => w[0]).join('').toUpperCase()}
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 600, fontSize: 13, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {c.nome}
                </div>
                <div style={{ fontSize: 11.5, color: 'var(--fg-subtle)' }}>{c.quantidadeMembros} membros</div>
              </div>
              <Icon name="chevronRight" size={14} className="text-subtle" />
            </Link>
          ))}
        </div>
      )}
    </Card>
  );
}

function useCursoNome(cursoId) {
  const [cursoNome, setCursoNome] = useState(null);
  const [loading, setLoading] = useState(Boolean(cursoId));

  useEffect(() => {
    if (!cursoId) {
      setCursoNome(null);
      setLoading(false);
      return;
    }
    let cancelled = false;
    setLoading(true);
    cursosService.getCurso(cursoId)
      .then((c) => {
        if (cancelled) return;
        setCursoNome(c ? (c.sigla ? `${c.sigla} — ${c.nome}` : c.nome) : null);
      })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [cursoId]);

  return { cursoNome, loading };
}

export function ProfileMePage() {
  const { me } = useAuth();
  const { cursoNome, loading: cursoLoading } = useCursoNome(me?.cursoId);
  const [editarOpen, setEditarOpen] = useState(false);

  if (!me) return null;

  return (
    <div className="app-content app-content-wide" data-screen-label="PerfilMe">
      <div className="stack-lg">
        <ProfileHero user={me} isMe onEditar={() => setEditarOpen(true)} />
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 18, alignItems: 'start' }}>
          <ProfileInfo user={me} isMe cursoNome={cursoNome} cursoLoading={cursoLoading} />
          <MeusClubesCard />
        </div>
      </div>
      <EditarPerfilDialog open={editarOpen} onClose={() => setEditarOpen(false)} />
    </div>
  );
}

export function ProfilePublicPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const { cursoNome, loading: cursoLoading } = useCursoNome(user?.cursoId);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    api.get(`/usuarios/${id}`)
      .then(({ data }) => { if (!cancelled) setUser(data); })
      .catch((err) => {
        if (cancelled) return;
        if (err.response?.status === 404) setNotFound(true);
        else toast.error('Não foi possível carregar o perfil', extractErrorMessage(err));
      })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [id]);

  if (loading) {
    return <div className="app-content app-content-narrow"><Card><Skel w="60%" h={20} /><Skel w="100%" h={12} style={{ marginTop: 12 }} /></Card></div>;
  }

  if (notFound || !user) {
    return (
      <div className="app-content app-content-narrow">
        <Empty icon="user" title="Usuário não encontrado" action={<Button onClick={() => navigate('/timeline')}>Voltar</Button>} />
      </div>
    );
  }

  return (
    <div className="app-content app-content-wide" data-screen-label={`Perfil-${user.id}`}>
      <div className="stack-lg">
        <ProfileHero user={user} isMe={false} />
        <ProfileInfo user={user} isMe={false} cursoNome={cursoNome} cursoLoading={cursoLoading} />
      </div>
    </div>
  );
}
