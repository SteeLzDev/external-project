package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: STJ</p>
 * <p>Description: Implementações específicas para STJ - Superior Tribunal de Justiça.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class STJ extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(STJ.class);

//  private static final String DATA_INI_MARGEM_5_ADICIONAL = "2021-04-24 00:00:00";
//  private static final String DATA_FIM_MARGEM_5_ADICIONAL = "2021-12-31 23:59:59";

//  private static final Short MARGEM_70               = Short.valueOf("100");
    private static final Short MARGEM_35_COM_ADICIONAL = Short.valueOf("101");
    private static final Short MARGEM_30_COM_ADICIONAL = Short.valueOf("102");
    private static final Short MARGEM_35_SEM_ADICIONAL = Short.valueOf("103");
    private static final Short MARGEM_30_SEM_ADICIONAL = Short.valueOf("104");

    @Override
    protected void excluirContratos(List<String> adeCodigos) throws DataAccessException {
        if (adeCodigos != null && !adeCodigos.isEmpty()) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.exportacao.removendo.contratos.sem.margem", (AcessoSistema)null));

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("delete from tb_tmp_exportacao ");
            query.append("where ade_codigo in (:adeCodigos) ");
            // inclusão de filtro para não remover contratos da csa 012 com ade_ano_mes_ini menor ou igual a 01/07/2016
            query.append("and !(csa_identificador = '012' and ade_ano_mes_ini <= '2016-07-01') ");
            queryParams.addValue("adeCodigos", adeCodigos);
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Lista os contratos de servidores com margem negativa, deconsiderando as mudanças após o corte, pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * @param stat
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        // ADEs feita entre "24/04/2021 00:00:00" e "31/12/2021 23:59:59" devem ser validadas na margem
        // com o adicional de 5%, ou seja 101 e 102. Os demais, sem o adicional, ou seja, 103 e 104.
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_data, tmp.ade_vlr, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial  ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where tmp.ade_inc_margem not in ('0','1','2','3') ");
        query.append("and (");
        query.append("select mrs_margem_rest + coalesce((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) ");
        query.append("from tb_margem_registro_servidor mrs ");
        query.append("where mrs.rse_codigo = tmp.rse_codigo ");
        query.append("and mrs.mar_codigo = tmp.ade_inc_margem ");
        query.append(") < 0.00 ");
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_data,ade_vlr,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
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
    @Override
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            ContratosSemMargem adeImpropria = new ContratosSemMargem();
            ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

            if (contratos != null && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr = null;
                Date adeData = null;

                List<String> adeRemovidas = new ArrayList<>();
                Map<Short, BigDecimal> margemFolha = new HashMap<>();
                Map<Short, BigDecimal> margemRestante = new HashMap<>();
                Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                Iterator<TransferObject> it = contratos.iterator();
                while (it.hasNext()) {
                    TransferObject contrato = it.next();
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();
                        adeRemovidas.clear();

                        List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (MargemTO margemTO : margens) {
                            Short marCodigo = margemTO.getMarCodigo();
                            margemFolha.put(marCodigo, margemTO.getMrsMargem());
                            margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                            margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            BigDecimal margemUsadaAposCorte = calcularMargemUsadaPosCorte(rseCodigo, marCodigo, responsavel);
                            margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaAposCorte));
                            margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaAposCorte));
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    if (contrato.getAttribute("ade_data") instanceof LocalDateTime) {
                        adeData = Date.from(((LocalDateTime) contrato.getAttribute("ade_data")).atZone(ZoneId.systemDefault()).toInstant());
                    } else if (contrato.getAttribute("ade_data") instanceof Date) {
                        adeData = (Date) contrato.getAttribute("ade_data");
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    // Se o contrato não estiver na janela permitida para uso da margem adicional de 5%, validar
                    // com a margem antiga, sem o adicional
                    BigDecimal margemRestExportacao = margemRestante.get(adeIncMargem);

                    if (adeIncMargem.equals(MARGEM_35_SEM_ADICIONAL) || adeIncMargem.equals(MARGEM_30_SEM_ADICIONAL)) {
                        Short adeIncMargemAdicional = adeIncMargem.equals(MARGEM_35_SEM_ADICIONAL) ? MARGEM_35_COM_ADICIONAL : MARGEM_30_COM_ADICIONAL;
                        BigDecimal margemComAdicional = margemRestante.get(adeIncMargemAdicional);
                        // Se a margem sem adicional está negativa e a margem com adicional está positiva, verifica
                        // se existem consignações incidindo na margem com adicional, mais novas que este contrato,
                        // que tornaram a margem sem adicional negativa. Caso tenha, reverte estes valores para a margem sem adiconal
                        BigDecimal contratosIncMargemAdicional = BigDecimal.ZERO;
                        if (margemRestExportacao.signum() < 0 && margemComAdicional.signum() >= 0) {
                            contratosIncMargemAdicional = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, adeData, null, adeIncMargemAdicional, adeRemovidas, responsavel);
                        }
                        margemRestExportacao = margemRestExportacao.add(contratosIncMargemAdicional);
                    }

                    if (margemRestExportacao.signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        // Passa o valor negativo, pois está sendo somado à margem
                        atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);

                        // Se não permite desconto parcial inclui o contrato na lista daqueles que não há margem para envio integral
                        adeImpropria.addContratoSemMargem(adeCodigo);
                        adeRemovidas.add(adeCodigo);
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("STJ.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(false, null);
        LOG.debug("fim - STJ.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
