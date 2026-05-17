import api from './api.js';

export async function login({ email, password }) {
  const { data } = await api.post('/auth/login', { email, password });
  return data;
}

export async function getMe() {
  const { data } = await api.get('/usuarios/me');
  return data;
}

export async function registrarAluno({ cursoId, nome, email, password, prontuario }) {
  const { data } = await api.post('/usuarios/alunos', {
    cursoId, nome, email, password, prontuario,
  });
  return data;
}

export async function ativarConta(token) {
  const { data } = await api.get('/usuarios/ativar', { params: { token } });
  return data;
}

export async function ativarConvidado({ token, novaSenha }) {
  const { data } = await api.post('/usuarios/ativar-convidado', { token, novaSenha });
  return data;
}

export async function atualizarMeuPerfil({ nome }) {
  await api.patch('/usuarios/me', { nome });
}

export async function alterarMinhaSenha({ senhaAtual, novaSenha }) {
  await api.patch('/usuarios/me/senha', { senhaAtual, novaSenha });
}

export async function esqueciSenha(email) {
  await api.post('/usuarios/esqueci-senha', { email });
}

export async function redefinirSenha({ token, novaSenha }) {
  await api.post('/usuarios/redefinir-senha', { token, novaSenha });
}
