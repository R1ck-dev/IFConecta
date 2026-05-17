import api from './api.js';

export async function listGeral({ pagina = 0, tamanho = 10 } = {}) {
  const { data } = await api.get('/posts', { params: { pagina, tamanho } });
  return data;
}

export async function getDetail(id) {
  const { data } = await api.get(`/posts/${id}`);
  return data;
}

export async function create({ conteudo, clubeId = null, anonimo = false }) {
  const body = { conteudo, clubeId, anonimo };
  await api.post('/posts', body);
}

export async function upvote(id) {
  await api.put(`/posts/${id}/upvote`);
}

export async function addComentario(postId, { conteudo }) {
  await api.post(`/posts/${postId}/comentarios`, { conteudo });
}
