package com.reciclamais.util;

import com.reciclamais.model.Usuario;

/**
 * Sessão da aplicação — armazena o usuário logado (Singleton).
 * Simula o conceito de "sessão" em aplicações desktop.
 */
public class Sessao {

    private static Sessao  instancia;
    private Usuario usuarioLogado;

    private Sessao() {}

    public static Sessao getInstance() {
        if (instancia == null) instancia = new Sessao();
        return instancia;
    }

    public void iniciar(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void encerrar() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean estaLogado() {
        return usuarioLogado != null;
    }
}