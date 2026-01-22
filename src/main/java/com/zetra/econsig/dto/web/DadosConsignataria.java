package com.zetra.econsig.dto.web;

/**
 * <p>Title: DadosConsignataria</p>
 * <p>Description: DTO para o caso de uso Manter Consignataria.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosConsignataria {

    private final String csaCodigo;
    private final String csaNome;
    private final String csaNomeAbrev;
    private final String csaIdentificador;
    private final String csaCnpj;
    private final String csaAtivo;
    private final String csaStatusDescricao;
    private final String csaNomeAbrevScript;
    private final String mensagemDesbloqueio;

    public DadosConsignataria(String csaCodigo, String csaNome, String csaNomeAbrev, String csaIdentificador, String csaCnpj, String csaAtivo, String csaStatusDescricao, String csaNomeAbrevScript, String mensagemDesbloqueio) {
        this.csaCodigo = csaCodigo;
        this.csaNome = csaNome;
        this.csaNomeAbrev = csaNomeAbrev;
        this.csaIdentificador = csaIdentificador;
        this.csaCnpj = csaCnpj;
        this.csaAtivo = csaAtivo;
        this.csaStatusDescricao = csaStatusDescricao;
        this.csaNomeAbrevScript = csaNomeAbrevScript;
        this.mensagemDesbloqueio = mensagemDesbloqueio;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public String getCsaCnpj() {
        return csaCnpj;
    }

    public String getCsaAtivo() {
        return csaAtivo;
    }

    public String getCsaStatusDescricao() {
        return csaStatusDescricao;
    }

    public String getCsaNomeAbrevScript() {
        return csaNomeAbrevScript;
    }

    public String getMensagemDesbloqueio() {
        return mensagemDesbloqueio;
    }
}
