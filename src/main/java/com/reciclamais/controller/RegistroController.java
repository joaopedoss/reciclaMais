package com.reciclamais.controller;

import com.reciclamais.model.RegistroReciclagem;
import com.reciclamais.service.RegistroService;
import com.reciclamais.view.RegistroView;

import javax.swing.*;
import java.util.List;

/**
 * Controller de RegistroReciclagem.
 * Intermediário entre RegistroView e RegistroService.
 */
public class RegistroController {

    private final RegistroView    view;
    private final RegistroService service = new RegistroService();

    public RegistroController(RegistroView view) {
        this.view = view;
    }

    public void carregarTabela() {
        SwingWorker<List<RegistroReciclagem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RegistroReciclagem> doInBackground() throws Exception {
                return service.listarTodos();
            }

            @Override
            protected void done() {
                try {
                    view.preencherTabela(get());
                } catch (Exception e) {
                    mostrarErro("Erro ao carregar registros: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void salvar(RegistroReciclagem registro, boolean isEdicao) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String mensagem;

            @Override
            protected Void doInBackground() throws Exception {
                if (isEdicao) {
                    service.atualizar(registro);
                    mensagem = "Registro atualizado com sucesso!";
                } else {
                    service.cadastrar(registro);
                    mensagem = "Registro cadastrado com sucesso!";
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                        view, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE
                    );
                    view.fecharDialog();
                    carregarTabela();
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    mostrarErro(causa.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void excluir(int id, String descricao) {
        int resposta = JOptionPane.showConfirmDialog(
            view,
            "Excluir o registro \"" + descricao + "\"?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                service.excluir(id);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                        view, "Registro excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarTabela();
                } catch (Exception e) {
                    mostrarErro("Erro ao excluir: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public RegistroReciclagem buscarPorId(int id) {
        try {
            return service.buscarPorId(id).orElse(null);
        } catch (Exception e) {
            mostrarErro("Erro ao buscar registro: " + e.getMessage());
            return null;
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}