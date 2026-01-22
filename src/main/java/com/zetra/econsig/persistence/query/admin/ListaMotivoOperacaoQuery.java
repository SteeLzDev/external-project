package com.zetra.econsig.persistence.query.admin;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMotivoOperacaoQuery</p>
 * <p>Description: Listagem de Tipo de Motivo da Operação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMotivoOperacaoQuery extends HQuery {

    public List<String> tenCodigos;

    public Short tmoAtivo;
    public String acaCodigo;
    public String tmoCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" select tmo.tmoCodigo, ");
        corpo.append(" tmo.tmoDescricao, ");
        corpo.append(" tmo.tmoIdentificador, ");
        corpo.append(" tmo.tmoAtivo, ");
        corpo.append(" concat(tmo.tmoCodigo, ';', tmo.tmoDescricao) as CODIGO_NOME, ");
        corpo.append(" tmo.tipoEntidade.tenCodigo, ");
        corpo.append(" tmo.tipoEntidade.tenDescricao, ");
        corpo.append(" tmo.tmoExigeObs, ");
        corpo.append(" tmo.tmoDecisaoJudicial, ");
        corpo.append(" tmo.acao.acaCodigo ");
        corpo.append(" from TipoMotivoOperacao tmo ");
        corpo.append(" where 1 = 1 ");

        if (tenCodigos != null && !tenCodigos.isEmpty()) {
            corpo.append(" and tmo.tipoEntidade.tenCodigo ").append(criaClausulaNomeada("tenCodigos", tenCodigos));
        }

        if (tmoAtivo != null) {
            corpo.append(" and tmo.tmoAtivo ").append(criaClausulaNomeada("tmoAtivo", tmoAtivo));
        }

        if (acaCodigo != null) {
            corpo.append(" and tmo.acao.acaCodigo ").append(criaClausulaNomeada("acaCodigo", acaCodigo));
        }

        if (tmoCodigo != null) {
            corpo.append(" and tmo.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigo));
        }

        corpo.append(" order by tmo.tipoEntidade.tenCodigo, tmo.tmoIdentificador asc");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (tenCodigos != null && !tenCodigos.isEmpty()) {
            defineValorClausulaNomeada("tenCodigos", tenCodigos, query);
        }

        if (tmoAtivo != null) {
            defineValorClausulaNomeada("tmoAtivo", tmoAtivo, query);
        }

        if (acaCodigo != null) {
            defineValorClausulaNomeada("acaCodigo", acaCodigo, query);
        }

        if (tmoCodigo != null) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TMO_CODIGO,
                Columns.TMO_DESCRICAO,
                Columns.TMO_IDENTIFICADOR,
                Columns.TMO_ATIVO,
                "CODIGO_NOME",
                Columns.TEN_CODIGO,
                Columns.TEN_DESCRICAO,
                Columns.TMO_EXIGE_OBS,
                Columns.TMO_DECISAO_JUDICIAL,
                Columns.TMO_ACA_CODIGO
        };
    }
}
