package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: LancamentoPensaoAlimenticia</p>
 * <p>Description: Classe POJO que representa um lançamento de pensão alimentícia no arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LancamentoPensaoAlimenticia {
    private String alimentandoCpf;
    private String alimentandoDataNascimento;
    private String alimentandoNome;
    private String relacaoDependencia;

    // 1-N : RTPA, ESPA
    private List<LancamentoDirf> lancamentosComuns;

    public String getAlimentandoCpf() {
        return alimentandoCpf;
    }

    public void setAlimentandoCpf(String alimentandoCpf) {
        this.alimentandoCpf = alimentandoCpf;
    }

    public String getAlimentandoDataNascimento() {
        return alimentandoDataNascimento;
    }

    public void setAlimentandoDataNascimento(String alimentandoDataNascimento) {
        this.alimentandoDataNascimento = alimentandoDataNascimento;
    }

    public String getAlimentandoNome() {
        return alimentandoNome;
    }

    public void setAlimentandoNome(String alimentandoNome) {
        this.alimentandoNome = alimentandoNome;
    }

    public String getRelacaoDependencia() {
        return relacaoDependencia;
    }

    public void setRelacaoDependencia(String relacaoDependencia) {
        this.relacaoDependencia = relacaoDependencia;
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