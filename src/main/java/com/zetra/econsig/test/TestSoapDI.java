package com.zetra.econsig.test;

public class TestSoapDI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String estabelecimento = "AT";
        String orgao = "001";
        String matricula = "123456019";
        String senha = "00RT5%#332";
        //MMMMMM VV D
        // matricula, digito, vinculo, parametros[3], parametros[2]
        // <MATRICULA(0,-3)>; <MATRICULA(-1)>; <MATRICULA(-3,-1)>; <ORGAO>; <SENHA>
        // Valores possíveis: <ESTABELECIMENTO>; <ORGAO>; <SENHA>; <MATRICULA>; <MATRICULA(x)>; <MATRICULA(x,y)> | Separador = ";"
        String wsdlMethodParamsConf = " 1; <MATRICULA>; <MATRICULA(0,-3)>; <MATRICULA(-3,-1)>; <MATRICULA(-1)>";
        String[] params = wsdlMethodParamsConf.split(";");
        for (int i = 0; i < params.length; i++) {
            if (params[i].trim().equalsIgnoreCase("<ESTABELECIMENTO>")) {
                params[i] = estabelecimento;
            } else if (params[i].trim().equalsIgnoreCase("<ORGAO>")) {
                params[i] = orgao;
            } else if (params[i].trim().equalsIgnoreCase("<SENHA>")) {
                params[i] = senha;
            } else if (params[i].trim().equalsIgnoreCase("<MATRICULA>")) {
                params[i] = matricula;
            } else if (params[i].trim().startsWith("<MATRICULA(")) {
                int indiceIni = 0;
                int indiceFim = matricula.length();
                if (params[i].indexOf(',') != -1) {
                    indiceIni = Integer.parseInt(params[i].substring(params[i].indexOf('(')+1, params[i].indexOf(',')).trim());
                    indiceFim = Integer.parseInt(params[i].substring(params[i].indexOf(',')+1, params[i].indexOf(')')).trim());
                } else {
                    indiceIni = Integer.parseInt(params[i].substring(params[i].indexOf('(')+1, params[i].indexOf(')')).trim());
                }
                if (indiceIni < 0) {
                    indiceIni = matricula.length() + indiceIni;
                }
                if (indiceFim < 0) {
                    indiceFim = matricula.length() + indiceFim;
                }
                if (indiceFim < indiceIni || indiceIni < 0 || indiceFim > matricula.length()) {
                    // ERRO DE CONFIGURAÇÃO!!
                } else {
                    params[i] = matricula.substring(indiceIni, indiceFim);
                }
            } else {
                params[i] = params[i].trim();
            }
        }
        
        for (int i = 0; i < params.length; i++) {
            System.out.println("[" + params[i] + "]");
        }
    }
}
