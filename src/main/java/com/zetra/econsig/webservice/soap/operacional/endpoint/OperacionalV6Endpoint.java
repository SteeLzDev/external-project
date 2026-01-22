package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V4_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_LIMITE;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContextHolder;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.exception.ParametroControllerException;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AlongarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AlterarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AutorizarReservaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CancelarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CancelarRenegociacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CancelarReservaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConfirmarReservaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConfirmarSolicitacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DetalharConsultaConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.InserirSolicitacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.LiquidarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReativarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.RenegociarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReservarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.SuspenderConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.v6.AlongarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.AlongarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.AlterarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.AlterarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.AutorizarReserva;
import com.zetra.econsig.webservice.soap.operacional.v6.AutorizarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarRenegociacao;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarRenegociacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarReserva;
import com.zetra.econsig.webservice.soap.operacional.v6.CancelarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarReserva;
import com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ConsultarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.ConsultarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ConsultarMargem;
import com.zetra.econsig.webservice.soap.operacional.v6.ConsultarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.DetalharConsultaConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.InfoMargem;
import com.zetra.econsig.webservice.soap.operacional.v6.InserirSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v6.InserirSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.LiquidarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.LiquidarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v6.ReativarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.ReativarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.RenegociarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.RenegociarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.ReservarMargem;
import com.zetra.econsig.webservice.soap.operacional.v6.ReservarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v6.Resumo;
import com.zetra.econsig.webservice.soap.operacional.v6.SuspenderConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v6.SuspenderConsignacaoResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 6.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV6Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV6Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v6_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "alongarConsignacao")
    @ResponsePayload
    public AlongarConsignacaoResponse alongarConsignacao(@RequestPayload AlongarConsignacao alongarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = AlongarConsignacaoAssembler.toMap(alongarConsignacao);

        AcessoSistema responsavel = null;
        final AlongarConsignacaoResponse resposta = new AlongarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(alongarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_ALONGAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_ALONGAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.alongar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "alterarConsignacao")
    @ResponsePayload
    public AlterarConsignacaoResponse alterarConsignacao(@RequestPayload AlterarConsignacao alterarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = AlterarConsignacaoAssembler.toMap(alterarConsignacao);

        AcessoSistema responsavel = null;
        final AlterarConsignacaoResponse resposta = new AlterarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(alterarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_ALTERAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_ALTERAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.alterar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "autorizarReserva")
    @ResponsePayload
    public AutorizarReservaResponse autorizarReserva(@RequestPayload AutorizarReserva autorizarReserva) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = AutorizarReservaAssembler.toMap(autorizarReserva);

        AcessoSistema responsavel = null;
        final AutorizarReservaResponse resposta = new AutorizarReservaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(autorizarReserva.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_AUTORIZAR_RESERVA_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_AUTORIZAR_RESERVA_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.autorizar.reserva.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarConsignacao")
    @ResponsePayload
    public CancelarConsignacaoResponse cancelarConsignacao(@RequestPayload CancelarConsignacao cancelarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CancelarConsignacaoAssembler.toMap(cancelarConsignacao);

        AcessoSistema responsavel = null;
        final CancelarConsignacaoResponse resposta = new CancelarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cancelarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarReserva")
    @ResponsePayload
    public CancelarReservaResponse cancelarReserva(@RequestPayload CancelarReserva cancelarReserva) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CancelarReservaAssembler.toMap(cancelarReserva);

        AcessoSistema responsavel = null;
        final CancelarReservaResponse resposta = new CancelarReservaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cancelarReserva.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_RESERVA_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_RESERVA_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarRenegociacao")
    @ResponsePayload
    public CancelarRenegociacaoResponse cancelarRenegociacao(@RequestPayload CancelarRenegociacao cancelarRenegociacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CancelarRenegociacaoAssembler.toMap(cancelarRenegociacao);

        AcessoSistema responsavel = null;
        final CancelarRenegociacaoResponse resposta = new CancelarRenegociacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cancelarRenegociacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {

            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_RENEGOCIACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_RENEGOCIACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.renegociacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "confirmarReserva")
    @ResponsePayload
    public ConfirmarReservaResponse confirmarReserva(@RequestPayload ConfirmarReserva confirmarReserva) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConfirmarReservaAssembler.toMap(confirmarReserva);

        AcessoSistema responsavel = null;
        final ConfirmarReservaResponse resposta = new ConfirmarReservaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(confirmarReserva.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONFIRMAR_RESERVA_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONFIRMAR_RESERVA_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.confirmar.reserva.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "confirmarSolicitacao")
    @ResponsePayload
    public ConfirmarSolicitacaoResponse confirmarSolicitacao(@RequestPayload ConfirmarSolicitacao confirmarSolicitacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConfirmarSolicitacaoAssembler.toMap(confirmarSolicitacao);

        AcessoSistema responsavel = null;
        final ConfirmarSolicitacaoResponse resposta = new ConfirmarSolicitacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(confirmarSolicitacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONFIRMAR_SOLICITACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONFIRMAR_SOLICITACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                   } else if (RESUMO.equals(nomeReg)) {
                       final Resumo resumo = new Resumo();
                       // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
                       BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
                       resposta.getResumos().add(resumo);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.confirmar.solicitacao.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.confirmar.solicitacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarMargem")
    @ResponsePayload
    public ConsultarMargemResponse consultarMargem(@RequestPayload ConsultarMargem consultarMargem) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ConsultarMargemAssembler.toMap(consultarMargem);

        AcessoSistema responsavel = null;
        final ConsultarMargemResponse resposta = new ConsultarMargemResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarMargem.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V6_0);
            final List<Map<CamposAPI, Object>> lstInfoMargem = new ArrayList<>();
            parametros.put(INFO_MARGEM, lstInfoMargem);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();

                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                    if (sucesso) {
                        // Caso possua informaçao de consulta múltipla de margem
                        if ((lstInfoMargem != null) && !lstInfoMargem.isEmpty()) {
                            for (final Map<CamposAPI, Object> map : lstInfoMargem) {
                                addInfoMargemConsultaMargem(map, resposta, responsavel);
                            }
                        } else {
                            addInfoMargemConsultaMargem(paramResposta, resposta, responsavel);
                        }
                    }
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V6_0);
                    if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
            }
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
        }

    }

    private void addInfoMargemConsultaMargem(Map<CamposAPI, Object> paramResposta, ConsultarMargemResponse consultarMargemResp, AcessoSistema responsavel) throws ParseException {
        final ObjectFactory infoObjFactory = new ObjectFactory();

        final InfoMargem info = infoObjFactory.createInfoMargem();

        info.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        info.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        info.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        info.setOrgao((String) paramResposta.get(ORGAO));
        info.setCategoria((String) paramResposta.get(RSE_TIPO));
        info.setServidor((String) paramResposta.get(SERVIDOR));
        info.setCpf((String) paramResposta.get(SER_CPF));
        info.setMatricula((String) paramResposta.get(RSE_MATRICULA));

        try {
            if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_LIMITE))) {
                info.setValorMargemLimite(infoObjFactory.createInfoMargemValorMargemLimite(Double.valueOf(paramResposta.get(VALOR_MARGEM_LIMITE).toString())));
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        final Object dataNasc = paramResposta.get(DATA_NASCIMENTO);
        final Object dataAdmissao = paramResposta.get(RSE_DATA_ADMISSAO);
        try {
            if (!TextHelper.isNull(dataNasc)) {
                if (dataNasc instanceof String) {
                    info.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataNasc.toString(), LocaleHelper.getDatePattern()), false));
                } else if (dataNasc instanceof final Date value) {
                    info.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(value, false));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }

        try {
            if (!TextHelper.isNull(dataAdmissao)) {
                if (dataAdmissao instanceof String) {
                    info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataAdmissao.toString(), LocaleHelper.getDatePattern()), false));
                } else if (dataAdmissao instanceof final Date value ) {
                    info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(value, false));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }

        //DESENV-9901: Se param de CSA habilitado, retorna prazoServidor = 0, se rsePrazo = NULL; prazoServidor = -1 se rseprazo <=0;
        //             e prazoServidor = rsePrazo se rsePrazo > 0
        ParametroDelegate paramDelegate = new ParametroDelegate();
        try {
            String formataRsePrazo = null;
            if (responsavel != null && responsavel.isCsaCor()) {
                formataRsePrazo = paramDelegate.getParamCsa(responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai(), CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP, responsavel);
            }

            if (!TextHelper.isNull(formataRsePrazo) && formataRsePrazo.equals(CodedValues.TPA_SIM)) {
                if (TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
                    info.setPrazoServidor(0);
                } else {
                    int rsePrazoAux = (Integer) paramResposta.get(RSE_PRAZO);

                    if (rsePrazoAux <= 0) {
                        info.setPrazoServidor(-1);
                    } else {
                        info.setPrazoServidor((Integer) paramResposta.get(RSE_PRAZO));
                    }
                }
            } else if (!TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
                info.setPrazoServidor(Integer.valueOf(paramResposta.get(RSE_PRAZO).toString()));
            } else {
                info.setPrazoServidor(-1);
            }
        } catch (ParametroControllerException e1) {
            LOG.warn("ERRO AO RECUPERAR PARÂMETRO DE CONSIGNATÁRIA " + CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP);
        }

        if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_1))) {
            try {
                info.setValorMargem(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_1), NumberHelper.getLang(), "en", 2, 8)));
            } catch (NumberFormatException | ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM))) {
            try {
                info.setValorMargem(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM), NumberHelper.getLang(), "en", 2, 8)));
            } catch (NumberFormatException | ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
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
            try {
                info.setValorMargem2(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_2), NumberHelper.getLang(), "en", 2, 8)));
            } catch (NumberFormatException | ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            info.setValorMargem2(Double.NaN);
        }

        if (paramResposta.get(TEXTO_MARGEM_2) != null) {
            info.setTextoMargem2(paramResposta.get(TEXTO_MARGEM_2).toString());
        } else {
            info.setTextoMargem2("");
        }

        if (!TextHelper.isNull(paramResposta.get(VALOR_MARGEM_3))) {
            try {
                info.setValorMargem3(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(VALOR_MARGEM_3), NumberHelper.getLang(), "en", 2, 8)));
            } catch (NumberFormatException | ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            info.setValorMargem3(Double.NaN);
        }

        if (paramResposta.get(TEXTO_MARGEM_3) != null) {
            info.setTextoMargem3(paramResposta.get(TEXTO_MARGEM_3).toString());
        } else {
            info.setTextoMargem3("");
        }


        consultarMargemResp.getInfoMargem().add(info);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarConsignacao")
    @ResponsePayload
    public ConsultarConsignacaoResponse consultarConsignacao(@RequestPayload ConsultarConsignacao consultarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarConsignacaoAssembler.toMap(consultarConsignacao);

        AcessoSistema responsavel = null;
        final ConsultarConsignacaoResponse resposta = new ConsultarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.consignacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserirSolicitacao")
    @ResponsePayload
    public InserirSolicitacaoResponse inserirSolicitacao(@RequestPayload InserirSolicitacao inserirSolicitacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = InserirSolicitacaoAssembler.toMap(inserirSolicitacao);

        AcessoSistema responsavel = null;
        final InserirSolicitacaoResponse resposta = new InserirSolicitacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(inserirSolicitacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV6(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "liquidarConsignacao")
    @ResponsePayload
    public LiquidarConsignacaoResponse liquidarConsignacao(@RequestPayload LiquidarConsignacao liquidarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = LiquidarConsignacaoAssembler.toMap(liquidarConsignacao);

        AcessoSistema responsavel = null;
        final LiquidarConsignacaoResponse resposta = new LiquidarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(liquidarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LIQUIDAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LIQUIDAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reativarConsignacao")
    @ResponsePayload
    public ReativarConsignacaoResponse reativarConsignacao(@RequestPayload ReativarConsignacao reativarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ReativarConsignacaoAssembler.toMap(reativarConsignacao);

        AcessoSistema responsavel = null;
        final ReativarConsignacaoResponse resposta = new ReativarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(reativarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reativar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "renegociarConsignacao")
    @ResponsePayload
    public RenegociarConsignacaoResponse renegociarConsignacao(@RequestPayload RenegociarConsignacao renegociarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = RenegociarConsignacaoAssembler.toMap(renegociarConsignacao);

        AcessoSistema responsavel = null;
        final RenegociarConsignacaoResponse resposta = new RenegociarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(renegociarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_RENEGOCIAR_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_RENEGOCIAR_CONSIGNACAO);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.renegociar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reservarMargem")
    @ResponsePayload
    public ReservarMargemResponse reservarMargem(@RequestPayload ReservarMargem reservarMargem) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ReservarMargemAssembler.toMap(reservarMargem);

        AcessoSistema responsavel = null;
        final ReservarMargemResponse resposta = new ReservarMargemResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin((String) parametros.get(USUARIO), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV6(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reservar.margem.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "suspenderConsignacao")
    @ResponsePayload
    public SuspenderConsignacaoResponse suspenderConsignacao(@RequestPayload SuspenderConsignacao suspenderConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = SuspenderConsignacaoAssembler.toMap(suspenderConsignacao);

        AcessoSistema responsavel = null;
        final SuspenderConsignacaoResponse resposta = new SuspenderConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(suspenderConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_SUSPENDER_CONSIGNACAO_V6_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_SUSPENDER_CONSIGNACAO_V6_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV6(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV6(paramResposta));
                    } else if (SERVIDOR_V4_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV6(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV6(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.suspender.consignacao.arg0", responsavel, e.getMessage()));
            }
       }
    }
}