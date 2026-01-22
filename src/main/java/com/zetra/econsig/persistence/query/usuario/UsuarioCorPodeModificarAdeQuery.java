package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UsuarioCorPodeModificarAdeQuery</p>
 * <p>Description: Verifica se o usuário de COR pode modificar a consignação,
 * ou seja se é da consignatária dona do contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCorPodeModificarAdeQuery extends HQuery {

    public String usuCodigo;
    public String adeCodigo;
    public boolean podeAcessarCsa = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (podeAcessarCsa) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCor uco");
            corpoBuilder.append(" INNER JOIN uco.correspondente cor WHERE cor.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" AND uco.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            corpoBuilder.append(")");
        } else {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCor uco WHERE uco.corCodigo = ade.correspondente.corCodigo");
            corpoBuilder.append(" AND uco.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            corpoBuilder.append(")");
        }

        // Verifica o convênio do correspondente
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCor uco");
        corpoBuilder.append(" INNER JOIN uco.correspondente cor");
        corpoBuilder.append(" INNER JOIN cor.correspondenteConvenioSet crc");
        corpoBuilder.append(" WHERE crc.cnvCodigo = cnv.cnvCodigo");
        corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" AND uco.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        return query;
    }
}
