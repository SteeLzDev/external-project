package com.zetra.econsig.persistence.query.ocorrenciaconsignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaOccBloqDesbloqVinculosByCsaQuery extends HQuery {

    public String csaCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        if (!count) {
            corpoBuilder.append("SELECT occ.occData, occ.occObs, occ.occIpAcesso, toc.tocDescricao, usu.usuCodigo, usu.usuLogin, usu.usuTipoBloq ");
        } else {
            corpoBuilder.append("SELECT count(*) as total ");
        }
        
        corpoBuilder.append("FROM OcorrenciaConsignataria occ ");
        corpoBuilder.append("INNER JOIN occ.tipoOcorrencia toc ");
        corpoBuilder.append("INNER JOIN occ.usuario usu ");
        corpoBuilder.append(" WHERE toc.tocCodigo in ('").append(CodedValues.TOC_BLOQUEIO_VINCULO).append("', '").append(CodedValues.TOC_DESBLOQUEIO_VINCULO).append("') ");
        corpoBuilder.append(" AND occ.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" ORDER BY occ.occData DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCC_DATA,
                Columns.OCC_OBS,
                Columns.OCC_IP_ACESSO,
                Columns.TOC_DESCRICAO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ
        };
    }

}
