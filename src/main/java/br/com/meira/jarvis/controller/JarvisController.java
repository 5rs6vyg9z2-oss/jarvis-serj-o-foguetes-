package br.com.meira.jarvis.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import br.com.meira.jarvis.model.Usuario;
import br.com.meira.jarvis.service.GerenciadorUsuarios;
import br.com.meira.jarvis.service.JarvisService;
import br.com.meira.jarvis.service.SinteseAudioService;
import br.com.meira.jarvis.service.TranscricaoAudioService;

@RestController
public class JarvisController {
    private final JarvisService jarvisService;
    private final GerenciadorUsuarios gerenciadorUsuarios;
    private final TranscricaoAudioService transcricaoAudioService;
    private final SinteseAudioService sinteseAudioService;

    public JarvisController(
            JarvisService jarvisService,
            GerenciadorUsuarios gerenciadorUsuarios,
            TranscricaoAudioService transcricaoAudioService,
            SinteseAudioService sinteseAudioService) {
        this.jarvisService = jarvisService;
        this.gerenciadorUsuarios = gerenciadorUsuarios;
        this.transcricaoAudioService = transcricaoAudioService;
        this.sinteseAudioService = sinteseAudioService;
    }

    @GetMapping("/status")
    public String status() {
        return "Jarvis API ta on.";
    }

