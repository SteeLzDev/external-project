package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AlteracaoADEValorRetornoQuery</p>
 * <p>Description: Relatório de contratos alterados no retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlteracaoADEValorRetornoQuery extends ReportHQuery {
    public String periodo;
    public String csaCodigo;
    public List<String> orgCodigos;
    public List<String> svcCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO);
        csaCodigo = (String) criterio.getAttribute(Columns.CNV_CSA_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.CNV_ORG_CODIGO);
        svcCodigo = (List<String>) criterio.getAttribute(Columns.CNV_SVC_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder query = new StringBuilder();

        query.append("select distinct ade.adeCodigo as ade_codigo, cast(ade.adeNumero as string) as ade_numero, prdVlr.prdVlrPrevisto as VLR_ANTIGO,");
        query.append("prdVlr.prdVlrRealizado as VLR_NOVO, statusAde.sadDescricao as sad_descricao, ade.adeData as ade_data,");
        query.append("coalesce(str(ade.adePrazo),'"+ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", (AcessoSistema) null)+"') as ade_prazo, coalesce(cast(ade.adePrdPagas as string),'0') as ade_prd_pagas,");
        query.append("concat(rse.rseMatricula, ' - ', ser.serNome) as servidor, srs.srsDescricao as srs_descricao,");
        query.append("ser.serCpf as ser_cpf, cnv.cnvCodVerba as cnv_cod_verba");

        query.append(" from AutDesconto ade");
        query.append(" inner join ade.parcelaDescontoSet prdVlr");
        query.append(" inner join ade.verbaConvenio vco");
        query.append(" inner join vco.convenio cnv");
        query.append(" inner join cnv.servico.paramSvcConsignanteSet pse");
        query.append(" inner join cnv.orgao.historicoExportacaoSet hie");
        query.append(" inner join ade.registroServidor rse");
        query.append(" inner join rse.servidor ser");
        query.append(" inner join rse.statusRegistroServidor srs");
        query.append(" inner join ade.statusAutorizacaoDesconto statusAde");

        query.append(" where statusAde.sadCodigo not in ('").append(CodedValues.SAD_LIQUIDADA).append("', '").append(CodedValues.SAD_CONCLUIDO).append("') ");
        query.append(" and pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO).append("'");
        query.append(" and pse.pseVlr = '1'");
        query.append(" and hie.hiePeriodo = prdVlr.prdDataDesconto");
        query.append(" and prdVlr.prdVlrRealizado <> prdVlr.prdVlrPrevisto");
        query.append(" and prdVlr.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
        query.append(" and prdVlr.prdDataDesconto = :periodo");
        query.append(" and not exists (select 1 from OcorrenciaAutorizacao oca where oca.autDesconto.adeCodigo = ade.adeCodigo");
        query.append("                  and oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
        query.append("                  and oca.ocaData > hie.hieDataFim)");

        if (!TextHelper.isNull(csaCodigo)) {
            query.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            query.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            query.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        query.append(" order by cnv.cnvCodVerba");

        Query<Object[]> hQuery = instanciarQuery(session, query.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, hQuery);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, hQuery);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, hQuery);
        }

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), hQuery);
        }

        return hQuery;
    }

}
