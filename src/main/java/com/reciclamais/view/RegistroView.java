package com.reciclamais.view;

import com.reciclamais.controller.RegistroController;
import com.reciclamais.model.RegistroReciclagem;
import com.reciclamais.model.RegistroReciclagem.Material;
import com.reciclamais.util.Sessao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Tela de gerenciamento de Registros de Reciclagem (entidade principal).
 */
public class RegistroView extends JFrame {

    // ─ Componentes principais 
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtBusca;
    private JButton btnNovo, btnEditar, btnExcluir, btnAtualizar, btnVoltar;

    // Dialog de formulário
    private JDialog dialog;

    // Campos do formulário
    private JComboBox<Material> fMaterial;
    private JTextField fPeso, fData, fPontoColeta, fObservacoes;

    private RegistroController controller;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Colunas da JTable
    private static final String[] COLUNAS = {
        "ID", "Material", "Peso (kg)", "Data", "Ponto de Coleta", "Responsável", "Observações"
    };

    public RegistroView() {
        // ─ Verificação de acesso 
        if (!Sessao.getInstance().estaLogado()) {
            JOptionPane.showMessageDialog(null, "Acesso negado. Faça login.");
            new LoginView().setVisible(true);
            dispose();
            return;
        }

        controller = new RegistroController(this);
        configurarJanela();
        construirUI();
        controller.carregarTabela();
    }

