package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemQuery</p>
 * <p>Description: Listagem de margens.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemQuery extends HQuery {

    public boolean isRaiz = false;
    public boolean alteracaoMultiplaAde = false;
    public String marCodigoPai = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT "
                     + "mar.marCodigo, "
                     + "mar.margemPai.marCodigo, "
                     + "mar.marDescricao, "
                     + "mar.marSequencia, "
                     + "mar.marPorcentagem, "
                     + "cast(mar.marExibeCse as char), "
                     + "cast(mar.marExibeOrg as char), "
                     + "cast(mar.marExibeSer as char), "
                     + "cast(mar.marExibeCsa as char), "
                     + "cast(mar.marExibeCor as char), "
                     + "cast(mar.marExibeSup as char), "
                     + "cast(mar.marTipoVlr  as char) ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM Margem mar ");

        if (isRaiz) {
            corpoBuilder.append(" WHERE mar.margemPai.marCodigo IS NULL");
        } else if (!TextHelper.isNull(marCodigoPai)) {
            corpoBuilder.append(" WHERE mar.margemPai.marCodigo ").append(criaClausulaNomeada("marCodigoPai", marCodigoPai));
        }
        if (alteracaoMultiplaAde) {
            corpoBuilder.append(" AND coalesce(mar.marExibeAltMultContratos, 'N') = 'S'");
        }
        corpoBuilder.append(" ORDER BY mar.margemPai.marCodigo, mar.marSequencia");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!isRaiz && !TextHelper.isNull(marCodigoPai)) {
            defineValorClausulaNomeada("marCodigoPai", marCodigoPai, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MAR_CODIGO,
                Columns.MAR_CODIGO_PAI,
                Columns.MAR_DESCRICAO,
                Columns.MAR_SEQUENCIA,
                Columns.MAR_PORCENTAGEM,
                Columns.MAR_EXIBE_CSE,
                Columns.MAR_EXIBE_ORG,
                Columns.MAR_EXIBE_SER,
                Columns.MAR_EXIBE_CSA,
                Columns.MAR_EXIBE_COR,
                Columns.MAR_EXIBE_SUP,
                Columns.MAR_TIPO_VLR
        };
    }
}
