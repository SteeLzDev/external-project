package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_SEGURO_PRESTAMISTA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: AlterarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de alterar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlterarConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarConsignacaoCommand.class);

    public AlterarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        validaValorAutorizacao(parametros);
        validaAdePrazo(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            final String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            BigDecimal adeVlrOld = (BigDecimal) autorizacao.getAttribute(Columns.ADE_VLR);
            Integer adePrazoOld = (Integer) autorizacao.getAttribute(Columns.ADE_PRAZO);
            final int adePrdPagas = autorizacao.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ((Integer) autorizacao.getAttribute(Columns.ADE_PRD_PAGAS)) : 0;


            final String svcCodigo = (String) parametros.get(SVC_CODIGO);
            final String csaCodigo = (String) parametros.get(CSA_CODIGO);
            final String rseCodigo = (String) parametros.get(RSE_CODIGO);
            Integer adePrazo = (Integer) parametros.get(ADE_PRAZO);
            final Integer carencia = (Integer) parametros.get(ADE_CARENCIA);
            BigDecimal adeVlr = (BigDecimal) parametros.get(ADE_VLR);
            final BigDecimal adeTaxaJuros = (BigDecimal) parametros.get(ADE_TAXA_JUROS);
            final BigDecimal adeVlrTac = (BigDecimal) parametros.get(ADE_VLR_TAC);
            final BigDecimal adeVlrIof = (BigDecimal) parametros.get(ADE_VLR_IOF);
            final BigDecimal adeVlrLiquido = (parametros.get(VALOR_LIBERADO) != null) ? (BigDecimal) parametros.get(VALOR_LIBERADO) : (BigDecimal) parametros.get(ADE_VLR_LIQUIDO);
            final BigDecimal adeVlrMensVinc = (BigDecimal) parametros.get(ADE_VLR_MENS_VINC);
            final BigDecimal adeVlrSegPrestamista = (BigDecimal) parametros.get(ADE_SEGURO_PRESTAMISTA);
            final String adeIndice = (String) parametros.get(ADE_INDICE);
            String novoAdeIdentificador = (String) parametros.get(NOVO_ADE_IDENTIFICADOR);
            final String serSenha = (String) parametros.get(SER_SENHA);
            final String token = (String) parametros.get(TOKEN);
            final String loginExterno = (String) parametros.get(SER_LOGIN);

            // Busca os parâmetros de serviço necessários para as validações
            ParamSvcTO paramSvcCse = null;
            try {
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            } catch (final ParametroControllerException ex) {
                throw ex;
            }
            final boolean retemMargemSvcPercentual = paramSvcCse.isTpsRetemMargemSvcPercentual();
            final String tipoVlr = paramSvcCse.getTpsTipoVlr();

            final String exigeSenhaServidor = paramSvcCse.getTpsExigeSenhaAlteracaoContratos();
            final String permiteAumentarVlrPrz = paramSvcCse.getTpsPermiteAumVlrPrzConsignacao();
            final boolean permiteAumentarVlr = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(permiteAumentarVlrPrz) || CodedValues.PERMITE_AUMENTAR_APENAS_VLR_CONTRATO.equals(permiteAumentarVlrPrz);
            final boolean permiteAumentarPrz = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(permiteAumentarVlrPrz) || CodedValues.PERMITE_AUMENTAR_APENAS_PRZ_CONTRATO.equals(permiteAumentarVlrPrz);

            if (retemMargemSvcPercentual && CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr)) {
                adeVlrOld = (BigDecimal) autorizacao.getAttribute(Columns.ADE_VLR_PERCENTUAL);
            }

            if (adeVlr == null) {
                // Se o novo valor não foi informado, então mantém o atual
                adeVlr = adeVlrOld;
            }
            if ((adePrazo == null) && (adePrazoOld != null)) {
                // Se o novo valor não foi informado, então mantém o atual
                adePrazo = adePrazoOld.intValue() - adePrdPagas;
            } else if ((adePrazo != null) && (adePrazo >= CodedValues.VLR_ADE_PRAZO_INDETERMINADO)) {
                adePrazo = null;
            }
            if (novoAdeIdentificador == null) {
                // Se o novo valor não foi informado, então mantém o atual
                novoAdeIdentificador = (String) autorizacao.getAttribute(Columns.ADE_IDENTIFICADOR);
            }

            if (adePrazoOld != null) {
                // Se não é indeterminado, veja se já tem parcelas pagas, e ajusta o prazo para o valor restante,
                // já que na operação o usuário informa a quantidade de parcelas ainda a serem pagas.
                adePrazoOld = adePrazoOld.intValue() - adePrdPagas;
            }

            // Verifica se aumentou o valor ou o prazo
            final boolean aumentouValor = (adeVlrOld.compareTo(adeVlr) == -1);
            final boolean aumentouPrazo = (((adePrazoOld != null) && (adePrazo == null)) ||
                    ((adePrazoOld != null) && (adePrazo != null) && (adePrazoOld.intValue() < adePrazo.intValue())));

            // Verifica se aumentou o capital devido
            final boolean aumentouCapitalDevido = (((adePrazoOld != null) && (adePrazo != null)) ?
                    (adePrazo.doubleValue() * adeVlr.doubleValue()) > (adePrazoOld.doubleValue() * adeVlrOld.doubleValue()) : true);


            // Se aumentou e não pode, então retorna erro para o usuário
            if (aumentouValor && !permiteAumentarVlr) {
                throw new ZetraException("mensagem.erro.valor.parcela.maior.atual", responsavel);
            }
            if (aumentouPrazo && !permiteAumentarPrz) {
                throw new ZetraException("mensagem.erro.prazo.maior.atual", responsavel);
            }

            final String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
            final boolean csaValidaSenha = (TextHelper.isNull(tpaCsaValidaSenha) || CodedValues.TPA_NAO.equals(tpaCsaValidaSenha)) == false;

            final CustomTransferObject permiteAlterarComLimitacao = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO, responsavel);
            final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);

            List<ParcelaDescontoTO> parcelasProcessadas = new ArrayList<>();
            List<ParcelaDescontoPeriodo> parcelasEmProcessamento = new ArrayList<>();

            boolean valorAlteradoDentroLimite = false;
            boolean permiteAlterarSemSenha = false;

            // Valida a senha do servidor para fazer a alteração do contrato
            if (CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS.equals(exigeSenhaServidor) ||
                    (CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR.equals(exigeSenhaServidor) && (aumentouValor || aumentouPrazo)) ||
                    (CodedValues.EXIGE_SENHA_ALTERACAO_CAPITAL_DEVIDO_MAIOR.equals(exigeSenhaServidor) && aumentouCapitalDevido)) {

                if ((permiteAlterarComLimitacao != null) && (permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR) != null) && "1".equals(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR))) {
                    parcelasProcessadas = parcelaController.findParcelas(adeCodigo, null, responsavel);
                    parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);

                    final Double percentualAlteracao = Double.parseDouble(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR).toString());
                    final BigDecimal valorContrato = adePrazo != null ? adeVlr.multiply(new BigDecimal(adePrazo)) : adeVlr;
                    final BigDecimal valorContratoOld = adePrazoOld != null ? adeVlrOld.multiply(new BigDecimal(adePrazoOld)) : adeVlrOld;

                    final boolean valorParcelaDentroLimite = ((adeVlr.divide(adeVlrOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;
                    final boolean valorContratoDentroLimite = ((valorContrato.divide(valorContratoOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;

                    valorAlteradoDentroLimite =  valorContratoDentroLimite && valorParcelaDentroLimite;

                    if (valorAlteradoDentroLimite && CodedValues.SAD_DEFERIDA.equals(autorizacao.getAttribute(Columns.SAD_CODIGO)) && (parcelasProcessadas != null) && parcelasProcessadas.isEmpty() && (parcelasEmProcessamento != null) && parcelasEmProcessamento.isEmpty()) {
                        permiteAlterarSemSenha = true;
                    }
                    if (!permiteAlterarSemSenha && (TextHelper.isNull(serSenha) || TextHelper.isNull(token))) {
                        throw new ZetraException("mensagem.senha.servidor.autorizacao.limite.sem.senha", responsavel, percentualAlteracao.toString());
                    }
                }
                if (!permiteAlterarSemSenha) {
                    if (!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) {
                        validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
                    } else {
                        throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
                    }
                }
            } else if((!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) && csaValidaSenha){
                validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
            }

            // Valida o novo valor do contrato
            AutorizacaoHelper.validarValorAutorizacao(adeVlr, svcCodigo, csaCodigo, responsavel);

            // Compara valor do identificador com máscara definida pelo parâmetro de serviço
            final String mascaraIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();
            if (!TextHelper.isNull(mascaraIdentificador)) {
                try {
                    novoAdeIdentificador = TextHelper.aplicarMascara(novoAdeIdentificador, mascaraIdentificador);
                } catch (final ZetraException ex) {
                    throw new ZetraException("mensagem.erro.ade.identificador.invalido", responsavel);
                }
            }

            final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazo,
                    novoAdeIdentificador, adeIndice, adeVlrTac, adeVlrIof,
                    adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeVlrSegPrestamista, carencia, loginExterno, serSenha);

            final String strOcaPeriodo = (String) parametros.get(PERIODO);
            if (((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                    ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) && !TextHelper.isNull(strOcaPeriodo)) {
                try {
                    if (strOcaPeriodo.matches("([0-9]{2})/([0-9]{4})")) {
                        final Date periodo = DateHelper.parsePeriodString(strOcaPeriodo);
                        final String ocaPeriodo = DateHelper.format(periodo, LocaleHelper.FORMATO_DATA_INGLES);

                        alterarParam.setOcaPeriodo(ocaPeriodo);
                    } else {
                        throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                    }
                } catch (final ParseException e) {
                    throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                }
            }

            // Salva anexo para ser vinculado ao contrato posteriormente
            final Anexo anexo = getAnexo(parametros.get(ANEXO));
            if (anexo != null) {
                try {
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    if (TextHelper.isNull(diretorioRaizArquivos)) {
                        throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
                    }

                    final String path = diretorioRaizArquivos + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date)autorizacao.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
                    final File file = salvarAnexo(path, anexo, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO);

                    alterarParam.setAnexo(file);

                } catch (final IOException e) {
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel);
                }
            }

            // Motivo de operação
            final String tmoObs = (String) parametros.get(TMO_OBS);
            final String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);

            if (!TextHelper.isNull(tmoIdentificador)) {
                try {
                    final TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                    final TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                    alterarParam.setTmoCodigo(tmo.getTmoCodigo());
                    alterarParam.setOcaObs(tmoObs);
                } catch (final TipoMotivoOperacaoControllerException tex) {
                    LOG.error(tex.getMessage(), tex);
                    throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
                }
            }

            // Altera consignação
            final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
            consigDelegate.alterarConsignacao(alterarParam, responsavel);
        }
    }
}
