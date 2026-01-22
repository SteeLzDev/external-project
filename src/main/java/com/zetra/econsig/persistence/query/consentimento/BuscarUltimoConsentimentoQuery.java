

package com.zetra.econsig.persistence.query.consentimento;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class BuscarUltimoConsentimentoQuery extends HQuery {

    public String cpf;
    public String tadCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ")
           .append(" ltu.ltuCodigo, ltu.ltuData, ltu.ltuTermoAceito, usu.usuCodigo ")
           .append(" FROM Servidor ser ")
           .append(" INNER JOIN ser.usuario usu ")
           .append(" INNER JOIN LeituraTermoUsuario ltu ON ltu.usuCodigo = usu.usuCodigo")
           .append(" WHERE ser.serCpf ").append(criaClausulaNomeada("serCpf", cpf))  
           .append(" AND ltu.tadCodigo ").append(criaClausulaNomeada("tadCodigo", tadCodigo))  
           .append(" ORDER BY ltu.ltuData DESC ");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("serCpf", cpf, query);
        defineValorClausulaNomeada("tadCodigo", tadCodigo, query);
        query.setMaxResults(1);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.LTU_CODIGO,
            Columns.LTU_DATA,
            Columns.LTU_TERMO_ACEITO,
            Columns.USU_CODIGO
        };
    }

    
}
