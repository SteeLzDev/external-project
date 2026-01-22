package com.zetra.econsig.service.totem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TotemAbstractControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class TotemAbstractControllerBean {

    protected Connection conectar(AcessoSistema responsavel) throws ClassNotFoundException, SQLException {
        final String url  = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_URL_BANCO_DE_DADOS, responsavel);
        final String user = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_USU_BANCO_DE_DADOS, responsavel);
        final String pass = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_PASS_BANCO_DE_DADOS, responsavel);

        // TODO Criar parâmetro de sistema
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Seta timeout de 15 segundos para evitar lock eterno
        final int connectionTimeout = 15;
        DriverManager.setLoginTimeout(connectionTimeout);
        final Connection sqlConnection = DriverManager.getConnection(url, user, pass);
        return DBHelper.getAuditedConnecton(sqlConnection);
    }
}
