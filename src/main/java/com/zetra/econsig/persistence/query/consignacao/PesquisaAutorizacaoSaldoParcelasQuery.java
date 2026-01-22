package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PesquisaAutorizacaoSaldoParcelasQuery</p>
 * <p>Description: Pesquisa os contratos do servidor, consignatária e serviço informado,
 * que sejam diferente do adeCodigo passado por parâmetro. Se informado
 * o adeIndice faz a verificação também do indice. A consulta é utilizada
 * para localizar o contrato relacionado ao saldo de parcelas.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PesquisaAutorizacaoSaldoParcelasQuery extends HQuery {

    public String adeCodigo;
    public String rseCodigo;
    public String csaCodigo;
    public String adeIndice;
    public List<String> svcCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> filtroAde = new ArrayList<>();
        filtroAde.add(CodedValues.NOT_EQUAL_KEY);
        filtroAde.add(adeCodigo);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.adeCodigo, ade.statusAutorizacaoDesconto.sadCodigo, ade.adePrdPagas ");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" WHERE cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("filtroAde", filtroAde));

        if (!TextHelper.isNull(adeIndice)) {
            corpoBuilder.append(" AND ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
        }

        corpoBuilder.append(" ORDER BY ade.adeData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("filtroAde", filtroAde, query);
        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_SAD_CODIGO,
                Columns.ADE_PRD_PAGAS
        };

    }

}
