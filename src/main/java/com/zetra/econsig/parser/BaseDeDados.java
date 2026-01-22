package com.zetra.econsig.parser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.config.DocumentoTipo;

/**
 * <p>Title: BaseDeDados</p>
 * <p>Description: Definição de uma configuração para banco de dados.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BaseDeDados {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BaseDeDados.class);

    protected Connection conn;
    protected DocumentoTipo doc;
    protected boolean podeFecharConexao = true;

    public BaseDeDados(String nomearqconfig) throws ParserException {
        this(nomearqconfig, null);
    }

    public BaseDeDados(String nomearqconfig, Connection conn) throws ParserException {
        try {
            doc = XmlHelper.unmarshal(nomearqconfig);
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if ((doc.getPropriedades() == null) || (doc.getPropriedades().getSeExiste() == null)) {
            throw new ParserException("mensagem.erro.escritor.base.dados.configuracao.ausente", AcessoSistema.getAcessoUsuarioSistema());
        }
        this.conn = conn;
    }

    public void abreConexao() throws SQLException, ClassNotFoundException {
        if (conn == null) {
            if ((doc.getPropriedades().getURL() == null) || (doc.getPropriedades().getUsuario() == null) ||
                    (doc.getPropriedades().getDriver() == null) || (doc.getPropriedades().getSenha() == null)) {
                conn = DBHelper.makeConnection();
            } else {
                Class.forName(doc.getPropriedades().getDriver());
                conn = DriverManager.getConnection(doc.getPropriedades().getURL(), doc.getPropriedades().getUsuario(), doc.getPropriedades().getSenha());
            }
        } else {
            podeFecharConexao = false;
        }
    }

    public void fechaConexao() throws SQLException {
        if (podeFecharConexao) {
            if ((doc.getPropriedades().getURL() == null) || (doc.getPropriedades().getUsuario() == null) ||
                    (doc.getPropriedades().getDriver() == null) || (doc.getPropriedades().getSenha() == null)) {
                DBHelper.releaseConnection(conn);
            } else {
                conn.close();
            }
        }
    }
}