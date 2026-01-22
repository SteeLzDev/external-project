package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBeneficiariosRemocaoSubsidioQuery</p>
 * <p>Description: Lista beneficiários para remoção do subsídio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficiariosRemocaoSubsidioQuery extends HQuery {

    public Date dataCalculoSubsidio;
    public String tipoEntidade;
    public List<String> entCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("ser.serCodigo, ");
        corpoBuilder.append("org.orgCodigo, ");
        corpoBuilder.append("ade.adeCodigo, ");
        corpoBuilder.append("cbe.cbeCodigo, ");
        corpoBuilder.append("cbe.cbeNumero, ");
        corpoBuilder.append("cbe.cbeDataInicioVigencia, ");
        corpoBuilder.append("cbe.beneficio.benCodigo, ");
        corpoBuilder.append("bfc.bfcCodigo, ");
        corpoBuilder.append("grp.grpCodigo, ");
        corpoBuilder.append("bfc.bfcOrdemDependencia, ");
        corpoBuilder.append("bfc.bfcNome, ");
        corpoBuilder.append("bfc.bfcDataNascimento, ");
        corpoBuilder.append("bfc.tipoBeneficiario.tibCodigo, ");
        corpoBuilder.append("bfc.motivoDependencia.mdeCodigo, ");
        corpoBuilder.append("rse.statusRegistroServidor.srsCodigo, ");
        corpoBuilder.append("rse.rseSalario, ");
        corpoBuilder.append("tla.tlaCodigo, ");
        corpoBuilder.append("tnt.tntCodigo, ");
        corpoBuilder.append("csa.csaCodigo, ");

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
        corpoBuilder.append("INNER JOIN cbe.beneficiario bfc ");
        corpoBuilder.append("INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN org.periodoBeneficioSet pbe ");
        corpoBuilder.append("INNER JOIN ade.tipoLancamento tla ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN tla.tipoNatureza tnt ");
        corpoBuilder.append("LEFT  JOIN bfc.grauParentesco grp ");

        // Faz join com os parâmetros apenas para registrar na memória de cálculo
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse256 WITH pse256.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TEM_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse257 WITH pse257.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse258 WITH pse258.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse259 WITH pse259.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse260 WITH pse260.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse261 WITH pse261.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse262 WITH pse262.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse263 WITH pse263.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse270 WITH pse270.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");

        // De natureza de relacionamento da mensalidade de benefício
        corpoBuilder.append("WHERE tla.tipoNatureza.tntCodigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("') ");

        // Consignações em andamento, ou excluídas pós corte
        corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
        corpoBuilder.append("OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_EXCLUIDOS_POS_CORTE, "','")).append("') ");
        corpoBuilder.append("AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXCLUSAO_POS_CORTE, "','")).append("') ");
        corpoBuilder.append("AND (oca.ocaData > pbe.pbeDataFim OR oca.ocaPeriodo > pbe.pbePeriodo)))) ");

        // Que não tenham memória de cáculo gravada após o início do cálculo de subsídio
        corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM cbe.memoriaCalculoSubsidioSet mcs WHERE mcs.mcsData >= :dataCalculoSubsidio) ");

        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            if (tipoEntidade.equals("RSE")) {
                corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if (tipoEntidade.equals("ORG")) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if (tipoEntidade.equals("EST")) {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dataCalculoSubsidio", dataCalculoSubsidio, query);
        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            defineValorClausulaNomeada("entCodigos", entCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CPF,
                Columns.SER_CODIGO,
                Columns.ORG_CODIGO,
                Columns.ADE_CODIGO,
                Columns.CBE_CODIGO,
                Columns.CBE_NUMERO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.BEN_CODIGO,
                Columns.BFC_CODIGO,
                Columns.BFC_GRP_CODIGO,
                Columns.BFC_ORDEM_DEPENDENCIA,
                Columns.BFC_NOME,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.TIB_CODIGO,
                Columns.MDE_CODIGO,
                Columns.SRS_CODIGO,
                Columns.RSE_SALARIO,
                Columns.TLA_CODIGO,
                Columns.TNT_CODIGO,
                Columns.CSA_CODIGO,
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