const formMensagem = document.getElementById('formMensagem');
const campoMensagem = document.getElementById('campoMensagem');
const areaConversa = document.getElementById('areaConversa');
const indicadorPensando = document.getElementById('indicadorPensando');
const statusJarvis = document.getElementById('statusJarvis');
const textoStatusJarvis = document.getElementById('textoStatusJarvis');
const indicadorApi = document.getElementById('indicadorApi');
const modoSessao = document.getElementById('modoSessao');
const botaoFoco = document.getElementById('botaoFoco');
const botaoMicrofone = document.getElementById('botaoMicrofone');
const botaoAparencia = document.getElementById('botaoAparencia');
const dialogoVoz = document.getElementById('dialogoVoz');
const esferaVoz = document.getElementById('esferaVoz');
const statusVoz = document.getElementById('statusVoz');
const transcricaoVoz = document.getElementById('transcricaoVoz');
const botaoOuvir = document.getElementById('botaoOuvir');
const botaoPararVoz = document.getElementById('botaoPararVoz');
const respostaEmVoz = document.getElementById('respostaEmVoz');
const conversaContinua = document.getElementById('conversaContinua');
const dialogoAparencia = document.getElementById('dialogoAparencia');
const botoesTema = document.querySelectorAll('.tema-pronto');
const controlesRgb = {
    vermelho: document.getElementById('controleVermelho'),
    verde: document.getElementById('controleVerde'),
    azul: document.getElementById('controleAzul')
};
let chaveAparenciaUsuario = 'visitante';
let modoVozAtivo = false;
let capturandoAudio = false;
let inicioCapturaSolicitado = false;
let gravadorAudio = null;
let fluxoMicrofone = null;
let partesAudio = [];
let descartarGravacao = false;
let contextoAudio = null;
let analisadorAudio = null;
let quadroMonitoramentoSilencio = null;
let falaFoiDetectada = false;
let instanteUltimaFala = 0;
let aguardandoRespostaVoz = false;
let audioRespostaAtual = null;
let urlAudioRespostaAtual = null;
let sequenciaAudioResposta = 0;
const botaoEnviar = formMensagem.querySelector('button[type="submit"]');

function atualizarEsferaVoz(estado, texto) {
    esferaVoz.dataset.estado = estado;
    esferaVoz.setAttribute('aria-label', `Jarvis ${texto.toLowerCase()}`);
    statusVoz.textContent = texto;
}

function atualizarControlesVoz() {
    botaoMicrofone.classList.toggle('captando', capturandoAudio);
    botaoMicrofone.setAttribute('aria-pressed', String(capturandoAudio));
    botaoMicrofone.setAttribute('aria-label', capturandoAudio ? 'Enviar gravação de voz' : 'Iniciar conversa por voz');
    botaoOuvir.disabled = !navigator.mediaDevices?.getUserMedia
            || typeof MediaRecorder === 'undefined'
            || aguardandoRespostaVoz;
    botaoOuvir.textContent = capturandoAudio ? 'Enviar agora' : 'Comecar a falar';
    botaoPararVoz.disabled = !modoVozAtivo;
}

function liberarMicrofone() {
    fluxoMicrofone?.getTracks().forEach(trilha => trilha.stop());
    fluxoMicrofone = null;
}

function pararMonitoramentoSilencio() {
    if (quadroMonitoramentoSilencio !== null) {
        cancelAnimationFrame(quadroMonitoramentoSilencio);
        quadroMonitoramentoSilencio = null;
    }
    analisadorAudio = null;
    if (contextoAudio && contextoAudio.state !== 'closed') {
        contextoAudio.close().catch(erro => console.warn('Nao foi possivel fechar o AudioContext:', erro));
    }
    contextoAudio = null;
}

