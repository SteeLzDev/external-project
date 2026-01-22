package com.zetra.econsig.report.dao;

import java.sql.Connection;
import java.sql.Statement;

/**
 * <p> Title: ReportDAO</p>
 * <p> Description: Gerencia a conexão com o banco de dados, mantendo-a aberta para que o
 * relatório possa usar o Data Source em tempo de geração do arquivo PDF.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ReportDAO {

    public abstract Connection getConnection();

    public abstract Statement getStatement();

    public abstract void closeConnection();
}