package br.com.meira.jarvis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.meira.jarvis.model.Usuario;
import br.com.meira.jarvis.service.GerenciadorUsuarios;
import br.com.meira.jarvis.service.JarvisService;

@RestController
public class JarvisController {
    private final JarvisService jarvisService;
    private final GerenciadorUsuarios gerenciadorUsuarios;

    public JarvisController(JarvisService jarvisService, GerenciadorUsuarios gerenciadorUsuarios) {
        this.jarvisService = jarvisService;
        this.gerenciadorUsuarios = gerenciadorUsuarios;
    }

    @GetMapping("/status")
    public String status() {
        return "Jarvis API ta on.";
    }

    @GetMapping("/mensagem")
    public String mensagem(@RequestParam String texto) {
        return jarvisService.processarComando(texto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = gerenciadorUsuarios.autenticar(
                loginRequest.getEmail(),
                loginRequest.getSenha());

        if (usuario != null) {
            return "Login bem-sucedido! Usuario: " + usuario.getNome();
        }

        return "Credenciais invalidas.";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@RequestBody CadastroRequest cadastroRequest) {
        Usuario novoUsuario = new Usuario(
                cadastroRequest.getNome(),
                cadastroRequest.getEmail(),
                cadastroRequest.getSenha());

        boolean cadastrado = gerenciadorUsuarios.adicionarUsuario(novoUsuario);

        if (cadastrado) {
            return "Cadastro bem-sucedido! Usuario: " + novoUsuario.getNome();
        }

        return "Nao foi possivel cadastrar usuario.";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(@RequestBody AlterarSenhaRequest alterarSenhaRequest) {
        boolean alterada = gerenciadorUsuarios.alterarSenha(
                alterarSenhaRequest.getEmail(),
                alterarSenhaRequest.getSenhaAtual(),
                alterarSenhaRequest.getNovaSenha());

        if (alterada) {
            return "Senha alterada com sucesso.";
        }

        return "Nao foi possivel alterar a senha. Confira o email e a senha atual.";
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
