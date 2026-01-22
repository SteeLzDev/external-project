package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: DeclarantePJ</p>
 * <p>Description: Classe POJO que representa a pessoa jurídica declarante do arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DeclarantePJ {
    // 1-N : IDREC
    private final List<Declaracao> declaracoes;

    private String declaranteCNPJ;
    private String declaranteNome;
    private String declaranteNatureza;
    private String declaranteCPFResponsavel;
    private String declaranteSocioOstensivo;
    private String declaranteDepositarioCreditoJudicial;
    private String declaranteAdministradoraFundoInvestimento;
    private String declaranteRendimentosPagosResidentesExterior;
    private String declarantePlanoPrivadoAssistenciaSaude;
    private String declaranteEntidadeUniaoMajoritario;
    private String declaranteFundacaoPublica;
    private String situacaoEspecial;
    private Date dataEvento;

    public DeclarantePJ() {
        declaracoes = new ArrayList<Declaracao>();
    }

    public List<Declaracao> getDeclaracoes() {
        return Collections.unmodifiableList(declaracoes);
    }

    public void addDeclaracao(Declaracao declaracao) {
        declaracoes.add(declaracao);
    }

    public String getDeclaranteCNPJ() {
        return declaranteCNPJ;
    }

    public void setDeclaranteCNPJ(String declaranteCNPJ) {
        this.declaranteCNPJ = declaranteCNPJ;
    }

    public String getDeclaranteNome() {
        return declaranteNome;
    }

    public void setDeclaranteNome(String declaranteNome) {
        this.declaranteNome = declaranteNome;
    }

    public String getDeclaranteNatureza() {
        return declaranteNatureza;
    }

    public void setDeclaranteNatureza(String declaranteNatureza) {
        this.declaranteNatureza = declaranteNatureza;
    }

    public String getDeclaranteCPFResponsavel() {
        return declaranteCPFResponsavel;
    }

    public void setDeclaranteCPFResponsavel(String declaranteCPFResponsavel) {
        this.declaranteCPFResponsavel = declaranteCPFResponsavel;
    }

    public String getDeclaranteSocioOstensivo() {
        return declaranteSocioOstensivo;
    }

    public void setDeclaranteSocioOstensivo(String declaranteSocioOstensivo) {
        this.declaranteSocioOstensivo = declaranteSocioOstensivo;
    }

    public String getDeclaranteDepositarioCreditoJudicial() {
        return declaranteDepositarioCreditoJudicial;
    }

    public void setDeclaranteDepositarioCreditoJudicial(String declaranteDepositarioCreditoJudicial) {
        this.declaranteDepositarioCreditoJudicial = declaranteDepositarioCreditoJudicial;
    }

    public String getDeclaranteAdministradoraFundoInvestimento() {
        return declaranteAdministradoraFundoInvestimento;
    }

    public void setDeclaranteAdministradoraFundoInvestimento(String declaranteAdministradoraFundoInvestimento) {
        this.declaranteAdministradoraFundoInvestimento = declaranteAdministradoraFundoInvestimento;
    }

    public String getDeclaranteRendimentosPagosResidentesExterior() {
        return declaranteRendimentosPagosResidentesExterior;
    }

    public void setDeclaranteRendimentosPagosResidentesExterior(String declaranteRendimentosPagosResidentesExterior) {
        this.declaranteRendimentosPagosResidentesExterior = declaranteRendimentosPagosResidentesExterior;
    }

    public String getDeclarantePlanoPrivadoAssistenciaSaude() {
        return declarantePlanoPrivadoAssistenciaSaude;
    }

    public void setDeclarantePlanoPrivadoAssistenciaSaude(String declarantePlanoPrivadoAssistenciaSaude) {
        this.declarantePlanoPrivadoAssistenciaSaude = declarantePlanoPrivadoAssistenciaSaude;
    }

    public String getDeclaranteEntidadeUniaoMajoritario() {
        return declaranteEntidadeUniaoMajoritario;
    }

    public void setDeclaranteEntidadeUniaoMajoritario(String declaranteEntidadeUniaoMajoritario) {
        this.declaranteEntidadeUniaoMajoritario = declaranteEntidadeUniaoMajoritario;
    }

    public String getDeclaranteFundacaoPublica() {
        return declaranteFundacaoPublica;
    }

    public void setDeclaranteFundacaoPublica(String declaranteFundacaoPublica) {
        this.declaranteFundacaoPublica = declaranteFundacaoPublica;
    }

    public String getSituacaoEspecial() {
        return situacaoEspecial;
    }

    public void setSituacaoEspecial(String situacaoEspecial) {
        this.situacaoEspecial = situacaoEspecial;
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }
}