function monitorarSilencio() {
    if (!capturandoAudio || !analisadorAudio) return;

    const amostras = new Uint8Array(analisadorAudio.fftSize);
    analisadorAudio.getByteTimeDomainData(amostras);
    const energiaMedia = amostras.reduce((soma, amostra) => {
        const amplitude = (amostra - 128) / 128;
        return soma + amplitude * amplitude;
    }, 0) / amostras.length;
    const volume = Math.sqrt(energiaMedia);
    const agora = performance.now();

    if (volume > 0.018) {
        falaFoiDetectada = true;
        instanteUltimaFala = agora;
    } else if (falaFoiDetectada && agora - instanteUltimaFala >= 1000) {
        transcricaoVoz.textContent = 'Um segundo de silencio detectado. Enviando sua fala...';
        pararCapturaVoz();
        return;
    }

    quadroMonitoramentoSilencio = requestAnimationFrame(monitorarSilencio);
}

function iniciarMonitoramentoSilencio(fluxo) {
    const AudioContextApi = window.AudioContext || window.webkitAudioContext;
    if (!AudioContextApi) {
        transcricaoVoz.textContent = 'Fale e toque em Enviar agora quando terminar.';
        return;
    }

    contextoAudio = contextoAudio || new AudioContextApi();
    const origemAudio = contextoAudio.createMediaStreamSource(fluxo);
    analisadorAudio = contextoAudio.createAnalyser();
    analisadorAudio.fftSize = 2048;
    origemAudio.connect(analisadorAudio);
    falaFoiDetectada = false;
    instanteUltimaFala = 0;
    monitorarSilencio();
}

function nomeArquivoAudio(tipo) {
    if (tipo.includes('mp4')) return 'gravacao.mp4';
    if (tipo.includes('ogg')) return 'gravacao.ogg';
    if (tipo.includes('wav')) return 'gravacao.wav';
    if (tipo.includes('mpeg')) return 'gravacao.mp3';
    return 'gravacao.webm';
}

async function iniciarCapturaVoz() {
    if (!navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === 'undefined') {
        atualizarEsferaVoz('erro', 'Este navegador nao permite gravar audio nesta pagina.');
        atualizarControlesVoz();
        return;
    }
    if (inicioCapturaSolicitado || capturandoAudio || aguardandoRespostaVoz) return;

    modoVozAtivo = true;
    inicioCapturaSolicitado = true;
    if (!dialogoVoz.open) dialogoVoz.showModal();
    atualizarEsferaVoz('ouvindo', 'Solicitando acesso ao microfone');
    transcricaoVoz.textContent = 'A permissao pode ser solicitada pelo navegador.';
    atualizarControlesVoz();

    try {
        const AudioContextApi = window.AudioContext || window.webkitAudioContext;
        if (AudioContextApi) {
            contextoAudio = new AudioContextApi();
            if (contextoAudio.state === 'suspended') await contextoAudio.resume();
        }

        const fluxo = await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: true,
                noiseSuppression: true,
                autoGainControl: true
            }
        });

        if (!modoVozAtivo) {
            fluxo.getTracks().forEach(trilha => trilha.stop());
            return;
        }

        fluxoMicrofone = fluxo;
        const formatos = ['audio/webm;codecs=opus', 'audio/mp4', 'audio/webm', 'audio/ogg;codecs=opus'];
        const formato = formatos.find(tipo => MediaRecorder.isTypeSupported(tipo));
        gravadorAudio = formato
                ? new MediaRecorder(fluxo, { mimeType: formato })
                : new MediaRecorder(fluxo);
        partesAudio = [];
        descartarGravacao = false;

        gravadorAudio.addEventListener('dataavailable', evento => {
            if (evento.data.size > 0) partesAudio.push(evento.data);
        });
        gravadorAudio.addEventListener('error', evento => {
            capturandoAudio = false;
            aguardandoRespostaVoz = false;
            modoVozAtivo = false;
            descartarGravacao = true;
            if (gravadorAudio?.state === 'recording') gravadorAudio.stop();
            pararMonitoramentoSilencio();
            liberarMicrofone();
            atualizarEsferaVoz('erro', 'O navegador encontrou um erro ao gravar o audio.');
            atualizarControlesVoz();
            console.error('Falha na gravacao de audio:', evento.error);
        });
        gravadorAudio.addEventListener('stop', () => {
            const tipoAudio = gravadorAudio?.mimeType || formato || 'audio/webm';
            const gravacao = new Blob(partesAudio, { type: tipoAudio });
            partesAudio = [];
            gravadorAudio = null;
            pararMonitoramentoSilencio();
            liberarMicrofone();

            if (descartarGravacao) {
                descartarGravacao = false;
                return;
            }
            enviarAudioParaJarvis(gravacao);
        }, { once: true });

        gravadorAudio.start();
        capturandoAudio = true;
        atualizarEsferaVoz('ouvindo', 'Estou ouvindo');
        transcricaoVoz.textContent = 'Fale normalmente. Envio automatico apos 1 segundo de silencio.';
        iniciarMonitoramentoSilencio(fluxo);
        atualizarControlesVoz();
    } catch (erro) {
        const mensagensErro = {
            NotAllowedError: 'Permita o acesso ao microfone nas configuracoes do navegador.',
            NotFoundError: 'Nao encontrei um microfone conectado.',
            NotReadableError: 'O microfone esta sendo usado por outro aplicativo.',
            SecurityError: 'O microfone exige localhost ou uma pagina HTTPS.'
        };
        modoVozAtivo = false;
        pararMonitoramentoSilencio();
        liberarMicrofone();
        atualizarEsferaVoz('erro', mensagensErro[erro.name] || 'Nao consegui iniciar a gravacao. Tente novamente.');
        atualizarControlesVoz();
        console.error('Falha ao iniciar a gravacao de audio:', erro);
    } finally {
        inicioCapturaSolicitado = false;
    }
}

