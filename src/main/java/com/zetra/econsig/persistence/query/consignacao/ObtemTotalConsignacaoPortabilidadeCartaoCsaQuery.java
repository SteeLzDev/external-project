package com.zetra.econsig.persistence.query.consignacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;

public class ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery extends HQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery.class);

    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("left join svc.paramSvcConsignanteSet pse332 with pse332.tpsCodigo = '").append(CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE).append("' ");
        corpoBuilder.append("left join ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rel with rel.tntCodigo = '").append(CodedValues.TNT_CARTAO).append("' ");
        corpoBuilder.append("left join rel.autDescontoByAdeCodigoDestino ade2 ");
        corpoBuilder.append("left outer join ade.dadosAutorizacaoDescontoSet dad12 with dad12.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_PERCENTUAL_MINIMO_VIGENCIA_RENEG).append("' ");
        corpoBuilder.append("left outer join ade.dadosAutorizacaoDescontoSet dad13 with dad13.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_PERCENTUAL_MINIMO_VIGENCIA_COMPRA).append("' ");

        corpoBuilder.append(" WHERE csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and ade.sadCodigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
        corpoBuilder.append(" AND svc.nseCodigo <> '").append(CodedValues.NSE_CARTAO).append("'");

        corpoBuilder.append(" AND (month_diff(coalesce(ade.adeAnoMesIniRef, ade.adeAnoMesIni), :periodo) ");
        corpoBuilder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(pse179.pseVlr) WHEN 1 THEN to_numeric_ne(pse179.pseVlr) ELSE 1000 END), -1000)");
        corpoBuilder.append(" FROM svc.paramSvcConsignanteSet pse179 WHERE pse179.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA).append("') ");
        corpoBuilder.append("OR month_diff(coalesce(ade.adeAnoMesIniRef, ade.adeAnoMesIni), :periodo) ");
        corpoBuilder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(psc179.pscVlr) WHEN 1 THEN to_numeric_ne(psc179.pscVlr) ELSE 1000 END), 1000)");
        corpoBuilder.append(" FROM svc.paramSvcConsignatariaSet psc179 WHERE psc179.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA).append("'");
        corpoBuilder.append(" AND psc179.consignataria.csaCodigo = :csaCodigo))");

        corpoBuilder.append(" group by ade.adeCodigo, pse332.pseVlr, rel.radData, ade.adeAnoMesIniRef, ade.adeAnoMesIni, svc.svcCodigo ");
        corpoBuilder.append(" having not (pse332.pseVlr = '1' and pse332.pseVlr is not null and count(rel.adeCodigoDestino) > 0)");
        corpoBuilder.append(" AND (");
        corpoBuilder.append(" case when max(ade2.adeAnoMesIniRef) is null then 1000 else month_diff(max(coalesce(ade2.adeAnoMesIniRef, ade2.adeAnoMesIni)), :periodo) end ");
        corpoBuilder.append(" > (SELECT coalesce(MAX(CASE isnumeric_ne(pse331.pseVlr) WHEN 1 THEN to_numeric_ne(pse331.pseVlr) ELSE 0 END), 0)");
        corpoBuilder.append(" FROM svc.paramSvcConsignanteSet pse331 WHERE pse331.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE).append("') )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        Date periodo = null;
        try {
            periodo = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
            defineValorClausulaNomeada("periodo", periodo, query);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new HQueryException(ex);
        }
        return query;
    }
}
