package com.zetra.econsig.persistence.query.coeficiente;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTaxasJurosComDataIniVigMaiorQuery</p>
 * <p>Description: Listagem de Coeficientes com data de início de vigência maior
 * do que o parâmetro.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTaxasJurosComDataIniVigMaiorQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public Date cftDataIniVig;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String fields = "select cft.cftCodigo, pzc.przCsaCodigo, cft.cftDia, prz.przVlr, cft.cftVlr, cft.cftVlrMinimo, "
                      + "cft.cftDataIniVig, cft.cftDataFimVig, cft.cftDataCadastro ";
        StringBuilder corpoBuilder = new StringBuilder(fields);

        corpoBuilder.append(" FROM Prazo prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" WHERE prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" AND cft.cftDataIniVig >= :cftDataIniVig");

        corpoBuilder.append(" ORDER BY prz.przVlr");

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("cftDataIniVig", cftDataIniVig, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFT_CODIGO,
                Columns.CFT_PRZ_CSA_CODIGO,
                Columns.CFT_DIA,
                Columns.PRZ_VLR,
                Columns.CFT_VLR,
                Columns.CFT_VLR_MINIMO,
                Columns.CFT_DATA_INI_VIG,
                Columns.CFT_DATA_FIM_VIG,
                Columns.CFT_DATA_CADASTRO
        };
    }
}
