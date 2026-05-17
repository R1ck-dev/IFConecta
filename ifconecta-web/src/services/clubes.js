import api from './api.js';

export async function listar({ pagina = 0, tamanho = 12 } = {}) {
  const { data } = await api.get('/clubes', { params: { pagina, tamanho } });
  return data;
}

export async function getDetail(clubeId) {
  const { data } = await api.get(`/clubes/${clubeId}`);
  return data;
}

export async function listarMeus() {
  const { data } = await api.get('/clubes/meus');
  return data;
}

export async function listarSolicitacoes(clubeId) {
  const { data } = await api.get(`/clubes/${clubeId}/solicitacoes`);
  return data;
}

export async function listarMembros(clubeId) {
  const { data } = await api.get(`/clubes/${clubeId}/membros`);
  return data;
}

export async function create({ nome, descricao, tipoAcesso }) {
  await api.post('/clubes', { nome, descricao, tipoAcesso });
}

export async function solicitarEntrada(clubeId) {
  await api.post(`/clubes/${clubeId}/membros`);
}

export async function avaliarMembro(clubeId, usuarioAlvoId, aprovado) {
  await api.put(`/clubes/${clubeId}/membros/${usuarioAlvoId}/avaliacao`, { aprovado });
}

export async function listarPosts(clubeId, { pagina = 0, tamanho = 10 } = {}) {
  const { data } = await api.get(`/clubes/${clubeId}/posts`, { params: { pagina, tamanho } });
  return data;
}
