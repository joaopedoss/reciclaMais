package com.reciclamais.view;

import com.reciclamais.controller.UsuarioController;
import com.reciclamais.model.Usuario;
import com.reciclamais.util.Sessao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela de gerenciamento de Usuários.
 */
public class UsuarioView extends JFrame {

    // ─ Componentes principais 
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtBusca;
    private JButton btnNovo, btnEditar, btnSenha, btnExcluir, btnVoltar;

    // Dialog de formulário (criar/editar)
    private JDialog dialog;

    // Campos do formulário
    private JTextField fUsuario, fNome, fEmail, fTelefone;
    private JPasswordField fSenha, fConfirmar;

    private UsuarioController controller;

    // Colunas exibidas na JTable
    private static final String[] COLUNAS = {"ID", "Usuário", "Nome", "E-mail", "Telefone"};

    public UsuarioView() {
        // ─ Verificação de acesso
        if (!Sessao.getInstance().estaLogado()) {
            JOptionPane.showMessageDialog(null, "Acesso negado. Faça login.");
            new LoginView().setVisible(true);
            dispose();
            return;
        }

        controller = new UsuarioController(this);
        configurarJanela();
        construirUI();
        controller.carregarTabela();
    }

    // ─ Configuração da janela principal 
    private void configurarJanela() {
        setTitle("ReciclaMais — Gerenciar Usuários");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 440));
    }

    // ─ Construção da UI
    private void construirUI() {
        setLayout(new BorderLayout(0, 0));

        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    // ─ Header com título e busca 
    private JPanel criarHeader() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(new Color(40, 80, 120));
        p.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lbl = new JLabel("Gerenciamento de Usuários");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        // Campo de busca em tempo real
        txtBusca = new JTextField(20);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBusca.setToolTipText("Filtrar por nome, usuário ou e-mail");

        // Filtra a tabela conforme o usuário digita
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setOpaque(false);
        direita.add(new JLabel("Buscar: ") {{ setForeground(Color.WHITE); }});
        direita.add(txtBusca);

        p.add(lbl, BorderLayout.WEST);
        p.add(direita, BorderLayout.EAST);
        return p;
    }

    // ─ Painel central com JTable 
    private JPanel criarPainelTabela() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 16, 8, 16));
        p.setBackground(Color.WHITE);

        // Modelo de tabela não editável diretamente
        modelo = new DefaultTableModel(COLUNAS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(new Color(230, 235, 240));
        tabela.setSelectionBackground(new Color(210, 230, 255));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(230, 238, 250));
        tabela.getTableHeader().setReorderingAllowed(false);

        // Esconde coluna ID (índice 0) — usada internamente
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        // Habilita ordenação por clique no cabeçalho
        tabela.setAutoCreateRowSorter(true);

        // Duplo clique na linha abre edição
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirDialogEdicao();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 218, 230)));
        p.add(scroll, BorderLayout.CENTER);

        // Contador de registros
        JLabel lblContagem = new JLabel();
        lblContagem.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblContagem.setForeground(Color.GRAY);
        modelo.addTableModelListener(e -> lblContagem.setText(modelo.getRowCount() + " usuário(s) encontrado(s)"));
        p.add(lblContagem, BorderLayout.SOUTH);

        return p;
    }

    // ─ Painel inferior com botões de ação
    private JPanel criarPainelBotoes() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8, 16, 16, 16));
        p.setBackground(Color.WHITE);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setOpaque(false);

        btnNovo = botao("Novo", new Color(27, 127, 90),  Color.WHITE);
        btnEditar = botao("Editar", new Color(40, 100, 180), Color.WHITE);
        btnSenha = botao("Senha", new Color(140, 90, 180), Color.WHITE);
        btnExcluir = botao("Excluir", new Color(190, 50, 50),  Color.WHITE);

        acoes.add(btnNovo);
        acoes.add(btnEditar);
        acoes.add(btnSenha);
        acoes.add(btnExcluir);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        nav.setOpaque(false);
        btnVoltar = botao("← Menu Principal", new Color(90, 90, 90), Color.WHITE);
        nav.add(btnVoltar);

        p.add(acoes, BorderLayout.WEST);
        p.add(nav,   BorderLayout.EAST);

        // ─ Eventos dos botões 
        btnNovo.addActionListener(e -> abrirDialogCriacao());
        btnEditar.addActionListener(e -> abrirDialogEdicao());
        btnSenha.addActionListener(e -> abrirDialogSenha());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnVoltar.addActionListener(e -> {
            new MenuPrincipalView().setVisible(true);
            dispose();
        });

        return p;
    }

    // ─ Dialog: Criar novo usuário
    private void abrirDialogCriacao() {
        dialog = new JDialog(this, "Novo Usuário", true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        // Título do dialog
        JLabel titulo = new JLabel("Novo Usuário");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(27, 127, 90));
        titulo.setPreferredSize(new Dimension(0, 44));
        dialog.add(titulo, BorderLayout.NORTH);

        // Formulário
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);

        fUsuario = new JTextField();
        fNome = new JTextField();
        fEmail = new JTextField();
        fTelefone = new JTextField();
        fSenha = new JPasswordField();
        fConfirmar = new JPasswordField();

        form.add(labelForm("Usuário *")); form.add(fUsuario);
        form.add(labelForm("Nome *")); form.add(fNome);
        form.add(labelForm("E-mail *")); form.add(fEmail);
        form.add(labelForm("Telefone")); form.add(fTelefone);
        form.add(labelForm("Senha *")); form.add(fSenha);
        form.add(labelForm("Confirmar *")); form.add(fConfirmar);

        // Botões do dialog
        JPanel painelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBtn.setBackground(Color.WHITE);
        JButton btnCancelar = botao("Cancelar", new Color(200, 200, 200), Color.DARK_GRAY);
        JButton btnSalvar = botao("Salvar", new Color(27, 127, 90),  Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSalvar.addActionListener(e -> {
            // Verifica confirmação de senha
            String s1 = new String(fSenha.getPassword());
            String s2 = new String(fConfirmar.getPassword());
            if (!s1.equals(s2)) {
                JOptionPane.showMessageDialog(dialog, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario u = new Usuario(
                fUsuario.getText().trim(),
                fNome.getText().trim(),
                fEmail.getText().trim(),
                s1,
                fTelefone.getText().trim()
            );
            controller.salvar(u, false);
        });

        painelBtn.add(btnCancelar);
        painelBtn.add(btnSalvar);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(painelBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─ Dialog: Editar usuário selecionado
    private void abrirDialogEdicao() {
        int id = getIdSelecionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = controller.buscarPorId(id);
        if (u == null) return;

        dialog = new JDialog(this, "Editar Usuário", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Editar Usuário");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(40, 100, 180));
        titulo.setPreferredSize(new Dimension(0, 44));
        dialog.add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);

        fUsuario = new JTextField(u.getUsuario());
        fNome = new JTextField(u.getNome());
        fEmail = new JTextField(u.getEmail());
        fTelefone = new JTextField(u.getTelefone() != null ? u.getTelefone() : "");

        form.add(labelForm("Usuário *")); form.add(fUsuario);
        form.add(labelForm("Nome *")); form.add(fNome);
        form.add(labelForm("E-mail *")); form.add(fEmail);
        form.add(labelForm("Telefone")); form.add(fTelefone);

        JPanel painelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBtn.setBackground(Color.WHITE);
        JButton btnCancelar = botao("Cancelar", new Color(200, 200, 200), Color.DARK_GRAY);
        JButton btnSalvar = botao("Salvar", new Color(40, 100, 180),  Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSalvar.addActionListener(e -> {
            u.setUsuario(fUsuario.getText().trim());
            u.setNome(fNome.getText().trim());
            u.setEmail(fEmail.getText().trim());
            u.setTelefone(fTelefone.getText().trim());
            controller.salvar(u, true);
        });

        painelBtn.add(btnCancelar);
        painelBtn.add(btnSalvar);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(painelBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─ Dialog: Alterar senha
    private void abrirDialogSenha() {
        int id = getIdSelecionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dialog = new JDialog(this, "Alterar Senha", true);
        dialog.setSize(360, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Alterar Senha");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(140, 90, 180));
        titulo.setPreferredSize(new Dimension(0, 44));
        dialog.add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);

        JPasswordField novaSenha = new JPasswordField();
        JPasswordField confirmacao = new JPasswordField();

        form.add(labelForm("Nova senha *")); form.add(novaSenha);
        form.add(labelForm("Confirmar *")); form.add(confirmacao);

        JPanel painelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBtn.setBackground(Color.WHITE);
        JButton btnCancelar = botao("Cancelar", new Color(200, 200, 200), Color.DARK_GRAY);
        JButton btnSalvar = botao("Alterar", new Color(140, 90, 180),  Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSalvar.addActionListener(e ->
            controller.alterarSenha(
                id,
                new String(novaSenha.getPassword()),
                new String(confirmacao.getPassword())
            )
        );

        painelBtn.add(btnCancelar);
        painelBtn.add(btnSalvar);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(painelBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─ Exclui o usuário selecionado 
    private void excluirSelecionado() {
        int id = getIdSelecionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Recupera o nome da linha selecionada (coluna 2 = nome)
        int    viewRow = tabela.getSelectedRow();
        String nome    = modelo.getValueAt(tabela.convertRowIndexToModel(viewRow), 2).toString();
        controller.excluir(id, nome);
    }

    // ─ Retorna o ID da linha selecionada na JTable
    private int getIdSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow == -1) return -1;
        return (int) modelo.getValueAt(tabela.convertRowIndexToModel(viewRow), 0);
    }

    // ─ Filtro de busca em tempo real
    private void filtrar() {
        String texto = txtBusca.getText().trim();
        if (tabela.getRowSorter() instanceof TableRowSorter<?> sorter) {
            sorter.setRowFilter(texto.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + texto, 1, 2, 3)); // colunas: usuário, nome, email
        }
    }

    // ─ Método chamado pelo Controller para preencher a JTable
    public void preencherTabela(List<Usuario> usuarios) {
        modelo.setRowCount(0); // limpa antes de repopular
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{
                u.getId(),
                u.getUsuario(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone() != null ? u.getTelefone() : "—"
            });
        }
    }

    // ─ Fecha o dialog aberto (chamado pelo Controller)
    public void fecharDialog() {
        if (dialog != null && dialog.isVisible()) dialog.dispose();
    }

    // ─ Helpers de estilo
    private JLabel labelForm(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(60, 70, 80));
        return l;
    }

    private JButton botao(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}