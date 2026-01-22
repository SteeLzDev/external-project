package com.zetra.econsig.persistence.query.funcao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CodedValues</p>
 * <p>Description: Constantes específicas do sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2022-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Douglas Neves, Leonel martins
 */
public class ListaFuncoesRegraTaxaQuery extends HQuery {

    public String funCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> funcoes = null;
        if (funCodigos == null || funCodigos.isEmpty()) {
            funcoes = List.of(
                    CodedValues.FUN_RES_MARGEM,
                    CodedValues.FUN_RENE_CONTRATO,
                    CodedValues.FUN_COMP_CONTRATO,
                    CodedValues.FUN_SIM_CONSIGNACAO,
                    CodedValues.FUN_ALT_CONSIGNACAO,
                    CodedValues.FUN_SIMULAR_RENEGOCIACAO,
                    CodedValues.FUN_SOLICITAR_PORTABILIDADE
            );
        } else {
            funcoes = List.of(
                    funCodigos.split(",")
            );
        }

        final String sql = "select fun.funCodigo, fun.funDescricao from Funcao fun where fun.funCodigo in (:funcoes) order by fun.funDescricao";

        Query<Object[]> query = instanciarQuery(session, sql);
        defineValorClausulaNomeada("funcoes", funcoes, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO
        };
    }
}
