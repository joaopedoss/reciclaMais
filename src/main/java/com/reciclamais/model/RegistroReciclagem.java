package com.reciclamais.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade principal — Registro de reciclagem.
 * Mapeada para `registros_reciclagem`.
 */
public class RegistroReciclagem {
    /** Tipos de material aceitos */
    public enum Material {
        PLASTICO("Plástico"),
        PAPEL("Papel"),
        VIDRO("Vidro"),
        METAL("Metal"),
        ORGANICO("Orgânico"),
        ELETRONICO("Eletrônico");

        private final String label;
        Material(String label) {
            this.label = label;
            }
        public String getLabel() {
            return label;
            }

        @Override
        public String toString() {
            return label;
            }
    }

    private int id;
    private Material material;
    private BigDecimal pesoKg;
    private LocalDate dataRegistro;
    private String pontoColeta;
    private String observacoes;
    private int usuarioId;
    private String usuarioNome;   
    private LocalDateTime createdAt;

    public RegistroReciclagem() {}

    public RegistroReciclagem(Material material, BigDecimal pesoKg,
                               LocalDate dataRegistro, String pontoColeta,
                               String observacoes, int usuarioId) {
        this.material = material;
        this.pesoKg = pesoKg;
        this.dataRegistro = dataRegistro;
        this.pontoColeta = pontoColeta;
        this.observacoes = observacoes;
        this.usuarioId = usuarioId;
    }

    public int getId() {
        return id;
        }
    public void setId(int id) {
        this.id = id;
        }

    public Material getMaterial() {
        return material;
        }
    public void setMaterial(Material material) {
        this.material = material;
        }

    public BigDecimal getPesoKg() {
        return pesoKg;
        }
    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
        }

    public LocalDate getDataRegistro() {
        return dataRegistro;
        }
    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
        }

    public String getPontoColeta() {
        return pontoColeta;
        }
    public void setPontoColeta(String pontoColeta) {
        this.pontoColeta = pontoColeta;
        }

    public String getObservacoes() {
        return observacoes;
        }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
        }

    public int getUsuarioId() { 
        return usuarioId;
        }
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
        }

    public String getUsuarioNome() {
        return usuarioNome;
        }
    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
        }

    public LocalDateTime getCreatedAt() {
        return createdAt;
        }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        }
}