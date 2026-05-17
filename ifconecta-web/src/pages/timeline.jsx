import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useOutletContext, useParams } from 'react-router-dom';
import {
  Avatar, Badge, Button, Checkbox, Empty, Skel, Textarea, timeAgo, useToast,
} from '../components/ui.jsx';
import { Icon, IconFilled } from '../components/icons.jsx';
import { useAuth } from '../store/AuthContext.jsx';
import * as postsService from '../services/posts.js';
import { extractErrorMessage } from '../services/api.js';

function PostHeader({ post, dense }) {
  const isAnon = post.anonimo;
  return (
    <div className="post-hd">
      <Avatar
        name={isAnon ? '' : (post.autorNome || '')}
        size={dense ? 'sm' : 'md'}
        anonymous={isAnon}
      />
      <div className="post-author" style={{ flex: 1, minWidth: 0 }}>
        <div className="post-author-name">
          {isAnon ? (
            <>
              <span>Anônimo</span>
              <Badge variant="outline">
                <Icon name="eyeOff" size={11} />
                <span style={{ marginLeft: 2 }}>anônimo</span>
              </Badge>
            </>
          ) : (
            <span>{post.autorNome}</span>
          )}
        </div>
        <div className="post-author-meta">
          <span>{timeAgo(post.dataCriacao)}</span>
        </div>
      </div>
    </div>
  );
}

function PostCard({ post, onUpvote, upvoting }) {
  const navigate = useNavigate();
  return (
    <article className="post" data-screen-label={`Post-${post.id}`}>
      <PostHeader post={post} />
      <div className="post-body">{post.conteudo}</div>
      <div className="post-ft">
        <button
          className={`post-action ${post.jaDeiUpvote ? 'active' : ''}`}
          onClick={() => onUpvote(post.id)}
          disabled={upvoting}
          aria-label={post.jaDeiUpvote ? 'Remover upvote' : 'Dar upvote'}
          aria-pressed={!!post.jaDeiUpvote}
        >
          {post.jaDeiUpvote ? <IconFilled name="arrowUp" size={15} /> : <Icon name="arrowUp" size={15} />}
          <span>{post.qtdUpvotes ?? 0}</span>
        </button>
        <button className="post-action" onClick={() => navigate(`/posts/${post.id}`)}>
          <Icon name="messageSquare" size={15} />
          <span>{post.qtdComentarios ?? 0}</span>
        </button>
        <div className="post-spacer" />
      </div>
    </article>
  );
}

function PostSkeleton() {
  return (
    <div className="card">
      <div className="post-hd">
        <Skel w={36} h={36} r={999} />
        <div style={{ flex: 1 }}>
          <Skel w="40%" h={12} />
          <Skel w="22%" h={10} style={{ marginTop: 6 }} />
        </div>
      </div>
      <Skel w="92%" h={12} style={{ marginBottom: 6 }} />
      <Skel w="76%" h={12} style={{ marginBottom: 6 }} />
      <Skel w="44%" h={12} />
    </div>
  );
}

