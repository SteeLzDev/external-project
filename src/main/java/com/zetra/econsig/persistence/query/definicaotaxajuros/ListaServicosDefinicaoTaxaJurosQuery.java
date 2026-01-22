package com.zetra.econsig.persistence.query.definicaotaxajuros;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: PesquisarDefinicaoTaxaJurosQuery</p>
 * <p>Description: Lista de serviços de definição de regra de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26246 $
 * $Date: 2019-04-12 10:34:49 -0200 (qua, 10 abr 2019) $
 */
public class ListaServicosDefinicaoTaxaJurosQuery extends HQuery {

    public String svcCodigo;

    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append(" select servico.svcCodigo");
        corpo.append(" from DefinicaoTaxaJuros ");
        corpo.append(" WHERE dtjDataVigenciaFim IS NULL and consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpo.append(" group by servico.svcCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (csaCodigo != null) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

}
