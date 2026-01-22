package com.zetra.econsig.persistence.query.consignataria;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNaturezaConsignatariaQuery</p>
 * <p>Description: Listagem de Natureza de Consignatária</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio $
 * $Revision: 7963 $
 * $Date: 2012-11-27 21:23:28 -0300 (ter, 27 nov 2012) $
 */
public class ListaCredenciamentoConsignatariaQuery extends HQuery {

    public String csaCodigo;
    public Date creDataIni;
    public Date creDataFim;
    public List<String> csaCodigos;
    public List<String> scrCodigos;

	@Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT cre.creCodigo, cre.csaCodigo, cre.scrCodigo, cre.creDataIni, cre.creDataFim, csa.csaNome, csa.csaIdentificador, scr.scrCodigo, scr.scrDescricao ");
        sql.append("FROM CredenciamentoCsa cre ");
        sql.append("INNER JOIN cre.consignataria csa ");
        sql.append("INNER JOIN cre.statusCredenciamento scr ");

        sql.append(" WHERE 1=1");

        if(scrCodigos !=null && !scrCodigos.isEmpty()) {
            sql.append(" AND cre.scrCodigo ").append(criaClausulaNomeada("scrCodigos", scrCodigos));
        }

        if(!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND cre.csaCodigo = :csaCodigo");
        }

        if(csaCodigos != null && !csaCodigos.isEmpty()) {
            sql.append(" AND cre.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }

        if(creDataIni != null && creDataFim !=null) {
            sql.append(" AND cre.creDataIni BETWEEN :creDataIni AND :creDataFim ");
        }

        sql.append(" ORDER BY cre.creDataIni DESC");

        Query<Object[]> query = instanciarQuery(session, sql.toString());
        if(!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if(csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        if(creDataIni != null && creDataFim !=null) {
            defineValorClausulaNomeada("creDataIni", creDataIni, query);
            defineValorClausulaNomeada("creDataFim", creDataFim, query);
        }

        if(scrCodigos !=null && !scrCodigos.isEmpty()) {
            defineValorClausulaNomeada("scrCodigos", scrCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.CRE_CODIGO,
                Columns.CRE_CSA_CODIGO,
                Columns.CRE_SCR_CODIGO,
                Columns.CRE_DATA_INI,
                Columns.CRE_DATA_FIM,
                Columns.CSA_NOME,
                Columns.CSA_IDENTIFICADOR,
    			Columns.CRE_SCR_CODIGO,
    			Columns.SCR_DESCRICAO
    	};
    }
}
