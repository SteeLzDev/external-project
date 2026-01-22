package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaOrgaoQuery</p>
 * <p>Description: lista ocorrências da entidade órgão.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaOrgaoQuery extends HQuery {

    public boolean count = false;
    public String orgCodigo;
    public String tocCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                + "toc.tocDescricao, "
                + "oor.orgao.orgCodigo, "
                + "oor.oorCodigo, "
                + "oor.oorObs, "
                + "oor.oorData, "
                + "oor.oorIpAcesso, "
                + "usu.usuCodigo, "
                + "usu.usuLogin, "
                + "usu.usuTipoBloq ";

        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaOrgao oor ");
        corpoBuilder.append("inner join oor.tipoOcorrencia toc ");
        corpoBuilder.append("inner join oor.usuario usu ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append("and oor.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" and oor.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        }

        if (!count) {
            corpoBuilder.append(" order by oor.oorData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.OOR_ORG_CODIGO,
                Columns.OOR_CODIGO,
                Columns.OOR_OBS,
                Columns.OOR_DATA,
                Columns.OOR_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ
        };
    }
}
