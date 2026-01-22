package com.zetra.econsig.webservice.soap.servidor.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PODE_SIMULAR;
import static com.zetra.econsig.webservice.CamposAPI.PODE_SOLICITAR;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
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
import com.zetra.econsig.webservice.soap.servidor.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.SimulacaoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.InserirSolicitacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.SimularConsignacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.v2.InserirSolicitacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v2.InserirSolicitacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v2.ObjectFactory;
import com.zetra.econsig.webservice.soap.servidor.v2.SimularConsignacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v2.SimularConsignacaoServidorResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: ServidorV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Servidor versão 2.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class ServidorV2Endpoint extends ServidorEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorV2Endpoint.class);

    private static final String NAMESPACE_URI = "ServidorService-v2_0";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserirSolicitacaoServidor")
    @ResponsePayload
    public InserirSolicitacaoServidorResponse inserirSolicitacaoServidor(@RequestPayload InserirSolicitacaoServidor inserirSolicitacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = InserirSolicitacaoServidorAssembler.toMap(inserirSolicitacaoServidor);
        final InserirSolicitacaoServidorResponse resposta = new InserirSolicitacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createDetalharConsultaConsignacaoServidorResponseBoleto(BoletoAssembler.toBoletoV2(paramResposta)));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "simularConsignacaoServidor")
    @ResponsePayload
    public SimularConsignacaoServidorResponse simularConsignacaoServidor(@RequestPayload SimularConsignacaoServidor simularConsignacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = SimularConsignacaoServidorAssembler.toMap(simularConsignacaoServidor);
        final SimularConsignacaoServidorResponse resposta = new SimularConsignacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);


            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                    resposta.setPodeSimular(factory.createSimularConsignacaoServidorResponsePodeSimular((Boolean)paramResposta.get(PODE_SIMULAR)));
                    resposta.setPodeSolicitar(factory.createSimularConsignacaoServidorResponsePodeSolicitar((Boolean)paramResposta.get(PODE_SOLICITAR)));
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO);
                    if (SIMULACAO.equals(nomeReg)) {
                        resposta.getSimulacoes().add(SimulacaoAssembler.toSimulacaoV2(paramResposta));
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
            } else if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioSenhaInvalidos".equals(e.getMessageKey())) {
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
                resposta.setSucesso(false);
                resposta.setMensagem(e.getMessage());
                LOG.error(e.getMessage(), e);

                return resposta;
            } else {
                LOG.error(e.getMessage(), e);
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }
}