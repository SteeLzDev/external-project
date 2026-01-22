package com.zetra.econsig.dto.web;

public class VisualizarHistoricoDTO {

	private String hmrData;
	private String marDescricao;
	private String descricao;
	private String adeNumero;
	private String labelTipoVlr;
	private String adeVlr;
	private String hmrMargemAntes;
	private String hmrMargemDepois;
	private String adeCodigo;
	private String hmrOperacao;
	private String rseCodigo;

	public VisualizarHistoricoDTO() {
	}

	public VisualizarHistoricoDTO(String hmrData, String marDescricao, String descricao, String adeNumero,
			String labelTipoVlr, String adeVlr, String hmrMargemAntes, String hmrMargemDepois, String adeCodigo,
			String hmrOperacao, String rseCodigo) {

		this.hmrData = hmrData;
		this.marDescricao = marDescricao;
		this.descricao = descricao;
		this.adeNumero = adeNumero;
		this.labelTipoVlr = labelTipoVlr;
		this.adeVlr = adeVlr;
		this.hmrMargemAntes = hmrMargemAntes;
		this.hmrMargemDepois = hmrMargemDepois;
		this.adeCodigo = adeCodigo;
		this.hmrOperacao = hmrOperacao;
		this.rseCodigo = rseCodigo;

	}

	public String getHmrData() {
		return hmrData;
	}

	public void setHmrData(String hmrData) {
		this.hmrData = hmrData;
	}

	public String getMarDescricao() {
		return marDescricao;
	}

	public void setMarDescricao(String marDescricao) {
		this.marDescricao = marDescricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getAdeNumero() {
		return adeNumero;
	}

	public void setAdeNumero(String adeNumero) {
		this.adeNumero = adeNumero;
	}

	public String getLabelTipoVlr() {
		return labelTipoVlr;
	}

	public void setLabelTipoVlr(String labelTipoVlr) {
		this.labelTipoVlr = labelTipoVlr;
	}

	public String getAdeVlr() {
		return adeVlr;
	}

	public void setAdeVlr(String adeVlr) {
		this.adeVlr = adeVlr;
	}

	public String getHmrMargemAntes() {
		return hmrMargemAntes;
	}

	public void setHmrMargemAntes(String hmrMargemAntes) {
		this.hmrMargemAntes = hmrMargemAntes;
	}

	public String getHmrMargemDepois() {
		return hmrMargemDepois;
	}

	public void setHmrMargemDepois(String hmrMargemDepois) {
		this.hmrMargemDepois = hmrMargemDepois;
	}

	public String getAdeCodigo() {
		return adeCodigo;
	}

	public void setAdeCodigo(String adeCodigo) {
		this.adeCodigo = adeCodigo;
	}

	public String getHmrOperacao() {
		return hmrOperacao;
	}

	public void setHmrOperacao(String hmrOperacao) {
		this.hmrOperacao = hmrOperacao;
	}

	public String getRseCodigo() {
		return rseCodigo;
	}

	public void setRseCodigo(String rseCodigo) {
		this.rseCodigo = rseCodigo;
	}

}