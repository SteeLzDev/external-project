package com.zetra.econsig.persistence.query.beneficios;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListarCountBeneficiosPorBeneficiariosQuery extends HQuery {

    public String rseCodigo = null;
    public String scbCodigo = null;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        List<String> statusInativos = new ArrayList<>();
        statusInativos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        corpo.append("SELECT bfc.bfcCodigo,bfc.bfcNome, count(case when ben.naturezaServico.nseCodigo = '").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("' then bfc.bfcCodigo end) as ODONTO, count(case when ben.naturezaServico.nseCodigo = '").append(CodedValues.NSE_PLANO_DE_SAUDE).append("' then bfc.bfcCodigo end) as SAUDE ");

        corpo.append("FROM ContratoBeneficio cbe ");
        corpo.append("INNER JOIN cbe.autDescontoSet ade ");
        corpo.append("INNER JOIN cbe.beneficiario bfc ");
        corpo.append("INNER JOIN cbe.beneficio ben ");
        corpo.append("INNER JOIN bfc.servidor ser ");
        corpo.append("INNER JOIN ser.registroServidorSet rse ");

        corpo.append("WHERE rse.rseCodigo = :rseCodigo ");
        corpo.append("AND ben.naturezaServico.nseCodigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
        if (!TextHelper.isNull(scbCodigo)) {
            corpo.append("AND cbe.statusContratoBeneficio.scbCodigo = :scbCodigo ");
        }
        corpo.append("AND NULLIF(TRIM(cbe.cbeNumero), '') IS NOT NULL ");
        corpo.append("AND (cbe.cbeDataFimVigencia is not null or cbe.cbeDataCancelamento is not null) " );
        corpo.append("AND ade.tipoLancamento.tipoNatureza.tntCodigo in ('").append(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE).append("','").append(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO).append("') ");
        corpo.append("AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("inativos", statusInativos));
        corpo.append(" GROUP BY bfc.bfcCodigo" );

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(scbCodigo)) {
            defineValorClausulaNomeada("scbCodigo", scbCodigo, query);
        }

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("inativos", statusInativos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BFC_CODIGO,
                Columns.BFC_NOME,
                "ODONTO",
                "SAUDE"
        };
    }
}
