import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class janelajarvis {
    
    public static void main(String[] args) {
        GerenciadorUsuarios gerenciadorUsuarios = new GerenciadorUsuarios();
        Calculadora calculadora = new Calculadora();
        System.out.println("Ola, eu sou o Jarvis, seu assistente pessoal. Como posso ajudar voce hoje?");
    
    
    JFrame janela = new JFrame("jarvis");
    janela.setSize(1500, 900);
    janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    janela.setLocationRelativeTo(null);
    janela.getContentPane().setBackground(Color.BLACK);
    
    JPanel painel = new JPanel();
    painel.setBackground(new Color(15, 18, 25));
    painel.setLayout(new BorderLayout());
    janela.add(painel);
    
    JLabel titulo = new JLabel("Jarvis");
    titulo.setForeground(Color.BLACK);
    painel.add(titulo, BorderLayout.NORTH);
    
    JTextArea campoUsuario = new JTextArea();
    JTextField campoEmail = new JTextField();
    JPasswordField campoSenha = new JPasswordField();
    JButton botaoEntrar = new JButton();
    JTextField campoMensagem = new JTextField();
    JButton botaoEnviar = new JButton("ENVIAR");
    JTextArea areaConversArea = new JTextArea();
    areaConversArea.append("ola, sou Serjao foguetes.\n");
    areaConversArea.append("em que posso te ajudar?\n");
    
    botaoEntrar.addActionListener(evento -> {
        String username = campoUsuario.getText();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        
        Usuario usuario = gerenciadorUsuarios.autenticar(email, senha);
        
        areaConversArea.append("clicou em entrar\n");
        areaConversArea.append("usuario: " + username + "\n");
        areaConversArea.append("email: " + email + "\n");
        areaConversArea.append("senha com " + senha.length() + " caracteres\n");
        areaConversArea.append("O usuario: " + usuario + "\n");
    });

    botaoEnviar.addActionListener(evento -> {
        String resposta = campoMensagem.getText().trim();

        areaConversArea.append("Voce: " + resposta + "\n");

        if (calculadora.temOperacao(resposta)) {
            String resultado = calculadora.calcular(resposta);
            areaConversArea.append("Jarvis: " + resultado + "\n");
        } else {
            areaConversArea.append("Jarvis: nao entendi.\n");
        }

        campoMensagem.setText("");
    });
    
    JPanel painelLogin = new JPanel();
    painelLogin.add(campoEmail);
    painelLogin.add(campoSenha);
    painelLogin.add(botaoEntrar);
    painelLogin.add(campoMensagem);
    painelLogin.add(botaoEnviar);
    
    painel.add(areaConversArea, BorderLayout.CENTER);
    painel.add(painelLogin, BorderLayout.SOUTH);
    
    janela.setVisible(true);
    }
}
