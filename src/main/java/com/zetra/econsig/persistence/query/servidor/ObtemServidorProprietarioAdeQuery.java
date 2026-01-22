package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServidorProprietarioAdeQuery</p>
 * <p>Description: busca servidor do contrato dado</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemServidorProprietarioAdeQuery extends HQuery {

    public String adeCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(adeCodigo)) {
            throw new HQueryException("mensagem.informe.codigo.contrato", (AcessoSistema) null);
        }

        String corpo = "select " +
                       "ser.serNome, " +
                       "ser.serCpf, " +
                       "ser.serEmail, " +
                       "rse.rseMatricula, " +
                       "rse.rseBancoSal, " +
                       "rse.rseAgenciaSal, " +
                       "rse.rseContaSal, " +
                       "rse.rseBancoSal2, " +
                       "rse.rseAgenciaSal2, " +
                       "rse.rseContaSal2 ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.autDescontoSet ade");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_EMAIL,
                Columns.RSE_MATRICULA,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.RSE_BANCO_SAL_2,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_CONTA_SAL_2
        };
    }

}
