package com.reciclamais.dao;

import com.reciclamais.model.RegistroReciclagem;
import com.reciclamais.model.RegistroReciclagem.Material;
import com.reciclamais.util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de RegistroReciclagem — tabela `registros_reciclagem`.
 */
public class RegistroReciclagemDAO {

    public void inserir(RegistroReciclagem r) throws SQLException {
        String sql = """
            INSERT INTO registros_reciclagem
                (material, peso_kg, data_registro, ponto_coleta, observacoes, usuario_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getMaterial().name());
            ps.setBigDecimal(2, r.getPesoKg());
            ps.setDate(3, Date.valueOf(r.getDataRegistro()));
            ps.setString(4, r.getPontoColeta());
            ps.setString(5, r.getObservacoes());
            ps.setInt(6, r.getUsuarioId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
    }

    public List<RegistroReciclagem> listarTodos() throws SQLException {
        String sql = """
            SELECT r.*, u.nome AS usuario_nome
            FROM registros_reciclagem r
            JOIN usuarios u ON r.usuario_id = u.id
            ORDER BY r.data_registro DESC
            """;

        List<RegistroReciclagem> lista = new ArrayList<>();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<RegistroReciclagem> listarPorUsuario(int usuarioId) throws SQLException {
        String sql = """
            SELECT r.*, u.nome AS usuario_nome
            FROM registros_reciclagem r
            JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.usuario_id = ?
            ORDER BY r.data_registro DESC
            """;

        List<RegistroReciclagem> lista = new ArrayList<>();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ─ READ — por ID 
    public Optional<RegistroReciclagem> buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT r.*, u.nome AS usuario_nome
            FROM registros_reciclagem r
            JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.id = ?
            """;

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // ─ UPDATE 
    public void atualizar(RegistroReciclagem r) throws SQLException {
        String sql = """
            UPDATE registros_reciclagem
            SET material=?, peso_kg=?, data_registro=?, ponto_coleta=?, observacoes=?
            WHERE id=?
            """;

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getMaterial().name());
            ps.setBigDecimal(2, r.getPesoKg());
            ps.setDate(3, Date.valueOf(r.getDataRegistro()));
            ps.setString(4, r.getPontoColeta());
            ps.setString(5, r.getObservacoes());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
        }
    }


    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM registros_reciclagem WHERE id=?";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public BigDecimal totalPesoKg() throws SQLException {
        String sql = "SELECT COALESCE(SUM(peso_kg), 0) FROM registros_reciclagem";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    public int totalRegistros() throws SQLException {
        String sql = "SELECT COUNT(*) FROM registros_reciclagem";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private RegistroReciclagem mapear(ResultSet rs) throws SQLException {
        RegistroReciclagem r = new RegistroReciclagem();
        r.setId(rs.getInt("id"));
        r.setMaterial(Material.valueOf(rs.getString("material")));
        r.setPesoKg(rs.getBigDecimal("peso_kg"));
        r.setDataRegistro(rs.getDate("data_registro").toLocalDate());
        r.setPontoColeta(rs.getString("ponto_coleta"));
        r.setObservacoes(rs.getString("observacoes"));
        r.setUsuarioId(rs.getInt("usuario_id"));
        r.setUsuarioNome(rs.getString("usuario_nome"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());

        return r;
    }
}