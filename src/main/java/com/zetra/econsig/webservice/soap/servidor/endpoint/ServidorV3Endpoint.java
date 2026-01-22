package com.zetra.econsig.webservice.soap.servidor.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR_V3_0;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
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
import com.zetra.econsig.webservice.soap.servidor.assembler.DadosServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.ConsultarDadosCadastraisServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.v3.ConsultarDadosCadastraisServidor;
import com.zetra.econsig.webservice.soap.servidor.v3.ConsultarDadosCadastraisServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v3.ObjectFactory;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: ServidorV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Servidor versão 3.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class ServidorV3Endpoint extends ServidorEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorV3Endpoint.class);

    private static final String NAMESPACE_URI = "ServidorService-v3_0";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarDadosCadastraisServidor")
    @ResponsePayload
    public ConsultarDadosCadastraisServidorResponse consultarDadosCadastraisServidor(@RequestPayload ConsultarDadosCadastraisServidor consultarDadosCadastraisServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarDadosCadastraisServidorAssembler.toMap(consultarDadosCadastraisServidor);
        final ConsultarDadosCadastraisServidorResponse resposta = new ConsultarDadosCadastraisServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);


            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0);
                    if (DADOS_SERVIDOR_V3_0.equals(nomeReg)) {
                        resposta.setDadosServidor(factory.createConsultarDadosCadastraisServidorResponseDadosServidor(DadosServidorAssembler.toDadosServidorV3(paramResposta)));
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
            } else if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioSenhaInvalidos".equals(e.getMessageKey())) {
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
                resposta.setSucesso(false);
                resposta.setMensagem(e.getMessage());
                LOG.error(e.getMessage(), e);

                return resposta;
            } else {
                LOG.error(e.getMessage(), e);
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.dados.cadastrais.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }
}