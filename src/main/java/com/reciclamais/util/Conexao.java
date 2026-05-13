package com.reciclamais.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexao {

    // ── Configurações ──────────
    private static final String URL      = "jdbc:mysql://localhost:3306/reciclamais"
                                         + "?useSSL=false&serverTimezone=America/Sao_Paulo"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String SENHA    = "root";

    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado. Adicione o JAR ao classpath.", e);
        }
    }

    /**
     * Retorna uma nova conexão com o banco.
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}