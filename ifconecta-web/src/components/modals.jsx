import React, { useEffect, useMemo, useState } from 'react';
import {
  Avatar, Button, Checkbox, Dialog, Field, Input, PwStrength, pwScore, RadioCard, Select, Textarea, useToast,
} from './ui.jsx';
import { Icon } from './icons.jsx';
import { podeComunicar, useAuth } from '../store/AuthContext.jsx';
import { useNotificacoesBadge } from '../store/NotificacoesContext.jsx';
import * as postsService from '../services/posts.js';
import * as clubesService from '../services/clubes.js';
import * as notificacoesService from '../services/notificacoes.js';
import * as authService from '../services/auth.js';
import * as academicoService from '../services/academico.js';
import * as cursosService from '../services/cursos.js';
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

export function EsqueciSenhaDialog({ open, onClose }) {
  const toast = useToast();
  const [email, setEmail] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [enviado, setEnviado] = useState(false);

  useEffect(() => {
    if (open) { setEmail(''); setEnviado(false); }
  }, [open]);

  const valido = /^\S+@\S+\.\S+$/.test(email);
  const canSubmit = valido && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await authService.esqueciSenha(email.trim());
      setEnviado(true);
    } catch (err) {
      toast.error('Não foi possível processar a solicitação', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Esqueci minha senha"
      subtitle={enviado ? 'Verifique seu e-mail' : 'Informe seu e-mail institucional'}
      footer={enviado ? (
        <Button variant="primary" onClick={onClose}>Entendi</Button>
      ) : (
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            Enviar link
          </Button>
        </>
      )}
    >
      {enviado ? (
        <div style={{ textAlign: 'center', padding: '12px 0' }}>
          <div style={{
            width: 56, height: 56, borderRadius: 999, background: 'var(--primary-soft)',
            color: 'var(--primary)', display: 'grid', placeItems: 'center', margin: '0 auto 12px',
          }}>
            <Icon name="mail" size={24} />
          </div>
          <div style={{ fontSize: 14, color: 'var(--fg-muted)', lineHeight: 1.5 }}>
            Se houver uma conta com <b style={{ color: 'var(--fg)' }}>{email}</b>, enviaremos um link de redefinição. <br />
            O link expira em 1 hora.
          </div>
        </div>
      ) : (
        <Field label="E-mail institucional">
          <Input
            icon="mail"
            type="email"
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu.nome@aluno.ifsp.edu.br"
          />
        </Field>
      )}
    </Dialog>
  );
}

