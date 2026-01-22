package com.zetra.econsig.webservice.soap.servidor.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EXIGE_GRUPO_PERGUNTAS;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.RequisicaoExternaAppController;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.endpoint.EndpointBase;

/**
 * <p>Title: EndpointBase</p>
 * <p>Description: Base para os Endpoints de comunicação SOAP do service Servidor</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
public class ServidorEndpointBase extends EndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorEndpointBase.class);

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, String remoteAddr, Integer remotePort) throws ZetraException {
        // Valida os parâmetros para evitar inclusão de XSS
        parametros = XSSPreventionFilter.stripXSS_API(parametros);

        final ServidorDelegate serDelegate = new ServidorDelegate();
        AcessoSistema responsavel = null;
        try {
            final TransferObject ctoUsuario = serDelegate.buscaUsuarioServidor(null, null, (String) parametros.get(RSE_MATRICULA), (String) parametros.get(ORG_IDENTIFICADOR), (String) parametros.get(EST_IDENTIFICADOR), responsavel);
            responsavel = AcessoSistema.recuperaAcessoSistema(ctoUsuario.getAttribute(Columns.USU_CODIGO).toString(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            final ZetraException ze = new ZetraException("mensagem.usuarioSenhaInvalidos", (AcessoSistema) null);
            LOG.error(e1.getMessage(), e1);
            throw ze;
        }

        final RequisicaoExternaCommand cmd = RequisicaoExternaAppController.createRequisicaoExternaCommand(parametros, responsavel);
        boolean autenticado = false;
        try {
            if (!TextHelper.isNull(parametros.get(EXIGE_GRUPO_PERGUNTAS)) && parametros.get(EXIGE_GRUPO_PERGUNTAS).equals(Boolean.TRUE.toString()) ||
                !TextHelper.isNull(parametros.get(USUARIO)) &&
                 !CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.contains(parametros.get(OPERACAO).toString()) &&
                 !CodedValues.OP_VERIFICA_LIMITE_SENHA_AUT.contains(parametros.get(OPERACAO).toString()) &&
                 !CodedValues.OP_GERAR_SENHA_AUTORIZACAO.contains(parametros.get(OPERACAO).toString())) {
                if (!TextHelper.isNull(parametros.get(OPERACAO)) &&
                    CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.contains(parametros.get(OPERACAO).toString()) &&
                    !TextHelper.isNull(parametros.get(SENHA_USUARIO))) {
                    parametros.put(SENHA, parametros.get(SENHA_USUARIO));
                }
                cmd.autenticaUsuario(parametros);
            } else {
                cmd.autenticaUsuarioServidor(parametros);
            }
            autenticado = true;
        } catch (final ZetraException autEx) {
            parametros.put(MENSAGEM, autEx.getMessage());
            parametros.put(COD_RETORNO, autEx.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            LOG.error(autEx.getMessage(), autEx);
        }
        if (autenticado) {
            if (!ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_SER_ACESSO_HOST_A_HOST, cmd.getResponsavel())) {
                final ZetraException ze = new ZetraException("mensagem.usuarioNaoTemPermissao", cmd.getResponsavel());
                throw ze;
            }
            cmd.processa();
        }
        final RespostaRequisicaoExternaCommand resposta = RequisicaoExternaAppController.createRespostaRequisicaoExterna(parametros, responsavel);
        return resposta.geraResposta(parametros);
    }
}