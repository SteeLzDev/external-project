package com.zetra.econsig.folha.exportacao.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorListTO;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;

/**
 * <p>Title: Exercito</p>
 * <p>Description: Implementações específicas para o Exército.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Exercito extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Exercito.class);
    private static final long serialVersionUID = 29895L;

    private static final String SUFIXO_ARQUIVO_DIFERENCAS = ".dif";
    private static final String SUFIXO_ARQUIVO_DIFERENCAS_VAZIO = ".vazio";

    public static final String EST_CODIGO_NAO_APLICADO = "2BEE6EC7534A4294A9418CE49AZCAB13";

    private boolean aplicarRotinas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) {
        if ((parametrosExportacao != null) &&
                (parametrosExportacao.getEstCodigos() != null) && !parametrosExportacao.getEstCodigos().isEmpty() &&
                parametrosExportacao.getEstCodigos().contains(EST_CODIGO_NAO_APLICADO)) {
            // DESENV-18011 : não executar as rotinas caso o estabelecimento seja o indicado acima
            return false;
        }
        return true;
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (!aplicarRotinas(parametrosExportacao, responsavel)) {
            return;
        }

        //DESENV-16294 - antes de verificar se os contratos cabem ou não na margem é necessário retirar alguns contratos da exportação
        // pois eles não se aplicam a esta regra.
        LOG.debug("Exercito.separarContratosNaoSerRemovidosMovimento: " + DateHelper.getSystemDatetime());
        separarContratosNaoSerRemovidosMovimento(true);
        LOG.debug("fim - Exercito.separarContratosNaoSerRemovidosMovimento: " + DateHelper.getSystemDatetime());

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("Exercito.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - Exercito.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());

        // Contratos são retornados ao movimento com o valor de 0,01
        separarContratosNaoSerRemovidosMovimento(false);
    }

    /**
     * Atualiza o motivo de não exportação dos contratos passados por parâmetro,
     * previamente selecionados na rotina de validação de margem
     * @param adeImpropria
     * @param tipoMotivoNaoExportacao
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void gravaMotivoNaoExportacao(List<String> adeImpropria, TipoMotivoNaoExportacaoEnum tipoMotivoNaoExportacao) throws DataAccessException {
        if ((adeImpropria != null) && !adeImpropria.isEmpty()) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("update tb_aut_desconto ade ");
            query.append("set mne_codigo = :mneCodigo ");
            query.append("where ade_codigo in (:adeCodigos) ");
            query.append("and not exists ( ");
            query.append("  select 1 from tb_verba_convenio vco ");
            query.append("  inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("  where ade.vco_codigo = vco.vco_codigo ");
            query.append("    and cnv.cnv_cod_verba = 'ZPV' ");
            query.append(")");
            queryParams.addValue("adeCodigos", adeImpropria);
            queryParams.addValue("mneCodigo", tipoMotivoNaoExportacao.getCodigo());
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Remove da tabela de exportação as consignações na lista passada por parâmetro
     * @param adeCodigos
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void excluirContratos(List<String> adeCodigos) throws DataAccessException {
        if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.exportacao.removendo.contratos.sem.margem", (AcessoSistema)null));

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("delete from tb_tmp_exportacao ");
            query.append("where ade_codigo in (:adeCodigos) ");
            query.append("and cnv_cod_verba <> 'ZPV' ");
            queryParams.addValue("adeCodigos", adeCodigos);
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Atualiza o valor dos contratos para pagamento parcial daqueles que não cabem integralmente na
     * margem, em sistemas que permite esta rotina, somente na tabela de exportação
     * @param parcialmenteSemMargem
     * @param stat
     * @throws SQLException
     */
    @Override
    protected void atualizarParcelaPgtParcial(Map<String, BigDecimal> parcialmenteSemMargem) throws SQLException {
        if ((parcialmenteSemMargem != null) && !parcialmenteSemMargem.isEmpty()) {
            final StringBuilder query = new StringBuilder();
            query.append("/*skip_log*/");
            query.append("update tb_tmp_exportacao ");
            query.append("set ade_vlr = ? ");
            query.append("where ade_codigo = ? ");
            query.append("and cnv_cod_verba <> 'ZPV' ");

            Connection conn = null;
            PreparedStatement preStat = null;
            try {
                conn = DBHelper.makeConnection();
                preStat = conn.prepareStatement(query.toString());
                for (final Map.Entry<String, BigDecimal> entrada : parcialmenteSemMargem.entrySet()) {
                    preStat.setBigDecimal(1, entrada.getValue());
                    preStat.setString(2, entrada.getKey());
                    preStat.executeUpdate();
                }
            } finally {
                DBHelper.closeStatement(preStat);
                DBHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Lista os contratos de servidores com margem negativa pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * @param stat
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Cria tabela temporária com a margem usada pós corte
        salvarMargemUsadaPosCorte(null);

        final StringBuilder query = new StringBuilder();
        // DESENV-13946 : Fixa a margem 1 como sendo a margem limite para exportação
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, '1' as ade_inc_margem, tmp.autoriza_pgt_parcial ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("left outer join tb_tmp_margem_usada_pos_corte mpc on (tmp.rse_codigo = mpc.rse_codigo and mpc.mar_codigo = 1) ");
        query.append("where tmp.ade_inc_margem <> '0' ");
        query.append("and tmp.rse_margem_rest + COALESCE(mpc.margem_usada_pos_corte, 0.00) < 0.00 ");

        // DESENV-14158: Consignações que não tenham sido afetadas por decisão judicial e sejam nas naturezas de empréstimo, financiamento ou auxílio de financiamento.
        query.append("and not exists ( ");
        query.append("select 1 ");
        query.append("from tb_aut_desconto ade ");
        query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        query.append("inner join tb_dados_autorizacao_desconto dad on (ade.ade_codigo = dad.ade_codigo and dad.tda_codigo = '").append(CodedValues.TDA_AFETADA_DECISAO_JUDICIAL).append("') ");
        query.append("inner join tb_ocorrencia_dados_ade oda on (dad.ade_codigo = oda.ade_codigo and dad.tda_codigo = oda.tda_codigo and oda.toc_codigo = '").append(CodedValues.TOC_CRIACAO_DADOS_ADICIONAIS).append("') ");
        query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) ");
        query.append("where ade.ade_codigo = tmp.ade_codigo ");
        query.append("and coalesce(dad.dad_valor, 'N') = 'S' ");
        query.append("and svc.nse_codigo in ('").append(CodedValues.NSE_EMPRESTIMO).append("', '").append(CodedValues.NSE_FINANCIAMENTO).append("', '").append(CodedValues.NSE_AUXILIO_FINANCEIRO).append("') ");
        query.append("and oda_data < pex_data_fim ");
        query.append(") ");

        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    /**
     * TODO: Estudar subir para a classe pai e substituir os pontos que calculam a margem pós corte item a item
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    protected void salvarMargemUsadaPosCorte(List<Short> marCodigos) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_margem_usada_pos_corte");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_margem_usada_pos_corte (RSE_CODIGO VARCHAR(32) NOT NULL, MAR_CODIGO SMALLINT NOT NULL, MARGEM_USADA_POS_CORTE DECIMAL(13,2) NOT NULL DEFAULT 0, PRIMARY KEY (RSE_CODIGO, MAR_CODIGO))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_margem_usada_pos_corte (RSE_CODIGO, MAR_CODIGO, MARGEM_USADA_POS_CORTE) ");
            query.append("SELECT rse.rse_codigo, hmr.mar_codigo, COALESCE(SUM(hmr.hmr_margem_antes - hmr.hmr_margem_depois), 0) ");
            query.append("FROM tb_registro_servidor rse ");
            query.append("INNER JOIN tb_historico_margem_rse hmr ON (rse.rse_codigo = hmr.rse_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("WHERE hmr.oca_codigo IS NOT NULL ");
            query.append("  AND hmr.hmr_data > pex.pex_data_fim ");

            if ((marCodigos != null) && !marCodigos.isEmpty()) {
                query.append(" AND hmr.mar_codigo IN (:marCodigos) ");
                queryParams.addValue("marCodigos", marCodigos);
            }

            query.append("GROUP BY rse.rse_codigo, hmr.mar_codigo ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    protected BigDecimal calcularMargemUsadaPosCorte(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);
        queryParams.addValue("marCodigo", marCodigo);

        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT mpc.margem_usada_pos_corte as total ");
        query.append("FROM tb_tmp_margem_usada_pos_corte mpc ");
        query.append("WHERE mpc.rse_codigo = :rseCodigo ");
        query.append("  AND mpc.mar_codigo = :marCodigo ");

        final BigDecimal margemUsadaPostCorte = jdbc.queryForObject(query.toString(), queryParams, BigDecimal.class);
        if (margemUsadaPostCorte != null) {
            LOG.debug(String.format("rseCodigo: %s - marCodigo: %d - margemUsadaPosCorte: %,.2f", rseCodigo, marCodigo, margemUsadaPostCorte));
            return margemUsadaPostCorte;
        }

        return BigDecimal.ZERO;
    }

    /**
     * DESENV-17481: Ignora do cálculo de margem usada não exportável consignações do serviço
     * indicado pois se refere a serviço que não integra folha usado para registrar consumos de
     * margem relativos a decisões judiciais.
     */
    @Override
    protected String incluirClausulaCalculoMargemUsadaNaoExportavel(String rseCodigo, Short marCodigo, AcessoSistema responsavel) {
        return " AND cnv.svc_codigo <> '7C2A629E02304ZBCB51BA156Z1ZZ5127' ";
    }

    /**
     *
     * @param diferencas
     * @param situacao
     * @param stat
     * @param sql
     * @throws SQLException
     */
    private void adicionarDiferenca(List<TransferObject> diferencas, String situacao, String sql) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
            final int nrCampos = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                final CustomTransferObject diferenca = new CustomTransferObject();
                for (int i = 1; i <= nrCampos; i++) {
                    // Foi necessário colocar o UpperCase, pois quando existem linhas de Exclusão juntamente com linhas de outra operação o tradutor acaba dando erro, pois espera um determinado campo
                    // maiusculo ou minusculo e como existem os dois tipo, pois buscam em tabelas diferentes acaba dando erro na tradução e o UpperCase padroniza o resultado para que seja todas as colunas
                    // maiusculas.
                    diferenca.setAttribute(rs.getMetaData().getColumnName(i).toUpperCase(), rs.getObject(i));
                }
                diferenca.setAttribute("SITUACAO", situacao);
                diferencas.add(diferenca);
            }
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     *
     */
    @Override
    public void gravaArquivoDiferencas(String nomeArqSaidaMov, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (!aplicarRotinas(parametrosExportacao, responsavel)) {
            return;
        }

        // Arquivos de configuração para processamento do retorno
        final ParamSist ps = ParamSist.getInstance();
        //String nomeArqConfEntrada = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN_DIFERENCAS, parametrosExportacao.responsavel);
        final String nomeArqConfSaidaDiferencas = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN_DIFERENCAS, responsavel);
        final String nomeArqConfTradutorDiferencas = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN_DIFERENCAS, responsavel);

        if (!TextHelper.isNull(nomeArqConfSaidaDiferencas) && !TextHelper.isNull(nomeArqConfTradutorDiferencas)) {
            // Diretório raiz de arquivos eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathConf = absolutePath + File.separatorChar + "conf";
            // Arquivos de configuração utilizados na exportação
            //String nomeArqConfEntradaDefault = pathConf + File.separatorChar + nomeArqConfEntrada;
            final String nomeArqConfSaida = pathConf + File.separatorChar + nomeArqConfSaidaDiferencas;
            final String nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfTradutorDiferencas;

            final String nomeArqSaida = nomeArqSaidaMov + SUFIXO_ARQUIVO_DIFERENCAS;
            // Apaga arquivos antigos
            new File(nomeArqSaida).delete();
            new File(nomeArqSaida + SUFIXO_ARQUIVO_DIFERENCAS_VAZIO).delete();


            try {
                if (parametrosExportacao.getAcao().equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
                    final StringBuilder query = new StringBuilder();
                    final List<TransferObject> diferencas = new ArrayList<>();

                    // 4) A diferença será gerada da seguinte forma:
                    // 4.1) Contratos enviados no primeiro movimento que não estão mais presentes no segundo, devem ser gerados como exclusão. Não é necessário gerar linhas de EXCLUSÃO para Prec-cp do Ativo quando for transferência entre tipos.
                    query.setLength(0);
                    query.append("SELECT rse.RSE_TIPO, ");
                    query.append("arm.PEX_PERIODO, ");
                    query.append("arm.PEX_PERIODO_ANT, ");
                    query.append("arm.PEX_PERIODO_POS, ");
                    query.append("arm.SER_CPF, ");
                    query.append("arm.RSE_MATRICULA, ");
                    query.append("arm.ORG_IDENTIFICADOR, ");
                    query.append("arm.EST_IDENTIFICADOR, ");
                    query.append("arm.CSA_IDENTIFICADOR, ");
                    query.append("arm.SVC_IDENTIFICADOR, ");
                    query.append("arm.CNV_COD_VERBA, ");
                    query.append("arm.ADE_NUMERO, ");
                    query.append("arm.ADE_INDICE, ");
                    query.append("arm.ADE_DATA, ");
                    query.append("arm.ADE_ANO_MES_INI, ");
                    query.append("arm.ADE_ANO_MES_FIM, ");
                    query.append("arm.ADE_PRAZO, ");
                    query.append("arm.ADE_VLR, ");
                    query.append("rse.RSE_ASSOCIADO ");
                    query.append("FROM tb_arquivo_movimento arm ");
                    query.append("STRAIGHT_JOIN tb_registro_servidor rse ON (arm.RSE_MATRICULA = rse.RSE_MATRICULA) ");
                    query.append("STRAIGHT_JOIN tb_orgao org ON (rse.ORG_CODIGO = org.ORG_CODIGO and org.ORG_IDENTIFICADOR = arm.ORG_IDENTIFICADOR) ");
                    query.append("STRAIGHT_JOIN tb_periodo_exportacao pex ON (pex.ORG_CODIGO = org.ORG_CODIGO and pex.PEX_PERIODO = arm.PEX_PERIODO) ");
                    query.append("WHERE NOT EXISTS ( ");
                    query.append("  SELECT 1 FROM tb_tmp_exportacao_ordenada teo ");
                    query.append("  WHERE arm.PEX_PERIODO = teo.PEX_PERIODO ");
                    query.append("  AND arm.RSE_MATRICULA = teo.RSE_MATRICULA ");
                    query.append("  AND arm.CNV_COD_VERBA = teo.CNV_COD_VERBA ");
                    query.append(") ");
                    // DESENV-15831 - Não enviar verbas ZPV e ZPU
                    query.append("AND arm.CNV_COD_VERBA NOT IN ('ZPV', 'ZPU') ");
                    LOG.debug(query.toString());
                    adicionarDiferenca(diferencas, "E", query.toString());

                    // 4.2) Contratos presentes nos dois movimentos mas com valor diferente no segundo movimento, devem gerar uma alteração.
                    query.setLength(0);
                    query.append("SELECT teo.RSE_TIPO, ");
                    query.append("teo.PEX_PERIODO, ");
                    query.append("teo.PEX_PERIODO_ANT, ");
                    query.append("teo.PEX_PERIODO_POS, ");
                    query.append("teo.SER_CPF, ");
                    query.append("teo.RSE_MATRICULA, ");
                    query.append("teo.ORG_IDENTIFICADOR, ");
                    query.append("teo.EST_IDENTIFICADOR, ");
                    query.append("teo.CSA_IDENTIFICADOR, ");
                    query.append("teo.SVC_IDENTIFICADOR, ");
                    query.append("teo.CNV_COD_VERBA, ");
                    query.append("teo.ADE_NUMERO, ");
                    query.append("teo.ADE_INDICE, ");
                    query.append("teo.ADE_DATA, ");
                    query.append("teo.ADE_ANO_MES_INI, ");
                    query.append("teo.ADE_ANO_MES_FIM, ");
                    query.append("teo.ADE_PRAZO, ");
                    query.append("teo.ADE_VLR, ");
                    query.append("teo.RSE_ASSOCIADO ");
                    query.append("FROM tb_tmp_exportacao_ordenada teo ");
                    query.append("WHERE EXISTS ( ");
                    query.append("  SELECT 1 FROM tb_arquivo_movimento arm ");
                    query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                    query.append("  AND teo.RSE_MATRICULA = arm.RSE_MATRICULA ");
                    query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                    query.append("  AND teo.ADE_VLR <> arm.ADE_VLR ");
                    query.append(") ");
                    // DESENV-15831 - Não enviar verbas ZPV e ZPU
                    query.append("AND teo.CNV_COD_VERBA NOT IN ('ZPV', 'ZPU') ");
                    LOG.debug(query.toString());
                    adicionarDiferenca(diferencas, "A", query.toString());

                    // 4.2.1) Contratos presentes nos dois movimentos mas que no segundo movimento a matrícula passou a iniciar com 96, devem gerar uma inclusão.
                    query.setLength(0);
                    query.append("SELECT teo.RSE_TIPO, ");
                    query.append("teo.PEX_PERIODO, ");
                    query.append("teo.PEX_PERIODO_ANT, ");
                    query.append("teo.PEX_PERIODO_POS, ");
                    query.append("teo.SER_CPF, ");
                    query.append("teo.RSE_MATRICULA, ");
                    query.append("teo.ORG_IDENTIFICADOR, ");
                    query.append("teo.EST_IDENTIFICADOR, ");
                    query.append("teo.CSA_IDENTIFICADOR, ");
                    query.append("teo.SVC_IDENTIFICADOR, ");
                    query.append("teo.CNV_COD_VERBA, ");
                    query.append("teo.ADE_NUMERO, ");
                    query.append("teo.ADE_INDICE, ");
                    query.append("teo.ADE_DATA, ");
                    query.append("teo.ADE_ANO_MES_INI, ");
                    query.append("teo.ADE_ANO_MES_FIM, ");
                    query.append("teo.ADE_PRAZO, ");
                    query.append("teo.ADE_VLR, ");
                    query.append("teo.RSE_ASSOCIADO ");
                    query.append("FROM tb_tmp_exportacao_ordenada teo ");
                    query.append("WHERE EXISTS ( ");
                    query.append("  SELECT 1 FROM tb_arquivo_movimento arm ");
                    query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                    query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                    query.append("  AND teo.SER_CPF = arm.SER_CPF  ");
                    query.append("  AND teo.RSE_MATRICULA != arm.RSE_MATRICULA ");
                    query.append("  AND (teo.RSE_MATRICULA LIKE '96%' AND arm.RSE_MATRICULA NOT LIKE '96%') ");
                    query.append(") ");
                    // DESENV-15831 - Não enviar verbas ZPV e ZPU
                    query.append("AND teo.CNV_COD_VERBA NOT IN ('ZPV', 'ZPU') ");
                    LOG.debug(query.toString());
                    adicionarDiferenca(diferencas, "I", query.toString());


                    // 4.3) Contratos presentes nos dois movimentos mas com valor igual no segundo movimento, NÃO devem compor o arquivo de diferenças.
                    // Nada a fazer. Comentário apenas para documentação.

                    // 4.4) Contratos não enviados no primeiro movimento, mas enviados no segundo, devem compor o arquivo de diferenças.
                    query.setLength(0);
                    query.append("SELECT teo.RSE_TIPO, ");
                    query.append("teo.PEX_PERIODO, ");
                    query.append("teo.PEX_PERIODO_ANT, ");
                    query.append("teo.PEX_PERIODO_POS, ");
                    query.append("teo.SER_CPF, ");
                    query.append("teo.RSE_MATRICULA, ");
                    query.append("teo.ORG_IDENTIFICADOR, ");
                    query.append("teo.EST_IDENTIFICADOR, ");
                    query.append("teo.CSA_IDENTIFICADOR, ");
                    query.append("teo.SVC_IDENTIFICADOR, ");
                    query.append("teo.CNV_COD_VERBA, ");
                    query.append("teo.ADE_NUMERO, ");
                    query.append("teo.ADE_INDICE, ");
                    query.append("teo.ADE_DATA, ");
                    query.append("teo.ADE_ANO_MES_INI, ");
                    query.append("teo.ADE_ANO_MES_FIM, ");
                    query.append("teo.ADE_PRAZO, ");
                    query.append("teo.ADE_VLR, ");
                    query.append("teo.RSE_ASSOCIADO ");
                    query.append("FROM tb_tmp_exportacao_ordenada teo ");
                    query.append("WHERE NOT EXISTS ( ");
                    query.append("  SELECT 1 FROM tb_arquivo_movimento arm ");
                    query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                    query.append("  AND teo.RSE_MATRICULA = arm.RSE_MATRICULA ");
                    query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                    query.append(") ");
                    query.append("AND NOT EXISTS ( ");
                    query.append("  SELECT 1 FROM tb_arquivo_movimento arm ");
                    query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                    query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                    query.append("  AND teo.SER_CPF = arm.SER_CPF  ");
                    query.append("  AND teo.RSE_MATRICULA != arm.RSE_MATRICULA ");
                    query.append("  AND (teo.RSE_MATRICULA LIKE '96%' AND arm.RSE_MATRICULA NOT LIKE '96%') ");
                    query.append(") ");
                    // DESENV-15831 - Não enviar verbas ZPV e ZPU
                    query.append("AND teo.CNV_COD_VERBA NOT IN ('ZPV', 'ZPU') ");
                    LOG.debug(query.toString());
                    adicionarDiferenca(diferencas, "I", query.toString());

                    final File arqDiferencas = new File(nomeArqSaida);
                    if (arqDiferencas.exists()) {
                        FileHelper.delete(nomeArqSaida);
                    }
                    if (diferencas.isEmpty()) {
                        LOG.debug("Nenhuma diferença encontrada.");
                        new File(nomeArqConfSaida + SUFIXO_ARQUIVO_DIFERENCAS_VAZIO).createNewFile();
                    } else {
                        final EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
                        final Leitor leitor = new LeitorListTO(diferencas);
                        final Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
                        tradutor.traduz();

                        LOG.debug("Arquivo de diferenças: " + nomeArqSaida);
                    }
                }
            } catch (final SQLException | ParserException | IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public String posProcessaArqLote(String nomeArqLote, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (!aplicarRotinas(parametrosExportacao, responsavel)) {
            return nomeArqLote;
        }

        // Diretório raiz de arquivos eConsig
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathLote = absolutePath + File.separatorChar + "movimento" + File.separatorChar + "cse";
        final String dia = DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy");
        final String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".txt";
        final String nomeArqZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".zip";

        final File arquivoDiferencas = new File(pathLote, nomeArqSaida + SUFIXO_ARQUIVO_DIFERENCAS);
        if (arquivoDiferencas.length() > 0) {
            nomeArqLote = arquivoDiferencas.getName();
            new File(pathLote, nomeArqSaida).delete();
            new File(pathLote, nomeArqZip).delete();
        }
        return nomeArqLote;
    }

    private void separarContratosNaoSerRemovidosMovimento(boolean separar) throws ExportaMovimentoException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            final StringBuilder query = new StringBuilder();

            if (separar) {
                query.append("drop temporary table if exists tb_tmp_exportacao_pos_codigo");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append(" create temporary table tb_tmp_exportacao_pos_codigo");
                query.append(" select *");
                query.append(" from tb_tmp_exportacao");
                query.append(" where pos_codigo in ('12','13','14','15','16','28','33','34')");
                query.append(" and cnv_cod_verba in ('ZQ6','ZB4','ZS2','ZD2','ZN5','ZGV')");
                query.append(" and ade_data > '2021-07-30 23:59:59'");
                query.append(" and rse_matricula not like '96%' and rse_matricula not like '98%'");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append("UPDATE tb_tmp_exportacao_pos_codigo tmp ");
                query.append("SET tmp.ade_vlr = '0.01' ");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append(" delete from tb_tmp_exportacao");
                query.append(" where pos_codigo in ('12','13','14','15','16','28','33','34')");
                query.append(" and cnv_cod_verba in ('ZQ6','ZB4','ZS2','ZD2','ZN5','ZGV')");
                query.append(" and ade_data > '2021-07-30 23:59:59'");
                query.append(" and rse_matricula not like '96%' and rse_matricula not like '98%'");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);

            } else {
                query.append("INSERT INTO tb_tmp_exportacao ");
                query.append("select * from tb_tmp_exportacao_pos_codigo");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
