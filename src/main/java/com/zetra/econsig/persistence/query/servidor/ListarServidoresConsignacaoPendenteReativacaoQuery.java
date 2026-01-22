package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarServidoresConsignacaoPendenteReativacaoQuery</p>
 * <p>Description: Consulta servidores com consignações pendentes de ativação, que tenham consignações suspensas durante o processamento do retorno.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 23454 $
 * $Date: 2022-03-15 11:35:34 -0200 (Ter, 15 Mar 2022) $
 */
public class ListarServidoresConsignacaoPendenteReativacaoQuery extends HQuery {
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT ");
        corpoBuilder.append("    rse.serCodigo,");
        corpoBuilder.append("    ser.serNome,");
        corpoBuilder.append("    ser.serCpf,");
        corpoBuilder.append("    ser.serEmail,");
        corpoBuilder.append("    ade.adeIncMargem ");
        corpoBuilder.append(" FROM RegistroServidor rse ");
        corpoBuilder.append(" INNER JOIN rse.servidor ser ");
        corpoBuilder.append(" INNER JOIN rse.autDescontoSet ade ");
        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SUSPENSA).append("'");
        corpoBuilder.append(" AND EXISTS( SELECT oca.adeCodigo FROM OcorrenciaAutorizacao oca WHERE oca.adeCodigo = ade.adeCodigo AND oca.tocCodigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA).append("')");
        corpoBuilder.append(" AND ade.adeIncMargem != ").append(CodedValues.INCIDE_MARGEM_NAO);
        corpoBuilder.append(" ORDER BY rse.serCodigo");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_EMAIL,
                Columns.ADE_INC_MARGEM
        };
    }
}