package com.zetra.econsig.report.jasper.dto;

/**
 * <p> Title: RegrasConvenioParametrosBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta de parâmetros para o Relatório de Regras Convênio.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegrasConvenioParametrosBean {

	private String codigo;
    private String chave;
    private String valor;
    private String svcCodigo;
    private String orgCodigo;
    private Short marCodigo;
    
    public RegrasConvenioParametrosBean(String codigo, String chave, String valor) {
		this.codigo = codigo;
		this.chave = chave;
		this.valor = valor;
	}
    
    public RegrasConvenioParametrosBean(String codigo, String chave, String valor, String orgCodigo) {
		this.codigo = codigo;
		this.chave = chave;
		this.valor = valor;
		this.orgCodigo = orgCodigo;
	}    
    
    public RegrasConvenioParametrosBean(String codigo, String chave, String valor, Short marCodigo) {
		this.codigo = codigo;
		this.chave = chave;
		this.valor = valor;
		this.marCodigo = marCodigo;
	}

	public String getCodigo() {
        return codigo;
    }

    public String getChave() {
        return chave;
    }

    public String getValor() {
        return valor;
    }

	public String getSvcCodigo() {
		return svcCodigo;
	}

	public void setSvcCodigo(String svcCodigo) {
		this.svcCodigo = svcCodigo;
	}

	public String getOrgCodigo() {
		return orgCodigo;
	}

	public Short getMarCodigo() {
		return marCodigo;
	}
}