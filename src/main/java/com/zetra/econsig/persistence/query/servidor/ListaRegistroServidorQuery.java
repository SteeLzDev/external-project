package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistroServidorQuery</p>
 * <p>Description: Listagem de registro de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorQuery extends HQuery {

    public boolean count = false;
    public boolean recuperaRseExcluido = true;

    public String serCodigo;
    public List<String> orgCodigos;
    public List<String> estCodigos;
    public List<String> rseMatriculas;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        if (count) {
            corpo = "SELECT COUNT(DISTINCT rse.rseCodigo) AS TOTAL ";
        } else {
            corpo = "select " +
                        "rse.rseCodigo, " +
                        "rse.rseMatricula, " +
                        "rse.servidor.serCodigo, " +
                        "srs.srsCodigo, " +
                        "srs.srsDescricao, " +
                        "rse.rseTipo, " +
                        "est.estIdentificador, " +
                        "est.estCodigo, " +
                        "org.orgIdentificador, " +
                        "org.orgCodigo, " +
                        "org.orgNome, " +
                        "est.estNome ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");

        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND rse.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (rseMatriculas != null && !rseMatriculas.isEmpty()) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatriculas));
        }

        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (ignoraServExcluidos || !recuperaRseExcluido) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY rse.rseMatricula");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (rseMatriculas != null && !rseMatriculas.isEmpty()) {
            defineValorClausulaNomeada("rseMatricula", rseMatriculas, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CODIGO,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.RSE_TIPO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.EST_NOME
        };
    }
}
