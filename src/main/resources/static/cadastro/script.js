const form = document.getElementById('form-cadastro');
const nomeInput = document.getElementById('nome');
const emailInput = document.getElementById('email');
const prontuarioInput = document.getElementById('prontuario');
const passwordInput = document.getElementById('password');
const feedback = document.getElementById('feedback');
const btnSubmit = document.getElementById('btn-submit');

function mostrarFeedback(msg, tipo) {
    feedback.textContent = msg;
    feedback.className = 'feedback ' + tipo;
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const nome = nomeInput.value.trim();
    const email = emailInput.value.trim();
    const prontuario = prontuarioInput.value.trim();
    const password = passwordInput.value;

    if (!nome || !email || !prontuario || !password) {
        mostrarFeedback('Preencha todos os campos.', 'error');
        return;
    }
    if (password.length < 8) {
        mostrarFeedback('A senha deve ter no mínimo 8 caracteres.', 'error');
        return;
    }

    btnSubmit.disabled = true;
    mostrarFeedback('Criando conta...', '');

    try {
        const response = await fetch('/api/usuarios/alunos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, password, prontuario })
        });

        if (response.status === 201) {
            form.reset();
            mostrarFeedback('Conta criada! Enviamos um e-mail de ativação. Confirme para poder entrar.', 'success');
            return;
        }

        let mensagem = 'Não foi possível criar a conta.';
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
