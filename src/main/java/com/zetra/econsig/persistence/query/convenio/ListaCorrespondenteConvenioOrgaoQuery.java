package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCorrespondenteConvenioOrgaoQuery</p>
 * <p>Description: Lista os correspondentes que estão relacionados para convênio de um
 * serviço, de acordo com os convênios ativos de sua consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCorrespondenteConvenioOrgaoQuery extends HNativeQuery {

    public String cnvCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ").append(Columns.COR_CODIGO).append(", ");
        corpoBuilder.append(" ").append(Columns.COR_NOME).append(", ").append(Columns.COR_IDENTIFICADOR).append(", ");
        corpoBuilder.append(" ").append(Columns.CNV_CODIGO).append(", coalesce(").append(Columns.CRC_SCV_CODIGO).append(", '2') as STATUS ");
        corpoBuilder.append(" from ").append(Columns.TB_CONVENIO).append(" ");
        //corpoBuilder.append(" inner join tb_orgao orgao1_ on convenio0_.ORG_CODIGO=orgao1_.ORG_CODIGO and (orgao1_.ORG_CODIGO in (select org_codigo from tb_orgao where org_identificador = '20')) ");
        corpoBuilder.append(" inner join ").append(Columns.TB_CONSIGNATARIA).append(" on ").append(Columns.CNV_CSA_CODIGO).append("=").append(Columns.CSA_CODIGO).append(" ");
        corpoBuilder.append(" inner join ").append(Columns.TB_CORRESPONDENTE).append(" on ").append(Columns.CSA_CODIGO).append("=").append(Columns.COR_CSA_CODIGO).append(" ");
        corpoBuilder.append(" left outer join ").append(Columns.TB_CORRESPONDENTE_CONVENIO).append(" on ");
        corpoBuilder.append(Columns.CNV_CODIGO).append("=").append(Columns.CRC_CNV_CODIGO);
        corpoBuilder.append(" and ").append(Columns.COR_CODIGO).append("=").append(Columns.CRC_COR_CODIGO).append(" ");
        corpoBuilder.append(" where ").append(Columns.CNV_SCV_CODIGO).append(" ='").append(CodedValues.SCV_ATIVO).append("' ");
        corpoBuilder.append(" and ").append(Columns.COR_ATIVO).append(" <> '").append(CodedValues.STS_INDISP).append("' ");
        corpoBuilder.append(" and ").append(Columns.CNV_CODIGO).append(" ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        //corpoBuilder.append(" and consignata2_.CSA_CODIGO in (select csa_codigo from tb_consignataria where csa_identificador = '039') ");
        corpoBuilder.append(" order by ").append(Columns.COR_NOME);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.COR_CODIGO,
                Columns.COR_NOME,
                Columns.COR_IDENTIFICADOR,
                Columns.CNV_CODIGO,
                "STATUS"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
