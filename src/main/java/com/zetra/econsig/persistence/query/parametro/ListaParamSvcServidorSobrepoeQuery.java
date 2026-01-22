package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSvcServidorSobrepoe</p>
 * <p>Description: Lista parâmetros de serviços do Servidor que Sobrepoe.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 27242 $
 * $Date: 2019-07-16 17:06:28 -0300 (ter, 16 jul 2019) $
 */
public class ListaParamSvcServidorSobrepoeQuery extends HQuery {
    public String svcCodigo;
    public String rseCodigo;
    public List<String> tpsCodigos;
    public String psrVlr;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "psr.tipoParamSvc.tpsCodigo, " +
                       "psr.psrVlr, " +
                       "psr.registroServidor.rseCodigo," +
                       "psr.servico.svcCodigo";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM ParamServicoRegistroSer psr ");
        corpoBuilder.append(" WHERE 1 = 1 ");
        corpoBuilder.append(" AND psr.tipoParamSvc.tpsPodeSobreporRse =" + "'" + CodedValues.TPC_SIM + "'");

        if (!TextHelper.isNull(psrVlr)) {
            corpoBuilder.append(" AND psr.psrVlr ").append(criaClausulaNomeada("psrVlr", psrVlr));
        }

        if (svcCodigo != null && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" AND psr.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (rseCodigo != null && !rseCodigo.isEmpty()) {
            corpoBuilder.append(" AND psr.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            corpoBuilder.append(" AND psr.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigos));
        }

       Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(psrVlr)) {
            defineValorClausulaNomeada("psrVlr", psrVlr, query);
        }

        if (svcCodigo != null && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (rseCodigo != null && !rseCodigo.isEmpty()) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSR_TPS_CODIGO,
                Columns.PSR_VLR,
                Columns.PSR_RSE_CODIGO,
                Columns.PSR_SVC_CODIGO,
        };
    }
}
