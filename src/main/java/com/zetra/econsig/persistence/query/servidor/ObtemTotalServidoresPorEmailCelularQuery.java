package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaServidorPorEmailQuery</p>
 * <p>Description: Retornar informações de servidores de acordo com o filtro.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalServidoresPorEmailCelularQuery extends HQuery {

    public String serEmail;
    public String serCelular;
    public String serCpfExceto;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(serEmail) && TextHelper.isNull(serCelular)) {
            throw new HQueryException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema());
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select count(*) ");
        corpoBuilder.append(" from Servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse");
        corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");

        if (!TextHelper.isNull(serEmail)) {
            corpoBuilder.append(" AND ser.serEmail ").append(criaClausulaNomeada("serEmail", serEmail));
        }
        if (!TextHelper.isNull(serCelular)) {
            corpoBuilder.append(" AND ser.serCelular ").append(criaClausulaNomeada("serCelular", serCelular));
        }
        if (!TextHelper.isNull(serCpfExceto)) {
            corpoBuilder.append(" AND ser.serCpf <> :serCpf");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(serEmail)) {
            defineValorClausulaNomeada("serEmail", serEmail, query);
        }
        if (!TextHelper.isNull(serCelular)) {
            defineValorClausulaNomeada("serCelular", serCelular, query);
        }
        if (!TextHelper.isNull(serCpfExceto)) {
            defineValorClausulaNomeada("serCpf", serCpfExceto, query);
        }

        return query;
    }
}