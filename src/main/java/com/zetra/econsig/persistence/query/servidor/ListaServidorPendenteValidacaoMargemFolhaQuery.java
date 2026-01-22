package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorMargemFolhaQuery</p>
 * <p>Description: Listagem de servidores pendentes de validação margem folha</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorPendenteValidacaoMargemFolhaQuery extends HQuery {

    private final List<String> estCodigo;
    private final List<String> orgCodigo;

    public ListaServidorPendenteValidacaoMargemFolhaQuery(List<String> estCodigo, List<String> orgCodigo) {
        super();
        this.estCodigo = estCodigo;
        this.orgCodigo = orgCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ser.serCodigo,");
        corpoBuilder.append(" ser.serNome,");
        corpoBuilder.append(" ser.serCpf,");
        corpoBuilder.append(" rse.rseCodigo,");
        corpoBuilder.append(" rse.rseMatricula,");
        corpoBuilder.append(" org.orgCodigo,");
        corpoBuilder.append(" org.orgIdentificador,");
        corpoBuilder.append(" org.orgNome,");
        corpoBuilder.append(" est.estCodigo,");
        corpoBuilder.append(" est.estIdentificador,");
        corpoBuilder.append(" est.estNome,");
        corpoBuilder.append(" rse.rseMargem, rse.rseMargemUsada, rse.rseMargemRest, rse.rseMediaMargem,");
        corpoBuilder.append(" rse.rseMargem2, rse.rseMargemUsada2, rse.rseMargemRest2, rse.rseMediaMargem2,");
        corpoBuilder.append(" rse.rseMargem3, rse.rseMargemUsada3, rse.rseMargemRest3, rse.rseMediaMargem3");
        corpoBuilder.append(" from Servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo = '").append(CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM).append("'");

        if (estCodigo != null && !estCodigo.isEmpty()) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigo != null && !estCodigo.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.RSE_MARGEM,
                Columns.RSE_MARGEM_USADA,
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MEDIA_MARGEM,
                Columns.RSE_MARGEM_2,
                Columns.RSE_MARGEM_USADA_2,
                Columns.RSE_MARGEM_REST_2,
                Columns.RSE_MEDIA_MARGEM_2,
                Columns.RSE_MARGEM_3,
                Columns.RSE_MARGEM_USADA_3,
                Columns.RSE_MARGEM_REST_3,
                Columns.RSE_MEDIA_MARGEM_3
        };
    }
}
