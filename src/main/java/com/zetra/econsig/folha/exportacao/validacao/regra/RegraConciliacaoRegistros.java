package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegraConciliacaoRegistros</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a conciliação dos registros gerados por tipo.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraConciliacaoRegistros extends Regra {
    /** Log object for this class. */
    private static org.apache.commons.logging.Log LOG;

    /** Parâmetros de sistema necessários para validação da regra */
    private final boolean exportacaoInicial;
    private final boolean folhaAceitaAlteracao;
    private final boolean consolidaExclusaoInclusao;
    private final boolean exportaLiqCancNaoPagas;
    private final boolean exportaLiqIndependenteAnoMesFim;
    private final boolean enviaContratoRseExcluido;
    private final boolean exportaLiqIndependenteQtdPagas;
    private final boolean enviaConclusaoFolha;
    private final boolean consolidaMovFin;
    int carenciaFolha;
    private boolean listaContratos = true;
    private List<String> camposChave = null;

    public RegraConciliacaoRegistros() {
        LOG = org.apache.commons.logging.LogFactory.getLog(this.getClass());

        exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exportação somente inicial? " + exportacaoInicial);

        folhaAceitaAlteracao = ParamSist.paramEquals(CodedValues.TPC_FOLHA_ACEITA_ALTERACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Folha aceita alteração? " + folhaAceitaAlteracao);

        consolidaExclusaoInclusao = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_EXC_INC_COMO_ALT, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Consolida exclusão/inclusão como alteração? " + consolidaExclusaoInclusao);

        exportaLiqCancNaoPagas = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exporta exclusão de contratos não pagos? " + exportaLiqCancNaoPagas);

        exportaLiqIndependenteAnoMesFim = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQ_INDEPENDENTE_ANO_MES_FIM, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exporta exclusão independente da data final? " + exportaLiqCancNaoPagas);

        enviaConclusaoFolha = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONCLUSAO_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exporta conclusão de contratos para a folha? " + enviaConclusaoFolha);

        exportaLiqIndependenteQtdPagas = !ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQ_INDEPENDENTE_QTD_PAGAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exporta liquidações de contratos independentemente da quantidade de parcelas pagas? " + exportaLiqIndependenteQtdPagas);

        consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Consolida descontos para integração com a folha? " + consolidaMovFin);

        enviaContratoRseExcluido = !ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Envia contratos de servidores excluídos? " + enviaContratoRseExcluido);

        Object paramCarenciaFolha = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
        carenciaFolha = 0;
        if (!TextHelper.isNull(paramCarenciaFolha)) {
            carenciaFolha = Integer.parseInt(paramCarenciaFolha.toString());
        }
        LOG.debug("Carência da folha para conclusão de contratos: " + carenciaFolha + " dias");
    }

    /**
     * Cria uma tabela temporária com os contratos que seriam exportados.
     * @return
     */
    protected void criaTabelaTemporaria() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String query = "delete from tb_tmp_val_mov_conciliacao";
        try {
            jdbc.update(query, queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível limpar tabela tb_tmp_val_mov_conciliacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }
    }

    @Override
    public void criarTabelasValidacao() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            String query = "create table if not exists tb_tmp_val_mov_conciliacao ("
                         + "  vmc_operacao varchar(1) NOT NULL,"
                         + "  org_identificador varchar(40) DEFAULT NULL,"
                         + "  est_identificador varchar(40) DEFAULT NULL,"
                         + "  csa_identificador varchar(40) DEFAULT NULL,"
                         + "  svc_identificador varchar(40) DEFAULT NULL,"
                         + "  cnv_cod_verba char(32) DEFAULT NULL,"
                         + "  cnv_codigo char(32) DEFAULT NULL,"
                         + "  SER_NOME varchar(255) DEFAULT NULL,"
                         + "  ser_cpf char(19) DEFAULT NULL,"
                         + "  rse_matricula varchar(20) DEFAULT NULL,"
                         + "  rse_matricula_inst varchar(20) DEFAULT NULL,"
                         + "  rse_codigo char(32) DEFAULT NULL,"
                         + "  amv_periodo date DEFAULT NULL,"
                         + "  amv_competencia date DEFAULT NULL,"
                         + "  amv_data date DEFAULT NULL,"
                         + "  pex_periodo date DEFAULT NULL,"
                         + "  pex_periodo_ant date DEFAULT NULL,"
                         + "  ade_indice char(32) DEFAULT NULL,"
                         + "  ade_numero bigint(20) unsigned DEFAULT NULL,"
                         + "  ade_prazo int(11) DEFAULT NULL,"
                         + "  ade_vlr decimal(13,2) DEFAULT NULL,"
                         + "  ade_tipo_vlr char(1) DEFAULT NULL,"
                         + "  ade_vlr_folha decimal(13,2) DEFAULT NULL,"
                         + "  ade_data datetime DEFAULT NULL,"
                         + "  ade_data_ref datetime DEFAULT NULL,"
                         + "  ade_ano_mes_ini date DEFAULT NULL,"
                         + "  ade_ano_mes_fim date DEFAULT NULL,"
                         + "  ade_ano_mes_ini_folha date DEFAULT NULL,"
                         + "  ade_ano_mes_fim_folha date DEFAULT NULL,"
                         + "  ade_ano_mes_ini_ref date DEFAULT NULL,"
                         + "  ade_ano_mes_fim_ref date DEFAULT NULL,"
                         + "  ade_cod_reg char(1) DEFAULT NULL,"
                         + "  KEY ix01 (rse_matricula, cnv_cod_verba))";
            LOG.debug(query);
            jdbc.update(query, queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível criar tabela tb_tmp_val_mov_conciliacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        if (!exportacaoInicial) {
            LOG.warn("A regra de validação de movimento 'RegraConciliacaoRegistros' somente se aplica a sistemas de movimento inicial.");
            return;
        }

        if (!folhaAceitaAlteracao && consolidaExclusaoInclusao) {
            String msg = "Parâmetros incompatíveis, 'Folha não aceita alteração' e 'Consolida exclusão/inclusão como alteração', movimento é inválido, verifique.";
            LOG.error(msg);
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(msg);
            return;
        }

        try {
            camposChave = getAmvCamposPreenchidos();
        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage());
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(ex.getMessage());
            return;
        }

        // Cria tabela temporaria
        try {
            criaTabelaTemporaria();
        } catch (ZetraException ex) {
            // Erro na criação da tabela temporária provoca a marcação do resultado como ERRO e interrompe o processo de validação.
            LOG.info(ex.getMessage());
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(ex.getMessage());
            return;
        }

        // Define os códigos da regra atual.
        rvaCodigo = rva.getRvaCodigo();
        rvmCodigo = regra.getRvmCodigo();
        this.estCodigos = estCodigos;
        this.orgCodigos = orgCodigos;

        periodo = DateHelper.format(rva.getRvaPeriodo(), "yyyy-MM-dd");

        resultado = new ResultadoRegraValidacaoMovimentoTO(rvaCodigo, regra.getRvmCodigo());
        String rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK;
        StringBuilder rrvValorEncontrado = new StringBuilder();

        long qtdAlteracao = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_ALTERACAO);
        long qtdAlteracaoBase = 0;
        try {
            qtdAlteracaoBase = buscaQtdAlteracaoBase();
        } catch (ZetraException ex) {
            LOG.info(ex.getMessage());
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(ex.getMessage());
            return;
        }

        long qtdExclusao = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_EXCLUSAO);
        long qtdExclusaoBase = 0;
        try {
            qtdExclusaoBase = buscaQtdExclusaoBase(queryParams);
        } catch (ZetraException ex) {
            LOG.info(ex.getMessage());
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(ex.getMessage());
            return;
        }

        long qtdInclusao = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_INCLUSAO);
        long qtdInclusaoBase = 0;
        try {
            qtdInclusaoBase = buscaQtdInclusaoBase(queryParams);
        } catch (ZetraException ex) {
            LOG.info(ex.getMessage());
            resultado.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
            resultado.setRrvValorEncontrado(ex.getMessage());
            return;
        }

        try {
            buscaQtdRenegociacaoBase();
        } catch (ZetraException ex) {
            LOG.info(ex.getMessage());
        }

        // Lista das diferenças
        List<TransferObject> listaDiferencaAlteracaoExportadoValidado = null;
        List<TransferObject> listaDiferencaAlteracaoValidadoExportado = null;
        List<TransferObject> listaDiferencaExclusaoExportadoValidado = null;
        List<TransferObject> listaDiferencaExclusaoValidadoExportado = null;
        List<TransferObject> listaDiferencaInclusaoExportadoValidado = null;
        List<TransferObject> listaDiferencaInclusaoValidadoExportado = null;
        if (listaContratos) {
            listaDiferencaAlteracaoExportadoValidado = buscaDiferencasExportadoValidado(Regra.TIPO_OPERACAO_ALTERACAO);
            listaDiferencaAlteracaoValidadoExportado = buscaDiferencasValidadoExportado(Regra.TIPO_OPERACAO_ALTERACAO);
            listaDiferencaExclusaoExportadoValidado = buscaDiferencasExportadoValidado(Regra.TIPO_OPERACAO_EXCLUSAO);
            listaDiferencaExclusaoValidadoExportado = buscaDiferencasValidadoExportado(Regra.TIPO_OPERACAO_EXCLUSAO);
            listaDiferencaInclusaoExportadoValidado = buscaDiferencasExportadoValidado(Regra.TIPO_OPERACAO_INCLUSAO);
            listaDiferencaInclusaoValidadoExportado = buscaDiferencasValidadoExportado(Regra.TIPO_OPERACAO_INCLUSAO);
        }

        String[] titulos = {"A: ", "E: ", "I: "};
        long[] qtds = {qtdAlteracao, qtdExclusao, qtdInclusao};
        long[] qtdsBase = {qtdAlteracaoBase, qtdExclusaoBase, qtdInclusaoBase};
        List<?>[] listasDiferencasExportadoValidado = {listaDiferencaAlteracaoExportadoValidado,
                listaDiferencaExclusaoExportadoValidado, listaDiferencaInclusaoExportadoValidado};
        List<?>[] listasDiferencasValidadoExportado = {listaDiferencaAlteracaoValidadoExportado,
                listaDiferencaExclusaoValidadoExportado, listaDiferencaInclusaoValidadoExportado};
        for (int i=0; i < titulos.length; i++) {
            long qtd = qtds[i];
            long qtdBase = qtdsBase[i];
            rrvValorEncontrado.append(titulos[i]).append(qtd).append("/").append(qtdBase);
            // Se for menor que zero é porque deu erro na busca do valor
            if (qtdBase < 0) {
                rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
            } else {
                long diferencaPercentual =  100 * Math.abs(qtd - qtdBase);
                if (qtdBase > 0) {
                    diferencaPercentual =  diferencaPercentual / qtdBase;
                }
                int limiteErro = regra.getRvmLimiteErro() != null ? Integer.parseInt(regra.getRvmLimiteErro()) : Integer.MAX_VALUE;
                int limiteAviso = regra.getRvmLimiteAviso() != null ? Integer.parseInt(regra.getRvmLimiteAviso()) : Integer.MAX_VALUE;

                if (diferencaPercentual >= limiteErro) {
                    rrvValorEncontrado.append(" **");
                    rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
                } else if (diferencaPercentual >= limiteAviso) {
                    rrvValorEncontrado.append(" *");
                    if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(rrvResultado)) {
                        rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                    }
                }
            }
            rrvValorEncontrado.append("<br/>");
            if (listaContratos && listasDiferencasExportadoValidado[i] != null && listasDiferencasExportadoValidado[i].size()>0){

                rrvValorEncontrado.append("Lista de contratos (rse_matricula:cnv_cod_verba:ade_vlr) exportados não listados na validação:<br>[");
                Iterator<?> it = listasDiferencasExportadoValidado[i].iterator();
                while(it.hasNext()) {
                    TransferObject to = (TransferObject) it.next();
                    rrvValorEncontrado.append(to.getAttribute("rse_matricula") + ":" + to.getAttribute("cnv_cod_verba") + ":" + to.getAttribute("ade_vlr") + (it.hasNext() ? ";" : "]"));
                 }
                rrvValorEncontrado.append("<br/>");
            }
            if (listaContratos && listasDiferencasValidadoExportado[i] != null && listasDiferencasValidadoExportado[i].size()>0){
                rrvValorEncontrado.append("Lista de contratos (rse_matricula:cnv_cod_verba:ade_vlr) listados na validação mas não exportados:<br>[");
                Iterator<?> it = listasDiferencasValidadoExportado[i].iterator();
                while(it.hasNext()) {
                    TransferObject to = (TransferObject) it.next();
                    rrvValorEncontrado.append(to.getAttribute("rse_matricula") + ":" + to.getAttribute("cnv_cod_verba") + ":" + to.getAttribute("ade_vlr") + (it.hasNext() ? ";" : "]"));
                 }
                rrvValorEncontrado.append("<br/>");
            }
        }
        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    protected long buscaQtdBase(String query, MapSqlParameterSource queryParams) {
        long qtd = 0;
        try {
            LOG.debug(query);

            List<TransferObject> rs = MySqlGenericDAO.getFieldsValuesList(queryParams, query, "qtd", MySqlDAOFactory.SEPARADOR);
            Iterator<TransferObject> it = rs.iterator();
            while (it.hasNext()) {
                qtd += Long.parseLong(it.next().getAttribute("qtd").toString());
            }
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            qtd = -1;
        }
        return qtd;
    }

    private long buscaQtdAlteracaoBase() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();

        // Contratos alterados no período
        query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao, ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(") ");
        query.append("SELECT DISTINCT 'A' AS 'TIPO', ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_ocorrencia_autorizacao ocaAlt ON (ade.ade_codigo = ocaAlt.ade_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
        query.append("WHERE (ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append("  AND (coalesce(ade.ade_prd_pagas, 0) > 0) ");
        query.append("  AND (ade.ade_ano_mes_ini < pex_periodo or ade.ade_ano_mes_ini_ref < pex_periodo) ");
        query.append("  AND (ocaAlt.toc_codigo in ('");
        query.append(CodedValues.TOC_ALTERACAO_CONTRATO).append("','");
        query.append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("','");
        query.append(CodedValues.TOC_RELANCAMENTO).append("')) ");
        query.append("  AND ocaAlt.oca_periodo = pex.pex_periodo ");
        query.append((!enviaContratoRseExcluido ? "  AND srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " : ""));
        query.append("  AND (ade.sad_codigo in ('");
        query.append(CodedValues.SAD_DEFERIDA).append("','");
        query.append(CodedValues.SAD_EMANDAMENTO).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')) ");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        // Quando ocorre consolidação, é necessario contabilizar as exclusões e inclusões que estão sendo exportadas
        // como alteração.
        if (consolidaMovFin) {
            queryBuscaQtdInclusaoBase(true); // Adiciona na tabela tb_tmp_val_mov_conciliacao lista de contratos de inclusão que serão consolidados como alteração
            queryBuscaQtdExclusaoBase(true); // Adiciona na tabela tb_tmp_val_mov_conciliacao lista de contratos de exclusão que serão consolidados como alteração
        }

        LOG.debug(query);
        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo ALTERACAO : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        if (!folhaAceitaAlteracao) {
            // Se a folha não aceita comandos de alteração de contrato, então para os
            // contratos alterados devemos mandar um comando de liquidação e outro de
            // inclusão. Inserimos então no resultado da query mais uma linha para
            // os contratos alterados, com a operação igual a "I", então podemos
            // mapear a alteração para a liquidação.
            query.setLength(0);
            query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao, ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(") ");
            query.append("SELECT DISTINCT 'I' AS 'TIPO', ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
            query.append("FROM tb_tmp_val_mov_conciliacao vmcSrc ");
            LOG.debug(query);
            try {
                jdbc.update(query.toString(), queryParams);
            } catch (DataAccessException ex) {
                String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo ALTERACAO : " + ex.getMessage();
                LOG.error(msg, ex);
                throw ZetraException.byMessage(msg, ex);
            }
            query.setLength(0);
            query.append("update tb_tmp_val_mov_conciliacao set vmc_operacao='E' where  vmc_operacao='A' ");
            LOG.debug(query);
            try {
                jdbc.update(query.toString(), queryParams);
            } catch (DataAccessException ex) {
                String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo ALTERACAO : " + ex.getMessage();
                LOG.error(msg, ex);
                throw ZetraException.byMessage(msg, ex);
            }
        }

        atualizaCamposChaveConsolidacao();

        query.setLength(0);
        query.append("SELECT 'ALTERACAO' AS 'TIPO', ").append(geraClausulaCount()).append(" AS 'qtd' ");
        query.append("FROM tb_tmp_val_mov_conciliacao vmc where vmc_operacao='A'");
        return buscaQtdBase(query.toString(), queryParams);
    }

    /**
     * Constroi a query que busca os contratos a serem incluidos ou se listaCandidatosAlteracao=true e movimento é consolidado, lista os contratos
     * incluidos exportados como alteração (esta lista é usada na query de alteração).
     * @return
     * @throws ZetraException
     */
    private String queryBuscaQtdInclusaoBase(Boolean listaCandidatosAlteracao) throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();

        // Inclusão/Relançamento no período
        query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao,").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(") ");
        if (consolidaMovFin && listaCandidatosAlteracao) {
            query.append("SELECT DISTINCT 'A' AS 'TIPO',").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
        } else {
            query.append("SELECT DISTINCT 'I' AS 'TIPO',").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
        }
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
        query.append("LEFT OUTER JOIN tb_ocorrencia_autorizacao ocaExc ON (ade.ade_codigo = ocaExc.ade_codigo and ocaExc.toc_codigo in ('");
        query.append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','");
        query.append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
        query.append("') and ocaExc.oca_periodo > pex.pex_periodo) ");
        query.append("WHERE (ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append("  AND (coalesce(ade.ade_prd_pagas, 0) = 0) ");
        query.append("  AND (ade.ade_ano_mes_ini = pex.pex_periodo) ");
        query.append((!enviaContratoRseExcluido ? "  AND srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " : ""));
        query.append("  AND (ade.sad_codigo IN ('");
        query.append(CodedValues.SAD_DEFERIDA).append("','");
        query.append(CodedValues.SAD_EMANDAMENTO).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')");
        query.append(" OR (ade.sad_codigo IN ('");
        query.append(CodedValues.SAD_CANCELADA).append("','");
        query.append(CodedValues.SAD_LIQUIDADA).append("')");
        query.append(" AND ocaExc.oca_codigo IS NOT NULL) ");
        query.append(") ");

        if (consolidaMovFin) {
            // Se existe contrato aberto anterior a esta inclusão então a inclusão vira alteração
            if (listaCandidatosAlteracao) {
                query.append("  AND (EXISTS (select 1 from tb_aut_desconto ade2 ");
            } else {
                query.append("  AND NOT EXISTS (select 1 from tb_aut_desconto ade2 ");
            }
            query.append("          inner join tb_verba_convenio vco2 ON (ade2.vco_codigo = vco2.vco_codigo) ");
            query.append("          inner join tb_convenio cnv2 ON (vco2.cnv_codigo = cnv2.cnv_codigo) ");
            query.append("          where ade2.sad_codigo in ('");
            query.append(CodedValues.SAD_DEFERIDA).append("','");
            query.append(CodedValues.SAD_EMANDAMENTO).append("','");
            query.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
            query.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')");
            query.append("          and ade.rse_codigo=ade2.rse_codigo ");
            query.append("          and cnv.csa_codigo=cnv2.csa_codigo ");
            query.append("          and cnv.cnv_cod_verba=cnv2.cnv_cod_verba ");
            query.append("          and ade2.ade_ano_mes_ini < pex.pex_periodo ");
            query.append("          )");

            // Se existe uma exclusão dentro do período trata como alteração
            if (listaCandidatosAlteracao) {
                query.append("  OR EXISTS (select 1 from tb_aut_desconto ade3 ");

            } else {
                query.append("  AND NOT EXISTS (select 1 from tb_aut_desconto ade3 ");
            }
            query.append("          inner join tb_verba_convenio vco3 ON (ade3.vco_codigo = vco3.vco_codigo) ");
            query.append("          inner join tb_convenio cnv3 ON (vco3.cnv_codigo = cnv3.cnv_codigo) ");

            query.append("          inner join tb_ocorrencia_autorizacao oca3 ON (ade3.ade_codigo = oca3.ade_codigo and oca3.toc_codigo in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            // Quando enviaConclusaoFolha é true, devemos enviar a inclusão como alteração.
            if (enviaConclusaoFolha && exportaLiqIndependenteQtdPagas) {
                query.append("','").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("','").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
            }
            query.append("'))");

            query.append("          where ade3.sad_codigo in ('");
            query.append(CodedValues.SAD_CANCELADA).append("','");
            if (enviaConclusaoFolha && exportaLiqIndependenteQtdPagas) {
                query.append(CodedValues.SAD_CONCLUIDO).append("','");
            }
            query.append(CodedValues.SAD_LIQUIDADA).append("')");
            query.append("          and ade.rse_codigo=ade3.rse_codigo ");
            query.append("          and cnv.csa_codigo=cnv3.csa_codigo ");
            query.append("          and cnv.cnv_cod_verba=cnv3.cnv_cod_verba ");
            query.append("          and oca3.oca_periodo = pex.pex_periodo ");
            query.append("          )");
            if (listaCandidatosAlteracao) {
                query.append("  )");
            }

        }

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.debug(query);
        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo INCLUSAO : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        atualizaCamposChaveConsolidacao();

        if (listaCandidatosAlteracao) {
            // A tabela de tb_tmp_val_mov_conciliacao foi carregada com os contratos incluídos candidatos a ALTERACAO para uso no método que conta alterações.
            return "";
        } else {
            query.setLength(0);
            query.append("SELECT 'INCLUSAO' AS 'TIPO', ").append(geraClausulaCount()).append(" AS 'qtd' ");
            query.append("FROM tb_tmp_val_mov_conciliacao vmc where vmc_operacao='I'");
            return query.toString();
        }
    }

    private long buscaQtdInclusaoBase(MapSqlParameterSource queryParams) throws ZetraException {
        return buscaQtdBase(queryBuscaQtdInclusaoBase(false), queryParams);
    }

    private long buscaQtdRenegociacaoBase() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();

        // Ocorrencias de renegociacao/compra no periodo
        query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao, rse_codigo, csa_identificador, cnv_cod_verba) ");
        query.append("SELECT DISTINCT 'R' AS 'TIPO', adeInc.rse_codigo, csa.csa_identificador, cnvInc.cnv_cod_verba ");
        query.append("FROM tb_aut_desconto adeInc ");
        query.append("INNER JOIN tb_verba_convenio vcoInc ON (adeInc.vco_codigo = vcoInc.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnvInc ON (vcoInc.cnv_codigo = cnvInc.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnvInc.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_relacionamento_autorizacao rad ON (adeInc.ade_codigo = rad.ade_codigo_destino) ");
        query.append("INNER JOIN tb_aut_desconto adeExc on (adeExc.ade_codigo = rad.ade_codigo_origem and COALESCE(adeInc.ade_indice, '') = COALESCE(adeExc.ade_indice, '')) ");
        query.append("INNER JOIN tb_verba_convenio vcoExc ON (adeExc.vco_codigo = vcoExc.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnvExc ON (vcoExc.cnv_codigo = cnvExc.cnv_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (adeInc.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (cnvInc.csa_codigo = csa.csa_codigo) ");
        query.append("LEFT OUTER JOIN tb_ocorrencia_autorizacao ocaExc ON (adeInc.ade_codigo = ocaExc.ade_codigo and ocaExc.toc_codigo in ('");
        query.append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','");
        query.append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
        query.append("') and ocaExc.oca_periodo > pex.pex_periodo) ");
        query.append("WHERE (adeInc.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append("  AND (adeInc.ade_vlr_folha IS NULL) ");
        query.append("  AND (adeInc.ade_ano_mes_ini = pex.pex_periodo) ");
        query.append(!enviaContratoRseExcluido ? "  AND srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " : "");
        query.append(exportaLiqIndependenteAnoMesFim ? "" : "  AND (adeExc.ade_ano_mes_fim is null or adeExc.ade_ano_mes_fim >= date_sub(pex_periodo, interval 1 month)) ");
        query.append(exportaLiqIndependenteQtdPagas ? "" : "  AND (ifnull(adeExc.ade_prazo, 999999999) > ifnull(adeExc.ade_prd_pagas, 0)) ");
        query.append(exportaLiqCancNaoPagas ? "" : "  AND adeExc.ade_vlr_folha is not null ");
        query.append(!enviaConclusaoFolha ? "  AND (ifnull(adeExc.ade_prazo, 999999999) > ifnull(adeExc.ade_prd_pagas, 0) or date_add(adeExc.ade_ano_mes_fim, interval ifnull(adeExc.ade_carencia_final, 0) + " + carenciaFolha + " month) >= pex_periodo) " : "");
        query.append("  AND (rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','");
        query.append(CodedValues.TNT_CONTROLE_COMPRA).append("')) ");
        query.append("  AND (cnvInc.csa_codigo = cnvExc.csa_codigo) ");
        query.append("  AND (adeInc.sad_codigo IN ('");
        query.append(CodedValues.SAD_DEFERIDA).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
        query.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')");
        query.append(" OR (adeInc.sad_codigo IN ('");
        query.append(CodedValues.SAD_CANCELADA).append("','");
        query.append(CodedValues.SAD_LIQUIDADA).append("')");
        query.append(" AND ocaExc.oca_codigo IS NOT NULL) ");
        query.append(") ");
        query.append("  AND (adeExc.sad_codigo = '").append(CodedValues.SAD_LIQUIDADA).append("') ");
        query.append("  AND rad.rad_data between pex.pex_data_ini and pex.pex_data_fim "); // Evita contabilizar contratos que tiveram a
                                                                                          // exclusão enviada em um período e a inclusão em outro

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        try {
            LOG.debug(query);
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo RENEGOCIACAO : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        query.setLength(0);
        // Liquidação e reserva no mesmo período mas sem ser via 'Renegociar Contrato'
        query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao, rse_codigo, csa_identificador, cnv_cod_verba) ");
        query.append("SELECT DISTINCT 'P' AS 'TIPO', adeInc.rse_codigo, csa.csa_identificador, cnvInc.cnv_cod_verba ");
        query.append("FROM tb_aut_desconto adeInc ");
        query.append("INNER JOIN tb_verba_convenio vcoInc ON (adeInc.vco_codigo = vcoInc.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnvInc ON (vcoInc.cnv_codigo = cnvInc.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnvInc.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (adeInc.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_ocorrencia_autorizacao ocaInc ON (adeInc.ade_codigo = ocaInc.ade_codigo) ");
        query.append("INNER JOIN tb_aut_desconto adeExc on (adeExc.rse_codigo = adeInc.rse_codigo and adeExc.vco_codigo = adeInc.vco_codigo and adeExc.ade_codigo <> adeInc.ade_codigo and COALESCE(adeInc.ade_indice, '') = COALESCE(adeExc.ade_indice, '')) ");
        query.append("INNER JOIN tb_ocorrencia_autorizacao ocaExc ON (adeExc.ade_codigo = ocaExc.ade_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (cnvInc.csa_codigo = csa.csa_codigo) ");
        query.append("LEFT OUTER JOIN tb_relacionamento_autorizacao rad ON (adeInc.ade_codigo = rad.ade_codigo_destino AND rad.tnt_codigo in ('6','7')) ");
        query.append("WHERE (adeInc.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append("  AND (adeInc.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("') ");
        query.append("  AND (ocaInc.toc_codigo = '").append(CodedValues.TOC_TARIF_RESERVA).append("') ");
        query.append("  AND (ocaInc.oca_periodo = pex.pex_periodo) ");
        query.append("  AND (adeExc.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append("  AND (adeExc.sad_codigo = '").append(CodedValues.SAD_LIQUIDADA).append("') ");
        query.append("  AND (ocaExc.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("') ");
        query.append("  AND (ocaExc.oca_periodo = pex.pex_periodo) ");
        query.append("  AND (rad.ade_codigo_origem is null) ");
        query.append((!enviaContratoRseExcluido ? "  AND srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " : ""));
        query.append(exportaLiqIndependenteAnoMesFim ? "" : "  AND (adeExc.ade_ano_mes_fim is null or adeExc.ade_ano_mes_fim >= date_sub(pex_periodo, interval 1 month)) ");
        query.append(exportaLiqIndependenteQtdPagas ? "" : "  AND (ifnull(adeExc.ade_prazo, 999999999) > ifnull(adeExc.ade_prd_pagas, 0)) ");
        query.append(exportaLiqCancNaoPagas ? "" : "  AND adeExc.ade_vlr_folha is not null ");
        query.append(!enviaConclusaoFolha ? "  AND (ifnull(adeExc.ade_prazo, 999999999) > ifnull(adeExc.ade_prd_pagas, 0) or date_add(adeExc.ade_ano_mes_fim, interval ifnull(adeExc.ade_carencia_final, 0) + " + carenciaFolha + " month) >= pex_periodo) " : "");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        try {
            LOG.debug(query);
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo PSEUDO_RENEGOCIACAO : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        atualizaCamposChaveConsolidacao();

        query.setLength(0);
        query.append("SELECT 'RENEGOCIACAO' AS 'TIPO', ").append(geraClausulaCount()).append(" AS 'qtd' ");
        query.append("FROM tb_tmp_val_mov_conciliacao vmc where vmc_operacao='R'");
        query.append(" UNION ");
        query.append("SELECT 'PSEUDO_RENEGOCIACAO' AS 'TIPO', ").append(geraClausulaCount()).append(" AS 'qtd' ");
        query.append("FROM tb_tmp_val_mov_conciliacao vmc where vmc_operacao='P'");
        return buscaQtdBase(query.toString(), queryParams);
    }

    /**
     * Constroi a query que busca os contratos a serem excluidos ou se listaCandidatosAlteracao=true e movimento é consolidado, lista os contratos
     * excluidos exportados como alteração (esta lista é usada na query de alteração).
     * @return
     * @throws ZetraException
     */
    private String queryBuscaQtdExclusaoBase(Boolean listaCandidatosAlteracao) throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();

        // Ocorrencias de liquidacao no periodo
        if (consolidaMovFin && listaCandidatosAlteracao) {
            query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao,").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(") ");
            query.append("SELECT DISTINCT 'A' AS 'TIPO', ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
        } else {
            query.append("insert into tb_tmp_val_mov_conciliacao (vmc_operacao,").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(") ");
            query.append("SELECT DISTINCT 'E' AS 'TIPO', ").append(TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR)).append(" ");
        }
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_ocorrencia_autorizacao ocaExc ON (ade.ade_codigo = ocaExc.ade_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
        query.append("WHERE (ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
        query.append((!exportaLiqCancNaoPagas ? "  AND (ade.ade_vlr_folha IS NOT NULL) " : ""));
        query.append((!exportaLiqIndependenteAnoMesFim ? "  AND (ade.ade_ano_mes_fim is null or ade.ade_ano_mes_fim >= date_sub(pex.pex_periodo, interval 1 month)) " : ""));
        query.append((!enviaContratoRseExcluido ? "  AND rse.srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " : ""));
        // Contratos concluídos no prazo possuem ifnull(ade.ade_prazo, 999999999) = ifnull(ade.ade_prd_pagas, 0)
        query.append((exportaLiqIndependenteQtdPagas ? "" : "  AND (ifnull(ade.ade_prazo, 999999999) >" + (listaCandidatosAlteracao?"=":"") + " ifnull(ade.ade_prd_pagas, 0)) "));
        query.append("  AND (ade.ade_ano_mes_ini < pex.pex_periodo) ");

        // Se existe contrato aberto anterior a este período diferente do contrato sendo excluído, a exclusão é consolidada com este contrato
        // gerando alteração
        query.append("  AND ( ");
        if (consolidaMovFin) {
            if (listaCandidatosAlteracao || enviaConclusaoFolha) {
                // Conclusão de contrato é enviado quando pode consolidar como alteração de contrato existente
                query.append("(ocaExc.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("','").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                query.append("') and ade.sad_codigo = ").append(CodedValues.SAD_CONCLUIDO).append(") or ");
            }
        } else if (enviaConclusaoFolha) {
            // Conclusão de contrato é enviado quando pode consolidar como alteração de contrato existente
            query.append("(ocaExc.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("','").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
            query.append("') and ade.sad_codigo = ").append(CodedValues.SAD_CONCLUIDO).append(") or ");
        }

        // Exclusão de contratos cancelados ou liquidados
        query.append("  (ocaExc.toc_codigo in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("')");
        query.append("  AND ade.sad_codigo in ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_CANCELADA).append("')) or ");

        // Exclusão de contratos que foram suspensos
        query.append("  (ocaExc.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("'");
        query.append("  AND ade.sad_codigo in ('").append(CodedValues.SAD_SUSPENSA).append("','").append(CodedValues.SAD_SUSPENSA_CSE).append("','").append(CodedValues.SAD_ESTOQUE_MENSAL).append("')))");

        query.append("  AND (ocaExc.oca_periodo = pex.pex_periodo) ");

        if (consolidaMovFin) {
            // Se existe contrato aberto anterior a este período diferente do contrato sendo excluído, a exclusão é consolidada com este contrato
            // gerando alteração
            if (listaCandidatosAlteracao) {
                query.append("  AND EXISTS (select 1 from tb_aut_desconto ade2 ");
            } else {
                query.append("  AND NOT EXISTS (select 1 from tb_aut_desconto ade2 ");
            }
            query.append("      inner join tb_verba_convenio vco2 ON (ade2.vco_codigo = vco2.vco_codigo) ");
            query.append("      inner join tb_convenio cnv2 ON (vco2.cnv_codigo = cnv2.cnv_codigo) ");
            query.append("      where ade2.sad_codigo in ('");
            query.append(CodedValues.SAD_DEFERIDA).append("','");
            query.append(CodedValues.SAD_EMANDAMENTO).append("','");
            query.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
            query.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')");
            query.append("      and ade.rse_codigo=ade2.rse_codigo ");
            query.append("      and cnv.csa_codigo=cnv2.csa_codigo ");

            // Conlusões são enviadas como alterações se existe outro contrato em andamento independente de enviaConclusaoFolha.
            query.append("      and (");
            if (listaCandidatosAlteracao || enviaConclusaoFolha) {
                query.append("      (ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' and ade2.ADE_ANO_MES_INI<=pex.pex_periodo) or ");
            }
            query.append("      (ade.sad_codigo in ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_CANCELADA).append("') and ade2.ADE_ANO_MES_INI<=pex.pex_periodo))");

            query.append("      and ade2.ADE_ANO_MES_INI<").append(listaCandidatosAlteracao?"":"=").append("pex.pex_periodo ");
            query.append("      and cnv.cnv_cod_verba=cnv2.cnv_cod_verba )");
        }

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.debug(query);
        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível gerar tabela tb_tmp_val_mov_conciliacao com dados de tipo EXCLUSAO : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        atualizaCamposChaveConsolidacao();

        if (listaCandidatosAlteracao) {
            // A tabela de tb_tmp_val_mov_conciliacao foi carregada com os contratos excluídos candidatos a ALTERACAO para uso no método que conta alterações.
            return "";
        } else {
            query.setLength(0);
            query.append("SELECT 'EXCLUSAO' AS 'TIPO', ").append(geraClausulaCount()).append(" AS 'qtd' ");
            query.append("FROM tb_tmp_val_mov_conciliacao vmc where vmc_operacao='E' ");

            return query.toString();
        }
    }

    private long buscaQtdExclusaoBase(MapSqlParameterSource queryParams) throws ZetraException {
        return buscaQtdBase(queryBuscaQtdExclusaoBase(false), queryParams);
    }

    private String geraClausulaCount() {
        if (consolidaMovFin) {
            return "COUNT(DISTINCT vmc.rse_matricula, COALESCE(vmc.org_identificador, '-'), COALESCE(vmc.est_identificador, '-'), COALESCE(vmc.csa_identificador, '-'), vmc.cnv_cod_verba)";
        } else {
            return "COUNT(*)";
        }
    }

    private void atualizaCamposChaveConsolidacao() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("UPDATE tb_tmp_val_mov_conciliacao vmc ");
        query.append("INNER JOIN tb_aut_desconto ade ON (vmc.ade_numero = ade.ade_numero) ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
        query.append("SET vmc.rse_matricula = COALESCE(vmc.rse_matricula, rse.rse_matricula) ");
        query.append("WHERE vmc.rse_matricula IS NULL ");
        LOG.debug(query);
        jdbc.update(query.toString(), queryParams);
    }

    public void setListaContratos(boolean listaContratos) {
        this.listaContratos = listaContratos;
    }

    /**
     * Busca contratos que foram exportados mas não constam na validação para exportar.
     * @return
     */
    private List<TransferObject> buscaDiferencasExportadoValidado(String tipoOperacao) {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("tipoOperacao", tipoOperacao);
        try {
            StringBuilder campos = new StringBuilder();
            for (int i=0;i<camposChave.size();i++) {
                campos.append((i==0) ? "amv." : ",amv.").append(camposChave.get(i));
             }

            StringBuilder query = new StringBuilder();
            query.append("SELECT DISTINCT ").append(campos);
            query.append(" FROM tb_arquivo_movimento_validacao amv");
            if (camposChave.contains("ade_codigo")) { // Não consolidado usa ade_codigo
                query.append(" INNER JOIN tb_aut_desconto ade ON (amv.ade_codigo=ade.ade_codigo) ");
                query.append(" LEFT JOIN tb_tmp_val_mov_conciliacao vmc ON (amv.amv_operacao=vmc.vmc_operacao ");
                query.append("   and amv.ade_codigo=vmc.ade_codigo) ");
                query.append(" and vmc.ade_codigo is null ");
            } else {
                query.append(" LEFT JOIN tb_tmp_val_mov_conciliacao vmc ON (amv.amv_operacao=vmc.vmc_operacao ");
                query.append("   and amv.org_identificador=vmc.org_identificador ");
                query.append("   and amv.est_identificador=vmc.est_identificador ");
                query.append("   and amv.rse_matricula=vmc.rse_matricula ");
                query.append("   and amv.cnv_cod_verba=vmc.cnv_cod_verba ");
                query.append("   ) ");
                query.append("where amv.amv_operacao = :tipoOperacao ");
                query.append("and vmc.rse_matricula is null ");
            }

            LOG.debug(query.toString());

            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "rse_matricula,cnv_cod_verba,ade_vlr", MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca contratos que não foram exportados mas constam na validação para exportar.
     * @return
     */
    private List<TransferObject> buscaDiferencasValidadoExportado(String tipoOperacao) {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("tipoOperacao", tipoOperacao);
        try {
            StringBuilder campos = new StringBuilder();
            for (int i=0;i<camposChave.size();i++) {
                campos.append((i==0) ? "vmc." : ",vmc.").append(camposChave.get(i));
             }

            StringBuilder query = new StringBuilder();
            query.append("SELECT DISTINCT ").append(campos);
            query.append(" FROM tb_tmp_val_mov_conciliacao vmc ");
            if (camposChave.contains("ade_codigo")) { // Não consolidado usa ade_codigo
                query.append(" INNER JOIN tb_aut_desconto ade ON (vmc.ade_codigo=ade.ade_codigo) ");
                query.append(" LEFT JOIN tb_arquivo_movimento_validacao amv ON (amv.amv_operacao=vmc.vmc_operacao ");
                query.append("   and amv.ade_codigo=vmc.ade_codigo) ");
                query.append(" and amv.ade_codigo is null ");
            } else {
                query.append(" LEFT JOIN tb_arquivo_movimento_validacao amv ON (amv.amv_operacao=vmc.vmc_operacao ");
                query.append("   and amv.org_identificador=vmc.org_identificador ");
                query.append("   and amv.est_identificador=vmc.est_identificador ");
                query.append("   and amv.rse_matricula=vmc.rse_matricula ");
                query.append("   and amv.cnv_cod_verba=vmc.cnv_cod_verba ");
                query.append("   ) ");
                query.append("where vmc.vmc_operacao = :tipoOperacao ");
                query.append("and amv.rse_matricula is null ");
            }

            LOG.debug(query.toString());

            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "rse_matricula,cnv_cod_verba,ade_vlr", MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
