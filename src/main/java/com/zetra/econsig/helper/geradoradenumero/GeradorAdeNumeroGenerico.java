package com.zetra.econsig.helper.geradoradenumero;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.helper.sistema.DBHelper;
/**
 * <p>Title: GeradorAdeNumeroGenerico</p>
 * <p>Description: Implementação genérica do gerador de AdeNumero, incremental.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorAdeNumeroGenerico implements GeradorAdeNumero {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorAdeNumeroGenerico.class);

    private Long max;
    
    public synchronized Long getNext(String vcoCodigo, Date anoMesIni) {
        // Busca o proximo numero
        if (this.max == null) {
            // Se não exists, pega o máximo existente na base de dados
            this.max = getMaxAdeNumero();
        }
        
        // Calcula o próximo como sendo o máximo + 1
        Long next = Long.valueOf(this.max.longValue() + 1);

        // Define o max igual ao atual
        this.max = next;
        
        // Retorna o próximo
        return next;
    }

    private Long getMaxAdeNumero() {
        // Valor inicial básico
        Long adeNumero = Long.valueOf(0);

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("SELECT COALESCE(MAX(ade_numero), 0) AS next ");
            query.append("FROM tb_aut_desconto ");

            LOG.debug("GERADOR ADE_NUMERO: " + query.toString());
            rs = stat.executeQuery(query.toString());

            if (rs.next()) {
                adeNumero = Long.valueOf(rs.getLong("next"));
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stat != null) {
                    stat.close();
                }
                if (conn != null) {
                    DBHelper.releaseConnection(conn);
                }
            } catch (SQLException ex) {
            }
        }
        return adeNumero;
    }
}
