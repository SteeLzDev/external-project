package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: VerificaAdeProvisionamentoQuery</p>
 * <p>Description: retorna o código de contrato e o código do seu respectivo serviço, se este for de natureza de provisionamento de margem.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificaAdeProvisionamentoQuery extends HQuery {

    public List<String> adeCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
                "SELECT " +
                "ade.adeCodigo, " +
                "relOrigem.servicoBySvcCodigoOrigem.svcCodigo, " +
                "relDest.servicoBySvcCodigoDestino.svcCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("LEFT OUTER JOIN svc.relacionamentoServicoByOrigemSet relOrigem WITH relOrigem.tipoNatureza.tntCodigo = '");
        corpoBuilder.append(CodedValues.TNT_CARTAO + "' ");
        corpoBuilder.append("LEFT OUTER JOIN svc.relacionamentoServicoByDestinoSet relDest WITH relDest.tipoNatureza.tntCodigo = '");
        corpoBuilder.append(CodedValues.TNT_CARTAO + "' ");
        corpoBuilder.append("WHERE ");

        corpoBuilder.append(" ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (adeCodigos != null) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                "SVC_CODIGO_ORIGEM",
                "SVC_CODIGO_DESTINO"
        };
    }

}
