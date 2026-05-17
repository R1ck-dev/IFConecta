import api from './api.js';

export async function listarTurmas({ pagina = 0, tamanho = 12, disciplinaId, semestre } = {}) {
  const params = { pagina, tamanho };
  if (disciplinaId) params.disciplinaId = disciplinaId;
  if (semestre) params.semestre = semestre;
  const { data } = await api.get('/turmas', { params });
  return data;
}

export async function listarMinhasTurmas() {
  const { data } = await api.get('/turmas/minhas');
  return data;
}

export async function listarTurmasLecionadas() {
  const { data } = await api.get('/turmas/lecionadas');
  return data;
}

export async function matricularTurma(turmaId) {
  await api.post(`/turmas/${turmaId}/matricular`);
}

export async function cancelarMatricula(turmaId) {
  await api.delete(`/turmas/${turmaId}/matricula`);
}

export async function listarDisciplinas({ cursoId } = {}) {
  const params = {};
  if (cursoId) params.cursoId = cursoId;
  const { data } = await api.get('/disciplinas', { params });
  return data;
}
