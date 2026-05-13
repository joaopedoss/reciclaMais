package com.reciclamais.service;

import com.reciclamais.dao.RegistroReciclagemDAO;
import com.reciclamais.model.RegistroReciclagem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service de RegistroReciclagem — regras de negócio.
 */
public class RegistroService {

    private final RegistroReciclagemDAO dao = new RegistroReciclagemDAO();

    public void cadastrar(RegistroReciclagem r) throws Exception {
        validar(r);
        dao.inserir(r);
    }

    public List<RegistroReciclagem> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public List<RegistroReciclagem> listarPorUsuario(int usuarioId) throws SQLException {
        return dao.listarPorUsuario(usuarioId);
    }

    public Optional<RegistroReciclagem> buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void atualizar(RegistroReciclagem r) throws Exception {
        validar(r);
        dao.atualizar(r);
    }

    public void excluir(int id) throws SQLException {
        dao.excluir(id);
    }

    public BigDecimal totalPesoKg() throws SQLException { return dao.totalPesoKg(); }
    public int totalRegistros() throws SQLException { return dao.totalRegistros(); }

    private void validar(RegistroReciclagem r) throws Exception {
        if (r.getMaterial() == null) throw new Exception("Material obrigatório.");
        if (r.getPesoKg() == null || r.getPesoKg().compareTo(BigDecimal.ZERO) <= 0)
            throw new Exception("Peso deve ser maior que zero.");
        if (r.getDataRegistro() == null) throw new Exception("Data obrigatória.");
        if (r.getDataRegistro().isAfter(LocalDate.now()))
            throw new Exception("Data não pode ser no futuro.");
        if (r.getPontoColeta() == null || r.getPontoColeta().isBlank())
            throw new Exception("Ponto de coleta obrigatório.");
    }
}