package com.zetra.econsig.persistence.query.folha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalBlocosProcessamentoQuery</p>
 * <p>Description: Obtém o total de blocos por status e/ou tipo</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalBlocosProcessamentoQuery extends HQuery  {

    public String tipoEntidade;
    public String codigoEntidade;

    public List<String> tbpCodigos;
    public List<String> sbpCodigos;
    public Boolean convenioMapeado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE 1=1 ");

        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.tipoBlocoProcessamento.tbpCodigo ").append(criaClausulaNomeada("tbpCodigos", tbpCodigos));
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));
        }
        if (convenioMapeado != null) {
            if (convenioMapeado) {
                corpoBuilder.append(" AND bpr.convenio.cnvCodigo IS NOT NULL ");
            } else {
                corpoBuilder.append(" AND bpr.convenio.cnvCodigo IS NULL ");
            }
        }

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("tbpCodigos", tbpCodigos, query);
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
