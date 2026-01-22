package com.zetra.econsig.persistence.query.pontuacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ObtemPontuacaoRseCsaQuery extends HQuery {

    private final String rseCodigo;
    private final String csaCodigo;

    public ObtemPontuacaoRseCsaQuery(String rseCodigo, String csaCodigo) {
        this.rseCodigo = rseCodigo;
        this.csaCodigo = csaCodigo;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select coalesce((select pcc.pccPerfil from csa.perfilConsignadoCsaSet pcc where pon.ponVlr between pcc.pccPontuacaoInferior and pcc.pccPontuacaoSuperior), ");
        corpoBuilder.append(" to_string(pon.ponVlr)) ");
        corpoBuilder.append("from PontuacaoRseCsa pon ");
        corpoBuilder.append("inner join pon.consignataria csa ");
        corpoBuilder.append("where pon.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("  and pon.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PON_VLR
        };
    }
}
