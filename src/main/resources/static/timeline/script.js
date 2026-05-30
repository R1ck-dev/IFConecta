const TOKEN_KEY = 'ifconecta_token';
const LOGIN_URL = '/login/Login.html';
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
const lista = document.getElementById('lista-posts');
const titulo = document.getElementById('clube-titulo');
const paginacao = document.getElementById('paginacao');
const pageInfo = document.getElementById('page-info');
const btnAnterior = document.getElementById('btn-anterior');
const btnProximo = document.getElementById('btn-proximo');

const params = new URLSearchParams(window.location.search);
const clubeId = params.get('clubeId');
const clubeNome = params.get('nome');

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

function formatarData(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleString('pt-BR', {
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
}

/* ===== Carregar timeline ===== */
async function carregarPosts(pagina) {
    const token = getToken();
    lista.innerHTML = '<p class="estado-vazio">Carregando posts...</p>';
    paginacao.hidden = true;

    try {
        const response = await fetch(`/api/clubes/${clubeId}/posts?pagina=${pagina}&tamanho=${TAMANHO_PAGINA}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (response.status === 401) {
            logout();
            return;
        }
        if (!response.ok) {
            lista.innerHTML = '';
            let mensagem = 'Não foi possível carregar a timeline (' + response.status + ').';
            try {
                const body = await response.json();
                if (body && body.erro) mensagem = body.erro;
            } catch (_) {}
            mostrarFeedback(mensagem, 'error');
            return;
        }

        const dados = await response.json();
        const posts = dados.itens || [];
        paginaAtual = dados.paginaAtual;
        totalPaginas = Math.max(dados.totalPaginas, 1);

        if (posts.length === 0) {
            lista.innerHTML = '<p class="estado-vazio">Ainda não há posts neste clube. Seja o primeiro a publicar!</p>';
            return;
        }

        lista.innerHTML = posts.map(renderPost).join('');
        wireComentarios();
        atualizarPaginacao();
    } catch (err) {
        lista.innerHTML = '';
        mostrarFeedback('Erro de rede: ' + err.message, 'error');
    }
}

function renderPost(p) {
    return `
        <article class="post-card" data-post="${esc(p.id)}">
            <div class="post-head">
                <span class="post-autor">${esc(p.autorNome)}</span>
                <span class="post-data">${esc(formatarData(p.dataCriacao))}</span>
            </div>
            <p class="post-conteudo">${esc(p.conteudo)}</p>
            <div class="post-rodape">
                <div class="post-comentarios-info"><span class="cont-coment">${p.qtdComentarios}</span> comentário(s)</div>
                <div class="comentar-box">
                    <input type="text" placeholder="Escreva um comentário..." data-coment-input maxlength="500">
                    <button type="button" class="submit-btn" data-coment-btn data-id="${esc(p.id)}">Comentar</button>
                </div>
            </div>
        </article>`;
}

function wireComentarios() {
    document.querySelectorAll('[data-coment-btn]').forEach((btn) => {
        const input = btn.parentElement.querySelector('[data-coment-input]');
        btn.addEventListener('click', () => comentar(btn.dataset.id, input, btn));
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') { e.preventDefault(); comentar(btn.dataset.id, input, btn); }
        });
    });
}

async function comentar(postId, input, btn) {
    const conteudo = input.value.trim();
    if (!conteudo) {
        mostrarFeedback('Escreva algo antes de comentar.', 'error');
        return;
    }

    const token = getToken();
    btn.disabled = true;
    input.disabled = true;

    try {
        const response = await fetch(`/api/posts/${postId}/comentarios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ conteudo })
        });

        if (response.status === 201) {
            input.value = '';
            const card = document.querySelector(`.post-card[data-post="${CSS.escape(postId)}"]`);
            const cont = card && card.querySelector('.cont-coment');
            if (cont) cont.textContent = (parseInt(cont.textContent, 10) || 0) + 1;
            mostrarFeedback('Comentário adicionado!', 'success');
            return;
        }
        if (response.status === 401) {
            logout();
            return;
        }

        let mensagem = 'Não foi possível adicionar o comentário.';
        try {
            const body = await response.json();
            if (body && body.erro) mensagem = body.erro;
        } catch (_) {}
        mostrarFeedback(mensagem, 'error');
    } catch (err) {
        mostrarFeedback('Erro de rede: ' + err.message, 'error');
    } finally {
        btn.disabled = false;
        input.disabled = false;
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
    if (paginaAtual > 0) carregarPosts(paginaAtual - 1);
});
btnProximo.addEventListener('click', () => {
    if (paginaAtual < totalPaginas - 1) carregarPosts(paginaAtual + 1);
});

/* ===== Init ===== */
const token = requireAuth();
if (token) {
    setupHeader(token);

    if (clubeNome) titulo.textContent = 'Timeline — ' + clubeNome;

    if (!clubeId) {
        lista.innerHTML = '<p class="estado-vazio">Clube não informado. Volte para a lista de clubes e escolha um.</p>';
    } else {
        carregarPosts(0);
    }
}
