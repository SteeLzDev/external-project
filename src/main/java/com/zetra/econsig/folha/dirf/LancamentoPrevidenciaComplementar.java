package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: LancamentoPrevidenciaComplementar</p>
 * <p>Description: Classe POJO que representa um lançamento de previdência complementar no arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LancamentoPrevidenciaComplementar {
    private String cnpj;
    private String nome;

    // 1-N : RTPP, RTFA, ESPP, ESFA
    private List<LancamentoDirf> lancamentosComuns;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLancamentosComuns(List<LancamentoDirf> lancamentosComuns) {
        this.lancamentosComuns = lancamentosComuns;
    }

    public List<LancamentoDirf> getLancamentosComuns() {
        return Collections.unmodifiableList(lancamentosComuns);
    }

    public void addLancamentoComun(LancamentoDirf lancamentoComun) {
        if (lancamentosComuns == null) {
            lancamentosComuns = new ArrayList<LancamentoDirf>();
        }
        lancamentosComuns.add(lancamentoComun);
    }
}