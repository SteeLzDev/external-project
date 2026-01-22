package com.zetra.econsig.persistence.query.servidor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistrosServidoresQuery</p>
 * <p>Description: Lista registros servidores de acordo com os filtros passados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistrosServidoresQuery extends HQuery {

    private boolean count = false;

    private List<String> srsCodigos;
    private List<String> orgCodigos;
    private List<String> estCodigos;
    private String rseMatricula;
    private String rseMargem;
    private String serCodigo;
    private boolean dataCargaNull;

    public ListaRegistrosServidoresQuery() {
    }

    public ListaRegistrosServidoresQuery(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos) {
        this.srsCodigos = srsCodigos;
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    public ListaRegistrosServidoresQuery(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, boolean count) {
        this.count = count;
        this.srsCodigos = srsCodigos;
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    public ListaRegistrosServidoresQuery(List<String> srsCodigos, String rseMargem) {
        this.srsCodigos = srsCodigos;
        this.rseMargem = rseMargem;
        dataCargaNull = (rseMargem == null);
    }

    public ListaRegistrosServidoresQuery(String serCodigo, String orgCodigo, String estCodigo, String rseMatricula) {
        if (!TextHelper.isNull(orgCodigo)) {
            orgCodigos = new ArrayList<>();
            orgCodigos.add(orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo)) {
            estCodigos = new ArrayList<>();
            estCodigos.add(estCodigo);
        }

        this.serCodigo = serCodigo;
        this.rseMatricula = rseMatricula;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        if (count) {
            corpo = "SELECT COUNT(DISTINCT rse.rseCodigo) AS TOTAL ";
        } else {
            corpo = "select " +
                    "rse.rseCodigo, " +
                    "rse.rseMatricula, " +
                    "rse.rseTipo, " +
                    "rse.rseMargem, " +
                    "rse.rseMargemRest, " +
                    "rse.rseMargemUsada, " +
                    "srs.srsCodigo, " +
                    "srs.srsDescricao, " +
                    "est.estCodigo, " +
                    "est.estIdentificador, " +
                    "org.orgCodigo, " +
                    "org.orgIdentificador, " +
                    "org.orgNome, " +
                    "ser.serNome, " +
                    "ser.serCodigo, " +
                    "ser.serCpf, " +
                    "ser.serEmail, " +
                    "ser.serDataValidacaoEmail, " +
                    "ser.serPermiteAlterarEmail, " +
                    "ser.serTel, " +
                    "ser.serCelular "
                    ;
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            corpoBuilder.append(" AND srs.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigos));
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(rseMargem)) {
            corpoBuilder.append(" AND rse.rseMargem ").append(criaClausulaNomeada("rseMargem", rseMargem));
        }

        if (dataCargaNull) {
            corpoBuilder.append(" AND rse.rseDataCarga IS NULL");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY rse.rseMatricula");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigo", srsCodigos, query);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(rseMargem)) {
            defineValorClausulaNomeada("rseMargem", new BigDecimal(rseMargem), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_TIPO,
                Columns.RSE_MARGEM,
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MARGEM_USADA,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.SER_NOME,
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.SER_EMAIL,
                Columns.SER_DATA_VALIDACAO_EMAIL,
                Columns.SER_PERMITE_ALTERAR_EMAIL,
                Columns.SER_TEL,
                Columns.SER_CELULAR
        };
    }
}
