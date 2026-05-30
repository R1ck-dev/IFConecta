const TOKEN_KEY = 'ifconecta_token';
const LOGIN_URL = '/login/index.html';
const TAMANHO_PAGINA = 10;

/* ===== Auth ===== */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function decodeJwt(token) {
    try {
        return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    } catch (_) {
        return null;
    }
}

function tokenValido(token) {
    const p = token && decodeJwt(token);
    return !!p && (!p.exp || p.exp * 1000 > Date.now());
}

function requireAuth() {
    const token = getToken();
    if (!tokenValido(token)) {
        localStorage.removeItem(TOKEN_KEY);
        window.location.replace(LOGIN_URL);
        return null;
    }
    return token;
}

function logout() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = LOGIN_URL;
}

function setupHeader(token) {
    const payload = decodeJwt(token);
    const emailEl = document.getElementById('user-email');
    if (emailEl && payload && payload.sub) emailEl.textContent = payload.sub;
    document.getElementById('btn-logout').addEventListener('click', logout);
}

/* ===== UI helpers ===== */
const feedback = document.getElementById('feedback');
const lista = document.getElementById('lista-clubes');
const paginacao = document.getElementById('paginacao');
const pageInfo = document.getElementById('page-info');
const btnAnterior = document.getElementById('btn-anterior');
const btnProximo = document.getElementById('btn-proximo');

let paginaAtual = 0;
let totalPaginas = 1;

function mostrarFeedback(msg, tipo) {
    feedback.textContent = msg;
    feedback.className = 'feedback ' + (tipo || '');
}

function esc(s) {
    return String(s == null ? '' : s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

/* ===== Carregar clubes ===== */
async function carregarClubes(pagina) {
    const token = getToken();
    lista.innerHTML = '<p class="estado-vazio">Carregando clubes...</p>';
    paginacao.hidden = true;

    try {
        const response = await fetch(`/api/clubes?pagina=${pagina}&tamanho=${TAMANHO_PAGINA}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }
        if (!response.ok) {
            lista.innerHTML = '';
            mostrarFeedback('Não foi possível carregar os clubes (' + response.status + ').', 'error');
            return;
        }

        const dados = await response.json();
        const clubes = dados.itens || [];
        paginaAtual = dados.paginaAtual;
        totalPaginas = Math.max(dados.totalPaginas, 1);

        if (clubes.length === 0) {
            lista.innerHTML = '<p class="estado-vazio">Nenhum clube cadastrado ainda. Que tal criar o primeiro?</p>';
            return;
        }

        lista.innerHTML = clubes.map(renderCard).join('');
        wireBotoesEntrar();
        atualizarPaginacao();
    } catch (err) {
        lista.innerHTML = '';
        mostrarFeedback('Erro de rede: ' + err.message, 'error');
    }
}

function renderCard(c) {
    const timelineUrl = `/timeline/index.html?clubeId=${encodeURIComponent(c.id)}&nome=${encodeURIComponent(c.nome || '')}`;
    return `
        <article class="clube-card">
            <h3>${esc(c.nome)}</h3>
            <p class="descricao">${esc(c.descricao)}</p>
            <span class="clube-meta">${c.quantidadeMembros} membro(s)</span>
            <div class="clube-actions">
                <button type="button" class="btn-outline" data-entrar data-id="${esc(c.id)}">Entrar no clube</button>
                <a class="submit-btn" href="${timelineUrl}">Ver timeline</a>
            </div>
        </article>`;
}

function wireBotoesEntrar() {
    document.querySelectorAll('[data-entrar]').forEach((btn) => {
        btn.addEventListener('click', () => entrarNoClube(btn.dataset.id, btn));
    });
}

async function entrarNoClube(clubeId, btn) {
    const token = getToken();
    btn.disabled = true;
    mostrarFeedback('Entrando no clube...', '');

    try {
        const response = await fetch(`/api/clubes/${clubeId}/entrar`, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (response.status === 204) {
            btn.textContent = '✓ Membro';
            mostrarFeedback('Você entrou no clube!', 'success');
            return;
        }
        if (response.status === 401) {
            logout();
            return;
        }

        let mensagem = 'Não foi possível entrar no clube.';
        try {
            const body = await response.json();
            if (body && body.erro) mensagem = body.erro;
        } catch (_) {}
        mostrarFeedback(mensagem, 'error');
        btn.disabled = false;
    } catch (err) {
        mostrarFeedback('Erro de rede: ' + err.message, 'error');
        btn.disabled = false;
    }
}

/* ===== Paginação ===== */
function atualizarPaginacao() {
    paginacao.hidden = totalPaginas <= 1;
    pageInfo.textContent = `Página ${paginaAtual + 1} de ${totalPaginas}`;
    btnAnterior.disabled = paginaAtual <= 0;
    btnProximo.disabled = paginaAtual >= totalPaginas - 1;
}

btnAnterior.addEventListener('click', () => {
    if (paginaAtual > 0) carregarClubes(paginaAtual - 1);
});
btnProximo.addEventListener('click', () => {
    if (paginaAtual < totalPaginas - 1) carregarClubes(paginaAtual + 1);
});

/* ===== Init ===== */
const token = requireAuth();
if (token) {
    setupHeader(token);
    carregarClubes(0);
}
