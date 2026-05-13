package com.reciclamais.view;

import com.reciclamais.controller.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela de Login — primeira tela da aplicação.
 * Layout: centralizado, campo usuário + senha + botão entrar.
 */
public class LoginView extends JFrame {

    // ─ Componentes 
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCadastrar;
    private JLabel lblStatus;

    private final AuthController controller = new AuthController(this);

    public LoginView() {
        configurarJanela();
        construirUI();
        configurarEventos();
    }

    // ── Configuração da janela
    private void configurarJanela() {
        setTitle("ReciclaMais — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null); 
        setResizable(false);
        getContentPane().setBackground(new Color(245, 250, 245));
    }

    // ── Construção da interface
    private void construirUI() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(245, 250, 245));
        painel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // ─ Logo / título
        JLabel lblIcone = new JLabel("♻️", SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcone.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("ReciclaMais", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(27, 107, 72));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("ODS 12 — Consumo Responsável", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitulo.setForeground(new Color(100, 130, 110));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // ─ Campos
        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 0, 8));
        painelCampos.setOpaque(false);
        painelCampos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lblU = criarLabel("Usuário");
        txtUsuario = criarTextField("Digite seu usuário");

        JLabel lblS = criarLabel("Senha");
        txtSenha = new JPasswordField();
        estilizarCampo(txtSenha);

        painelCampos.add(lblU);
        painelCampos.add(txtUsuario);
        painelCampos.add(lblS);
        painelCampos.add(txtSenha);

        // ─ Botões
        btnEntrar = criarBotao("Entrar", new Color(27, 127, 90),  Color.WHITE);
        btnCadastrar = criarBotao("Criar conta", new Color(240, 248, 244), new Color(27, 107, 72));
        btnCadastrar.setBorder(BorderFactory.createLineBorder(new Color(27, 107, 72)));

        // ─ Status (erros)
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(180, 30, 30));
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);

        // ─ Montagem
        painel.add(lblIcone);
        painel.add(Box.createVerticalStrut(8));
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(lblSubtitulo);
        painel.add(Box.createVerticalStrut(30));
        painel.add(painelCampos);
        painel.add(Box.createVerticalStrut(16));
        painel.add(btnEntrar);
        painel.add(Box.createVerticalStrut(8));
        painel.add(btnCadastrar);
        painel.add(Box.createVerticalStrut(12));
        painel.add(lblStatus);

        add(painel);
    }

    // ─ Eventos 
    private void configurarEventos() {
        btnEntrar.addActionListener(e -> controller.fazerLogin(
            txtUsuario.getText().trim(),
            new String(txtSenha.getPassword())
        ));

        txtSenha.addActionListener(e -> btnEntrar.doClick());

        btnCadastrar.addActionListener(e -> {
            new CadastroView().setVisible(true);
            dispose();
        });
    }

    // ─ Helpers de criação de componentes 
    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(50, 80, 60));
        return l;
    }

    private JTextField criarTextField(String placeholder) {
        JTextField tf = new JTextField();
        estilizarCampo(tf);
        return tf;
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 190), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private JButton criarBotao(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Métodos públicos para o Controller 
    public void mostrarErro(String msg) {
        lblStatus.setText("⚠ " + msg);
        }
    public void limparStatus() {
        lblStatus.setText(" ");
        }
    public void setBotaoHabilitado(boolean b) {
        btnEntrar.setEnabled(b);
        }

    public static void main(String[] args) {
        // Habilita Look & Feel nativo do sistema operacional
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}