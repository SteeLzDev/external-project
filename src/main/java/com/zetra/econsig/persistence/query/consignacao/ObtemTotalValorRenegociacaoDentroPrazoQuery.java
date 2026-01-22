package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ObtemTotalValorRenegociacaoDentroPrazoQuery extends HQuery {

    public String adeCodigo;
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT oca.ocaAdeVlrAnt ");
        corpo.append(" FROM AutDesconto ade ");
        corpo.append(" INNER JOIN ade.ocorrenciaAutorizacaoSet oca");
        corpo.append(" WHERE oca.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpo.append(" AND oca.tocCodigo ").append(criaClausulaNomeada("tocCodigoRetem", CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO));
        corpo.append(" AND NOT EXISTS (SELECT 1 FROM OcorrenciaAutorizacao oca1 WHERE oca1.adeCodigo = ade.adeCodigo ");
        corpo.append(" AND oca1.tocCodigo ").append(criaClausulaNomeada("tocCodigoLibera", CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO));
        corpo.append(" ) ");
        corpo.append(" ORDER BY oca.ocaData DESC");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("tocCodigoRetem", CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO, query);
        defineValorClausulaNomeada("tocCodigoLibera", CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.OCA_ADE_VLR_ANT
        };
    }
}
