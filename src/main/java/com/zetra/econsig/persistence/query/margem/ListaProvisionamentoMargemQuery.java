package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * Lista os valores de provisionamento e lançamento de serviços para um dado servidor.
 * @author marcelo
 */
public class ListaProvisionamentoMargemQuery extends HQuery {

    public String rseCodigo;
    public List<String> adeCodigos;
    public boolean excluirAdesCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
                "SELECT " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "ade.statusAutorizacaoDesconto.sadCodigo, " +
                "CASE WHEN EXISTS (SELECT 1 FROM RelacionamentoServico relSvc " +
                "    WHERE relSvc.servicoBySvcCodigoOrigem.svcCodigo = svc.svcCodigo " +
                "    AND relSvc.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CARTAO + "') THEN ade.adeVlr ELSE 0 END AS VLR_PROVISIONADO, " +
                "CASE WHEN EXISTS (SELECT 1 FROM RelacionamentoServico relSvc " +
                "    WHERE relSvc.servicoBySvcCodigoDestino.svcCodigo = svc.svcCodigo " +
                "    AND relSvc.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CARTAO + "') THEN ade.adeVlr ELSE 0 END AS VLR_LANCADO ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE 1 = 1 ");
        corpoBuilder.append("AND ( rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (excluirAdesCodigos && (adeCodigos != null) && !adeCodigos.isEmpty()) {
            corpoBuilder.append(" AND NOT (ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos)).append(")");
        } else if (!excluirAdesCodigos && (adeCodigos != null) && !adeCodigos.isEmpty()) {
            corpoBuilder.append(" OR ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        }

        corpoBuilder.append(")");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if ((adeCodigos != null) && (!adeCodigos.isEmpty())) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SAD_CODIGO,
                "VLR_PROVISIONADO",
                "VLR_LANCADO"
         };
    }
}
