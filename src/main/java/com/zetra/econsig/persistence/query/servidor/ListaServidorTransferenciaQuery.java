package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorQuery</p>
 * <p>Description: Listagem de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorTransferenciaQuery extends HQuery {

    public String estIdentificador;
    public String orgIdentificador;
    public String rseMatricula;
    public String orgCnpj;
    public String serCPF;
    public boolean ativo = false;
    public boolean orgIdentificadorSemMascara = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct rse.rseCodigo ");
        corpoBuilder.append(" from RegistroServidor rse");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" AND est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            if (orgIdentificadorSemMascara) {
                corpoBuilder.append(" AND substituir(substituir(substituir(org.orgIdentificador,'.',''),'-',''),'/','') ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
            } else {
                corpoBuilder.append(" AND org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
            }
        }

        if (!TextHelper.isNull(orgCnpj)) {
            corpoBuilder.append(" AND org.orgCnpj ").append(criaClausulaNomeada("orgCnpj", orgCnpj));
        }

        if (ativo) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }


        // Adiciona cláusula de matricula e cpf
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCPF, false));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCPF, false, query);

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(orgCnpj)) {
            defineValorClausulaNomeada("orgCnpj", orgCnpj, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO
        };
    }
}
