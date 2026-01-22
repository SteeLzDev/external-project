package com.zetra.econsig.persistence.query.movimento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegraValidacaoMovimentoQuery</p>
 * <p>Description: Lista as regras de validação de movimentação financeira</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegraValidacaoMovimentoQuery extends HQuery {

    public Boolean rvmAtivo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select rvmCodigo, " +
                "rvmIdentificador, " +
                "rvmDescricao, " +
                "rvmAtivo, " +
                "rvmJavaClassName, " +
                "rvmSequencia, " +
                "rvmInvalidaMovimento, " +
                "rvmLimiteErro, " +
                "rvmLimiteAviso " +
                "from RegraValidacaoMovimento " +
                (rvmAtivo != null ? (rvmAtivo.booleanValue() ? "where rvmAtivo = true " : "where rvmAtivo = false ") : "") +
                "order by rvmSequencia";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RVM_CODIGO             ,
                Columns.RVM_IDENTIFICADOR      ,
                Columns.RVM_DESCRICAO          ,
                Columns.RVM_ATIVO              ,
                Columns.RVM_JAVA_CLASS_NAME    ,
                Columns.RVM_SEQUENCIA          ,
                Columns.RVM_INVALIDA_MOVIMENTO ,
                Columns.RVM_LIMITE_ERRO        ,
                Columns.RVM_LIMITE_AVISO
        };
    }
}
