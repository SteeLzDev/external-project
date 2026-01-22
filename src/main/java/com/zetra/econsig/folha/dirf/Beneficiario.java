package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: Beneficiario</p>
 * <p>Description: Classe POJO que representa um beneficiário no arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Beneficiario {
    private String beneficiarioCpf;
    private String beneficiarioNome;
    private String beneficiarioDataMolestiaGrave;
    private String beneficiarioIdentificacaoAlimentando;
    private String beneficiarioIdentificacaoPrevidenciaComplementar;

    public Beneficiario() {
        lancamentosComuns = new ArrayList<LancamentoDirf>();
        lancamentosPrevidenciaComplementar = new ArrayList<LancamentoPrevidenciaComplementar>();
        lancamentosPensaoAlimenticia = new ArrayList<LancamentoPensaoAlimenticia>();
    }

    // 1-N : RTRT, RTPO, RTDP ...
    private final List<LancamentoDirf> lancamentosComuns;

    // 1-N : INFPC
    private final List<LancamentoPrevidenciaComplementar> lancamentosPrevidenciaComplementar;

    // 1-N : INFPA
    private final List<LancamentoPensaoAlimenticia> lancamentosPensaoAlimenticia;

    public String getBeneficiarioCpf() {
        return beneficiarioCpf;
    }

    public void setBeneficiarioCpf(String beneficiarioCpf) {
        this.beneficiarioCpf = beneficiarioCpf;
    }

    public String getBeneficiarioNome() {
        return beneficiarioNome;
    }

    public void setBeneficiarioNome(String beneficiarioNome) {
        this.beneficiarioNome = beneficiarioNome;
    }

    public String getBeneficiarioDataMolestiaGrave() {
        return beneficiarioDataMolestiaGrave;
    }

    public void setBeneficiarioDataMolestiaGrave(String beneficiarioDataMolestiaGrave) {
        this.beneficiarioDataMolestiaGrave = beneficiarioDataMolestiaGrave;
    }

    public String getBeneficiarioIdentificacaoAlimentando() {
        return beneficiarioIdentificacaoAlimentando;
    }

    public void setBeneficiarioIdentificacaoAlimentando(String beneficiarioIdentificacaoAlimentando) {
        this.beneficiarioIdentificacaoAlimentando = beneficiarioIdentificacaoAlimentando;
    }

    public String getBeneficiarioIdentificacaoPrevidenciaComplementar() {
        return beneficiarioIdentificacaoPrevidenciaComplementar;
    }

    public void setBeneficiarioIdentificacaoPrevidenciaComplementar(String beneficiarioIdentificacaoPrevidenciaComplementar) {
        this.beneficiarioIdentificacaoPrevidenciaComplementar = beneficiarioIdentificacaoPrevidenciaComplementar;
    }

    public List<LancamentoDirf> getLancamentosComuns() {
        return Collections.unmodifiableList(lancamentosComuns);
    }

    public void addLancamentoComun(LancamentoDirf lancamentoComun) {
        lancamentosComuns.add(lancamentoComun);
    }

    public List<LancamentoPrevidenciaComplementar> getLancamentosPrevidenciaComplementar() {
        return Collections.unmodifiableList(lancamentosPrevidenciaComplementar);
    }

    public void addLancamentoPrevidenciaComplementar(LancamentoPrevidenciaComplementar lancamentoPrevidenciaComplementar) {
        lancamentosPrevidenciaComplementar.add(lancamentoPrevidenciaComplementar);
    }

    public List<LancamentoPensaoAlimenticia> getLancamentosPensaoAlimenticia() {
        return Collections.unmodifiableList(lancamentosPensaoAlimenticia);
    }

    public void addLancamentoPensaoAlimenticia(LancamentoPensaoAlimenticia lancamentoPensaoAlimenticia) {
        lancamentosPensaoAlimenticia.add(lancamentoPensaoAlimenticia);
    }
}