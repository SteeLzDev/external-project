package com.zetra.econsig.persistence.query.usuario;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class FindEmailUsuarioRepeatQuery extends HQuery {
    public String emailUsuario;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String build = "SELECT usu.usuEmail, " +
                " usu.usuCodigo, " +
                " usuSup.cseCodigo, " +
                " usuSer.serCodigo, " +
                " usuCor.corCodigo, " +
                " usuOrg.orgCodigo, " +
                " usuCsa.csaCodigo, " +
                " usuCse.cseCodigo " +
                " FROM Usuario as usu ";

        StringBuilder corpoBuild = new StringBuilder(build);
        corpoBuild.append(" LEFT JOIN UsuarioSup usuSup");
        corpoBuild.append(" ON usu.usuCodigo = usuSup.usuCodigo ");
        corpoBuild.append(" LEFT JOIN UsuarioSer usuSer");
        corpoBuild.append(" ON usu.usuCodigo = usuSer.usuCodigo ");
        corpoBuild.append(" LEFT JOIN UsuarioCor usuCor");
        corpoBuild.append(" ON usu.usuCodigo = usuCor.usuCodigo ");
        corpoBuild.append(" LEFT JOIN UsuarioOrg usuOrg");
        corpoBuild.append(" ON usu.usuCodigo = usuOrg.usuCodigo ");
        corpoBuild.append(" LEFT JOIN UsuarioCsa usuCsa");
        corpoBuild.append(" ON usu.usuCodigo = usuCsa.usuCodigo ");
        corpoBuild.append(" LEFT JOIN UsuarioCse usuCse");
        corpoBuild.append(" ON usu.usuCodigo = usuCse.usuCodigo ");


        corpoBuild.append(" WHERE usu.usuEmail ").append(criaClausulaNomeada("usuEmail", emailUsuario));
        corpoBuild.append(" AND usu.stuCodigo ").append(criaClausulaNomeada("stuCodigo", CodedValues.STU_ATIVO));


        Query<Object[]> query = instanciarQuery(session, corpoBuild.toString());
        defineValorClausulaNomeada("usuEmail", emailUsuario, query);
        defineValorClausulaNomeada("stuCodigo", CodedValues.STU_ATIVO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.USU_EMAIL,
                Columns.USU_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO
        };
    }

}
