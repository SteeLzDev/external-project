package com.zetra.econsig.persistence.query.beneficios;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery</p>
 * <p>Description: Query que busca o relaciona de beneficio por serviço de acordo com o correspondente.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery extends HQuery {

    public String csaCodigo;
    public List<String> corCodigos;
    public String nseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ben.benCodigo,");
        sql.append(" ben.benDescricao, ");
        sql.append(" ben.benTextoCor, ");
        sql.append(" ben.benImagemBeneficio, ");
        sql.append(" ben.benLinkBeneficio, ");
        sql.append(" ben.benTextoLinkBeneficio, ");
        sql.append(" cor.corCodigo, ");
        sql.append(" bse.svcCodigo ");
        sql.append(" FROM Beneficio ben");
        sql.append(" INNER JOIN ben.correspondente cor");
        sql.append(" LEFT JOIN ben.beneficioServicoSet bse");
        sql.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND ben.consignataria.csaCodigo = :csaCodigo");
        }

        if (!TextHelper.isNull(corCodigos)) {
            sql.append(" AND cor.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            sql.append(" AND ben.naturezaServico.nseCodigo = :nseCodigo");
        }

        sql.append(" order by cor.corNome, ben.benDescricao ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigos)) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BEN_CODIGO,
                Columns.BEN_DESCRICAO,
                Columns.BEN_TEXTO_COR,
                Columns.BEN_IMAGEM_BENEFICIO,
                Columns.BEN_LINK_BENEFICIO,
                Columns.BEN_TEXTO_LINK_BENEFICIO,
                Columns.COR_CODIGO,
                Columns.SVC_CODIGO
        };
    }
}
