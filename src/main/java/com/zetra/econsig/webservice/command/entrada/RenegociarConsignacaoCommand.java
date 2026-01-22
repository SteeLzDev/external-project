package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACOES;
import static com.zetra.econsig.webservice.CamposAPI.COR_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: RenegociarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de renegociar/comprar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RenegociarConsignacaoCommand extends SolicitarReservaCommand {

    private static final String MENSAGEM_NENHUMA_CONSIGNACAO_ENCONTRADA = "mensagem.nenhumaConsignacaoEncontrada";

    public RenegociarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaVerbaPorNaturezaServico(parametros);
        validaCnvCodigoSvcCodigo(parametros);
        validaCpfMatricula(parametros);
        validaCodigoVerba(parametros);
        validaDataNascimento(parametros);
        validaValorAutorizacao(parametros);
        validaAdePrazo(parametros);
        validaInfoBancaria(parametros);
    }

    @Override
    protected void realizaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        final List<TransferObject> autorizacoes = (parametros.get(CONSIGNACOES) != null) ? (List<TransferObject>) parametros.get(CONSIGNACOES) : (List<TransferObject>) parametros.get(CONSIGNACAO);
        if ((autorizacoes == null) || autorizacoes.isEmpty()) {
            throw new ZetraException(MENSAGEM_NENHUMA_CONSIGNACAO_ENCONTRADA, responsavel);
        }

        final String operacao = (String) parametros.get(OPERACAO);
        final boolean isCompra = CodedValues.OP_COMPRAR_CONTRATO.equals(operacao);

        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final Object adeVlr = parametros.get(ADE_VLR);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String corCodigo = (String) parametros.get(COR_CODIGO);
        final Object adePrazo = parametros.get(ADE_PRAZO);
        final Object adeCarencia = parametros.get(ADE_CARENCIA);
        final String cnvCodigo = (String) parametros.get(CNV_CODIGO);
        //    Indice
        final Object adeIndice = parametros.get(ADE_INDICE);
        final BigDecimal adeVlrTac = (BigDecimal) parametros.get(ADE_VLR_TAC);
        final BigDecimal adeVlrIof = (BigDecimal) parametros.get(ADE_VLR_IOF);

        final Object vlrLiquidoAux = (parametros.get(ADE_VLR_LIQUIDO) != null) ? parametros.get(ADE_VLR_LIQUIDO):parametros.get(VALOR_LIBERADO);
        BigDecimal adeVlrLiquido = null;
        if (vlrLiquidoAux != null) {
            adeVlrLiquido = (vlrLiquidoAux instanceof Double) ? ((vlrLiquidoAux.equals(Double.NaN)) ? null : BigDecimal.valueOf((Double) vlrLiquidoAux)) : (BigDecimal) vlrLiquidoAux;
        }

        final BigDecimal adeVlrMensVinc = (BigDecimal) parametros.get(ADE_VLR_MENS_VINC);
        final BigDecimal adeTaxaJuros = (BigDecimal) parametros.get(ADE_TAXA_JUROS);

        // Informações bancárias
        final Object numBanco = parametros.get(RSE_BANCO);
        final Object numAgencia = parametros.get(RSE_AGENCIA);
        final Object numConta = parametros.get(RSE_CONTA);

        final List<String> adeCodigos = new ArrayList<>();
        for (final TransferObject autorizacao: autorizacoes) {
            adeCodigos.add(autorizacao.getAttribute(Columns.ADE_CODIGO).toString());
        }

        // Cria objeto de parâmetros de renegociação
        final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo("CSA");
        renegociarParam.setRseCodigo(rseCodigo);
        renegociarParam.setAdeVlr((BigDecimal)adeVlr);
        renegociarParam.setCorCodigo(corCodigo);
        renegociarParam.setAdePrazo((Integer)adePrazo);
        renegociarParam.setAdeCarencia((Integer)adeCarencia);
        renegociarParam.setAdeIdentificador(adeIdentificador);
        renegociarParam.setCnvCodigo(cnvCodigo);
        renegociarParam.setSerSenha(serSenha);
        renegociarParam.setSerAtivo(!permiteIncluirAdeServidorExcluido(csaCodigo));
        renegociarParam.setComSerSenha(comSerSenha);
        renegociarParam.setAdeIndice((String)adeIndice);
        renegociarParam.setAdeVlrTac(adeVlrTac);
        renegociarParam.setAdeVlrIof(adeVlrIof);
        renegociarParam.setAdeVlrLiquido(adeVlrLiquido);
        renegociarParam.setAdeVlrMensVinc(adeVlrMensVinc);
        renegociarParam.setAdeTaxaJuros(adeTaxaJuros);
        renegociarParam.setAdeCodigosRenegociacao(adeCodigos);
        renegociarParam.setCompraContrato(isCompra);
        renegociarParam.setCftCodigo(cftCodigo);
        renegociarParam.setCdeVlrLiberado(vlrLiberado);
        renegociarParam.setCdeRanking(ranking);
        renegociarParam.setCdeTxtContato("");
        renegociarParam.setAdeBanco((String) numBanco);
        renegociarParam.setAdeAgencia((String) numAgencia);
        renegociarParam.setAdeConta((String) numConta);
        renegociarParam.setValidaAnexo(false);
        renegociarParam.setAlterarDataEncerramento(CodedValues.TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_ALTERA_DATA_ENCERRAMENTO_RENEGOCIACAO_PADRAO, responsavel)));

        if (isCompra && (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OPCIONAL, responsavel) ||
                ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel))) {

            final Anexo anexoRequisicao = getAnexo(parametros.get(ANEXO));
            if ((anexoRequisicao != null) && (anexoRequisicao.getArquivo() != null) && (anexoRequisicao.getNomeArquivo() != null) && !anexoRequisicao.getNomeArquivo().isBlank()) {
                try {
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    if (TextHelper.isNull(diretorioRaizArquivos)) {
                        throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
                    }

                    final String idAnexo = UUID.randomUUID().toString();
                    final String path = diretorioRaizArquivos + File.separatorChar + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + idAnexo;
                    final File anexo = salvarAnexo(path, anexoRequisicao, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO);

                    // Passa as informações para registrar o arquivo anexo na operação
                    renegociarParam.setIdAnexo(idAnexo);
                    renegociarParam.setAnexoDocAdicionalCompra(anexo.getName());

                } catch (final IOException ex) {
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
                }
            } else if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel)) {
                // Se não foi informado, mas é obrigatório, reporta o erro ao usuário
                throw new ZetraException("mensagem.erro.nao.pode.inserir.nova.reserva.para.este.servico.pois.anexo.obrigatorio", responsavel);
            }
        }

        // Faz renegociação
        final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
        adeCodigo = consigDelegate.renegociarContrato(renegociarParam, responsavel);
    }
    
    @Override
    protected void recuperaAutorizacao(Map<CamposAPI, Object> parametros, Object adeNumero, Object adeIdentificador,
            CustomTransferObject criterio) throws AutorizacaoControllerException, ZetraException {
        
        try {
            super.recuperaAutorizacao(parametros, adeNumero, adeIdentificador, criterio);
        } catch (ZetraException e) {
            if (verificarSeErroDeveSerLancado(e)) {
                throw e;
            }
        }

    }

    @Override
    protected void recuperaAutorizacaoByIdn(Map<CamposAPI, Object> parametros, Object adeIdentificador,
            CustomTransferObject criterio) throws AutorizacaoControllerException, ZetraException {

        try {
            super.recuperaAutorizacaoByIdn(parametros, adeIdentificador, criterio);
        } catch (ZetraException e) {
            if (verificarSeErroDeveSerLancado(e)) {
                throw e;
            }
        }
        
    }

    private boolean verificarSeErroDeveSerLancado(ZetraException e) {
        return !MENSAGEM_NENHUMA_CONSIGNACAO_ENCONTRADA.equalsIgnoreCase(e.getMessageKey());
    }

}
