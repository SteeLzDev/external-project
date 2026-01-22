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

/**
 * <p> Title: RelatorioConsignacoesQuery</p>
 * <p> Description: Relatório de consignações por período.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConsignacoesQuery extends ReportHQuery {
    public String tipoEntidade;
    public String periodo;
    public String dataIni;
    public String dataFim;

    public String dataIniLiquidacao;

    public String dataFimLiquidacao;
    public List<String> orgCodigos;
    public String estCodigo;
    public String csaCodigo;
    public List<String> corCodigos;
    public String sboCodigo;
    public String uniCodigo;
    public List<String> svcCodigo;
    public List<String> sadCodigos;
    public List<String> tocCodigos;
    public String order;
    public List<String> origemAdes;
    public List<String> motivoTerminoAdes;
    private List<String> srsCodigos;
    public List<String> nseCodigos;
    public boolean tmoDecisaoJudicial;

    protected boolean relatorioDescontos;
    public String echCodigo;
    public String plaCodigo;
    public String cnvCodVerba;

    public String adeNumero;

    public String rseMatricula;

    public String serCpf;

    private String rseTipo;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        sboCodigo = (String) criterio.getAttribute("SBO_CODIGO");
        uniCodigo = (String) criterio.getAttribute("UNI_CODIGO");
        svcCodigo = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigos = (List<String>) criterio.getAttribute("SAD_CODIGO");
        tocCodigos = (List<String>) criterio.getAttribute("TOC_CODIGO");
        order = (String) criterio.getAttribute("ORDER");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
        motivoTerminoAdes = (List<String>) criterio.getAttribute("TERMINO_ADE");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
        tmoDecisaoJudicial = (Boolean) criterio.getAttribute("TMO_DECISAO_JUDICIAL");

        if ((responsavel == null) && (criterio.getAttribute("responsavel") != null)) {
            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
        }

        dataIniLiquidacao = (String) criterio.getAttribute("DATA_INI_LIQUIDACAO");
        dataFimLiquidacao = (String) criterio.getAttribute("DATA_FIM_LIQUIDACAO");
        adeNumero = (String) criterio.getAttribute("ADE_NUMERO");
        rseMatricula = (String) criterio.getAttribute("MATRICULA");
        serCpf = (String) criterio.getAttribute("CPF");
        rseTipo = (String) criterio.getAttribute("RSE_TIPO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(orgCodigos) && responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        }

        final boolean temStatus = ((srsCodigos != null) && !srsCodigos.isEmpty());
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
        corpoBuilder.append("case when oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("'");
        corpoBuilder.append(" then '"+ApplicationResourcesHelper.getMessage("rotulo.relatorio.consignacoes.tipo.ocorrencia.inclusao.reserva", (AcessoSistema) null)+"' else toc.tocDescricao end AS OCORRENCIA,");
        corpoBuilder.append("cast(case when oca.tipoOcorrencia.tocCodigo in ('").append(CodedValues.TOC_TARIF_RESERVA).append("','").append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("')");
        corpoBuilder.append(" then 0.00 else coalesce(oca.ocaAdeVlrAnt,ade.adeVlr) end as Double) AS VLR_ANT,");
        corpoBuilder.append("case when oca.tipoOcorrencia.tocCodigo in ('").append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("')");
        corpoBuilder.append(" then (oca.ocaAdeVlrNovo - oca.ocaAdeVlrAnt) else coalesce(oca.ocaAdeVlrNovo, ade.adeVlr) end AS VLR_NOVO,");
        corpoBuilder.append("ade.adeData AS ADE_DATA,");
        corpoBuilder.append("ade.adeVlr AS ADE_VLR,");
        corpoBuilder.append("ade.adeNumero AS ADE_NUMERO,");
        corpoBuilder.append("ade.adeAnoMesIni AS ADE_ANO_MES_INI,");
        corpoBuilder.append("ade.adeAnoMesFim AS ADE_ANO_MES_FIM,");
        corpoBuilder.append("ade.adeIdentificador AS ADE_IDENTIFICADOR,");
        corpoBuilder.append("ade.adeTaxaJuros AS ADE_TAXA_JUROS,");
        corpoBuilder.append("oca.ocaData AS OCA_DATA,");
        corpoBuilder.append("ser.serCpf AS SER_CPF,");
        corpoBuilder.append("rse.rseTipo AS RSE_TIPO,");
        corpoBuilder.append("srs.srsDescricao AS SRS_DESCRICAO,");
        corpoBuilder.append("case when usu.statusLogin.stuCodigo <> '").append(CodedValues.STU_EXCLUIDO).append("' ");
        corpoBuilder.append("then usu.usuLogin else coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin) end AS USU_LOGIN,");
        corpoBuilder.append("sad.sadDescricao AS SAD_DESCRICAO,");
        corpoBuilder.append("ser.serNome AS SER_NOME,");
        corpoBuilder.append("cft.cftVlr AS CFT_VLR,");
        corpoBuilder.append("cde.cdeVlrLiberado AS CDE_VLR_LIBERADO,");
        corpoBuilder.append("cde.cdeVlrLiberadoCalc AS CDE_VLR_LIBERADO_CALC,");
        corpoBuilder.append("ade.adeVlrLiquido AS ADE_VLR_LIQUIDO, ");

        corpoBuilder.append("usuarioCsa.csaCodigo as csa_codigo, ");
        corpoBuilder.append("usuarioCse.cseCodigo as cse_codigo, ");
        corpoBuilder.append("usuarioCor.corCodigo as cor_codigo, ");
        corpoBuilder.append("usuarioOrg.orgCodigo as org_codigo, ");
        corpoBuilder.append("usuarioSer.serCodigo as ser_codigo, ");
        corpoBuilder.append("usuarioSup.cseCodigo as sup_cse_codigo, ");
        corpoBuilder.append("vrs.vrsDescricao as VINCULO_REGISTRO_SERVIDOR, ");
        corpoBuilder.append("vrs.vrsIdentificador as IDENTIFICADOR_REGISTRO_SERVIDOR, ");
        corpoBuilder.append("oca.ocaIpAcesso as OCA_IP_ACESSO, ");

        if (tmoDecisaoJudicial) {
	        corpoBuilder.append("'TRUE' as TMO_DECISAO_JUDICIAL, ");
	        corpoBuilder.append("usu.usuNome as USU_RESPONSAVEL, ");
	        corpoBuilder.append("oca.ocaObs as OCA_OBSERVACAO, ");
	        corpoBuilder.append("tmo.tmoDescricao as TMO_DESCRICAO, ");
        } else {
        	corpoBuilder.append("'FALSE' as TMO_DECISAO_JUDICIAL, ");
        	corpoBuilder.append("'' as USU_RESPONSAVEL, ");
	        corpoBuilder.append("'' as OCA_OBSERVACAO, ");
	        corpoBuilder.append("'' as TMO_DESCRICAO, ");
        }

        //coluna de origem
        corpoBuilder.append(" case when ");
        corpoBuilder.append("(NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" where rad.tipoNatureza.tntCodigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("',");
        corpoBuilder.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
        corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
        corpoBuilder.append(" then '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel)).append("' ");
        corpoBuilder.append(" when (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                            corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
                            corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
        corpoBuilder.append(" then '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel)).append("' ");
        corpoBuilder.append(" when (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
        corpoBuilder.append(" then '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel)).append("' else ''");
        corpoBuilder.append(" end as ORIGEM_CONTRATO, ");
        corpoBuilder.append(" nse.nseDescricao as NSE_DESCRICAO ");


        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join ade.usuario usu ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append(" inner join oca.tipoOcorrencia toc ");
        corpoBuilder.append(" inner join rse.orgao org");
        corpoBuilder.append(" inner join svc.naturezaServico nse ");

        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" inner join rse.subOrgao sbo");
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" inner join rse.unidade uni");
        }

        if (("EST".equalsIgnoreCase(tipoEntidade)) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" inner join org.estabelecimento est");
        }

        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");

        if ("COR".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" inner join ade.correspondente cor");
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor");
        }

        if (tmoDecisaoJudicial) {
            corpoBuilder.append(" inner join oca.tipoMotivoOperacao tmo");
        }

        corpoBuilder.append(" left outer join ade.coeficienteDescontoSet cde");
        corpoBuilder.append(" left outer join cde.coeficiente cft");

        if (relatorioDescontos) {
            corpoBuilder.append(" inner join ade.despesaIndividualSet des ");

            if (!TextHelper.isNull(plaCodigo)) {
                corpoBuilder.append(" inner join des.plano pla ");
            }

            if (!TextHelper.isNull(echCodigo)) {
                corpoBuilder.append(" inner join des.permissionario per ");
            }
        }

        corpoBuilder.append(" left outer join usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" left outer join usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" left outer join usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" left outer join usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" left outer join usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" left outer join usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" left outer join rse.vinculoRegistroServidor vrs");

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" AND oca.ocaPeriodo").append(criaClausulaNomeada("periodo", periodo));
        }

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND oca.ocaData between :dataIni and :dataFim");
        }

        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        if (tmoDecisaoJudicial) {
        	corpoBuilder.append(" AND tmo.tmoDecisaoJudicial").append(criaClausulaNomeada("tmoDecisaoJudicial", CodedValues.TPC_SIM));
        }

        filtroOrigemAde(corpoBuilder);
        filtroTerminoAde(corpoBuilder);

        if (responsavel.isSer()) {
            corpoBuilder.append(" and rse.rseCodigo").append(criaClausulaNomeada("rseCodigo", responsavel.getRseCodigo()));
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (("EST".equalsIgnoreCase(tipoEntidade)) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if(!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if((tocCodigos != null) && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));

            if (!TextHelper.isNull(dataIniLiquidacao) && !TextHelper.isNull(dataFimLiquidacao)) {
                corpoBuilder.append(" AND oca.ocaData between :dataIniLiquidacao and :dataFimLiquidacao");
            }
        }

        if ("COR".equalsIgnoreCase(tipoEntidade)) {
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

        if ((tocCodigos != null) && tocCodigos.contains(CodedValues.TOC_ALTERACAO_CONTRATO)) {
            corpoBuilder.append(" and not exists (select 1 from OcorrenciaAutorizacao oca1 where ade.adeCodigo = oca1.autDesconto.adeCodigo");
            corpoBuilder.append("                 and toc.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
            corpoBuilder.append("                 and oca1.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("'");
            corpoBuilder.append("                 and oca.ocaCodigo <> oca1.ocaCodigo");
            corpoBuilder.append("                 and oca.ocaAdeVlrAnt = oca1.ocaAdeVlrAnt");
            corpoBuilder.append("                 and oca.ocaAdeVlrNovo = oca1.ocaAdeVlrNovo").append(")");
        }

        if ("EST".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo = org.orgCodigo ");
        }

        if ("COR".equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" and cor.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" and crc.correspondente.corCodigo = cor.corCodigo");
        }

        if (relatorioDescontos) {
            if (!TextHelper.isNull(plaCodigo)) {
                corpoBuilder.append(" and pla.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
            }

            if (!TextHelper.isNull(echCodigo)) {
                corpoBuilder.append(" and per.enderecoConjHabitacional.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
            }

            if (!TextHelper.isNull(cnvCodVerba)) {
                corpoBuilder.append(" and cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
            }
        }

        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" and sbo.sboCodigo ").append(criaClausulaNomeada("sboCodigo", sboCodigo));
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" and uni.uniCodigo ").append(criaClausulaNomeada("uniCodigo", uniCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(rseTipo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("rse.rseTipo", "rseTipo", CodedValues.LIKE_MULTIPLO + rseTipo + CodedValues.LIKE_MULTIPLO));
        }

        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" and ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        if (TextHelper.isNull(order)) {
            ordenacao.append("concat(concat(csa.csaIdentificador, ' - '),").append("       case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome").append("       else csa.csaNomeAbrev end)");
        } else if ("CONSIGNATARIA".equals(order)) {
            ordenacao.append("1");
        } else if ("ORGAO".equals(order)) {
            ordenacao.append("4");
        }

        ordenacao.append(",sad.sadDescricao");
        ordenacao.append(",ser.serNome");
        ordenacao.append(",oca.ocaData desc");

        corpoBuilder.append(ordenacao.toString());

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if ((srsCodigos != null) && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (tmoDecisaoJudicial) {
        	defineValorClausulaNomeada("tmoDecisaoJudicial", CodedValues.TPC_SIM, query);
        }

        if (responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
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

        if ((tocCodigos != null) && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);

            if (!TextHelper.isNull(dataIniLiquidacao)) {
                defineValorClausulaNomeada("dataIniLiquidacao", parseDateTimeString(dataIniLiquidacao), query);
            }

            if (!TextHelper.isNull(dataFimLiquidacao)) {
                defineValorClausulaNomeada("dataFimLiquidacao", parseDateTimeString(dataFimLiquidacao), query);
            }
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

        if (relatorioDescontos && !TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (relatorioDescontos && !TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (relatorioDescontos && !TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }

        if (!TextHelper.isNull(uniCodigo)) {
            defineValorClausulaNomeada("uniCodigo", uniCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(rseTipo)) {
            defineValorClausulaNomeada("rseTipo", CodedValues.LIKE_MULTIPLO + rseTipo + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
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

}
