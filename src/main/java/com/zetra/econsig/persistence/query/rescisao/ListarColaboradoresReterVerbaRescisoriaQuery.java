package com.zetra.econsig.persistence.query.rescisao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarColaboradoresReterVerbaRescisoriaQuery</p>
 * <p>Description: Listar os registros de colaboradores para informar a verba rescisória</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarColaboradoresReterVerbaRescisoriaQuery extends HQuery  {

    public boolean count = false;
    public List<String> notSvrCodigos;
    public String orgCodigo;

    public ListarColaboradoresReterVerbaRescisoriaQuery() {
        super();
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("SELECT COUNT(DISTINCT rse.rseCodigo) ");
        } else {
            corpoBuilder.append("SELECT rse.rseMatricula,");
            corpoBuilder.append(" ser.serNome, ");
            corpoBuilder.append(" ser.serCpf, ");
            corpoBuilder.append(" svr.svrDescricao, ");
            corpoBuilder.append(" vrr.vrrDataIni, ");
            corpoBuilder.append(" vrr.vrrCodigo, ");
            corpoBuilder.append(" rse.rseCodigo, ");
            corpoBuilder.append(" org.orgNome, ");
            corpoBuilder.append(" org.orgIdentificador, ");
            corpoBuilder.append(" svr.svrCodigo, ");
            corpoBuilder.append(" vrr.vrrProcessado ");
        }
        corpoBuilder.append("FROM VerbaRescisoriaRse vrr ");
        corpoBuilder.append("INNER JOIN vrr.statusVerbaRescisoria svr ");
        corpoBuilder.append("INNER JOIN vrr.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("WHERE 1=1 ");

        if (notSvrCodigos != null && !notSvrCodigos.isEmpty()) {
            corpoBuilder.append(" AND svr.svrCodigo ").append(criaClausulaNomeada("notSvrCodigos", CodedValues.NOT_EQUAL_KEY));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        // a ordenação da lista deve seguir os critérios: status asc (svr_codigo) e data início desc (vrr_data_ini);
        corpoBuilder.append(" ORDER BY svr.svrCodigo asc, vrr.vrrDataIni desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (notSvrCodigos != null && !notSvrCodigos.isEmpty()) {
            defineValorClausulaNomeada("notSvrCodigos", notSvrCodigos, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SVR_DESCRICAO,
                Columns.VRR_DATA_INI,
                Columns.VRR_CODIGO,
                Columns.RSE_CODIGO,
                Columns.ORG_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.SVR_CODIGO,
                Columns.VRR_PROCESSADO
        };
    }
}
