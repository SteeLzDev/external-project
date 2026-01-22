package com.zetra.econsig.values;

/**
 * <p>Title: CamposRelatorioSinteticoEnum</p>
 * <p>Description: Enumeração de campos dos relatórios sintético de consignações, de descontos (spd) e ocorrencia de consignação.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum CamposRelatorioSinteticoEnum {

    CAMPO_CONSIGNATARIA_ABREV("2", Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev"),
    CAMPO_CONSIGNATARIA("10", Columns.CSA_NOME, "csa.csaNome"),
    CAMPO_CORRESPONDENTE("1", Columns.COR_NOME, "cor.corNome"),
    CAMPO_ESTABELECIMENTO("9", Columns.EST_NOME, "est.estNome"),
    CAMPO_ORGAO("4", Columns.ORG_NOME, "org.orgNome"),
    CAMPO_NATUREZA_SERVICO("23", Columns.NSE_DESCRICAO, "svc.naturezaServico.nseDescricao"),
    CAMPO_SERVICO("7", Columns.SVC_DESCRICAO, "svc.svcDescricao"),
    CAMPO_VERBA("22", Columns.CNV_COD_VERBA, "cnv.cnvCodVerba"),
    CAMPO_STATUS("3", Columns.SAD_DESCRICAO, "sad.sadDescricao"),
    CAMPO_DATA("6", Columns.ADE_DATA, "to_locale_date(ade.adeData)"),
    CAMPO_DATA_INI("5", Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni)"),
    CAMPO_DATA_FIM("8", Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim)"),
    CAMPO_DATA_DECISAO_JUDICIAL("16", Columns.DJU_DATA, "to_locale_date(dju.djuData)"),
    CAMPO_TIPO_JUSTICA("17", Columns.TJU_DESCRICAO, "tju.tjuDescricao"),
    CAMPO_COMARCA_DECISAO_JUDICIAL("18", Columns.CID_NOME, "cid.cidNome"),
    CAMPO_DATA_OCORRENCIA("19", Columns.OCA_DATA, "to_locale_date(oca.ocaData)"),
    CAMPO_TIPO_OCORRENCIA("20", Columns.TOC_DESCRICAO, "toc.tocDescricao"),
    CAMPO_TIPO_MOTIVO_OPERACAO("21", Columns.TMO_DESCRICAO, "tmo.tmoDescricao"),
    CAMPO_CONSIGNATARIA_NOME("15", "CSA_NOME", "CSA_NOME"),
    CAMPO_CONTRATOS("CONTRATOS", "CONTRATOS", "COUNT(*)"),
    CAMPO_PRESTACAO("PRESTACAO", "PRESTACAO", "SUM(ade.adeVlr * COALESCE(ade.adePrazo, 1))"),
    CAMPO_VALOR("VALOR", "VALOR", "SUM(ade.adeVlr)"),
    CAMPO_CAPITAL_DEVIDO("CAPITAL_DEVIDO", "CAPITAL_DEVIDO", "SUM(ade.adeVlr * COALESCE(ade.adePrazo - coalesce(ade.adePrdPagas,0), 1))"),
    CAMPO_MEDIA_QTD_PARCELAS("MEDIA_QTD_PARCELAS", "MEDIA_QTD_PARCELAS", "AVG(COALESCE(ade.adePrazo, 1))"),
    CAMPO_VALOR_MEDIO_PARCELAS("VALOR_MEDIO_PARCELAS", "VALOR_MEDIO_PARCELAS", "AVG(ade.adeVlr)"),
    CAMPO_MEDIA_QNTD_PARCELAS_PAGAS("MEDIA_QNTD_PARCELAS_PAGAS", "MEDIA_QNTD_PARCELAS_PAGAS", "AVG(ade.adePrdPagas)")
    ;

    private String codigo;
    private String campo;
    private String campoOrderBy;

    private CamposRelatorioSinteticoEnum(String codigo, String campo, String campoOrderBy) {
        this.codigo = codigo;
        this.campo = campo;
        this.campoOrderBy = campoOrderBy;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCampo() {
        return campo;
    }

    public String getCampoOrderBy() {
        return campoOrderBy;
    }

    /**
     * Recupera o campo de acordo com o código passado.
     * @param codigo Código do campo que deve ser recuperado.
     * @return Retorna um campo que será usado no relatório
     * @throws IllegalArgumentException Caso o código do campo seja inválido
     */
    public static CamposRelatorioSinteticoEnum recuperaCampo(String codigo) {
        CamposRelatorioSinteticoEnum campo = null;

        for (CamposRelatorioSinteticoEnum bean : CamposRelatorioSinteticoEnum.values()) {
            if (bean.getCodigo().equals(codigo)) {
                campo = bean;
                break;
            }
        }

        return campo;
    }

    public final boolean equals(CamposRelatorioSinteticoEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
