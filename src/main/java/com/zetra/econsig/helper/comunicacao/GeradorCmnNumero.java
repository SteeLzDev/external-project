package com.zetra.econsig.helper.comunicacao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: GeradorCmnNumero</p>
 * <p>Description: armazena a numeração mais recente da tabela comunicação e cria novos números, </p>
 * <p>             a partir deste, para as novas comunicações criadas.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorCmnNumero {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorCmnNumero.class);
    
    private static GeradorCmnNumero geradorCmn;    
    private Long max;

    static {
        geradorCmn = new GeradorCmnNumero();
    }  

    static public GeradorCmnNumero getInstance() {
        return geradorCmn;
    }

    public synchronized Long getNext() {
        // Busca o proximo numero
        if (this.max == null) {
            // Se não exists, pega o máximo existente na base de dados
            this.max = getMaxCmnNumero();
        }
        
        // Calcula o próximo como sendo o máximo + 1
        Long next = Long.valueOf(this.max.longValue() + 1);

        // Define o max igual ao atual
        this.max = next;
        
        // Retorna o próximo
        return next;
    }

    private Long getMaxCmnNumero() {
        // Valor inicial básico
        Long cmnNumero = Long.valueOf(0);

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("SELECT COALESCE(MAX(cmn_numero), 0) AS next ");
            query.append("FROM tb_comunicacao ");

            LOG.debug("GERADOR CMN_NUMERO: " + query.toString());
            rs = stat.executeQuery(query.toString());

            if (rs.next()) {
                cmnNumero = Long.valueOf(rs.getLong("next"));
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
        return cmnNumero;
    }
}
