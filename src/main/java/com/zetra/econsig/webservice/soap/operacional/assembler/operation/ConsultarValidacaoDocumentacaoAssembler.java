package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_DEVIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_OUTRO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME_ABREV;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.MANDADO_PAG;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORS_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.PAGINACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.QUANTIDADE_VALIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RG_FRENTE;
import static com.zetra.econsig.webservice.CamposAPI.RG_VERSO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.STATUS_VALIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.StatusValidacao;
import com.zetra.econsig.webservice.soap.operacional.v8.ValidarDocumentacao;

/**
 * <p>Title: AlongarConsignacaoAssembler</p>
 * <p>Description: Assembler para ConsultarValidacaoDocumentos.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Marcos Nolasco
 */

public class ConsultarValidacaoDocumentacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarValidacaoDocumentacaoAssembler.class);

    private ConsultarValidacaoDocumentacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.ConsultarValidacaoDocumentacao consultarValidacaoDocumentacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        final StatusValidacao statusValidacao = getValue(consultarValidacaoDocumentacao.getStatusValidacao());

        parametros.put(USUARIO, consultarValidacaoDocumentacao.getUsuario());
        parametros.put(SENHA, consultarValidacaoDocumentacao.getSenha());

        final List<String> ssoCodigos = new ArrayList<>();
        if (!TextHelper.isNull(statusValidacao)) {
            if (!TextHelper.isNull(statusValidacao.getPendente()) && Boolean.TRUE.equals(statusValidacao.getPendente())) {
                ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
            }
            if (!TextHelper.isNull(statusValidacao.getAprovada()) && Boolean.TRUE.equals(statusValidacao.getAprovada())) {
                ssoCodigos.add(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo());
            }
        }

        parametros.put(STATUS_VALIDACAO, ssoCodigos);
        final int pagina = !TextHelper.isNull(getValue(consultarValidacaoDocumentacao.getPagina())) ? getValue(consultarValidacaoDocumentacao.getPagina()) : 1;
        parametros.put(PAGINACAO, pagina);

        return parametros;
    }

    public static ValidarDocumentacao toValidarDocumentacaoV8(Map<CamposAPI, Object> paramvalidarDocumentacao) {
        final ValidarDocumentacao validarDocumentacao = new ValidarDocumentacao();

        validarDocumentacao.setConsignataria(String.valueOf(paramvalidarDocumentacao.get(CSA_NOME_ABREV)));
        validarDocumentacao.setConsignatariaCodigo(String.valueOf(paramvalidarDocumentacao.get(CSA_IDENTIFICADOR)));
        validarDocumentacao.setResponsavel(String.valueOf(paramvalidarDocumentacao.get(RESPONSAVEL)));
        validarDocumentacao.setAdeNumero(String.valueOf(paramvalidarDocumentacao.get(ADE_NUMERO)));
        validarDocumentacao.setAdeIdentificador(String.valueOf(paramvalidarDocumentacao.get(ADE_IDENTIFICADOR)));
        try {
            validarDocumentacao.setDataReserva((BaseAssembler.toXMLGregorianCalendar((Date) paramvalidarDocumentacao.get(DATA_RESERVA), true)));
            validarDocumentacao.setDataInicial((BaseAssembler.toXMLGregorianCalendar((Date) paramvalidarDocumentacao.get(DATA_INICIAL), true)));
            validarDocumentacao.setDataFinal((BaseAssembler.toXMLGregorianCalendar((Date) paramvalidarDocumentacao.get(DATA_FINAL), true)));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR DATA RESERVA  " + paramvalidarDocumentacao.get(DATA_RESERVA) + " VIA SOAP.");
            LOG.warn("ERRO AO RECUPERAR DATA INICIAL " + paramvalidarDocumentacao.get(DATA_INICIAL) + " VIA SOAP.");
            LOG.warn("ERRO AO RECUPERAR DATA FINAL " + paramvalidarDocumentacao.get(DATA_FINAL) + " VIA SOAP.");
        }

        try {
            validarDocumentacao.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramvalidarDocumentacao.get(ADE_VLR).toString(), NumberHelper.getLang(), "en", 2, 8)));
            if (!TextHelper.isNull(paramvalidarDocumentacao.get(ADE_VLR_LIQUIDO))) {
                validarDocumentacao.setValorLiberado(Double.parseDouble(NumberHelper.reformat(paramvalidarDocumentacao.get(ADE_VLR_LIQUIDO).toString(), NumberHelper.getLang(), "en", 2, 8)));
            }
            if (!TextHelper.isNull(paramvalidarDocumentacao.get(ADE_VLR_DEVIDO))) {
                validarDocumentacao.setValorDevido(Double.parseDouble(NumberHelper.reformat(paramvalidarDocumentacao.get(ADE_VLR_DEVIDO).toString(), NumberHelper.getLang(), "en", 2, 8)));
            }
        } catch (NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR VALOR CONTRATO  " + paramvalidarDocumentacao.get(ADE_VLR) + " VIA SOAP.");
            LOG.warn("ERRO AO RECUPERAR VALOR LIQUIDO CONTRATO " + paramvalidarDocumentacao.get(ADE_VLR_LIQUIDO) + " VIA SOAP.");
            LOG.warn("ERRO AO RECUPERAR VALOR DEVIDO " + paramvalidarDocumentacao.get(ADE_VLR_DEVIDO) + " VIA SOAP.");
        }

        validarDocumentacao.setServico(String.valueOf(paramvalidarDocumentacao.get(SVC_DESCRICAO)));
        validarDocumentacao.setCodVerba(String.valueOf(paramvalidarDocumentacao.get(CNV_COD_VERBA)));
        validarDocumentacao.setOrgao(String.valueOf(paramvalidarDocumentacao.get(ORG_NOME)));
        validarDocumentacao.setPrazo(Integer.parseInt(paramvalidarDocumentacao.get(PRAZO).toString()));
        validarDocumentacao.setServidor(String.valueOf(paramvalidarDocumentacao.get(SER_NOME)));
        validarDocumentacao.setCpf(String.valueOf(paramvalidarDocumentacao.get(SER_CPF)));
        validarDocumentacao.setQuantValidacao(Integer.parseInt(paramvalidarDocumentacao.get(QUANTIDADE_VALIDACAO).toString()));
        validarDocumentacao.setOrigem(String.valueOf(paramvalidarDocumentacao.get(ORS_DESCRICAO)));
        validarDocumentacao.setNomeArquivoRGFrente(String.valueOf(paramvalidarDocumentacao.get(RG_FRENTE)));
        validarDocumentacao.setNomeArquivoRGVerso(String.valueOf(paramvalidarDocumentacao.get(RG_VERSO)));
        validarDocumentacao.setNomeArquivoMandadoPag(String.valueOf(paramvalidarDocumentacao.get(MANDADO_PAG)));
        validarDocumentacao.setNomeArquivoContraCheque(String.valueOf(paramvalidarDocumentacao.get(CONTRACHEQUE)));
        validarDocumentacao.setNomeArquivoOutro(String.valueOf(paramvalidarDocumentacao.get(ARQUIVO_OUTRO)));
        validarDocumentacao.setObservacao(!TextHelper.isNull(paramvalidarDocumentacao.get(OBSERVACAO)) ? String.valueOf(paramvalidarDocumentacao.get(OBSERVACAO)) : "");
        return validarDocumentacao;
    }
}