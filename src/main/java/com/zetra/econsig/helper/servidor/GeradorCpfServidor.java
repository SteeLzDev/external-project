package com.zetra.econsig.helper.servidor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
/**
 * <p>Title: GeradorCpfServidor</p>
 * <p>Description: Implementação genérica do gerador de CPF, incremental, com sufixo do País.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorCpfServidor {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorCpfServidor.class);

    private static GeradorCpfServidor gerador;
    private Long max;
    private final String country;

    static {
        gerador = new GeradorCpfServidor();
    }

    private GeradorCpfServidor() {
        country = LocaleHelper.getLocaleObject().getCountry();
    }

    public static GeradorCpfServidor getInstance() {
        return gerador;
    }

    public synchronized String getNext() {
        if (max == null) {
            // Se não exists, pega o máximo existente na base de dados
            max = getMax();
        }

        // Obtém o próximo e atualiza o max
        String next = TextHelper.formataMensagem(String.valueOf(++max), "0", 9, false);

        // Retorna o resultado
        return next + "-" + country;
    }

    private Long getMax() {
        Long numero = Long.valueOf(0);



        Connection conn = null;
        Statement stat = null;
        ResultSet rset = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            rset = stat.executeQuery("select coalesce(max(cast(replace(ser_cpf, '-" + country + "', '') as unsigned)), 0) as max from tb_servidor where ser_cpf like '%-" + country + "'");

            if (rset.next()) {
                numero = Long.valueOf(rset.getLong("max"));
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            DBHelper.closeResultSet(rset);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
        return numero;
    }
}