    // ─ Configuração da janela 
    private void configurarJanela() {
        setTitle("ReciclaMais — Registros de Reciclagem");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 580);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 460));
    }

    // ─ Construção da UI 
    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    // ─ Header verde com título e busca 
    private JPanel criarHeader() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(new Color(27, 107, 72));
        p.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lbl = new JLabel("Registros de Reciclagem  |  ODS 12");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        txtBusca = new JTextField(22);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 200, 170)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBusca.setToolTipText("Filtrar por material, ponto de coleta ou responsável");

        // Filtro em tempo real
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setOpaque(false);
        direita.add(new JLabel ("Buscar: "){{ setForeground(Color.WHITE); }});
        direita.add(txtBusca);

        p.add(lbl, BorderLayout.WEST);
        p.add(direita, BorderLayout.EAST);
        return p;
    }

    // ─ Painel da JTable
    private JPanel criarPainelTabela() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 16, 8, 16));
        p.setBackground(Color.WHITE);

        modelo = new DefaultTableModel(COLUNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(new Color(220, 230, 220));
        tabela.setSelectionBackground(new Color(200, 240, 215));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(220, 240, 225));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setAutoCreateRowSorter(true);

        // Esconde coluna ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        // Duplo clique editar
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirDialogEdicao();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(190, 220, 200)));
        p.add(scroll, BorderLayout.CENTER);

        // Rodapé com contagem
        JLabel lblContagem = new JLabel();
        lblContagem.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblContagem.setForeground(Color.GRAY);
        modelo.addTableModelListener(e ->
            lblContagem.setText(modelo.getRowCount() + " registro(s) encontrado(s)")
        );
        p.add(lblContagem, BorderLayout.SOUTH);

        return p;
    }

    // ─ Painel inferior com botões 
    private JPanel criarPainelBotoes() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8, 16, 16, 16));
        p.setBackground(Color.WHITE);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setOpaque(false);

        btnNovo = botao("Novo", new Color(27, 127, 90), Color.WHITE);
        btnEditar = botao("Editar", new Color(40, 100, 180), Color.WHITE);
        btnExcluir = botao("Excluir", new Color(190, 50, 50), Color.WHITE);
        btnAtualizar = botao("Atualizar", new Color(80, 80, 80), Color.WHITE);

        acoes.add(btnNovo);
        acoes.add(btnEditar);
        acoes.add(btnExcluir);
        acoes.add(btnAtualizar);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        nav.setOpaque(false);
        btnVoltar = botao("Menu Principal", new Color(90, 90, 90), Color.WHITE);
        nav.add(btnVoltar);

        // ─ Eventos 
        btnNovo.addActionListener(e -> abrirDialogCriacao());
        btnEditar.addActionListener(e -> abrirDialogEdicao());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnAtualizar.addActionListener(e -> controller.carregarTabela());
        btnVoltar.addActionListener(e -> {
            new MenuPrincipalView().setVisible(true);
            dispose();
        });

        p.add(acoes, BorderLayout.WEST);
        p.add(nav, BorderLayout.EAST);
        return p;
    }

    // ─ Dialog: Criar novo registro
    private void abrirDialogCriacao() {
        dialog = criarDialog("Novo Registro de Reciclagem", new Color(27, 127, 90), 460, 390);

        JPanel form = criarFormulario(null);

        JPanel painelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBtn.setBackground(Color.WHITE);
        JButton btnCancelar = botao("Cancelar", new Color(200, 200, 200), Color.DARK_GRAY);
        JButton btnSalvar = botao("Salvar", new Color(27, 127, 90),  Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSalvar.addActionListener(e -> {
            RegistroReciclagem r = coletarDadosFormulario(-1);
            if (r != null) controller.salvar(r, false);
        });

        painelBtn.add(btnCancelar);
        painelBtn.add(btnSalvar);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(painelBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─ Dialog: Editar registro selecionado
    private void abrirDialogEdicao() {
        int id = getIdSelecionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um registro para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RegistroReciclagem r = controller.buscarPorId(id);
        if (r == null) return;

        dialog = criarDialog("Editar Registro", new Color(40, 100, 180), 460, 390);

        JPanel form = criarFormulario(r); // pré-preenche com dados existentes

        JPanel painelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBtn.setBackground(Color.WHITE);
        JButton btnCancelar = botao("Cancelar", new Color(200, 200, 200), Color.DARK_GRAY);
        JButton btnSalvar = botao("Salvar ✔", new Color(40, 100, 180),  Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSalvar.addActionListener(e -> {
            RegistroReciclagem atualizado = coletarDadosFormulario(id);
            if (atualizado != null) controller.salvar(atualizado, true);
        });

        painelBtn.add(btnCancelar);
        painelBtn.add(btnSalvar);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(painelBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Monta o painel de formulário.
     * Se registro != null, pré-preenche os campos (modo edição).
     */
    private JPanel criarFormulario(RegistroReciclagem r) {
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);

        fMaterial = new JComboBox<>(Material.values());
        fPeso = new JTextField();
        fData = new JTextField(LocalDate.now().format(FMT)); // padrão: hoje
        fPontoColeta = new JTextField();
        fObservacoes = new JTextField();

        // Pré-preenchimento para edição
        if (r != null) {
            fMaterial.setSelectedItem(r.getMaterial());
            fPeso.setText(r.getPesoKg().toPlainString());
            fData.setText(r.getDataRegistro().format(FMT));
            fPontoColeta.setText(r.getPontoColeta());
            fObservacoes.setText(r.getObservacoes() != null ? r.getObservacoes() : "");
        }

        form.add(labelForm("Material *")); form.add(fMaterial);
        form.add(labelForm("Peso (kg) *")); form.add(fPeso);
        form.add(labelForm("Data (dd/MM/aaaa)")); form.add(fData);
        form.add(labelForm("Ponto de Coleta *")); form.add(fPontoColeta);
        form.add(labelForm("Observações")); form.add(fObservacoes);

        return form;
    }

    /**
     * Lê e valida os campos do formulário.
     * Retorna null se houver erro de validação.
     */
    private RegistroReciclagem coletarDadosFormulario(int id) {
        try {
            Material material = (Material) fMaterial.getSelectedItem();
            BigDecimal peso = new BigDecimal(fPeso.getText().trim().replace(",", "."));
            LocalDate data = LocalDate.parse(fData.getText().trim(), FMT);
            String coleta  = fPontoColeta.getText().trim();
            String obs = fObservacoes.getText().trim();
            int usuarioId = Sessao.getInstance().getUsuarioLogado().getId();

            if (coleta.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ponto de coleta é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            RegistroReciclagem r = new RegistroReciclagem(material, peso, data, coleta, obs, usuarioId);
            if (id > 0) r.setId(id);
            return r;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Peso inválido. Use números (ex: 1.5)", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(dialog, "Data inválida. Use o formato dd/MM/aaaa", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // ─ Exclui o registro selecionado
    private void excluirSelecionado() {
        int id = getIdSelecionado();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um registro para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int viewRow  = tabela.getSelectedRow();
        String desc  = modelo.getValueAt(tabela.convertRowIndexToModel(viewRow), 1).toString()
                     + " — " + modelo.getValueAt(tabela.convertRowIndexToModel(viewRow), 4).toString();
        controller.excluir(id, desc);
    }

    // ─ Retorna o ID da linha selecionada 
    private int getIdSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow == -1) return -1;
        return (int) modelo.getValueAt(tabela.convertRowIndexToModel(viewRow), 0);
    }

    // ─ Filtro em tempo real 
    private void filtrar() {
        String texto = txtBusca.getText().trim();
        if (tabela.getRowSorter() instanceof TableRowSorter<?> sorter) {
            sorter.setRowFilter(texto.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + texto, 1, 4, 5)); // material, coleta, responsável
        }
    }

    // ─ Preenche a JTable (chamado pelo Controller)
    public void preencherTabela(List<RegistroReciclagem> lista) {
        modelo.setRowCount(0);
        for (RegistroReciclagem r : lista) {
            modelo.addRow(new Object[]{
                r.getId(),
                r.getMaterial().getLabel(),
                r.getPesoKg() + " kg",
                r.getDataRegistro().format(FMT),
                r.getPontoColeta(),
                r.getUsuarioNome() != null ? r.getUsuarioNome() : "—",
                r.getObservacoes() != null ? r.getObservacoes() : "—"
            });
        }
    }

    // ─ Fecha o dialog aberto (chamado pelo Controller)
    public void fecharDialog() {
        if (dialog != null && dialog.isVisible()) dialog.dispose();
    }

    // ─ Helper: cria JDialog padronizado
    private JDialog criarDialog(String titulo, Color corTitulo, int w, int h) {
        JDialog d = new JDialog(this, titulo, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        d.setResizable(false);
        d.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("  " + titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(corTitulo);
        lblTitulo.setPreferredSize(new Dimension(0, 44));
        d.add(lblTitulo, BorderLayout.NORTH);

        return d;
    }

    // ── Helpers de estilo
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