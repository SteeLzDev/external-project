package com.zetra.econsig.persistence.query.senha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery</p>
 * <p>Description: Busca as ocorrências de um usuário servidor com senha autorização gerada via totem no dia.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery extends HQuery {

    public String usuCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tocCodigo = CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO_TOTEM;

        StringBuilder corpoBuilder = new StringBuilder("select count(*) as total ");
        corpoBuilder.append(" from OcorrenciaUsuario ous ");
        corpoBuilder.append(" inner join ous.usuarioByUsuCodigo usu ");
        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        corpoBuilder.append(" and ous.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));

        corpoBuilder.append(" and ous.ousData between to_datetime(format_datetime(data_corrente(), '00:00:00')) ");
        corpoBuilder.append(" and to_datetime(format_datetime(data_corrente(), '23:59:59')) ");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {};
    }
}
