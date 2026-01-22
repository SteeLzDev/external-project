package com.zetra.econsig.webservice.soap.servidor.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NUMERO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_3;

import java.text.ParseException;
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
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.ContrachequeAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.DadosServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.SimulacaoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.CadastrarEmailServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.CancelarSolicitacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.ConsultarConsignacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.ConsultarContraChequeServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.ConsultarDadosCadastraisServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.ConsultarMargemServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.DetalharConsultaConsignacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.GerarSenhaAutorizacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.InserirSolicitacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.RecuperarPerguntaDadosCadastraisAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.SimularConsignacaoServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.VerificarEmailServidorAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.VerificarLimitesSenhaAutorizacaoAssembler;
import com.zetra.econsig.webservice.soap.servidor.assembler.operation.VerificarRespostaDadosCadastraisAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.CadastrarEmailServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.CadastrarEmailServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.CancelarSolicitacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.CancelarSolicitacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarConsignacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarConsignacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarContraChequeServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarContraChequeServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarDadosCadastraisServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarDadosCadastraisServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarMargemServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarMargemServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.DetalharConsultaConsignacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.DetalharConsultaConsignacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.GerarSenhaAutorizacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.GerarSenhaAutorizacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.InfoMargem;
import com.zetra.econsig.webservice.soap.servidor.v1.InserirSolicitacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.InserirSolicitacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.servidor.v1.RecuperarPerguntaDadosCadastrais;
import com.zetra.econsig.webservice.soap.servidor.v1.RecuperarPerguntaDadosCadastraisResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.SimularConsignacaoServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.SimularConsignacaoServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarEmailServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarEmailServidorResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarLimitesSenhaAutorizacao;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarLimitesSenhaAutorizacaoResponse;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarRespostaDadosCadastrais;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarRespostaDadosCadastraisResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: ServidorV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Servidor versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class ServidorV1Endpoint extends ServidorEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorV1Endpoint.class);

    private static final String NAMESPACE_URI = "ServidorService-v1_0";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarEmailServidor")
    @ResponsePayload
    public CadastrarEmailServidorResponse cadastrarEmailServidor(@RequestPayload CadastrarEmailServidor cadastrarEmailServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CadastrarEmailServidorAssembler.toMap(cadastrarEmailServidor);
        final CadastrarEmailServidorResponse resposta = new CadastrarEmailServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);


            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.verificar.email.servidor.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarSolicitacaoServidor")
    @ResponsePayload
    public CancelarSolicitacaoServidorResponse cancelarSolicitacaoServidor(@RequestPayload CancelarSolicitacaoServidor cancelarSolicitacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CancelarSolicitacaoServidorAssembler.toMap(cancelarSolicitacaoServidor);
        final CancelarSolicitacaoServidorResponse resposta = new CancelarSolicitacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_SOLICITACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_SOLICITACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createDetalharConsultaConsignacaoServidorResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.solicitacao.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarConsignacaoServidor")
    @ResponsePayload
    public ConsultarConsignacaoServidorResponse consultarConsignacaoServidor(@RequestPayload ConsultarConsignacaoServidor consultarConsignacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarConsignacaoServidorAssembler.toMap(consultarConsignacaoServidor);
        final ConsultarConsignacaoServidorResponse resposta = new ConsultarConsignacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createDetalharConsultaConsignacaoServidorResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV1(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.consignacao.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarContraChequeServidor")
    @ResponsePayload
    public ConsultarContraChequeServidorResponse consultarContraChequeServidor(@RequestPayload ConsultarContraChequeServidor consultarContraChequeServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarContraChequeServidorAssembler.toMap(consultarContraChequeServidor);
        final ConsultarContraChequeServidorResponse resposta = new ConsultarContraChequeServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONTRACHEQUE);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_CONTRACHEQUE);
                    if (BOLETO.equals(nomeReg) || CONTRACHEQUE.equals(nomeReg)) {
                        resposta.setContracheque((factory.createConsultarContraChequeServidorResponseContracheque(ContrachequeAssembler.toContrachequeV1(paramResposta))));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.contracheque.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarDadosCadastraisServidor")
    @ResponsePayload
    public ConsultarDadosCadastraisServidorResponse consultarDadosCadastraisServidor(@RequestPayload ConsultarDadosCadastraisServidor consultarDadosCadastraisServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarDadosCadastraisServidorAssembler.toMap(consultarDadosCadastraisServidor);
        final ConsultarDadosCadastraisServidorResponse resposta = new ConsultarDadosCadastraisServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONS_DADOS_CADASTRAIS);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONS_DADOS_CADASTRAIS);
                    if (DADOS_SERVIDOR.equals(nomeReg)) {
                        resposta.setDadosServidor(factory.createConsultarDadosCadastraisServidorResponseDadosServidor(DadosServidorAssembler.toDadosServidorV1(paramResposta)));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarMargemServidor")
    @ResponsePayload
    public ConsultarMargemServidorResponse consultarMargemServidor(@RequestPayload ConsultarMargemServidor consultarMargem) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarMargemServidorAssembler.toMap(consultarMargem);

        final ConsultarMargemServidorResponse resposta = new ConsultarMargemServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();

                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));


                    if (sucesso) {
                        final InfoMargem info = new InfoMargem();
                        info.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
                        info.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
                        info.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
                        info.setOrgao((String) paramResposta.get(ORGAO));
                        info.setCategoria((String) paramResposta.get(RSE_TIPO));
                        info.setServidor((String) paramResposta.get(SERVIDOR));
                        info.setCpf((String) paramResposta.get(SER_CPF));
                        info.setMatricula((String) paramResposta.get(RSE_MATRICULA));
                        final String dataNasc = (String) paramResposta.get(DATA_NASCIMENTO);
                        final String dataAdmissao = (String) paramResposta.get(RSE_DATA_ADMISSAO);
                        try {
                            if (!TextHelper.isNull(dataNasc)) {
                                info.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataNasc, LocaleHelper.getDatePattern()), false));
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                        }

                        try {
                            if (!TextHelper.isNull(dataAdmissao)) {
                                info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataAdmissao, LocaleHelper.getDatePattern()), false));
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                        }


                        if (paramResposta.get(RSE_PRAZO) != null) {
                            info.setPrazoServidor((Integer) paramResposta.get(RSE_PRAZO));
                        }

                        if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_1))) {
                            info.setValorMargem(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_1), NumberHelper.getLang(), "en", 2, 8)));
                        } else if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM))) {
                            info.setValorMargem(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM), NumberHelper.getLang(), "en", 2, 8)));
                        } else {
                            // Se não exibe margem, retorna NaN para que os clientes (como o centralizador) possam
                            // distinguir esta restrição, e não confundir com Zero quando a margem é realmente Zero.
                            info.setValorMargem(Double.NaN);
                        }

                        if (paramResposta.get(TEXTO_MARGEM_1) != null) {
                            info.setTextoMargem(paramResposta.get(TEXTO_MARGEM_1).toString());
                        } else if (paramResposta.get(TEXTO_MARGEM) != null) {
                            info.setTextoMargem(paramResposta.get(TEXTO_MARGEM).toString());
                        } else {
                            info.setTextoMargem("");
                        }

                        if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_2))) {
                            info.setValorMargem2(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_2), NumberHelper.getLang(), "en", 2, 8)));
                        } else {
                            info.setValorMargem2(Double.NaN);
                        }

                        if (paramResposta.get(TEXTO_MARGEM_2) != null) {
                            info.setTextoMargem2(paramResposta.get(TEXTO_MARGEM_2).toString());
                        } else {
                            info.setTextoMargem2("");
                        }

                        if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_3))) {
                            info.setValorMargem3(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_3), NumberHelper.getLang(), "en", 2, 8)));
                        } else {
                            info.setValorMargem3(Double.NaN);
                        }

                        if (paramResposta.get(TEXTO_MARGEM_3) != null) {
                            info.setTextoMargem3(paramResposta.get(TEXTO_MARGEM_3).toString());
                        } else {
                            info.setTextoMargem3("");
                        }

                        resposta.setInfoMargem(factory.createConsultarMargemServidorResponseInfoMargem(info));
                    }
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM);
                    if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", (AcessoSistema) null, e.getMessage()));
            }
        } catch (final NumberFormatException | ParseException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", (AcessoSistema) null, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "detalharConsultaConsignacaoServidor")
    @ResponsePayload
    public DetalharConsultaConsignacaoServidorResponse detalharConsultaConsignacaoServidor(@RequestPayload DetalharConsultaConsignacaoServidor detalharConsultaConsignacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = DetalharConsultaConsignacaoServidorAssembler.toMap(detalharConsultaConsignacaoServidor);

        final DetalharConsultaConsignacaoServidorResponse resposta = new DetalharConsultaConsignacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createDetalharConsultaConsignacaoServidorResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.consulta.consignacao.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "gerarSenhaAutorizacaoServidor")
    @ResponsePayload
    public GerarSenhaAutorizacaoServidorResponse gerarSenhaAutorizacaoServidor(@RequestPayload GerarSenhaAutorizacaoServidor gerarSenhaAutorizacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = GerarSenhaAutorizacaoServidorAssembler.toMap(gerarSenhaAutorizacaoServidor);
        final GerarSenhaAutorizacaoServidorResponse resposta = new GerarSenhaAutorizacaoServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_GERAR_SENHA_AUTORIZACAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                    resposta.setSenhaServidor(factory.createGerarSenhaAutorizacaoServidorResponseSenhaServidor((String) paramResposta.get(SENHA_AUTORIZACAO)));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

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
                        resposta.setBoleto(factory.createDetalharConsultaConsignacaoServidorResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "recuperarPerguntaDadosCadastrais")
    @ResponsePayload
    public RecuperarPerguntaDadosCadastraisResponse recuperarPerguntaDadosCadastrais(@RequestPayload RecuperarPerguntaDadosCadastrais recuperarPerguntaDadosCadastrais) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = RecuperarPerguntaDadosCadastraisAssembler.toMap(recuperarPerguntaDadosCadastrais);
        final RecuperarPerguntaDadosCadastraisResponse resposta = new RecuperarPerguntaDadosCadastraisResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_RECUPERAR_PERG_DADOS_CAD);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                    if (!TextHelper.isNull(paramResposta.get(NUMERO_PERGUNTA))) {
                        resposta.setNumeroPergunta(factory.createRecuperarPerguntaDadosCadastraisResponseNumeroPergunta(paramResposta.get(NUMERO_PERGUNTA).toString()));
                    }
                    resposta.setTextoPergunta(factory.createRecuperarPerguntaDadosCadastraisResponseTextoPergunta((String) paramResposta.get(TEXTO_PERGUNTA)));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "simularConsignacaoServidor")
    @ResponsePayload
    public SimularConsignacaoServidorResponse simularConsignacaoServidor(@RequestPayload SimularConsignacaoServidor simularConsignacaoServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

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

                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO);
                    if (SIMULACAO.equals(nomeReg)) {
                        resposta.getSimulacoes().add(SimulacaoAssembler.toSimulacaoV1(paramResposta));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "verificarEmailServidor")
    @ResponsePayload
    public VerificarEmailServidorResponse verificarEmailServidor(@RequestPayload VerificarEmailServidor verificarEmailServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = VerificarEmailServidorAssembler.toMap(verificarEmailServidor);
        final VerificarEmailServidorResponse resposta = new VerificarEmailServidorResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_VERIFICAR_EMAIL_SERVIDOR);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.verificar.email.servidor.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "verificarLimitesSenhaAutorizacao")
    @ResponsePayload
    public VerificarLimitesSenhaAutorizacaoResponse verificarLimitesSenhaAutorizacao(@RequestPayload VerificarLimitesSenhaAutorizacao verificarLimitesSenhaAutorizacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = VerificarLimitesSenhaAutorizacaoAssembler.toMap(verificarLimitesSenhaAutorizacao);
        final VerificarLimitesSenhaAutorizacaoResponse resposta = new VerificarLimitesSenhaAutorizacaoResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_VERIFICA_LIMITE_SENHA_AUT);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "verificarRespostaDadosCadastrais")
    @ResponsePayload
    public VerificarRespostaDadosCadastraisResponse verificarRespostaDadosCadastrais(@RequestPayload VerificarRespostaDadosCadastrais verificarRespostaDadosCadastrais) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = VerificarRespostaDadosCadastraisAssembler.toMap(verificarRespostaDadosCadastrais);
        final VerificarRespostaDadosCadastraisResponse resposta = new VerificarRespostaDadosCadastraisResponse();

        try {
            parametros.put(OPERACAO, CodedValues.OP_VERIFICA_RESP_PERG_DADOS);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, remoteAddr, remotePort);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", (AcessoSistema) null, e.getMessage()));
            }
        }
    }
}