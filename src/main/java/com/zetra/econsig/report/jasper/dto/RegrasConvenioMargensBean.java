package com.zetra.econsig.report.jasper.dto;

/**
 * <p> Title: RegrasConvenioMargensBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta de margens para o Relatório de Regras Convênio.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegrasConvenioMargensBean {

    private String marDescricao;
    private String marPorcentagem;
    private String exibeMargemNegativaCsa;

    public RegrasConvenioMargensBean(String marDescricao, String marPorcentagem, String exibeMargemNegativaCsa) {
        this.marDescricao = marDescricao;
        this.marPorcentagem = marPorcentagem;
        this.exibeMargemNegativaCsa = exibeMargemNegativaCsa;
    }

	public String getMarDescricao() {
		return marDescricao;
	}

	public String getMarPorcentagem() {
		return marPorcentagem;
	}

	public String getExibeMargemNegativaCsa() {
		return exibeMargemNegativaCsa;
	}

}