function pararCapturaVoz() {
    if (!gravadorAudio || gravadorAudio.state !== 'recording') return;
    capturandoAudio = false;
    aguardandoRespostaVoz = true;
    pararMonitoramentoSilencio();
    atualizarEsferaVoz('pensando', 'Enviando audio para transcricao');
    transcricaoVoz.textContent = 'Aguarde enquanto o Groq transforma sua fala em texto.';
    atualizarControlesVoz();
    gravadorAudio.stop();
}

function enviarAudioParaJarvis(gravacao) {
    if (!gravacao.size) {
        aguardandoRespostaVoz = false;
        atualizarEsferaVoz('erro', 'A gravacao ficou vazia. Fale e tente novamente.');
        atualizarControlesVoz();
        return;
    }

    if (gravacao.size > 20 * 1024 * 1024) {
        aguardandoRespostaVoz = false;
        atualizarEsferaVoz('erro', 'A gravacao ultrapassou o limite de 20 MB.');
        atualizarControlesVoz();
        return;
    }

    const formularioAudio = new FormData();
    formularioAudio.append('audio', gravacao, nomeArquivoAudio(gravacao.type));

    fetch('/transcrever', { method: 'POST', body: formularioAudio })
        .then(async resposta => {
            const texto = await resposta.text();
            if (!resposta.ok) throw new Error(texto || 'Nao foi possivel transcrever o audio.');
            return texto;
        })
        .then(textoReconhecido => {
            const mensagem = textoReconhecido.trim();
            if (!mensagem) throw new Error('Nao identifiquei fala no audio. Tente novamente.');
            transcricaoVoz.textContent = mensagem;
            campoMensagem.value = mensagem;
            atualizarEsferaVoz('pensando', 'Jarvis esta pensando');
            formMensagem.requestSubmit();
        })
        .catch(erro => {
            aguardandoRespostaVoz = false;
            atualizarEsferaVoz('erro', erro.message || 'Nao consegui enviar o audio ao Groq.');
            atualizarControlesVoz();
            if (modoVozAtivo && conversaContinua.checked) {
                window.setTimeout(iniciarCapturaVoz, 1200);
            }
        });
}

