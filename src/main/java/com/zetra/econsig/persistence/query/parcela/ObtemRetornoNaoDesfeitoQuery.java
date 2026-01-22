package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemRetornoNaoDesfeitoQuery</p>
 * <p>Description: Obtém contagem de registro de historico de conclusão de retorno não desfeito para periodo específico</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ObtemRetornoNaoDesfeitoQuery extends HNativeQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ObtemRetornoNaoDesfeitoQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select pex.pex_periodo from tb_periodo_exportacao pex ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append("inner join tb_orgao org on (pex.org_codigo = org.org_codigo) ");
        }

        corpoBuilder.append("where exists (select 1 from tb_historico_conclusao_retorno hcr ");
        corpoBuilder.append("where hcr.org_codigo = pex.org_codigo ");
        corpoBuilder.append("and hcr.hcr_desfeito = 'N' ");
        corpoBuilder.append("and hcr.hcr_data_fim is not null ");
        corpoBuilder.append("and hcr.hcr_periodo = pex.pex_periodo ");
        corpoBuilder.append(") ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and pex.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" group by pex.pex_periodo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PEX_PERIODO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
