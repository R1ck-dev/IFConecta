const TOKEN_KEY = 'ifconecta_token';
const HOME_URL = '/clubes/Clubes.html';

const form = document.getElementById('form-login');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const feedback = document.getElementById('feedback');
const btnSubmit = document.getElementById('btn-submit');

function mostrarFeedback(msg, tipo) {
    feedback.textContent = msg;
    feedback.className = 'feedback ' + tipo;
}

// Decodifica o payload do JWT (base64url) para checar expiração.
function tokenValido(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        return !payload.exp || payload.exp * 1000 > Date.now();
    } catch (_) {
        return false;
    }
}

// Se já está logado, pula direto para a home.
const tokenExistente = localStorage.getItem(TOKEN_KEY);
if (tokenExistente && tokenValido(tokenExistente)) {
    window.location.replace(HOME_URL);
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    if (!email || !password) {
        mostrarFeedback('Preencha e-mail e senha.', 'error');
        return;
    }

    btnSubmit.disabled = true;
    mostrarFeedback('Entrando...', '');

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const body = await response.json();
            localStorage.setItem(TOKEN_KEY, body.token);
            window.location.href = HOME_URL;
            return;
        }

        let mensagem = 'E-mail ou senha inválidos.';
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