export function TimelinePage() {
  const { openCreatePost, refreshSignal } = useOutletContext();
  const { me } = useAuth();
  const toast = useToast();
  const [posts, setPosts] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMais, setLoadingMais] = useState(false);
  const [upvotingId, setUpvotingId] = useState(null);

  const carregar = useCallback(async (pg = 0) => {
    const isFirst = pg === 0;
    if (isFirst) setLoading(true); else setLoadingMais(true);
    try {
      const data = await postsService.listGeral({ pagina: pg, tamanho: 10 });
      setPosts((prev) => (isFirst ? data.itens : [...prev, ...data.itens]));
      setPagina(data.paginaAtual);
      setTotalPaginas(data.totalPaginas);
    } catch (err) {
      toast.error('Não foi possível carregar a timeline', extractErrorMessage(err));
    } finally {
      setLoading(false);
      setLoadingMais(false);
    }
  }, [toast]);

  useEffect(() => {
    carregar(0);
  }, [carregar, refreshSignal]);

  const handleUpvote = useCallback(async (postId) => {
    setUpvotingId(postId);
    let snapshot = null;
    setPosts((prev) => prev.map((p) => {
      if (p.id !== postId) return p;
      snapshot = p;
      const delta = p.jaDeiUpvote ? -1 : 1;
      return { ...p, jaDeiUpvote: !p.jaDeiUpvote, qtdUpvotes: Math.max(0, (p.qtdUpvotes ?? 0) + delta) };
    }));
    try {
      await postsService.upvote(postId);
    } catch (err) {
      if (snapshot) setPosts((prev) => prev.map((p) => (p.id === postId ? snapshot : p)));
      toast.error('Não foi possível registrar o upvote', extractErrorMessage(err));
    } finally {
      setUpvotingId(null);
    }
  }, [toast]);

  const podeCarregarMais = pagina + 1 < totalPaginas;

  return (
    <div className="app-content app-content-narrow" data-screen-label="Timeline">
      <div className="page-hd">
        <div className="page-hd-text">
          <h1>Timeline</h1>
          <div className="page-sub">O que está acontecendo no campus agora</div>
        </div>
      </div>

      <div className="composer" onClick={() => openCreatePost()} style={{ marginBottom: 14 }}>
        <Avatar name={me?.nome || ''} size="md" />
        <div className="composer-faux">No que você está pensando, {(me?.nome || '').split(' ')[0] || 'colega'}?</div>
        <Button size="sm" icon={<Icon name="plus" size={14} />} onClick={(e) => { e.stopPropagation(); openCreatePost(); }}>
          Postar
        </Button>
      </div>

      <div className="stack-md">
        {loading ? (
          <>
            <PostSkeleton />
            <PostSkeleton />
            <PostSkeleton />
          </>
        ) : posts.length === 0 ? (
          <Empty
            icon="messageSquare"
            title="Nenhum post por aqui ainda"
            message="Seja o primeiro a compartilhar algo com a comunidade."
            action={<Button onClick={() => openCreatePost()} icon={<Icon name="plus" size={14} />}>Criar post</Button>}
          />
        ) : (
          posts.map((p) => (
            <PostCard key={p.id} post={p} onUpvote={handleUpvote} upvoting={upvotingId === p.id} />
          ))
        )}
        {!loading && podeCarregarMais && (
          <div style={{ textAlign: 'center', padding: 8 }}>
            <Button variant="outline" loading={loadingMais} onClick={() => carregar(pagina + 1)}>
              Carregar mais
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

function CommentItem({ comment }) {
  return (
    <div style={{ display: 'flex', gap: 11, padding: '12px 0', borderBottom: '1px solid var(--divider)' }}>
      <Avatar name={comment.autorNome || ''} size="sm" />
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
          <span style={{ fontWeight: 600, fontSize: 13.5 }}>{comment.autorNome || 'Usuário'}</span>
          <span style={{ fontSize: 11.5, color: 'var(--fg-subtle)' }}>{timeAgo(comment.dataCriacao)}</span>
        </div>
        <div style={{ fontSize: 13.5, marginTop: 4, lineHeight: 1.55, color: 'var(--fg)' }}>
          {comment.conteudo}
        </div>
      </div>
    </div>
  );
}

export function PostDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { me } = useAuth();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [novoComentario, setNovoComentario] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [upvoting, setUpvoting] = useState(false);
  const inputRef = useRef(null);

  const carregar = useCallback(async () => {
    setLoading(true);
    try {
      const data = await postsService.getDetail(id);
      setDetail(data);
      setNotFound(false);
    } catch (err) {
      if (err?.response?.status === 404) setNotFound(true);
      else toast.error('Não foi possível carregar o post', extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [id, toast]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  if (loading) {
    return (
      <div className="app-content app-content-narrow">
        <div className="card"><Skel w="60%" h={14} /><Skel w="100%" h={12} style={{ marginTop: 12 }} /><Skel w="80%" h={12} style={{ marginTop: 8 }} /></div>
      </div>
    );
  }

  if (notFound || !detail) {
    return (
      <div className="app-content app-content-narrow">
        <Empty
          icon="alertCircle"
          title="Post não encontrado"
          message="Este post pode ter sido removido."
          action={<Button onClick={() => navigate('/timeline')}>Voltar à timeline</Button>}
        />
      </div>
    );
  }

  const isAnon = detail.anonimo;
  const comentarios = detail.comentarios || [];

  const handleUpvote = async () => {
    setUpvoting(true);
    const prev = detail;
    const delta = detail.jaDeiUpvote ? -1 : 1;
    setDetail({
      ...detail,
      jaDeiUpvote: !detail.jaDeiUpvote,
      qtdUpvotes: Math.max(0, (detail.qtdUpvotes ?? 0) + delta),
    });
    try {
      await postsService.upvote(detail.id);
    } catch (err) {
      setDetail(prev);
      toast.error('Não foi possível registrar o upvote', extractErrorMessage(err));
    } finally {
      setUpvoting(false);
    }
  };

  const submitComentario = async () => {
    const texto = novoComentario.trim();
    if (!texto) return;
    setSubmitting(true);
    try {
      await postsService.addComentario(detail.id, { conteudo: texto });
      setNovoComentario('');
      await carregar();
    } catch (err) {
      toast.error('Não foi possível comentar', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="app-content app-content-narrow" data-screen-label={`PostDetail-${detail.id}`}>
      <button className="btn btn-ghost btn-sm" onClick={() => navigate('/timeline')} style={{ marginBottom: 14, paddingLeft: 6 }}>
        <Icon name="arrowLeft" size={15} /> Voltar
      </button>

      <article className="card" style={{ marginBottom: 18 }}>
        <div className="post-hd">
          <Avatar name={isAnon ? '' : (detail.autorNome || '')} size="md" anonymous={isAnon} />
          <div className="post-author" style={{ flex: 1, minWidth: 0 }}>
            <div className="post-author-name">
              {isAnon ? (
                <>
                  <span>Anônimo</span>
                  <Badge variant="outline">
                    <Icon name="eyeOff" size={11} />
                    <span style={{ marginLeft: 2 }}>anônimo</span>
                  </Badge>
                </>
              ) : (
                <span>{detail.autorNome}</span>
              )}
            </div>
            <div className="post-author-meta">
              <span>{timeAgo(detail.dataCriacao)}</span>
            </div>
          </div>
        </div>
        <div className="post-body" style={{ fontSize: 15.5 }}>{detail.conteudo}</div>
        <div className="post-ft" style={{ marginTop: 16 }}>
          <button
            className={`post-action ${detail.jaDeiUpvote ? 'active' : ''}`}
            onClick={handleUpvote}
            disabled={upvoting}
            style={{ padding: '8px 14px', fontSize: 14 }}
          >
            {detail.jaDeiUpvote ? <IconFilled name="arrowUp" size={16} /> : <Icon name="arrowUp" size={16} />}
            <span>{detail.qtdUpvotes ?? 0} upvotes</span>
          </button>
          <button className="post-action" onClick={() => inputRef.current?.focus()} style={{ padding: '8px 14px' }}>
            <Icon name="messageSquare" size={16} />
            <span>{comentarios.length} comentários</span>
          </button>
          <div className="post-spacer" />
        </div>
      </article>

      <div className="section-hd">
        <h2>Comentários</h2>
        <span className="badge">{comentarios.length}</span>
      </div>

      {comentarios.length === 0 ? (
        <Empty
          icon="messageSquare"
          title="Ainda sem comentários"
          message="Seja o primeiro a comentar este post."
        />
      ) : (
        <div className="card-flat card" style={{ padding: '0 18px' }}>
          {comentarios.map((c) => <CommentItem key={c.id} comment={c} />)}
        </div>
      )}

      <div style={{
        position: 'sticky',
        bottom: 12,
        marginTop: 18,
        background: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--r-lg)',
        padding: 14,
        boxShadow: 'var(--shadow-md)',
      }}>
        <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
          <Avatar name={me?.nome || ''} size="sm" />
          <Textarea
            ref={inputRef}
            rows={2}
            placeholder="Comente algo…"
            value={novoComentario}
            onChange={(e) => setNovoComentario(e.target.value)}
            style={{ flex: 1, minHeight: 0 }}
          />
        </div>
        <div style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', marginTop: 10 }}>
          <Button
            size="sm"
            onClick={submitComentario}
            icon={<Icon name="send" size={13} />}
            disabled={!novoComentario.trim() || submitting}
            loading={submitting}
          >
            Enviar
          </Button>
        </div>
      </div>
    </div>
  );
}
