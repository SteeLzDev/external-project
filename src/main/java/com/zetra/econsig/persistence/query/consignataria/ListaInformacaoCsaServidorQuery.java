package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;



/**
 * <p>Title: ListaInformacaoCsaServidorQuery</p>
 * <p>Description: Querys de listagem das informacoes do servidor da csa</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */



public class ListaInformacaoCsaServidorQuery extends HQuery{
    public String csaCodigo;
    public String serCodigo;
    
    
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select ics.icsCodigo, "
                + "ics.icsValor, "
                + "ics.icsData, "
                + "usu.usuNome, "
                + "ics.serCodigo, "
                + "ics.csaCodigo, "
                + "ics.icsIpAcesso ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from InformacaoCsaServidor ics ");
        corpoBuilder.append("inner join ics.usuario usu ");
        corpoBuilder.append("where 1=1 ");
        
        if(!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("ics.csaCodigo", "csaCodigo", csaCodigo));
        }
        
        if(!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" and " ).append(criaClausulaNomeada("ics.serCodigo", "serCodigo", serCodigo));
        }
        
        corpoBuilder.append(" ORDER BY ics.icsData DESC");
        
        Query<Object[]> query = instanciarQuery(session,corpoBuilder.toString());
        
        if(!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        
        if(!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }
        
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] { 
                Columns.ICS_CODIGO, 
                Columns.ICS_VALOR, 
                Columns.ICS_DATA, 
                Columns.USU_NOME, 
                Columns.ICS_SER_CODIGO, 
                Columns.ICS_CSA_CODIGO, 
                Columns.ICS_IP_ACESSO
                };
    }
}
