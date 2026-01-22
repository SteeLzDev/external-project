package com.zetra.econsig.webservice.soap.endpoint;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.RequisicaoExternaAppController;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

import jakarta.xml.bind.JAXBElement;

/**
 * <p>Title: EndpointBase</p>
 * <p>Description: Base para os Endpoints de comunicação SOAP</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
@SuppressWarnings("java:S1118")
public abstract class EndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EndpointBase.class);

    // Constantes
    protected static final String USUARIO_OU_SENHA_INVALIDOS = "358";
    protected static final String USUARIO_SEM_PERMISSAO_PARA_ACAO = "329";
    protected static final String COD_SUCESSO_RESULTADO_QTD_LIMITADA = "010";

    protected static final String LIMITE_RESULTADO = "30";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(String funCodigo, Map<CamposAPI, Object> parametros, String versaoInterface, AcessoSistema responsavel) throws ZetraException {
        // Valida os parâmetros para evitar inclusão de XSS
        parametros = XSSPreventionFilter.stripXSS_API(parametros);

        final RequisicaoExternaCommand cmd = RequisicaoExternaAppController.createRequisicaoExternaCommand(parametros, responsavel);
        boolean autenticado = false;
        try {
            cmd.setVersaoInterface(versaoInterface);
            cmd.autenticaUsuario(parametros);
            autenticado = true;
        } catch (final ZetraException autEx) {
            parametros.put(CamposAPI.MENSAGEM, autEx.getMessage());
            parametros.put(CamposAPI.COD_RETORNO, autEx.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            LOG.error(autEx.getMessage(), autEx);
        }
        if (autenticado) {
            validaPermissao(funCodigo, cmd.getResponsavel());
            cmd.processa();
        }
        final RespostaRequisicaoExternaCommand resposta = RequisicaoExternaAppController.createRespostaRequisicaoExterna(parametros, responsavel);
        return resposta.geraResposta(parametros);
    }

    protected void validaPermissao(String funCodigo, AcessoSistema responsavel) throws ZetraException {
        // Se não tem permissão de Integrar via XML, então não precisa continuar
        if (!responsavel.temPermissao(funCodigo)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }
    }

    protected JAXBElement<String> createCodRetorno(String namespaceURI, String codRetorno) {
        return new JAXBElement<>(new javax.xml.namespace.QName(namespaceURI, "codRetorno"), String.class, codRetorno);
    }
}