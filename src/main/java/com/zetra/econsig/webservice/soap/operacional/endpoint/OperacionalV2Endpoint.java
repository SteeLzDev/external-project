package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;

import java.util.List;
import java.util.Map;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContextHolder;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.soap.operacional.assembler.ParametroSetAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarParametrosAssembler;
import com.zetra.econsig.webservice.soap.operacional.v2.ConsultarParametros;
import com.zetra.econsig.webservice.soap.operacional.v2.ConsultarParametrosResponse;
import com.zetra.econsig.webservice.soap.operacional.v2.ObjectFactory;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 2.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV2Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV2Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v2_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarParametros")
    @ResponsePayload
    public ConsultarParametrosResponse consultarParametros(@RequestPayload ConsultarParametros consultarParametros) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarParametrosAssembler.toMap(consultarParametros);

        AcessoSistema responsavel = null;
        final ConsultarParametrosResponse resposta = new ConsultarParametrosResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarParametros.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {

            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS_v2_0);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS_v2_0);

                    if (PARAMETRO_SET.equals(nomeReg)) {
                        resposta.setParametroSet(factory.createConsultarParametrosResponseParametroSet(ParametroSetAssembler.toParametroSetV2(paramResposta)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV2(paramResposta));
                    }
                }
            }

            return resposta;
        } catch (final ZetraException e) {
            if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioNaoTemPermissao".equals(e.getMessageKey())) {
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_SEM_PERMISSAO_PARA_ACAO));
                resposta.setSucesso(false);
                resposta.setMensagem(e.getMessage());
                LOG.error(e.getMessage(), e);

                return resposta;
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.parametros.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.parametros.arg0", responsavel, e.getMessage()));
        }
    }
}
