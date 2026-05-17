import React, { useEffect, useMemo, useState } from 'react';
import {
  Avatar, Button, Checkbox, Dialog, Field, Input, RadioCard, Select, Textarea, useToast,
} from './ui.jsx';
import { Icon } from './icons.jsx';
import { podeComunicar, useAuth } from '../store/AuthContext.jsx';
import { useNotificacoesBadge } from '../store/NotificacoesContext.jsx';
import * as postsService from '../services/posts.js';
import * as clubesService from '../services/clubes.js';
import * as notificacoesService from '../services/notificacoes.js';
import * as authService from '../services/auth.js';
import { extractErrorMessage } from '../services/api.js';

const POST_LIMIT = 1000;

export function CreatePostDialog({ open, onClose, defaultClubeId, onCreated }) {
  const { me } = useAuth();
  const toast = useToast();
  const [conteudo, setConteudo] = useState('');
  const [anonimo, setAnonimo] = useState(false);
  const [clubeId, setClubeId] = useState(defaultClubeId || '');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setConteudo('');
      setAnonimo(false);
      setClubeId(defaultClubeId || '');
    }
  }, [open, defaultClubeId]);

  const remaining = POST_LIMIT - conteudo.length;
  const counterClass = remaining < 0 ? 'over' : remaining < 60 ? 'near' : '';
  const canSubmit = conteudo.trim().length > 0 && conteudo.length <= POST_LIMIT && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await postsService.create({
        conteudo: conteudo.trim(),
        clubeId: clubeId || null,
        anonimo,
      });
      toast.success('Post publicado!', 'Já está visível na timeline.');
      onCreated?.();
      onClose();
    } catch (err) {
      toast.error('Não foi possível publicar', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Criar post"
      subtitle="Compartilhe algo com a comunidade"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            Publicar
          </Button>
        </>
      }
    >
      <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
        <Avatar name={me?.nome || ''} size="md" anonymous={anonimo} />
        <div>
          <div style={{ fontWeight: 600, fontSize: 14 }}>{anonimo ? 'Anônimo' : me?.nome}</div>
          <div style={{ fontSize: 12, color: 'var(--fg-subtle)' }}>
            {defaultClubeId ? 'publicando no clube selecionado' : 'publicando na timeline geral'}
          </div>
        </div>
      </div>

      <Field rightLabel={<span className={`char-counter ${counterClass}`}>{conteudo.length}/{POST_LIMIT}</span>}>
        <Textarea
          autoFocus
          rows={6}
          placeholder="No que você está pensando? Compartilhe uma dúvida, novidade ou descoberta…"
          value={conteudo}
          onChange={(e) => setConteudo(e.target.value)}
          maxLength={POST_LIMIT + 100}
        />
      </Field>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '4px 0' }}>
        <Checkbox checked={anonimo} onChange={setAnonimo} label="Postar como anônimo" />
        <div style={{ fontSize: 11.5, color: 'var(--fg-subtle)' }}>
          Seu nome ficará oculto na publicação
        </div>
      </div>
    </Dialog>
  );
}

export function CreateClubeDialog({ open, onClose, onCreated }) {
  const toast = useToast();
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [tipoAcesso, setTipoAcesso] = useState('PUBLICO');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) { setNome(''); setDescricao(''); setTipoAcesso('PUBLICO'); }
  }, [open]);

  const canSubmit = nome.trim().length >= 3 && descricao.trim().length >= 10 && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await clubesService.create({
        nome: nome.trim(),
        descricao: descricao.trim(),
        tipoAcesso,
      });
      toast.success('Clube criado!', `${nome.trim()} já está disponível na lista.`);
      onCreated?.();
      onClose();
    } catch (err) {
      toast.error('Não foi possível criar o clube', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Criar um clube"
      subtitle="Reúna pessoas em torno de um interesse comum"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            Criar clube
          </Button>
        </>
      }
    >
      <Field label="Nome do clube" hint="Mínimo de 3 caracteres. Deve ser único.">
        <Input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Ex.: Grupo de Estudos em Cibersegurança" />
      </Field>
      <Field label="Descrição" hint="Mínimo de 10 caracteres.">
        <Textarea
          rows={4}
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          placeholder="Conte do que se trata e quem deve participar."
        />
      </Field>
      <Field label="Tipo de acesso">
        <div className="radio-group">
          <RadioCard
            checked={tipoAcesso === 'PUBLICO'}
            onClick={() => setTipoAcesso('PUBLICO')}
            icon="globe"
            title="Público"
            desc="Qualquer pessoa pode entrar imediatamente, sem aprovação."
          />
          <RadioCard
            checked={tipoAcesso === 'PRIVADO'}
            onClick={() => setTipoAcesso('PRIVADO')}
            icon="lock"
            title="Privado"
            desc="Você (líder) aprova as solicitações de entrada uma a uma."
          />
        </div>
      </Field>
    </Dialog>
  );
}

