package com.zetra.econsig.persistence.dao.generic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioPelosIdentificadoresQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericImpRetornoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de Imp. Retorno. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericImpRetornoDAO implements ImpRetornoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericImpRetornoDAO.class);

    protected static final String TB_ARQ_RETORNO_INSERT_SQL = "INSERT INTO tb_arquivo_retorno (nome_arquivo, id_linha, cnv_cod_verba, "
            + "est_identificador, org_identificador, csa_identificador, svc_identificador, "
            + "ano_mes_desconto, prd_vlr_realizado, prd_data_realizado, "
            + "ade_numero, ade_indice, ade_cod_reg, ade_data, ade_ano_mes_ini, ade_ano_mes_fim, "
            + "ade_prd_pagas, ade_prazo, ade_carencia, "
            + "ocp_obs, spd_codigo, quitacao, tipo_envio, tde_codigo, rse_matricula, ser_nome, ser_cpf, pos_identificador, "
            + "mapeada, processada, pode_pagar_consolidacao_exata, art_ferias, linha) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected String nomeArqRetorno;
    protected Connection conn;
    protected PreparedStatement pstatLinhasArquivo;
    protected int linhasCarregadas = 0;
    protected boolean retAtrasadoSomaAparcela = false;

    @Override
    public void setNomeArqRetorno(String nomeArqRetorno) {
        this.nomeArqRetorno = nomeArqRetorno;
    }

    /**
     * opção que define se no retorno atrasado o valor realizado deste arquivo deve ser somado ao realizado da tabela de histórico de parcela.
     */
    @Override
    public void setRetAtrasadoSomaAparcela(boolean somaParcela) throws DAOException {
        retAtrasadoSomaAparcela = somaParcela;
    }

    @Override
    public void iniciaCargaArquivoRetorno(String nomeArqRetorno, boolean mantemArqRetorno, List<String> orgIdentRemocao) throws DAOException {
        try {
            this.nomeArqRetorno = nomeArqRetorno;
            conn = DBHelper.makeConnection();
            pstatLinhasArquivo = conn.prepareStatement(TB_ARQ_RETORNO_INSERT_SQL);

            LOG.debug("Início da carga do arquivo de retorno");

            // Limpa a tabela de arquivo de retorno
            limpaTabelaRetorno(mantemArqRetorno, orgIdentRemocao);

        } catch (final SQLException | DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    protected void limpaTabelaRetorno(boolean mantemArqRetorno, List<String> orgIdentRemocao) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        if (!mantemArqRetorno) {
            // Se não mantém arquivo de retorno, remove todos os registros da tabela do arquivo
            final StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(Columns.TB_ARQUIVO_RETORNO_PARCELA);
            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DELETE FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        } else /*
         * Se mantém arquivo de retorno:
         * remove apenas os registros da tabela que tem o identificador
         * informado na lista passada por parâmetro. Se a lista de parâmetros
         * for vazia, então não remove nenhum registro
         */
        if ((orgIdentRemocao != null) && (orgIdentRemocao.size() > 0)) {
            queryParams.addValue("orgIdentificador", orgIdentRemocao);

            final StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(Columns.TB_ARQUIVO_RETORNO_PARCELA).append(" WHERE ADE_CODIGO IN (");
            query.append(" SELECT ").append(Columns.ADE_CODIGO);
            query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR);
            query.append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.ADE_RSE_CODIGO).append(")");
            query.append(" INNER JOIN ").append(Columns.TB_ORGAO);
            query.append(" ON (").append(Columns.ORG_CODIGO).append(" = ").append(Columns.RSE_ORG_CODIGO).append(")");
            query.append(" WHERE (ORG_IDENTIFICADOR IN (:orgIdentificador)))");
            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DELETE FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            query.append(" WHERE (ORG_IDENTIFICADOR IN (:orgIdentificador))");
            query.append(" OR ORG_IDENTIFICADOR IS NULL");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        }
    }

    @Override
    public boolean insereLinhaTabelaRetorno(Map<String, Object> entrada, String linha, int numLinha, HashMap<String, List<TransferObject>> conveniosMap, Map<String, String> mapVerbaRef, Map<String, String> mapVerbaFerias, AcessoSistema responsavel) throws DAOException {
        try {
            String estIdentificador, orgIdentificador, csaIdentificador, svcIdentificador, cnvCodVerba, posIdentificador,
                   adeNumero, adeIndice, adeCodReg, adeData, adeAnoMesIni, adeAnoMesFim, adePrdPagas, adePrazo, adeCarencia,
                   periodo, sinalPrdVlr, prdVlr, prdData, ocpObs, prdSituacao, spdCodigo, tdeCodigo,
                   quitacao, tipoEnvio, rseMatricula, serNome, serCpf, mapeamento, artFerias;

            final boolean utilizaVerbaRef = ParamSist.getBoolParamSist(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, AcessoSistema.getAcessoUsuarioSistema());
            final boolean temProcessamentoFerias = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, AcessoSistema.getAcessoUsuarioSistema());
            boolean possuiMapeamento = false;

            // Lê campos do arquivo de retorno
            estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
            orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
            csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
            svcIdentificador = (String) entrada.get("SVC_IDENTIFICADOR");
            cnvCodVerba      = (String) entrada.get("CNV_COD_VERBA");

            /*
             * Se utiliza verba de referência e o valor passado é uma código de referência,
             * obtém o valor do código normal através do mapeamento de verbas
             */
            if (utilizaVerbaRef && (mapVerbaRef != null) && (mapVerbaRef.get(cnvCodVerba) != null)) {
                cnvCodVerba = mapVerbaRef.get(cnvCodVerba).toString();
                entrada.put("CNV_COD_VERBA", cnvCodVerba);
            }

            /*
             * Se tem processamento de férias e o valor passado é uma código de verba de férias,
             * obtém o valor do código normal através do mapeamento de verbas
             */
            if (temProcessamentoFerias && (mapVerbaFerias != null) && (mapVerbaFerias.get(cnvCodVerba) != null)) {
                cnvCodVerba = mapVerbaFerias.get(cnvCodVerba).toString();
                entrada.put("CNV_COD_VERBA", cnvCodVerba);
                entrada.put("ART_FERIAS", "1");
            }

            periodo             = (String) entrada.get("PERIODO");
            sinalPrdVlr         = (String) entrada.get("SINAL_VLR_REALIZADO");
            prdVlr              = (String) entrada.get("PRD_VLR_REALIZADO");
            prdData             = (String) entrada.get("PRD_DATA_REALIZADO");
            adeNumero           = (String) entrada.get("ADE_NUMERO");
            adeIndice           = (String) entrada.get("ADE_INDICE");
            adeCodReg           = (String) entrada.get("ADE_COD_REG");
            adeData             = (String) entrada.get("ADE_DATA");
            adeAnoMesIni        = (String) entrada.get("ADE_ANO_MES_INI");
            adeAnoMesFim        = (String) entrada.get("ADE_ANO_MES_FIM");
            adePrdPagas         = (String) entrada.get("ADE_PRD_PAGAS");
            adePrazo            = (String) entrada.get("ADE_PRAZO");
            adeCarencia         = (String) entrada.get("ADE_CARENCIA");
            ocpObs              = (String) entrada.get("OCP_OBS");
            prdSituacao         = (String) entrada.get("SITUACAO");   // Resultado da operação I (Indeferida), D (Deferida), Q (Quitação)
            tipoEnvio           = (String) entrada.get("TIPO_ENVIO"); // Operação enviada: I,A,E
            tdeCodigo           = (String) entrada.get("TDE_CODIGO");
            rseMatricula        = (String) entrada.get("RSE_MATRICULA");
            serNome             = (String) entrada.get("SER_NOME");
            serCpf              = (String) entrada.get("SER_CPF");
            posIdentificador    = (String) entrada.get("POS_IDENTIFICADOR");
            artFerias           = (String) entrada.get("ART_FERIAS");

            final boolean quitar = ("Q".equals(prdSituacao));
            spdCodigo = (("I".equals(prdSituacao)) ? CodedValues.SPD_REJEITADAFOLHA : CodedValues.SPD_LIQUIDADAFOLHA);

            // Verifica e converte o valor fornecido da parcela
            if ((sinalPrdVlr != null) && !"".equals(sinalPrdVlr) && (prdVlr != null) && !"".equals(prdVlr)) {
                try {
                    final BigDecimal valorRealizado = new BigDecimal(prdVlr).multiply(new BigDecimal(sinalPrdVlr));
                    prdVlr = NumberHelper.format(valorRealizado.doubleValue(), "en", 2, 2);
                } catch (final Exception ex) {
                    throw new DAOException("mensagem.erro.importacao.retorno.valor.realizado.invalido", (AcessoSistema) null, ex);
                }
            }

            // Cria mensagem de ocorrência de retorno
            ocpObs = (!TextHelper.isNull(ocpObs)) ? (ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.retorno.obs.parcela", responsavel) + ": " + ocpObs) : "1".equals(artFerias) ? ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel);

            if (tipoEnvio == null) {
                tipoEnvio = "I";
            }

            /*
             * Se no arquivo de retorno existir o código da verba ou o código do
             * serviço + órgão + consignatária, então determina se as linhas
             * possuem mapeamento de convênio no sistema
             */
            final boolean temCodVerba = ((cnvCodVerba != null) && !"".equals(cnvCodVerba));
            final boolean temConvenio = ((csaIdentificador != null) && !"".equals(csaIdentificador)) &&
                                  ((orgIdentificador != null) && !"".equals(orgIdentificador)) &&
                                  ((svcIdentificador != null) && !"".equals(svcIdentificador));

            if (temCodVerba || temConvenio) {
                // Map com os convênios já encontrados
                if (conveniosMap == null) {
                    conveniosMap = new HashMap<>();
                }

                final StringBuilder chaveConvenio = new StringBuilder();
                chaveConvenio.append("param: [").append(cnvCodVerba).append(", ");
                chaveConvenio.append(csaIdentificador).append(", ");
                chaveConvenio.append(svcIdentificador).append(", ");
                chaveConvenio.append(orgIdentificador).append(", ");
                chaveConvenio.append(estIdentificador).append("]");

                if (!conveniosMap.containsKey(chaveConvenio.toString())) {
                    List<TransferObject> convenios = null;
                    if (temCodVerba) {
                        final ListaConveniosQuery lstConvenios = new ListaConveniosQuery();
                        lstConvenios.cnvCodVerba = cnvCodVerba;
                        lstConvenios.ativo = false;

                        convenios = lstConvenios.executarDTO();
                    } else {
                        final ListaConvenioPelosIdentificadoresQuery query = new ListaConvenioPelosIdentificadoresQuery();
                        query.csaIdentificador = csaIdentificador;
                        query.estIdentificador = estIdentificador;
                        query.orgIdentificador = orgIdentificador;
                        query.svcIdentificador = svcIdentificador;

                        convenios = query.executarDTO();
                    }
                    conveniosMap.put(chaveConvenio.toString(), ((convenios != null) && (convenios.size() > 0)) ? convenios : null);
                }

                if (conveniosMap.get(chaveConvenio.toString()) == null) {
                    mapeamento = "N";
                    possuiMapeamento = false;
                } else {
                    mapeamento = "S";
                    possuiMapeamento = true;
                }
            } else {
                /*
                 * Se no arquivo não tem o código da verba e nem os dados do convênio (SVC, CSA, ORG)
                 * então não verifica se as linhas possuem mapeamento. Neste caso o sistema tentará
                 * importar todas as linhas baseadas nas chaves de localização dos contratos
                 */
                mapeamento = "S";
                possuiMapeamento = true;
            }

            if (quitar) {
                quitacao = "S";
            } else {
                quitacao = "N";
            }

            // Formata valores para inserção na tabela:
            // Campos Texto: Faz escape SQL
            // TODO: Ainda necessário fazer escape individual ?
            estIdentificador = (!TextHelper.isNull(estIdentificador) ? TextHelper.escapeSql(estIdentificador) : null);
            orgIdentificador = (!TextHelper.isNull(orgIdentificador) ? TextHelper.escapeSql(orgIdentificador) : null);
            csaIdentificador = (!TextHelper.isNull(csaIdentificador) ? TextHelper.escapeSql(csaIdentificador) : null);
            svcIdentificador = (!TextHelper.isNull(svcIdentificador) ? TextHelper.escapeSql(svcIdentificador) : null);
            cnvCodVerba      = (!TextHelper.isNull(cnvCodVerba) ? TextHelper.escapeSql(cnvCodVerba) : " ");
            rseMatricula     = (!TextHelper.isNull(rseMatricula) ? TextHelper.escapeSql(rseMatricula) : " ");
            serNome          = (!TextHelper.isNull(serNome) ? TextHelper.escapeSql(serNome) : null);
            serCpf           = (!TextHelper.isNull(serCpf) ? TextHelper.escapeSql(serCpf) : null);
            posIdentificador = (!TextHelper.isNull(posIdentificador) ? TextHelper.escapeSql(posIdentificador) : null);
            adeIndice        = (!TextHelper.isNull(adeIndice) ? TextHelper.escapeSql(adeIndice) : null);
            adeCodReg        = (!TextHelper.isNull(adeCodReg) ? TextHelper.escapeSql(adeCodReg) : null);
            ocpObs           = (!TextHelper.isNull(ocpObs) ? TextHelper.escapeSql(ocpObs) : null);
            quitacao         = (!TextHelper.isNull(quitacao) ? TextHelper.escapeSql(quitacao) : null);
            spdCodigo        = (!TextHelper.isNull(spdCodigo) ? TextHelper.escapeSql(spdCodigo) : null);
            tipoEnvio        = (!TextHelper.isNull(tipoEnvio) ? TextHelper.escapeSql(tipoEnvio) : null);
            tdeCodigo        = (!TextHelper.isNull(tdeCodigo) ? TextHelper.escapeSql(tdeCodigo) : null);
            mapeamento       = (!TextHelper.isNull(mapeamento) ? TextHelper.escapeSql(mapeamento) : null);

            // Datas: realiza o parse das datas
            periodo          = (verificarCampoRetorno(java.sql.Date.class, "PERIODO", periodo) ? formatarPeriodo(periodo, responsavel) : null);
            prdData          = (verificarCampoRetorno(java.sql.Date.class, "PRD_DATA_REALIZADO", prdData) ? prdData : null);
            adeData          = (verificarCampoRetorno(java.sql.Date.class, "ADE_DATA", adeData) ? adeData : null);
            adeAnoMesIni     = (verificarCampoRetorno(java.sql.Date.class, "ADE_ANO_MES_INI", adeAnoMesIni) ? formatarPeriodo(adeAnoMesIni, responsavel) : null);
            adeAnoMesFim     = (verificarCampoRetorno(java.sql.Date.class, "ADE_ANO_MES_FIM", adeAnoMesFim) ? formatarPeriodo(adeAnoMesFim, responsavel) : null);

            // Numéricos: inteiros e decimais
            prdVlr           = (verificarCampoRetorno(Double.class, "PRD_VLR_REALIZADO", prdVlr) ? prdVlr : "0.00");
            adePrdPagas      = (verificarCampoRetorno(Integer.class, "ADE_PRD_PAGAS", adePrdPagas) ? adePrdPagas : null);
            adePrazo         = (verificarCampoRetorno(Integer.class, "ADE_PRAZO", adePrazo) ? adePrazo : null);
            adeCarencia      = (verificarCampoRetorno(Integer.class, "ADE_CARENCIA", adeCarencia) ? adeCarencia : null);
            adeNumero        = (verificarCampoRetorno(Long.class, "ADE_NUMERO", adeNumero) ? adeNumero : null);

            linhasCarregadas++;

            // Cria StringBuilder com valores da linha para inserção na tabela de retorno.
            adicionarLinhaCargaTabela(estIdentificador, orgIdentificador, csaIdentificador, svcIdentificador, cnvCodVerba,
                    periodo, prdVlr, prdData, adeNumero, adeIndice, adeCodReg, adeData, adeAnoMesIni, adeAnoMesFim, adePrdPagas, adePrazo, adeCarencia,
                    ocpObs, spdCodigo, quitacao, tipoEnvio, tdeCodigo, rseMatricula, serNome, serCpf, posIdentificador, mapeamento, artFerias, linha, numLinha);

            if ((linhasCarregadas % 1000) == 0) {
                efetuaCargaLinhasTabela();
                LOG.debug("Linhas carregadas = " + linhasCarregadas);
            }

            return possuiMapeamento;
        } catch (final DataAccessException | HQueryException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private void adicionarLinhaCargaTabela(String estIdentificador, String orgIdentificador, String csaIdentificador, String svcIdentificador, String cnvCodVerba,
            String periodo, String prdVlr, String prdData, String adeNumero, String adeIndice, String adeCodReg, String adeData, String adeAnoMesIni, String adeAnoMesFim, String adePrdPagas, String adePrazo, String adeCarencia,
            String ocpObs, String spdCodigo, String quitacao, String tipoEnvio, String tdeCodigo, String rseMatricula, String serNome, String serCpf, String posIdentificador, String mapeamento, String artFerias, String linha, int numLinha) throws SQLException {

        pstatLinhasArquivo.setString(1, nomeArqRetorno);
        pstatLinhasArquivo.setInt(2, numLinha);
        pstatLinhasArquivo.setString(3, cnvCodVerba);
        pstatLinhasArquivo.setString(4, estIdentificador);
        pstatLinhasArquivo.setString(5, orgIdentificador);
        pstatLinhasArquivo.setString(6, csaIdentificador);
        pstatLinhasArquivo.setString(7, svcIdentificador);
        pstatLinhasArquivo.setString(8, periodo);
        pstatLinhasArquivo.setString(9, prdVlr);
        pstatLinhasArquivo.setString(10, prdData);
        pstatLinhasArquivo.setString(11, adeNumero);
        pstatLinhasArquivo.setString(12, adeIndice);
        pstatLinhasArquivo.setString(13, adeCodReg);
        pstatLinhasArquivo.setString(14, adeData);
        pstatLinhasArquivo.setString(15, adeAnoMesIni);
        pstatLinhasArquivo.setString(16, adeAnoMesFim);
        pstatLinhasArquivo.setString(17, adePrdPagas);
        pstatLinhasArquivo.setString(18, adePrazo);
        pstatLinhasArquivo.setString(19, adeCarencia);
        pstatLinhasArquivo.setString(20, ocpObs);
        pstatLinhasArquivo.setString(21, spdCodigo);
        pstatLinhasArquivo.setString(22, quitacao);
        pstatLinhasArquivo.setString(23, tipoEnvio);
        pstatLinhasArquivo.setString(24, tdeCodigo);
        pstatLinhasArquivo.setString(25, rseMatricula);
        pstatLinhasArquivo.setString(26, serNome);
        pstatLinhasArquivo.setString(27, serCpf);
        pstatLinhasArquivo.setString(28, posIdentificador);
        pstatLinhasArquivo.setString(29, mapeamento);
        pstatLinhasArquivo.setString(30, "N");
        pstatLinhasArquivo.setString(31, "N");
        pstatLinhasArquivo.setString(32, artFerias);
        pstatLinhasArquivo.setString(33, linha);
        pstatLinhasArquivo.addBatch();
    }

    private void efetuaCargaLinhasTabela() throws SQLException {
        pstatLinhasArquivo.executeBatch();
        pstatLinhasArquivo.clearBatch();
    }

    @Override
    public void encerraCargaArquivoRetorno(boolean mantemArqRetorno) throws DAOException {
        try {
            // Carrega as últimas linhas do buffer
            efetuaCargaLinhasTabela();
            LOG.debug("Total de linhas carregadas = " + linhasCarregadas);

            if (mantemArqRetorno) {
                /*
                 * Se mantem o arquivo de retorno, seta os identificadores
                 * dos órgãos na tabela de retorno para aqueles que ainda
                 * não possuem a informação setada
                 */
                setarIdnOrgao();
            }

            // Marca as linha que são de férias
            atualizaCampoFerias();

            LOG.debug("Término da carga do arquivo de retorno");
        } catch (final DataAccessException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(pstatLinhasArquivo);
            DBHelper.releaseConnection(conn);
        }
    }

    protected abstract void setarIdnOrgao() throws DataAccessException;

    protected abstract void atualizaCampoFerias() throws DataAccessException;

    /**
     * Verifica se o campo "nomeCampo" com valor "valorCampo" está no
     * formato correto para o tipo.
     * @param tipo
     * @param nomeCampo
     * @param valorCampo
     * @return
     */
    protected boolean verificarCampoRetorno(Class<?> tipo, String nomeCampo, String valorCampo) {
        if (valorCampo == null) {
            return false;
        }

        boolean formatoCorreto = true;
        if (tipo.equals(java.sql.Date.class)) {
            if (!DateHelper.verifyPattern(valorCampo, "yyyy-MM-dd")) {
                formatoCorreto = false;
            }
        } else if (tipo.equals(java.util.Date.class)) {
            if (!DateHelper.verifyPattern(valorCampo, "yyyy-MM-dd HH:mm:ss")) {
                formatoCorreto = false;
            }
        } else if (tipo.equals(Integer.class) || tipo.equals(Long.class)) {
            if (!TextHelper.isNum(valorCampo)) {
                formatoCorreto = false;
            }
        } else if (tipo.equals(Float.class) || tipo.equals(Double.class)) {
            if (!TextHelper.isDecimalNum(valorCampo)) {
                formatoCorreto = false;
            }
        } else {
            formatoCorreto = false;
        }

        if (!formatoCorreto) {
            LOG.warn("Valor incorreto para o campo \"" + nomeCampo + "\": \"" + valorCampo + "\". O valor NULL será assumido");
        }

        return formatoCorreto;
    }

    /**
     * Para sistemas de periodicidade mensal, reformada o campo de data, ignorando
     * a parte do dia do mês, colocando fixo "01". Em periodicidade quinzenal, o
     * valor recebido será utilizado na rotina.
     * @param data
     * @param responsavel
     * @return
     */
    private String formatarPeriodo(String data, AcessoSistema responsavel) {
        try {
            if (PeriodoHelper.folhaMensal(responsavel)) {
                data = DateHelper.reformat(data, "yyyy-MM-dd", "yyyy-MM-01");
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return data;
    }

    @Override
    public void getAdeCodigosLiquidacao(List<String> adeCodigosLiquidacao, boolean ferias) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        final StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT rpd.ade_codigo ");
        query.append("FROM tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_tmp_retorno_parcelas").append(ferias ? "_ferias" : "").append(" rpd ON (rpd.id_linha = art.id_linha) ");
        query.append("WHERE art.mapeada = 'S' ");
        query.append("AND art.processada = 'S' ");
        query.append("AND art.quitacao = 'S' ");
        query.append("AND art.nome_arquivo = :nomeArqRetorno ");
        LOG.trace(query.toString());

        if (adeCodigosLiquidacao == null) {
            adeCodigosLiquidacao = new ArrayList<>();
        }

        final List<String> adeCodigos = jdbc.queryForList(query.toString(), queryParams, String.class);
        adeCodigosLiquidacao.addAll(adeCodigos);
    }

    @Override
    public List<String> getAdeCodigosPermiteLiquidacao(List<String> adeCodigosLiquidacao) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("adeCodigosLiquidacao", adeCodigosLiquidacao);

        // Status de autorizações que podem ser liquidadas
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        // Não deixa liquidar contratos aguard. liquidação pois ocorre erro na finalização
        // do prorcesso de renegociação/compra
        // sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        // sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        queryParams.addValue("sadCodigos", sadCodigos);

        final StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ").append(Columns.ADE_CODIGO);
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" IN (:adeCodigosLiquidacao)");
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN (:sadCodigos)");
        LOG.trace(query.toString());

        return jdbc.queryForList(query.toString(), queryParams, String.class);
    }

    @Override
    public void getAdeCodigosAlteracao(Map<String, Map<String, Object>> linhasSemProcessamento, List<String> adeCodigosAlteracao, HashMap<String, String> adeTipoEnvio,
                                       boolean exportaMensal, boolean atrasado, boolean critica, boolean ferias) throws DAOException {

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        if (adeCodigosAlteracao == null) {
            adeCodigosAlteracao = new ArrayList<>();
        }
        if (adeTipoEnvio == null) {
            adeTipoEnvio = new HashMap<>();
        }

        try {
            final StringBuilder query = new StringBuilder();

            query.append("SELECT DISTINCT rpd.ade_codigo, art.id_linha, art.tipo_envio, art.spd_codigo ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas").append(ferias ? "_ferias" : "").append(" rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE art.mapeada = 'S' ");
            query.append("AND rpd.processada = 'S' ");
            query.append("AND art.processada = 'S' ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            LOG.trace(query.toString());

            final List<Map<String, Object>> resultSet = jdbc.queryForList(query.toString(), queryParams);

            String adeCodigo = null;
            String spdCodigo = null;
            String idLinha = null;
            String tipoEnvio = null;

            // Salva quais ADEs foram alteradas, com o tipo de envio, se for o caso,
            // e remove da lista de linhas sem processamento aquelas que foram pagas.
            for (final Map<String, Object> row : resultSet) {
                adeCodigo = row.get("ade_codigo").toString();
                spdCodigo = row.get("spd_codigo").toString();
                idLinha = row.get("id_linha").toString();
                tipoEnvio = row.get("tipo_envio").toString();

                linhasSemProcessamento.remove(idLinha);

                if (!exportaMensal && critica) {
                    adeTipoEnvio.put(adeCodigo, tipoEnvio);
                } else if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) ||
                        (atrasado && CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo))) {
                    adeCodigosAlteracao.add(adeCodigo);
                }
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> buscaLinhasConsolidacaoExata(boolean ferias, boolean agrupaPorAdeCodigo) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        final String fields = (agrupaPorAdeCodigo ? "id_linha,ade_codigo" : "id_linha");
        final StringBuilder query = new StringBuilder();
        if (agrupaPorAdeCodigo) {
            query.append("SELECT art.id_linha, rpd.ade_codigo FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_consolidadas rpc on (art.id_linha = rpc.id_linha and rpc.ade_codigo = rpd.ade_codigo) ");
        } else {
            query.append("SELECT art.id_linha FROM tb_arquivo_retorno art ");
        }
        query.append("WHERE art.mapeada = 'S' ");
        query.append("AND art.processada = 'N' ");
        query.append("AND art.pode_pagar_consolidacao_exata = 'S' ");
        query.append("AND art.nome_arquivo = :nomeArqRetorno ");
        query.append("AND art.art_ferias = ").append(ferias ? 1 : 0).append(" ");
        if (agrupaPorAdeCodigo) {
            query.append("AND art.prd_vlr_realizado = rpc.vlr_total ");
            query.append("GROUP BY art.id_linha, rpd.ade_codigo ");
            query.append("ORDER BY art.id_linha, rpd.ade_numero ");
        } else {
            query.append("ORDER BY art.id_linha ");
        }
        LOG.trace(query.toString());

        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
    }

    @Override
    public void geraArqLinhasSemMapeamento(String nomeArquivo) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            final StringBuilder query = new StringBuilder();

            query.append("SELECT linha ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("WHERE art.mapeada = 'N' ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("ORDER BY id_linha ");
            LOG.trace(query.toString());
            final List<String> linhas = jdbc.queryForList(query.toString(), queryParams, String.class);

            final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)));

            for (final String linha : linhas) {
                out.println(linha);
            }

            out.close();
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException ("mensagem.erro.criacao.arquivo.saida", (AcessoSistema) null, ex);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void marcaLinhaConsolidadaComoProcessada(String idLinha) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        query.append("UPDATE tb_arquivo_retorno SET processada = 'S' ");
        query.append("WHERE id_linha = :idLinha ");
        query.append("AND nome_arquivo = :nomeArqRetorno");

        queryParams.addValue("idLinha", idLinha);
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    public int countArquivoTabelaRetorno(String nomeArqRetorno) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            final StringBuilder query = new StringBuilder();

            // Se existem registros de importação de retorno para o arquivo que está sendo importado, levanta exceção
            query.setLength(0);
            query.append("SELECT COUNT(*) AS QTDE FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            query.append(" WHERE (").append(Columns.ART_NOME_ARQUIVO).append(" = :nomeArqRetorno)");
            LOG.trace(query.toString());
            return Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> getLinhasSemProcessamento() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder fields = new StringBuilder();
        fields.append(Columns.ART_EST_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ORG_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_SVC_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_CSA_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_RSE_MATRICULA).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_CNV_COD_VERBA).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_PRD_VLR_REALIZADO).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_SPD_CODIGO).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_OCP_OBS).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_NUMERO).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_INDICE).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_ANO_MES_INI).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_ANO_MES_FIM).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_PRD_PAGAS).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_PRAZO).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_ADE_CARENCIA).append(MySqlDAOFactory.SEPARADOR);
        fields.append(Columns.ART_LINHA);

        final StringBuilder query = new StringBuilder();
        query.append(" SELECT ").append(fields);
        query.append(" FROM ").append(Columns.TB_ARQUIVO_RETORNO);
        query.append(" WHERE ").append(Columns.ART_MAPEADA).append(" = 'S'");
        query.append(" AND ").append(Columns.ART_PROCESSADA).append(" = 'N'");

        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields.toString(), MySqlDAOFactory.SEPARADOR);
    }

    @Override
    public void associarLinhaRetornoParcelaExata(boolean ferias) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            final StringBuilder query = new StringBuilder();

            query.append("INSERT INTO tb_arquivo_retorno_parcela (ADE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, NOME_ARQUIVO, ID_LINHA) ");
            query.append("SELECT rpd.ade_codigo, rpd.prd_numero, art.ano_mes_desconto, art.nome_arquivo, art.id_linha ");
            query.append("FROM tb_arquivo_retorno art ");
            if (!ferias) {
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
                query.append("WHERE art.art_ferias = 0 ");
            } else {
                query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha) ");
                query.append("WHERE art.art_ferias = 1 ");
            }
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND art.mapeada = 'S' ");
            query.append("AND art.processada = 'S' ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            query.append("AND rpd.processada = 'S' ");
            LOG.trace(query.toString());
            final int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void associarLinhaRetornoParcela(String adeCodigo, Short prdNumero, Date prdDataDesconto, int numLinha) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final StringBuilder query = new StringBuilder();
            query.append("INSERT INTO tb_arquivo_retorno_parcela (ade_codigo, prd_numero, prd_data_desconto, nome_arquivo, id_linha) ");
            query.append("VALUES (:adeCodigo, :prdNumero, :prdDataDesconto, :nomeArqRetorno, :numLinha)");

            queryParams.addValue("adeCodigo", adeCodigo);
            queryParams.addValue("prdNumero", prdNumero);
            queryParams.addValue("prdDataDesconto", prdDataDesconto);
            queryParams.addValue("numLinha", numLinha);
            queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public List<Integer> getLinhasProcessamentoFerias() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        final StringBuilder query = new StringBuilder();
        query.append(" SELECT ").append(Columns.ART_ID_LINHA);
        query.append(" FROM ").append(Columns.TB_ARQUIVO_RETORNO);
        query.append(" WHERE ").append(Columns.ART_FERIAS).append(" = 1");
        query.append(" AND ").append(Columns.ART_NOME_ARQUIVO).append(" = :nomeArqRetorno ");

        return jdbc.queryForList(query.toString(), queryParams, Integer.class);
    }
}
