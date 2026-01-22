package com.zetra.econsig.persistence.query.texto;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCampoSistemaQuery</p>
 * <p>Description: Listagem de fieldspermission</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCampoSistemaQuery extends HQuery {

    public String casChave;
    public AcessoSistema responsavel;
    public boolean somenteCamposEditaveis = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // campos editáveis
        List<String> casValoresEditaveis = new ArrayList<>();
        casValoresEditaveis.add(CodedValues.CAS_SIM);
        casValoresEditaveis.add(CodedValues.CAS_OBRIGATORIO);

        StringBuilder corpo = new StringBuilder("select cas.casChave, cas.casValor from CampoSistema cas where 1=1 ");

        if (responsavel != null && responsavel.getCodigoEntidade() != null) {
            if(casChave.charAt(3) != '.') {
                casChave = responsavel.getTipoEntidade().toLowerCase() + "." + (TextHelper.isNull(casChave) ? "" : casChave);
            }
        }

        if(!TextUtils.isEmpty(casChave)){
            corpo.append(" and cas.casChave like :casChave");
        }

        if (somenteCamposEditaveis) {
            corpo.append(" and cas.casValor in ('").append(TextHelper.join(casValoresEditaveis, "','")).append("')");
        }

        corpo.append(" order by cas.casChave");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if(!TextUtils.isEmpty(casChave)){
            if (DAOFactory.isOracle()) {
                query.setParameter("casChave", casChave + ".*");
            } else {
                query.setParameter("casChave", casChave + "%");
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAS_CHAVE,
                Columns.CAS_VALOR
        };
    }
}

