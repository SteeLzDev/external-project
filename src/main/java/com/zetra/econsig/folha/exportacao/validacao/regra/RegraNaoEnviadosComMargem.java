package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.ArrayList;
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
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegraNaoEnviadosComMargem</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se
 *    existe algum contrato que não foi enviado mesmo a matrícula tendo margem.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraNaoEnviadosComMargem extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraNaoEnviadosComMargem.class);

    /** Define se adiciona cláusulas dos parâmetros de reimplante: a regra RegraNaoEnviadosComMargemSemReimplante irá desabilitar esta opção **/
    protected boolean verificaReimplante = true;
    /** Define se adiciona cláusula para validação do convênio ativo: a regra RegraNaoEnviadosComMargemCnvAtivo irá habilitar esta opção **/
    protected boolean verificaCnvAtivo = false;

    /** Parâmetros de sistema necessários para validação da regra */
    private final boolean exportacaoInicial;
    private final boolean consolidaMovFin;
    private String defaultReimplante;
    private String defaultPreservacao;

    private List<String> camposChave;

    public RegraNaoEnviadosComMargem() {
        exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exportação somente inicial? " + exportacaoInicial);

        consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Consolida descontos para integração com a folha? " + consolidaMovFin);

        defaultReimplante = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, AcessoSistema.getAcessoUsuarioSistema());
        defaultReimplante = (defaultReimplante == null) ? CodedValues.TPC_NAO : defaultReimplante;
        LOG.debug("Padrão para parâmetro de serviço de reimplantação de contratos: " + defaultReimplante);

        defaultPreservacao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, AcessoSistema.getAcessoUsuarioSistema());
        defaultPreservacao = (defaultPreservacao == null) ? CodedValues.TPC_NAO : defaultPreservacao;
        LOG.debug("Padrão para parâmetro de serviço de preservação de parcelas rejeitadas:  " + defaultPreservacao);
        try {
            camposChave = getAmvCamposPreenchidos();
        } catch (DataAccessException ex) {
            camposChave = new ArrayList<>();
            ex.printStackTrace();
        }
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        if (!exportacaoInicial) {
            LOG.warn("A regra de validação de movimento 'RegraNaoEnviadosComMargem' somente se aplica a sistemas de movimento inicial.");
            return;
        }

        // Define os códigos da regra atual.
        rvaCodigo = rva.getRvaCodigo();
        rvmCodigo = regra.getRvmCodigo();
        this.estCodigos = estCodigos;
        this.orgCodigos = orgCodigos;

        periodo = DateHelper.format(rva.getRvaPeriodo(), "yyyy-MM-dd");

        resultado = new ResultadoRegraValidacaoMovimentoTO(rvaCodigo, regra.getRvmCodigo());

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

        String rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK;
        StringBuilder rrvValorEncontrado = new StringBuilder();

        List<TransferObject> erros = buscaContratosNaoExportados();
        int qtdErros = erros.size();
        long qtdBase = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_INCLUSAO)
                     + buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_ALTERACAO);

        rrvValorEncontrado.append("Contratos não exportados com margem ").append(qtdErros).append("/").append(qtdBase);
        // Se for menor que zero é porque deu erro na busca do valor
        if (qtdBase < 0) {
            rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
        } else {
            long diferencaPercentual;
            if (qtdBase > 0) {
                diferencaPercentual = Math.round(100 * ((double) qtdErros / qtdBase));
            } else {
                diferencaPercentual = 0;
            }
            int limiteErro = regra.getRvmLimiteErro() != null ? Integer.parseInt(regra.getRvmLimiteErro()) : Integer.MAX_VALUE;
            int limiteAviso = regra.getRvmLimiteAviso() != null ? Integer.parseInt(regra.getRvmLimiteAviso()) : Integer.MAX_VALUE;

            if (diferencaPercentual > limiteErro) {
                rrvValorEncontrado.append(" **");
                rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
            } else if (diferencaPercentual > limiteAviso) {
                rrvValorEncontrado.append(" *");
                if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(rrvResultado)) {
                    rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                }
            }
            if (qtdErros > 0) {
                rrvValorEncontrado.append(getListaContratosComProblema(true, erros));
            }
        }
        rrvValorEncontrado.append("<br>");
        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    /**
     * Cria uma tabela temporária com os contratos que seriam exportados.
     * @return
     */
    private void criaTabelaTemporaria() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("defaultReimplante", defaultReimplante);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        // Exclui a tabela anterior
        try {
            jdbc.update("drop temporary table if exists tb_tmp_inclusao_movimento_validacao", queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível excluir tabela tb_tmp_inclusao_movimento_validacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        // Cria e Preenche novamente
        StringBuilder query = new StringBuilder();
        query.append("create temporary table tb_tmp_inclusao_movimento_validacao (");
        query.append("  est_codigo varchar(32),");
        query.append("  est_identificador varchar(40),");
        query.append("  org_codigo varchar(32),");
        query.append("  org_identificador varchar(40),");
        query.append("  csa_codigo varchar(32),");
        query.append("  csa_identificador varchar(40),");
        query.append("  svc_codigo varchar(32),");
        query.append("  svc_identificador varchar(40),");
        query.append("  cnv_codigo varchar(32),");
        query.append("  cnv_cod_verba varchar(32),");
        query.append("  ser_nome varchar(100),");
        query.append("  ser_cpf varchar(19),");
        query.append("  rse_matricula varchar(20),");
        query.append("  ade_indice varchar(32),");
        query.append("  ade_prazo int,");
        query.append("  ade_data_ref datetime,");
        query.append("  ade_tipo_vlr varchar(1),");
        query.append("  ade_vlr_folha decimal(13,2),");
        query.append("  ade_ano_mes_ini date,");
        query.append("  ade_ano_mes_fim date,");
        query.append("  ade_cod_reg varchar(1),");
        query.append("  ade_vlr decimal(13,2),");
        query.append("  ade_numero int,");
        query.append("  KEY ix01 (rse_matricula, cnv_cod_verba))");
        query.append(" select est.est_codigo, est.est_identificador, org.org_codigo, org.org_identificador,");
        query.append(" csa.csa_codigo, csa.csa_identificador,");
        query.append(" svc.svc_codigo, svc.svc_identificador,");
        query.append(" cnv.cnv_codigo, cnv.cnv_cod_verba,");
        query.append(" ser.ser_nome, ser.ser_cpf, rse.rse_matricula,");
        if (consolidaMovFin) {
            query.append(" group_concat(distinct ade.ade_indice) as ade_indice, max(ade.ade_prazo) AS ade_prazo, max(ade.ade_data_ref) as ade_data_ref,");
            query.append(" group_concat(distinct ade.ade_tipo_vlr) as ade_tipo_vlr, sum(ade.ade_vlr_folha) as ade_vlr_folha,");
            query.append(" max(ade.ade_ano_mes_ini) as ade_ano_mes_ini, max(ade.ade_ano_mes_fim) as ade_ano_mes_fim, group_concat(distinct ade.ade_cod_reg) as ade_cod_reg,");
            query.append(" sum(ade.ade_vlr) as ade_vlr, max(ade.ade_numero) as ade_numero");
        } else {
            query.append(" ade.ade_indice, ade.ade_prazo, ade.ade_data_ref, ade.ade_tipo_vlr, ade.ade_vlr_folha, ade.ade_ano_mes_ini, ade.ade_ano_mes_fim, ade.ade_cod_reg,");
            query.append(" ade.ade_vlr, ade.ade_numero");
        }
        query.append(" from tb_aut_desconto ade ");
        query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        query.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        query.append("inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) ");
        query.append("inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo) ");
        query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) ");
        query.append("inner join tb_servidor ser on (ser.ser_codigo = rse.ser_codigo) ");
        query.append("left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') ");
        query.append("left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') ");
        query.append("left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') ");
        query.append("left outer join tb_param_svc_consignataria psc35 on (cnv.svc_codigo = psc35.svc_codigo and cnv.csa_codigo = psc35.csa_codigo and psc35.tps_codigo = '" + CodedValues.TPS_REIMPLANTACAO_AUTOMATICA + "' and coalesce(psc35.psc_ativo, '1') = '1') ");
        query.append("left outer join tb_param_svc_consignataria psc36 on (cnv.svc_codigo = psc36.svc_codigo and cnv.csa_codigo = psc36.csa_codigo and psc36.tps_codigo = '" + CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL + "' and coalesce(psc36.psc_ativo, '1') = '1') ");
        query.append("left outer join tb_ocorrencia_autorizacao oca10 on (ade.ade_codigo = oca10.ade_codigo and oca10.toc_codigo = '" + CodedValues.TOC_RELANCAMENTO + "' and oca10.oca_periodo = pex.pex_periodo) ");
        query.append("left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '" + CodedValues.TOC_ALTERACAO_CONTRATO + "' and oca14.oca_periodo = pex.pex_periodo) ");
        query.append("left outer join tb_ocorrencia_autorizacao oca4 on (ade.ade_codigo = oca4.ade_codigo and oca4.toc_codigo = '" + CodedValues.TOC_TARIF_RESERVA + "' and oca4.oca_periodo = pex.pex_periodo) ");
        query.append("where ");
        query.append("((ade.sad_codigo in ('" + CodedValues.SAD_ESTOQUE + "','" + CodedValues.SAD_ESTOQUE_MENSAL + "')) or ");
        query.append(" (ade.sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "','" + CodedValues.SAD_EMANDAMENTO + "','" + CodedValues.SAD_AGUARD_LIQUIDACAO + "','" + CodedValues.SAD_AGUARD_LIQUI_COMPRA + "') and ade.ade_vlr_folha is null and ade.ade_ano_mes_ini <= pex.pex_periodo) or ");
        query.append(" (ade.sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and ade.ade_vlr_folha is not null and ade.ade_vlr_folha <> ade.ade_vlr and oca14.oca_codigo is null)) ");
        query.append("and (ade.ade_int_folha = '1') ");
        query.append("and (ade.ade_ano_mes_ini <= pex.pex_periodo) ");
        query.append("and (coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0)) ");

        if (verificaReimplante) {
            query.append("and (coalesce(psc35.psc_vlr, :defaultReimplante) = 'S' or oca10.oca_codigo is not null or oca14.oca_codigo is not null or oca4.oca_codigo is not null) ");

        } else {
            /*
             * Apesar dos parâmetros de reimplante estarem habilitados, existem sistemas que não possuem
             * script de reimplante, porque a própria folha controla os reimplantes. Desta forma, os
             * contratos não pagos que não tem ocorrências no período não devem ser exportados.
             * A regra RegraNaoEnviadosComMargemSemReimplante, que estende a regra atual, deve ser
             * utilizada nestes casos.
             */
            query.append("and (oca10.oca_codigo is not null or oca14.oca_codigo is not null or oca4.oca_codigo is not null) ");
        }

        query.append("and ((coalesce(psc36.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= pex.pex_periodo or ade.ade_ano_mes_fim is null)) or coalesce(psc36.psc_vlr, :defaultPreservacao) = 'S') ");
        query.append("and (rse.srs_codigo = '" + CodedValues.SRS_ATIVO + "') ");
        query.append("and pcr.pcr_vlr is null ");
        query.append("and psr.psr_vlr is null ");
        query.append("and pnr.pnr_vlr is null ");

        if (verificaCnvAtivo) {
            query.append("and cnv.scv_codigo = '" + CodedValues.SCV_ATIVO + "' ");
        }

        query.append("and ((ade.ade_inc_margem = 1 and rse.rse_margem_rest   >= 0) or ");
        query.append("     (ade.ade_inc_margem = 2 and rse.rse_margem_rest_2 >= 0) or ");
        query.append("     (ade.ade_inc_margem = 3 and rse.rse_margem_rest_3 >= 0)) ");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("and org.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND org.org_codigo in (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        if (consolidaMovFin) {
            query.append(" group by rse.rse_codigo, csa.csa_codigo, cnv.cnv_cod_verba");
        }

        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível preencher a tabela tb_tmp_inclusao_movimento_validacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }
    }

    /**
     * Busca contratos que não foram exportados, mas deveriam ter sido pois o servidor tem margem.
     * @return
     */
    private List<TransferObject> buscaContratosNaoExportados() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();

            // VERIFICA SE TEM ALGUM CONTRATO QUE NAO FOI EXPORTADO
            query.append("SELECT tmp.cnv_cod_verba, tmp.rse_matricula, tmp.ade_vlr, cast(tmp.ade_numero AS char) as ade_numero");
            query.append(" FROM tb_tmp_inclusao_movimento_validacao tmp");
            query.append(" LEFT OUTER JOIN tb_arquivo_movimento_validacao amv ON (amv.amv_operacao in ('I', 'A') and amv.rse_matricula = tmp.rse_matricula");
            for (String campo : camposChave) {
                if (campo.equalsIgnoreCase("ade_vlr")) {
                    query.append(" AND (amv.").append(campo).append(" <=> tmp.").append(campo);
                    if (!camposChave.contains("ade_tipo_vlr")) {
                        query.append(" OR (tmp.ade_tipo_vlr = 'P' AND amv.ade_tipo_vlr IS NULL)");
                    }
                    query.append(")");
                } else if (!campo.equalsIgnoreCase("rse_matricula")) {
                    query.append(" AND amv.").append(campo).append(" = tmp.").append(campo);
                }
            }
            query.append(")");
            query.append(" WHERE amv.rse_matricula IS NULL");

            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND tmp.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND tmp.org_codigo IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            LOG.debug(query.toString());

            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "cnv_cod_verba,rse_matricula,ade_vlr,ade_numero", MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
