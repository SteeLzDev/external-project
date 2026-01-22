package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsultaSalarioServidorQuery</p>
 * <p>Description: Lista salario Servidor.</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsultaSalarioServidorQuery extends HQuery {

    public String serCpf;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "ser.serNome, " +
                "ser.serCpf, " +
                "cse.cseNome, " +
                "cse.cseCnpj, " +
                "est.estNome, " +
                "est.estCnpj, " +
                "org.orgNome, " +
                "org.orgCnpj, " +
                "rse.rseMunicipioLotacao, " +
                "rse.rseTipo, " +
                "crs.crsIdentificador, " +
                "crs.crsDescricao, " +
                "pos.posIdentificador, " +
                "pos.posDescricao, " +
                "vrs.vrsIdentificador, " +
                "vrs.vrsDescricao, " +
                "rse.rseSalario, " +
                "rse.rseProventos, " +
                "rse.rseDescontosComp, " +
                "rse.rseDescontosFacu, " +
                "rse.rseOutrosDescontos, " +
                "rse.rseDataUltSalario, " +
                "rse.rseDataCtc, " +
                "rse.rseDataSaida, " +
                "rse.srsCodigo ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("FROM RegistroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("INNER JOIN org.estabelecimento est ");
        corpoBuilder.append("INNER JOIN est.consignante cse ");
        corpoBuilder.append("LEFT JOIN rse.vinculoRegistroServidor vrs ");
        corpoBuilder.append("LEFT JOIN rse.postoRegistroServidor pos ");
        corpoBuilder.append("LEFT JOIN rse.cargoRegistroServidor crs ");
        corpoBuilder.append("WHERE ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("serCpf", serCpf, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CSE_NOME,
                Columns.CSE_CNPJ,
                Columns.EST_NOME,
                Columns.EST_CNPJ,
                Columns.ORG_NOME,
                Columns.ORG_CNPJ,
                Columns.RSE_MUNICIPIO_LOTACAO,
                Columns.RSE_TIPO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_DESCRICAO,
                Columns.POS_IDENTIFICADOR,
                Columns.POS_DESCRICAO,
                Columns.VRS_IDENTIFICADOR,
                Columns.VRS_DESCRICAO,
                Columns.RSE_SALARIO,
                Columns.RSE_PROVENTOS,
                Columns.RSE_DESCONTOS_COMP,
                Columns.RSE_DESCONTOS_FACU,
                Columns.RSE_OUTROS_DESCONTOS,
                Columns.RSE_DATA_ULT_SALARIO,
                Columns.RSE_DATA_CTC,
                Columns.RSE_DATA_SAIDA,
                Columns.SRS_CODIGO
        };
    }
}
