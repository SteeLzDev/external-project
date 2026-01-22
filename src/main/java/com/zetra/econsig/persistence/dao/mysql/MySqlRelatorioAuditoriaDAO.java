package com.zetra.econsig.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.log.ControleTipoEntidade;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericRelatorioAuditoriaDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlRelatorioAuditoriaDAO</p>
 * <p>Description: Implementacao do DAO do Relatório de Auditoria para o MySql</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlRelatorioAuditoriaDAO extends GenericRelatorioAuditoriaDAO {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlRelatorioAuditoriaDAO.class);

    @Override
    public String select(Map<String, Object> parametros, MapSqlParameterSource queryParams) throws DAOException {
        String tipoEntidadeUsuario = (String) getParametro("TIPO_ENTIDADE_USUARIO", parametros);
        boolean auditoria = !TextHelper.isNull(getParametro("MODULO_AUDITORIA", parametros)) ? getParametro("MODULO_AUDITORIA", parametros).toString().equals("true") : false;
        String nomeTabelaResultado = recuperaTabelaEntidade(auditoria, tipoEntidadeUsuario);

        String campoObs = "LOG_OBS";
        String campoIp = "LOG_IP";
        String campoData = "LOG_DATA";

        // Caso seja um processo do Módulo de Auditoria, utiliza as tabelas de armazenamento de dados dos relatórios gerados.
        if (!TextHelper.isNull(tipoEntidadeUsuario) && auditoria) {
            if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) {
                campoObs = Columns.getColumnName(Columns.ACE_OBS);
                campoIp = Columns.getColumnName(Columns.ACE_IP);
                campoData = Columns.getColumnName(Columns.ACE_DATA);
            } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) {
                campoObs = Columns.getColumnName(Columns.ACS_OBS);
                campoIp = Columns.getColumnName(Columns.ACS_IP);
                campoData = Columns.getColumnName(Columns.ACS_DATA);
            } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_COR)) {
                campoObs = Columns.getColumnName(Columns.ACO_OBS);
                campoIp = Columns.getColumnName(Columns.ACO_IP);
                campoData = Columns.getColumnName(Columns.ACO_DATA);
            } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                campoObs = Columns.getColumnName(Columns.AOR_OBS);
                campoIp = Columns.getColumnName(Columns.AOR_IP);
                campoData = Columns.getColumnName(Columns.AOR_DATA);
            } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                campoObs = Columns.getColumnName(Columns.ASU_OBS);
                campoIp = Columns.getColumnName(Columns.ASU_IP);
                campoData = Columns.getColumnName(Columns.ASU_DATA);
            }
        }

        Date dataIni = null;
        Date dataFim = null;
        try {
            dataIni = !TextHelper.isNull(getParametro("DATA_INI", parametros)) ? DateHelper.parse(getParametro("DATA_INI", parametros).toString(), "yyyy-MM-dd HH:mm:ss") : null;
            dataFim = !TextHelper.isNull(getParametro("DATA_FIM", parametros)) ? DateHelper.parse(getParametro("DATA_FIM", parametros).toString(), "yyyy-MM-dd HH:mm:ss") : null;
        } catch (ParseException e) {
            throw new DAOException("mensagem.erro.periodo.parse.invalido", (AcessoSistema) null);
        }

        String estCodigo = (String) getParametro("EST_CODIGO", parametros);
        List<String> orgCodigos = (List<String>) getParametro("ORG_CODIGO", parametros);
        String rseMatricula = (String) getParametro("RSE_MATRICULA", parametros);
        String logCanal = (String) getParametro("LOG_CANAL", parametros);

        StringBuilder sql = new StringBuilder();
        sql.setLength(0);
        sql.append("SELECT ");

        sql.append("CONCAT(CASE WHEN ").append(Columns.USU_STU_CODIGO).append(" = '3' then COALESCE(").append(Columns.USU_TIPO_BLOQ).append(", COALESCE(");
        sql.append(Columns.USU_LOGIN).append(", '')) ELSE COALESCE(").append(Columns.USU_LOGIN).append(", '') END, ' - ', ").append(Columns.USU_NOME).append(") AS USU_NOME, ");
        sql.append("TLO_DESCRICAO AS TLO_DESCRICAO, ");
        sql.append("REPLACE(REPLACE(").append(campoObs).append(", '<BR>', '\n'), '<br>', '\n') AS LOG_OBS, "); // Operador replace é CASE SENSITIVE

        sql.append(campoIp).append(" AS LOG_IP, ");
        sql.append("FUN_DESCRICAO AS FUN_DESCRICAO, ");

        if (TextHelper.isNull(tipoEntidadeUsuario) || !tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) {
            sql.append(" CASE WHEN COALESCE(").append(Columns.CSE_NOME).append(",'') <> '' THEN ").append(Columns.CSE_NOME);
            sql.append(" WHEN COALESCE(").append(Columns.CSA_NOME).append(",'') <> '' THEN ").append(Columns.CSA_NOME);
            sql.append(" WHEN COALESCE(").append("TB_CONSIGNATARIA_COR.CSA_NOME,'') <> '' AND COALESCE(").append(Columns.COR_NOME).append(",'') <> '' THEN ");
            sql.append("CONCAT(CONCAT(TB_CONSIGNATARIA_COR.CSA_NOME,' - '),").append(Columns.COR_NOME).append(")");
            sql.append(" WHEN COALESCE(").append(Columns.ORG_NOME).append(",'') <> '' THEN ").append(Columns.ORG_NOME);
            sql.append(" WHEN COALESCE(").append(Columns.SER_NOME).append(",'') <> '' THEN ").append(Columns.SER_NOME);
            sql.append(" WHEN COALESCE(sup.").append(Columns.getColumnName(Columns.CSE_NOME)).append(",'') <> '' THEN sup.").append(Columns.getColumnName(Columns.CSE_NOME));
            sql.append(" ELSE '' END AS ENTIDADE, ");
        } else {
            sql.append(" CASE WHEN COALESCE(").append(Columns.CSA_NOME).append(",'') <> '' THEN ").append(Columns.CSA_NOME);
            sql.append(" WHEN COALESCE(").append("TB_CONSIGNATARIA_COR.CSA_NOME,'') <> '' AND COALESCE(").append(Columns.COR_NOME).append(",'') <> '' THEN ");
            sql.append("CONCAT(CONCAT(TB_CONSIGNATARIA_COR.CSA_NOME,' - '),").append(Columns.COR_NOME).append(")");
            sql.append(" ELSE '' END AS ENTIDADE, ");
        }

        sql.append("DATE_FORMAT(").append(campoData).append(", '%d/%m/%Y %H:%i:%s') AS DATA_LOG ");

        if (nomeTabelaResultado.toLowerCase().equals("tb_resultado_relatorio")) {
            sql.append(", LOG_CANAL ");
        } else {
            sql.append(", '' ");
        }

        sql.append("as LOG_CANAL ");

        sql.append("FROM ").append(nomeTabelaResultado).append(" l ");

        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO).append(" ON ").append(Columns.USU_CODIGO).append(" = l.USU_CODIGO");
        if (TextHelper.isNull(tipoEntidadeUsuario) || !tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) {
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSE).append(" ON ").append(Columns.UCE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" ON ").append(Columns.CSE_CODIGO).append(" = ").append(Columns.UCE_CSE_CODIGO);
        }
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSA).append(" ON ").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.UCA_CSA_CODIGO);
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_COR).append(" ON ").append(Columns.UCO_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CORRESPONDENTE).append(" ON ").append(Columns.COR_CODIGO).append(" = ").append(Columns.UCO_COR_CODIGO);
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" AS TB_CONSIGNATARIA_COR ON ").append("TB_CONSIGNATARIA_COR.CSA_CODIGO = ").append(Columns.COR_CSA_CODIGO);

        if (TextHelper.isNull(tipoEntidadeUsuario) || !tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) {
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_ORG).append(" ON ").append(Columns.UOR_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON ").append(Columns.ORG_CODIGO).append(" = ").append(Columns.UOR_ORG_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ESTABELECIMENTO).append(" ON ").append(Columns.EST_CODIGO).append(" = ").append(Columns.ORG_EST_CODIGO);

            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SER).append(" ON ").append(Columns.USE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVIDOR).append(" ON ").append(Columns.SER_CODIGO).append(" = ").append(Columns.USE_SER_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" ON ").append(Columns.RSE_SER_CODIGO).append(" = ").append(Columns.SER_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" org2 ON org2.org_codigo = ").append(Columns.RSE_ORG_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ESTABELECIMENTO).append(" est2 ON est2.est_codigo = org2.est_codigo");

            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SUP).append(" ON ").append(Columns.USP_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" sup ON sup.").append(Columns.getColumnName(Columns.CSE_CODIGO)).append(" = ").append(Columns.USP_CSE_CODIGO);
        }

        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_TIPO_LOG).append(" on (").append(Columns.TLO_CODIGO).append(" = l.TLO_CODIGO)");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_TIPO_ENTIDADE).append(" on (").append(Columns.TEN_CODIGO).append(" = l.TEN_CODIGO)");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_FUNCAO).append(" on (").append(Columns.FUN_CODIGO).append(" = l.FUN_CODIGO)");

        sql.append(" WHERE 1 = 1");
        sql.append(" AND ").append(campoData).append(" BETWEEN :dataIni AND :dataFim");
        queryParams.addValue("dataIni", dataIni);
        queryParams.addValue("dataFim", dataFim);

        if (!TextHelper.isNull(estCodigo)) {
            sql.append(" AND (").append(Columns.EST_CODIGO).append(" = :estCodigo ");
            sql.append("  OR est2.est_codigo = :estCodigo) ");
            queryParams.addValue("estCodigo", estCodigo);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND (").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos)");
            sql.append(" OR org2.org_codigo IN (:orgCodigos)) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }
        if (!TextHelper.isNull(rseMatricula)) {
            sql.append(" AND ").append(Columns.RSE_MATRICULA).append(" = :rseMatricula ");
            queryParams.addValue("rseMatricula", rseMatricula);
        }
        if (!TextHelper.isNull(logCanal)) {
            sql.append(" AND LOG_CANAL = :logCanal ");
            queryParams.addValue("logCanal", logCanal);
        }

        sql.append(" ORDER BY ").append(campoData).append(" ASC ");

        LOG.trace(sql.toString());
        return sql.toString();
    }

    @Override
    protected void populaTabelaResultado(Connection conn, String nomeTabelaResultado, String nomeTabela,
            String tipoOperador, String operador, String tenCodigo, String entidade, String tipoEntidadeUsuario,
            boolean auditoria, List<String> tabelasResult, List<String> rseCodigos) throws SQLException {

        tipoEntidadeUsuario = !TextHelper.isNull(tipoEntidadeUsuario) ? tipoEntidadeUsuario : "";

        final String rotuloAuditoriaTotal = ApplicationResourcesHelper.getMessage("rotulo.auditar.registroservidor.rse_auditoria_total", null);

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        for (String grupoFuncao : tabelasResult) {
            sql.setLength(0);
            sql.append("INSERT INTO ").append(nomeTabelaResultado).append(" (");

            /*
             * Caso seja um processo do Módulo de Auditoria, utiliza as tabelas de armazenamento dados dos relatórios gerados.
             * No Relatório de Auditoria agendado pelo usuário no sistema, será utilizada uma tabela padrão que é recriada a cada geração de relatório.
             */
            final List<String> camposTabelaAuditoria = Columns.TABELAS_AUDITORIA.get(nomeTabelaResultado);
            if (camposTabelaAuditoria != null && !camposTabelaAuditoria.isEmpty()) {
                Iterator<String> ite = camposTabelaAuditoria.iterator();
                while (ite.hasNext()) {
                    sql.append(Columns.getColumnName(ite.next().toString()));
                    if (ite.hasNext()) {
                        sql.append(", ");
                    }
                }
            } else {
                // campos da tabela padrão que é utilizada no Relatório de Auditoria
                sql.append("CODIGO_ENTIDADE, TLO_CODIGO, USU_CODIGO, FUN_CODIGO, TEN_CODIGO, AUDITADO, LOG_DATA, LOG_IP, LOG_OBS ");

                if (nomeTabelaResultado.toLowerCase().equals("tb_resultado_relatorio")) {
                    sql.append(", LOG_CANAL");
                }
            }
            sql.append(") ");
            sql.append("SELECT ");

            if (tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_CSE)) {
                sql.append("coalesce(cse.CSE_CODIGO, '").append(CodedValues.CSE_CODIGO_SISTEMA).append("')");
            } else if (tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_CSA)) {
                sql.append("csa.CSA_CODIGO");
            } else if (tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_COR)) {
                sql.append("cor.COR_CODIGO");
            } else if (tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_ORG)) {
                sql.append("org.ORG_CODIGO");
            } else if (tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_SUP)) {
                sql.append("coalesce(sup.CSE_CODIGO, '").append(CodedValues.CSE_CODIGO_SISTEMA).append("')");
            } else {
                sql.append("CASE ");
                sql.append("WHEN cse.CSE_CODIGO is not null THEN cse.CSE_CODIGO ");
                sql.append("WHEN csa.CSA_CODIGO is not null THEN csa.CSA_CODIGO ");
                sql.append("WHEN cor.COR_CODIGO is not null THEN cor.COR_CODIGO ");
                sql.append("WHEN org.ORG_CODIGO is not null THEN org.ORG_CODIGO ");
                sql.append("WHEN sup.CSE_CODIGO is not null THEN sup.CSE_CODIGO ");
                sql.append("ELSE '").append(CodedValues.CSE_CODIGO_SISTEMA).append("' ");
                sql.append("end");
            }
            sql.append(" AS CODIGO_ENTIDADE, ");
            sql.append("l.TLO_CODIGO, l.USU_CODIGO_LOG, l.FUN_CODIGO_LOG, l.TEN_CODIGO_LOG, 'N' AS AUDITADO, LOG_DATA, l.LOG_IP, ");

            sql.append("CONCAT(");
            if (rseCodigos != null && !rseCodigos.isEmpty()) {
                sql.append("CASE WHEN l.RSE_CODIGO IS NOT NULL AND l.RSE_CODIGO IN (:rseCodigos) THEN '").append(rotuloAuditoriaTotal).append("\\n' ELSE '' END, ");
                queryParams.addValue("rseCodigos", rseCodigos);
            }
            sql.append("l.LOG_OBS, '\\n', ");
            StringBuilder join = new StringBuilder();
            StringBuilder where = new StringBuilder();
            Iterator<String> iteCampo = getCamposPorGrupoFuncao().get(grupoFuncao).iterator();
            while (iteCampo.hasNext()) {
                String campo = iteCampo.next();
                String campoDescricao = CAMPO_DESCRICAO.get(campo);
                if (!TextHelper.isNull(campoDescricao)) {
                    String tabelaJoin = getTableName(campoDescricao);
                    String descricao = Columns.getColumnName(campoDescricao);
                    join.append("LEFT OUTER JOIN ").append(tabelaJoin).append(" on (").append(tabelaJoin).append(".").append(campo).append(" = l.").append(campo).append(") ");
                    sql.append("COALESCE(CONCAT('").append(Columns.getColumnLabel(campoDescricao)).append(": '").append(", cast(").append(tabelaJoin).append(".").append(descricao).append(" as char), '\\n'), '')");
                    if (!TextHelper.isNull(tenCodigo) && !TextHelper.isNull(entidade)) {
                    	if ((tenCodigo.equals(Log.CONSIGNANTE) && campoDescricao.equals(Columns.CSE_NOME)) ||
                    			(tenCodigo.equals(Log.ESTABELECIMENTO) && campoDescricao.equals(Columns.EST_NOME)) ||
                    			(tenCodigo.equals(Log.ORGAO) && campoDescricao.equals(Columns.ORG_NOME)) ||
                    			(tenCodigo.equals(Log.CONSIGNATARIA) && campoDescricao.equals(Columns.CSA_NOME)) ||
                    			(tenCodigo.equals(Log.CORRESPONDENTE) && campoDescricao.equals(Columns.COR_NOME)) ||
                    			(tenCodigo.equals(Log.SERVIDOR) && campoDescricao.equals(Columns.SER_NOME)) ||
                    			(tenCodigo.equals(Log.REGISTRO_SERVIDOR) && campoDescricao.equals(Columns.RSE_MATRICULA)) ||
                    			(tenCodigo.equals(Log.USUARIO) && campoDescricao.equals(Columns.USU_LOGIN)) ||
                    			(tenCodigo.equals(Log.SERVICO) && campoDescricao.equals(Columns.SVC_DESCRICAO)) ||
                    			(tenCodigo.equals(Log.CONVENIO) && campoDescricao.equals(Columns.CNV_COD_VERBA)) ||
                    			(tenCodigo.equals(Log.PERFIL) && campoDescricao.equals(Columns.PER_DESCRICAO)) ||
                    			(tenCodigo.equals(Log.AUTORIZACAO) && campoDescricao.equals(Columns.ADE_NUMERO))) {
                        	where.append("AND ").append(campoDescricao).append(" like :entidade ");
                            queryParams.addValue("entidade", "%" + entidade + "%");
                        }
                    }
                    if (iteCampo.hasNext()) {
                        sql.append(",");
                    }
                }
            }
            if (sql.charAt(sql.length() -1) == ',') {
                sql.delete(sql.length() -1, sql.length());
            }
            sql.append(") AS OBSERVACAO ");

            if (nomeTabelaResultado.toLowerCase().equals("tb_resultado_relatorio")) {
                sql.append(", l.LOG_CANAL ");
            }

            sql.append("FROM ").append(nomeTabela).append("_").append(grupoFuncao).append(" l ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_TIPO_LOG).append(" tlo on (tlo.TLO_CODIGO = l.TLO_CODIGO) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_TIPO_ENTIDADE).append(" ten on (ten.TEN_CODIGO = l.TEN_CODIGO_LOG) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_FUNCAO).append(" fun on (fun.FUN_CODIGO = l.FUN_CODIGO_LOG) ");

            // Ligações de usuário e entidade relacionada ao usuário
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO).append(" usu on (usu.USU_CODIGO = l.USU_CODIGO_LOG) ");
            if (TextHelper.isNull(tipoEntidadeUsuario) || !tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_CSA)) {
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSE).append(" usuCse ON (usuCse.USU_CODIGO = usu.USU_CODIGO) ");
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" cse ON (cse.CSE_CODIGO = usuCse.CSE_CODIGO) ");
            }
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSA).append(" usuCsa ON (usuCsa.USU_CODIGO = usu.USU_CODIGO) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" csa ON (csa.CSA_CODIGO = usuCsa.CSA_CODIGO) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_COR).append(" usuCor ON (usuCor.USU_CODIGO = usu.USU_CODIGO) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_CORRESPONDENTE).append(" cor ON (cor.COR_CODIGO = usuCor.COR_CODIGO) ");
            sql.append("LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" AS TB_CONSIGNATARIA_COR ON (TB_CONSIGNATARIA_COR.CSA_CODIGO = cor.CSA_CODIGO) ");
            if (TextHelper.isNull(tipoEntidadeUsuario) || !tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_CSA)) {
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_ORG).append(" usuOrg ON (usuOrg.USU_CODIGO = usu.USU_CODIGO) ");
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" org ON (org.ORG_CODIGO = usuOrg.ORG_CODIGO) ");
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SER).append(" usuSer ON (usuSer.USU_CODIGO = usu.USU_CODIGO) ");
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_SERVIDOR).append(" ser ON (ser.SER_CODIGO = usuSer.SER_CODIGO) ");

                sql.append("LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SUP).append(" usuSup ON (usuSup.USU_CODIGO = usu.USU_CODIGO) ");
                sql.append("LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" sup ON (sup.CSE_CODIGO = usuSup.CSE_CODIGO) ");
            }

            sql.append(join);
            sql.append(" WHERE 1 = 1 ");

            if (!TextHelper.isNull(tipoOperador) && !TextHelper.isNull(operador)) {
                if (tipoOperador.equals("CSE")) {
                    sql.append(" AND cse.CSE_NOME like :operador ");
                } else if (tipoOperador.equals("CSA")) {
                    sql.append(" AND csa.CSA_NOME like :operador ");
                } else if (tipoOperador.equals("COR")) {
                    sql.append(" AND cor.COR_NOME like :operador ");
                } else if (tipoOperador.equals("SER")) {
                    sql.append(" AND ser.SER_NOME like :operador ");
                } else if (tipoOperador.equals("USU")) {
                    sql.append(" AND usu.USU_LOGIN like :operador ");
                } else if (tipoOperador.equals("SUP")) {
                    sql.append(" AND sup.CSE_NOME like :operador ");
                } else if (tipoOperador.equals("TODOS")) {
                    sql.append(" AND (cse.CSE_NOME like :operador ");
                    sql.append(" OR csa.CSA_NOME like :operador ");
                    sql.append(" OR cor.COR_NOME like :operador ");
                    sql.append(" OR ser.SER_NOME like :operador ");
                    sql.append(" OR sup.CSE_NOME like :operador ");
                    sql.append(" OR usu.USU_LOGIN like :operador) ");
                }
                queryParams.addValue("operador", "%" + operador + "%");
            }

			if (!TextHelper.isNull(tenCodigo) && !TextHelper.isNull(entidade) && !TextHelper.isNull(where) && where.length() > 0) {
				sql.append(where);
			}

            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
        }
    }

    @Override
    protected void criaTabela(String nomeTabela) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Exclui a tabela de relatório de auditoria caso já exista
        excluiTabela(nomeTabela);

        Map<String, Map<String, String>> lista = ControleTipoEntidade.getInstance().lstTipoEntidadePorEntidade();
        Iterator<String> ite = lista.keySet().iterator();

        LOG.debug("Criando tabela com dados para gerar relatório de auditoria: ['" + nomeTabela + "'].");
        StringBuilder query = new StringBuilder();
        query.setLength(0);
        query.append("CREATE TABLE ").append(nomeTabela).append(" (");
        query.append("TLO_CODIGO                     varchar(32)    not null, ");
        query.append("TEN_CODIGO_LOG                 varchar(32), ");
        query.append("USU_CODIGO_LOG                 varchar(32), ");
        query.append("FUN_CODIGO_LOG                 varchar(32), ");
        query.append("LOG_DATA                       datetime       not null, ");
        query.append("LOG_OBS                        text           not null, ");
        query.append("LOG_IP                         varchar(45), ");
        query.append("LOG_CANAL                      char(1), ");
        while (ite.hasNext()) {
            String chave = ite.next();
            query.append(Columns.getColumnName(chave).toUpperCase()).append(" varchar(32), ");
        }
        int count = 1;
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`TLO_CODIGO`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`TEN_CODIGO_LOG`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`USU_CODIGO_LOG`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`FUN_CODIGO_LOG`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count).append("` (`LOG_DATA`) ");
        query.append(")");

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    protected void criaTabelaGrupoFuncao(String nomeTabela, String grupoFuncao) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Exclui a tabela de relatório de auditoria caso já exista
        excluiTabela(nomeTabela);

        Map<String, Map<String, String>> lista = ControleTipoEntidade.getInstance().lstTipoEntidadePorEntidade();
        Iterator<String> ite = lista.keySet().iterator();

        LOG.debug("Criando tabela com dados para gerar relatório de auditoria: ['" + nomeTabela + "'].");
        StringBuilder query = new StringBuilder();
        query.setLength(0);
        query.append("CREATE TABLE ").append(nomeTabela).append(" (");
        query.append("TLO_CODIGO                     varchar(32)    not null, ");
        query.append("TEN_CODIGO_LOG                 varchar(32), ");
        query.append("USU_CODIGO_LOG                 varchar(32), ");
        query.append("FUN_CODIGO_LOG                 varchar(32), ");
        query.append("LOG_DATA                       datetime       not null, ");
        query.append("LOG_OBS                        text           not null, ");
        query.append("LOG_IP                         varchar(45), ");
        query.append("LOG_CANAL                      char(1), ");
        while (ite.hasNext()) {
            String chave = ite.next();
            query.append(Columns.getColumnName(chave).toUpperCase()).append(" varchar(32), ");
        }

        Map<String, List<String>> camposPorGrupoFuncao = getCamposPorGrupoFuncao();
        Iterator<String> iteCampos = camposPorGrupoFuncao.get(grupoFuncao).iterator();
        int count = 1;
        while (iteCampos.hasNext()) {
            query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`").append(iteCampos.next()).append("`), ");
        }

        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count).append("` (`LOG_DATA`) ");
        query.append(")");

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    protected void criaTabelaResultado(String nomeTabela) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Exclui a tabela de resultado do relatório de auditoria caso já exista
        excluiTabela(nomeTabela);

        LOG.debug("Criando tabela resultado para gerar relatório de auditoria: ['" + nomeTabela + "'].");
        StringBuilder query = new StringBuilder();
        query.setLength(0);
        query.append("CREATE TABLE ").append(nomeTabela).append(" (");
        query.append("CODIGO_ENTIDADE                varchar(32), ");
        query.append("USU_CODIGO                     varchar(32), ");
        query.append("TLO_CODIGO                     varchar(32)    not null, ");
        query.append("FUN_CODIGO                     varchar(32), ");
        query.append("TEN_CODIGO                     varchar(32), ");
        query.append("AUDITADO                       char(1)        not null default 'N', ");
        query.append("LOG_IP                         varchar(45), ");
        query.append("LOG_DATA                       datetime       not null, ");
        query.append("LOG_OBS                        text           not null, ");
        query.append("LOG_CANAL                      char(1), ");

        int count = 1;
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`CODIGO_ENTIDADE`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`USU_CODIGO`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`TLO_CODIGO`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`FUN_CODIGO`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count++).append("` (`TEN_CODIGO`), ");
        query.append("KEY `").append(nomeTabela.toUpperCase()).append("_IDX_").append(count).append("` (`LOG_DATA`) ");
        query.append(")");

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    protected void excluiTabela(String nomeTabela) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        LOG.debug("Excluindo tabela do relatório de auditoria: ['" + nomeTabela + "'].");
        StringBuilder query = new StringBuilder();
        query.setLength(0);
        query.append("DROP TABLE IF EXISTS ").append(nomeTabela).append("");

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }
}
