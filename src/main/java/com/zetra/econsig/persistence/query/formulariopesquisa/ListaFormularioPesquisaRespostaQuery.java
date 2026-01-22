package com.zetra.econsig.persistence.query.formulariopesquisa;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaFormularioPesquisaRespostaQuery  extends HQuery {
    public String fpeCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select fpr.fprCodigo, fpr.usuario.usuCodigo, fpr.fprDtCriacao ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from FormularioPesquisaResposta fpr WHERE 1=1 ");

        if (!TextHelper.isNull(fpeCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("fpr.formularioPesquisa.fpeCodigo ", "fpeCodigo", fpeCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(fpeCodigo)) {
            defineValorClausulaNomeada("fpeCodigo", fpeCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (!count) {
            return new String[] {
                    Columns.FPR_CODIGO,
                    Columns.FPR_USU_CODIGO,
                    Columns.FPR_DT_CRIACAO
            };
        } else {
            return new String[] { "total" };
        }

    }
}
