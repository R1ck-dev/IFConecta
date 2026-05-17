import api from './api.js';

export async function listar({ pagina = 0, tamanho = 15 } = {}) {
  const { data } = await api.get('/comunicados/minhas', { params: { pagina, tamanho } });
  return data;
}

export async function marcarLida(notificacaoId) {
  await api.patch(`/comunicados/${notificacaoId}/lida`);
}

export async function marcarTodasLidas() {
  const { data } = await api.patch('/comunicados/lidas');
  return data;
}

export async function contarNaoLidas() {
  const { data } = await api.get('/comunicados/minhas/nao-lidas/contagem');
  return data;
}

export async function enviar({ titulo, mensagem, tipoAlvo, alvoId = null }) {
  await api.post('/comunicados', { titulo, mensagem, tipoAlvo, alvoId });
}
