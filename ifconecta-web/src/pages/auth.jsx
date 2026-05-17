import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Button, Field, Input, PwStrength, Select, pwScore, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import { BrandMark } from '../components/layout.jsx';
import { EsqueciSenhaDialog } from '../components/modals.jsx';
import { useAuth } from '../store/AuthContext.jsx';
import * as authService from '../services/auth.js';
import * as cursosService from '../services/cursos.js';
import { extractErrorMessage } from '../services/api.js';

function AuthArt() {
  return (
    <div className="auth-art">
      <div className="auth-art-content">
        <div className="auth-art-brand">
          <BrandMark size={38} />
          <span>IFConecta</span>
        </div>
      </div>
      <div className="auth-art-content" style={{ marginTop: 'auto', position: 'relative', zIndex: 1 }}>
        <div className="auth-art-hero">
          A rede do <em>Campus Salto</em>, feita por quem vive o campus.
        </div>
        <div className="auth-art-tag">
          Timeline, clubes, comunicados e turmas — tudo em um só lugar.
          Sem ruído de redes sociais, com a cara da nossa comunidade.
        </div>
      </div>
    </div>
  );
}

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const toast = useToast();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [errs, setErrs] = useState({});
  const [loading, setLoading] = useState(false);
  const [esqueciOpen, setEsqueciOpen] = useState(false);

  const submit = async (e) => {
    e?.preventDefault();
    const newErrs = {};
    if (!email) newErrs.email = 'Informe seu email.';
    else if (!/^\S+@\S+\.\S+$/.test(email)) newErrs.email = 'Email em formato inválido.';
    if (!senha) newErrs.senha = 'Informe sua senha.';
    setErrs(newErrs);
    if (Object.keys(newErrs).length) return;

    setLoading(true);
    try {
      await login(email, senha);
      toast.success('Bem-vindo(a) de volta!');
      navigate('/timeline');
    } catch (err) {
      toast.error('Falha ao entrar', extractErrorMessage(err, 'Email ou senha inválidos.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell" data-screen-label="Login">
      <AuthArt />
      <div className="auth-form-wrap">
        <form className="auth-form" onSubmit={submit}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
            <BrandMark size={32} />
            <span style={{ fontWeight: 700, fontSize: 17 }}>IFConecta</span>
          </div>
          <h1>Acesse sua conta</h1>
          <p>Use seu email institucional do IFSP.</p>

          <Field label="Email" error={errs.email}>
            <Input
              icon="mail"
              type="email"
              autoComplete="email"
              value={email}
              error={errs.email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="seu.nome@aluno.ifsp.edu.br"
            />
          </Field>

          <Field
            label="Senha"
            rightLabel={<a className="auth-link" onClick={() => setEsqueciOpen(true)}>Esqueci minha senha</a>}
            error={errs.senha}
          >
            <div style={{ position: 'relative' }}>
              <Input
                icon="lock"
                type={showPw ? 'text' : 'password'}
                autoComplete="current-password"
                value={senha}
                error={errs.senha}
                onChange={(e) => setSenha(e.target.value)}
                placeholder="••••••••"
                style={{ paddingRight: 40 }}
              />
              <button
                type="button"
                onClick={() => setShowPw(!showPw)}
                aria-label={showPw ? 'Ocultar senha' : 'Mostrar senha'}
                style={{ position: 'absolute', right: 8, top: '50%', transform: 'translateY(-50%)', border: 0, background: 'transparent', color: 'var(--fg-subtle)', cursor: 'pointer', padding: 6, display: 'grid', placeItems: 'center' }}
              >
                <Icon name={showPw ? 'eyeOff' : 'eye'} size={16} />
              </button>
            </div>
          </Field>

          <Button type="submit" block loading={loading}>Entrar</Button>

          <div className="auth-form-foot">
            Não tem uma conta? <a className="auth-link" onClick={() => navigate('/cadastro')}>Cadastrar como aluno</a>
          </div>

          <div className="card-flat" style={{ padding: 11, borderRadius: 'var(--r-md)', marginTop: 8, fontSize: 12, color: 'var(--fg-muted)', display: 'flex', gap: 8 }}>
            <Icon name="info" size={14} style={{ flexShrink: 0, marginTop: 1 }} />
            <span>Professores e servidores devem aguardar o convite institucional por email para definir senha.</span>
          </div>
        </form>
      </div>
      <EsqueciSenhaDialog open={esqueciOpen} onClose={() => setEsqueciOpen(false)} />
    </div>
  );
}

export function CadastroPage() {
  const navigate = useNavigate();
  const toast = useToast();
  const [cursos, setCursos] = useState([]);
  const [cursosLoading, setCursosLoading] = useState(true);
  const [form, setForm] = useState({
    nome: '', email: '', senha: '', confirma: '', prontuario: '', cursoId: '',
  });
  const [errs, setErrs] = useState({});
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);

  useEffect(() => {
    let cancelled = false;
    cursosService.listar()
      .then((lista) => { if (!cancelled) setCursos(lista); })
      .catch(() => { if (!cancelled) toast.error('Não foi possível carregar os cursos.'); })
      .finally(() => { if (!cancelled) setCursosLoading(false); });
    return () => { cancelled = true; };
  }, []);

  const update = (k, v) => {
    setForm((f) => ({ ...f, [k]: v }));
    setErrs((e) => ({ ...e, [k]: undefined }));
  };

  const submit = async (e) => {
    e?.preventDefault();
    const newErrs = {};
    if (!form.nome.trim()) newErrs.nome = 'Informe seu nome completo.';
    else if (form.nome.trim().split(' ').length < 2) newErrs.nome = 'Informe nome e sobrenome.';
    if (!form.email) newErrs.email = 'Informe seu email institucional.';
    else if (!/^\S+@aluno\.ifsp\.edu\.br$/.test(form.email)) newErrs.email = 'Use seu email @aluno.ifsp.edu.br';
    if (!form.senha) newErrs.senha = 'Crie uma senha.';
    else if (form.senha.length < 8) newErrs.senha = 'A senha precisa ter pelo menos 8 caracteres.';
    else if (pwScore(form.senha) < 2) newErrs.senha = 'A senha precisa ser mais forte.';
    if (form.senha !== form.confirma) newErrs.confirma = 'As senhas não conferem.';
    if (!form.prontuario) newErrs.prontuario = 'Informe seu prontuário.';
    else if (!/^SP\d{7}$/i.test(form.prontuario.trim())) newErrs.prontuario = 'Formato: SP + 7 dígitos.';
    if (!form.cursoId) newErrs.cursoId = 'Selecione seu curso.';
    setErrs(newErrs);
    if (Object.keys(newErrs).length) return;

    setLoading(true);
    try {
      await authService.registrarAluno({
        cursoId: form.cursoId,
        nome: form.nome.trim(),
        email: form.email.trim(),
        password: form.senha,
        prontuario: form.prontuario.trim().toUpperCase(),
      });
      setDone(true);
    } catch (err) {
      toast.error('Falha ao cadastrar', extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (done) {
    return (
      <div className="auth-shell" data-screen-label="Cadastro Sucesso">
        <AuthArt />
        <div className="auth-form-wrap">
          <div className="auth-form" style={{ textAlign: 'center', alignItems: 'center' }}>
            <div style={{
              width: 64, height: 64, borderRadius: 999, background: 'var(--primary-soft)',
              color: 'var(--primary)', display: 'grid', placeItems: 'center', margin: '0 auto',
            }}>
              <Icon name="mail" size={28} />
            </div>
            <h1 style={{ textAlign: 'center' }}>Verifique seu email</h1>
            <p style={{ textAlign: 'center' }}>
              Enviamos um link de ativação para<br />
              <b style={{ color: 'var(--fg)' }}>{form.email}</b>.<br />
              Clique nele para ativar sua conta.
            </p>
            <Button variant="outline" block onClick={() => navigate('/login')}>Voltar ao login</Button>
          </div>
        </div>
      </div>
    );
  }

  const cursosPorModalidade = cursos.reduce((acc, c) => {
    const m = c.modalidade || 'OUTROS';
    (acc[m] = acc[m] || []).push(c);
    return acc;
  }, {});
  const ordemModalidades = ['SUPERIOR', 'INTEGRADO', 'SUBSEQUENTE', 'POS_GRADUACAO', 'OUTROS'];
  const labelModalidade = {
    SUPERIOR: 'Superior',
    INTEGRADO: 'Integrado',
    SUBSEQUENTE: 'Subsequente',
    POS_GRADUACAO: 'Pós-graduação',
    OUTROS: 'Outros',
  };

  return (
    <div className="auth-shell" data-screen-label="Cadastro">
      <AuthArt />
      <div className="auth-form-wrap">
        <form className="auth-form" onSubmit={submit} style={{ maxWidth: 420 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
            <BrandMark size={32} />
            <span style={{ fontWeight: 700, fontSize: 17 }}>IFConecta</span>
          </div>
          <h1>Cadastro de aluno</h1>
          <p>Apenas com email <code style={{ fontFamily: 'var(--font-mono)', fontSize: 12.5 }}>@aluno.ifsp.edu.br</code>.</p>

          <Field label="Nome completo" error={errs.nome}>
            <Input icon="user" value={form.nome} onChange={(e) => update('nome', e.target.value)} error={errs.nome} placeholder="Seu nome completo" />
          </Field>

          <Field label="Email institucional" error={errs.email}>
            <Input icon="mail" type="email" value={form.email} onChange={(e) => update('email', e.target.value)} error={errs.email} placeholder="seu.nome@aluno.ifsp.edu.br" />
          </Field>

          <div className="grid-2">
            <Field label="Prontuário" error={errs.prontuario} hint={!errs.prontuario && 'Formato: SP + 7 dígitos'}>
              <Input value={form.prontuario} onChange={(e) => update('prontuario', e.target.value.toUpperCase())} error={errs.prontuario} placeholder="SP3012345" />
            </Field>
            <Field label="Curso" error={errs.cursoId}>
              <Select
                value={form.cursoId}
                onChange={(e) => update('cursoId', e.target.value)}
                error={errs.cursoId}
                disabled={cursosLoading}
              >
                <option value="">{cursosLoading ? 'Carregando…' : 'Selecione…'}</option>
                {ordemModalidades.map((mod) => {
                  const lista = cursosPorModalidade[mod];
                  if (!lista?.length) return null;
                  return (
                    <optgroup key={mod} label={labelModalidade[mod]}>
                      {lista.map((c) => (
                        <option key={c.id} value={c.id}>{c.sigla ? `${c.sigla} — ${c.nome}` : c.nome}</option>
                      ))}
                    </optgroup>
                  );
                })}
              </Select>
            </Field>
          </div>

          <Field label="Senha" error={errs.senha}>
            <Input icon="lock" type="password" value={form.senha} onChange={(e) => update('senha', e.target.value)} error={errs.senha} placeholder="Mínimo de 8 caracteres" />
            <PwStrength pw={form.senha} />
          </Field>

          <Field label="Confirmar senha" error={errs.confirma}>
            <Input icon="lock" type="password" value={form.confirma} onChange={(e) => update('confirma', e.target.value)} error={errs.confirma} placeholder="Repita a senha" />
          </Field>

          <Button type="submit" block loading={loading}>Criar conta</Button>

          <div className="auth-form-foot">
            Já tem uma conta? <a className="auth-link" onClick={() => navigate('/login')}>Entrar</a>
          </div>
        </form>
      </div>
    </div>
  );
}

export function AtivarPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [state, setState] = useState('loading');
  const [errMsg, setErrMsg] = useState('');
  const token = params.get('token');

  useEffect(() => {
    if (!token) {
      setState('err');
      setErrMsg('Token de ativação ausente na URL.');
      return;
    }
    let cancelled = false;
    authService.ativarConta(token)
      .then(() => { if (!cancelled) setState('ok'); })
      .catch((err) => {
        if (cancelled) return;
        setErrMsg(extractErrorMessage(err, 'Link inválido ou expirado.'));
        setState('err');
      });
    return () => { cancelled = true; };
  }, [token]);

  return (
    <div className="auth-shell" data-screen-label="Ativar">
      <AuthArt />
      <div className="auth-form-wrap">
        <div className="auth-form" style={{ textAlign: 'center', alignItems: 'center' }}>
          <BrandMark size={42} />
          {state === 'loading' && (
            <>
              <h1>Ativando sua conta…</h1>
              <p>Aguarde só um instante.</p>
              <div className="spinner spinner-lg" style={{ margin: '12px auto' }} />
            </>
          )}
          {state === 'ok' && (
            <>
              <div style={{ width: 64, height: 64, borderRadius: 999, background: 'oklch(0.95 0.06 155)', color: 'var(--success)', display: 'grid', placeItems: 'center' }}>
                <Icon name="checkCircle" size={32} />
              </div>
              <h1>Conta ativada!</h1>
              <p>Sua conta está pronta. Faça login para começar.</p>
              <Button block onClick={() => navigate('/login')}>Fazer login</Button>
            </>
          )}
          {state === 'err' && (
            <>
              <div style={{ width: 64, height: 64, borderRadius: 999, background: 'var(--danger-soft)', color: 'var(--danger)', display: 'grid', placeItems: 'center' }}>
                <Icon name="alertCircle" size={32} />
              </div>
              <h1>{errMsg || 'Link inválido ou expirado'}</h1>
              <p>Solicite um novo link de ativação para concluir o cadastro.</p>
              <Button variant="outline" block onClick={() => navigate('/cadastro')}>Voltar ao cadastro</Button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export function RedefinirSenhaPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [senha, setSenha] = useState('');
  const [confirma, setConfirma] = useState('');
  const [errs, setErrs] = useState({});
  const [loading, setLoading] = useState(false);
  const token = params.get('token') || '';

  const submit = async (e) => {
    e?.preventDefault();
    const newErrs = {};
    if (!token) newErrs.senha = 'Token ausente na URL — solicite um novo link.';
    if (!senha) newErrs.senha = 'Crie uma nova senha.';
    else if (senha.length < 8) newErrs.senha = 'A senha precisa ter pelo menos 8 caracteres.';
    else if (pwScore(senha) < 2) newErrs.senha = 'Senha precisa ser mais forte.';
    if (senha !== confirma) newErrs.confirma = 'As senhas não conferem.';
    setErrs(newErrs);
    if (Object.keys(newErrs).length) return;

    setLoading(true);
    try {
      await authService.redefinirSenha({ token, novaSenha: senha });
      toast.success('Senha redefinida!', 'Faça login com a nova senha.');
      navigate('/login');
    } catch (err) {
      toast.error('Não foi possível redefinir', extractErrorMessage(err, 'Link inválido ou expirado.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell" data-screen-label="Redefinir Senha">
      <AuthArt />
      <div className="auth-form-wrap">
        <form className="auth-form" onSubmit={submit}>
          <BrandMark size={32} />
          <h1>Redefinir senha</h1>
          <p>Escolha uma nova senha para acessar sua conta.</p>
          <Field label="Nova senha" error={errs.senha}>
            <Input icon="lock" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} error={errs.senha} placeholder="Mínimo 8 caracteres" />
            <PwStrength pw={senha} />
          </Field>
          <Field label="Confirmar nova senha" error={errs.confirma}>
            <Input icon="lock" type="password" value={confirma} onChange={(e) => setConfirma(e.target.value)} error={errs.confirma} placeholder="Repita a senha" />
          </Field>
          <Button type="submit" block loading={loading}>Redefinir senha</Button>
        </form>
      </div>
    </div>
  );
}

export function AtivarConvidadoPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [senha, setSenha] = useState('');
  const [confirma, setConfirma] = useState('');
  const [errs, setErrs] = useState({});
  const [loading, setLoading] = useState(false);
  const token = params.get('token') || '';

  const submit = async (e) => {
    e?.preventDefault();
    const newErrs = {};
    if (!token) newErrs.senha = 'Token ausente na URL — peça um novo convite.';
    if (!senha) newErrs.senha = 'Crie uma senha.';
    else if (senha.length < 8) newErrs.senha = 'A senha precisa ter pelo menos 8 caracteres.';
    else if (pwScore(senha) < 2) newErrs.senha = 'Senha precisa ser mais forte.';
    if (senha !== confirma) newErrs.confirma = 'As senhas não conferem.';
    setErrs(newErrs);
    if (Object.keys(newErrs).length) return;

    setLoading(true);
    try {
      await authService.ativarConvidado({ token, novaSenha: senha });
      toast.success('Senha definida!', 'Faça login para continuar.');
      navigate('/login');
    } catch (err) {
      toast.error('Falha ao ativar', extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell" data-screen-label="Ativar Convidado">
      <AuthArt />
      <div className="auth-form-wrap">
        <form className="auth-form" onSubmit={submit}>
          <BrandMark size={32} />
          <h1>Defina sua senha</h1>
          <p>Você foi convidado a se juntar ao IFConecta. Crie uma senha para acessar sua conta.</p>
          <Field label="Nova senha" error={errs.senha}>
            <Input icon="lock" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} error={errs.senha} placeholder="Mínimo 8 caracteres" />
            <PwStrength pw={senha} />
          </Field>
          <Field label="Confirmar senha" error={errs.confirma}>
            <Input icon="lock" type="password" value={confirma} onChange={(e) => setConfirma(e.target.value)} error={errs.confirma} placeholder="Repita a senha" />
          </Field>
          <Button type="submit" block loading={loading}>Definir senha e continuar</Button>
        </form>
      </div>
    </div>
  );
}
