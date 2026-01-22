package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBeneficiariosCalculoSubsidioQuery</p>
 * <p>Description: Lista beneficiários para cálculo de subsídio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficiariosCalculoSubsidioQuery extends HQuery {

    // Caso a Ade seja informada não vamos validar o periodo e o status do contrato.
    public String adeCodigo;
    public String serCodigo;
    public String rseCodigo;
    public List<String> tntCodigos;
    public List<String> scbCodigos;
    public boolean ignoraPeriodo = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("bfc.bfcCodigo, ");
        corpoBuilder.append("grp.grpCodigo, ");
        corpoBuilder.append("bfc.bfcOrdemDependencia, ");
        corpoBuilder.append("bfc.bfcNome, ");
        corpoBuilder.append("bfc.bfcDataNascimento, ");
        corpoBuilder.append("bfc.bfcEstadoCivil, ");
        corpoBuilder.append("bfc.tipoBeneficiario.tibCodigo, ");
        corpoBuilder.append("bfc.motivoDependencia.mdeCodigo, ");
        corpoBuilder.append("bfc.bfcSubsidioConcedido, ");
        corpoBuilder.append("bfc.bfcCpf, ");
        corpoBuilder.append("cbe.cbeCodigo, ");
        corpoBuilder.append("cbe.cbeNumero, ");
        corpoBuilder.append("cbe.cbeValorTotal, ");
        corpoBuilder.append("cbe.cbeDataInicioVigencia, ");
        corpoBuilder.append("cbe.beneficio.benCodigo, ");
        corpoBuilder.append("svc.naturezaServico.nseCodigo, ");
        corpoBuilder.append("ade.adeCodigo, ");
        corpoBuilder.append("rse.rseCodigo, ");
        corpoBuilder.append("org.orgCodigo, ");
        corpoBuilder.append("csa.csaCodigo, ");
        corpoBuilder.append("ser.serCodigo, ");
        corpoBuilder.append("rse.statusRegistroServidor.srsCodigo, ");
        corpoBuilder.append("rse.rseSalario, ");
        corpoBuilder.append("tla.tlaCodigo, ");
        corpoBuilder.append("tnt.tntCodigo, ");
        corpoBuilder.append("ben.naturezaServico.nseCodigo, ");
        corpoBuilder.append("svc.svcCodigo, ");

        corpoBuilder.append("pse256.pseVlr, ");
        corpoBuilder.append("pse257.pseVlr, ");
        corpoBuilder.append("pse258.pseVlr, ");
        corpoBuilder.append("pse259.pseVlr, ");
        corpoBuilder.append("pse260.pseVlr, ");
        corpoBuilder.append("pse261.pseVlr, ");
        corpoBuilder.append("pse262.pseVlr, ");
        corpoBuilder.append("pse263.pseVlr,  ");
        corpoBuilder.append("pse270.pseVlr  ");

        corpoBuilder.append("FROM ContratoBeneficio cbe ");
        corpoBuilder.append("INNER JOIN cbe.beneficio ben ");
        corpoBuilder.append("INNER JOIN cbe.beneficiario bfc ");
        corpoBuilder.append("INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        if (!ignoraPeriodo) {
            corpoBuilder.append("INNER JOIN org.periodoBeneficioSet pbe ");
        }
        corpoBuilder.append("INNER JOIN ade.tipoLancamento tla ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN tla.tipoNatureza tnt ");
        corpoBuilder.append("LEFT  JOIN bfc.grauParentesco grp ");

        corpoBuilder.append("INNER JOIN svc.paramSvcConsignanteSet pse256 WITH pse256.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TEM_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse257 WITH pse257.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse258 WITH pse258.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse259 WITH pse259.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse260 WITH pse260.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse261 WITH pse261.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse262 WITH pse262.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse263 WITH pse263.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse270 WITH pse270.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");

        corpoBuilder.append("WHERE 1 = 1 ");

        // Beneficiários do grupo familiar
        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append("AND bfc.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo)).append(" ");
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(" ");
        }

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append("AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo)).append(" ");
        }

        if (!TextHelper.isNull(scbCodigos)) {
            corpoBuilder.append("AND cbe.statusContratoBeneficio.scbCodigo in (:scbCodigos) ");
        }

        // De serviços que possuem subsídio
        corpoBuilder.append("AND pse256.pseVlr = '1' ");

        // De natureza de relacionamento da mensalidade de benefício
        corpoBuilder.append("AND tla.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigos)).append(" ");

        // Consignações em andamento, ou excluídas pós corte
        if (!ignoraPeriodo) {
            corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
            corpoBuilder.append("OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_EXCLUIDOS_POS_CORTE, "','")).append("') ");
            corpoBuilder.append("AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
            corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXCLUSAO_POS_CORTE, "','")).append("') ");
            corpoBuilder.append("AND (oca.ocaData > pbe.pbeDataFim OR oca.ocaPeriodo > pbe.pbePeriodo)))) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tntCodigo", tntCodigos, query);

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        if (!TextHelper.isNull(scbCodigos)) {
            defineValorClausulaNomeada("scbCodigos", scbCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BFC_CODIGO,
                Columns.BFC_GRP_CODIGO,
                Columns.BFC_ORDEM_DEPENDENCIA,
                Columns.BFC_NOME,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.BFC_ESTADO_CIVIL,
                Columns.TIB_CODIGO,
                Columns.MDE_CODIGO,
                Columns.BFC_SUBSIDIO_CONCEDIDO,
                Columns.BFC_CPF,
                Columns.CBE_CODIGO,
                Columns.CBE_NUMERO,
                Columns.CBE_VALOR_TOTAL,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.BEN_CODIGO,
                Columns.NSE_CODIGO,
                Columns.ADE_CODIGO,
                Columns.RSE_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SER_CODIGO,
                Columns.SRS_CODIGO,
                Columns.RSE_SALARIO,
                Columns.TLA_CODIGO,
                Columns.TNT_CODIGO,
                Columns.NSE_CODIGO,
                Columns.SVC_CODIGO,
                Columns.PSE_VLR + CodedValues.TPS_TEM_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO,
        };
    }
}