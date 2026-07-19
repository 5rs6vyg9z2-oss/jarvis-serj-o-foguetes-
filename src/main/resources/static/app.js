const formMensagem = document.getElementById('formMensagem');
const campoMensagem = document.getElementById('campoMensagem');
const areaConversa = document.getElementById('areaConversa');

formMensagem.addEventListener('submit', function(event) {
    event.preventDefault();
    const mensagemUsuario = campoMensagem.value.trim();
    if (mensagemUsuario === '') return;

    const mensagemElemento = document.createElement('div');
    mensagemElemento.className = 'mensagem usuario';
    mensagemElemento.textContent = 'Voce: ' + mensagemUsuario;
    areaConversa.appendChild(mensagemElemento);
    fetch('/mensagem?texto=' + encodeURIComponent(mensagemUsuario))
        .then(response => response.text())
        .then(respostaJarvis => {
            const mensagemJarvisElemento = document.createElement('div');
            mensagemJarvisElemento.className = 'mensagem jarvis';
            mensagemJarvisElemento.textContent = 'Jarvis: ' + respostaJarvis;
            areaConversa.appendChild(mensagemJarvisElemento);
            campoMensagem.value = '';
            areaConversa.scrollTop = areaConversa.scrollHeight;
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
            alert('Login bem-sucedido!');
            document.querySelector('.telaLogin').style.display = 'none';
        } else {
            alert('email ou senha incorretos.');
        }
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
            alert('Cadastro bem-sucedido!');
            telaCadastroFormulario.style.display = 'none';
            telaLoginFormulario.style.display = 'block';
        } else {
            alert('Erro ao cadastrar. Tente novamente.');
        }
    });
});

const telaLoginFormulario = document.getElementById('telaLoginFormulario');
const telaCadastroFormulario = document.getElementById('telaCadastroFormulario');
const botaoMostrarCadastro = document.getElementById('botaoMostrarCadastro');
const botaoMostrarLogin = document.getElementById('botaoMostrarLogin');



botaoMostrarCadastro.addEventListener('click', function() {
    telaLoginFormulario.style.display = 'none';
    telaCadastroFormulario.style.display = 'block';
});

botaoMostrarLogin.addEventListener('click', function() {
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
