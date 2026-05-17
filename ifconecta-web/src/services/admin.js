import api from './api.js';

export async function convidarProfessor({ nome, emailAcad, siape }) {
  await api.post('/admin/usuarios/professores/convidar', { nome, emailAcad, siape });
}

export async function convidarInstitucional({ nome, emailAcad, setor, cargo }) {
  await api.post('/admin/usuarios/institucionais/convidar', { nome, emailAcad, setor, cargo });
}

export async function seederCurso(payload) {
  await api.post('/admin/academico/seeder', payload);
}
