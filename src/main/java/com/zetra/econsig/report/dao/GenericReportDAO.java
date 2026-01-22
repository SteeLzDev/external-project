package com.zetra.econsig.report.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.helper.sistema.DBHelper;
/**
 * <p> Title: GenericReportDAO</p>
 * <p> Description: Gerencia a conexão com o banco de dados</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GenericReportDAO implements ReportDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericReportDAO.class);

    private Connection conn;
    private Statement stmt;

    /**
     * Estabelece uma conexão com o banco de dados
     */
    @Override
    public synchronized Connection getConnection() {
        if (conn == null) {
            try {
                // Esta forma de se fazer a conexão pode ser alterada
                // para que se torne independente do EJB
                conn = DBHelper.makeConnection();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return conn;
    }

    @Override
    public synchronized Statement getStatement() {
        if (conn == null) {
            getConnection();
        }
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return stmt;
    }

    /**
     * Fecha o Statement e a Conexão com o banco de dados caso existam.
     */
    @Override
    public void closeConnection() {
         DBHelper.closeStatement(stmt);
         DBHelper.releaseConnection(conn);
    }
}
