package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: GovRJ3</p>
 * <p>Description: Implementações específicas para GovRJ - Governo do Estado do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GovRJ4 extends GovRJ3 {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GovRJ4.class);

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
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        StringBuilder query = new StringBuilder();
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
                Date adeData = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

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
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());
                    final Object adeDataObj = contrato.getAttribute("ade_data");
                    adeData = DateHelper.objectToDate(adeDataObj);

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
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    BigDecimal margemRestExportacao = margemRestante.get(adeIncMargem);
                    List<Short> marCodigosComAdicional = new ArrayList<>();

                    if(adeIncMargem.equals(MARGEM_CARTAO_35)) {
                        marCodigosComAdicional.add(MARGEM_CARTAO_40);
                        marCodigosComAdicional.add(MARGEM_CARTAO_50);
                    } else if(adeIncMargem.equals(MARGEM_CONSIGNACAO_30)) {
                        marCodigosComAdicional.add(MARGEM_CONSIGNACAO_35);
                        marCodigosComAdicional.add(MARGEM_CONSIGNACAO_40);
                    }

                    for (Short marCodigoAdicional : marCodigosComAdicional) {
                        BigDecimal margemComAdicional = margemRestante.get(marCodigoAdicional);
                        // Se a margem sem adicional está negativa e a margem com adicional está positiva, verifica
                        // se existem consignações incidindo na margem com adicional, mais novas que este contrato,
                        // que tornaram a margem sem adicional negativa. Caso tenha, reverte estes valores para a margem sem adiconal
                        BigDecimal contratosIncMargemAdicional = BigDecimal.ZERO;
                        if (margemRestExportacao.signum() < 0 && margemComAdicional.signum() >= 0) {
                            contratosIncMargemAdicional = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, adeData, null, marCodigoAdicional, adeRemovidas, responsavel);
                        }
                        margemRestExportacao = margemRestExportacao.add(contratosIncMargemAdicional);
                    }

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestExportacao != null && margemRestExportacao.signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        // Passa o valor negativo, pois está sendo somado à margem
                        atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);

                        // Obtém novamente a margem restante do qual a ADE deve validar
                        margemRestExportacao = margemRestante.get(adeIncMargem);

                        if (!permiteDescontoParcial || margemRestExportacao.signum() <= 0) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                            adeRemovidas.add(adeCodigo);
                        } else if (verificaParamCsaPgParcial) {
                            if (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && contrato.getAttribute("autoriza_pgt_parcial").equals(CodedValues.TPA_SIM)) {
                                // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                                // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                                adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestExportacao);
                            } else {
                                // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
                                // lista dos contratos a serem removidos do movimento
                                adeImpropria.addContratoSemMargem(adeCodigo);
                                adeRemovidas.add(adeCodigo);
                            }
                        } else {
                            // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                            // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                            adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestExportacao);
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
}
