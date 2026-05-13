package com.reciclamais.view;

import com.reciclamais.model.Usuario;
import com.reciclamais.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela de cadastro de novo usuário (pública — sem login).
 */
public class CadastroView extends JFrame {

    private JTextField txtUsuario, txtNome, txtEmail, txtTelefone;
    private JPasswordField txtSenha, txtConfirmar;
    private JButton btnSalvar, btnVoltar;
    private JLabel lblStatus;

    private final UsuarioService service = new UsuarioService();

    public CadastroView() {
        configurarJanela();
        construirUI();
    }

    private void configurarJanela() {
        setTitle("ReciclaMais - Criar Conta");
        setSize(440, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 250, 245));
    }

    private void construirUI() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(245, 250, 245));
        painel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Título
        JLabel lbl = new JLabel("Criar conta", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(27, 107, 72));
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        // Campos em grid 2 colunas
        JPanel grid = new JPanel(new GridLayout(6, 2, 10, 8));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        txtUsuario  = new JTextField();
        txtNome     = new JTextField();
        txtEmail    = new JTextField();
        txtTelefone = new JTextField();
        txtSenha    = new JPasswordField();
        txtConfirmar = new JPasswordField();

        grid.add(label("Usuário *"));    grid.add(txtUsuario);
        grid.add(label("Nome *"));       grid.add(txtNome);
        grid.add(label("E-mail *"));     grid.add(txtEmail);
        grid.add(label("Telefone"));     grid.add(txtTelefone);
        grid.add(label("Senha *"));      grid.add(txtSenha);
        grid.add(label("Confirmar *"));  grid.add(txtConfirmar);

        btnSalvar  = botao("Criar conta", new Color(27, 107, 72),  Color.WHITE);
        btnVoltar  = botao("← Voltar",   new Color(240, 248, 244), new Color(27, 107, 72));

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.RED);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);


        btnSalvar.addActionListener(e -> salvar());
        btnVoltar.addActionListener(e -> { new LoginView().setVisible(true); dispose(); });

        painel.add(lbl);
        painel.add(Box.createVerticalStrut(20));
        painel.add(grid);
        painel.add(Box.createVerticalStrut(16));
        painel.add(btnSalvar);
        painel.add(Box.createVerticalStrut(8));
        painel.add(btnVoltar);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblStatus);

        add(painel);
    }

    private void salvar() {
        String senhaTexto    = new String(txtSenha.getPassword());
        String confirmaTexto = new String(txtConfirmar.getPassword());

        if (!senhaTexto.equals(confirmaTexto)) {
            lblStatus.setText("⚠ As senhas não coincidem.");
            return;
        }

        Usuario u = new Usuario(
            txtUsuario.getText().trim(),
            txtNome.getText().trim(),
            txtEmail.getText().trim(),
            senhaTexto,
            txtTelefone.getText().trim()
        );

        try {
            service.cadastrar(u);
            JOptionPane.showMessageDialog(this, "Conta criada com sucesso! Faça login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            new LoginView().setVisible(true);
            dispose();
        } catch (Exception ex) {
            lblStatus.setText("⚠ " + ex.getMessage());
        }
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JButton botao(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}