function pararConversaVoz() {
    modoVozAtivo = false;
    aguardandoRespostaVoz = false;
    if ('speechSynthesis' in window) window.speechSynthesis.cancel();
    sequenciaAudioResposta++;
    limparAudioResposta();
    if (gravadorAudio?.state === 'recording') {
        descartarGravacao = true;
        gravadorAudio.stop();
        capturandoAudio = false;
    } else {
        pararMonitoramentoSilencio();
        liberarMicrofone();
    }
    atualizarControlesVoz();
    atualizarEsferaVoz('pronto', 'Microfone parado');
}

function finalizarTurnoVoz() {
    aguardandoRespostaVoz = false;
    if (!modoVozAtivo) return;

    if (conversaContinua.checked) {
        atualizarEsferaVoz('pronto', 'Resposta concluída. Pode falar novamente.');
        window.setTimeout(iniciarCapturaVoz, 450);
    } else {
        atualizarEsferaVoz('pronto', 'Pronto para ouvir novamente');
        atualizarControlesVoz();
    }
}

function limparAudioResposta() {
    if (audioRespostaAtual) {
        audioRespostaAtual.pause();
        audioRespostaAtual = null;
    }
    if (urlAudioRespostaAtual) {
        URL.revokeObjectURL(urlAudioRespostaAtual);
        urlAudioRespostaAtual = null;
    }
}

