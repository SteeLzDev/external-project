package com.zetra.econsig.persistence.query.consignataria;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ObtemAnexoConsignatariaQuery extends HQuery {

    public String csaCodigo;
    public String nomeArquivo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select axc.axcCodigo, " +
                "axc.csaCodigo, " +
                "axc.tarCodigo, " +
                "axc.usuCodigo, " +
                "axc.axcNome, " +
                "axc.axcAtivo, " +
                "axc.axcData, " +
                "axc.axcIpAcesso " +
                "from AnexoConsignataria axc ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" where 1 = 1");
        corpoBuilder.append(" and axc.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and axc.axcNome ").append(criaClausulaNomeada("axcNome", nomeArquivo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("axcNome", nomeArquivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.AXC_AXC_CODIGO,
                Columns.AXC_CSA_CODIGO,
                Columns.AXC_TAR_CODIGO,
                Columns.AXC_USU_CODIGO,
                Columns.AXC_AXC_NOME,
                Columns.AXC_AXC_ATIVO,
                Columns.AXC_AXC_DATA,
                Columns.AXC_AXC_IP_ACESSO
        };
    }
}
