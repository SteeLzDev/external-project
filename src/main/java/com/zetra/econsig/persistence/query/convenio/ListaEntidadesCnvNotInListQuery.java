package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConveniosQuery</p>
 * <p>Description: lista os códigos das entidades (órgão ou consignatária) que não estão presentes na lista ignoredCodList
 * e que possuem convênio com o serviço svcCodigo dado para a consgnatária ou órgão definidos por csaCodigo
 * e orgCodigo, respectivamente.
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEntidadesCnvNotInListQuery extends HQuery {
    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;
    public List<String> ignoredCodList;

    /**
     * lista os códigos das entidades (órgão ou consignatária) que não estão presentes na lista ignoredCodList
     * e que possuem convênio com o serviço svcCodigo dado para a consgnatária ou órgão definidos por csaCodigo
     * e orgCodigo, respectivamente.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!TextHelper.isNull(csaCodigo)) {
            corpo = "select org.orgCodigo," +
                    "cnv.cnvCodVerba," +
                    "org.orgNome ";
        } else if(!TextHelper.isNull(orgCodigo)) {
            corpo = "select csa.csaCodigo," +
                    "cnv.cnvCodVerba," +
                    "csa.csaNome ";
        }

        StringBuilder query = new StringBuilder(corpo);
        query.append("from ");

        if(!TextHelper.isNull(csaCodigo)) {
            query.append("Orgao org");
            query.append(" inner join org.convenioSet cnv ");
        } else if(!TextHelper.isNull(orgCodigo)) {
            query.append("Consignataria csa");
            query.append(" inner join csa.convenioSet cnv ");
        }

        query.append(" where 1=1 ");

        if (!TextHelper.isNull(svcCodigo)) {
            query.append("and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo",svcCodigo));
        }

        if(!TextHelper.isNull(csaCodigo)) {
            query.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo",csaCodigo));
            if(!TextHelper.isNull(orgCodigo)) {
                query.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo",orgCodigo));
            }
        } else if(!TextHelper.isNull(orgCodigo)) {
            query.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo",orgCodigo));
        }

        if(ignoredCodList != null && !ignoredCodList.isEmpty()) {
            if(!TextHelper.isNull(csaCodigo)) {
                query.append(" AND org.orgCodigo NOT IN (:ignoredCodList)");
            } else if(!TextHelper.isNull(orgCodigo)) {
                query.append(" AND csa.csaCodigo NOT IN (:ignoredCodList)");
            }
        }

        Query<Object[]> hQuery = instanciarQuery(session, query.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, hQuery);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, hQuery);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, hQuery);
        }

        if(ignoredCodList != null && !ignoredCodList.isEmpty() && (!TextHelper.isNull(csaCodigo) || !TextHelper.isNull(orgCodigo))) {
            defineValorClausulaNomeada("ignoredCodList", ignoredCodList, hQuery);
        }

        return hQuery;
    }

    @Override
    protected String[] getFields() {
        if (!TextHelper.isNull(csaCodigo)) {
            return new String[] {
                    Columns.ORG_CODIGO,
                    Columns.CNV_COD_VERBA,
                    Columns.ORG_NOME
            };
        } else {
            return new String[] {
                    Columns.CSA_CODIGO,
                    Columns.CNV_COD_VERBA,
                    Columns.CSA_NOME
            };
        }
    }
}
