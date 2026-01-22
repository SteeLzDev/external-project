package com.zetra.econsig.persistence.query.rescisao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarVerbaRescisoriaQuery</p>
 * <p>Description: Listar os registros de verbas rescisórias de colaboradores</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarVerbaRescisoriaQuery extends HQuery  {

    public boolean count = false; 
    public List<String> svrCodigos;
    public String orgCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("SELECT COUNT(DISTINCT rse.rseCodigo) ");
        } else {
            corpoBuilder.append("SELECT vrr.vrrCodigo ");
            corpoBuilder.append(", rse.rseMatricula ");
            corpoBuilder.append(", rse.rseCodigo ");
            corpoBuilder.append(", rse.rseDataAdmissao ");
            corpoBuilder.append(", ser.serNome ");
            corpoBuilder.append(", ser.serCpf ");
            corpoBuilder.append(", org.orgNome ");
            corpoBuilder.append(", org.orgIdentificador ");
            corpoBuilder.append(", svr.svrCodigo ");
            corpoBuilder.append(", svr.svrDescricao ");
            corpoBuilder.append(", vrr.vrrDataIni ");
            corpoBuilder.append(", vrr.vrrDataFim ");
            corpoBuilder.append(", vrr.vrrDataUltAtualizacao ");
            corpoBuilder.append(", vrr.vrrValor ");
            corpoBuilder.append(", vrr.vrrProcessado ");
        }
        corpoBuilder.append("FROM VerbaRescisoriaRse vrr ");
        corpoBuilder.append("INNER JOIN vrr.statusVerbaRescisoria svr ");
        corpoBuilder.append("INNER JOIN vrr.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("WHERE 1=1 ");

        if (svrCodigos != null && !svrCodigos.isEmpty()) {
            corpoBuilder.append(" AND vrr.statusVerbaRescisoria.svrCodigo ").append(criaClausulaNomeada("svrCodigos", svrCodigos));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" ORDER BY vrr.vrrDataIni DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (svrCodigos != null && !svrCodigos.isEmpty()) {
            defineValorClausulaNomeada("svrCodigos", svrCodigos, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.VRR_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_CODIGO,
                Columns.RSE_DATA_ADMISSAO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.ORG_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.SVR_CODIGO,
                Columns.SVR_DESCRICAO,
                Columns.VRR_DATA_INI,
                Columns.VRR_DATA_FIM,
                Columns.VRR_DATA_ULT_ATUALIZACAO,
                Columns.VRR_VALOR,
                Columns.VRR_PROCESSADO
        };
    }
}
