const statusIcon = document.getElementById('status-icon');
const statusMsg = document.getElementById('status-msg');
const acao = document.getElementById('acao');

function definirEstado(icone, msg, tipo) {
    statusIcon.textContent = icone;
    statusMsg.textContent = msg;
    statusMsg.className = 'status-msg ' + (tipo || '');
    acao.hidden = false;
}

async function ativar() {
    const token = new URLSearchParams(window.location.search).get('token');

    if (!token) {
        definirEstado('⚠️', 'Link inválido: token de ativação ausente. Use o link enviado para o seu e-mail.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/usuarios/ativar?token=' + encodeURIComponent(token));

        if (response.ok) {
            let mensagem = 'Conta ativada com sucesso! Você já pode fazer login.';
            try {
                const body = await response.json();
                if (body && body.mensagem) mensagem = body.mensagem;
            } catch (_) {}
            definirEstado('✅', mensagem, 'success');
            return;
        }

        let mensagem = 'Não foi possível ativar a conta. O link pode estar expirado ou já ter sido usado.';
        try {
            const body = await response.json();
            if (body && body.erro) mensagem = body.erro;
        } catch (_) {}
        definirEstado('❌', mensagem, 'error');
    } catch (err) {
        definirEstado('❌', 'Erro de rede ao ativar a conta: ' + err.message, 'error');
    }
}

ativar();
