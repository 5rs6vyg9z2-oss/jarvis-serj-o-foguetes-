// Modelo de dados: representa uma pessoa cadastrada no Jarvis.
public class Usuario {
    // Campos privados protegem os dados; o acesso acontece por getters e setters.
    private String nome;
    private String email;
    private String senha;

    // Construtor: obriga criar um Usuario ja com nome, email e senha.
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getter devolve o valor de um campo privado.
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Mantido para compatibilidade com a versao antiga que salvava usuario em texto.
    public String transformarEmLinha() {
        return nome + ";" + email + ";" + senha;
    }

    // Recria um Usuario a partir de uma linha no formato nome;email;senha.
    public static Usuario criarPelaLinha(String linha) {
        String[] partes = linha.split(";", -1);

        if (partes.length == 3) {
            return new Usuario(partes[0].trim(), partes[1].trim(), partes[2].trim());
        }

        return null;
    }
}
