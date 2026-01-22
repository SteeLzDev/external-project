package com.zetra.econsig.dto.web;

/**
 * <p>Title: ListarServicosDTO</p>
 * <p>Description: Classe para trasferir dados para a camada de visão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Date: 2018-05-16 14:30:44 -0300 (Qua, 16 Mai 2018) $
 */
public class ListarServicosDTO {

	String svcCodigo;
	String svcDescricao;
	String svcIdentificador;
	String codVerba;
	String coeficienteAtivo;
	String icone;
	String msgTaxaJuros;
	boolean isSvcSemPrazoConvenioCsa = false;

	public ListarServicosDTO(String svcCodigo, String svcDescricao, String svcIdentificador, String codVerba,
			String coeficienteAtivo, String icone, String msgTaxaJuros) {

		this.svcCodigo = svcCodigo;
		this.svcDescricao = svcDescricao;
		this.svcIdentificador = svcIdentificador;
		this.codVerba = codVerba;
		this.coeficienteAtivo = coeficienteAtivo;
		this.icone = icone;
		this.msgTaxaJuros = msgTaxaJuros;

	}

	public String getSvcCodigo() {
		return svcCodigo;
	}

	public void setSvcCodigo(String svcCodigo) {
		this.svcCodigo = svcCodigo;
	}

	public String getSvcDescricao() {
		return svcDescricao;
	}

	public void setSvcDescricao(String svcDescricao) {
		this.svcDescricao = svcDescricao;
	}

	public String getSvcIdentificador() {
		return svcIdentificador;
	}

	public void setSvcIdentificador(String svcIdentificador) {
		this.svcIdentificador = svcIdentificador;
	}

	public String getCodVerba() {
		return codVerba;
	}

	public void setCodVerba(String codVerba) {
		this.codVerba = codVerba;
	}

	public String getCoeficienteAtivo() {
		return coeficienteAtivo;
	}

	public void setCoeficienteAtivo(String coeficienteAtivo) {
		this.coeficienteAtivo = coeficienteAtivo;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public String getMsgTaxaJuros() {
		return msgTaxaJuros;
	}

	public void setMsgTaxaJuros(String msgTaxaJuros) {
		this.msgTaxaJuros = msgTaxaJuros;
	}

    public boolean getIsSvcSemPrazoConvenioCsa() {
        return isSvcSemPrazoConvenioCsa;
    }

    public void setIsSvcSemPrazoConvenioCsa(boolean isSvcSemPrazoConvenioCsa) {
        this.isSvcSemPrazoConvenioCsa = isSvcSemPrazoConvenioCsa;
    }
}