    @PostMapping("/mensagem")
    public ResponseEntity<String> mensagem(
        @RequestBody MensagemRequest requisicao,
        HttpSession sessao){
            if (!sessaoAtiva(sessao)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body("faça login antes de conversar com o jarvis.");
            }

            if (requisicao == null || requisicao.getTexto() == null || requisicao.getTexto().isBlank()){
                return ResponseEntity.badRequest().body("digite uma mensagem");
            }
            if (requisicao.getTexto().length() > 4000) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("a mensagem ultrapassou o limite de 4000 caracteres.");
            }
            return ResponseEntity.ok(
                jarvisService.processarComando(requisicao.getTexto()));
            
        }
    
    @PostMapping(value = "/transcrever", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> transcrever(
            @RequestParam("audio") MultipartFile audio,
            HttpSession sessao) {
        if (!sessaoAtiva(sessao)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("faca login antes de usar o microfone.");
        }

        if (audio == null || audio.isEmpty()) {
            return ResponseEntity.badRequest().body("o audio enviado esta vazio.");
        }

        String tipoAudio = audio.getContentType();
        if (tipoAudio == null || !tipoAudio.toLowerCase().startsWith("audio/")) {
            return ResponseEntity.badRequest().body("envie um arquivo de audio valido.");
        }

        if (audio.getSize() > 20L * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("a gravacao ultrapassou o limite de 20 MB.");
        }

        try {
            String texto = transcricaoAudioService.transcrever(audio);
            if (texto.isBlank()) {
                return ResponseEntity.badRequest().body("nao consegui identificar fala no audio.");
            }
            return ResponseEntity.ok(texto);
        } catch (RestClientException | IllegalStateException erro) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("nao consegui transcrever o audio. tente novamente.");
        }
    }

    @PostMapping(value = "/sintetizar", produces = "audio/wav")
    public ResponseEntity<byte[]> sintetizar(
            @RequestBody MensagemRequest requisicao,
            HttpSession sessao) {
        if (!sessaoAtiva(sessao)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (requisicao == null || requisicao.getTexto() == null || requisicao.getTexto().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (requisicao.getTexto().length() > 5000) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        try {
            byte[] audio = sinteseAudioService.sintetizar(requisicao.getTexto());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .header("Cache-Control", "no-store")
                    .body(audio);
        } catch (IllegalStateException erro) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest loginRequest,
            HttpSession sessao,
            HttpServletRequest requisicaoHttp) {
        if (loginRequest == null || textoVazio(loginRequest.getEmail())
                || textoVazio(loginRequest.getSenha()) || loginRequest.getEmail().length() > 254
                || senhaExcedeLimite(loginRequest.getSenha())) {
            return ResponseEntity.badRequest().body("informe um email e uma senha validos.");
        }

        Usuario usuario = gerenciadorUsuarios.autenticar(
                loginRequest.getEmail(),
                loginRequest.getSenha());

        if (usuario != null) {
            requisicaoHttp.changeSessionId();
            sessao.removeAttribute("modoVisitante");
            sessao.setAttribute("usuarioEmail", usuario.getEmail());
            sessao.setAttribute("usuarioNome", usuario.getNome());
            return ResponseEntity.ok("Login bem-sucedido! Usuario: " + usuario.getNome());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais invalidas.");
    }

    @PostMapping("/visitante")
    public ResponseEntity<String> entrarComoVisitante(HttpSession sessao, HttpServletRequest requisicaoHttp) {
        requisicaoHttp.changeSessionId();
        sessao.setAttribute("modoVisitante", true);
        return ResponseEntity.ok("Acesso como visitante iniciado.");
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@RequestBody CadastroRequest cadastroRequest) {
        if (cadastroRequest == null || textoVazio(cadastroRequest.getNome())
                || textoVazio(cadastroRequest.getEmail()) || textoVazio(cadastroRequest.getSenha())
                || cadastroRequest.getNome().length() > 100 || cadastroRequest.getEmail().length() > 254
                || senhaExcedeLimite(cadastroRequest.getSenha())) {
            return ResponseEntity.badRequest().body("confira nome, email e senha; os campos devem respeitar os limites permitidos.");
        }

        Usuario novoUsuario = new Usuario(
                cadastroRequest.getNome(),
                cadastroRequest.getEmail(),
                cadastroRequest.getSenha());

        boolean cadastrado = gerenciadorUsuarios.adicionarUsuario(novoUsuario);

        if (cadastrado) {
            return ResponseEntity.ok("Cadastro bem-sucedido! Usuario: " + novoUsuario.getNome());
        }

        return ResponseEntity.badRequest().body("Nao foi possivel cadastrar usuario.");
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhaRequest alterarSenhaRequest, HttpSession sessao) {
        if (alterarSenhaRequest == null || textoVazio(alterarSenhaRequest.getEmail())
                || textoVazio(alterarSenhaRequest.getSenhaAtual())
                || textoVazio(alterarSenhaRequest.getNovaSenha())
                || alterarSenhaRequest.getEmail().length() > 254
                || senhaExcedeLimite(alterarSenhaRequest.getSenhaAtual())
                || senhaExcedeLimite(alterarSenhaRequest.getNovaSenha())) {
            return ResponseEntity.badRequest().body("informe email e senhas validos.");
        }

        String emailLogado = (String) sessao.getAttribute("usuarioEmail");
        if (emailLogado == null || !emailLogado.equals(alterarSenhaRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("faca login na conta cuja senha deseja alterar.");
        }

        boolean alterada = gerenciadorUsuarios.alterarSenha(
                alterarSenhaRequest.getEmail(),
                alterarSenhaRequest.getSenhaAtual(),
                alterarSenhaRequest.getNovaSenha());

        if (alterada) {
            return ResponseEntity.ok("Senha alterada com sucesso.");
        }

        return ResponseEntity.badRequest()
                .body("Nao foi possivel alterar a senha. Confira o email e a senha atual.");
    }

    private boolean sessaoAtiva(HttpSession sessao) {
        return sessao.getAttribute("usuarioEmail") != null
                || Boolean.TRUE.equals(sessao.getAttribute("modoVisitante"));
    }

    private boolean textoVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private boolean senhaExcedeLimite(String senha) {
        return senha.getBytes(StandardCharsets.UTF_8).length > 72;
    }
}

class LoginRequest {
    private String email;
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}

class CadastroRequest {
    private String nome;
    private String email;
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

class AlterarSenhaRequest {
    private String email;
    private String senhaAtual;
    private String novaSenha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

}
class MensagemRequest{
    private String texto;

    public String getTexto(){
        return texto;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }
}

