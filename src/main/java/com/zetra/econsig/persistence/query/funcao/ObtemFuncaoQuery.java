package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemFuncaoQuery</p>
 * <p>Description: Recupera dados de uma função dado pelo seu código. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemFuncaoQuery extends HQuery {
    public String funCodigo;
    public String funExigeTmo;
    public String funExigeSegundaSenha = null;
    public Boolean funRestritaNca = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
                "select " +
                        "fun.funCodigo, " +
                        "fun.funDescricao, " +
                        "fun.funPermiteBloqueio, " +
                        "fun.funExigeTmo, " +
                        "fun.funExigeSegundaSenhaCse, " +
                        "fun.funExigeSegundaSenhaSup, " +
                        "fun.funExigeSegundaSenhaOrg, " +
                        "fun.funExigeSegundaSenhaCsa, " +
                        "fun.funExigeSegundaSenhaCor, " +
                        "grf.grfCodigo, " +
                        "grf.grfDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.grupoFuncao grf");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(funCodigo)) {
            corpoBuilder.append(" AND fun.funCodigo ").append(criaClausulaNomeada("funCodigo",funCodigo));
        }

        if (!TextHelper.isNull(funExigeTmo)) {
            corpoBuilder.append(" AND fun.funExigeTmo ").append(criaClausulaNomeada("funExigeTmo",funExigeTmo));
        }

        if (funExigeSegundaSenha != null) {
            String operacaoExigeSenha = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO;
            if (funExigeSegundaSenha.equals(CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)) {
                operacaoExigeSenha = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM;
            } else if (funExigeSegundaSenha.equals(CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)) {
                operacaoExigeSenha = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA;
            }

            corpoBuilder.append(" AND (fun.funExigeSegundaSenhaCse = '").append(operacaoExigeSenha).append("'");
            corpoBuilder.append(" OR fun.funExigeSegundaSenhaSup = '").append(operacaoExigeSenha).append("'");
            corpoBuilder.append(" OR fun.funExigeSegundaSenhaOrg = '").append(operacaoExigeSenha).append("'");
            corpoBuilder.append(" OR fun.funExigeSegundaSenhaCsa = '").append(operacaoExigeSenha).append("'");
            corpoBuilder.append(" OR fun.funExigeSegundaSenhaCor = '").append(operacaoExigeSenha).append("')");
        }

        if (funRestritaNca != null) {
            if (funRestritaNca) {
                corpoBuilder.append(" AND fun.funRestritaNca = 'S' ");
            } else {
                corpoBuilder.append(" AND fun.funRestritaNca = 'N' ");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(funCodigo)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        if (!TextHelper.isNull(funExigeTmo)) {
            defineValorClausulaNomeada("funExigeTmo", funExigeTmo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.FUN_PERMITE_BLOQUEIO,
                Columns.FUN_EXIGE_TMO,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_COR,
                Columns.GRF_CODIGO,
                Columns.GRF_DESCRICAO
        };
    }
}
