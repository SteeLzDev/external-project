package com.zetra.econsig.persistence.query.formulariopesquisa;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaFormularioPesquisaQuery  extends HQuery {
    public String fpeNome;
    public boolean count = false;
    public boolean somentePublicados = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select fpe.fpeCodigo, fpe.fpeNome, fpe.fpeBloqueiaSistema, fpe.fpeDtCriacao, fpe.fpeDtFim, fpe.fpePublicado ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from FormularioPesquisa fpe WHERE 1=1 ");

        if (!TextHelper.isNull(fpeNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("fpe.fpeNome ", "fpeNome", fpeNome));
        }

        if (somentePublicados) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("fpe.fpePublicado ", "somentePublicados", somentePublicados));
        }

        corpoBuilder.append(" ORDER BY fpe.fpeDtCriacao desc");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(fpeNome)) {
            defineValorClausulaNomeada("fpeNome", fpeNome, query);
        }

        if (somentePublicados) {
            defineValorClausulaNomeada("somentePublicados", somentePublicados, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FPE_CODIGO,
                Columns.FPE_NOME,
                Columns.FPE_BLOQUEIA_SISTEMA,
                Columns.FPE_DT_CRIACAO,
                Columns.FPE_DT_FIM,
                Columns.FPE_PUBLICADO
        };
    }
}
