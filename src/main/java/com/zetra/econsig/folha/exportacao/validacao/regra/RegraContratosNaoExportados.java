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
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegraContratosNaoExportados</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação dos contratos não exportados.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraContratosNaoExportados extends Regra {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraContratosNaoExportados.class);

    protected boolean consolidaDescontos = false;
    protected boolean exportaMensal = false;
    protected boolean temProcessamentoFerias = false;
    protected boolean enviaContratoPagoFerias = false;

    private List<String> camposChave;

    public RegraContratosNaoExportados() {
        try {
            camposChave = getAmvCamposPreenchidos();
        } catch (DataAccessException ex) {
            camposChave = new ArrayList<>();
            ex.printStackTrace();
        }
    }

    @Override
    public void criarTabelasValidacao() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final StringBuilder query = new StringBuilder();
            query.append("create table if not exists tb_tmp_movimento_validacao_1 (rse_codigo varchar(32), ");
            query.append(" est_codigo varchar(32), est_identificador varchar(40),");
            query.append(" org_codigo varchar(32), org_identificador varchar(40),");
            query.append(" csa_codigo varchar(32), csa_identificador varchar(40),");
            query.append(" svc_codigo varchar(32), svc_identificador varchar(40),");
            query.append(" cnv_codigo varchar(32), cnv_cod_verba varchar(32),");
            query.append(" ser_nome varchar(100), ser_cpf varchar(19), rse_matricula varchar(20),");
            query.append(" ade_indice varchar(32), ade_prazo int, ade_data_ref datetime, ade_tipo_vlr varchar(1),");
            query.append(" ade_vlr_folha decimal(13,2), ade_ano_mes_ini date, ade_ano_mes_fim date, ade_cod_reg varchar(1),");
            query.append(" ade_vlr decimal(13,2), ade_numero int, consolida_desconto varchar(1)");
            query.append(" )");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table if not exists tb_tmp_movimento_validacao (");
            query.append(" est_codigo varchar(32),");
            query.append(" est_identificador varchar(40),");
            query.append(" org_codigo varchar(32),");
            query.append(" org_identificador varchar(40),");
            query.append(" csa_codigo varchar(32),");
            query.append("  csa_identificador varchar(40),");
            query.append(" svc_codigo varchar(32),");
            query.append(" svc_identificador varchar(40),");
            query.append(" cnv_codigo varchar(32),");
            query.append(" cnv_cod_verba varchar(32),");
            query.append(" ser_nome varchar(100),");
            query.append(" ser_cpf varchar(19),");
            query.append(" rse_matricula varchar(20),");
            query.append(" ade_indice varchar(32),");
            query.append(" ade_prazo int,");
            query.append(" ade_data_ref datetime,");
            query.append(" ade_tipo_vlr varchar(1),");
            query.append(" ade_vlr_folha decimal(13,2),");
            query.append(" ade_ano_mes_ini date,");
            query.append(" ade_ano_mes_fim date,");
            query.append(" ade_cod_reg varchar(1),");
            query.append(" ade_vlr decimal(13,2),");
            query.append(" ade_numero int,");
            query.append(" KEY ix01 (rse_matricula, cnv_cod_verba));");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {

        // Carrega parâmetros
        consolidaDescontos = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        exportaMensal = !ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        temProcessamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        enviaContratoPagoFerias = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATOS_PAGOS_FERIAS_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // Define os códigos da regra atual.
        super.rvaCodigo = rva.getRvaCodigo();
        super.rvmCodigo = regra.getRvmCodigo();
        super.estCodigos = estCodigos;
        super.orgCodigos = orgCodigos;

        super.periodo = DateHelper.format(rva.getRvaPeriodo(), "yyyy-MM-dd");

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
        long qtdBase = buscaQtdRegistrosArquivo();

        rrvValorEncontrado.append("Contratos não exportados ").append(qtdErros).append("/").append(qtdBase);
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
    protected void criaTabelaTemporaria() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Exclui a tabela anterior
        try {
            jdbc.update("delete from tb_tmp_movimento_validacao_1", queryParams);
            jdbc.update("delete from tb_tmp_movimento_validacao", queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível limpar as tabelas tb_tmp_movimento_validacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }

        // preenche dados das parcelas em processamento na tabela temporária de validação
        preencheTabelaTemporaria(false);
        // Se exporta mensal, tem processamento de retorno de férias e deve enviar contratos pagos
        // no retorno de férias no movimento do mês, então inclui estes registros na tabela temporária de validação
        if (exportaMensal && temProcessamentoFerias && enviaContratoPagoFerias) {
            preencheTabelaTemporaria(true);
        }

        // Consolida descontos dos contratos marcados para consolidar
        StringBuilder query = new StringBuilder();
        query.append("insert into tb_tmp_movimento_validacao ");
        query.append(" select est_codigo, est_identificador, org_codigo, org_identificador,");
        query.append(" csa_codigo, csa_identificador,");
        query.append(" svc_codigo, svc_identificador,");
        query.append(" cnv_codigo, cnv_cod_verba,");
        query.append(" ser_nome, ser_cpf, rse_matricula,");
        query.append(" ade_indice, ade_prazo, ade_data_ref, ade_tipo_vlr, ade_vlr_folha, ade_ano_mes_ini, ade_ano_mes_fim, ade_cod_reg,");
        query.append(" ade_vlr, ade_numero");
        query.append(" from tb_tmp_movimento_validacao_1");
        query.append(" where consolida_desconto = 'N' ");
        query.append(" union ");
        query.append(" select est_codigo, est_identificador, org_codigo, org_identificador,");
        query.append(" csa_codigo, csa_identificador,");
        query.append(" svc_codigo, svc_identificador,");
        query.append(" cnv_codigo, cnv_cod_verba,");
        query.append(" ser_nome, ser_cpf, rse_matricula,");
        query.append(" group_concat(distinct ade_indice) as ade_indice, max(ade_prazo) as ade_prazo, max(ade_data_ref) as ade_data_ref,");
        query.append(" group_concat(distinct ade_tipo_vlr) as ade_tipo_vlr, sum(ade_vlr_folha) as ade_vlr_folha,");
        query.append(" max(ade_ano_mes_ini) as ade_ano_mes_ini, max(ade_ano_mes_fim) as ade_ano_mes_fim, group_concat(distinct ade_cod_reg) as ade_cod_reg,");
        query.append(" sum(ade_vlr) as ade_vlr, max(ade_numero) as ade_numero");
        query.append(" from tb_tmp_movimento_validacao_1");
        query.append(" where consolida_desconto = 'S' ");
        query.append(" group by rse_codigo, csa_codigo, cnv_cod_verba");

        LOG.debug(query.toString());

        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível preencher a tabela tb_tmp_movimento_validacao : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }
    }

    /**
     * Preenche a tabela temporária com os contratos que seriam exportados.
     * @return
     */
    protected void preencheTabelaTemporaria(boolean enviaContratosPagosFerias) throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        List<String> sadCodigosAtivos = new ArrayList<>();
        sadCodigosAtivos.add(CodedValues.SAD_DEFERIDA);
        sadCodigosAtivos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigosAtivos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigosAtivos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        List<String> sadCodigosInativos = new ArrayList<>();
        sadCodigosInativos.add(CodedValues.SAD_CANCELADA);
        sadCodigosInativos.add(CodedValues.SAD_LIQUIDADA);

        // Cria e Preenche novamente
        StringBuilder query = new StringBuilder();
        query.append(" insert into tb_tmp_movimento_validacao_1 (rse_codigo, est_codigo, est_identificador, org_codigo, org_identificador, ");
        query.append(" csa_codigo, csa_identificador, svc_codigo, svc_identificador,");
        query.append(" cnv_codigo, cnv_cod_verba, ser_nome, ser_cpf, rse_matricula,");
        query.append(" ade_indice, ade_prazo, ade_data_ref, ade_tipo_vlr, ade_vlr_folha, ade_ano_mes_ini, ade_ano_mes_fim, ade_cod_reg,");
        query.append(" ade_vlr, ade_numero, consolida_desconto)");
        query.append(" select rse.rse_codigo, est.est_codigo, est.est_identificador, org.org_codigo, org.org_identificador,");
        query.append(" csa.csa_codigo, csa.csa_identificador, svc.svc_codigo, svc.svc_identificador,");
        query.append(" cnv.cnv_codigo, cnv.cnv_cod_verba, ser.ser_nome, ser.ser_cpf, rse.rse_matricula,");
        query.append(" ade.ade_indice, ade.ade_prazo, ade.ade_data_ref, ade.ade_tipo_vlr, ade.ade_vlr_folha, ade.ade_ano_mes_ini,");
        query.append(" ade.ade_ano_mes_fim, ade.ade_cod_reg, prd.prd_vlr_previsto as ade_vlr, ade.ade_numero,");
        query.append(" coalesce(coalesce(cnv.cnv_consolida_descontos, psi19.psi_vlr), 'N') as consolida_desconto");
        if (!enviaContratosPagosFerias) {
            query.append(" from tb_parcela_desconto_periodo prd USE INDEX ()");
        } else {
            query.append(" from tb_parcela_desconto prd ");
        }
        query.append(" inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo)");
        query.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        query.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
        query.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)");
        query.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
        query.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        query.append(" inner join tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");
        query.append(" inner join tb_orgao org on (org.org_codigo = rse.org_codigo)");
        query.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
        query.append(" inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo)");
        // -- Envia Servidor Excluido
        query.append(" left outer join tb_param_sist_consignante psi146 on (psi146.tpc_codigo = '" + CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO + "')");
        //-- Limite Valor Minimo Sistema
        query.append(" left outer join tb_param_sist_consignante psi101 on (psi101.tpc_codigo = '" + CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO + "')");
        query.append(" left outer join tb_param_sist_consignante psi184 on (psi184.tpc_codigo = '" + CodedValues.TPC_EXPORTA_ADE_MENORES_MINIMO_SVC + "')");
        // recupera "consolida desconto" do convênio ou do parâmetro de sistema
        query.append(" left outer join tb_param_sist_consignante psi19 on (psi19.tpc_codigo = '" + CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO + "')");
        // -- Limite Minimo Servico
        query.append(" left outer join tb_param_svc_consignante pse118 on (cnv.svc_codigo = pse118.svc_codigo and pse118.tps_codigo = '" + CodedValues.TPS_VLR_MINIMO_CONTRATO + "')");
        query.append(" left outer join tb_param_svc_consignataria psc118 on (cnv.svc_codigo = psc118.svc_codigo and psc118.tps_codigo = '" + CodedValues.TPS_VLR_MINIMO_CONTRATO + "')");
        query.append(" left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo in ('" + CodedValues.TOC_TARIF_LIQUIDACAO + "','" + CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO + "') and oca.oca_periodo > pex.pex_periodo)");
        query.append(" where (ade.sad_codigo in ('").append(TextHelper.join(sadCodigosAtivos, "','")).append("') or (ade.sad_codigo in ('").append(TextHelper.join(sadCodigosInativos, "','")).append("') and oca.oca_codigo is not null))");
        if (!enviaContratosPagosFerias) {
            query.append(" and prd.spd_codigo = '" + CodedValues.SPD_EMPROCESSAMENTO + "' ");
            query.append(" and ade.ade_ano_mes_ini <= pex.pex_periodo");
            query.append(" and ade.ade_data < pex.pex_data_fim");
        } else {
            query.append(" and prd.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' ");
            query.append(" and prd.prd_data_desconto = pex.pex_periodo");
        }
        query.append(" and ade.ade_int_folha = " + CodedValues.INTEGRA_FOLHA_SIM + " ");
        query.append(" and (ifnull(psi184.psi_vlr, 'S') = 'S' or ade.ade_vlr >= ifnull(nullif(replace(coalesce(psc118.psc_vlr, pse118.pse_vlr), ',', '.'), ''), ifnull(nullif(psi101.psi_vlr, '') , 0)) + 0)");
        query.append(" and (ifnull(ade.ade_prazo, 9999999) > ifnull(ade.ade_prd_pagas, 0) or (ade.ade_vlr_sdo_ret IS NOT NULL AND ade.ade_vlr_sdo_ret > 0))");
        query.append(" and ((ifnull(psi146.psi_vlr, 'S') = 'N' and rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')) or (ifnull(psi146.psi_vlr, 'S') = 'S'))");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND est.est_codigo IN (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND org.org_codigo IN (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.debug(query.toString());

        try {
            jdbc.update(query.toString(), queryParams);
        } catch (DataAccessException ex) {
            String msg = "Não foi possível preencher a tabela tb_tmp_movimento_validacao_1 : " + ex.getMessage();
            LOG.error(msg, ex);
            throw ZetraException.byMessage(msg, ex);
        }
    }

    /**
     * Busca contratos que não foram exportados, mas deveriam ter sido.
     * @return
     */
    private List<TransferObject> buscaContratosNaoExportados() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            // VERIFICA SE TEM ALGUM CONTRATO QUE NAO FOI EXPORTADO
            query.append("SELECT tmp.cnv_cod_verba, tmp.rse_matricula, tmp.ade_vlr, cast(tmp.ade_numero AS char) as ade_numero");
            query.append(" FROM tb_tmp_movimento_validacao tmp");
            query.append(" LEFT OUTER JOIN tb_arquivo_movimento_validacao amv ON (amv.rse_matricula = tmp.rse_matricula");
            for (String campo : camposChave) {
                if (campo.equalsIgnoreCase("ade_vlr")) {
                    query.append(" AND (amv.").append(campo).append(" <=> tmp.").append(campo);
                    if (!camposChave.contains("ade_tipo_vlr")) {
                        query.append(" OR (tmp.ade_tipo_vlr = 'P' AND amv.ade_tipo_vlr IS NULL)");
                    }
                    query.append(")");
                } else {
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
