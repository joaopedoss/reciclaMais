package com.reciclamais.controller;

import com.reciclamais.model.Usuario;
import com.reciclamais.service.UsuarioService;
import com.reciclamais.util.Sessao;
import com.reciclamais.view.LoginView;
import com.reciclamais.view.MenuPrincipalView;

import javax.swing.*;

/**
 * Controller de autenticação.
 * Intermediário entre LoginView e UsuarioService.
 */
public class AuthController {

    private final LoginView view;
    private final UsuarioService service = new UsuarioService();

    public AuthController(LoginView view) {
        this.view = view;
    }

    /**
     * Tenta autenticar o usuário.
     * Executado em thread separada para não travar a UI.
     */
    public void fazerLogin(String usuario, String senha) {
        view.limparStatus();

        if (usuario.isBlank() || senha.isBlank()) {
            view.mostrarErro("Preencha usuário e senha.");
            return;
        }

        view.setBotaoHabilitado(false);

        // SwingWorker: operação pesada (JDBC) fora da EDT
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return service.autenticar(usuario, senha);
            }

            @Override
            protected void done() {
                view.setBotaoHabilitado(true);
                try {
                    Usuario u = get();
                    Sessao.getInstance().iniciar(u);
                    view.dispose();
                    SwingUtilities.invokeLater(() -> new MenuPrincipalView().setVisible(true));
                } catch (Exception e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    view.mostrarErro(msg);
                }
            }
        };

        worker.execute();
    }
}