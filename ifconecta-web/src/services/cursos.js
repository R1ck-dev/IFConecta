import api from './api.js';

let cursosPromise = null;
let cursosMap = null;

async function carregarTodos() {
  if (!cursosPromise) {
    cursosPromise = api.get('/cursos').then(({ data }) => {
      cursosMap = new Map(data.map((c) => [c.id, c]));
      return data;
    }).catch((err) => {
      cursosPromise = null;
      throw err;
    });
  }
  return cursosPromise;
}

export async function listar() {
  return carregarTodos();
}

export async function getCurso(cursoId) {
  if (!cursoId) return null;
  if (!cursosMap) {
    try { await carregarTodos(); }
    catch { return null; }
  }
  return cursosMap?.get(cursoId) ?? null;
}
