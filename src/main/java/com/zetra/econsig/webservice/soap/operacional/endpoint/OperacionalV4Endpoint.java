package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V4_0;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CadastrarServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DetalharConsultaConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.PesquisarServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.v4.CadastrarServidor;
import com.zetra.econsig.webservice.soap.operacional.v4.CadastrarServidorResponse;
import com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v4.PesquisarServidor;
import com.zetra.econsig.webservice.soap.operacional.v4.PesquisarServidorResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 4.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV4Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV4Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v4_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarServidor")
    @ResponsePayload
    public CadastrarServidorResponse cadastrarServidor(@RequestPayload CadastrarServidor cadastrarServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CadastrarServidorAssembler.toMap(cadastrarServidor);

        AcessoSistema responsavel = null;
        final CadastrarServidorResponse resposta = new CadastrarServidorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarServidor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_SERVIDOR);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR);
                    if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.setServidor(factory.createCadastrarServidorResponseServidor(ServidorAssembler.toServidorV4(paramResposta, responsavel)));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.servidor.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "detalharConsultaConsignacao")
    @ResponsePayload
    public DetalharConsultaConsignacaoResponse detalharConsultaConsignacao(@RequestPayload DetalharConsultaConsignacao detalharConsultaConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = DetalharConsultaConsignacaoAssembler.toMap(detalharConsultaConsignacao);

        AcessoSistema responsavel = null;
        final DetalharConsultaConsignacaoResponse resposta = new DetalharConsultaConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(detalharConsultaConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE);

                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV4(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV4(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV4(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV4(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.consulta.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pesquisarServidor")
    @ResponsePayload
    public PesquisarServidorResponse pesquisarServidor(@RequestPayload PesquisarServidor pesquisarServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = PesquisarServidorAssembler.toMap(pesquisarServidor);
        parametros.put(CamposAPI.LIMITE_RESULTADO, LIMITE_RESULTADO);

        AcessoSistema responsavel = null;
        final PesquisarServidorResponse resposta = new PesquisarServidorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(pesquisarServidor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            int countSer = 0;
            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    if (sucesso) {
                        resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
                    } else {
                        resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                    }
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR);

                    if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV4(paramResposta, responsavel));
                        countSer++;
                    }
                }
            }

            if (countSer >= Integer.parseInt(LIMITE_RESULTADO)) {
                resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.sucesso.resultado.limitado.offset", responsavel, LIMITE_RESULTADO));
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, COD_SUCESSO_RESULTADO_QTD_LIMITADA));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.pesquisar.servidor.arg0", responsavel, e.getMessage()));
            }
        }
    }
}