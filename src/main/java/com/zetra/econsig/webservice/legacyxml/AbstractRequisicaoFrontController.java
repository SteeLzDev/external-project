package com.zetra.econsig.webservice.legacyxml;

import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;

import java.io.OutputStream;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AbstractRequisicaoFrontController</p>
 * <p>Description: abstract front controller para requisição externa às operações eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractRequisicaoFrontController {
    protected AcessoSistema responsavel = null;

    //Saida para o qual será gerada o documento XML
    protected Object saida;

    protected void preProcessa(Map<CamposAPI, Object> parametros) throws RequisicaoFrontControllerException {
        // Verifica se o sistema não está indisponível
        Short status;
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        try {
            status = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            if (status.equals(CodedValues.STS_INDISP)) {
                throw new RequisicaoFrontControllerException("mensagem.restricao.acesso.geral.arg0", responsavel, LoginHelper.getMensagemSistemaIndisponivel());
            }

        } catch (ConsignanteControllerException ex) {
            if (ex.getMessageKey() != null) {
                parametros.put(COD_RETORNO, ex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            }
            parametros.put(MENSAGEM, ex.getMessage());
            throw new RequisicaoFrontControllerException(ex);
        } catch (RequisicaoFrontControllerException ex) {
            if (ex.getMessageKey() != null) {
                parametros.put(COD_RETORNO, ex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            }
            parametros.put(MENSAGEM, ex.getMessage());
            throw ex;
        }

    }

    protected void validaPermissao(AcessoSistema responsavel) throws RequisicaoFrontControllerException {
        // Se não tem permissão de Integrar via XML, então não precisa continuar
        if (!responsavel.temPermissao(CodedValues.FUN_INTEGRAR_XML)) {
            throw new RequisicaoFrontControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
        }
    }

    /**
     * valida se requisição remota é uma das operações válidas no eConsig
     * @param operacao
     * @throws RequisicaoFrontControllerException
     */
    protected void validaOperacao(String operacao) throws RequisicaoFrontControllerException {
        if (!(operacao != null &&
                (CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_ATUALIZAR_PARCELA) ||
                CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CANCELAR_CONSIGNACAO_SV) ||
                CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao) ||
                CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_PARAMETROS) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_PARAMETROS_v2_0) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_PARAMETROS_v8_0) ||
                CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao) ||
                CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_LISTA_SOLICITACOES) ||
                CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_SIMULAR_CONSIGNACAO) ||
                operacao.equalsIgnoreCase(CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_PARCELA) ||
                operacao.equalsIgnoreCase(CodedValues.OP_LIQUIDAR_PARCELA) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_TAXA_JUROS) ||
                CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_SERVIDOR) ||
                operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_SERVIDOR_V8_0)
                ))) {
            throw new RequisicaoFrontControllerException("mensagem.operacaoInvalida", responsavel);
        }
    }

    public abstract void processa() throws RequisicaoFrontControllerException;

    protected abstract Object geraSaida(Map<CamposAPI, Object> parametros) throws RequisicaoFrontControllerException;

    protected class RequisicaoFrontControllerException extends ZetraException {

        public RequisicaoFrontControllerException(Throwable cause) {
            super(cause);
        }

        public RequisicaoFrontControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
            super(messageKey, responsavel, messageArgs);
        }

        public RequisicaoFrontControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
            super(messageKey, responsavel, cause, messageArgs);
        }

        @Override
        public String getMessage() {
            String message = super.getMessage();
            return TextHelper.removeAccent(message);
        }
    }

    public Object getSaida() {
        return saida;
    }

    public void setSaida(OutputStream saida) {
        this.saida = saida;
    }

}
