package com.zetra.econsig.persistence.query.arquivo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaArquivoRegistroServidorQuery</p>
 * <p>Description: Listagem de arquivos do registro servidor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaArquivoRegistroServidorQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("   arq.arqCodigo, ");
        corpoBuilder.append("   arq.arqConteudo, ");
        corpoBuilder.append("   tar.tarCodigo, ");
        corpoBuilder.append("   tar.tarDescricao, ");
        corpoBuilder.append("   usu.usuLogin, ");
        corpoBuilder.append("   arqRse.arsDataCriacao, ");
        corpoBuilder.append("   arqRse.arsNome, ");
        corpoBuilder.append("   arqRse.arsIpAcesso ");
        corpoBuilder.append(" from Arquivo arq ");
        corpoBuilder.append(" inner join arq.tipoArquivo tar ");
        corpoBuilder.append(" inner join arq.arquivoRseSet arqRse ");
        corpoBuilder.append(" inner join arqRse.registroServidor rse ");
        corpoBuilder.append(" inner join arqRse.usuario usu ");
        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" order by arqRse.arsDataCriacao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ARQ_CODIGO,
                Columns.ARQ_CONTEUDO,
                Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.ARS_DATA_CRIACAO,
                Columns.ARS_NOME,
                Columns.ARS_IP_ACESSO
                };
    }
}
