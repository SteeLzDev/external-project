package com.zetra.econsig.persistence.query.termoAdesao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ObterTermoAdesaoComLeituraQuery extends HQuery {
    public String tadCodigo;
    public String usuCodigo;
    public String ltuCodigo;
    @Override
protected Query preparar(Session session) throws HQueryException {
    final StringBuilder sql = new StringBuilder();
    sql.append("SELECT ")
       .append(" tad.tadCodigo, ")
       .append(" tad.tadVersaoTermo, ")
       .append(" tad.tadEnviaApiConsentimento, ")
       .append(" ltu.ltuCodigo, ")
       .append(" ltu.ltuData, ")
       .append(" ltu.ltuTermoAceito ")
       .append(" FROM TermoAdesao tad ")
       .append(" INNER JOIN LeituraTermoUsuario ltu ON ltu.tadCodigo = tad.tadCodigo ")
       .append(" WHERE tad.tadCodigo = :tadCodigo ")
       .append(" AND ltu.usuCodigo = :usuCodigo ")
       .append(" AND ltu.ltuCodigo = :ltuCodigo ")
       .append(" ORDER BY ltu.ltuData DESC");
 
    final Query query = instanciarQuery(session, sql.toString());
    defineValorClausulaNomeada("tadCodigo", tadCodigo, query);
    defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
    defineValorClausulaNomeada("ltuCodigo", ltuCodigo, query);
    query.setMaxResults(1);
    return query;
}
    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.TAD_CODIGO,
            Columns.TAD_VERSAO_TERMO,
            Columns.TAD_ENVIA_API_CONSENTIMENTO,
            Columns.LTU_CODIGO,
            Columns.LTU_DATA,
            Columns.LTU_TERMO_ACEITO
        };
    }
}