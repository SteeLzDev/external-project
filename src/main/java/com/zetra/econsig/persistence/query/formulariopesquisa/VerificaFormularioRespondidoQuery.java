package com.zetra.econsig.persistence.query.formulariopesquisa;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class VerificaFormularioRespondidoQuery extends HQuery {

    public String usuCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT fpr.fprCodigo, fpe.fpeCodigo, fpe.fpePublicado ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("FROM FormularioPesquisa fpe ");
        corpoBuilder.append("LEFT JOIN FormularioPesquisaResposta fpr ON fpe.fpeCodigo = fpr.formularioPesquisa.fpeCodigo AND fpr.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo",usuCodigo));

        corpoBuilder.append(" ORDER BY fpe.fpeDtCriacao DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FPR_CODIGO,
                Columns.FPE_CODIGO,
                Columns.FPE_PUBLICADO
        };
    }
}