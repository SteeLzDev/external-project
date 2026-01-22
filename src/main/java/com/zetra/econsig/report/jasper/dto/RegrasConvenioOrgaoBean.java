package com.zetra.econsig.report.jasper.dto;

/**
 * <p> Title: RegrasConvenioOrgaoBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta de órgãos para o Relatório de Regras Convênio.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegrasConvenioOrgaoBean {

    private String nome;
    private String cnpj;
    private Long valor;

    public RegrasConvenioOrgaoBean(String nome, String cnpj, Long valor) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.valor = valor;
    }

	public String getNome() {
		return nome;
	}

	public String getCnpj() {
		return cnpj;
	}

	public Long getValor() {
		return valor;
	}

}