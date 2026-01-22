package com.zetra.econsig.persistence.query.movimento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOrgaoExpMovQuery</p>
 * <p>Description: Lista os órgãos para a exportação de movimentação financeira</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOrgaoExpMovQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "org.orgCodigo, " +
                       "org.orgIdentificador, " +
                       "org.orgNome, " +
                       "org.orgResponsavel, " +
                       "est.estCodigo, " +
                       "est.estIdentificador, " +
                       "COALESCE(STR(DAY(MAX(hie.hieData))) || '/' || STR(MONTH(MAX(hie.hieData))) || '/' || STR(YEAR(MAX(hie.hieData))), '00/00/0000') AS HIE_DATA " +
                       "from Orgao org " +
                       "inner join org.estabelecimento est " +
                       "left outer join org.historicoExportacaoSet hie " +
                       "group by org.orgCodigo, org.orgIdentificador, org.orgNome, org.orgResponsavel, est.estCodigo, est.estIdentificador " +
                       "order by max(hie.hieData), est.estIdentificador, org.orgNome";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_RESPONSAVEL,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.HIE_DATA
        };
    }
}