export function EditarPerfilDialog({ open, onClose }) {
  const { me, refresh } = useAuth();
  const toast = useToast();
  const [nome, setNome] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) setNome(me?.nome || '');
  }, [open, me]);

  const trimmed = nome.trim();
  const valido = trimmed.length > 0 && trimmed.split(/\s+/).length >= 2;
  const mudou = trimmed !== (me?.nome || '').trim();
  const canSubmit = valido && mudou && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await authService.atualizarMeuPerfil({ nome: trimmed });
      await refresh();
      toast.success('Perfil atualizado.');
      onClose();
    } catch (err) {
      toast.error('Não foi possível atualizar o perfil', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Editar perfil"
      subtitle="Atualize as informações do seu perfil"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            Salvar
          </Button>
        </>
      }
    >
      <Field label="Nome completo" hint={!valido && trimmed.length > 0 ? 'Informe nome e sobrenome.' : 'Esse é o nome que aparece nas suas publicações.'}>
        <Input
          icon="user"
          autoFocus
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          placeholder="Seu nome completo"
        />
      </Field>
      <div className="card-flat" style={{ padding: 11, borderRadius: 'var(--r-md)', fontSize: 12, color: 'var(--fg-muted)', display: 'flex', gap: 8 }}>
        <Icon name="info" size={14} style={{ flexShrink: 0, marginTop: 1 }} />
        <span>E-mail, prontuário e SIAPE são vínculos institucionais e não podem ser alterados aqui.</span>
      </div>
    </Dialog>
  );
}

export function ComunicadoDialog({ open, onClose }) {
  const { me } = useAuth();
  const { refresh: refreshBadge } = useNotificacoesBadge();
  const toast = useToast();
  const [titulo, setTitulo] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [tipoAlvo, setTipoAlvo] = useState('GERAL');
  const [alvoId, setAlvoId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [clubesLiderados, setClubesLiderados] = useState([]);
  const [carregandoClubes, setCarregandoClubes] = useState(false);

  const podeGeral = podeComunicar(me);
  const temClubesLiderados = clubesLiderados.length > 0;

  useEffect(() => {
    if (!open) return;
    setTitulo('');
    setMensagem('');
    setAlvoId('');
    setTipoAlvo(podeGeral ? 'GERAL' : 'CLUBE');

    let cancelled = false;
    setCarregandoClubes(true);
    (async () => {
      try {
        const meus = await clubesService.listarMeus();
        if (!meus.length) {
          if (!cancelled) setClubesLiderados([]);
          return;
        }
        const detalhes = await Promise.all(
          meus.map((c) => clubesService.getDetail(c.id).catch(() => null))
        );
        if (cancelled) return;
        const liderados = detalhes
          .filter((d) => d && d.souLider)
          .map((d) => ({ id: d.id, nome: d.nome }));
        setClubesLiderados(liderados);
      } catch {
        if (!cancelled) setClubesLiderados([]);
      } finally {
        if (!cancelled) setCarregandoClubes(false);
      }
    })();
    return () => { cancelled = true; };
  }, [open, podeGeral]);

  useEffect(() => { setAlvoId(''); }, [tipoAlvo]);

  const opcoesAlvo = useMemo(() => {
    const arr = [];
    if (podeGeral) arr.push({ value: 'GERAL', label: 'Toda a comunidade' });
    if (temClubesLiderados) arr.push({ value: 'CLUBE', label: 'Um clube que lidero' });
    return arr;
  }, [podeGeral, temClubesLiderados]);

  const needsAlvo = tipoAlvo === 'CLUBE';
  const canSubmit = titulo.trim().length >= 3
    && mensagem.trim().length >= 10
    && opcoesAlvo.length > 0
    && (!needsAlvo || alvoId)
    && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await notificacoesService.enviar({
        titulo: titulo.trim(),
        mensagem: mensagem.trim(),
        tipoAlvo,
        alvoId: needsAlvo ? alvoId : null,
      });
      toast.success('Comunicado enviado!', 'Os destinatários serão notificados.');
      refreshBadge();
      onClose();
    } catch (err) {
      toast.error('Não foi possível enviar', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Enviar comunicado"
      subtitle="Comunique-se com a comunidade ou com um clube que você lidera"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button
            variant="primary"
            icon={<Icon name="send" size={14} />}
            onClick={handleSubmit}
            disabled={!canSubmit}
            loading={submitting}
          >
            Enviar
          </Button>
        </>
      }
    >
      {opcoesAlvo.length === 0 && !carregandoClubes && (
        <div className="card-flat" style={{ padding: 12, borderRadius: 'var(--r-md)', fontSize: 13, color: 'var(--fg-muted)', display: 'flex', gap: 8 }}>
          <Icon name="alertCircle" size={14} style={{ flexShrink: 0, marginTop: 2 }} />
          <span>Você ainda não pode enviar comunicados. Apenas servidores institucionais, administradores e líderes de clube têm permissão.</span>
        </div>
      )}

      <Field label="Título">
        <Input value={titulo} onChange={(e) => setTitulo(e.target.value)} placeholder="Ex.: Aula da próxima segunda cancelada" maxLength={100} />
      </Field>
      <Field label="Mensagem">
        <Textarea rows={5} value={mensagem} onChange={(e) => setMensagem(e.target.value)} placeholder="Detalhe o comunicado…" />
      </Field>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        <Field label="Alcance">
          <Select value={tipoAlvo} onChange={(e) => setTipoAlvo(e.target.value)} disabled={opcoesAlvo.length <= 1}>
            {opcoesAlvo.length === 0 && <option value="">—</option>}
            {opcoesAlvo.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
          </Select>
        </Field>
        {needsAlvo ? (
          <Field label="Clube">
            <Select value={alvoId} onChange={(e) => setAlvoId(e.target.value)} disabled={carregandoClubes}>
              <option value="">{carregandoClubes ? 'Carregando…' : 'Selecione…'}</option>
              {clubesLiderados.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </Select>
          </Field>
        ) : tipoAlvo === 'GERAL' ? (
          <Field label="Quem recebe">
            <div className="card-flat" style={{ padding: '8px 12px', borderRadius: 'var(--r-md)', display: 'flex', alignItems: 'center', gap: 8, fontSize: 13 }}>
              <Icon name="globe" size={14} /> Todos os usuários ativos
            </div>
          </Field>
        ) : null}
      </div>
    </Dialog>
  );
}