function prepararTextoParaFala(texto) {
    return texto
        .replace(/```[\s\S]*?```/g, ' ')
        .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1')
        .replace(/https?:\/\/\S+/g, ' ')
        .replace(/^\s{0,3}#{1,6}\s*/gm, '')
        .replace(/^\s*[-*+]\s+/gm, '')
        .replace(/[*_~`]/g, '')
        .replace(/\s+([,.!?;:])/g, '$1')
        .replace(/\s+/g, ' ')
        .trim();
}

async function falarResposta(texto) {
    const textoFalado = prepararTextoParaFala(texto);
    if (!textoFalado) {
        finalizarTurnoVoz();
        return;
    }
    if ('speechSynthesis' in window) window.speechSynthesis.cancel();
    limparAudioResposta();
    const sequencia = ++sequenciaAudioResposta;
    let fallbackIniciado = false;
    const usarVozNativa = () => {
        if (fallbackIniciado || sequencia !== sequenciaAudioResposta || !modoVozAtivo) return;
        fallbackIniciado = true;
        limparAudioResposta();
        falarRespostaNativa(textoFalado);
    };

    try {
        const resposta = await fetch('/sintetizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ texto: textoFalado })
        });
        if (!resposta.ok) throw new Error('Sintese Piper indisponivel');

        const audio = await resposta.blob();
        if (!modoVozAtivo || sequencia !== sequenciaAudioResposta) return;

        urlAudioRespostaAtual = URL.createObjectURL(audio);
        audioRespostaAtual = new Audio(urlAudioRespostaAtual);
        audioRespostaAtual.onplay = () => atualizarEsferaVoz('falando', 'Jarvis está respondendo');
        audioRespostaAtual.onended = () => {
            limparAudioResposta();
            finalizarTurnoVoz();
        };
        audioRespostaAtual.onerror = usarVozNativa;
        await audioRespostaAtual.play();
    } catch (erro) {
        usarVozNativa();
    }
}

function falarRespostaNativa(texto) {
    if (!('speechSynthesis' in window) || !window.SpeechSynthesisUtterance) {
        atualizarEsferaVoz('pronto', 'Resposta exibida em texto; voz indisponível neste navegador');
        finalizarTurnoVoz();
        return;
    }

    window.speechSynthesis.cancel();
    const fala = new SpeechSynthesisUtterance(texto);
    fala.lang = 'pt-BR';
    fala.rate = 1;
    fala.pitch = 1;
    fala.volume = 1;
    const vozesPortugues = window.speechSynthesis.getVoices();
    const vozPortugues = vozesPortugues.find(voz => voz.lang.toLowerCase().startsWith('pt-br'))
            || vozesPortugues.find(voz => voz.lang.toLowerCase().startsWith('pt'));
    if (vozPortugues) fala.voice = vozPortugues;

    let falaConcluida = false;
    const concluirFala = () => {
        if (falaConcluida) return;
        falaConcluida = true;
        finalizarTurnoVoz();
    };
    fala.onstart = () => atualizarEsferaVoz('falando', 'Jarvis está respondendo');
    fala.onend = concluirFala;
    fala.onerror = concluirFala;
    window.speechSynthesis.speak(fala);
}

if (!navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === 'undefined') {
    botaoOuvir.disabled = true;
    atualizarEsferaVoz('erro', 'A gravacao de audio nao e suportada neste navegador.');
    transcricaoVoz.textContent = 'Você ainda pode conversar digitando no campo da tela principal.';
}
atualizarControlesVoz();

botaoMicrofone.addEventListener('click', () => {
    if (capturandoAudio) {
        pararCapturaVoz();
        return;
    }
    if (aguardandoRespostaVoz) {
        pararConversaVoz();
        return;
    }
    if (!dialogoVoz.open) dialogoVoz.showModal();
    iniciarCapturaVoz();
});

document.getElementById('fecharVoz').addEventListener('click', () => dialogoVoz.close());

botaoOuvir.addEventListener('click', () => {
    if (capturandoAudio) pararCapturaVoz();
    else iniciarCapturaVoz();
});

botaoPararVoz.addEventListener('click', pararConversaVoz);

dialogoVoz.addEventListener('close', () => {
    pararConversaVoz();
});

dialogoVoz.addEventListener('click', evento => {
    if (evento.target === dialogoVoz) dialogoVoz.close();
});

function atualizarStatusJarvis(estado, texto) {
    statusJarvis.dataset.estado = estado;
    textoStatusJarvis.textContent = texto;
    telaChat.dataset.estado = estado;
}

function atualizarEstadoApi(online) {
    indicadorApi.dataset.estado = online ? 'online' : 'offline';
    indicadorApi.querySelector('span').textContent = online ? 'API conectada' : 'API indisponível';
}

formMensagem.addEventListener('submit', function(event) {
    event.preventDefault();
    const mensagemUsuario = campoMensagem.value.trim();
    if (mensagemUsuario === '') return;

    adicionarMensagem('usuario', mensagemUsuario);
    campoMensagem.value = '';
    areaConversa.appendChild(indicadorPensando);
    indicadorPensando.hidden = false;
    areaConversa.scrollTop = areaConversa.scrollHeight;
    atualizarStatusJarvis('processando', 'Analisando');
    campoMensagem.disabled = true;
    botaoEnviar.disabled = true;

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
        atualizarStatusJarvis('pronto', 'Pronto');
        atualizarEstadoApi(true);
        adicionarMensagem('jarvis', respostaJarvis);
        if (modoVozAtivo && respostaEmVoz.checked) falarResposta(respostaJarvis);
        else finalizarTurnoVoz();
    })
    .catch(erro => {
        atualizarStatusJarvis('erro', 'Indisponivel');
        atualizarEstadoApi(false);
        const mensagemErro = erro.message || 'Nao foi possivel conectar ao Jarvis.';
        adicionarMensagem('jarvis', mensagemErro);
        if (modoVozAtivo) {
            aguardandoRespostaVoz = false;
            atualizarEsferaVoz('erro', 'Não consegui enviar a mensagem');
            atualizarControlesVoz();
            if (conversaContinua.checked) window.setTimeout(iniciarCapturaVoz, 1200);
        }
    })
    .finally(() => {
        indicadorPensando.hidden = true;
        campoMensagem.disabled = false;
        botaoEnviar.disabled = false;
        campoMensagem.focus();
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
            const nomeUsuario = resultado.match(/Usuario:\s*(.+)$/i)?.[1]?.trim();
            carregarAparencia('usuario-' + email.toLowerCase());
            modoSessao.textContent = 'Conta ativa';
            atualizarSaudacao(nomeUsuario ? `Olá, ${nomeUsuario}. Estou pronto para ajudar.` : 'Olá! Estou pronto para ajudar.');
            abrirConversa();
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
const mensagemVisitante = document.getElementById('mensagemVisitante');
const botaoVisitante = document.getElementById('botaoVisitante');



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

botaoVisitante.addEventListener('click', function() {
    mensagemVisitante.textContent = '';
    fetch('/visitante', { method: 'POST' })
        .then(response => response.text().then(texto => {
            if (!response.ok) throw new Error(texto);
            return texto;
        }))
        .then(() => {
            carregarAparencia('visitante');
            modoSessao.textContent = 'Visitante';
            atualizarSaudacao('Olá, visitante. Explore a conversa com o Jarvis.');
            abrirConversa();
        })
        .catch(erro => {
            mostrarMensagem(mensagemVisitante, erro.message || 'Nao foi possivel entrar como visitante.', false);
        });
});

botaoFoco.addEventListener('click', function() {
    const ativo = telaChat.classList.toggle('modo-foco');
    botaoFoco.setAttribute('aria-pressed', String(ativo));
    botaoFoco.textContent = ativo ? 'Sair do foco' : 'Foco';
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

function abrirConversa() {
    telaInicial.style.display = 'none';
    telaLogin.style.display = 'none';
    telaChat.style.display = 'flex';
    atualizarStatusJarvis('pronto', 'Pronto');
    campoMensagem.focus();
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
    const assinatura = document.createElement('span');
    assinatura.className = 'assinatura-mensagem';
    assinatura.textContent = tipo === 'usuario' ? 'VOCÊ' : 'JARVIS';
    const corpo = document.createElement('span');
    corpo.className = 'corpo-mensagem';
    corpo.textContent = texto;
    mensagemElemento.append(assinatura, corpo);
    areaConversa.appendChild(mensagemElemento);
    areaConversa.scrollTop = areaConversa.scrollHeight;
}

function atualizarSaudacao(texto) {
    document.querySelector('#saudacaoJarvis .corpo-mensagem').textContent = texto;
}

const temasProntos = {
    ciano: { rgb: [34, 211, 238] },
    violeta: { rgb: [192, 132, 252] },
    esmeralda: { rgb: [52, 211, 153] },
    coral: { rgb: [251, 113, 133] },
    ambar: { rgb: [251, 191, 36] },
    aurora: { rgb: [129, 140, 248] },
    reator: { rgb: [163, 230, 53] },
    eclipse: { rgb: [167, 139, 250] },
    oceano: { rgb: [56, 189, 248] },
    solar: { rgb: [251, 146, 60] }
};

function misturarCor(cor, fundo, proporcao) {
    return cor.map((canal, indice) => Math.round(canal * (1 - proporcao) + fundo[indice] * proporcao));
}

function luminancia(cor) {
    const linearizar = canal => {
        const valor = canal / 255;
        return valor <= 0.04045 ? valor / 12.92 : ((valor + 0.055) / 1.055) ** 2.4;
    };
    return 0.2126 * linearizar(cor[0]) + 0.7152 * linearizar(cor[1]) + 0.0722 * linearizar(cor[2]);
}

function corTexto(corFundo) {
    const claro = [255, 255, 255];
    const escuro = [8, 17, 28];
    const brilho = luminancia(corFundo);
    const contraste = cor => (Math.max(brilho, luminancia(cor)) + 0.05) / (Math.min(brilho, luminancia(cor)) + 0.05);
    return contraste(escuro) >= contraste(claro) ? '#08111c' : '#ffffff';
}

function salvarAparencia(valor) {
    try {
        localStorage.setItem('jarvis-aparencia-' + chaveAparenciaUsuario, JSON.stringify(valor));
    } catch (erro) {
        // A personalização continua ativa durante esta sessão mesmo sem armazenamento local.
    }
}

function atualizarPreviaRgb(rgb) {
    const hex = '#' + rgb.map(canal => canal.toString(16).padStart(2, '0')).join('').toUpperCase();
    document.getElementById('amostraCorAtual').style.backgroundColor = `rgb(${rgb.join(' ')})`;
    document.getElementById('codigoHex').textContent = hex;
    document.getElementById('valorVermelho').textContent = rgb[0];
    document.getElementById('valorVerde').textContent = rgb[1];
    document.getElementById('valorAzul').textContent = rgb[2];
}

function marcarTemaSelecionado(tema) {
    botoesTema.forEach(botao => {
        botao.setAttribute('aria-pressed', String(botao.dataset.tema === tema));
    });
}

function aplicarTemaPronto(tema, salvar = true) {
    const temaEscolhido = temasProntos[tema] ? tema : 'ciano';
    const rgb = temasProntos[temaEscolhido].rgb;
    const raiz = document.documentElement;
    raiz.dataset.tema = temaEscolhido;
    ['--accent', '--accent-light', '--accent-soft', '--accent-ink'].forEach(propriedade => {
        raiz.style.removeProperty(propriedade);
    });
    raiz.style.setProperty('--button-ink', corTexto(rgb));
    Object.values(controlesRgb).forEach((controle, indice) => { controle.value = rgb[indice]; });
    atualizarPreviaRgb(rgb);
    marcarTemaSelecionado(temaEscolhido);
    if (salvar) salvarAparencia({ modo: 'pronto', tema: temaEscolhido });
}

function aplicarRgbPersonalizado(salvar = true) {
    const rgb = Object.values(controlesRgb).map(controle => Number(controle.value));
    const raiz = document.documentElement;
    const clara = misturarCor(rgb, [255, 255, 255], 0.32);
    const escura = misturarCor(rgb, [17, 24, 39], 0.58);
    raiz.dataset.tema = 'personalizado';
    raiz.style.setProperty('--accent', `rgb(${rgb.join(' ')})`);
    raiz.style.setProperty('--accent-light', `rgb(${clara.join(' ')})`);
    raiz.style.setProperty('--accent-soft', `rgb(${escura.join(' ')})`);
    raiz.style.setProperty('--accent-ink', corTexto(escura));
    raiz.style.setProperty('--button-ink', corTexto(rgb));
    atualizarPreviaRgb(rgb);
    marcarTemaSelecionado('');
    if (salvar) salvarAparencia({ modo: 'rgb', rgb });
}

function carregarAparencia(usuario) {
    chaveAparenciaUsuario = usuario;
    try {
        const salva = JSON.parse(localStorage.getItem('jarvis-aparencia-' + usuario));
        if (salva?.modo === 'rgb' && Array.isArray(salva.rgb) && salva.rgb.length === 3) {
            Object.values(controlesRgb).forEach((controle, indice) => {
                controle.value = Math.min(255, Math.max(0, Number(salva.rgb[indice]) || 0));
            });
            aplicarRgbPersonalizado(false);
        } else if (salva?.modo === 'pronto' && temasProntos[salva.tema]) {
            aplicarTemaPronto(salva.tema, false);
        } else {
            aplicarTemaPronto(localStorage.getItem('jarvis-tema') || 'ciano', false);
        }
    } catch (erro) {
        aplicarTemaPronto('ciano', false);
    }
}

botoesTema.forEach(botao => {
    botao.addEventListener('click', () => aplicarTemaPronto(botao.dataset.tema));
});

Object.values(controlesRgb).forEach(controle => {
    controle.addEventListener('input', () => aplicarRgbPersonalizado());
});

botaoAparencia.addEventListener('click', () => dialogoAparencia.showModal());
document.getElementById('fecharAparencia').addEventListener('click', () => dialogoAparencia.close());
document.getElementById('concluirAparencia').addEventListener('click', () => dialogoAparencia.close());
document.getElementById('restaurarAparencia').addEventListener('click', () => aplicarTemaPronto('ciano'));
dialogoAparencia.addEventListener('click', evento => {
    if (evento.target === dialogoAparencia) dialogoAparencia.close();
});

carregarAparencia('visitante');

fetch('/status', { cache: 'no-store' })
    .then(response => {
        if (!response.ok) throw new Error('API indisponível');
        atualizarEstadoApi(true);
        atualizarStatusJarvis('pronto', 'Pronto');
    })
    .catch(() => {
        atualizarEstadoApi(false);
        atualizarStatusJarvis('erro', 'Sem conexão');
    });
