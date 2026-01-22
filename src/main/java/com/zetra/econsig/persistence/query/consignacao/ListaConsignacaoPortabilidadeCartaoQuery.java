package com.zetra.econsig.persistence.query.consignacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.Date;

public class ListaConsignacaoPortabilidadeCartaoQuery extends HQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaConsignacaoPortabilidadeCartaoQuery.class);

    public Collection<String> adeCodigos;
    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("select distinct ade.adeCodigo, ");
            corpoBuilder.append("ade.adeNumero, ");
            corpoBuilder.append("ade.adeIdentificador, ");
            corpoBuilder.append("ade.adeData, ");
            corpoBuilder.append("ade.adePrazo, ");
            corpoBuilder.append("ade.adeVlrLiquido, ");
            corpoBuilder.append("ade.adeVlr, ");
            corpoBuilder.append("ade.adeTaxaJuros, ");
            corpoBuilder.append("ade.adePrdPagas, ");
            corpoBuilder.append("ade.adeAnoMesIni, ");
            corpoBuilder.append("ade.adeAnoMesFim, ");
            corpoBuilder.append("ade.adeTipoVlr, ");
            corpoBuilder.append("ade.adeCarencia, ");
            corpoBuilder.append("sad.sadCodigo, ");
            corpoBuilder.append("sad.sadDescricao, ");
            corpoBuilder.append("usu.usuLogin, ");
            corpoBuilder.append("svc.svcCodigo, ");
            corpoBuilder.append("svc.svcDescricao, ");
            corpoBuilder.append("svc.svcIdentificador, ");
            corpoBuilder.append("csa.csaCodigo, ");
            corpoBuilder.append("csa.csaNome, ");
            corpoBuilder.append("cnv.cnvCodigo, ");
            corpoBuilder.append("cnv.cnvCodVerba, ");
            corpoBuilder.append("cft.cftVlr ");
        } else {
            corpoBuilder.append("select distinct ade.adeCodigo ");
        }

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("left join svc.paramSvcConsignanteSet pse332 with pse332.tpsCodigo = '").append(CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE).append("' ");
        corpoBuilder.append("left join ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rel with rel.tntCodigo = '").append(CodedValues.TNT_CARTAO).append("' ");
        corpoBuilder.append("left join rel.autDescontoByAdeCodigoDestino ade2 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("inner join ade.usuario usu ");
            corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
            corpoBuilder.append(" left outer join ade.coeficienteDescontoSet cde ");
            corpoBuilder.append(" left outer join cde.coeficiente cft ");
            corpoBuilder.append(" WHERE csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(adeCodigos)) {
            corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));
        }

        corpoBuilder.append(" AND NOT EXISTS (select 1 from ParamConsignataria tpa101 where tpa101.csaCodigo = csa.csaCodigo ");
        corpoBuilder.append(" and tpa101.tpaCodigo = '").append(CodedValues.TPA_CSA_PODE_VENDER_CONTRATO_CARTAO).append("' ");
        corpoBuilder.append(" and (tpa101.pcsVlr = '").append(CodedValues.TPA_NAO).append("' or tpa101.pcsVlr is null) ");
        corpoBuilder.append(" and svc.nseCodigo = '").append(CodedValues.NSE_CARTAO).append("') ");
        corpoBuilder.append(" and ade.sadCodigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");

        corpoBuilder.append(" AND ( EXISTS ( select 1 from ParamConsignataria tpa102 ");
        corpoBuilder.append(" where tpa102.tpaCodigo = '").append(CodedValues.TPA_CSA_PODE_COMPRAR_CONTRATO_CARTAO).append("' ");
        corpoBuilder.append(" and tpa102.pcsVlr = '").append(CodedValues.TPA_SIM).append("' ");
        corpoBuilder.append(" and tpa102.csaCodigo <> csa.csaCodigo )");
        corpoBuilder.append(" OR svc.nseCodigo <> '").append(CodedValues.NSE_CARTAO).append("') ");

        corpoBuilder.append(" group by ade.adeCodigo, pse332.pseVlr, ade.adeAnoMesIniRef, ade.adeAnoMesIni, svc.svcCodigo ");
        corpoBuilder.append(" having not (pse332.pseVlr = '1' and pse332.pseVlr is not null and count(rel.adeCodigoDestino) > 0)");
        corpoBuilder.append(" AND (");
        corpoBuilder.append(" case when max(ade2.adeAnoMesIniRef) is null then 1000 else month_diff(max(coalesce(ade2.adeAnoMesIniRef, ade2.adeAnoMesIni)), :periodo) end ");
        corpoBuilder.append(" > (SELECT coalesce(MAX(CASE isnumeric_ne(pse331.pseVlr) WHEN 1 THEN to_numeric_ne(pse331.pseVlr) ELSE 0 END), 0)");
        corpoBuilder.append(" FROM svc.paramSvcConsignanteSet pse331 WHERE pse331.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE).append("') )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (corpoBuilder.toString().contains(":adeCodigo")) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }
        if (corpoBuilder.toString().contains(":csaCodigo")) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

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

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_DATA,
                Columns.ADE_PRAZO,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR,
                Columns.ADE_TAXA_JUROS,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_CARENCIA,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CFT_VLR
        };
    }
}