export function CreateTurmaDialog({ open, onClose, onCreated }) {
  const { me } = useAuth();
  const toast = useToast();
  const isAluno = me?.tipo === 'ALUNO';

  const [disciplinas, setDisciplinas] = useState([]);
  const [carregandoDisciplinas, setCarregandoDisciplinas] = useState(false);
  const [professores, setProfessores] = useState([]);
  const [carregandoProfessores, setCarregandoProfessores] = useState(false);
  const [disciplinaId, setDisciplinaId] = useState('');
  const [professorId, setProfessorId] = useState('');
  const [semestre, setSemestre] = useState('');
  const [codigoTurma, setCodigoTurma] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setDisciplinaId('');
    setSemestre('');
    setCodigoTurma('');
    setProfessorId('');

    let cancelled = false;
    setCarregandoDisciplinas(true);
    academicoService.listarDisciplinas()
      .then((lista) => { if (!cancelled) setDisciplinas(lista); })
      .catch(() => { if (!cancelled) setDisciplinas([]); })
      .finally(() => { if (!cancelled) setCarregandoDisciplinas(false); });

    if (isAluno) {
      setCarregandoProfessores(true);
      academicoService.listarProfessores()
        .then((lista) => { if (!cancelled) setProfessores(lista); })
        .catch(() => { if (!cancelled) setProfessores([]); })
        .finally(() => { if (!cancelled) setCarregandoProfessores(false); });
    }
    return () => { cancelled = true; };
  }, [open, isAluno]);

  const semestreOk = /^\d{4}\.[12]$/.test(semestre.trim());
  const codigoOk = codigoTurma.trim().length > 0;
  const professorOk = isAluno ? !!professorId : true;
  const canSubmit = disciplinaId && semestreOk && codigoOk && professorOk && me?.id && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await academicoService.criarTurma({
        disciplinaId,
        professorId: isAluno ? professorId : me.id,
        semestre: semestre.trim(),
        codigoTurma: codigoTurma.trim(),
      });
      if (isAluno) {
        toast.success('Solicitação enviada!', 'O professor escolhido vai revisar e decidir.');
      } else {
        toast.success('Turma criada!', 'Já aparece na sua lista de turmas lecionadas.');
      }
      onCreated?.();
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
      title={isAluno ? 'Sugerir nova turma' : 'Nova turma'}
      subtitle={isAluno
        ? 'A turma só fica ativa depois que o professor escolhido aprovar.'
        : 'Crie uma turma para uma disciplina existente'}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            {isAluno ? 'Enviar solicitação' : 'Criar turma'}
          </Button>
        </>
      }
    >
      <Field label="Disciplina">
        <Select value={disciplinaId} onChange={(e) => setDisciplinaId(e.target.value)} disabled={carregandoDisciplinas}>
          <option value="">{carregandoDisciplinas ? 'Carregando…' : 'Selecione…'}</option>
          {disciplinas.map((d) => (
            <option key={d.id} value={d.id}>
              {d.cursoSigla ? `${d.cursoSigla} — ${d.nome}` : d.nome}
            </option>
          ))}
        </Select>
      </Field>
      {isAluno && (
        <Field label="Professor responsável">
          <Select value={professorId} onChange={(e) => setProfessorId(e.target.value)} disabled={carregandoProfessores}>
            <option value="">{carregandoProfessores ? 'Carregando…' : 'Selecione…'}</option>
            {professores.map((p) => (
              <option key={p.id} value={p.id}>{p.nome}</option>
            ))}
          </Select>
        </Field>
      )}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        <Field label="Semestre" hint={semestre.length > 0 && !semestreOk ? 'Formato: AAAA.S (ex.: 2026.1)' : 'Formato: AAAA.S'}>
          <Input value={semestre} onChange={(e) => setSemestre(e.target.value)} placeholder="2026.1" />
        </Field>
        <Field label="Código da turma">
          <Input value={codigoTurma} onChange={(e) => setCodigoTurma(e.target.value)} placeholder="Ex.: T01" />
        </Field>
      </div>
      <div className="card-flat" style={{ padding: 11, borderRadius: 'var(--r-md)', fontSize: 12, color: 'var(--fg-muted)', display: 'flex', gap: 8 }}>
        <Icon name="info" size={14} style={{ flexShrink: 0, marginTop: 1 }} />
        <span>
          {isAluno
            ? 'Sua solicitação aparece na fila do professor escolhido. Ele pode aprovar ou rejeitar.'
            : <>Você (<b>{me?.nome}</b>) é definido como professor responsável.</>}
        </span>
      </div>
    </Dialog>
  );
}

