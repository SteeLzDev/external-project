package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesPermitidasPapelQuery</p>
 * <p>Description: Lista as funções permitidas para criação de um novo usuário, de acordo com a tabela papel_função. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesPermitidasPapelQuery extends HQuery {
    public String papCodigoOrigem;
    public String papCodigoDestino;

    /**
     * Lista as funções permitidas para criação de um novo usuário, de acordo com a tabela papel_função.
     * O parâmetro papCodigoOrigem é o papel do usuário que está criando/editando
     * o usuário com o papel papCodigoDestino. As funções bloqueadas para repasse não serão listadas.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
            "select " +
            "fun.funCodigo, " +
            "fun.funDescricao, " +
            "grf.grfCodigo, " +
            "grf.grfDescricao, " +
            "fun.funRestritaNca ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.papelFuncaoSet pf");
        corpoBuilder.append(" inner join fun.grupoFuncao grf");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(papCodigoDestino)) {
            corpoBuilder.append(" AND pf.papCodigo ").append(criaClausulaNomeada("papCodigoDestino", papCodigoDestino));
        }
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM BloqueioRepasseFuncao brf WHERE ");
        corpoBuilder.append("brf.funcao.funCodigo = fun.funCodigo AND ");
        corpoBuilder.append("brf.papelDestino.papCodigo = pf.papCodigo AND ");
        corpoBuilder.append("brf.papelOrigem.papCodigo ").append(criaClausulaNomeada("papCodigoOrigem", papCodigoOrigem)).append(")");
        corpoBuilder.append(" ORDER BY cast(case when grf.grfCodigo = '" + CodedValues.GRUPO_FUNCAO_ADMINISTRADOR + "' then '99' else grf.grfCodigo end as int), fun.funDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(papCodigoDestino)) {
            defineValorClausulaNomeada("papCodigoDestino", papCodigoDestino, query);
        }

        if (!TextHelper.isNull(papCodigoOrigem)) {
            defineValorClausulaNomeada("papCodigoOrigem", papCodigoOrigem, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.FUN_GRF_CODIGO,
                Columns.GRF_DESCRICAO,
                Columns.FUN_RESTRITA_NCA
        };
    }
}
