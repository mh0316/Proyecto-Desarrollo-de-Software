package com.example.appmunicipal.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class RutUtil {

    // Expresión regular para validar formato de RUT
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}[-]?[0-9kK]$");

    /**
     * Validar si el RUT tiene un formato válido
     *
     * @param rut RUT a validar (puede venir con o sin formato)
     * @return true si el formato es válido
     */
    public boolean validarFormato(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        String rutLimpio = rut.trim();

        // Validar formato básico
        if (!RUT_PATTERN.matcher(rutLimpio).matches()) {
            log.warn("❌ RUT con formato inválido: {}", rut);
            return false;
        }

        return true;
    }

    /**
     * Limpiar RUT removiendo puntos y guiones
     * Ejemplo: "12.345.678-9" -> "123456789"
     *
     * @param rut RUT con formato
     * @return RUT sin formato
     */
    public String limpiarRut(String rut) {
        if (rut == null) {
            return null;
        }
        return rut.replaceAll("[.\\-]", "").toUpperCase();
    }

    /**
     * Formatear RUT al formato estándar chileno
     * Ejemplo: "123456789" -> "12.345.678-9"
     *
     * @param rut RUT sin formato
     * @return RUT formateado
     */
    public String formatearRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return null;
        }

        String rutLimpio = limpiarRut(rut);

        if (rutLimpio.length() < 2) {
            return rut;
        }

        String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
        String dv = rutLimpio.substring(rutLimpio.length() - 1);

        // Agregar puntos cada 3 dígitos de derecha a izquierda
        StringBuilder numeroFormateado = new StringBuilder(numero);
        int length = numeroFormateado.length();

        if (length > 3) {
            numeroFormateado.insert(length - 3, ".");
        }
        if (length > 6) {
            numeroFormateado.insert(length - 6, ".");
        }

        return numeroFormateado.toString() + "-" + dv;
    }

    /**
     * Validar dígito verificador del RUT
     * Algoritmo oficial chileno (Módulo 11)
     *
     * @param rut RUT completo (con o sin formato)
     * @return true si el dígito verificador es correcto
     */
    public boolean validarDigitoVerificador(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        try {
            String rutLimpio = limpiarRut(rut);

            if (rutLimpio.length() < 2) {
                return false;
            }

            String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
            String dvIngresado = rutLimpio.substring(rutLimpio.length() - 1);

            String dvCalculado = calcularDigitoVerificador(numero);

            boolean valido = dvIngresado.equalsIgnoreCase(dvCalculado);

            if (!valido) {
                log.warn("❌ RUT con dígito verificador inválido: {} (esperado: {}, recibido: {})",
                        rut, dvCalculado, dvIngresado);
            }

            return valido;

        } catch (Exception e) {
            log.error("Error al validar dígito verificador del RUT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Calcular dígito verificador según algoritmo chileno (Módulo 11)
     *
     * @param numeroRut Número del RUT sin dígito verificador
     * @return Dígito verificador calculado (0-9 o K)
     */
    public String calcularDigitoVerificador(String numeroRut) {
        if (numeroRut == null || numeroRut.isEmpty()) {
            throw new IllegalArgumentException("Número de RUT no puede estar vacío");
        }

        int suma = 0;
        int multiplicador = 2;

        // Recorrer el RUT de derecha a izquierda
        for (int i = numeroRut.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(numeroRut.charAt(i)) * multiplicador;
            multiplicador++;

            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resto = suma % 11;
        int dv = 11 - resto;

        if (dv == 11) {
            return "0";
        } else if (dv == 10) {
            return "K";
        } else {
            return String.valueOf(dv);
        }
    }

    /**
     * Validación completa del RUT (formato + dígito verificador)
     *
     * @param rut RUT a validar
     * @return true si el RUT es válido
     */
    public boolean validarRut(String rut) {
        return validarFormato(rut) && validarDigitoVerificador(rut);
    }

    /**
     * Extraer solo el número del RUT (sin dígito verificador)
     *
     * @param rut RUT completo
     * @return Número del RUT
     */
    public String extraerNumero(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return null;
        }

        String rutLimpio = limpiarRut(rut);
        return rutLimpio.substring(0, rutLimpio.length() - 1);
    }

    /**
     * Extraer solo el dígito verificador del RUT
     *
     * @param rut RUT completo
     * @return Dígito verificador
     */
    public String extraerDigitoVerificador(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return null;
        }

        String rutLimpio = limpiarRut(rut);
        return rutLimpio.substring(rutLimpio.length() - 1);
    }

    /**
     * Normalizar RUT para almacenamiento
     * Guarda en formato sin puntos: "12345678-9"
     *
     * @param rut RUT en cualquier formato
     * @return RUT normalizado
     */
    public String normalizarRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return null;
        }

        String rutLimpio = limpiarRut(rut);

        if (rutLimpio.length() < 2) {
            return rut;
        }

        String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
        String dv = rutLimpio.substring(rutLimpio.length() - 1);

        return numero + "-" + dv;
    }
}