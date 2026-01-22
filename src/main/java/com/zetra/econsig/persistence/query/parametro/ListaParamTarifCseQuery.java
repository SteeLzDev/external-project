package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamTarifCseQuery</p>
 * <p>Description: Listagem dos parâmetros de tarifação de um determinado serviço.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamTarifCseQuery extends HQuery {

    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "pcv.consignante.cseCodigo, " +
                "pcv.tipoParamTarifCse.tptCodigo, " +
                "pcv.pcvCodigo, " +
                "pcv.pcvAtivo, " +
                "pcv.pcvDataIniVig, " +
                "pcv.pcvDataFimVig, " +
                "pcv.pcvVlr, " +
                "pcv.pcvVlrIni, " +
                "pcv.pcvVlrFim, " +
                "pcv.pcvDecimais, " +
                "pcv.pcvFormaCalc, " +
                "pcv.pcvBaseCalc ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamTarifConsignante pcv ");
        corpoBuilder.append(" where pcv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PCV_CSE_CODIGO,
                Columns.PCV_TPT_CODIGO,
                Columns.PCV_CODIGO,
                Columns.PCV_ATIVO,
                Columns.PCV_DATA_INI_VIG,
                Columns.PCV_DATA_FIM_VIG,
                Columns.PCV_VLR,
                Columns.PCV_VLR_INI,
                Columns.PCV_VLR_FIM,
                Columns.PCV_DECIMAIS,
                Columns.PCV_FORMA_CALC,
                Columns.PCV_BASE_CALC
        };
    }
}
