const TOKEN_KEY = 'ifconecta_token';

const form = document.getElementById('form-criar-post');
const selectClube = document.getElementById('clubeId');
const conteudoInput = document.getElementById('conteudoPost');
const feedback = document.getElementById('feedback');
const btnSubmit = document.getElementById('btn-submit');
const btnCancelar = document.getElementById('btn-cancelar');

function mostrarFeedback(msg, tipo) {
    feedback.textContent = msg;
    feedback.className = 'feedback ' + tipo;
}

function obterToken() {
    return localStorage.getItem(TOKEN_KEY);
}

async function carregarClubes() {
    const token = obterToken();
    if (!token) {
        selectClube.innerHTML = '<option value="">Sem token: faça login antes</option>';
        mostrarFeedback('Você precisa estar autenticado. Faça login antes de publicar.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/clubes?pagina=0&tamanho=100', {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!response.ok) {
            selectClube.innerHTML = '<option value="">Erro ao carregar clubes</option>';
            mostrarFeedback('Não foi possível carregar a lista de clubes (' + response.status + ').', 'error');
            return;
        }

        const pagina = await response.json();
        const clubes = pagina.itens || [];

        if (clubes.length === 0) {
            selectClube.innerHTML = '<option value="">Nenhum clube disponível</option>';
            mostrarFeedback('Não há clubes cadastrados. Crie um clube primeiro.', 'error');
            return;
        }

        selectClube.innerHTML = '<option value="">Selecione um clube...</option>'
            + clubes.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
    } catch (err) {
        selectClube.innerHTML = '<option value="">Erro de rede</option>';
        mostrarFeedback('Erro de rede ao carregar clubes: ' + err.message, 'error');
    }
}

btnCancelar.addEventListener('click', () => {
    form.reset();
    mostrarFeedback('', '');
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const clubeId = selectClube.value;
    const conteudo = conteudoInput.value.trim();

    if (!clubeId) {
        mostrarFeedback('Selecione um clube.', 'error');
        return;
    }
    if (!conteudo) {
        mostrarFeedback('Escreva o conteúdo do post.', 'error');
        return;
    }

    const token = obterToken();
    if (!token) {
        mostrarFeedback('Você precisa estar autenticado.', 'error');
        return;
    }

    btnSubmit.disabled = true;
    mostrarFeedback('Publicando...', '');

    try {
        const response = await fetch('/api/posts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ clubeId, conteudo })
        });

        if (response.status === 201) {
            mostrarFeedback('Post publicado com sucesso!', 'success');
            conteudoInput.value = '';
            return;
        }

        if (response.status === 401 || response.status === 403) {
            mostrarFeedback('Sessão expirada ou inválida. Faça login novamente.', 'error');
            return;
        }

        let mensagem = 'Não foi possível publicar o post.';
        try {
            const body = await response.json();
            if (body && body.erro) mensagem = body.erro;
        } catch (_) {}
        mostrarFeedback(mensagem, 'error');
    } catch (err) {
        mostrarFeedback('Erro de rede: ' + err.message, 'error');
    } finally {
        btnSubmit.disabled = false;
    }
});

carregarClubes();
