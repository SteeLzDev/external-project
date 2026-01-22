package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoReaberturaQuery</p>
 * <p>Description: Listagem de Consignações de um registro servidor para reabertura</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoReaberturaQuery extends HQuery {

    private final String rseCodigo;
    private final Date periodoAtual;

    public ListaConsignacaoReaberturaQuery(String rseCodigo, Date periodoAtual) {
        this.rseCodigo = rseCodigo;
        this.periodoAtual = periodoAtual;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        Object paramCarenciaFolha = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
        int carenciaFolha = (!TextHelper.isNull(paramCarenciaFolha) ? Integer.parseInt(paramCarenciaFolha.toString()) : 0);

        corpoBuilder.append("select ade.adeCodigo ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo = :rseCodigo ");
        corpoBuilder.append("and ade.statusAutorizacaoDesconto.sadCodigo = :sadCodigo ");

        // DESENV-18322 : não reabrir consignações de serviços em que o parâmetro "290 - Concluir contratos na
        // data fim se o servidor estiver excluído" esteja habilitado e a data fim tenha passado.
        corpoBuilder.append("and (not exists (");
        corpoBuilder.append("   select 1 from ParamSvcConsignataria psc ");
        corpoBuilder.append("   where psc.svcCodigo = cnv.svcCodigo ");
        corpoBuilder.append("     and psc.csaCodigo = cnv.csaCodigo");
        corpoBuilder.append("     and psc.tpsCodigo = '").append(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO).append("'");
        corpoBuilder.append("     and coalesce(psc.pscAtivo, 1) = 1");
        corpoBuilder.append("     and coalesce(nullif(psc.pscVlr, ''), '").append(CodedValues.PSC_BOOLEANO_NAO).append("') = '").append(CodedValues.PSC_BOOLEANO_SIM).append("' ");
        corpoBuilder.append(") ");

        // Não tem data fim
        corpoBuilder.append("or ade.adeAnoMesFim is null ");

        // Data fim ainda não passou
        corpoBuilder.append("or add_month(ade.adeAnoMesFim, coalesce(ade.adeCarenciaFinal, 0) + ").append(carenciaFolha).append(") >= :periodoAtual ");

        corpoBuilder.append(") ");

        corpoBuilder.append("order by ade.adeData asc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_ENCERRADO, query);
        defineValorClausulaNomeada("periodoAtual", periodoAtual, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
