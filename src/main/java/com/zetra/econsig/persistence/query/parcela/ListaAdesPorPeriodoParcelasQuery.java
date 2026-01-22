package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAdesPorPeriodoParcelasQuery</p>
 * <p>Description: Classe da query que busca o resultado para relatorio de parcelas processadas e futuras
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListaAdesPorPeriodoParcelasQuery extends HQuery{
    public String tipoEntidade;
    public String dataIni;
    public String dataFim;
    public String rseMatricula;
    public String serCpf;
    public List<String> orgCodigos;
    public String estCodigo;
    public String csaCodigo;
    public List<String> corCodigos;
    public String sboCodigo;
    public String uniCodigo;
    public List<String> svcCodigo;
    public List<String> sadCodigos;
    public List<String> origemAdes;
    public List<String> motivoTerminoAdes;
    public List<String> srsCodigos;
    public List<String> nseCodigos;
    public boolean tmoDecisaoJudicial;
    public boolean parcelaDescontoPeriodo;

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(orgCodigos) && responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        }

        final boolean temStatus = ((srsCodigos != null) && !srsCodigos.isEmpty());

        final String fields = "select distinct ";

        final StringBuilder corpoBuilder = new StringBuilder(fields);

        corpoBuilder.append("cnv.cnvCodVerba, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("ser.serNome, ");
        corpoBuilder.append("coalesce(str(ade.adePrazo), str(999)) AS PRAZO,");
        corpoBuilder.append("ade.adeData AS ADE_DATA,");
        corpoBuilder.append("ade.adeAnoMesFim as DATA_FIM, ");
        corpoBuilder.append("ade.adeNumero AS ADE_NUMERO,");
        corpoBuilder.append("ser.serCpf AS SER_CPF, ");
        corpoBuilder.append("ade.adeCodigo AS ADE_CODIGO, ");
        corpoBuilder.append("usu.usuNome AS USU_NOME, ");
        corpoBuilder.append("spd.spdDescricao AS SPD_DESCRICAO, ");
        corpoBuilder.append("prd.prdNumero AS PRD_NUMERO, ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when prd.spdCodigo in ('" + CodedValues.SPD_LIQUIDADAFOLHA  + "', '" + CodedValues.SPD_LIQUIDADAMANUAL + "') then prd.prdVlrRealizado ");
        corpoBuilder.append("else prd.prdVlrPrevisto ");
        corpoBuilder.append("end as PRD_VLR_PREVISTO,");
        corpoBuilder.append("prd.prdDataDesconto AS PRD_DATA_DESCONTO, ");
        corpoBuilder.append("ade.adeVlr as ADE_VLR, ");
        corpoBuilder.append("ade.sadCodigo as SAD_CODIGO ");

        if(parcelaDescontoPeriodo) {
            corpoBuilder.append(" from ParcelaDescontoPeriodo prd ");
        }else {
            corpoBuilder.append(" from ParcelaDesconto prd ");
        }

        corpoBuilder.append(" inner join prd.statusParcelaDesconto spd ");
        corpoBuilder.append(" inner join prd.autDesconto ade ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join ade.usuario usu ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append(" inner join rse.orgao org");

        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" inner join rse.subOrgao sbo");
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" inner join rse.unidade uni");
        }

        if (((tipoEntidade != null) && "EST".equalsIgnoreCase(tipoEntidade)) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" inner join org.estabelecimento est");
        }

        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");

        if ((tipoEntidade != null) && "COR".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" inner join ade.correspondente cor");
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor");
        }

        if (tmoDecisaoJudicial) {
            corpoBuilder.append(" inner join oca.tipoMotivoOperacao tmo");
        }

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND prd.prdDataDesconto between :dataIni and :dataFim");
        }

        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        if (tmoDecisaoJudicial) {
            corpoBuilder.append(" AND tmo.tmoDecisaoJudicial").append(criaClausulaNomeada("tmoDecisaoJudicial", CodedValues.TPC_SIM));
        }

        filtroOrigemAde(corpoBuilder);
        filtroTerminoAde(corpoBuilder);

        if(!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if(!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (((tipoEntidade != null) && "EST".equalsIgnoreCase(tipoEntidade)) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if ((tipoEntidade != null) && "ORG".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if(!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((tipoEntidade != null) && "COR".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        } else if (((corCodigos != null) && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            corpoBuilder.append(" and ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }

        if((svcCodigo != null) && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.servico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigos));
        }

        if ((tipoEntidade != null) && "EST".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo = org.orgCodigo ");
        }

        if ((tipoEntidade != null) && "COR".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and cor.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" and crc.correspondente.corCodigo = cor.corCodigo");
        }

       if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" and sbo.sboCodigo ").append(criaClausulaNomeada("sboCodigo", sboCodigo));
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" and uni.uniCodigo ").append(criaClausulaNomeada("uniCodigo", uniCodigo));
        }

        // Ordena a lista total de parcelas por Verba, Data Inclusão, Ade Numero e Numero Parcela
        corpoBuilder.append("order by cnv.cnvCodVerba, ade.adeData, ade.adeNumero, prd.prdNumero");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateString(dataIni), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateString(dataFim), query);
        }

        if ((srsCodigos != null) && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (tmoDecisaoJudicial) {
            defineValorClausulaNomeada("tmoDecisaoJudicial", CodedValues.TPC_SIM, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (((corCodigos != null) && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }

        if ((svcCodigo != null) && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigos, query);
        }

        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }

        if (!TextHelper.isNull(uniCodigo)) {
            defineValorClausulaNomeada("uniCodigo", uniCodigo, query);
        }


        return query;
    }

    protected void filtroOrigemAde(StringBuilder corpoBuilder) {
        if ((origemAdes != null) && (origemAdes.size() > 0)) {
            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && !origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                if (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    //só nova
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else {
                    //nova ou portabilidade
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                }
            }

            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)
                    && origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA))) {
                //nova ou renegociação
                corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
                corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
            }

            if (!origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA)) {
                if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA) && origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    //renegociação ou portabilidade
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    //só renegociação
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    //só portabilidade
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                }
            }
        }
    }


    /**
     * filtra os contratos liquidados de acordo com a natureza do encerramento (se foi renegociação, venda, conclusão, cancelamento ou
     * liquidação antecipada).
     * @param corpoBuilder
     */
    protected void filtroTerminoAde(StringBuilder corpoBuilder) {
        boolean usouRelacionamento = false;

        if ((motivoTerminoAdes != null) && !motivoTerminoAdes.isEmpty()) {
            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) && !motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA)) {
                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else {
                    corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                }

                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CONCLUSAO)) {
                    corpoBuilder.append(" AND sad.sadCodigo <> '").append(CodedValues.SAD_CONCLUIDO).append("'");
                }

                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                    corpoBuilder.append(" AND sad.sadCodigo <> '").append(CodedValues.SAD_CANCELADA).append("'");
                }
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) && (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)
                    && motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA))) {
                corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
                corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                usouRelacionamento = true;
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) &&
                    motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA) &&
                    motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                corpoBuilder.append(" AND (sad.sadCodigo = '").append(CodedValues.SAD_LIQUIDADA).append("'");
                usouRelacionamento = true;
            }

            if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA)) {
                if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA) && motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                }
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CONCLUSAO)) {
                if (usouRelacionamento) {
                    corpoBuilder.append(" OR ");
                } else {
                    corpoBuilder.append(" AND ");
                }

                if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                    corpoBuilder.append(" sad.sadCodigo IN ('").append(CodedValues.SAD_CONCLUIDO).append("','").append(CodedValues.SAD_CANCELADA).append("')");
                } else {
                    corpoBuilder.append(" sad.sadCodigo = '").append(CodedValues.SAD_CONCLUIDO).append("'");
                }
            } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                if (usouRelacionamento) {
                    corpoBuilder.append(" OR ");
                } else {
                    corpoBuilder.append(" AND ");
                }
                corpoBuilder.append(" sad.sadCodigo = '").append(CodedValues.SAD_CANCELADA).append("'");
            }

            if (usouRelacionamento) {
                corpoBuilder.append(")");
            }
        }
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.ADE_PRAZO,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_NUMERO,
                Columns.SER_CPF,
                Columns.ADE_CODIGO,
                Columns.USU_NOME,
                Columns.SPD_DESCRICAO,
                Columns.PRD_NUMERO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_DATA_DESCONTO,
                Columns.ADE_VLR,
                Columns.ADE_SAD_CODIGO
        };
    }

}
