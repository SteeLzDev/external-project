package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusConvenioCorrespondenteQuery</p>
 * <p>Description: Lista o status de todos os convênios, que a consignatária possui,
 * (bloqueado se não existir), para um correspondente.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor Lucas, Leonel Martins
 */
public class ListaConvenioCorrespondenteByCsaQuery extends HQuery {

    public String csaCodigo;
    public boolean filtraPorCnvCodVerbaRef = false;
    public boolean filtraPorCnvCodVerbaFerias = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select distinct cor.corCodigo, ");

        if (filtraPorCnvCodVerbaRef) {
            corpoBuilder.append(" cnv.cnvCodVerbaRef ");
        } else if (filtraPorCnvCodVerbaFerias) {
            corpoBuilder.append(" cnv.cnvCodVerbaFerias ");
        } else {
            corpoBuilder.append(" cnv.cnvCodVerba ");
        }

        corpoBuilder.append(" AS CNV_COD_VERBA ");

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.correspondenteConvenioSet ccr ");
        corpoBuilder.append(" inner join ccr.correspondente cor ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and ccr.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cor.corAtivo <> ").append(CodedValues.STS_INDISP);
        corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and cor.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" order by cor.corCodigo, cnv.cnvCodVerba");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.COR_CODIGO,
                Columns.CNV_COD_VERBA
        };
    }
}
