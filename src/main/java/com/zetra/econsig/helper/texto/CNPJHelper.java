package com.zetra.econsig.helper.texto;

public class CNPJHelper {

    public static String getCnpjSemMascara(String csaCnpj) {
        // Remove formatação do cnpj, somente numérico
        return !TextHelper.isNull(csaCnpj) ? csaCnpj.replaceAll("[^a-zA-Z0-9]", "") : "";
    }

}