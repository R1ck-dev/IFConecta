import React, { useCallback, useEffect, useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Badge, Button, Empty, Skel, Tabs, timeAgo, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import { podeComunicar, useAuth } from '../store/AuthContext.jsx';
import { useNotificacoesBadge } from '../store/NotificacoesContext.jsx';
import * as notificacoesService from '../services/notificacoes.js';
import { extractErrorMessage } from '../services/api.js';

function TargetBadge({ tipo }) {
  const map = {
    GERAL: { icon: 'globe', label: 'Geral', variant: 'primary' },
    CURSO: { icon: 'graduation', label: 'Curso', variant: 'default' },
    TURMA: { icon: 'bookOpen', label: 'Turma', variant: 'default' },
    CLUBE: { icon: 'users', label: 'Clube', variant: 'default' },
  };
  const it = map[tipo] || map.GERAL;
  return (
    <Badge variant={it.variant}>
      <Icon name={it.icon} size={11} /> {it.label}
    </Badge>
  );
}

function NotifSkeleton() {
  return (
    <div className="notif-item">
      <Skel w={36} h={36} r={10} />
      <div style={{ flex: 1 }}>
        <Skel w="40%" h={12} />
        <Skel w="90%" h={12} style={{ marginTop: 8 }} />
        <Skel w="30%" h={10} style={{ marginTop: 6 }} />
      </div>
    </div>
  );
}

export function NotificacoesPage() {
  const { openComunicado } = useOutletContext();
  const { me } = useAuth();
  const { naoLidas, refresh } = useNotificacoesBadge();
  const toast = useToast();
  const canComunicar = podeComunicar(me);

  const [itens, setItens] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMais, setLoadingMais] = useState(false);
  const [marcandoTodas, setMarcandoTodas] = useState(false);
  const [filter, setFilter] = useState('todas');

  const carregar = useCallback(async (pg = 0) => {
    const isFirst = pg === 0;
    if (isFirst) setLoading(true); else setLoadingMais(true);
    try {
      const data = await notificacoesService.listar({ pagina: pg, tamanho: 15 });
      setItens((prev) => (isFirst ? data.itens : [...prev, ...data.itens]));
      setPagina(data.paginaAtual);
      setTotalPaginas(data.totalPaginas);
    } catch (err) {
      toast.error('Não foi possível carregar as notificações', extractErrorMessage(err));
    } finally {
      setLoading(false);
      setLoadingMais(false);
    }
  }, [toast]);

  useEffect(() => { carregar(0); }, [carregar]);

  const handleClickItem = async (n) => {
    if (n.lida) return;
    setItens((prev) => prev.map((it) => (it.id === n.id ? { ...it, lida: true } : it)));
    try {
      await notificacoesService.marcarLida(n.id);
      refresh();
    } catch (err) {
      setItens((prev) => prev.map((it) => (it.id === n.id ? { ...it, lida: false } : it)));
      toast.error('Não foi possível marcar como lida', extractErrorMessage(err));
    }
  };

  const handleMarcarTodas = async () => {
    setMarcandoTodas(true);
    try {
      await notificacoesService.marcarTodasLidas();
      setItens((prev) => prev.map((it) => ({ ...it, lida: true })));
      refresh();
      toast.success('Tudo em dia!');
    } catch (err) {
      toast.error('Não foi possível concluir', extractErrorMessage(err));
    } finally {
      setMarcandoTodas(false);
    }
  };

  const list = filter === 'nao-lidas' ? itens.filter((n) => !n.lida) : itens;

  return (
    <div className="app-content app-content-narrow" data-screen-label="Notificacoes">
      <div className="page-hd">
        <div className="page-hd-text">
          <h1>Notificações</h1>
          <div className="page-sub">
            {naoLidas > 0
              ? <>Você tem <b style={{ color: 'var(--primary)' }}>{naoLidas}</b> {naoLidas === 1 ? 'comunicado' : 'comunicados'} não {naoLidas === 1 ? 'lido' : 'lidos'}.</>
              : 'Nada pendente por aqui.'}
          </div>
        </div>
        <div className="page-hd-actions">
          {naoLidas > 0 && (
            <Button variant="outline" size="sm" icon={<Icon name="check" size={14} />} loading={marcandoTodas} onClick={handleMarcarTodas}>
              Marcar todas como lidas
            </Button>
          )}
          {canComunicar && (
            <Button size="sm" icon={<Icon name="send" size={14} />} onClick={openComunicado}>
              Novo comunicado
            </Button>
          )}
        </div>
      </div>

      <Tabs
        value={filter}
        onChange={setFilter}
        items={[
          { value: 'todas', label: 'Todas', count: itens.length },
          { value: 'nao-lidas', label: 'Não lidas', count: naoLidas },
        ]}
      />

      {loading ? (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <NotifSkeleton /><NotifSkeleton /><NotifSkeleton />
        </div>
      ) : list.length === 0 ? (
        <Empty
          icon="bell"
          title={filter === 'nao-lidas' ? 'Tudo em dia!' : 'Sem notificações'}
          message={filter === 'nao-lidas' ? 'Você está em dia com seus comunicados.' : 'Comunicados aparecerão aqui.'}
        />
      ) : (
        <>
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            {list.map((n, i) => (
              <div
                key={n.id}
                className={`notif-item ${!n.lida ? 'unread' : ''}`}
                onClick={() => handleClickItem(n)}
                style={i === list.length - 1 ? { borderBottom: 0 } : undefined}
              >
                <div style={{
                  width: 36, height: 36, borderRadius: 10,
                  background: n.lida ? 'var(--surface-2)' : 'var(--primary-soft)',
                  color: n.lida ? 'var(--fg-subtle)' : 'var(--primary)',
                  display: 'grid', placeItems: 'center', flexShrink: 0,
                }}>
                  <Icon name={
                    n.tipoAlvo === 'GERAL' ? 'globe'
                      : n.tipoAlvo === 'CURSO' ? 'graduation'
                      : n.tipoAlvo === 'TURMA' ? 'bookOpen' : 'users'
                  } size={16} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 8 }}>
                    <div className="notif-title">{n.titulo}</div>
                    <TargetBadge tipo={n.tipoAlvo} />
                  </div>
                  <div className="notif-body">{n.mensagem}</div>
                  <div className="notif-meta">
                    <span><b>{n.remetenteNome || 'Sistema'}</b></span>
                    <span>·</span>
                    <span>{timeAgo(n.dataCriacao)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
          {filter === 'todas' && pagina + 1 < totalPaginas && (
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
