package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ALTERA_VLR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CFT_VLR;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.COEFICIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConfirmarSolicitacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de confirmar solicitação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConfirmarSolicitacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarSolicitacaoCommand.class);

    public ConfirmarSolicitacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        exigeMotivoOperacao(parametros);
        validaInfoBancaria(parametros);
        validarCoeficiente(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            String svcCodigo = (String) parametros.get(SVC_CODIGO);
            String orgCodigo = (String) parametros.get(ORG_CODIGO);
            String serSenha = (String)parametros.get(SER_SENHA);
            String token = (String) parametros.get(TOKEN);
            String rseCodigo = (String) parametros.get(RSE_CODIGO);
            String loginExterno = (String) parametros.get(SER_LOGIN);
            String csaCodigo = (String) parametros.get(CSA_CODIGO);
            // Informações bancárias
            Object numBanco = parametros.get(RSE_BANCO);
            Object numAgencia = parametros.get(RSE_AGENCIA);
            Object numConta = parametros.get(RSE_CONTA);
            String codAutorizacao = (String) parametros.get(CODIGO_AUTORIZACAO);

            SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
            AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

            CoeficienteController coeficienteController = ApplicationContextProvider.getApplicationContext().getBean(CoeficienteController.class);

            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();

            if (TextHelper.isNull(svcCodigo)) {
                svcCodigo = (String) autorizacao.getAttribute(Columns.SVC_CODIGO);
            }

            //      Busca os parâmetros de serviço
            ParamSvcTO paramSvcCse = null;
            try {
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            } catch (ParametroControllerException ex) {
                throw ex;
            }

            boolean serSenhaObrigatoria = paramSvcCse.isTpsExigeSenhaConfirmacaoSolicitacao();
            boolean comSerSenha = false;

            String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
            boolean csaValidaSenha = (TextHelper.isNull(tpaCsaValidaSenha) || tpaCsaValidaSenha.equals(CodedValues.TPA_NAO)) ? false : true;

            if (serSenhaObrigatoria) {
                if (!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) {
                    try {
                        validarSenhaServidor(rseCodigo, serSenha, true, loginExterno, csaCodigo, token, responsavel);
                        comSerSenha = true;
                    } catch (ZetraException ex) {
                        throw ex;
                    }
                } else {
                    throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
                }
            } else if((!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) && csaValidaSenha){
                validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
            }

            Object novoAdeIdentificador = parametros.get(NOVO_ADE_IDENTIFICADOR);

            String tmoObs = (String) parametros.get(TMO_OBS);
            String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoIdentificador)) {
                try {
                    TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                    TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                    tmoTO = new CustomTransferObject();
                    tmoTO.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmoTO.setAttribute(Columns.TMO_CODIGO, tmo.getTmoCodigo());
                    tmoTO.setAttribute(Columns.OCA_OBS, tmoObs);
                } catch (TipoMotivoOperacaoControllerException tex) {
                    LOG.error(tex.getMessage(), tex);
                    throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
                }
            }

            if (parametros.get(CFT_VLR) != null) {
                try {
                    BigDecimal cftVlr = (BigDecimal) parametros.get(CFT_VLR);

                    // Pega o coeficiente atual, caso exista. Se não houver
                    // coeficiente desconto cadastrado, será gerada uma exceção
                    CustomTransferObject cftdes = simulacaoController.findCdeByAdeCodigo(adeCodigo, responsavel);
                    TransferObject cft = coeficienteController.getCoeficiente(cftdes.getAttribute(Columns.CDE_CFT_CODIGO).toString(), responsavel);
                    BigDecimal cftVlrAtual = (BigDecimal) cft.getAttribute(Columns.CFT_VLR);

                    // Compara se o novo coeficiente é menor que o atual
                    if (cftVlr.compareTo(cftVlrAtual) == -1) {
                        BigDecimal vlrParcela = (BigDecimal) autorizacao.getAttribute(Columns.ADE_VLR);
                        BigDecimal vlrLiberado = (BigDecimal) cftdes.getAttribute(Columns.CDE_VLR_LIBERADO);

                        Object paramAlteraVlrLiberado = parametros.get(ALTERA_VLR_LIBERADO);
                        boolean alteraVlrLiberado = false;

                        if (paramAlteraVlrLiberado == null) {
                            throw new ZetraException("mensagem.confirmar.solicitacao.escolha.alteracao", responsavel);
                        }

                        if (paramAlteraVlrLiberado instanceof String) {
                            if (TextHelper.isNull(paramAlteraVlrLiberado)) {
                                throw new ZetraException("mensagem.confirmar.solicitacao.escolha.alteracao", responsavel);
                            }
                            alteraVlrLiberado = paramAlteraVlrLiberado.equals("S");
                        } else if (paramAlteraVlrLiberado instanceof Boolean) {
                            alteraVlrLiberado = (Boolean) paramAlteraVlrLiberado;
                        }

                        // Busca os parâmetros de serviço necessários
                        boolean podeAlterarValorParcela = true;
                        try {
                            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                            String tipoVlr   = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
                            // Se é tipo valor total da margem, não pode alterar o valor da parcela
                            if (tipoVlr.equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)) {
                                podeAlterarValorParcela = false;
                            }
                        } catch (ParametroControllerException ex) {
                            throw ex;
                        }

                        // Pega as taxas - TAC e OP
                        BigDecimal adeTac = null, adeOp = null;
                        try {
                            List<String> tpsCodigosTaxas = new ArrayList<>();
                            tpsCodigosTaxas.add(CodedValues.TPS_TAC_FINANCIADA);
                            tpsCodigosTaxas.add(CodedValues.TPS_OP_FINANCIADA);
                            Map<String, String> taxas = adeDelegate.getParamSvcADE(adeCodigo, tpsCodigosTaxas, responsavel);
                            adeTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
                            adeOp = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());
                        } catch (AutorizacaoControllerException ex) {
                            throw ex;
                        }

                        String adePeridiocidade = (String) autorizacao.getAttribute(Columns.ADE_PERIODICIDADE);
                        // Recalcula os valores da simulação de acordo com o novo coeficiente/taxa juros
                        BigDecimal retorno = simulacaoController.alterarValorTaxaJuros(alteraVlrLiberado, vlrParcela, vlrLiberado, cftVlr, adeTac, adeOp, ((Integer) autorizacao.getAttribute(Columns.ADE_PRAZO)).intValue(), orgCodigo, svcCodigo, csaCodigo, adePeridiocidade, responsavel);

                        if (alteraVlrLiberado) {
                            vlrLiberado = retorno;
                        } else if (podeAlterarValorParcela) {
                            vlrParcela = retorno;
                        } else {
                            throw new ZetraException("mensagem.erro.valor.parcela.usar.toda.margem.disponivel", responsavel);
                        }

                        // Confirma a solicitação
                        consigDelegate.confirmarConsignacao(adeCodigo, vlrParcela, (String) novoAdeIdentificador, (String) numBanco, (String) numAgencia, (String) numConta, null, null, serSenha, codAutorizacao, comSerSenha, tmoTO, responsavel);

                        // Salva o novo coeficiente, passando cftVlr no formado NumberHelper.getLang()
                        cft.setAttribute(Columns.CFT_VLR, parametros.get(COEFICIENTE));
                        cft.setAttribute(Columns.CFT_CODIGO, null);
                        String cft_codigo = coeficienteController.insertCoeficiente(cft, responsavel);

                        // Atualiza o coeficiente desconto com o novo coeficiente e o novo valor liberado
                        simulacaoController.updateCoeficienteDesconto(cftdes.getAttribute(Columns.CDE_CODIGO).toString(), cft_codigo, vlrLiberado, responsavel);
                    } else {
                        throw new ZetraException("mensagem.erro.coeficiente.menor.anterior", responsavel);
                    }
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ZetraException(ex);
                }
            } else {
                consigDelegate.confirmarConsignacao(adeCodigo, null, (String) novoAdeIdentificador, (String) numBanco, (String) numAgencia, (String) numConta, null, null, serSenha, codAutorizacao, comSerSenha, tmoTO, responsavel);
            }
        }
    }

    private void validarCoeficiente(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object cftVlr = parametros.get(COEFICIENTE);

        if (cftVlr != null) {
            try {
                cftVlr = parceEntradaDecimal(cftVlr);
                if (((BigDecimal) cftVlr).doubleValue() > 0) {
                    parametros.put(CFT_VLR, cftVlr);
                } else {
                    throw new ZetraException("mensagem.erro.coeficiente.invalido", responsavel);
                }
            } catch (Exception ex) {
                throw new ZetraException("mensagem.erro.coeficiente.invalido", responsavel);
            }
        }
    }

    private BigDecimal parceEntradaDecimal(Object vlr) throws ZetraException {
        BigDecimal vlrBigDecimal = null;
        if (vlr instanceof Double) {
            vlrBigDecimal =  BigDecimal.valueOf((Double) vlr);
        } else {
            try {
                vlrBigDecimal = new BigDecimal(NumberHelper.reformat(vlr.toString(), NumberHelper.getLang(), "en", 2, 8));
            } catch (ParseException e) {
                throw new ZetraException("mensagem.erro.coeficiente.invalido", responsavel);
            }
        }

        return vlrBigDecimal;
    }
}
