package com.reciclamais.controller;

import com.reciclamais.model.Usuario;
import com.reciclamais.service.UsuarioService;
import com.reciclamais.util.Sessao;
import com.reciclamais.view.UsuarioView;

import javax.swing.*;
import java.util.List;

/**
 * Controller de Usuários.
 * Intermediário entre UsuarioView e UsuarioService.
 * A View nunca acessa o Service ou DAO diretamente.
 */
public class UsuarioController {

    private final UsuarioView    view;
    private final UsuarioService service = new UsuarioService();

    public UsuarioController(UsuarioView view) {
        this.view = view;
    }

    public void carregarTabela() {
        SwingWorker<List<Usuario>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return service.listarTodos();
            }

            @Override
            protected void done() {
                try {
                    view.preencherTabela(get());
                } catch (Exception e) {
                    mostrarErro("Erro ao carregar usuários: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void salvar(Usuario usuario, boolean isEdicao) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String mensagem;

            @Override
            protected Void doInBackground() throws Exception {
                if (isEdicao) {
                    service.atualizar(usuario);
                    mensagem = "Usuário atualizado com sucesso!";
                } else {
                    service.cadastrar(usuario);
                    mensagem = "Usuário cadastrado com sucesso!";
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

    public void alterarSenha(int usuarioId, String novaSenha, String confirmacao) {
        if (!novaSenha.equals(confirmacao)) {
            mostrarErro("As senhas não coincidem.");
            return;
        }
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                service.alterarSenha(usuarioId, novaSenha);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                        view, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE
                    );
                    view.fecharDialog();
                } catch (Exception e) {
                    Throwable c = e.getCause() != null ? e.getCause() : e;
                    mostrarErro(c.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void excluir(int id, String nomeUsuario) {
        if (Sessao.getInstance().getUsuarioLogado().getId() == id) {
            mostrarErro("Você não pode excluir seu próprio usuário.");
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
            view,
            "Tem certeza que deseja excluir o usuário \"" + nomeUsuario + "\"?\n"
                + "Todos os registros vinculados também serão excluídos.",
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
                        view, "Usuário excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarTabela();
                } catch (Exception e) {
                    mostrarErro("Erro ao excluir: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public Usuario buscarPorId(int id) {
        try {
            return service.buscarPorId(id).orElse(null);
        } catch (Exception e) {
            mostrarErro("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}