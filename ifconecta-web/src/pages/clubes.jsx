import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate, useOutletContext, useParams } from 'react-router-dom';
import {
  Avatar, Badge, Button, Card, Empty, Skel, Tabs, timeAgo, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import { useAuth } from '../store/AuthContext.jsx';
import * as clubesService from '../services/clubes.js';
import { extractErrorMessage } from '../services/api.js';

function TipoChip({ tipoAcesso }) {
  return tipoAcesso === 'PUBLICO'
    ? <Badge variant="success" dot>Público</Badge>
    : <Badge variant="warning" dot>Privado</Badge>;
}

function ClubeCard({ clube, onOpen }) {
  return (
    <Card hoverable onClick={() => onOpen(clube.id)}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 10, marginBottom: 8 }}>
        <div style={{
          width: 40, height: 40, borderRadius: 10,
          background: `linear-gradient(135deg, var(--primary), oklch(0.5 0.14 calc(var(--primary-h) + 60)))`,
          color: 'white', display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 16,
          letterSpacing: '-0.02em', flexShrink: 0,
        }}>
          {clube.nome.split(' ').slice(0, 2).map((w) => w[0]).join('').toUpperCase()}
        </div>
        <TipoChip tipoAcesso={clube.tipoAcesso} />
      </div>
      <h3 style={{ margin: '4px 0 4px', fontSize: 16, fontWeight: 700, letterSpacing: '-0.01em', lineHeight: 1.2 }}>
        {clube.nome}
      </h3>
      <p style={{ margin: 0, fontSize: 13, color: 'var(--fg-muted)', lineHeight: 1.5, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
        {clube.descricao}
      </p>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 12, paddingTop: 12, borderTop: '1px solid var(--divider)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, color: 'var(--fg-muted)' }}>
          <Icon name="users" size={13} />
          <span><b style={{ color: 'var(--fg)' }}>{clube.quantidadeMembros}</b> {clube.quantidadeMembros === 1 ? 'membro' : 'membros'}</span>
        </div>
        <div style={{ flex: 1 }} />
        <Button size="xs" variant="soft" iconRight={<Icon name="chevronRight" size={12} />} onClick={(e) => { e.stopPropagation(); onOpen(clube.id); }}>
          Ver clube
        </Button>
      </div>
    </Card>
  );
}

function ClubeCardSkeleton() {
  return (
    <Card>
      <div style={{ display: 'flex', gap: 10, marginBottom: 8 }}>
        <Skel w={40} h={40} r={10} />
        <div style={{ flex: 1 }} />
        <Skel w={70} h={20} r={999} />
      </div>
      <Skel w="60%" h={16} />
      <Skel w="92%" h={12} style={{ marginTop: 8 }} />
      <Skel w="74%" h={12} style={{ marginTop: 6 }} />
    </Card>
  );
}

export function ClubesListPage() {
  const { openCreateClube, refreshSignal } = useOutletContext();
  const navigate = useNavigate();
  const toast = useToast();
  const [filter, setFilter] = useState('todos');
  const [q, setQ] = useState('');
  const [itens, setItens] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMais, setLoadingMais] = useState(false);

  const carregar = useCallback(async (pg = 0) => {
    const isFirst = pg === 0;
    if (isFirst) setLoading(true); else setLoadingMais(true);
    try {
      if (filter === 'meus') {
        const lista = await clubesService.listarMeus();
        setItens(lista);
        setPagina(0);
        setTotalPaginas(1);
      } else {
        const data = await clubesService.listar({ pagina: pg, tamanho: 12 });
        setItens((prev) => (isFirst ? data.itens : [...prev, ...data.itens]));
        setPagina(data.paginaAtual);
        setTotalPaginas(data.totalPaginas);
      }
    } catch (err) {
      toast.error('Não foi possível carregar clubes', extractErrorMessage(err));
    } finally {
      setLoading(false);
      setLoadingMais(false);
    }
  }, [filter, toast]);

  useEffect(() => {
    carregar(0);
  }, [carregar, refreshSignal]);

  const list = itens.filter((c) => {
    if (filter === 'publicos' && c.tipoAcesso !== 'PUBLICO') return false;
    if (filter === 'privados' && c.tipoAcesso !== 'PRIVADO') return false;
    if (q && !`${c.nome} ${c.descricao}`.toLowerCase().includes(q.toLowerCase())) return false;
    return true;
  });

  const podeCarregarMais = filter !== 'meus' && pagina + 1 < totalPaginas;

  return (
    <div className="app-content app-content-wide" data-screen-label="Clubes">
      <div className="page-hd">
        <div className="page-hd-text">
          <h1>Clubes</h1>
          <div className="page-sub">Grupos de interesse, projetos e iniciativas do campus</div>
        </div>
        <div className="page-hd-actions">
          <Button icon={<Icon name="plus" size={15} />} onClick={openCreateClube}>Criar clube</Button>
        </div>
      </div>

      <div className="filter-bar">
        <div className="input-prefix-wrap" style={{ flex: 1, maxWidth: 320 }}>
          <span className="prefix"><Icon name="search" size={16} /></span>
          <input className="input" placeholder="Buscar clubes…" value={q} onChange={(e) => setQ(e.target.value)} style={{ height: 36, fontSize: 13.5 }} />
        </div>
        <button className={`filter-pill ${filter === 'todos' ? 'active' : ''}`} onClick={() => setFilter('todos')}>Todos</button>
        <button className={`filter-pill ${filter === 'meus' ? 'active' : ''}`} onClick={() => setFilter('meus')}>
          <Icon name="bookmarkFill" size={13} /> Meus clubes
        </button>
        <button className={`filter-pill ${filter === 'publicos' ? 'active' : ''}`} onClick={() => setFilter('publicos')}>
          <Icon name="globe" size={13} /> Públicos
        </button>
        <button className={`filter-pill ${filter === 'privados' ? 'active' : ''}`} onClick={() => setFilter('privados')}>
          <Icon name="lock" size={13} /> Privados
        </button>
      </div>

      {loading ? (
        <div className="grid-clubes">
          <ClubeCardSkeleton /><ClubeCardSkeleton /><ClubeCardSkeleton />
        </div>
      ) : list.length === 0 ? (
        <Empty
          icon="users"
          title={filter === 'meus' ? 'Você ainda não participa de nenhum clube' : 'Nenhum clube encontrado'}
          message={q ? `Nada combina com "${q}".` : filter === 'meus' ? 'Explore a aba "Todos" e entre em algum.' : 'Que tal criar o primeiro?'}
          action={<Button icon={<Icon name="plus" size={14} />} onClick={openCreateClube}>Criar clube</Button>}
        />
      ) : (
        <>
          <div className="grid-clubes">
            {list.map((c) => <ClubeCard key={c.id} clube={c} onOpen={(id) => navigate(`/clubes/${id}`)} />)}
          </div>
          {podeCarregarMais && (
            <div style={{ textAlign: 'center', padding: 12 }}>
              <Button variant="outline" loading={loadingMais} onClick={() => carregar(pagina + 1)}>
                Carregar mais
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

function PostMini({ post }) {
  const navigate = useNavigate();
  const isAnon = post.autorNome == null;
  return (
    <article className="post" onClick={() => navigate(`/posts/${post.id}`)} style={{ cursor: 'pointer' }}>
      <div className="post-hd">
        <Avatar name={isAnon ? '' : post.autorNome} size="md" anonymous={isAnon} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <div className="post-author-name">{isAnon ? 'Anônimo' : post.autorNome}</div>
          <div className="post-author-meta"><span>{timeAgo(post.dataCriacao)}</span></div>
        </div>
      </div>
      <div className="post-body">{post.conteudo}</div>
      <div className="post-ft">
        <span className="post-action" aria-disabled>
          <Icon name="arrowUp" size={15} /> <span>{post.qtdUpvotes ?? 0}</span>
        </span>
        <span className="post-action" aria-disabled>
          <Icon name="messageSquare" size={15} /> <span>{post.qtdComentarios ?? 0}</span>
        </span>
      </div>
    </article>
  );
}

function PostsTab({ clubeId }) {
  const toast = useToast();
  const [posts, setPosts] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMais, setLoadingMais] = useState(false);

  const carregar = useCallback(async (pg = 0) => {
    const isFirst = pg === 0;
    if (isFirst) setLoading(true); else setLoadingMais(true);
    try {
      const data = await clubesService.listarPosts(clubeId, { pagina: pg, tamanho: 10 });
      setPosts((prev) => (isFirst ? data.itens : [...prev, ...data.itens]));
      setPagina(data.paginaAtual);
      setTotalPaginas(data.totalPaginas);
    } catch (err) {
      toast.error('Não foi possível carregar os posts do clube', extractErrorMessage(err));
    } finally {
      setLoading(false);
      setLoadingMais(false);
    }
  }, [clubeId, toast]);

  useEffect(() => { carregar(0); }, [carregar]);

  if (loading) {
    return <Card><Skel w="60%" h={14} /><Skel w="100%" h={12} style={{ marginTop: 12 }} /></Card>;
  }
  if (posts.length === 0) {
    return <Empty icon="messageSquare" title="Sem posts ainda" message="Quando alguém publicar algo neste clube, aparece aqui." />;
  }
  return (
    <div className="stack-md">
      {posts.map((p) => <PostMini key={p.id} post={p} />)}
      {pagina + 1 < totalPaginas && (
        <div style={{ textAlign: 'center', padding: 8 }}>
          <Button variant="outline" loading={loadingMais} onClick={() => carregar(pagina + 1)}>
            Carregar mais
          </Button>
        </div>
      )}
    </div>
  );
}

function MembrosTab() {
  return (
    <Empty
      icon="users"
      title="Em breve"
      message="A listagem de membros depende de um endpoint do backend que ainda não existe."
    />
  );
}

function SolicitacoesTab({ clubeId, onAvaliado }) {
  const toast = useToast();
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(null);

  const carregar = useCallback(async () => {
    setLoading(true);
    try {
      const lista = await clubesService.listarSolicitacoes(clubeId);
      setSolicitacoes(lista);
    } catch (err) {
      toast.error('Não foi possível carregar as solicitações', extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [clubeId, toast]);

  useEffect(() => { carregar(); }, [carregar]);

  const avaliar = async (usuarioAlvoId, aprovado) => {
    setSubmitting(usuarioAlvoId);
    try {
      await clubesService.avaliarMembro(clubeId, usuarioAlvoId, aprovado);
      toast.success(aprovado ? 'Membro aprovado.' : 'Solicitação rejeitada.');
      await carregar();
      onAvaliado?.();
    } catch (err) {
      toast.error('Não foi possível registrar a avaliação', extractErrorMessage(err));
    } finally {
      setSubmitting(null);
    }
  };

  if (loading) {
    return <Card><Skel w="60%" h={14} /></Card>;
  }
  if (solicitacoes.length === 0) {
    return <Empty icon="inbox" title="Sem solicitações pendentes" message="Quando alguém pedir para entrar, aparece aqui." />;
  }

  return (
    <div className="stack-md">
      {solicitacoes.map((s) => (
        <Card key={s.usuarioId} className="card-tight" style={{ display: 'flex', gap: 14, alignItems: 'center' }}>
          <Avatar name={s.usuarioNome} size="md" />
          <div style={{ flex: 1 }}>
            <div style={{ fontWeight: 600, fontSize: 14 }}>{s.usuarioNome}</div>
            <div style={{ fontSize: 11.5, color: 'var(--fg-subtle)', marginTop: 4 }}>
              Solicitado {timeAgo(s.dataSolicitacao)}
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <Button size="sm" variant="outline" disabled={submitting === s.usuarioId} onClick={() => avaliar(s.usuarioId, false)}>
              Rejeitar
            </Button>
            <Button
              size="sm"
              icon={<Icon name="check" size={13} />}
              loading={submitting === s.usuarioId}
              onClick={() => avaliar(s.usuarioId, true)}
            >
              Aprovar
            </Button>
          </div>
        </Card>
      ))}
    </div>
  );
}

function SobreTab({ clube }) {
  return (
    <Card>
      <h3 style={{ margin: 0 }}>Sobre o clube</h3>
      <p style={{ color: 'var(--fg-muted)', lineHeight: 1.6 }}>{clube.descricao}</p>
      <div className="divider" />
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, fontSize: 13.5 }}>
        <div>
          <div className="text-subtle text-xs">Líder</div>
          <div className="fw-600">{clube.liderNome}</div>
        </div>
        <div>
          <div className="text-subtle text-xs">Tipo</div>
          <div className="fw-600">{clube.tipoAcesso === 'PUBLICO' ? 'Público' : 'Privado'}</div>
        </div>
        <div>
          <div className="text-subtle text-xs">Membros</div>
          <div className="fw-600">{clube.quantidadeMembros}</div>
        </div>
      </div>
    </Card>
  );
}

export function ClubeDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { me } = useAuth();
  const [clube, setClube] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [tab, setTab] = useState('posts');
  const [joining, setJoining] = useState(false);

  const carregar = useCallback(async () => {
    setLoading(true);
    try {
      const data = await clubesService.getDetail(id);
      setClube(data);
      setNotFound(false);
    } catch (err) {
      if (err?.response?.status === 404) setNotFound(true);
      else if (err?.response?.status === 400) {
        const msg = extractErrorMessage(err);
        if (msg.toLowerCase().includes('não encontrado')) setNotFound(true);
        else toast.error('Não foi possível carregar o clube', msg);
      } else {
        toast.error('Não foi possível carregar o clube', extractErrorMessage(err));
      }
    } finally {
      setLoading(false);
    }
  }, [id, toast]);

  useEffect(() => { carregar(); }, [carregar]);

  if (loading) {
    return (
      <div className="app-content app-content-narrow">
        <Card><Skel w="60%" h={20} /><Skel w="100%" h={12} style={{ marginTop: 12 }} /></Card>
      </div>
    );
  }

  if (notFound || !clube) {
    return (
      <div className="app-content app-content-narrow">
        <Empty icon="alertCircle" title="Clube não encontrado" action={<Button onClick={() => navigate('/clubes')}>Ver clubes</Button>} />
      </div>
    );
  }

  const isLider = clube.souLider;
  const isMember = clube.souMembro;
  const isPendente = clube.minhaSituacao === 'PENDENTE';

  const tabs = [
    { value: 'posts', label: 'Posts', icon: 'messageSquare' },
    { value: 'membros', label: 'Membros', icon: 'users', count: clube.quantidadeMembros },
    ...(isLider ? [{ value: 'solicitacoes', label: 'Solicitações', icon: 'inbox' }] : []),
    { value: 'sobre', label: 'Sobre', icon: 'info' },
  ];

  const handleEntrar = async () => {
    setJoining(true);
    try {
      await clubesService.solicitarEntrada(clube.id);
      toast.success(clube.tipoAcesso === 'PUBLICO' ? 'Você entrou no clube!' : 'Solicitação enviada.');
      await carregar();
    } catch (err) {
      toast.error('Não foi possível concluir', extractErrorMessage(err));
    } finally {
      setJoining(false);
    }
  };

  return (
    <div className="app-content app-content-wide" data-screen-label={`Clube-${clube.id}`}>
      <button className="btn btn-ghost btn-sm" onClick={() => navigate('/clubes')} style={{ marginBottom: 12, paddingLeft: 6 }}>
        <Icon name="arrowLeft" size={15} /> Clubes
      </button>

      <div className="card" style={{ padding: 22, marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 16, flexWrap: 'wrap' }}>
          <div style={{
            width: 64, height: 64, borderRadius: 14,
            background: 'linear-gradient(135deg, var(--primary), oklch(0.5 0.14 calc(var(--primary-h) + 60)))',
            color: 'white', display: 'grid', placeItems: 'center', fontWeight: 800, fontSize: 26,
          }}>
            {clube.nome.split(' ').slice(0, 2).map((w) => w[0]).join('').toUpperCase()}
          </div>
          <div style={{ flex: 1, minWidth: 240 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
              <h1 style={{ margin: 0, fontSize: 22, fontWeight: 700, letterSpacing: '-0.015em' }}>{clube.nome}</h1>
              <TipoChip tipoAcesso={clube.tipoAcesso} />
              {isLider && <Badge variant="primary"><Icon name="award" size={11} /> Líder</Badge>}
              {isMember && !isLider && <Badge variant="success">Membro</Badge>}
              {isPendente && <Badge variant="warning">Solicitação pendente</Badge>}
            </div>
            <div style={{ fontSize: 13.5, color: 'var(--fg-muted)', marginTop: 6, lineHeight: 1.5 }}>{clube.descricao}</div>
            <div style={{ display: 'flex', gap: 18, marginTop: 12, flexWrap: 'wrap' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13, color: 'var(--fg-muted)' }}>
                <Icon name="users" size={14} /> <b style={{ color: 'var(--fg)' }}>{clube.quantidadeMembros}</b> membros
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13, color: 'var(--fg-muted)' }}>
                <Icon name="award" size={14} /> Líder: <b style={{ color: 'var(--fg)' }}>{clube.liderNome}</b>
              </div>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            {!isMember && !isPendente && (
              <Button onClick={handleEntrar} loading={joining}>
                {clube.tipoAcesso === 'PUBLICO' ? 'Entrar no clube' : 'Solicitar entrada'}
              </Button>
            )}
          </div>
        </div>
      </div>

      <Tabs value={tab} onChange={setTab} items={tabs} />

      {tab === 'posts' && <PostsTab clubeId={clube.id} />}
      {tab === 'membros' && <MembrosTab />}
      {tab === 'solicitacoes' && isLider && <SolicitacoesTab clubeId={clube.id} onAvaliado={carregar} />}
      {tab === 'sobre' && <SobreTab clube={clube} />}
    </div>
  );
}
