package com.zetra.econsig.web.controller.coeficiente;

/**
 * <p>Title: ListarServicosCoeficienteDTO</p>
 * <p>Description: DTO para listar servicoes coeficiente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Revision: 24435 $
 * $Date: 2018-05-28 08:58:59 -0300 (Seg, 28 mai 2018) $
 */
public class ListarServicosCoeficienteDTO {

	String svcCodigo;
	String svcDescricao;
	String svcIdentificador;
	String codVerba;
	String coeficienteAtivo;

	public ListarServicosCoeficienteDTO(String svcCodigo, String svcDescricao, String svcIdentificador,
			String codVerba, String coeficienteAtivo) {

		this.svcCodigo = svcCodigo;
		this.svcDescricao = svcDescricao;
		this.svcIdentificador = svcIdentificador;
		this.codVerba = codVerba;
		this.coeficienteAtivo = coeficienteAtivo;
		
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

}