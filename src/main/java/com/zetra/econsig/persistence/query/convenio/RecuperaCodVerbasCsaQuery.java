package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RecuperaCodVerbasCsaQuery</p>
 * <p>Description: Recupera tods Códigos de Verbas, de referência e de férias  de uma Consignatária, podendo selecionar
 *                 se apenas os de convênios ativos ou não. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecuperaCodVerbasCsaQuery extends HQuery {

    public String csaCodigo;
    public boolean incluiCnvBloqueados = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
            "select distinct " +
            "cnv.cnvCodVerba, " +
            "coalesce(cnv.cnvCodVerbaRef, ''), " +
            "coalesce(cnv.cnvCodVerbaFerias, ''), " +
            "csa.csaNome, " +
            "csa.csaIdentificador, " +
            "svc.svcCodigo, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("where csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        if (!incluiCnvBloqueados) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA,
                Columns.CNV_COD_VERBA_REF,
                Columns.CNV_COD_VERBA_FERIAS,
                Columns.CSA_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO
        };
    }
}
