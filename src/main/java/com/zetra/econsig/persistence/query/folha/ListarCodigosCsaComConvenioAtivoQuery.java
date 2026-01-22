package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListarCsaBlocosProcessamentoQuery</p>
 * <p>Description: Lista as consignatárias que tiveram blocos mapeados em seus convênios</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarCodigosCsaComConvenioAtivoQuery extends HQuery  {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT csa.csaCodigo ");
        corpoBuilder.append("FROM Consignataria csa ");
        corpoBuilder.append("WHERE COALESCE(csa.csaAtivo, :csaAtiva) <> :csaExcluida ");
        corpoBuilder.append("AND EXISTS (");
        corpoBuilder.append("SELECT 1 FROM csa.convenioSet cnv ");
        if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append("INNER JOIN cnv.orgao org ");
        }
        corpoBuilder.append("WHERE cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade)) {
            corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaAtiva", CodedValues.STS_ATIVO, query);
        defineValorClausulaNomeada("csaExcluida", CodedValues.STS_INDISP, query);

        if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
