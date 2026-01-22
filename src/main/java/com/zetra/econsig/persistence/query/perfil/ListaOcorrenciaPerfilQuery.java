package com.zetra.econsig.persistence.query.perfil;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaPerfilQuery</p>
 * <p>Description: Listagem de Ocorrências do Perfil selecionado</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2020-12-14 09:27:49 -0200 (seg, 14 dez 2020) $
 */
public class ListaOcorrenciaPerfilQuery extends HQuery {

    public boolean count = false;
    public String perCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder = new StringBuilder("SELECT count(*)");
        } else {
            corpoBuilder.append(" SELECT opr.oprData, ");
            corpoBuilder.append(" usu.usuLogin, ");
            corpoBuilder.append(" toc.tocDescricao, ");
            corpoBuilder.append(" opr.oprObs, ");
            corpoBuilder.append(" opr.oprIpAcesso ");
        }

        corpoBuilder.append(" FROM OcorrenciaPerfil opr ");
        corpoBuilder.append(" INNER JOIN opr.usuario usu ");
        corpoBuilder.append(" INNER JOIN opr.tipoOcorrencia toc ");
        corpoBuilder.append(" WHERE 1=1 ");
        
        if (!TextHelper.isNull(perCodigo)) {
            corpoBuilder.append("AND opr.perfil.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
        }
        
        corpoBuilder.append(" ORDER BY opr.oprData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(perCodigo)) {
            defineValorClausulaNomeada("perCodigo", perCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OPR_DATA,
                Columns.USU_LOGIN,
                Columns.TOC_DESCRICAO,
                Columns.OPR_OBS,
                Columns.OPR_IP_ACESSO
        };
    }

}
