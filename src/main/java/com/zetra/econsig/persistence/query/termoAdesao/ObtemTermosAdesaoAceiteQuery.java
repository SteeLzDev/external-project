package com.zetra.econsig.persistence.query.termoAdesao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * <p>Title: ListaServidorPorEmailQuery</p>
 * <p>Description: Retornar informações de servidores de acordo com o filtro.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTermosAdesaoAceiteQuery extends HQuery {

	public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder query = new StringBuilder();
        query.append(" SELECT tad.tadCodigo, tad.tadTitulo, tad.tadTexto, ltu.ltuData, tad.tadData, tad.tadSequencia, tad.tadHtml, tad.tadPermiteLerDepois, tad.tadPermiteRecusar,  ltu.ltuTermoAceito, tad.funCodigo,");
        query.append(" CASE WHEN ltu.ltuTermoAceito IS NULL OR ltu.ltuData < tad.tadData THEN 'N' ELSE 'S' END AS aceiteValido ");
        query.append(" FROM TermoAdesao tad ");
        query.append(" LEFT OUTER JOIN tad.leituraTermoUsuarioSet ltu ");
        query.append(" WITH (tad.tadData <= ltu.ltuData AND ltu.usuCodigo = :usuCodigo AND ltu.ltuTermoAceito IN ('").append(CodedValues.TPC_SIM).append("', '").append(CodedValues.TPC_NAO).append("')) ");
        query.append(" WHERE 1 = 1 ");

        if (responsavel.isSer()) {
            query.append(" AND tad.tadExibeSer = '").append(CodedValues.TPC_SIM).append("'");
        }

        query.append(" ORDER BY tad.tadSequencia ASC ");

        Query<Object[]> bean = instanciarQuery(session, query.toString());

        defineValorClausulaNomeada("usuCodigo", responsavel.getUsuCodigo(), bean);

        return bean;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TAD_CODIGO,
                Columns.TAD_TITULO,
                Columns.TAD_TEXTO,
                Columns.LTU_DATA,
                Columns.TAD_DATA,
                Columns.TAD_SEQUENCIA,
                Columns.TAD_HTML,
                Columns.TAD_PERMITE_LER_DEPOIS,
                Columns.TAD_PERMITE_RECUSAR,
                Columns.LTU_TERMO_ACEITO,
                Columns.FUN_CODIGO,
                "aceiteValido"
        };
    }
}
