const formMensagem = document.getElementById('formMensagem');
const campoMensagem = document.getElementById('campoMensagem');
const areaConversa = document.getElementById('areaConversa');

formMensagem.addEventListener('submit', function(event) {
    event.preventDefault();
    const mensagemUsuario = campoMensagem.value.trim();
    if (mensagemUsuario === '') return;

    adicionarMensagem('usuario', 'Voce: ' + mensagemUsuario);
    campoMensagem.value = '';

    fetch('/mensagem', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ texto: mensagemUsuario })
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        }

        return response.text().then(mensagemErro => {
            throw new Error(mensagemErro);
        });
    })
    .then(respostaJarvis => {
        adicionarMensagem('jarvis', 'Jarvis: ' + respostaJarvis);
    })
    .catch(erro => {
        const mensagemErro = erro.message || 'Nao foi possivel conectar ao Jarvis.';
        adicionarMensagem('jarvis', 'Jarvis: ' + mensagemErro);
    });
});

const formularioLogin = document.getElementById('loginForm');
formularioLogin.addEventListener('submit', function(event) {
    event.preventDefault();
    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value.trim();
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email, senha: senha })
    })
    .then(response => response.text())
    .then(resultado => {
        if (resultado.includes('Login bem-sucedido')) {
            mensagemLogin.textContent = '';
            telaLogin.style.display = 'none';
            telaChat.style.display = 'flex';
            campoMensagem.focus();
            return;
        }

        mostrarMensagem(mensagemLogin, resultado, false);
    })
    .catch(() => {
        mostrarMensagem(mensagemLogin, 'Nao foi possivel conectar ao Jarvis.', false);
    });
});

const formularioCadastro = document.getElementById('cadastroForm');
formularioCadastro.addEventListener('submit', function(event) {
    event.preventDefault();
    const nomeCadastro = document.getElementById('nomeCadastro').value.trim();
    const emailCadastro = document.getElementById('emailCadastro').value.trim();
    const senhaCadastro = document.getElementById('senhaCadastro').value.trim();
    fetch('/cadastro', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ nome: nomeCadastro, email: emailCadastro, senha: senhaCadastro })
    })
    .then(response => response.text())
    .then(resultado => {
        if (resultado.includes('Cadastro bem-sucedido')) {
            telaCadastroFormulario.style.display = 'none';
            telaLoginFormulario.style.display = 'block';
            mostrarMensagem(mensagemLogin, 'Cadastro realizado. Agora entre na sua conta.', true);
        } else {
            mostrarMensagem(mensagemCadastro, resultado, false);
        }
    })
    .catch(() => {
        mostrarMensagem(mensagemCadastro, 'Nao foi possivel conectar ao Jarvis.', false);
    });
});

const telaLoginFormulario = document.getElementById('telaLoginFormulario');
const telaCadastroFormulario = document.getElementById('telaCadastroFormulario');
const botaoMostrarCadastro = document.getElementById('botaoMostrarCadastro');
const botaoMostrarLogin = document.getElementById('botaoMostrarLogin');
const telaInicial = document.getElementById('tela-inicial');
const telaLogin = document.querySelector('.telaLogin');
const telaChat = document.querySelector('.janela');
const botaoEntrar = document.getElementById('botao-entrar');
const mensagemLogin = document.getElementById('mensagemLogin');
const mensagemCadastro = document.getElementById('mensagemCadastro');



botaoMostrarCadastro.addEventListener('click', function() {
    mensagemCadastro.textContent = '';
    telaLoginFormulario.style.display = 'none';
    telaCadastroFormulario.style.display = 'block';
});

botaoMostrarLogin.addEventListener('click', function() {
    mensagemLogin.textContent = '';
    telaCadastroFormulario.style.display = 'none';
    telaLoginFormulario.style.display = 'block';
});

const campoEmailLogin = document.getElementById('email');
const campoSenhaLogin = document.getElementById('senha');
const campoNomeCadastro = document.getElementById('nomeCadastro');
const campoEmailCadastro = document.getElementById('emailCadastro');
const campoSenhaCadastro = document.getElementById('senhaCadastro');

campoEmailLogin.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        campoSenhaLogin.focus();
    }
});

campoSenhaLogin.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        formularioLogin.requestSubmit();
    }
});

campoNomeCadastro.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        campoEmailCadastro.focus();
    }
});

campoEmailCadastro.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        campoSenhaCadastro.focus();
    }
});

campoSenhaCadastro.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        formularioCadastro.requestSubmit();
    }
});

function abrirLogin() {
    telaInicial.style.display = 'none';
    telaLogin.style.display = 'flex';
    campoEmailLogin.focus();
}

botaoEntrar.addEventListener('click', abrirLogin);

document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter' && telaInicial.style.display !== 'none') {
        event.preventDefault();
        botaoEntrar.click();
    }
});

function mostrarMensagem(elemento, texto, sucesso) {
    elemento.textContent = texto;
    elemento.classList.toggle('sucesso', sucesso);
}

function adicionarMensagem(tipo, texto) {
    const mensagemElemento = document.createElement('div');
    mensagemElemento.className = 'mensagem ' + tipo;
    mensagemElemento.textContent = texto;
    areaConversa.appendChild(mensagemElemento);
    areaConversa.scrollTop = areaConversa.scrollHeight;
}
