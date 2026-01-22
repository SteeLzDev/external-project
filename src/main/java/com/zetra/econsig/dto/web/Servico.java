package com.zetra.econsig.dto.web;

/**
 * <p>Title: Servico</p>
 * <p>Description: POJO para serviços na listagem dinâmica.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Servico {
    private String svcCodigo;

    private String svcIdentificador;

    private String svcDescricao;

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getSvcIdentificador() {
        return svcIdentificador;
    }

    public void setSvcIdentificador(String svcIdentificador) {
        this.svcIdentificador = svcIdentificador;
    }

    public String getSvcDescricao() {
        return svcDescricao;
    }

    public void setSvcDescricao(String svcDescricao) {
        this.svcDescricao = svcDescricao;
    }
}
