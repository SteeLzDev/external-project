package com.zetra.econsig.persistence.query.arquivo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaArquivoServidorQuery</p>
 * <p>Description: Listagem de arquivos do servidor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaArquivoServidorQuery extends HQuery {

    public String serCodigo;
    public List<String> tarCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("   arq.arqCodigo, ");
        corpoBuilder.append("   arq.arqConteudo, ");
        corpoBuilder.append("   tar.tarCodigo, ");
        corpoBuilder.append("   tar.tarDescricao, ");
        corpoBuilder.append("   usu.usuLogin, ");
        corpoBuilder.append("   arqSer.aseDataCriacao, ");
        corpoBuilder.append("   arqSer.aseNome, ");
        corpoBuilder.append("   arqSer.aseIpAcesso ");
        corpoBuilder.append(" from Arquivo arq ");
        corpoBuilder.append(" inner join arq.tipoArquivo tar ");
        corpoBuilder.append(" inner join arq.arquivoSerSet arqSer ");
        corpoBuilder.append(" inner join arqSer.servidor ser ");
        corpoBuilder.append(" inner join arqSer.usuario usu ");
        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            corpoBuilder.append(" and tar.tarCodigo ").append(criaClausulaNomeada("tarCodigos", tarCodigos));
        }

        corpoBuilder.append(" order by arqSer.aseDataCriacao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("serCodigo", serCodigo, query);

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            defineValorClausulaNomeada("tarCodigos", tarCodigos, query);
        }

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
                Columns.ASE_DATA_CRIACAO,
                Columns.ASE_NOME,
                Columns.ASE_IP_ACESSO
                };
    }
}
