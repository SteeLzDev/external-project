package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoCancelamentoParametroCsaQuery</p>
 * <p> Description: Listagem de consignações filtradas por codigo da csa, codigo de registro servidor e sad codigo 0</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListaConsignacaoCancelamentoParametroCsaQuery extends HQuery{
    public String rseCodigo;
    public String csaCodigo;
    public List<String> sadCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("where ade.sadCodigo = '" + CodedValues.SAD_SOLICITADO + "'");

        if (!TextHelper.isNull(rseCodigo)) {
            // Se os parâmetros são válidos, insere as ocorrências para um servidor, mas
            // se os parâmetros são nulos ou vazios, insere ocorrência para todos os servidores
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo = :rseCodigo ");
        }
        if (!TextHelper.isNull(csaCodigo)) {
            // Se o usuário é de consignatária ou correspondente, não cancela as
            // consignações da consignatária do usuário
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo = :csaCodigo ");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
         };
    }
}
