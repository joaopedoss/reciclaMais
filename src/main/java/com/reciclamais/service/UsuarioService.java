package com.reciclamais.service;

import com.reciclamais.dao.UsuarioDAO;
import com.reciclamais.model.Usuario;
import com.reciclamais.util.SenhaUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service de Usuario — regras de negócio e validações.
 * A View NUNCA acessa o DAO diretamente.
 */
public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario autenticar(String usuario, String senha) throws Exception {
        Optional<Usuario> opt = dao.buscarPorUsuario(usuario);

        if (opt.isEmpty()) throw new Exception("Usuário não encontrado.");

        Usuario u = opt.get();
        if (!SenhaUtil.verificar(senha, u.getSenha()))
            throw new Exception("Senha incorreta.");

        return u;
    }

    public void cadastrar(Usuario u) throws Exception {
        validar(u);
        if (dao.usuarioExiste(u.getUsuario()))
            throw new Exception("Nome de usuário já em uso.");
        if (dao.emailExiste(u.getEmail()))
            throw new Exception("E-mail já cadastrado.");

        u.setSenha(SenhaUtil.hash(u.getSenha())); // hash antes de salvar
        dao.inserir(u);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Optional<Usuario> buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void atualizar(Usuario u) throws Exception {
        if (u.getNome() == null || u.getNome().isBlank()) throw new Exception("Nome obrigatório.");
        if (u.getEmail() == null || u.getEmail().isBlank()) throw new Exception("E-mail obrigatório.");
        if (u.getUsuario() == null || u.getUsuario().isBlank()) throw new Exception("Usuário obrigatório.");
        dao.atualizar(u);
    }

    public void alterarSenha(int id, String novaSenha) throws Exception {
        if (novaSenha == null || novaSenha.length() < 6)
            throw new Exception("A senha deve ter no mínimo 6 caracteres.");
        dao.atualizarSenha(id, SenhaUtil.hash(novaSenha));
    }

    public void excluir(int id) throws SQLException {
        dao.excluir(id);
    }


    private void validar(Usuario u) throws Exception {
        if (u.getUsuario() == null || u.getUsuario().isBlank()) throw new Exception("Usuário obrigatório.");
        if (u.getNome() == null || u.getNome().isBlank()) throw new Exception("Nome obrigatório.");
        if (u.getEmail() == null || u.getEmail().isBlank()) throw new Exception("E-mail obrigatório.");
        if (u.getSenha() == null || u.getSenha().length() < 6)
            throw new Exception("Senha deve ter no mínimo 6 caracteres.");
        if (!u.getEmail().contains("@")) throw new Exception("E-mail inválido.");
    }
}