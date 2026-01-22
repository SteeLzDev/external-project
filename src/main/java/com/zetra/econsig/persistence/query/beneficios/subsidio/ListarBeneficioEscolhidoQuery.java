package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: ListarBeneficioEscolhidoQuery</p>
 * <p>Description: Lista benefícios selecionados para simulação de cálculo de benefícios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficioEscolhidoQuery extends HQuery {

    public List<String> benCodigos;
    public List<String> svcCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ben.benCodigo, ");
        corpoBuilder.append("csa.csaCodigo, ");
        corpoBuilder.append("nse.nseCodigo, ");
        corpoBuilder.append("tnt.tntCodigo, ");
        corpoBuilder.append("svc.svcCodigo, ");
        corpoBuilder.append("svc.svcIdentificador, ");
        corpoBuilder.append("pse256.pseVlr, ");
        corpoBuilder.append("pse257.pseVlr, ");
        corpoBuilder.append("pse258.pseVlr, ");
        corpoBuilder.append("pse259.pseVlr, ");
        corpoBuilder.append("pse260.pseVlr, ");
        corpoBuilder.append("pse261.pseVlr, ");
        corpoBuilder.append("pse262.pseVlr, ");
        corpoBuilder.append("pse263.pseVlr,  ");
        corpoBuilder.append("pse270.pseVlr  ");

        corpoBuilder.append("FROM BeneficioServico bse ");
        corpoBuilder.append("INNER JOIN bse.beneficio ben ");
        corpoBuilder.append("INNER JOIN ben.consignataria csa ");
        corpoBuilder.append("INNER JOIN ben.naturezaServico nse ");
        corpoBuilder.append("INNER JOIN bse.servico svc ");
        corpoBuilder.append("INNER JOIN nse.tipoLancamentoSet tla ");
        corpoBuilder.append("INNER JOIN tla.tipoNatureza tnt ");
        corpoBuilder.append("INNER JOIN svc.paramSvcConsignanteSet pse256 WITH pse256.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TEM_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse257 WITH pse257.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse258 WITH pse258.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse259 WITH pse259.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse260 WITH pse260.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse261 WITH pse261.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse262 WITH pse262.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse263 WITH pse263.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse270 WITH pse270.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO).append("' ");

        corpoBuilder.append("WHERE ben.benCodigo IN (:benCodigos) ");
        corpoBuilder.append("AND bse.tipoBeneficiario.tibCodigo = :tibCodigo ");
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append("AND svc.svcCodigo IN (:svcCodigos) ");
        }

        corpoBuilder.append(" AND tnt.tntCodigo ").append(criaClausulaNomeada("tntCodigos", CodedValues.TNT_BENEFICIO_MENSALIDADE));

        corpoBuilder.append("ORDER BY TO_NUMERIC(COALESCE(NULLIF(TRIM(pse257.pseVlr), ''), '0')) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("benCodigos", benCodigos, query);
        defineValorClausulaNomeada("tibCodigo", TipoBeneficiarioEnum.TITULAR.tibCodigo, query);
        defineValorClausulaNomeada("tntCodigos", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BEN_CODIGO,
                Columns.CSA_CODIGO,
                Columns.NSE_CODIGO,
                Columns.TNT_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.PSE_VLR + CodedValues.TPS_TEM_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO
        };
    }
}
