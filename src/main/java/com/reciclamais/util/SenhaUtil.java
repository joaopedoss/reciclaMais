package com.reciclamais.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitário de hash de senha com SHA-256.
 */
public class SenhaUtil {

    private SenhaUtil() {} // impede instanciação

    /**
     * Gera hash SHA-256 da senha em texto puro
     * @param senhaTexto senha sem hash
     * @return string hexadecimal de 64 caracteres
     */
    public static String hash(String senhaTexto) {
        try {
            MessageDigest md     = MessageDigest.getInstance("SHA-256");
            byte[]        digest = md.digest(senhaTexto.getBytes());
            BigInteger    numero = new BigInteger(1, digest);
            StringBuilder sb     = new StringBuilder(numero.toString(16));

            while (sb.length() < 64) sb.insert(0, '0');
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 não disponível.", e);
        }
    }

    /**
     * Verifica se a senha digitada corresponde ao hash armazenado.
     */
    public static boolean verificar(String senhaTexto, String hashArmazenado) {
        return hash(senhaTexto).equals(hashArmazenado);
    }
}