package com.zetra.econsig.persistence.query.beneficios.provedor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaProvedorBeneficioQuery extends HQuery {

    public String proCodigo;
    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder builder = new StringBuilder();

        builder.append(" select ");
        builder.append(" pro.proCodigo, ");
        builder.append(" pro.proTituloDetalheTopo, ");
        builder.append(" pro.proTituloDetalheRodape, ");
        builder.append(" pro.proTextoDetalheTopo, ");
        builder.append(" pro.proTextoDetalheRodape, ");
        builder.append(" pro.proTituloListaBeneficio, ");
        builder.append(" pro.proLinkBeneficio, ");
        builder.append(" pro.proImagemBeneficio, ");
        builder.append(" pro.proAgrupa, ");
        builder.append(" csa.csaCodigo, ");
        builder.append(" csa.csaNome, ");
        builder.append(" cor.corCodigo, ");
        builder.append(" cor.corNome ");
        builder.append(" from ProvedorBeneficio pro ");
        builder.append(" inner join pro.consignataria csa ");
        builder.append(" left outer join pro.correspondente cor ");

        builder.append(" where 1=1 ");

        if (!TextHelper.isNull(proCodigo)) {
            builder.append(" and pro.proCodigo ").append(criaClausulaNomeada("proCodigo", proCodigo));
        }

        // O motivo do corCodigo ser is not null e a busca ser por csa é pela regra de negócio definir que os provedores das Csas serem os correspondentes nesta situação.
        if (!TextHelper.isNull(csaCodigo)) {
            builder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            builder.append(" and pro.corCodigo IS NOT NULL");

            builder.append(" order by cor.corNome ");
        }

        Query<Object[]> query = instanciarQuery(session, builder.toString());

        if (!TextHelper.isNull(proCodigo)) {
            defineValorClausulaNomeada("proCodigo", proCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRO_CODIGO,
                Columns.PRO_TITULO_DETALHE_TOPO,
                Columns.PRO_TITULO_DETALHE_RODAPE,
                Columns.PRO_TEXTO_DETALHE_TOPO,
                Columns.PRO_TEXTO_DETALHE_RODAPE,
                Columns.PRO_TITULO_LISTA_BENEFICIO,
                Columns.PRO_LINK_BENEFICIO,
                Columns.PRO_IMAGEM_BENEFICIO,
                Columns.PRO_AGRUPA,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.COR_CODIGO,
                Columns.COR_NOME
        };
    }

}
