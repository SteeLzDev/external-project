package com.zetra.econsig.folha.exportacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExportaMovimentoBase</p>
 * <p>Description: Classe base para as classes de exportação de modo a centralizar
 * métodos comuns e evitar a declaração de métodos vazios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ExportaMovimentoBase implements ExportaMovimento {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaMovimentoBase.class);
    private static final long serialVersionUID = 29769L;

    @Override
    public boolean sobreporExportaMovimentoFinanceiro(AcessoSistema responsavel) {
        return false;
    }

    @Override
    public String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        return null;
    }

    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void preProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void posProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void preProcessaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void posProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void gravaArquivoDiferencas(String nomeArqSaidaMov, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public String posProcessaArqLote(String nomeArqLote, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Para manter compatibilidade, não faz nada, apenas retorna o nome original
        return nomeArqLote;
    }


    @Override
    public void preCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    }

    /**
     * Remove da tabela de exportação os contratos de servidores bloqueados ou que possuem bloqueios de verba/serviço.
     * @throws DataAccessException
     */
    protected void removerContratosServidoresBloqueados() throws DataAccessException {
        removerContratosServidoresBloqueados(false);
    }

    /**
     * Remove da tabela de exportação os contratos de servidores bloqueados ou que possuem bloqueios de verba/serviço/natureza.
     * Se "ignoraEmAndamento" = TRUE, não exclui contratos que já estão na folha (em andamento com valor folha).
     * @param ignoraEmAndamento
     * @throws DataAccessException
     */
    protected void removerContratosServidoresBloqueados(boolean ignoraEmAndamento) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Situações as quais contratos de servidores bloqueados podem ser exportados
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_CANCELADA);
        sadCodigos.add(CodedValues.SAD_LIQUIDADA);
        // Envia exclusão de contratos suspensos, para movimento inicial
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        // Por garantia inclui também os concluidos
        sadCodigos.add(CodedValues.SAD_CONCLUIDO);
        // Contratos em estoque mensal só são exportados como exclusões
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);

        // Ignora contratos que estão na folha, ou seja em andamento com valor folha maior
        // que zero. Contratos que já estão na folha só devem ser exportados por causa de uma alteração.
        String complemento = "";
        if (ignoraEmAndamento) {
            complemento = " and not (sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and coalesce(ade_vlr_folha, 0) > 0)";
        }

        final StringBuilder query = new StringBuilder();

        // Não exporta inclusões/alterações de servidores bloqueados
        query.setLength(0);
        query.append("delete from tb_tmp_exportacao where srs_codigo in ('" + TextHelper.join(CodedValues.SRS_BLOQUEADOS, "','") + "') and sad_codigo not in ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        query.append(complemento);
        LOG.debug(query.toString());
        jdbc.update(query.toString(), queryParams);

        // Não exporta inclusões/alterações de servidores bloqueados para determinada verba
        query.setLength(0);
        query.append("delete from tb_tmp_exportacao using tb_tmp_exportacao ");
        query.append("inner join tb_param_convenio_registro_ser pcr on (pcr.rse_codigo = tb_tmp_exportacao.rse_codigo and pcr.cnv_codigo = tb_tmp_exportacao.cnv_codigo) ");
        query.append("where pcr.pcr_vlr = '0' and pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' and sad_codigo not in ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        query.append(complemento);
        LOG.debug(query.toString());
        jdbc.update(query.toString(), queryParams);

        // Não exporta inclusões/alterações de servidores bloqueados para determinado serviço
        query.setLength(0);
        query.append("delete from tb_tmp_exportacao using tb_tmp_exportacao ");
        query.append("inner join tb_param_servico_registro_ser psr on (psr.rse_codigo = tb_tmp_exportacao.rse_codigo and psr.svc_codigo = tb_tmp_exportacao.svc_codigo) ");
        query.append("where psr.psr_vlr = '0' and psr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("' and sad_codigo not in ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        query.append(complemento);
        LOG.debug(query.toString());
        jdbc.update(query.toString(), queryParams);

        // Não exporta inclusões/alterações de servidores bloqueados para determinada natureza de serviço
        query.setLength(0);
        query.append("delete from tb_tmp_exportacao using tb_tmp_exportacao ");
        query.append("inner join tb_servico svc on (svc.svc_codigo = tb_tmp_exportacao.svc_codigo) ");
        query.append("inner join tb_param_nse_registro_ser pnr on (pnr.rse_codigo = tb_tmp_exportacao.rse_codigo and pnr.nse_codigo = svc.nse_codigo) ");
        query.append("where pnr.pnr_vlr = '0' and pnr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("' and sad_codigo not in ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        query.append(complemento);
        LOG.debug(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Remove da tabela de exportação os contratos que não cabem na margem.
     * A implementação considera qualquer uma das margens 1, 2 ou 3.
     * Se for margem casada, faz tratamento diferencial.
     * Método implementado para exportação mensal.
     * @throws ExportaMovimentoException
     */
    @Deprecated
    protected void removerContratosSemMargemMovimentoMensal() throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

        final StringBuilder query = new StringBuilder();

        // Lista os contratos de servidores com margem negativa pela ordem de exportação
        if (margem1CasadaMargem3 || margem123Casadas || margem1CasadaMargem3Esq || margem123CasadasEsq || margem1CasadaMargem3Lateral) {
            throw new ExportaMovimentoException("mensagem.erro.exportacao.rotina.casamento.margem.nao.suportado", responsavel);
        } else {
            // OBS: A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
            // contratos removidos à margem restante até que a mesma seja positiva.
            query.append("select tmp.rse_codigo, tmp.rse_margem_rest, tmp.rse_margem_rest_2, tmp.rse_margem_rest_3, tmp.ade_codigo, tmp.ade_vlr, ade.ade_inc_margem ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) ");
            query.append("where ((ade.ade_inc_margem = '1' and tmp.rse_margem_rest   < 0.00) ");
            query.append("    OR (ade.ade_inc_margem = '2' and tmp.rse_margem_rest_2 < 0.00) ");
            query.append("    OR (ade.ade_inc_margem = '3' and tmp.rse_margem_rest_3 < 0.00)) ");
            query.append("order by tmp.rse_codigo, ade.ade_inc_margem, ");
            query.append("coalesce(svc_prioridade, 9999999) + 0 DESC, coalesce(cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        }

        LOG.debug(query.toString());

        try {
            final String fieldsNames = "rse_codigo,rse_margem_rest,rse_margem_rest_2,rse_margem_rest_3,ade_codigo,ade_vlr,ade_inc_margem";
            final List<TransferObject> contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
            final List<String> adeImpropria = obterContratosSemMargemMovimentoMensal(contratos);

            // Apaga os contratos que não devem ser lançados do último servidor
            if (!adeImpropria.isEmpty()) {
                gravaMotivoNaoExportacao(adeImpropria, TipoMotivoNaoExportacaoEnum.SERVIDOR_SEM_MARGEM_SUFICIENTE);

                excluirContratos(adeImpropria);
                adeImpropria.clear();
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    /**
    *
    * @param rseCodigo
    * @param marCodigo
    * @param queryParams
    * @return
    */
    private String queryMargemUsadaPosCorte(String rseCodigo, Short marCodigo, MapSqlParameterSource queryParams) {
        queryParams.addValue("rseCodigo", rseCodigo);
        queryParams.addValue("marCodigo", marCodigo);

        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT sum(hmr_margem_antes-hmr_margem_depois) AS total ");
        query.append("FROM tb_historico_margem_rse hmr ");
        query.append("WHERE hmr.rse_codigo = :rseCodigo");
        query.append(" AND hmr.mar_codigo = :marCodigo");
        query.append(" AND oca_codigo IS NOT NULL");
        query.append(" AND hmr_data > ");
        query.append("(select pex_data_fim ");
        query.append("from tb_periodo_exportacao pex ");
        query.append("inner join tb_registro_servidor rse ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where rse.rse_codigo = hmr.rse_codigo)");
        query.append(" GROUP BY hmr.rse_codigo");
        return query.toString();
    }

    /**
     *
     * @param rseCodigo
     * @param marCodigo
     * @param pexDataFim
     * @param queryParams
     * @return
     */
    protected String queryMargemUsadaPosCorte(String rseCodigo, Short marCodigo, String pexDataFim, MapSqlParameterSource queryParams) {
        queryParams.addValue("rseCodigo", rseCodigo);
        queryParams.addValue("marCodigo", marCodigo);
        queryParams.addValue("pexDataFim", pexDataFim);

        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT sum(hmr_margem_antes-hmr_margem_depois) AS total ");
        query.append("FROM tb_historico_margem_rse hmr ");
        query.append("WHERE hmr.rse_codigo = :rseCodigo");
        query.append(" AND hmr.mar_codigo = :marCodigo");
        query.append(" AND oca_codigo IS NOT NULL");
        query.append(" AND hmr_data > :pexDataFim");
        query.append(" GROUP BY hmr.rse_codigo");
        return query.toString();
    }

    /**
     * Faz o join com "rse.rse_codigo" e "pex_data_fim", utilizando o marCodigo passado
     * @param marCodigo
     * @param queryParams
     * @return
     */
    protected String queryMargemUsadaPosCorte(Short marCodigo, MapSqlParameterSource queryParams) {
        queryParams.addValue("marCodigo", marCodigo);

        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT sum(hmr_margem_antes-hmr_margem_depois) AS total ");
        query.append("FROM tb_historico_margem_rse hmr ");
        query.append("WHERE hmr.rse_codigo = rse.rse_codigo");
        query.append(" AND hmr.mar_codigo = :marCodigo");
        query.append(" AND oca_codigo IS NOT NULL");
        query.append(" AND hmr_data > pex.pex_data_fim");
        query.append(" GROUP BY hmr.rse_codigo");
        return query.toString();
    }

    /**
     * Faz o join com "rse.rse_codigo", "mrs.mar_codigo" e "pex_data_fim"
     * @param queryParams
     * @return
     */
    protected String queryMargemUsadaPosCorte(MapSqlParameterSource queryParams) {
        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT sum(hmr_margem_antes-hmr_margem_depois) AS total ");
        query.append("FROM tb_historico_margem_rse hmr ");
        query.append("WHERE hmr.rse_codigo = rse.rse_codigo");
        query.append(" AND hmr.mar_codigo = mrs.mar_codigo");
        query.append(" AND oca_codigo IS NOT NULL");
        query.append(" AND hmr_data > pex.pex_data_fim");
        query.append(" GROUP BY hmr.rse_codigo");
        return query.toString();
    }
    /**
     *
     * @param connection
     * @param rseCodigo
     * @param marCodigo
     * @param responsavel
     * @return
     * @throws DataAccessException
     */
    protected BigDecimal calcularMargemUsadaPosCorte(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final BigDecimal margemUsadaPostCorte = jdbc.queryForObject(queryMargemUsadaPosCorte(rseCodigo, marCodigo, queryParams), queryParams, BigDecimal.class);

        if (margemUsadaPostCorte != null) {
            LOG.debug(String.format("rseCodigo: %s - marCodigo: %d - margemUsadaPosCorte: %,.2f", rseCodigo, marCodigo, margemUsadaPostCorte));
            return margemUsadaPostCorte;
        }

        return BigDecimal.ZERO;
    }

    /**
     * Lista os contratos de servidores com margem negativa, deconsiderando as mudanças após o corte, pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
    	final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, ").append(getClausulaIncideMargemNaListaContratosSemMargem(marCodigos)).append(", tmp.autoriza_pgt_parcial ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 2 and tmp.rse_margem_rest_2 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_data,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    /**
     * Por padrão, a validação dos contratos sem margem é feita na margem ao qual a consignação incide, o que corresponde
     * ao campo ade_inc_margem da tabela tb_tmp_exportacao (alias tmp). Este método serve para sobreposição desta configuração
     * padrão, a exemplo, forçando a validação em uma margem específica. OBS: NÃO ALTERAR O PADRÃO, CASO NECESSÁRIO CRIAR
     * CLASSES ESPECÍFICAS DE SISTEMA PARA ALTERAR O COMPORTAMENTO.
     * @param marCodigos
     * @return
     */
    protected String getClausulaIncideMargemNaListaContratosSemMargem(List<Short> marCodigos) {
        return "tmp.ade_inc_margem";
    }

    protected void removerContratosSemMargemMovimentoMensalv2(boolean permiteDescontoParcial, List<Short> marCodigos) throws ExportaMovimentoException {
        removerContratosSemMargemMovimentoMensalv2(permiteDescontoParcial, marCodigos, false);
    }

    protected void removerContratosSemMargemMovimentoMensalv2(boolean permiteDescontoParcial, List<Short> marCodigos, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        removerContratosSemMargemMovimentoMensalv2(permiteDescontoParcial, marCodigos, verificaParamCsaPgParcial, false);
    }

    /**
     * Rotina, para movimento mensal, que remove da tabela de exportação os contratos que não cabem na margem.
     * A implementação considera qualquer uma das margens 1, 2, 3 ou margem extra. Se for margem casada, faz
     * tratamento de acordo com os parâmetros de casamento. Casamento de margem extra não foi implementado ainda.
     * @param permiteDescontoParcial
     * @param marCodigos
     * @param verificaParamCsaPgParcial - determina se verifica o parâmetro de CSA que permite ou não pagamento parcial
     * @param verificaLancamentoCartao - Adiciona os lançamentos cartão trazendo a margem do serviço da reserva para verificação
     * @throws ExportaMovimentoException
     */
    protected void removerContratosSemMargemMovimentoMensalv2(boolean permiteDescontoParcial, List<Short> marCodigos, boolean verificaParamCsaPgParcial, boolean verificaLancamentoCartao) throws ExportaMovimentoException {
        try {
            ContratosSemMargem adeImpropria = new ContratosSemMargem();
            if (verificaLancamentoCartao) {
            	final List<TransferObject> contratos = listaContratosSemMargemCandidatosInclusiveCartao(marCodigos);

            	// Foi criado um novo método pareceido com o  obterContratosSemMargemMovimentoMensalv2 e não alterado ele, pois o impacto poderia ser maior
            	// que o esperado caso ocorra alguma coisa, por não termos dimensão da quantidade de sistemas que usam o método sem cartão.
            	adeImpropria = obterContratosSemMargemMovimentoMensalIncluseCartao(contratos, permiteDescontoParcial, verificaParamCsaPgParcial);
            } else {
            	// Lista contratos candidatos a serem removidos de servidores com margem negativa
            	final List<TransferObject> contratos = listaContratosSemMargemCandidatosv2(marCodigos);
            	adeImpropria = obterContratosSemMargemMovimentoMensalv2(contratos, permiteDescontoParcial, verificaParamCsaPgParcial);
            }

            // Apaga os contratos que não devem ser lançados do último servidor
            if (!adeImpropria.getIntegralmenteSemMargem().isEmpty()) {
                gravaMotivoNaoExportacao(adeImpropria.getIntegralmenteSemMargem(), TipoMotivoNaoExportacaoEnum.SERVIDOR_SEM_MARGEM_SUFICIENTE);
                excluirContratos(adeImpropria.getIntegralmenteSemMargem());
            }
            if (permiteDescontoParcial && !adeImpropria.getParcialmenteSemMargem().isEmpty()) {
                atualizarParcelaPgtParcial(adeImpropria.getParcialmenteSemMargem());
            }
        } catch (final SQLException | DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o motivo de não exportação dos contratos passados por parâmetro,
     * previamente selecionados na rotina de validação de margem
     * @param adeImpropria
     * @param tipoMotivoNaoExportacao
     * @throws DataAccessException
     */
    protected void gravaMotivoNaoExportacao(List<String> adeImpropria, TipoMotivoNaoExportacaoEnum tipoMotivoNaoExportacao) throws DataAccessException {
        if ((adeImpropria != null) && !adeImpropria.isEmpty()) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("update tb_aut_desconto ade ");
            query.append("set mne_codigo = :mneCodigo ");
            query.append("where ade_codigo in (:adeCodigos) ");
            queryParams.addValue("adeCodigos", adeImpropria);
            queryParams.addValue("mneCodigo", tipoMotivoNaoExportacao.getCodigo());
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Remove da tabela de exportação as consignações na lista passada por parâmetro
     * @param adeCodigos
     * @throws DataAccessException
     */
    protected void excluirContratos(List<String> adeCodigos) throws DataAccessException {
        if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.exportacao.removendo.contratos.sem.margem", (AcessoSistema)null));

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("delete from tb_tmp_exportacao ");
            query.append("where ade_codigo in (:adeCodigos) ");
            queryParams.addValue("adeCodigos", adeCodigos);
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Atualiza o valor dos contratos para pagamento parcial daqueles que não cabem integralmente na
     * margem, em sistemas que permite esta rotina, somente na tabela de exportação
     * @param parcialmenteSemMargem
     * @throws SQLException
     */
    protected void atualizarParcelaPgtParcial(Map<String, BigDecimal> parcialmenteSemMargem) throws SQLException {
        if ((parcialmenteSemMargem != null) && !parcialmenteSemMargem.isEmpty()) {
            final String query = "update tb_tmp_exportacao set ade_vlr = ? where ade_codigo = ?";
            Connection conn = null;
            PreparedStatement preStat = null;
            try {
                conn = DBHelper.makeConnection();
                preStat = conn.prepareStatement(query);
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
     * Navega na lista de contratos passada e verifica se este pode
     * ser exportado de acordo com a margem restante do servidor
     * @param contratos
     * @return
     */
    @Deprecated
    public static List<String> obterContratosSemMargemMovimentoMensal(List<TransferObject> contratos) {
        final List<String> adeImpropria = new ArrayList<>();
        if ((contratos != null) && !contratos.isEmpty()) {
            String rseCodigo = null;
            String rseCodigoAnterior = null;
            Short adeIncMargem = null;
            Short adeIncMargemAnterior = null;
            String adeCodigo = null;

            BigDecimal margemRest = new BigDecimal(0);
            BigDecimal adeVlr;

            for (final TransferObject contrato : contratos) {
                rseCodigo = contrato.getAttribute("rse_codigo").toString();
                adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                // Se trocou de servidor ou de incidência na margem (ou é a primeira vez)
                if (!rseCodigo.equals(rseCodigoAnterior) || !adeIncMargem.equals(adeIncMargemAnterior)) {
                    rseCodigoAnterior = rseCodigo;
                    adeIncMargemAnterior = adeIncMargem;

                    if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                        margemRest = new BigDecimal(contrato.getAttribute("rse_margem_rest").toString());
                    } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                        margemRest = new BigDecimal(contrato.getAttribute("rse_margem_rest_2").toString());
                    } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                        margemRest = new BigDecimal(contrato.getAttribute("rse_margem_rest_3").toString());
                    } else {
                        margemRest = new BigDecimal(contrato.getAttribute("mrs_margem_rest").toString());
                    }
                }

                adeCodigo = contrato.getAttribute("ade_codigo").toString();
                adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                // Soma o ade_vlr na margem rest até que este fique positivo
                // assim o tratamento realizado não precisa ser diferente caso o
                // sistema trabalhe com margem cheia ou líquida
                if (margemRest.doubleValue() < 0) {
                    // Contrato não cabe na margem
                    adeImpropria.add(adeCodigo);
                    margemRest = margemRest.add(adeVlr);
                }
            }
        }
        return adeImpropria;
    }

    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial) throws ExportaMovimentoException {
        return obterContratosSemMargemMovimentoMensalv2(contratos, permiteDescontoParcial, false);
    }

    /**
     * Navega na lista de contratos passada e verifica se este pode
     * ser exportado de acordo com a margem restante do servidor
     * @param contratos
     * @param permiteDescontoParcial
     * @param verificaParamCsaPgParcial - determina se verifica o parâmetro de CSA que permite ou não pagamento parcial
     * @return
     * @throws ExportaMovimentoException
     */
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            final ContratosSemMargem adeImpropria = new ContratosSemMargem();
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            final Short marCodigo = margemTO.getMarCodigo();
                            margemFolha.put(marCodigo, margemTO.getMrsMargem());
                            margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                            margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            final BigDecimal margemUsadaAposCorte = calcularMargemUsadaPosCorte(rseCodigo, marCodigo, responsavel);
                            margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaAposCorte));
                            margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaAposCorte));
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestante.get(adeIncMargem).signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (margem1CasadaMargem3Esq || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            // Contrato originalmente incidindo na margem 3, ao ser retirado, com casamento de margem
                            // deve ser adicionado às margens 1 e 2, de acordo com o tipo de casamento.
                            if (margem1CasadaMargem3 || margem1CasadaMargem3Esq || margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123Casadas || margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }
                        } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            // Passa o valor negativo, pois está sendo somado à margem
                            atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
                        }

                        if (margem1CasadaMargem3 || margem1CasadaMargem3Esq) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                        } else if (margem123Casadas || margem123CasadasEsq) {
                            // Realiza o acerto da margem, de acordo com o real restante e o limite das superiores
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2))));
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                        } else if (margem1CasadaMargem3Lateral) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3)).add(margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(BigDecimal.ZERO)));
                        }

                        if (!permiteDescontoParcial || (margemRestante.get(adeIncMargem).signum() <= 0)) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else if (!verificaParamCsaPgParcial || (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && CodedValues.TPA_SIM.equals(contrato.getAttribute("autoriza_pgt_parcial")))) {
						    // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
						    // na lista daqueles que podem pagar parcialmente com o valor restante de margem
						    adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
						} else {
						    // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
						    // lista dos contratos a serem removidos do movimento
						    adeImpropria.addContratoSemMargem(adeCodigo);
						}
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    /**
     *
     * @param connection
     * @param rseCodigo
     * @param marCodigo
     * @param margemFolha
     * @param margemRestante
     * @param margemUsada
     * @param responsavel
     * @throws AutorizacaoControllerException
     * @throws DataAccessException
     */
    protected void adicionarMargemUsadaNaoExportavel(String rseCodigo, List<MargemTO> margens,
                                                     Map<Short, BigDecimal> margemFolha, Map<Short, BigDecimal> margemRestante, Map<Short, BigDecimal> margemUsada,
                                                     AcessoSistema responsavel) throws AutorizacaoControllerException, SQLException {
        final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

        for (final MargemTO margemTO : margens) {
            final Short marCodigo = margemTO.getMarCodigo();
            // Buscar margem usada por contratos que não são exportados.
            final BigDecimal margemUsadaApenasEConsig = calcularMargemUsadaNaoExportavel(rseCodigo, marCodigo, responsavel);
            if (margemUsadaApenasEConsig.compareTo(BigDecimal.valueOf(0)) != 0) {
                margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaApenasEConsig));
                margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaApenasEConsig));

                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    if (margem1CasadaMargem3Esq || margem123CasadasEsq) {
                        margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(margemUsadaApenasEConsig));
                        margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsadaApenasEConsig));
                        if (margem123CasadasEsq) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(margemUsadaApenasEConsig));
                            margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsadaApenasEConsig));
                        }
                    }

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    if (margem123Casadas || margem123CasadasEsq) {
                        margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(margemUsadaApenasEConsig));
                        margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(margemUsadaApenasEConsig));
                        if (margem123CasadasEsq) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(margemUsadaApenasEConsig));
                            margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsadaApenasEConsig));
                        }
                    }

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    // Contrato originalmente incidindo na margem 3, ao ser retirado, com casamento de margem
                    // deve ser adicionado às margens 1 e 2, de acordo com o tipo de casamento.
                    if (margem1CasadaMargem3 || margem1CasadaMargem3Esq || margem123Casadas || margem123CasadasEsq) {
                        margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(margemUsadaApenasEConsig));
                        margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(margemUsadaApenasEConsig));
                        if (margem123Casadas || margem123CasadasEsq) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(margemUsadaApenasEConsig));
                            margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsadaApenasEConsig));
                        }
                    }
                } else if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                    // Passa o valor negativo, pois está sendo somado à margem
                    atualizaMargemExtraCasada(marCodigo, margemUsadaApenasEConsig.negate(), margemFolha, margemRestante, margemUsada);
                }

                if (margem1CasadaMargem3 || margem1CasadaMargem3Esq) {
                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                } else if (margem123Casadas || margem123CasadasEsq) {
                    // Realiza o acerto da margem, de acordo com o real restante e o limite das superiores
                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2))));
                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                } else if (margem1CasadaMargem3Lateral) {
                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3)).add(margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(BigDecimal.ZERO)));
                }
            }
        }
    }

    /**
     * Calcula margem de contratos que não são exportados mas que incidem na margem.
     * São considerados os contratos que:
     * - que estejam em situação "inativa", independentemente se integram ou não na folha (ade_int_folha):
     *    public static final String SAD_ESTOQUE                  = "12";
     *    public static final String SAD_ESTOQUE_NAO_LIBERADO     = "13";
     *    public static final String SAD_EMCARENCIA               = "14";
     *    public static final String SAD_ESTOQUE_MENSAL           = "16";
     *    public static final String SAD_AGUARD_MARGEM            = "17";
     * - que estejam tenham sido suspensas até o período atual
     *    public static final String SAD_SUSPENSA                 = "6";
     *    public static final String SAD_SUSPENSA_CSE             = "10";
     * - não integram a folha (ade_int_folha = 0) em qualquer situação "ativa":
     *    public static final String SAD_SOLICITADO               = "0";
     *    public static final String SAD_AGUARD_CONF              = "1";
     *    public static final String SAD_AGUARD_DEFER             = "2";
     *    public static final String SAD_DEFERIDA                 = "4";
     *    public static final String SAD_EMANDAMENTO              = "5";
     * - integram a folha (ade_int_folha = 1) a serem enviados apenas em período futuros (carência):
     *    public static final String SAD_SOLICITADO               = "0";
     *    public static final String SAD_AGUARD_CONF              = "1";
     *    public static final String SAD_AGUARD_DEFER             = "2";
     *    public static final String SAD_DEFERIDA                 = "4";
     * @param connection
     * @param rseCodigo
     * @param marCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     * @throws DataAccessException
     */
    protected BigDecimal calcularMargemUsadaNaoExportavel(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, SQLException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);
        queryParams.addValue("marCodigo", marCodigo);

        final StringBuilder query = new StringBuilder("/*SKIP_LOG*/");
        query.append("SELECT SUM(ade_vlr) AS total ");
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
        query.append("WHERE rse_codigo = :rseCodigo AND ade_inc_margem = :marCodigo ");
        query.append("AND (sad_codigo IN (");
        query.append("'").append(CodedValues.SAD_ESTOQUE).append("', ");
        query.append("'").append(CodedValues.SAD_ESTOQUE_NAO_LIBERADO).append("', ");
        query.append("'").append(CodedValues.SAD_EMCARENCIA).append("', ");
        query.append("'").append(CodedValues.SAD_ESTOQUE_MENSAL).append("', ");
        query.append("'").append(CodedValues.SAD_AGUARD_MARGEM).append("') ");
        query.append("OR (sad_codigo IN (");
        query.append("'").append(CodedValues.SAD_SUSPENSA).append("', ");
        query.append("'").append(CodedValues.SAD_SUSPENSA_CSE).append("') ");
        query.append("AND NOT EXISTS(");
        query.append("SELECT * ");
        query.append("FROM tb_ocorrencia_autorizacao oca ");
        query.append("WHERE oca.toc_codigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("' ");
        query.append("AND oca.ade_codigo = ade.ade_codigo ");
        query.append("AND oca_data > pex_data_fim ");
        query.append(")) ");
        query.append("OR (sad_codigo IN (");
        query.append("'").append(CodedValues.SAD_SOLICITADO).append("', ");
        query.append("'").append(CodedValues.SAD_AGUARD_CONF).append("', ");
        query.append("'").append(CodedValues.SAD_AGUARD_DEFER).append("', ");
        query.append("'").append(CodedValues.SAD_DEFERIDA).append("', ");
        query.append("'").append(CodedValues.SAD_EMANDAMENTO).append("') AND ade_int_folha = 0) ");
        query.append("OR (sad_codigo IN (");
        query.append("'").append(CodedValues.SAD_SOLICITADO).append("', ");
        query.append("'").append(CodedValues.SAD_AGUARD_CONF).append("', ");
        query.append("'").append(CodedValues.SAD_AGUARD_DEFER).append("') AND ade_int_folha = 1  ");
        query.append("AND ade_ano_mes_ini > pex_periodo) ");
        // DESENV-15831 : se o contrato é compra/renegociação então os contratos antigos ainda serão exportados e
        //                portanto a margem usada pelo novo contrato não deve ser considerada na exportação
        query.append("OR (sad_codigo = ").append("'").append(CodedValues.SAD_DEFERIDA).append("' ");
        query.append("AND ade_int_folha = 1 ");
        query.append("AND ade_ano_mes_ini > pex_periodo ");
        query.append("AND NOT EXISTS( ");
        query.append("SELECT * FROM tb_relacionamento_autorizacao rad ");
        query.append("WHERE rad.ade_codigo_destino = ade.ade_codigo ");
        query.append("AND rad.tnt_codigo IN ( ");
        query.append("'").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("', ");
        query.append("'").append(CodedValues.TNT_CONTROLE_COMPRA).append("'))) ");
        query.append("OR (sad_codigo IN (");
        query.append("'").append(CodedValues.SAD_DEFERIDA).append("', ");
        query.append("'").append(CodedValues.SAD_EMANDAMENTO).append("') AND ade_int_folha = 1 ");
        query.append("AND EXISTS(");
        query.append("SELECT * ");
        query.append("FROM tb_ocorrencia_autorizacao oca ");
        query.append("WHERE oca.toc_codigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' ");
        query.append("AND oca.ade_codigo = ade.ade_codigo ");
        query.append("AND oca_data > pex_data_fim ");
        query.append("))) ");
        query.append("AND NOT EXISTS(");
        query.append("SELECT * ");
        query.append("FROM tb_ocorrencia_autorizacao oca ");
        query.append("INNER JOIN tb_historico_margem_rse hmr ON (oca.oca_codigo = hmr.oca_codigo) ");
        query.append("WHERE hmr.rse_codigo = ade.rse_codigo AND mar_codigo = ade_inc_margem ");
        query.append("AND oca.ade_codigo = ade.ade_codigo ");
        query.append("AND hmr_data > pex_data_fim ");
        query.append(") ");
        // DESENV-17481 : Adiciona cláusula específica de sistemas
        query.append(incluirClausulaCalculoMargemUsadaNaoExportavel(rseCodigo, marCodigo, responsavel));
        BigDecimal margemNaoExportavel = jdbc.queryForObject(query.toString(), queryParams, BigDecimal.class);
        if (margemNaoExportavel == null) {
            margemNaoExportavel = BigDecimal.ZERO;
        }

        if (margemNaoExportavel.signum() > 0) {
            query.setLength(0);
            query.append("/*SKIP_LOG*/");
            // DESENV-17015 : Subtrai do valor os lançamentos de cartão que estejam associadas
            // a reserva de cartão que incidem na margem passada por parâmetro
            query.append("SELECT SUM(ade.ade_vlr) AS total_usado ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
            query.append("WHERE ade.rse_codigo = :rseCodigo ");
            query.append("AND ade.ade_inc_margem = 0 ");
            query.append("AND ade.ade_int_folha = 1 ");
            query.append("AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append("AND ade.ade_ano_mes_ini <= pex.pex_periodo ");
            query.append("AND EXISTS ( ");
            query.append("SELECT 1 FROM tb_relacionamento_autorizacao rad ");
            query.append("INNER JOIN tb_aut_desconto ade2 ON (rad.ade_codigo_origem = ade2.ade_codigo) ");
            query.append("WHERE rad.ade_codigo_destino = ade.ade_codigo ");
            query.append("AND rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
            query.append("AND ade2.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append("AND ade2.ade_int_folha = 0 ");
            query.append("AND ade2.ade_inc_margem = :marCodigo ");
            query.append(")");

            final BigDecimal vlrLancamentoCartao = jdbc.queryForObject(query.toString(), queryParams, BigDecimal.class);
            if (vlrLancamentoCartao != null) {
                margemNaoExportavel = margemNaoExportavel.subtract(vlrLancamentoCartao);
            }
        }

        if (margemNaoExportavel.signum() > 0) {
            LOG.debug(String.format("rseCodigo: %s - marCodigo: %d - margemNaoExportavel: %,.2f", rseCodigo, marCodigo, margemNaoExportavel));
        }

        return margemNaoExportavel;
    }

    /**
     * DESENV-17481 : Inclui cláusula adicional no cálculo de margem usada não exportável, caso algum
     * sistema queira excluir ou incluir certas condições no cálculo. Por padrão, retorna vazio, indicando
     * que não há cláusula extra.
     * @param rseCodigo
     * @param marCodigo
     * @param responsavel
     * @return
     */
    protected String incluirClausulaCalculoMargemUsadaNaoExportavel(String rseCodigo, Short marCodigo, AcessoSistema responsavel) {
        return ""; // NÃO ALTERAR O COMPORTAMENTO PADRÃO. CASO NECESSÁRIO USE A CLASSE ESPECÍFICA DO SISTEMA
    }

    /**
     * Faz os cálculos de margem extra casada. Método baseado no AutorizacaoControllerBean.atualizaMargemExtraCasada
     * @param adeIncMargem
     * @param adeVlr
     * @param margemFolha
     * @param margemRestante
     * @param margemUsada
     * @see {@link com.zetra.econsig.service.consignacao.AutorizacaoControllerBean#atualizaMargemExtraCasada}
     */
    protected void atualizaMargemExtraCasada(Short adeIncMargem, BigDecimal adeVlr, Map<Short, BigDecimal> margemFolha, Map<Short, BigDecimal> margemRestante, Map<Short, BigDecimal> margemUsada) {
        // Lista de margens que foram afetadas, e podem desencadear efeitos em outras margens
        final List<Short> marCodigosOrigem = new ArrayList<>();
        marCodigosOrigem.add(adeIncMargem);

        // Navega nos grupos de casamento:
        final List<Short> grupos = CasamentoMargem.getInstance().getGrupos();
        if ((grupos != null) && !grupos.isEmpty()) {
            for (final Short grupo : grupos) {
                final String tipo = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                final List<Short> marCodigosCasamento = CasamentoMargem.getInstance().getMargensCasadas(grupo);

                // 1) Se a margem afetada, desencadeia efeito em uma margem deste grupo:
                final Set<Short> marCodigosAfetados = CasamentoMargem.getInstance().getMargensAfetadas(grupo, marCodigosOrigem);

                for (final Short marCodigoAfetado : marCodigosAfetados) {
                    // Afeta a margem usada do casamento
                    margemUsada.put(marCodigoAfetado, margemUsada.get(marCodigoAfetado).add(adeVlr));

                    // Adiciona o código desta margem na lista de margens usadas afetadas
                    marCodigosOrigem.add(marCodigoAfetado);
                }

                // Recalcula as margens restantes do grupo de casamento, caso tenha alguma margem afetada
                for (int i = 0; i < marCodigosCasamento.size(); i++) {
                    final Short marCodigo = marCodigosCasamento.get(i);

                    switch (tipo) {
					case CasamentoMargem.DIREITA:
						LOG.debug("Calcula grupo " + grupo + " de margem casada pela direita [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
						if (i == 0) {
                            // Se é a margem base: rest = margem - usada
                            margemRestante.put(marCodigo, margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo)));
                        } else {
                            // Se é outra margem, então: rest = min(rest_anterior, margem - usada)
                            margemRestante.put(marCodigo, margemRestante.get(marCodigosCasamento.get(i - 1)).min(margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo))));
                        }
						break;
					case CasamentoMargem.ESQUERDA:
						LOG.debug("Calcula grupo " + grupo + " de margem casada pela esquerda [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
						margemRestante.put(marCodigo, margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo)));
						break;
					case CasamentoMargem.LATERAL:
						LOG.debug("Calcula grupo " + grupo + " de margem casada lateralmente [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
						if (i == 0) {
                            // Se é a margem base: rest = margem - usada
                            margemRestante.put(marCodigo, margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo)));
                        } else {
                            // Se é outra margem, então: rest = min(rest_anterior, margem - usada)
                            margemRestante.put(marCodigo, margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo)).add(margemRestante.get(marCodigosCasamento.get(i - 1)).min(BigDecimal.ZERO)));
                        }
						break;
					case CasamentoMargem.MINIMO:
						LOG.debug("Calcula grupo " + grupo + " de margem casada limitada ao mínimo [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
						if (i > 0) {
                            // Se não é a margem base: então: rest = min(rest_anterior, margem - usada)
                            margemRestante.put(marCodigo, margemRestante.get(marCodigosCasamento.get(i - 1)).min(margemFolha.get(marCodigo).subtract(margemUsada.get(marCodigo))));
                        }
						break;
					case null:
					default:
						break;
					}
                }
            }
        }
    }

    public static class ContratosSemMargem {
        private final List<String> integralmenteSemMargem;
        private final Map<String, BigDecimal> parcialmenteSemMargem;

        public ContratosSemMargem() {
            integralmenteSemMargem = new ArrayList<>();
            parcialmenteSemMargem = new HashMap<>();
        }

        public List<String> getIntegralmenteSemMargem() {
            return integralmenteSemMargem;
        }
        public Map<String, BigDecimal> getParcialmenteSemMargem() {
            return parcialmenteSemMargem;
        }

        public void addContratoSemMargem(String adeCodigo) {
            integralmenteSemMargem.add(adeCodigo);
        }
        public void addContratoParcialmenteSemMargem(String adeCodigo, BigDecimal adeVlrParcial) {
            parcialmenteSemMargem.put(adeCodigo, adeVlrParcial);
        }
    }

    /**
     * Lista os contratos de servidores com margem negativa, deconsiderando as mudanças após o corte, pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * Além disso é incluido os contratos de cartão.
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    protected List<TransferObject> listaContratosSemMargemCandidatosInclusiveCartao(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini, tmp.ade_data_ref, tmp.ade_data, tmp.ade_numero ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 2 and tmp.rse_margem_rest_2 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }
        query.append(" UNION ALL ");
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, x.pse_vlr AS ade_inc_margem, tmp.autoriza_pgt_parcial, tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini, tmp.ade_data_ref, tmp.ade_data, tmp.ade_numero ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join ( ");
        query.append("SELECT DISTINCT tps.pse_vlr, rel.svc_codigo_destino AS svc_codigo ");
        query.append("FROM tb_relacionamento_servico rel ");
        query.append("INNER JOIN tb_param_svc_consignante tps ON (tps.tps_codigo='").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_origem = tps.svc_codigo) ");
        query.append("WHERE tnt_codigo='").append(CodedValues.TNT_CARTAO).append("' ");
        query.append(") AS x ON (x.svc_codigo = tmp.svc_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where tmp.ade_inc_margem = 0 ");
        query.append("AND ((CASE WHEN x.pse_vlr='1' THEN tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00 ");
        query.append(" WHEN x.pse_vlr='2' THEN tmp.rse_margem_rest_2 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00 ");
        query.append(" WHEN x.pse_vlr='3' THEN tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00 ");
        query.append(" WHEN x.pse_vlr not in (1,2,3) THEN (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = x.pse_vlr) < 0.00 ");
        query.append(" ELSE 1=2 END) ");
        query.append("OR ((");
        query.append("select mrs_margem_rest + coalesce((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) ");
        query.append("from tb_margem_registro_servidor mrs ");
        query.append("where mrs.rse_codigo = tmp.rse_codigo ");
        query.append("and mrs.mar_codigo = x.pse_vlr ) < 0.00");
        query.append(")) ");
        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            query.append(" and x.pse_vlr in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }

        query.append("ORDER BY rse_codigo, COALESCE(svc_prioridade, 9999999) + 0 DESC, COALESCE(cnv_prioridade, 9999999) + 0 DESC, COALESCE(ade_ano_mes_ini_ref, ade_ano_mes_ini) DESC, COALESCE(ade_data_ref, ade_data) DESC, ade_numero DESC ");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_data,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    protected List<Short> listaMargensReservaCartao() throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT tps.pse_vlr as mar_codigo ");
        query.append("FROM tb_relacionamento_servico rel ");
        query.append("INNER JOIN tb_param_svc_consignante tps ON (tps.tps_codigo='").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_origem = tps.svc_codigo) ");
        query.append("WHERE tnt_codigo='").append(CodedValues.TNT_CARTAO).append("' ");
        LOG.debug(query.toString());

        List<TransferObject> codigosMargem = new ArrayList<>();
        final List<Short> marCodigos = new ArrayList<>();
        try {
            final String fieldsNames = "mar_codigo";
            codigosMargem = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);

            for (final TransferObject codigoMargem : codigosMargem) {
            	final Short marCodigo = Short.valueOf((String) codigoMargem.getAttribute("mar_codigo"));
            	marCodigos.add(marCodigo);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return marCodigos;
    }

    /**
     * Navega na lista de contratos passada e verifica se este pode
     * ser exportado de acordo com a margem restante do servidor
     * Esse método foi criado para não impactar nos cenários já existentes
     * @param contratos
     * @param permiteDescontoParcial
     * @param verificaParamCsaPgParcial - determina se verifica o parâmetro de CSA que permite ou não pagamento parcial
     * @return
     * @throws ExportaMovimentoException
     */
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalIncluseCartao(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            final ContratosSemMargem adeImpropria = new ContratosSemMargem();
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

            final List<Short> marCodigosCartao = listaMargensReservaCartao();
        	final PeriodoController periodoController = ApplicationContextProvider.getApplicationContext().getBean(PeriodoController.class);
            final TransferObject periodoExportacao = periodoController.obtemPeriodoExportacaoDistinto(null, null, responsavel);
            final java.util.Date periodoDate = (java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO);
            final java.sql.Date periodoAtual = new java.sql.Date(periodoDate.getTime());

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            final Short marCodigo = margemTO.getMarCodigo();
                            if ((marCodigosCartao != null) && !marCodigosCartao.isEmpty() && marCodigosCartao.contains(marCodigo)) {

                            	// Precisamos identificar quais são os reais valores para validar na margem Cartão, pois precisamos considerar o valor da reserva e não do margem rest
                                // pois posso ter reserva maior que a margem atual, negativando os contratos e os mesmos precisam ser enviados no movimento cabendo na margem correta de cartão
                                final List<String> cnvCodigosReserva = new ArrayList<>();
                                final List<String> cnvCodigosLancamentos = new ArrayList<>();
                                final List<TransferObject> lstCnvCodigosCartaoReserva3 = convenioController.ListaConveniosIncMargemCartaoReservaLancamento(marCodigo, true, responsavel);
                                final List<TransferObject> lstCnvCodigosCartaoLancamento = convenioController.ListaConveniosIncMargemCartaoReservaLancamento(marCodigo, false, responsavel);

                                for (final TransferObject cnv : lstCnvCodigosCartaoReserva3) {
                                    cnvCodigosReserva.add((String) cnv.getAttribute(Columns.CNV_CODIGO));
                                }

                                for (final TransferObject cnv : lstCnvCodigosCartaoLancamento) {
                                    cnvCodigosLancamentos.add((String) cnv.getAttribute(Columns.CNV_CODIGO));
                                }

                                final BigDecimal totalReservasCartao = pesquisarConsignacaoController.ObtemTotalValorConsignacaoPorRseCnv(rseCodigo, cnvCodigosReserva, null, responsavel);
                                final BigDecimal totalLancamentosCartao = pesquisarConsignacaoController.ObtemTotalValorConsignacaoPorRseCnv(rseCodigo, cnvCodigosLancamentos, periodoAtual, responsavel);

                                if (!TextHelper.isNull(totalReservasCartao) && (totalReservasCartao.compareTo(BigDecimal.ZERO) > 0)
                                && !TextHelper.isNull(totalLancamentosCartao) && (totalLancamentosCartao.compareTo(BigDecimal.ZERO) > 0)) {
                                    margemFolha.put(marCodigo, totalReservasCartao.add(margemTO.getMrsMargemRest()));
                                    margemUsada.put(marCodigo, totalLancamentosCartao);
                                    margemRestante.put(marCodigo, totalReservasCartao.add(margemTO.getMrsMargemRest()).subtract(totalLancamentosCartao));
                                } else {
                                    margemFolha.put(marCodigo, margemTO.getMrsMargem());
                                    margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                                    margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                                }
                            } else {
                                margemFolha.put(marCodigo, margemTO.getMrsMargem());
                                margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                                margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            }
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestante.get(adeIncMargem).signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (margem1CasadaMargem3Esq || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            // Contrato originalmente incidindo na margem 3, ao ser retirado, com casamento de margem
                            // deve ser adicionado às margens 1 e 2, de acordo com o tipo de casamento.
                            if (margem1CasadaMargem3 || margem1CasadaMargem3Esq || margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123Casadas || margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }
                        } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)
                        		&& ((marCodigosCartao == null) || marCodigosCartao.isEmpty()
                            		|| ((marCodigosCartao != null) && !marCodigosCartao.isEmpty() && !marCodigosCartao.contains(adeIncMargem)))) {
                            // Passa o valor negativo, pois está sendo somado à margem
                    		atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
                        }

                        if (margem1CasadaMargem3 || margem1CasadaMargem3Esq) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                        } else if (margem123Casadas || margem123CasadasEsq) {
                            // Realiza o acerto da margem, de acordo com o real restante e o limite das superiores
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2))));
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
                        } else if (margem1CasadaMargem3Lateral) {
                            margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3)).add(margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(BigDecimal.ZERO)));
                        }

                        if (!permiteDescontoParcial || (margemRestante.get(adeIncMargem).signum() <= 0)) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else if (!verificaParamCsaPgParcial || (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && CodedValues.TPA_SIM.equals(contrato.getAttribute("autoriza_pgt_parcial")))) {
						    // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
						    // na lista daqueles que podem pagar parcialmente com o valor restante de margem
						    adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
						} else {
						    // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
						    // lista dos contratos a serem removidos do movimento
						    adeImpropria.addContratoSemMargem(adeCodigo);
						}
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException | PeriodoException | ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }
}
