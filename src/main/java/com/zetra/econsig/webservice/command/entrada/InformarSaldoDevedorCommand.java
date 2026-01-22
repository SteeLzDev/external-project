package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ANEXO_BOLETO_DSD;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO_DSD;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.DETALHE;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.SDV_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_1;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_2;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_3;
import static com.zetra.econsig.webservice.CamposAPI.SDV_LINK_BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_MULTIPLO_TO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_NOME_FAVORECIDO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_NUM_CONTRATO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_PROPOSTA_REFIN;
import static com.zetra.econsig.webservice.CamposAPI.SDV_TO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_1;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_2;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_3;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: InformarSaldoDevedorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de info. de saldo devedor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InformarSaldoDevedorCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformarSaldoDevedorCommand.class);

    private boolean exigeAnexoDsdSaldo;
    private boolean exigeAnexoBoletoSaldo;
    private boolean exigeAnexoDsdSaldoCompra;
    private boolean exigeAnexoBoletoSaldoCompra;

    public InformarSaldoDevedorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);

        CustomTransferObject saldosDevedoresMultiplos = null;

        @SuppressWarnings("unchecked")
        final
        List<TransferObject> autdesList = (List<TransferObject>) parametros.get(CONSIGNACAO);

        final CustomTransferObject autdes = (CustomTransferObject) autdesList.get(0);
        final String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        // TODO Avaliar a possibilidade de inclusão no WebService um parâmetro para informação se a operação é de compra ou solicitação de saldo devedor
        final boolean isCompra = CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(autdes.getAttribute(Columns.SAD_CODIGO));
        final boolean isSolicitacaoSaldo = !isCompra;

        if(isCompra && CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase((String) parametros.get(OPERACAO))) {
            throw new ZetraException("mensagem.editar.saldo.devedor.operacao.nao.permitida", responsavel);
        }

        boolean infSaldoDevedorOpcional = false;
        if (isCompra) {
            infSaldoDevedorOpcional = ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, CodedValues.INF_SALDO_DEVEDOR_COMPRA_AUSENTE, responsavel)
                                   || ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, CodedValues.INF_SALDO_DEVEDOR_COMPRA_OPCIONAL, responsavel);
        } else if (isSolicitacaoSaldo) {
            infSaldoDevedorOpcional = ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        }

        if (!infSaldoDevedorOpcional) {
            if (TextHelper.isNull(parametros.get(SDV_AGENCIA))) {
                throw new ZetraException ("mensagem.informe.agencia.deposito", responsavel);
            } else if (TextHelper.isNull(parametros.get(SDV_BANCO))) {
                throw new ZetraException ("mensagem.informe.banco.deposito", responsavel);
            } else if (TextHelper.isNull(parametros.get(SDV_CONTA))) {
                throw new ZetraException ("mensagem.informe.conta.deposito", responsavel);
            } else if (TextHelper.isNull(parametros.get(SDV_NOME_FAVORECIDO))) {
                throw new ZetraException ("mensagem.informe.favorecido.deposito", responsavel);
            } else if (TextHelper.isNull(parametros.get(SDV_CNPJ))) {
                throw new ZetraException ("mensagem.informe.cnpj.deposito", responsavel);
            }
        }

        exigeAnexoDsdSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        exigeAnexoBoletoSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        exigeAnexoDsdSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        exigeAnexoBoletoSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);

        final SaldoDevedorController saldoDevedorController = ApplicationContextProvider.getApplicationContext().getBean(SaldoDevedorController.class);

        if (saldoDevedorController.temSolicitacaoSaldoInformacaoApenas(adeCodigo, responsavel)) {
            exigeAnexoBoletoSaldo = false;
        }

        // Se não é solicitação de saldo devedor pelo servidor, então não exige anexos no cadastro de saldo
        final boolean exigeAnexoBoleto = (!isSolicitacaoSaldo ? exigeAnexoBoletoSaldoCompra : exigeAnexoBoletoSaldo);

        // Se não é compra, então não exige anexo do DSD para compra
        final boolean exigeAnexoDsd = (!isCompra ? exigeAnexoDsdSaldo : exigeAnexoDsdSaldoCompra);

        if ((exigeAnexoDsd && (parametros.get(ANEXO_DSD) == null)) ||
                (exigeAnexoBoleto && (parametros.get(ANEXO_BOLETO_DSD) == null))) {
            List<TransferObject> anexos = null;
            try {
                final List<String> tarCodigos = new ArrayList<>();
                if (exigeAnexoDsd) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                }
                if (exigeAnexoBoleto) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                }
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
                anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            boolean possuiAnexoDsd = false;
            boolean possuiAnexoBoleto = false;
            if (anexos != null){
                for (final TransferObject anexo : anexos) {
                    final String tarCodigo = anexo.getAttribute(Columns.TAR_CODIGO).toString();
                    if (exigeAnexoDsd && tarCodigo.equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo())) {
                        possuiAnexoDsd = true;
                    }
                    if (exigeAnexoBoleto && tarCodigo.equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo())) {
                        possuiAnexoBoleto = true;
                    }
                }
            }

            if (exigeAnexoDsd && !possuiAnexoDsd && (parametros.get(ANEXO_DSD) == null)) {
                throw new ZetraException("mensagem.informe.anexo.saldo.dsd", responsavel);
            } else if (exigeAnexoBoleto && !possuiAnexoBoleto && (parametros.get(ANEXO_BOLETO_DSD) == null)) {
                throw new ZetraException ("mensagem.informe.anexo.saldo.boleto", responsavel);
            }
        }

        // Busca os parâmetros de sistema necessários
        final boolean exigeMultiplosSaldos = ParamSist.paramEquals(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, CodedValues.TPC_SIM, responsavel);

        final String svcCodigo = (String) autdes.getAttribute(Columns.SVC_CODIGO);

        final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

        // Configurações para o campo de número de contrato.
        // - A exigência do número de contrato é configurada via parâmetro de serviço.
        final SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();
        final boolean numeroContratoObrigatorio = isCompra? paramSvc.isTpsExigeNroContratoInfSaldoDevedorCompra(): (isSolicitacaoSaldo? paramSvc.isTpsExigeNroContratoInfSaldoDevedorSolicSaldo(): false);

        final Long numeroContrato = (Long) parametros.get(SDV_NUM_CONTRATO);

        if (numeroContratoObrigatorio && TextHelper.isNull(numeroContrato)) {
            throw new ZetraException("mensagem.informe.numero.contrato", responsavel);
        }

        final Double valorSaldoDevedor = (Double) parametros.get(SDV_VLR_SALDO_DEVEDOR_1);

        if (valorSaldoDevedor == null) {
            throw new ZetraException("mensagem.informe.saldo.devedor", responsavel);
        }
        final BigDecimal sdvValor = BigDecimal.valueOf(valorSaldoDevedor);
        if (sdvValor.compareTo(new BigDecimal("0")) <= 0) {
            throw new ZetraException("mensagem.erro.saldo.devedor.maior.zero", responsavel);
        }

        // Cria TO com os dados do saldo devedor
        final SaldoDevedorTransferObject saldoDevedorTO = new SaldoDevedorTransferObject();
        saldoDevedorTO.setAdeCodigo(adeCodigo);
        saldoDevedorTO.setUsuCodigo((responsavel != null ? responsavel.getUsuCodigo() : null));
        saldoDevedorTO.setBcoCodigo(!TextHelper.isNull(parametros.get(SDV_BANCO)) ? Short.valueOf((String) parametros.get(SDV_BANCO)) : null);
        saldoDevedorTO.setSdvAgencia(parametros.get(SDV_AGENCIA) != null ? parametros.get(SDV_AGENCIA).toString() : "");
        saldoDevedorTO.setSdvConta(parametros.get(SDV_CONTA) != null ? parametros.get(SDV_CONTA).toString() : "");
        saldoDevedorTO.setSdvNomeFavorecido(parametros.get(SDV_NOME_FAVORECIDO) != null ? parametros.get(SDV_NOME_FAVORECIDO).toString() : "");
        String cnpjFavorecido = (String) parametros.get(SDV_CNPJ);
        if (!TextHelper.isNull(cnpjFavorecido)) {
            String numeroCnpj = TextHelper.dropSeparator(cnpjFavorecido);
            if (numeroCnpj.length() < 14) {
                numeroCnpj = TextHelper.formataMensagem(numeroCnpj, "0", 14, false);
            }
            cnpjFavorecido = TextHelper.format(numeroCnpj, "##.###.###/####-##");
        }
        saldoDevedorTO.setSdvCnpj(cnpjFavorecido != null ? cnpjFavorecido : "");
        saldoDevedorTO.setObs((String) parametros.get(OBS));
        saldoDevedorTO.setSdvValor(sdvValor);
        saldoDevedorTO.setSdvLinkBoletoQuitacao((String) parametros.get(SDV_LINK_BOLETO));
        saldoDevedorTO.setSdvNumeroContrato(!TextHelper.isNull(numeroContrato) ? numeroContrato.toString() : null);

        // Cria TO com os dados dos múltiplos saldos devedores
        if (exigeMultiplosSaldos) {
            final Short qtdePrestacoes = (Short) parametros.get(PRAZO);
            final Date dataSaldoDevedor1 = (Date) parametros.get(SDV_DATA_VENC_1);
            final Date dataSaldoDevedor2 = (Date) parametros.get(SDV_DATA_VENC_2);
            final Date dataSaldoDevedor3 = (Date) parametros.get(SDV_DATA_VENC_3);
            final Double valorSaldoDevedor2 = (Double) parametros.get(SDV_VLR_SALDO_DEVEDOR_2);
            final Double valorSaldoDevedor3 = (Double) parametros.get(SDV_VLR_SALDO_DEVEDOR_3);

            if (TextHelper.isNull(qtdePrestacoes)) {
                throw new ZetraException("mensagem.informe.sdv.multiplos.qtde.parcelas", responsavel);
            }

            final StringBuilder obs = new StringBuilder();
            obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.qtde.prd.liquidada", responsavel)).append(":").append(qtdePrestacoes != null ? qtdePrestacoes.toString() : "");
            try {
                if ((dataSaldoDevedor1 != null) && (valorSaldoDevedor != null) && !valorSaldoDevedor.isNaN()) {
                    obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, DateHelper.toDateString(dataSaldoDevedor1), NumberHelper.reformat(valorSaldoDevedor.toString(), "en", NumberHelper.getLang())));
                } else {
                    throw new ZetraException("mensagem.informe.sdv.multiplos.primeiro.data.valor", responsavel);
                }

                if ((dataSaldoDevedor2 != null) && (valorSaldoDevedor2 != null) && !valorSaldoDevedor2.isNaN()) {
                    obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, DateHelper.toDateString(dataSaldoDevedor2), NumberHelper.reformat(valorSaldoDevedor2.toString(), "en", NumberHelper.getLang())));
                } else {
                    throw new ZetraException("mensagem.informe.sdv.multiplos.segundo.data.valor", responsavel);
                }

                if ((dataSaldoDevedor3 != null) && (valorSaldoDevedor3 != null) && !valorSaldoDevedor3.isNaN()) {
                    obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, DateHelper.toDateString(dataSaldoDevedor3), NumberHelper.reformat(valorSaldoDevedor3.toString(), "en", NumberHelper.getLang())));
                } else {
                    throw new ZetraException("mensagem.informe.sdv.multiplos.terceiro.data.valor", responsavel);
                }
            } catch (final ParseException e) {
                throw new ZetraException("mensagem.erro.sdv.formato.invalido", responsavel);
            }
            if (!TextHelper.isNull(parametros.get(OBS))) {
                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.observacao.abreviado", responsavel)).append(": ").append((String) parametros.get(OBS));
            }

            // Valores a serem usados na rotina padrão.
            saldoDevedorTO.setObs(obs.toString());

            // Salva as informações na DadosAutorizacaoDesconto
            saldosDevedoresMultiplos = new CustomTransferObject();
            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATACADASTRO, DateHelper.toDateTimeString(DateHelper.getSystemDatetime()));
            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES, qtdePrestacoes.toString());
            if ((dataSaldoDevedor1 != null) && (valorSaldoDevedor != null)) {
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO1, DateHelper.toDateString(dataSaldoDevedor1));
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO1, valorSaldoDevedor.toString());
            }
            if ((dataSaldoDevedor2 != null) && (valorSaldoDevedor2 != null)) {
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO2, DateHelper.toDateString(dataSaldoDevedor2));
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO2, valorSaldoDevedor2.toString());
            }
            if ((dataSaldoDevedor3 != null) && (valorSaldoDevedor3 != null)) {
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO3, DateHelper.toDateString(dataSaldoDevedor3));
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO3, valorSaldoDevedor3.toString());
            }
        }

        String detalhe = (String) parametros.get(DETALHE);

        // Se limite saldo cadastrado e permite saldo fora da faixa limite, então chama rotina para validação
        // de saldo, e caso retorne falso, o saldo está acima da faixa limite, portanto a consignatária
        // deve informar os detalhes do cálculo do saldo.
        if ((paramSvc.isTpsLimitaSaldoDevedorCadastrado() && paramSvc.isTpsPermiteSaldoForaFaixaLimite()) && !sdvDelegate.validarSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, null, responsavel)) {
            if (TextHelper.isNull(detalhe)) {
                throw new ZetraException("mensagem.erro.saldo.devedor.limite.invalido", responsavel);
            } else {
                // Se já foi informado o detalhe, então prossegue a atualização das informações
                detalhe = detalhe.replace("\r\n", "<BR>").replace("\n", "<BR>");
                saldoDevedorTO.setObs(saldoDevedorTO.getObs() + "<BR><B>" + ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.detalhe", responsavel) +":</B> " + detalhe);
            }
        }

        parametros.put(SDV_MULTIPLO_TO, saldosDevedoresMultiplos);
        parametros.put(SDV_TO, saldoDevedorTO);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();
        @SuppressWarnings("unchecked")
        final
        List<TransferObject> autdesList = (List<TransferObject>) parametros.get(CONSIGNACAO);

        final CustomTransferObject autdes = (CustomTransferObject) autdesList.get(0);
        final String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        final SaldoDevedorTransferObject saldoDevedorTO = (SaldoDevedorTransferObject) parametros.get(SDV_TO);
        final CustomTransferObject saldosDevedoresMultiplos = (CustomTransferObject) parametros.get(SDV_MULTIPLO_TO);

        // TODO Avaliar a possibilidade de inclusão no WebService um parâmetro para informação se a operação é de compra ou solicitação de saldo devedor
        final String sadCodigo = autdes.getAttribute(Columns.SAD_CODIGO).toString();
		final boolean isCompra = CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo);
        final boolean isSolicitacaoSaldo = !isCompra;

        if(isCompra && CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase((String) parametros.get(OPERACAO))) {
            throw new ZetraException("mensagem.editar.saldo.devedor.operacao.nao.permitida", responsavel);
        }

        // Se compra valida se possui relacionamento de compra, se solicitação de saldo do servidor, verifica se possui solicitação de informação de saldo
        if (isCompra) {
        	final CompraContratoController compraContratoController = ApplicationContextProvider.getApplicationContext().getBean(CompraContratoController.class);
            final Boolean temRelacionamentoCompraByOrigem = compraContratoController.temRelacionamentoCompraByOrigem(adeCodigo);

            if (!temRelacionamentoCompraByOrigem || !responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR)) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        	}
        } else if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && !responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER))  {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        // Se não é solicitação de saldo devedor pelo servidor, então não exige anexos no cadastro de saldo
        final boolean exigeAnexoBoleto = (!isSolicitacaoSaldo ? exigeAnexoBoletoSaldoCompra : exigeAnexoBoletoSaldo);
        // Se não é compra, então não exige anexo do DSD para compra
        final boolean exigeAnexoDsd = (!isCompra ? exigeAnexoDsdSaldo : exigeAnexoDsdSaldoCompra);

        if (exigeAnexoDsd || exigeAnexoBoleto) {
            final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
            if (TextHelper.isNull(diretorioRaizArquivos)) {
                throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
            }

            final String path = diretorioRaizArquivos + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date)autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;

            try {
                if (exigeAnexoBoleto) {
                    final Anexo arquivoBoletoDsd = getAnexo(parametros.get(ANEXO_BOLETO_DSD));
                    if (arquivoBoletoDsd != null) {
                        final File fileBoletoDsd = salvarAnexo(path, arquivoBoletoDsd, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_SALDO_DEVEDOR);
                        // Se o anexo do Boleto foi informado, inclui no objeto de saldo devedor para ser salvo
                        saldoDevedorTO.setAnexoBoleto(fileBoletoDsd);
                    }
                }
                if (exigeAnexoDsd) {
                    final Anexo arquivoDsd = getAnexo(parametros.get(ANEXO_DSD));
                    if (arquivoDsd != null) {
                        final File fileDsd = salvarAnexo(path, arquivoDsd, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_SALDO_DEVEDOR);
                        // Se o anexo do DSD foi informado, inclui no objeto de saldo devedor para ser salvo
                        saldoDevedorTO.setAnexoDsd(fileDsd);
                    }
                }
            } catch (final IOException e) {
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
        }

        final String textoRefinanciamentoParcela = (String) parametros.get(SDV_PROPOSTA_REFIN);
        final CustomTransferObject novaCmn = new CustomTransferObject();

        if (!TextHelper.isNull(textoRefinanciamentoParcela)) {
            final String serEmail = autdes.getAttribute(Columns.SER_EMAIL) != null ? autdes.getAttribute(Columns.SER_EMAIL).toString() : "";

            final StringBuilder textoComunicacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.info.saldo.devedor.refinanciamento.parcelas",responsavel,autdes.getAttribute(Columns.SER_NOME).toString(),autdes.getAttribute(Columns.CSA_NOME).toString())).append("<br/>\n<br/>\n");

            textoComunicacao.append(textoRefinanciamentoParcela);
            // Cria comunicacao
            novaCmn.setAttribute(Columns.PAP_CODIGO, CodedValues.PAP_SERVIDOR);
            novaCmn.setAttribute(Columns.CMN_TEXTO, textoComunicacao.toString());
            novaCmn.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            novaCmn.setAttribute(Columns.SER_CODIGO, autdes.getAttribute(Columns.SER_CODIGO).toString());
            novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, CodedValues.ASSUNTO_REFINANCIAMENTO_PROPOSTA);
            novaCmn.setAttribute(Columns.CMN_IP_ACESSO, responsavel.getIpUsuario());
            novaCmn.setAttribute(Columns.RSE_CODIGO, autdes.getAttribute(Columns.RSE_CODIGO).toString());
            novaCmn.setAttribute(Columns.SER_EMAIL, serEmail);
            novaCmn.setAttribute(Columns.ADE_CODIGO, adeCodigo);
        }

        // Atualiza as informações
        if (sdvDelegate.getSaldoDevedor(adeCodigo, responsavel) == null) {
            sdvDelegate.createSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, null, isCompra, novaCmn, responsavel);
        } else {
            sdvDelegate.updateSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, null, isCompra, novaCmn, responsavel);
        }

        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        // Verifica se a consignatária pode ser desbloqueada automaticamente
        if (responsavel.isCsaCor() && csaDelegate.verificarDesbloqueioAutomaticoConsignataria((String) parametros.get(CSA_CODIGO), responsavel)) {
            parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
        }
    }
}
