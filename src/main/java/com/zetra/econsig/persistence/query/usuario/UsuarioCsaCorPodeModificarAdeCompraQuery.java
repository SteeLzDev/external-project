package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UsuarioCsaCorPodeModificarAdeCompraQuery</p>
 * <p>Description: Verifica se o usuário de CSA/COR pode modificar a consignação em relacionamento para compra,
 * ou seja se é da consignatária compradora do contrato.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCsaCorPodeModificarAdeCompraQuery extends HQuery {

    public String usuCodigo;
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM RelacionamentoAutorizacao rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("WHERE rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND rad.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCsa uca WHERE uca.csaCodigo = cnv.consignataria.csaCodigo");
        corpoBuilder.append(" AND uca.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
        corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioCor uco");
        corpoBuilder.append(" INNER JOIN uco.correspondente cor WHERE cor.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
        corpoBuilder.append(" AND uco.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        return query;
    }
}
