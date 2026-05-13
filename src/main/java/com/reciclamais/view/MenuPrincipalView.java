package com.reciclamais.view;

import com.reciclamais.service.RegistroService;
import com.reciclamais.util.Sessao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Menu Principal (Dashboard) — tela após login.
 * Exibe resumo do sistema e navegação para os módulos.
 */
public class MenuPrincipalView extends JFrame {

    private final RegistroService registroService = new RegistroService();

    public MenuPrincipalView() {
        configurarJanela();
        construirUI();
    }

    private void configurarJanela() {
        setTitle("ReciclaMais — Painel Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 480));
    }

    private void construirUI() {
        setLayout(new BorderLayout());

        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelCards(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    // ─ Header 
    private JPanel criarHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(27, 107, 72));
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        String nome = Sessao.getInstance().getUsuarioLogado().getNome();
        JLabel lblOla = new JLabel("Olá, " + nome + "! 👋");
        lblOla.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblOla.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("ODS 12 — Consumo e Produção Responsáveis");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(180, 230, 200));

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSair.setBackground(new Color(200, 60, 60));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setBorderPainted(false);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Deseja sair?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                Sessao.getInstance().encerrar();
                dispose();
                new LoginView().setVisible(true);
            }
        });

        JPanel esquerda = new JPanel(new GridLayout(2, 1));
        esquerda.setOpaque(false);
        esquerda.add(lblOla);
        esquerda.add(lblSub);

        p.add(esquerda, BorderLayout.WEST);
        p.add(btnSair, BorderLayout.EAST);
        return p;
    }

    // ─ Cards de estatísticas 
    private JPanel criarPainelCards() {
        JPanel p = new JPanel(new GridLayout(1, 3, 20, 0));
        p.setBorder(new EmptyBorder(30, 30, 20, 30));
        p.setBackground(new Color(245, 250, 245));

        try {
            int total = registroService.totalRegistros();
            BigDecimal peso = registroService.totalPesoKg();
            p.add(criarCard("Registros", String.valueOf(total), new Color(27, 107, 72)));
            p.add(criarCard("Total reciclado", peso + " kg", new Color(32, 150, 100)));
            p.add(criarCard("ODS 12", "Consumo Responsável", new Color(60, 130, 80)));
        } catch (Exception e) {
            p.add(criarCard("Erro", "Falha ao carregar dados", Color.GRAY));
        }

        return p;
    }

    private JPanel criarCard(String titulo, String valor, Color cor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 230, 215), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));



        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(80, 100, 90));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblValor  = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(cor);
        lblValor.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(8));
        card.add(lblValor);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTitulo);

        return card;
    }

    // ─ Painel de navegação 
    private JPanel criarPainelBotoes() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setBackground(new Color(245, 250, 245));
        p.setBorder(new EmptyBorder(10, 30, 30, 30));

        JButton btnRegistros = criarBotaoNav("Registros de Reciclagem", new Color(27, 107, 72));
        JButton btnUsuarios = criarBotaoNav("Gerenciar Usuários", new Color(40, 80, 120));

        btnRegistros.addActionListener(e -> {
            new RegistroView().setVisible(true);
            dispose();
        });

        btnUsuarios.addActionListener(e -> {
            new UsuarioView().setVisible(true);
            dispose();
        });

        p.add(btnRegistros);
        p.add(btnUsuarios);
        return p;
    }

    private JButton criarBotaoNav(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(0, 56));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}