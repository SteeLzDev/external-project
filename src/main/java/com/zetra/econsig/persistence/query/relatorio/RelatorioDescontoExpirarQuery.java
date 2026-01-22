package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


public class RelatorioDescontoExpirarQuery extends ReportHQuery{

	public String tipoEntidade;
    public String adeAnoMesFim;
    public List<String> orgCodigos;
    public String estCodigo;
    public String csaCodigo;
    public String corCodigo;
    public List<String> svcCodigo;
    public List<String> sadCodigos;
    public String order;
    public List<String> origemAdes;
    public List<String> motivoTerminoAdes;
    private List<String> srsCodigos;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        adeAnoMesFim = (String) criterio.getAttribute("ADE_ANO_MES_FIM");
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        corCodigo = (String) criterio.getAttribute("COR_CODIGO");
        svcCodigo = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigos = (List<String>) criterio.getAttribute("SAD_CODIGO");
        order = (String) criterio.getAttribute("ORDER");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
        motivoTerminoAdes = (List<String>) criterio.getAttribute("TERMINO_ADE");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(orgCodigos) && responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        }

        final boolean temStatus = ((srsCodigos != null) && (srsCodigos.size() > 0));
        final boolean permiteCadIndice = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, AcessoSistema.getAcessoUsuarioSistema());
        final StringBuilder ordenacao = new StringBuilder(" order by ");

        final String fields = "select distinct " +
                "concat(concat(csa.csaIdentificador, ' - ')," +
                "       case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome" +
                "       else csa.csaNomeAbrev end) as consignataria," +
                "concat(concat(cor.corIdentificador, ' - '), cor.corNome) AS correspondente," +
                "concat(concat(rse.rseMatricula, ' - '), ser.serNome) AS servidor," +
                "concat(concat(org.orgIdentificador, ' - '), org.orgNome) AS orgao," +
                "csa.csaIdentificador as csa_identificador," +
                "case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome " +
                "else csa.csaNomeAbrev end as csa_nome," +
                "cor.corIdentificador as cor_identificador, cor.corNome as cor_nome, " +
                "rse.rseMatricula as rse_matricula, ser.serNome as ser_nome," +
                "org.orgIdentificador as org_identificador, org.orgNome as org_nome, ";

        final StringBuilder corpoBuilder = new StringBuilder(fields);

        if (permiteCadIndice) {
            corpoBuilder.append("concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            corpoBuilder.append("       case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) AS CODIGO_SERVICO,");
            corpoBuilder.append("concat(concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            corpoBuilder.append("       ' - '),concat(svc.svcDescricao, case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) AS SERVICO,");
            corpoBuilder.append("svc.svcDescricao as svc_descricao,");
        } else {
            corpoBuilder.append("concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) AS CODIGO_SERVICO,");
            corpoBuilder.append("concat(concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), ' - '),concat(svc.svcDescricao,case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) AS SERVICO,");
            corpoBuilder.append("svc.svcDescricao as svc_descricao,");
        }

        corpoBuilder.append("coalesce(str(ade.adePrazo), '"+ApplicationResourcesHelper.getMessage("rotulo.relatorio.consignacoes.indeterminado.abreviado", (AcessoSistema) null)+"') AS PRAZO,");
        corpoBuilder.append("coalesce(str(ade.adeCarencia), '0') AS ADE_CARENCIA,");
        corpoBuilder.append("coalesce(str(ade.adePrdPagas), '0') AS PAGAS,");
        corpoBuilder.append("str(ade.adeNumero) AS ADE_NUM,");
        corpoBuilder.append("ade.adeData AS ADE_DATA,");
        corpoBuilder.append("ade.adeVlr AS ADE_VLR,");
        corpoBuilder.append("ade.adeNumero AS ADE_NUMERO,");
        corpoBuilder.append("ade.adeAnoMesFim AS ADE_ANO_MES_FIM,");
        corpoBuilder.append("ade.adeIdentificador AS ADE_IDENTIFICADOR,");
        corpoBuilder.append("ade.adeTaxaJuros AS ADE_TAXA_JUROS,");
        corpoBuilder.append("ser.serCpf AS SER_CPF,");
        corpoBuilder.append("rse.rseTipo AS RSE_TIPO,");
        corpoBuilder.append("srs.srsDescricao AS SRS_DESCRICAO,");
        corpoBuilder.append("case when usu.statusLogin.stuCodigo <> '").append(CodedValues.STU_EXCLUIDO).append("' ");
        corpoBuilder.append("then usu.usuLogin else coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin) end AS USU_LOGIN,");
        corpoBuilder.append("sad.sadDescricao AS SAD_DESCRICAO,");
        corpoBuilder.append("ser.serNome AS SER_NOME,");
        corpoBuilder.append("cft.cftVlr AS CFT_VLR,");
        corpoBuilder.append("cde.cdeVlrLiberado AS CDE_VLR_LIBERADO,");
        corpoBuilder.append("ade.adeVlrLiquido AS ADE_VLR_LIQUIDO, ");

        corpoBuilder.append("usuarioCsa.csaCodigo as csa_codigo, ");
        corpoBuilder.append("usuarioCse.cseCodigo as cse_codigo, ");
        corpoBuilder.append("usuarioCor.corCodigo as cor_codigo, ");
        corpoBuilder.append("usuarioOrg.orgCodigo as org_codigo, ");
        corpoBuilder.append("usuarioSer.serCodigo as ser_codigo, ");
        corpoBuilder.append("usuarioSup.cseCodigo as sup_cse_codigo ");

        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join ade.usuario usu ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.orgao org");

        if (((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("EST")) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" inner join org.estabelecimento est");
        }

        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");

        if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" inner join ade.correspondente cor");
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor");
        }

        corpoBuilder.append(" left outer join ade.coeficienteDescontoSet cde");
        corpoBuilder.append(" left outer join cde.coeficiente cft");

        corpoBuilder.append(" left outer join usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" left outer join usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" left outer join usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" left outer join usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" left outer join usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" left outer join usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" where 1=1");
        corpoBuilder.append(" AND ade.adeAnoMesFim").append(criaClausulaNomeada("adeAnoMesFim", adeAnoMesFim));

        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        filtroOrigemAde(corpoBuilder);
        filtroTerminoAde(corpoBuilder);

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("EST")) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("ORG")) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if(!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        } else if ((corCodigo != null) && !corCodigo.equals("")) {
            corpoBuilder.append(" and ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if((svcCodigo != null) && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("EST")) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo = org.orgCodigo ");
        }

        if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" and cor.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" and crc.correspondente.corCodigo = cor.corCodigo");
        }

        if (TextHelper.isNull(order)) {
            ordenacao.append("concat(concat(csa.csaIdentificador, ' - '),").append("       case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome").append("       else csa.csaNomeAbrev end)");
        } else {
            if (order.equals("CONSIGNATARIA")) {
                ordenacao.append("1");
            } else if (order.equals("ORGAO")) {
                ordenacao.append("4");
            }

        }

        ordenacao.append(",sad.sadDescricao");
        ordenacao.append(",ser.serNome");

        corpoBuilder.append(ordenacao.toString());

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeAnoMesFim)) {
            defineValorClausulaNomeada("adeAnoMesFim", parseDateString(adeAnoMesFim), query);
        }

        if ((srsCodigos != null) && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
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

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if ((svcCodigo != null) && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        return query;
    }

    protected void filtroOrigemAde(StringBuilder corpoBuilder) {
        if ((origemAdes != null) && (origemAdes.size() > 0)) {
            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && !origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                if (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else {
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                }
            }

            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)
                    && origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA))) {
                corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
                corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
            }

            if (!origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA)) {
                if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA) && origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
                    corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
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

        if ((motivoTerminoAdes != null) && (motivoTerminoAdes.size() > 0)) {
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

}
