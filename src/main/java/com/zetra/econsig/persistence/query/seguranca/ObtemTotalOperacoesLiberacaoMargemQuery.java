package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalOperacoesLiberacaoMargemQuery</p>
 * <p>Description: Obtem o total de operações de liberação de margem por Consignatária ou por Usuário</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalOperacoesLiberacaoMargemQuery extends HQuery {

    public String usuCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from OperacaoLiberaMargem olm ");
        corpoBuilder.append("where olm.olmBloqueio = 'N' ");
        corpoBuilder.append("and olm.olmConfirmada = 'S' ");

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and olm.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and olm.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
}
