package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistroServidorUsuarioSer</p>
 * <p>Description: Lista o registro do servidor pelo código do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorRseCodigoQuery extends HQuery {

    private final String rseCodigo;

    public ListaServidorRseCodigoQuery(String rseCodigo) {
        super();
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
                "select distinct " +
                        "rse.rseCodigo, " +
                        "rse.rseMatricula, " +
                        "rse.rseBancoSal, " +
                        "rse.rseAgenciaSal, " +
                        "rse.rseContaSal, " +
                        "srs.srsCodigo, " +
                        "srs.srsDescricao, " +
                        "ser.serCodigo, " +
                        "ser.serNome, " +
                        "ser.serCpf, " +
                        "ser.serDataNasc, " +
                        "ser.serEmail, " +
                        "org.orgCodigo, " +
                        "org.orgIdentificador, " +
                        "org.orgNome, " +
                        "est.estCodigo, " +
                        "est.estIdentificador, " +
                        "est.estNome ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");
        corpoBuilder.append(" where rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.SER_EMAIL,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME
        };
    }
}