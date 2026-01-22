package com.zetra.econsig.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.parser.config.ColunaTipo;
import com.zetra.econsig.parser.config.ParametroTipo;

/**
 * <p>Title: LeitorBaseDeDados</p>
 * <p>Description: Implementação do Leitor para banco de dados.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorBaseDeDados extends BaseDeDados implements Leitor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeitorBaseDeDados.class);

    private String sql;
    private String sqlHeader;
    private String sqlFooter;

    private ResultSet rst;
    private ResultSet rstHeader;
    private ResultSet rstFooter;

    private PreparedStatement stat;
    private PreparedStatement statHeader;
    private PreparedStatement statFooter;

    public LeitorBaseDeDados(String nomearqconfig) throws ParserException {
        this(nomearqconfig, null, null, null);
    }

    public LeitorBaseDeDados(String nomearqconfig, String sql) throws ParserException {
        this(nomearqconfig, sql, null, null);
    }

    public LeitorBaseDeDados(String nomearqconfig, ResultSet rst) throws ParserException {
        this(nomearqconfig, null, null, rst);
    }

    public LeitorBaseDeDados(String nomearqconfig, String sql, Connection conn) throws ParserException {
        this(nomearqconfig, null, conn, null);
    }

    private LeitorBaseDeDados(String nomearqconfig, String sql, Connection conn, ResultSet rst) throws ParserException {
        super(nomearqconfig, conn);
        this.sql = sql;
        this.rst = rst;
        sqlHeader = null;
        sqlFooter = null;

        stat = null;
        statHeader = null;
        statFooter = null;
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        if (doc.getParametro() != null) {
            // Faz o tratamento dos parametros, caso exista algum
            Iterator<ParametroTipo> it = doc.getParametro().iterator();
            while (it.hasNext()) {
                ParametroTipo param = it.next();
                if (sql == null && param.getNome().equalsIgnoreCase("query")) {
                    sql = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("Header.query")) {
                    sqlHeader = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("Footer.query")) {
                    sqlFooter = param.getValor();
                }
            }
        }

        if (this.rst == null) {
            if (sql == null || sql.equals("")) {
                throw new ParserException("mensagem.erro.base.dados.consulta.nao.informada", (AcessoSistema) null);
            }

            try {
                // Inicia conexão com a base de dados
                abreConexao();
            } catch (Exception ex) {
                throw new ParserException("mensagem.erro.base.dados.conexao.nao.estabelecida", (AcessoSistema) null, ex);
            }

            try {
                // Executa as querys de leitura dos dados
                if (sql != null && !sql.equals("")) {
                    stat = conn.prepareStatement(sql);
                    rst = stat.executeQuery();
                }
                if (sqlHeader != null && !sqlHeader.equals("")) {
                    statHeader = conn.prepareStatement(sqlHeader);
                    rstHeader = statHeader.executeQuery();
                }
                if (sqlFooter != null && !sqlFooter.equals("")) {
                    statFooter = conn.prepareStatement(sqlFooter);
                    rstFooter = statFooter.executeQuery();
                }
            } catch (SQLException ex) {
                throw new ParserException("mensagem.erro.base.dados.consulta.incorreta", (AcessoSistema) null, ex);
            }
        }
    }

    @Override
    public Map<String, Object> le() {
        Map<String, Object> retorno = new HashMap<>();
        ResultSet rs = null;

        try {
            if (rstHeader == null || !rstHeader.next()) {
                if (rst == null || !rst.next()) {
                    if (rstFooter == null || !rstFooter.next()) {
                        return null;
                    } else {
                        rs = rstFooter;
                        retorno.put("__FOOTER__", "S");
                    }
                } else {
                    rs = rst;
                }
            } else {
                rs = rstHeader;
                retorno.put("__HEADER__", "S");
            }

            Iterator<ColunaTipo> it = doc.getColuna().iterator();
            while (it.hasNext()) {
                ColunaTipo col = it.next();
                if (!col.getTipo().equalsIgnoreCase("ENTRADA")) {
                    try {
                        Object valor = rs.getString(col.getNome());
                        retorno.put(col.getNome(), valor);
                    } catch (SQLException ex) {
                        LOG.error(ex.getMessage());
                        retorno.put(col.getNome(), null);
                    }
                }
            }
            return retorno;

        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public void encerraLeitura() throws ParserException {
        try {
            if (rst != null) {
                rst.close();
            }
            if (rstHeader != null) {
                rstHeader.close();
            }
            if (rstFooter != null) {
                rstFooter.close();
            }

            if (stat != null) {
                stat.close();
            }
            if (statHeader != null) {
                statHeader.close();
            }
            if (statFooter != null) {
                statFooter.close();
            }

            fechaConexao();
        } catch (SQLException ex) {
            throw new ParserException("mensagem.erro.base.dados.conexao.nao.encerrada", (AcessoSistema) null, ex);
        }
    }
}