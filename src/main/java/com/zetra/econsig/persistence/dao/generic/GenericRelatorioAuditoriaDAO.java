package com.zetra.econsig.persistence.dao.generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.log.ControleTipoEntidade;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.RelatorioAuditoriaDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericRelatorioAuditoriaDAO</p>
 * <p>Description: Implementacao Genérica do DAO de relatório de Auditoria. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericRelatorioAuditoriaDAO implements RelatorioAuditoriaDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericRelatorioAuditoriaDAO.class);

    private static final String TB_LOG_HISTORICA_REGEX = "tb_log(_[0-9]{4}_[a-zA-Z0-9]+){0,1}";

    protected static final Map<String, String> CAMPO_DESCRICAO = new HashMap<>();

    static {
        CAMPO_DESCRICAO.put("ACR_CODIGO", null);
        CAMPO_DESCRICAO.put("ADE_CODIGO_DESTINO", null);
        CAMPO_DESCRICAO.put("ADE_CODIGO", Columns.ADE_NUMERO);
        CAMPO_DESCRICAO.put("CDE_CODIGO", null);
        CAMPO_DESCRICAO.put("CFT_CODIGO", null);
        CAMPO_DESCRICAO.put("CMN_CODIGO", null);
        CAMPO_DESCRICAO.put("CNV_CODIGO", Columns.CNV_COD_VERBA);
        CAMPO_DESCRICAO.put("COR_CODIGO", Columns.COR_NOME);
        CAMPO_DESCRICAO.put("CRS_CODIGO", Columns.CRS_DESCRICAO);
        CAMPO_DESCRICAO.put("CSA_CODIGO_DESTINO", null);
        CAMPO_DESCRICAO.put("CSA_CODIGO", Columns.CSA_NOME);
        CAMPO_DESCRICAO.put("CSE_CODIGO", Columns.CSE_NOME);
        CAMPO_DESCRICAO.put("ECO_CODIGO", Columns.ECO_NOME);
        CAMPO_DESCRICAO.put("EST_CODIGO", Columns.EST_NOME);
        CAMPO_DESCRICAO.put("FAQ_CODIGO", null);
        CAMPO_DESCRICAO.put("FUN_CODIGO", Columns.FUN_DESCRICAO);
        CAMPO_DESCRICAO.put("HMR_CODIGO", null);
        CAMPO_DESCRICAO.put("ITM_CODIGO", Columns.ITM_DESCRICAO);
        CAMPO_DESCRICAO.put("MEN_CODIGO", null);
        CAMPO_DESCRICAO.put("MNU_CODIGO", Columns.MNU_DESCRICAO);
        CAMPO_DESCRICAO.put("NSE_CODIGO", Columns.NSE_DESCRICAO);
        CAMPO_DESCRICAO.put("OCA_CODIGO", null);
        CAMPO_DESCRICAO.put("ORG_CODIGO", Columns.ORG_NOME);
        CAMPO_DESCRICAO.put("OUS_CODIGO", null);
        CAMPO_DESCRICAO.put("PAP_CODIGO", Columns.PAP_DESCRICAO);
        CAMPO_DESCRICAO.put("PCV_CODIGO", null);
        CAMPO_DESCRICAO.put("PER_CODIGO", Columns.PER_DESCRICAO);
        CAMPO_DESCRICAO.put("PRZ_CODIGO", null);
        CAMPO_DESCRICAO.put("PRZ_CSA_CODIGO", null);
        CAMPO_DESCRICAO.put("PSC_CODIGO", null);
        CAMPO_DESCRICAO.put("PSE_CODIGO", null);
        CAMPO_DESCRICAO.put("RSE_CODIGO_DESTINO", null);
        CAMPO_DESCRICAO.put("RSE_CODIGO", Columns.RSE_MATRICULA);
        CAMPO_DESCRICAO.put("SAD_CODIGO", Columns.SAD_DESCRICAO);
        CAMPO_DESCRICAO.put("SCV_CODIGO", Columns.SCV_DESCRICAO);
        CAMPO_DESCRICAO.put("SER_CODIGO", Columns.SER_NOME);
        CAMPO_DESCRICAO.put("SPD_CODIGO", Columns.SPD_DESCRICAO);
        CAMPO_DESCRICAO.put("SRS_CODIGO", Columns.SRS_DESCRICAO);
        CAMPO_DESCRICAO.put("STU_CODIGO", Columns.STU_DESCRICAO);
        CAMPO_DESCRICAO.put("SVC_CODIGO_DESTINO", null);
        CAMPO_DESCRICAO.put("SVC_CODIGO", Columns.SVC_DESCRICAO);
        CAMPO_DESCRICAO.put("TDA_CODIGO", Columns.TDA_DESCRICAO);
        CAMPO_DESCRICAO.put("TEN_CODIGO", Columns.TEN_DESCRICAO);
        CAMPO_DESCRICAO.put("TGC_CODIGO", Columns.TGC_DESCRICAO);
        CAMPO_DESCRICAO.put("TGS_CODIGO", null);
        CAMPO_DESCRICAO.put("TOC_CODIGO", Columns.TOC_DESCRICAO);
        CAMPO_DESCRICAO.put("TPA_CODIGO", Columns.TPA_DESCRICAO);
        CAMPO_DESCRICAO.put("TPE_CODIGO", Columns.TPE_DESCRICAO);
        CAMPO_DESCRICAO.put("TPS_CODIGO", Columns.TPS_DESCRICAO);
        CAMPO_DESCRICAO.put("TPT_CODIGO", Columns.TPT_DESCRICAO);
        CAMPO_DESCRICAO.put("USU_CODIGO", Columns.USU_LOGIN);
        CAMPO_DESCRICAO.put("VCO_CODIGO_DESTINO", null);
        CAMPO_DESCRICAO.put("VCO_CODIGO", null);
        CAMPO_DESCRICAO.put("VRS_CODIGO", Columns.VRS_DESCRICAO);
    }

    @Override
    public final void preparaDadosRelatorio(Map<String, Object> parametros) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        Connection conn = null;
        Statement stat = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            final String nomeTabela = "tb_rel_auditoria";
            final String tipoOperador = (String) getParametro("TIPO_OPERADOR", parametros);
            final String operador = (String) getParametro("OPERADOR", parametros);
            final String tenCodigo = (String) getParametro("TEN_CODIGO", parametros);
            final String entidade = (String) getParametro("ENTIDADE", parametros);
            final String tloCodigo = (String) getParametro("TLO_CODIGO", parametros);
            final String usuCodigo = (String) getParametro("USU_CODIGO", parametros);
            final String tipoEntidadeUsuario = (String) getParametro("TIPO_ENTIDADE_USUARIO", parametros);
            final String codigoEntidadeUsuario = (String) getParametro("CODIGO_ENTIDADE_USUARIO", parametros);
            final boolean auditoria = !TextHelper.isNull(getParametro("MODULO_AUDITORIA", parametros)) ? "true".equals(getParametro("MODULO_AUDITORIA", parametros).toString()) : false;
            final boolean somenteFuncoesSensiveis = !TextHelper.isNull(getParametro("SOMENTE_FUNCOES_SENSIVEIS", parametros)) ? "true".equals(getParametro("SOMENTE_FUNCOES_SENSIVEIS", parametros).toString()) : false;
            final String papCodigo = (String) getParametro("PAP_CODIGO", parametros);

            final List<String> funCodigo = (List<String>) getParametro("FUN_CODIGO", parametros);
            Date dataIni = null;
            Date dataFim = null;
            try {
                dataIni = !TextHelper.isNull(getParametro("DATA_INI", parametros)) ? DateHelper.parse(getParametro("DATA_INI", parametros).toString(), "yyyy-MM-dd HH:mm:ss") : null;
                dataFim = !TextHelper.isNull(getParametro("DATA_FIM", parametros)) ? DateHelper.parse(getParametro("DATA_FIM", parametros).toString(), "yyyy-MM-dd HH:mm:ss") : null;
            } catch (final ParseException e) {
                throw new DAOException("mensagem.erro.periodo.parse.invalido", (AcessoSistema) null);
            }

            final List<TransferObject> histArqLog = new LogDelegate().lstHistoricoArqLog(dataIni, dataFim);

            if (histArqLog != null) { // && histArqLog.size() > 0) {
                // Monta os campos chave que podem estar presentes no log de acordo com os tipos entidade e seus LOG_COD_ENT setados no banco de dados
                final StringBuilder campos = new StringBuilder();

                // Monta qual LOG_COD_ENT deve ser exibido Através de cases para determinar qual o código correto
                // Ex.: Para a entidade X, LOG_COD_ENT_00 é RSE_CODIGO e para a entidade Y, RSE_CODIGO é o LOG_COG_ENT_03
                final StringBuilder camposCase = new StringBuilder();

                final Map<String, Map<String, String>> lista = ControleTipoEntidade.getInstance().lstTipoEntidadePorEntidade();
                final Iterator<String> ite = lista.keySet().iterator();
                while (ite.hasNext()) {
                    final String campo = ite.next();

                    // Accept only "campo" value that matches this pattern CSA_CODIGO or CSA_CODIGO_ORIGEM
                    if (campo.matches("[a-zA-Z0-9]{3}_[a-zA-Z0-9]+") || campo.matches("[a-zA-Z0-9]{3}_[a-zA-Z0-9]+_[a-zA-Z0-9]+")) {
                        campos.append(campo);
                        if (ite.hasNext()) {
                            campos.append(", ");
                        }

                        final Map<String, String> map = lista.get(campo);
                        camposCase.append("CASE ");
                        for (final String chave : map.keySet()) {
                            final String valor = map.get(chave);
                            // Accept only "chave" value that is numeric and "valor" when matches tb_log.log_cod_ent_XX pattern
                            if (chave.matches("[0-9]+") && valor.toLowerCase().matches("tb_log.log_cod_ent_[0-9]+")) {
                                camposCase.append("WHEN TEN_CODIGO = '").append(chave).append("' then ").append(valor).append(" ");
                            } else {
                                LOG.warn("Configuracao incorreta de tipo de entidade para o '" + campo + "' com tipo de codigo '" + chave + "'");
                            }
                        }
                        camposCase.append("ELSE NULL ");
                        camposCase.append("END AS ").append(campo.toUpperCase());
                        if (ite.hasNext()) {
                            camposCase.append(", ");
                        }
                    } else {
                        LOG.warn("Configuracao incorreta de tipo de entidade para o '" + campo + "', nao esta no padrao de nome aceito");
                    }
                }

                // Se for módulo de auditoria e o tipo de entidade for consignante, exibe auditoria total de matrícula especial.
                final boolean exibeAuditoriaTotal = auditoria && (!TextHelper.isNull(tipoEntidadeUsuario) && AcessoSistema.ENTIDADE_CSE.equals(tipoEntidadeUsuario));
                /* Recupera registros servidores para auditoria total somente se exibe auditoria total de matrícula especial.
                 * Auditoria total de um registro servidor é definida pelo campo RSE_AUDITORIA_TOTAL.
                 */
                final List<String> rseCodigos = exibeAuditoriaTotal ? getRegistroServidorAuditoriaTotal() : null;

                // Cria e popula tabela de auditoria total com os logs que serão tratados para geração do relatório caso exista registro servidor marcado como auditoria total
                String nomeTabelaAuditoriaTotal = null;
                if ((rseCodigos != null) && !rseCodigos.isEmpty()) {
                    nomeTabelaAuditoriaTotal = recuperaLogRseAuditoriaTotal(histArqLog, campos, camposCase, codigoEntidadeUsuario, tipoEntidadeUsuario, dataIni, dataFim, auditoria);
                }

                // Cria tabela com os logs que serão tratados para geração do relatório
                criaTabela(nomeTabela);

                if (!histArqLog.isEmpty()) {
                    /*
                     * Através das tabelas de histórico de log, são selecionados os dados pertinentes aos filtros passados e
                     * setados os códigos em seus respectivos campos em uma nova tabela.
                     * Lembrando que para cada tipo de entidade, existe um mapeamento específico para cada código.
                     * Ex.: Para a entidade X, LOG_COD_ENT_00 é RSE_CODIGO e para a entidade Y, RSE_CODIGO é o LOG_COG_ENT_03
                     */
                    final StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO ").append(nomeTabela).append(" (");
                    sql.append("TLO_CODIGO, TEN_CODIGO_LOG, USU_CODIGO_LOG, FUN_CODIGO_LOG, LOG_DATA, LOG_OBS, LOG_IP, LOG_CANAL, ");
                    sql.append(campos);
                    sql.append(") ");

                    final Iterator<TransferObject> i = histArqLog.iterator();
                    while (i.hasNext()) {
                        final TransferObject to = i.next();
                        final String tabelaLogHistorico = to.getAttribute(Columns.HAL_NOME_TABELA).toString();

                        // Error if table name doesn't follow expected pattern
                        if (!tabelaLogHistorico.matches(TB_LOG_HISTORICA_REGEX)) {
                            LOG.warn("Nome invalido tabela historica de log: '" + tabelaLogHistorico + "'");
                            throw new DAOException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema());
                        }

                        sql.append("SELECT TLO_CODIGO, TEN_CODIGO, ").append(tabelaLogHistorico).append(".USU_CODIGO, FUN_CODIGO, LOG_DATA, LOG_OBS, CASE WHEN LOG_PORTA IS NULL THEN LOG_IP ELSE CONCAT(CONCAT(LOG_IP, ':'), LOG_PORTA) END, LOG_CANAL, ");
                        String camposClausula = camposCase.toString();
                        camposClausula = camposClausula.replace(Columns.TB_LOG.toLowerCase(), tabelaLogHistorico);
                        camposClausula = camposClausula.replace(Columns.TB_LOG.toUpperCase(), tabelaLogHistorico);
                        sql.append(camposClausula);
                        sql.append(" FROM ").append(tabelaLogHistorico);

                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO).append(" ON ").append(Columns.USU_CODIGO).append(" = ").append(tabelaLogHistorico).append(".USU_CODIGO");
                        if (TextHelper.isNull(tipoEntidadeUsuario) || !AcessoSistema.ENTIDADE_CSA.equals(tipoEntidadeUsuario)) {
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSE).append(" ON ").append(Columns.UCE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" ON ").append(Columns.CSE_CODIGO).append(" = ").append(Columns.UCE_CSE_CODIGO);
                        }
                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSA).append(" ON ").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.UCA_CSA_CODIGO);
                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_COR).append(" ON ").append(Columns.UCO_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CORRESPONDENTE).append(" ON ").append(Columns.COR_CODIGO).append(" = ").append(Columns.UCO_COR_CODIGO);
                        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" TB_CONSIGNATARIA_COR ON ").append("TB_CONSIGNATARIA_COR.CSA_CODIGO = ").append(Columns.COR_CSA_CODIGO);
                        if (TextHelper.isNull(tipoEntidadeUsuario) || !AcessoSistema.ENTIDADE_CSA.equals(tipoEntidadeUsuario)) {
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_ORG).append(" ON ").append(Columns.UOR_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON ").append(Columns.ORG_CODIGO).append(" = ").append(Columns.UOR_ORG_CODIGO);
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SER).append(" ON ").append(Columns.USE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVIDOR).append(" ON ").append(Columns.SER_CODIGO).append(" = ").append(Columns.USE_SER_CODIGO);

                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SUP).append(" ON ").append(Columns.USP_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" sup ON sup.").append(Columns.getColumnName(Columns.CSE_CODIGO)).append(" = ").append(Columns.USP_CSE_CODIGO);
                        }

                        sql.append(" WHERE 1 = 1");
                        sql.append(" AND LOG_DATA BETWEEN :dataIni AND :dataFim");
                        queryParams.addValue("dataIni", dataIni);
                        queryParams.addValue("dataFim", dataFim);

                        if (!TextHelper.isNull(tloCodigo)) {
                            sql.append(" AND TLO_CODIGO = :tloCodigo");
                            queryParams.addValue("tloCodigo", tloCodigo);
                        }
                        if (!TextHelper.isNull(usuCodigo)) {
                            sql.append(" AND ").append(tabelaLogHistorico).append(".USU_CODIGO = :usuCodigo");
                            queryParams.addValue("usuCodigo", usuCodigo);
                        }
                        if (!TextHelper.isNull(tenCodigo)) {
                            sql.append(" AND TEN_CODIGO = :tenCodigo");
                            queryParams.addValue("tenCodigo", tenCodigo);
                        }
                        // Se filtra pelas funções sensíveis, não deve filtrar por uma função específica
                        if (somenteFuncoesSensiveis) {
                            sql.append(" AND FUN_CODIGO in (:funCodigos)");
                            queryParams.addValue("funCodigos", createQueryFuncoesSensiveis(papCodigo));
                        } else if ((funCodigo != null) && !funCodigo.isEmpty()) {
                            sql.append(" AND FUN_CODIGO in (:funCodigos)");
                            queryParams.addValue("funCodigos", funCodigo);
                        }

                        if (!TextHelper.isNull(codigoEntidadeUsuario)) {
                            if (!AcessoSistema.ENTIDADE_CSA.equals(tipoEntidadeUsuario)) {
                                sql.append(" AND (").append(Columns.CSE_CODIGO).append(" = :codigoEntidadeUsuario ");
                                sql.append(" OR ").append(Columns.ORG_CODIGO).append(" = :codigoEntidadeUsuario ");
                                sql.append(" OR ").append(Columns.CSA_CODIGO).append(" = :codigoEntidadeUsuario ");
                                sql.append(" OR ").append(Columns.COR_CODIGO).append(" = :codigoEntidadeUsuario ");
                                sql.append(" OR sup.").append(Columns.getColumnName(Columns.CSE_CODIGO)).append(" = :codigoEntidadeUsuario ");
                                sql.append(")");
                            } else {
                                sql.append(" AND (").append(Columns.CSA_CODIGO).append(" = :codigoEntidadeUsuario ");
                                sql.append(" OR ").append(Columns.COR_CSA_CODIGO).append(" = :codigoEntidadeUsuario ").append(")");
                            }
                            queryParams.addValue("codigoEntidadeUsuario", codigoEntidadeUsuario);
                        }

                        if (auditoria && !TextHelper.isNull(tipoEntidadeUsuario)) {
                            if ("CSE".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.UCE_USU_CODIGO).append(" IS NOT NULL");
                            } else if ("CSA".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.UCA_USU_CODIGO).append(" IS NOT NULL");
                            } else if ("COR".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.UCO_USU_CODIGO).append(" IS NOT NULL");
                            } else if ("ORG".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.UOR_USU_CODIGO).append(" IS NOT NULL");
                            } else if ("SER".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.USE_USU_CODIGO).append(" IS NOT NULL");
                            } else if ("SUP".equals(tipoEntidadeUsuario)) {
                                sql.append(" AND ").append(Columns.USP_USU_CODIGO).append(" IS NOT NULL");
                            }
                        }

                        if (i.hasNext()) {
                            sql.append(" UNION ALL ");
                        }
                    }
                    LOG.trace(sql.toString());
                    jdbc.update(sql.toString(), queryParams);
                }

                final List<String> tabelasResult = criaLogPorGrupoFuncao(stat, nomeTabela, nomeTabelaAuditoriaTotal, campos, rseCodigos);
                final String nomeTabelaResultado = recuperaTabelaEntidade(auditoria, tipoEntidadeUsuario);
                /*
                 * Caso seja um processo do módulo de Auditoria, utiliza as tabelas de armazenamento de dados dos relatórios gerados.
                 * Essa implementação permite que dados sejam incluídos por um processo paralelo nessas tabelas, permitindo serem extraídos no módulo de Auditoria.
                 * No relatório de Auditoria agendado pelo usuário no sistema, uma tabela padrão será recriada a cada geração de relatório.
                 */
                if (!auditoria) {
                    criaTabelaResultado(nomeTabelaResultado);
                }
                populaTabelaResultado(conn, nomeTabelaResultado, nomeTabela, tipoOperador, operador, tenCodigo, entidade, tipoEntidadeUsuario, auditoria, tabelasResult, rseCodigos);
            }
        } catch (final DataAccessException | SQLException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }

	protected Object getParametro(String parametro, Map<String, Object> parameterMap) {
        return (parameterMap != null) && (parameterMap.get("CRITERIO") != null) ? ((CustomTransferObject) parameterMap.get("CRITERIO")).getAttribute(parametro) : null;
    }

    /**
     * Cria tabela que será utilizada para incluir os possíveis logs candidatos para gerar o relatório de auditoria.
     *
     * @param stat
     * @param nomeTabela
     * @throws DataAccessException
     */
    protected abstract void criaTabela(String nomeTabela) throws DataAccessException;

    /**
     * @param stat
     * @param nomeTabela
     * @throws DataAccessException
     */
    protected abstract void criaTabelaResultado(String nomeTabela) throws DataAccessException;

    /**
     * Cria tabela baseada no grupo função passado que será utilizada para dividir o log por grupo de função.
     * A criação de tabelas separadas por grupo de função tenta solucionar o problema de fazer ligações com tabelas desnecessárias que não possuem vínculo com um determinado grupo de função.
     *
     * @param stat
     * @param nomeTabela
     * @param grupoFuncao
     * @throws DataAccessException
     */
    protected abstract void criaTabelaGrupoFuncao(String nomeTabela, String grupoFuncao) throws DataAccessException;

    /**
     * Popula a tabela com o resultado encontrado para gerar o relatório de auditoria.
     * Sobre essa tabela será gerado o relatório de auditoria.
     *
     * @param conn
     * @param nomeTabelaResultado
     * @param nomeTabela
     * @param tipoOperador
     * @param operador
     * @param tenCodigo
     * @param entidade
     * @param tipoEntidadeUsuario
     * @param auditoria
     * @param tabelasResult
     * @param rseCodigos
     * @throws DataAccessException
     */
    protected abstract void populaTabelaResultado(Connection conn, String nomeTabelaResultado, String nomeTabela,
                                                  String tipoOperador, String operador, String tenCodigo, String entidade, String tipoEntidadeUsuario,
                                                  boolean auditoria, List<String> tabelasResult, List<String> rseCodigos) throws SQLException;

    /**
     * Exclui a tabela passada por parâmetro.
     *
     * @param stat
     * @param nomeTabela
     * @throws DataAccessException
     */
    protected abstract void excluiTabela(String nomeTabela) throws DataAccessException;

    /**
     * Recupera a tabela que deverá armazenar os logs para a geração do relatório de auditoria.
     * Caso seja módulo de auditoria e um tipo de entidade seja informado, o log ao invés de ser armazenado em uma tabela temporária,
     * será incluído em uma tabela de histórico de log de auditoria por tipo de entidade.
     *
     * @param moduloAuditoria
     * @param tipoEntidade
     * @return
     */
    protected String recuperaTabelaEntidade(boolean moduloAuditoria, String tipoEntidade) {
        String tabela = "tb_resultado_relatorio";

        if (!TextHelper.isNull(tipoEntidade) && moduloAuditoria) {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                tabela = Columns.TB_AUDITORIA_CSE;
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                tabela = Columns.TB_AUDITORIA_CSA;
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                tabela = Columns.TB_AUDITORIA_COR;
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
                tabela = Columns.TB_AUDITORIA_ORG;
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                tabela = Columns.TB_AUDITORIA_SUP;
            }
        }

        return tabela;
    }

    protected static final String getTableName(String nome) {
        final int indice = nome.indexOf('.');
        if (indice == -1) {
            return nome;
        } else {
            return nome.substring(0, indice);
        }
    }

    /**
     * Recupera o campos vinculados ao seu grupo de função.
     * Com base nesse mapeamento é feito os joins entre as tabelas de log e suas possíveis chaves estrangeiras.
     *
     * @return Retorna o campos vinculados ao seu grupo de função.
     */
    protected final Map<String, List<String>> getCamposPorGrupoFuncao() {
        // TODO Alterar esse método para ser mais dinâmico
        final Map<String, List<String>> camposPorGrupoFuncao = new HashMap<>();
        List<String> campos = new ArrayList<>();

        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_GERAL, campos);
        campos.add("ADE_CODIGO");
        campos.add("RSE_CODIGO");
        campos.add("SER_CODIGO");
        campos.add("USU_CODIGO");
        campos.add("FUN_CODIGO");
        campos.add("PAP_CODIGO");
        campos.add("TOC_CODIGO");
        campos.add("MEN_CODIGO");
        campos.add("FAQ_CODIGO");
        campos.add("TEN_CODIGO");
        campos.add("TPE_CODIGO");
        campos.add("CMN_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_OPERACIONAL, campos);
        campos.add("ADE_CODIGO");
        campos.add("RSE_CODIGO");
        campos.add("SER_CODIGO");
        campos.add("CSE_CODIGO");
        campos.add("ORG_CODIGO");
        campos.add("CSA_CODIGO");
        campos.add("COR_CODIGO");
        campos.add("VRS_CODIGO");
        campos.add("CRS_CODIGO");
        campos.add("SRS_CODIGO");
        campos.add("USU_CODIGO");
        campos.add("SVC_CODIGO");
        campos.add("CNV_CODIGO");
        campos.add("VCO_CODIGO");
        campos.add("TPS_CODIGO");
        campos.add("SAD_CODIGO");
        campos.add("SPD_CODIGO");
        campos.add("PRZ_CODIGO");
        campos.add("PRZ_CSA_CODIGO");
        campos.add("CFT_CODIGO");
        campos.add("CDE_CODIGO");
        campos.add("PSC_CODIGO");
        campos.add("TOC_CODIGO");
        campos.add("TDA_CODIGO");
        campos.add("TEN_CODIGO");
        campos.add("OCA_CODIGO");
        campos.add("CSA_CODIGO_DESTINO");
        campos.add("SVC_CODIGO_DESTINO");
        campos.add("RSE_CODIGO_DESTINO");
        campos.add("VCO_CODIGO_DESTINO");
        campos.add("ADE_CODIGO_DESTINO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_COMPRA_CONTRATO, campos);
        campos.add("ADE_CODIGO");
        campos.add("RSE_CODIGO");
        campos.add("SER_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_SIMULACAO, campos);
        campos.add("ADE_CODIGO");
        campos.add("RSE_CODIGO");
        campos.add("SER_CODIGO");
        campos.add("CSE_CODIGO");
        campos.add("ORG_CODIGO");
        campos.add("CSA_CODIGO");
        campos.add("COR_CODIGO");
        campos.add("VRS_CODIGO");
        campos.add("CRS_CODIGO");
        campos.add("SRS_CODIGO");
        campos.add("USU_CODIGO");
        campos.add("CNV_CODIGO");
        campos.add("VCO_CODIGO");
        campos.add("TPS_CODIGO");
        campos.add("SAD_CODIGO");
        campos.add("SPD_CODIGO");
        campos.add("PSC_CODIGO");
        campos.add("OCA_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_RELATORIOS, campos);
        campos.add("CSA_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_CSE, campos);
        campos.add("CSE_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_EST, campos);
        campos.add("CSE_CODIGO");
        campos.add("EST_CODIGO");
        campos.add("ORG_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_ORG, campos);
        campos.add("EST_CODIGO");
        campos.add("ORG_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_SER, campos);
        campos.add("SER_CODIGO");
        campos.add("RSE_CODIGO");
        campos.add("ORG_CODIGO");
        campos.add("VRS_CODIGO");
        campos.add("CRS_CODIGO");
        campos.add("SRS_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_CSA, campos);
        campos.add("CSA_CODIGO");
        campos.add("TGC_CODIGO");
        campos.add("TPA_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_COR, campos);
        campos.add("CSA_CODIGO");
        campos.add("COR_CODIGO");
        campos.add("ECO_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_SVC, campos);
        campos.add("CSE_CODIGO");
        campos.add("SVC_CODIGO");
        campos.add("TGS_CODIGO");
        campos.add("NSE_CODIGO");
        campos.add("PCV_CODIGO");
        campos.add("TPT_CODIGO");
        campos.add("PSE_CODIGO");
        campos.add("TPS_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_CNV, campos);
        campos.add("RSE_CODIGO");
        campos.add("ORG_CODIGO");
        campos.add("CSA_CODIGO");
        campos.add("COR_CODIGO");
        campos.add("VRS_CODIGO");
        campos.add("SVC_CODIGO");
        campos.add("CNV_CODIGO");
        campos.add("SCV_CODIGO");
        campos.add("VCO_CODIGO");
        campos.add("TPS_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_USU, campos);
        campos.add("USU_CODIGO");
        campos.add("STU_CODIGO");
        campos.add("PER_CODIGO");
        campos.add("FUN_CODIGO");
        campos.add("OUS_CODIGO");
        campos.add("TOC_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_MANUTENCAO_USU_SER, campos);
        campos.add("RSE_CODIGO");
        campos.add("SER_CODIGO");
        campos.add("USU_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_INTEGRACAO_FOLHA, campos);
        campos.add("EST_CODIGO");
        campos.add("ORG_CODIGO");

        campos = new ArrayList<>();
        camposPorGrupoFuncao.put(CodedValues.GRUPO_FUNCAO_ADMINISTRADOR, campos);
        campos.add("USU_CODIGO");
        campos.add("PER_CODIGO");
        campos.add("HMR_CODIGO");
        campos.add("ACR_CODIGO");
        campos.add("ITM_CODIGO");
        campos.add("MNU_CODIGO");

        return camposPorGrupoFuncao;
    }

    /**
     * Recupera os logs de registro servidor que podem estar vinculados à um registro servidor marcado para auditoria total.
     *
     * @param stat
     * @param histArqLog
     * @param campos
     * @param camposCase
     * @param codigoEntidadeUsuario
     * @param tipoEntidadeUsuario
     * @param dataIni
     * @param dataFim
     * @param auditoria
     * @return
     * @throws DataAccessException
     */
    private String recuperaLogRseAuditoriaTotal(List<TransferObject> histArqLog, StringBuilder campos, StringBuilder camposCase,
                                                String codigoEntidadeUsuario, String tipoEntidadeUsuario, Date dataIni, Date dataFim, boolean auditoria) throws DataAccessException, DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final String nomeTabela = "tb_rel_aud_matricula_especial";
        criaTabela(nomeTabela);

        if (!histArqLog.isEmpty()) {
            /*
             * Através das tabelas de histórico de log, são selecionados os dados pertinentes aos filtros passados e
             * setados os códigos em seus respectivos campos em uma nova tabela.
             * Lembrando que para cada tipo de entidade, existe um mapeamento específico para cada código.
             * Ex.: Para a entidade X, LOG_COD_ENT_00 é RSE_CODIGO e para a entidade Y, RSE_CODIGO é o LOG_COG_ENT_03
             */
            final StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(nomeTabela).append(" (");
            sql.append("TLO_CODIGO, TEN_CODIGO_LOG, USU_CODIGO_LOG, FUN_CODIGO_LOG, LOG_DATA, LOG_OBS, LOG_IP, LOG_CANAL, ");
            sql.append(campos);
            sql.append(") ");

            final Iterator<TransferObject> i = histArqLog.iterator();
            while (i.hasNext()) {
                final TransferObject to = i.next();
                final String tabelaLogHistorico = to.getAttribute(Columns.HAL_NOME_TABELA).toString();

                // Error if table name doesn't follow expected pattern
                if (!tabelaLogHistorico.matches(TB_LOG_HISTORICA_REGEX)) {
                    LOG.warn("Nome invalido tabela historica de log: '" + tabelaLogHistorico + "'");
                    throw new DAOException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema());
                }

                sql.append("SELECT TLO_CODIGO, TEN_CODIGO, ").append(tabelaLogHistorico).append(".USU_CODIGO, FUN_CODIGO, LOG_DATA, LOG_OBS, LOG_IP, LOG_CANAL, ");
                String camposClausula = camposCase.toString();
                camposClausula = camposClausula.replace(Columns.TB_LOG.toLowerCase(), tabelaLogHistorico);
                camposClausula = camposClausula.replace(Columns.TB_LOG.toUpperCase(), tabelaLogHistorico);
                sql.append(camposClausula);
                sql.append(" FROM ").append(tabelaLogHistorico);

                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO).append(" ON ").append(Columns.USU_CODIGO).append(" = ").append(tabelaLogHistorico).append(".USU_CODIGO");
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSE).append(" ON ").append(Columns.UCE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" ON ").append(Columns.CSE_CODIGO).append(" = ").append(Columns.UCE_CSE_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_CSA).append(" ON ").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.UCA_CSA_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_COR).append(" ON ").append(Columns.UCO_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CORRESPONDENTE).append(" ON ").append(Columns.COR_CODIGO).append(" = ").append(Columns.UCO_COR_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" TB_CONSIGNATARIA_COR ON ").append("TB_CONSIGNATARIA_COR.CSA_CODIGO = ").append(Columns.COR_CSA_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_ORG).append(" ON ").append(Columns.UOR_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON ").append(Columns.ORG_CODIGO).append(" = ").append(Columns.UOR_ORG_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SER).append(" ON ").append(Columns.USE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVIDOR).append(" ON ").append(Columns.SER_CODIGO).append(" = ").append(Columns.USE_SER_CODIGO);

                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_USUARIO_SUP).append(" ON ").append(Columns.USP_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO);
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNANTE).append(" sup ON sup.").append(Columns.getColumnName(Columns.CSE_CODIGO)).append(" = ").append(Columns.USP_CSE_CODIGO);

                sql.append(" WHERE 1 = 1");
                sql.append(" AND LOG_DATA BETWEEN :dataIni AND :dataFim");
                queryParams.addValue("dataIni", dataIni);
                queryParams.addValue("dataFim", dataFim);

                if (i.hasNext()) {
                    sql.append(" UNION ALL ");
                }
            }
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
        }

        return nomeTabela;
    }

    /**
     * Recupera os registros servidores que estão selecionados para auditoria total.
     *
     * @return
     * @throws DAOException
     */
    private List<String> getRegistroServidorAuditoriaTotal() throws DAOException {
        try {
            final List<String> rseCodigos = new ArrayList<>();

            final ServidorDelegate serDelegate = new ServidorDelegate();
            final List<TransferObject> registros = serDelegate.lstRegistroServidorAuditoriaTotal(AcessoSistema.getAcessoUsuarioSistema());
            if ((registros != null) && !registros.isEmpty()) {
                for (final TransferObject to : registros) {
                    rseCodigos.add(to.getAttribute(Columns.RSE_CODIGO).toString());
                }
            }

            // Se não existe registro servidor para auditoria total, não precisa procurar por logs de auditoria
            if ((registros == null) || rseCodigos.isEmpty()) {
                return null;
            }

            return rseCodigos;
        } catch (final ServidorControllerException e) {
            throw new DAOException("mensagem.erro.log.auditoria.servidor.nao.encontrado", (AcessoSistema) null, e);
        }
    }

    /**
     * Cria tabelas separadas por grupo de função e popula as tabelas com os dados encontrados na tabela passada por parâmetro.
     * Retorna as tabelas separadas por grupo de função que possuem dados populados.
     * As tabelas foram separadas por grupo de função para evitar a ligação desnecessária com outras tabelas que não se relacionam com um determinado grupo de função.
     *
     * @param stat
     * @param nomeTabela
     * @param nomeTabelaAuditoriaTotal
     * @param campos
     * @param rseCodigos
     * @return Retorna as tabelas separadas por grupo de função que possuem dados populados.
     * @throws DataAccessException
     */
    private List<String> criaLogPorGrupoFuncao(Statement stat, String nomeTabela, String nomeTabelaAuditoriaTotal, StringBuilder campos, List<String> rseCodigos) throws SQLException {
        // Variáveis para acesso ao banco
        final StringBuilder sql = new StringBuilder();
        ResultSet rs = null;

        // Recupera grupos de função
        final List<String> gruposFuncao = new ArrayList<>(getCamposPorGrupoFuncao().keySet());
        // Cria tabelas por grupo de função
        for (final String grupoFuncao : gruposFuncao) {
            criaTabelaGrupoFuncao(nomeTabela + "_" + grupoFuncao, grupoFuncao);
        }

        separaLogPorGrupoFuncao(nomeTabela, nomeTabela, campos, rseCodigos, gruposFuncao, false);
        if ((rseCodigos != null) && !rseCodigos.isEmpty()) {
            separaLogPorGrupoFuncao(nomeTabela, nomeTabelaAuditoriaTotal, campos, rseCodigos, gruposFuncao, true);
        }

        // Seleciona os valores montando a tabela de resultado que será usada para gerar o relatório
        final List<String> tabelasResult = new ArrayList<>();
        for (final String grupoFuncao : gruposFuncao) {
            sql.setLength(0);
            sql.append("SELECT COUNT(*) AS QTDE ");
            sql.append("FROM ").append(nomeTabela).append("_").append(grupoFuncao);
            LOG.trace(sql.toString());
            rs = stat.executeQuery(sql.toString());
            if (rs.next() && (rs.getInt("QTDE") > 0)) {
                tabelasResult.add(grupoFuncao);
            }
        }

        return tabelasResult;
    }

    /**
     * Popula as tabelas criadas por grupo de função com os dados encontrados na tabela passada por parâmetro.
     *
     * @param stat
     * @param nomeTabelaInsert
     * @param nomeTabelaSelect
     * @param campos
     * @param rseCodigos
     * @param gruposFuncao
     * @param auditoriaTotal
     * @throws DataAccessException
     */
    private void separaLogPorGrupoFuncao(String nomeTabelaInsert, String nomeTabelaSelect, StringBuilder campos, List<String> rseCodigos, List<String> gruposFuncao, boolean auditoriaTotal) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        for (final String grupoFuncao : gruposFuncao) {
            sql.setLength(0);
            sql.append("INSERT INTO ").append(nomeTabelaInsert).append("_").append(grupoFuncao).append(" (");
            sql.append("TLO_CODIGO, TEN_CODIGO_LOG, USU_CODIGO_LOG, FUN_CODIGO_LOG, LOG_DATA, LOG_OBS, LOG_IP, LOG_CANAL, ");
            sql.append(campos);
            sql.append(") ");
            sql.append("SELECT TLO_CODIGO, TEN_CODIGO_LOG, USU_CODIGO_LOG, FUN_CODIGO_LOG, LOG_DATA, LOG_OBS, LOG_IP, LOG_CANAL, ");
            sql.append("l." + campos.toString().replace(", ", ", l."));
            sql.append(" FROM ").append(nomeTabelaSelect).append(" l ");
            sql.append("LEFT OUTER JOIN tb_funcao fun ON (l.FUN_CODIGO_LOG = fun.fun_codigo) ");
            sql.append("LEFT OUTER JOIN tb_grupo_funcao grf ON (grf.grf_codigo = fun.grf_codigo) ");

            // Se possuir matriculas especiais
            if ((rseCodigos != null) && !rseCodigos.isEmpty()) {
                sql.append("LEFT OUTER JOIN tb_registro_servidor rse ON (l.RSE_CODIGO = rse.rse_codigo) ");
            }

            sql.append("WHERE (grf.grf_codigo = '").append(grupoFuncao).append("' ");
            if (CodedValues.GRUPO_FUNCAO_GERAL.equals(grupoFuncao)) {
                sql.append("OR grf.grf_codigo IS NULL ");
            }
            sql.append(")");
            if ((rseCodigos != null) && !rseCodigos.isEmpty()) {
                if (auditoriaTotal) {
                    sql.append(" AND (rse.rse_codigo IN (:rseCodigos))");
                    queryParams.addValue("rseCodigos", rseCodigos);
                } else {
                    sql.append(" AND (rse.rse_codigo IS NULL OR rse.rse_codigo NOT IN (:rseCodigos))");
                    queryParams.addValue("rseCodigos", rseCodigos);
                }
            }

            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
        }
    }

    protected String createQueryFuncoesSensiveis(String papCodigo) throws DAOException {
    	String campoExigeSegundaSenhaPapel = "";

        if (!TextHelper.isNull(papCodigo)) {
            if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(papCodigo)) {
            	campoExigeSegundaSenhaPapel = Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE;
            } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(papCodigo)) {
            	campoExigeSegundaSenhaPapel = Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA;
            } else if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(papCodigo)) {
            	campoExigeSegundaSenhaPapel = Columns.FUN_EXIGE_SEGUNDA_SENHA_COR;
            } else if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(papCodigo)) {
            	campoExigeSegundaSenhaPapel = Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG;
            } else if (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(papCodigo)) {
            	campoExigeSegundaSenhaPapel = Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP;
            }
        } else {
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

    	final StringBuilder query = new StringBuilder();
    	query.append("SELECT f.FUN_CODIGO FROM tb_funcao f WHERE ").append(Columns.getColumnName(campoExigeSegundaSenhaPapel)).append(" <> '").append(CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO).append("'");

    	return query.toString();
    }
}
