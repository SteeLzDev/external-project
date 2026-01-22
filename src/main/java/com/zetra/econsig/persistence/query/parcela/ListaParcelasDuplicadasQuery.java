package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaParcelasDuplicadasQuery</p>
 * <p>Description: Lista os períodos que possuem parcelas do período duplicada com
 * alguma parcela histórica.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasDuplicadasQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ListaParcelasDuplicadasQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select prd.prdDataDesconto, pdp.prdDataDesconto, count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.parcelaDescontoPeriodoSet pdp ");
        corpoBuilder.append("inner join ade.parcelaDescontoSet prd ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("where prd.prdNumero = pdp.prdNumero ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and prd.statusParcelaDesconto.spdCodigo != '").append(CodedValues.SPD_EMABERTO).append("'");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                ParamSist.paramEquals(CodedValues.TPC_PADRAO_FORMA_NUMERACAO_PARCELAS, CodedValues.FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR, AcessoSistema.getAcessoUsuarioSistema())) {
            String padraoFormaNumeracao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_FORMA_NUMERACAO_PARCELAS, AcessoSistema.getAcessoUsuarioSistema());
            if (TextHelper.isNull(padraoFormaNumeracao)) {
                padraoFormaNumeracao = CodedValues.FORMA_NUMERACAO_PARCELAS_SEQUENCIAL;
            }

            corpoBuilder.append(" and (prd.prdDataDesconto = pdp.prdDataDesconto ");
            corpoBuilder.append("   or coalesce(( ");
            corpoBuilder.append("    select psc.pscVlr ");
            corpoBuilder.append("    from ParamSvcConsignataria psc ");
            corpoBuilder.append("    where psc.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
            corpoBuilder.append("      and psc.servico.svcCodigo = cnv.servico.svcCodigo ");
            corpoBuilder.append("      and psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS).append("' ");
            corpoBuilder.append("   ), '").append(padraoFormaNumeracao).append("') <> '").append(CodedValues.FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR).append("'");
            corpoBuilder.append(")");
        }

        corpoBuilder.append(" group by prd.prdDataDesconto, pdp.prdDataDesconto");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "PERIODO_PRD",
                "PERIODO_PDP",
                "QTD"
        };
    }
}
