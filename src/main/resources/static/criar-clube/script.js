const TOKEN_KEY = 'ifconecta_token';

const form = document.getElementById('form-criar-clube');
const nomeInput = document.getElementById('nomeClube');
const descricaoInput = document.getElementById('descricaoClube');
const feedback = document.getElementById('feedback');
const btnSubmit = document.getElementById('btn-submit');
const btnCancelar = document.getElementById('btn-cancelar');

// Preenche o bloco de autor com o usuário logado (e-mail do JWT). Sem backend.
(function preencherAutor() {
    const nomeEl = document.querySelector('.profile-name');
    if (!nomeEl) return;
    const token = localStorage.getItem(TOKEN_KEY);
    let email = null;
    if (token) {
        try {
            email = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))).sub;
        } catch (_) {}
    }
    nomeEl.textContent = email || 'Visitante (faça login)';
})();

function mostrarFeedback(msg, tipo) {
    feedback.textContent = msg;
    feedback.className = 'feedback ' + tipo;
}

btnCancelar.addEventListener('click', () => {
    form.reset();
    mostrarFeedback('', '');
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const nome = nomeInput.value.trim();
    const descricao = descricaoInput.value.trim();

    if (!nome || !descricao) {
        mostrarFeedback('Preencha nome e descrição.', 'error');
        return;
    }

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        mostrarFeedback('Você precisa estar autenticado. Faça login antes de criar um clube.', 'error');
        return;
    }

    btnSubmit.disabled = true;
    mostrarFeedback('Criando clube...', '');

    try {
        const response = await fetch('/api/clubes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ nome, descricao })
        });

        if (response.status === 201) {
            mostrarFeedback('Clube criado com sucesso!', 'success');
            form.reset();
            return;
        }

        if (response.status === 401 || response.status === 403) {
            mostrarFeedback('Sessão expirada ou inválida. Faça login novamente.', 'error');
            return;
        }

        let mensagem = 'Não foi possível criar o clube.';
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
