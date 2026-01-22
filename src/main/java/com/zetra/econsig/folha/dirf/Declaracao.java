package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: Declaracao</p>
 * <p>Description: Classe POJO que representa o conteúdo de declarações do arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Declaracao {
    private String codigoReceita;

    // 1-N : BPFDEC
    private final List<Beneficiario> beneficiarios;

    public Declaracao() {
        beneficiarios = new ArrayList<Beneficiario>();
    }

    public String getCodigoReceita() {
        return codigoReceita;
    }

    public void setCodigoReceita(String codigoReceita) {
        this.codigoReceita = codigoReceita;
    }

    public List<Beneficiario> getBeneficiarios() {
        return Collections.unmodifiableList(beneficiarios);
    }

    public void addBeneficiario(Beneficiario beneficiario) {
        beneficiarios.add(beneficiario);
    }
}
