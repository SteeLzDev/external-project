package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemRegistroServidorQuery</p>
 * <p>Description: Listagem de registros de servidores</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemRegistroServidorQuery extends HQuery {

    public String estCodigo;
    public String estIdentificador;
    public String estCnpj;

    public String orgCodigo;
    public String orgIdentificador;
    public String orgCnpj;

    public String rseMatricula;
    public String serCpf;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select rse.rseCodigo ");
        corpoBuilder.append(", rse.rseDataCarga ");
        corpoBuilder.append(", (select min(ors.orsData) from OcorrenciaRegistroSer ors where ors.registroServidor.rseCodigo = rse.rseCodigo) as dataPrimeiraOcorrencia ");
        corpoBuilder.append("from RegistroServidor rse ");
        corpoBuilder.append("inner join rse.orgao org ");
        corpoBuilder.append("inner join org.estabelecimento est ");
        corpoBuilder.append("inner join rse.servidor ser ");

        corpoBuilder.append("where rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        } else if (!TextHelper.isNull(estCnpj)) {
            corpoBuilder.append(" and est.estCnpj ").append(criaClausulaNomeada("estCnpj", estCnpj));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        } else if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        } else if (!TextHelper.isNull(orgCnpj)) {
            corpoBuilder.append(" and org.orgCnpj ").append(criaClausulaNomeada("orgCnpj", orgCnpj));
        }
        
        // Ordenação pela data de inclusão de forma decrescente
        corpoBuilder.append(" order by rse.rseDataCarga desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseMatricula", rseMatricula, query);

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        } else if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        } else if (!TextHelper.isNull(estCnpj)) {
            defineValorClausulaNomeada("estCnpj", estCnpj, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        } else if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        } else if (!TextHelper.isNull(orgCnpj)) {
            defineValorClausulaNomeada("orgCnpj", orgCnpj, query);
        }

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_DATA_CARGA,
                Columns.ORS_DATA
        };
    }
}
