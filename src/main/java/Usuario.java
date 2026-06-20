public class Usuario {
    private String nome;
    private String email;
    private String senha;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

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

    public String transformarEmLinha() {
        return nome + ";" + email + ";" + senha;
    }

    public static Usuario criarPelaLinha(String linha) {
        String[] partes = linha.split(";", -1);

        if (partes.length == 3) {
            return new Usuario(partes[0].trim(), partes[1].trim(), partes[2].trim());
        }

        return null;
    }
}
