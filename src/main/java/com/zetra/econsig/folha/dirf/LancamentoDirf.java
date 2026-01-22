package com.zetra.econsig.folha.dirf;

import java.util.Arrays;

/**
 * <p>Title: LancamentoDirf</p>
 * <p>Description: Classe POJO que representa um lançamento no arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LancamentoDirf {
    private String tipo;
    private Double[] valoresMensais;
    private Double valorAnual;
    private String detalhe;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double[] getValoresMensais() {
        return valoresMensais;
    }

    public void setValoresMensais(Double[] valoresMensais) {
        this.valoresMensais = valoresMensais;
    }

    public Double getValorAnual() {
        return valorAnual;
    }

    public void setValorAnual(Double valorAnual) {
        this.valorAnual = valorAnual;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public Double getValorTotal() {
        if (valoresMensais != null && valoresMensais.length > 0) {
            // Em caso de valores mensais, retorna a soma sem o valor do 13o
            return Arrays.stream(Arrays.copyOfRange(valoresMensais, 0, 12)).mapToDouble(Double::doubleValue).sum();
        } else {
            // Em caso de valor anual, retorna o próprio valor
            return valorAnual;
        }
    }

    public Double getValorDecimoTerceiro() {
        if (valoresMensais != null && valoresMensais.length > 12 && valoresMensais[12] != null) {
            return valoresMensais[12];
        }
        return Double.valueOf(0);
    }
}