export function AlterarSenhaDialog({ open, onClose }) {
  const toast = useToast();
  const [senhaAtual, setSenhaAtual] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirma, setConfirma] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) { setSenhaAtual(''); setNovaSenha(''); setConfirma(''); }
  }, [open]);

  const forteOk = pwScore(novaSenha) >= 2;
  const tamanhoOk = novaSenha.length >= 8;
  const confereOk = novaSenha === confirma;
  const diferenteAtual = novaSenha !== senhaAtual;
  const canSubmit = senhaAtual.length > 0
    && tamanhoOk
    && forteOk
    && confereOk
    && diferenteAtual
    && !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await authService.alterarMinhaSenha({ senhaAtual, novaSenha });
      toast.success('Senha alterada com sucesso.');
      onClose();
    } catch (err) {
      toast.error('Não foi possível alterar a senha', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const novaSenhaErro = novaSenha.length > 0 && !tamanhoOk
    ? 'Mínimo de 8 caracteres.'
    : novaSenha.length > 0 && !forteOk
      ? 'Senha precisa ser mais forte.'
      : novaSenha.length > 0 && !diferenteAtual
        ? 'A nova senha deve ser diferente da atual.'
        : null;
  const confirmaErro = confirma.length > 0 && !confereOk ? 'As senhas não conferem.' : null;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title="Alterar senha"
      subtitle="Informe sua senha atual e a nova senha"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" onClick={handleSubmit} disabled={!canSubmit} loading={submitting}>
            Alterar senha
          </Button>
        </>
      }
    >
      <Field label="Senha atual">
        <Input
          icon="lock"
          type="password"
          autoFocus
          autoComplete="current-password"
          value={senhaAtual}
          onChange={(e) => setSenhaAtual(e.target.value)}
          placeholder="Sua senha atual"
        />
      </Field>
      <Field label="Nova senha" error={novaSenhaErro}>
        <Input
          icon="lock"
          type="password"
          autoComplete="new-password"
          value={novaSenha}
          error={novaSenhaErro}
          onChange={(e) => setNovaSenha(e.target.value)}
          placeholder="Mínimo de 8 caracteres"
        />
        <PwStrength pw={novaSenha} />
      </Field>
      <Field label="Confirmar nova senha" error={confirmaErro}>
        <Input
          icon="lock"
          type="password"
          autoComplete="new-password"
          value={confirma}
          error={confirmaErro}
          onChange={(e) => setConfirma(e.target.value)}
          placeholder="Repita a nova senha"
        />
      </Field>
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
  const [clubesDisponiveis, setClubesDisponiveis] = useState([]);
  const [carregandoClubes, setCarregandoClubes] = useState(false);
  const [cursos, setCursos] = useState([]);
  const [carregandoCursos, setCarregandoCursos] = useState(false);
  const [turmasDisponiveis, setTurmasDisponiveis] = useState([]);
  const [carregandoTurmas, setCarregandoTurmas] = useState(false);

  const podeGeral = podeComunicar(me);
  const isProfessor = me?.tipo === 'PROFESSOR';
  const temClubes = clubesDisponiveis.length > 0;
  const temTurmas = turmasDisponiveis.length > 0;

  useEffect(() => {
    if (!open) return;
    setTitulo('');
    setMensagem('');
    setAlvoId('');

    let cancelled = false;
    setCarregandoClubes(true);
    (async () => {
      try {
        if (podeGeral) {
          // admin/institucional: todos os clubes da plataforma
          const pagina = await clubesService.listar({ pagina: 0, tamanho: 200 });
          if (cancelled) return;
          setClubesDisponiveis((pagina.itens || []).map((c) => ({ id: c.id, nome: c.nome })));
          return;
        }
        const meus = await clubesService.listarMeus();
        if (!meus.length) {
          if (!cancelled) setClubesDisponiveis([]);
          return;
        }
        if (isProfessor) {
          // professor: qualquer clube em que participa (membro aprovado)
          if (!cancelled) setClubesDisponiveis(meus.map((c) => ({ id: c.id, nome: c.nome })));
          return;
        }
        // demais: somente clubes que lidera
        const detalhes = await Promise.all(
          meus.map((c) => clubesService.getDetail(c.id).catch(() => null))
        );
        if (cancelled) return;
        const liderados = detalhes
          .filter((d) => d && d.souLider)
          .map((d) => ({ id: d.id, nome: d.nome }));
        setClubesDisponiveis(liderados);
      } catch {
        if (!cancelled) setClubesDisponiveis([]);
      } finally {
        if (!cancelled) setCarregandoClubes(false);
      }
    })();

    if (podeGeral) {
      setCarregandoCursos(true);
      cursosService.listar()
        .then((cs) => { if (!cancelled) setCursos(cs); })
        .catch(() => { if (!cancelled) setCursos([]); })
        .finally(() => { if (!cancelled) setCarregandoCursos(false); });
    }

    if (podeGeral || isProfessor) {
      setCarregandoTurmas(true);
      const carregar = podeGeral
        ? academicoService.listarTurmas({ pagina: 0, tamanho: 200 }).then((p) => p.itens || [])
        : academicoService.listarTurmasLecionadas();
      carregar
        .then((ts) => { if (!cancelled) setTurmasDisponiveis(ts); })
        .catch(() => { if (!cancelled) setTurmasDisponiveis([]); })
        .finally(() => { if (!cancelled) setCarregandoTurmas(false); });
    }

    return () => { cancelled = true; };
  }, [open, podeGeral, isProfessor]);

  const opcoesAlvo = useMemo(() => {
    const arr = [];
    if (podeGeral) arr.push({ value: 'GERAL', label: 'Toda a comunidade' });
    if (podeGeral) arr.push({ value: 'CURSO', label: 'Alunos de um curso' });
    if ((podeGeral || isProfessor) && temTurmas) arr.push({ value: 'TURMA', label: 'Alunos de uma turma' });
    if (temClubes) arr.push({ value: 'CLUBE', label: podeGeral ? 'Membros de um clube' : isProfessor ? 'Um clube que participo' : 'Um clube que lidero' });
    return arr;
  }, [podeGeral, isProfessor, temTurmas, temClubes]);

  useEffect(() => {
    if (!open) return;
    if (opcoesAlvo.length === 0) return;
    if (!opcoesAlvo.some((o) => o.value === tipoAlvo)) {
      setTipoAlvo(opcoesAlvo[0].value);
    }
  }, [open, opcoesAlvo, tipoAlvo]);

  useEffect(() => { setAlvoId(''); }, [tipoAlvo]);

  const needsAlvo = tipoAlvo === 'CLUBE' || tipoAlvo === 'CURSO' || tipoAlvo === 'TURMA';
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
      subtitle="Escolha o alcance: comunidade inteira, curso, turma que leciona ou clube que lidera"
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
      {opcoesAlvo.length === 0 && !carregandoClubes && !carregandoTurmas && (
        <div className="card-flat" style={{ padding: 12, borderRadius: 'var(--r-md)', fontSize: 13, color: 'var(--fg-muted)', display: 'flex', gap: 8 }}>
          <Icon name="alertCircle" size={14} style={{ flexShrink: 0, marginTop: 2 }} />
          <span>Você ainda não pode enviar comunicados. Precisa ser servidor institucional, admin, professor com turma ativa ou líder de clube.</span>
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
          <Select value={tipoAlvo} onChange={(e) => setTipoAlvo(e.target.value)} disabled={opcoesAlvo.length === 0}>
            {opcoesAlvo.length === 0 && <option value="">—</option>}
            {opcoesAlvo.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
          </Select>
        </Field>
        {tipoAlvo === 'CLUBE' ? (
          <Field label="Clube">
            <Select value={alvoId} onChange={(e) => setAlvoId(e.target.value)} disabled={carregandoClubes}>
              <option value="">{carregandoClubes ? 'Carregando…' : 'Selecione…'}</option>
              {clubesDisponiveis.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </Select>
          </Field>
        ) : tipoAlvo === 'CURSO' ? (
          <Field label="Curso">
            <Select value={alvoId} onChange={(e) => setAlvoId(e.target.value)} disabled={carregandoCursos}>
              <option value="">{carregandoCursos ? 'Carregando…' : 'Selecione…'}</option>
              {cursos.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.sigla ? `${c.sigla} — ${c.nome}` : c.nome}
                </option>
              ))}
            </Select>
          </Field>
        ) : tipoAlvo === 'TURMA' ? (
          <Field label="Turma">
            <Select value={alvoId} onChange={(e) => setAlvoId(e.target.value)} disabled={carregandoTurmas}>
              <option value="">{carregandoTurmas ? 'Carregando…' : 'Selecione…'}</option>
              {turmasDisponiveis.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.disciplinaNome} · {t.codigoTurma} ({t.semestre})
                </option>
              ))}
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
