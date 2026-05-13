package com.reciclamais.model;

import java.time.LocalDateTime;

/**
 * Entidade Usuario — mapeada para a tabela `usuarios`.
 * Encapsulamento completo com getters e setters.
 */
public class Usuario {

    private int id;
    private String usuario;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private LocalDateTime createdAt;

    public Usuario() {}

    public Usuario(String usuario, String nome, String email, String senha, String telefone) {
        this.usuario = usuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public int getId() {
        return id; 
        }
    public void setId(int id)  {
        this.id = id; 
        }

    public String getUsuario() {
        return usuario; 
        }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
        }

    public String getNome() {
        return nome;
        }
    public void setNome(String nome) {
        this.nome = nome;
        }

    public String getEmail() {
        return email;
        }
    public void setEmail(String email) {
        this.email = email;
        }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
        }

    public String getTelefone() {
        return telefone;
        }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
        }

    public LocalDateTime getCreatedAt() {
        return createdAt;
        }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        }

    @Override
    public String toString() {
        return nome + " (@" + usuario + ")";
    }
}