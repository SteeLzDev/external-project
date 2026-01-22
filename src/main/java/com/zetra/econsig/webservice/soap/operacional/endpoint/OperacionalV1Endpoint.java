package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SOLICITACAO;
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

import java.text.ParseException;
import java.util.Date;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.DadoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ParametroSetAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.SimulacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.SolicitacaoAssembler;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarParametrosAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DetalharConsultaConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.IncluirAnexoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.IncluirDadoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.InserirSolicitacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.LiquidarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ListarDadoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ListarSolicitacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReativarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.RenegociarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReservarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.SimularConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.SuspenderConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.AlongarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.AlongarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.AlterarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.AlterarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.AutorizarReserva;
import com.zetra.econsig.webservice.soap.operacional.v1.AutorizarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarRenegociacao;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarRenegociacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarReserva;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarReserva;
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarReservaResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarMargem;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarParametros;
import com.zetra.econsig.webservice.soap.operacional.v1.ConsultarParametrosResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.DetalharConsultaConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.IncluirAnexoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.IncluirAnexoConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.IncluirDadoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.IncluirDadoConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.InfoMargem;
import com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.LiquidarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.LiquidarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarDadoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarDadoConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v1.ReativarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.ReativarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.RenegociarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.RenegociarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.ReservarMargem;
import com.zetra.econsig.webservice.soap.operacional.v1.ReservarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.SimularConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.SimularConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v1.SuspenderConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v1.SuspenderConsignacaoResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV1Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV1Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v1_0";

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

            parametros.put(OPERACAO, CodedValues.OP_ALONGAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_ALONGAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            parametros.put(OPERACAO, CodedValues.OP_ALTERAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_ALTERAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            parametros.put(OPERACAO, CodedValues.OP_AUTORIZAR_RESERVA);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_AUTORIZAR_RESERVA);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.consignacao.arg0", responsavel, e.getMessage()));
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

            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_RENEGOCIACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_RENEGOCIACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.renegociacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_RESERVA);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CANCELAR_RESERVA);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cancelar.reserva.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_CONFIRMAR_RESERVA);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONFIRMAR_RESERVA);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            parametros.put(OPERACAO, CodedValues.OP_CONFIRMAR_SOLICITACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONFIRMAR_SOLICITACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.confirmar.solicitacao.arg0", responsavel, e.getMessage()));
            }
        }
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.consignacao.arg0", responsavel, e.getMessage()));
            }
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM);
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
                        @SuppressWarnings("unchecked")
                        final List<Map<CamposAPI, Object>> lstInfoMargem = (List<Map<CamposAPI, Object>>) paramResposta.get(INFO_MARGEM);
                        if ((lstInfoMargem != null) && !lstInfoMargem.isEmpty()) {
                            for (final Map<CamposAPI, Object> map : lstInfoMargem) {
                                addInfoMargemConsultaMargem(map, resposta, responsavel);
                            }
                        } else {
                            addInfoMargemConsultaMargem(paramResposta, resposta, responsavel);
                        }
                    }
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM);
                    if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException | ParseException e) {
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
        final Object dataNasc = paramResposta.get(DATA_NASCIMENTO);
        final Object dataAdmissao = paramResposta.get(RSE_DATA_ADMISSAO);
        try {
            if (!TextHelper.isNull(dataNasc)) {
                if (dataNasc instanceof String) {
                    info.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataNasc.toString(), LocaleHelper.getDatePattern()), false));
                } else if (dataNasc instanceof final Date dataNascAsDate) {
                    info.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(dataNascAsDate, false));
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
        }

        try {
            if (!TextHelper.isNull(dataAdmissao)) {
                if (dataAdmissao instanceof String) {
                    info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(dataAdmissao.toString(), LocaleHelper.getDatePattern()), false));
                } else if (dataAdmissao instanceof final Date dataAdmissaoAsDate) {
                    info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(dataAdmissaoAsDate, false));
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
        }

        if (!TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
            info.setPrazoServidor(Integer.valueOf(paramResposta.get(RSE_PRAZO).toString()));
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

        consultarMargemResp.getInfoMargem().add(info);
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

            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS);

                    if (PARAMETRO_SET.equals(nomeReg)) {
                        resposta.setParametroSet(factory.createConsultarParametrosResponseParametroSet(ParametroSetAssembler.toParametroSetV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.parametros.arg0", responsavel, e.getMessage()));
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
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.consulta.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "incluirAnexoConsignacao")
    @ResponsePayload
    public IncluirAnexoConsignacaoResponse incluirAnexoConsignacao(@RequestPayload IncluirAnexoConsignacao incluirAnexoConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = IncluirAnexoConsignacaoAssembler.toMap(incluirAnexoConsignacao);

        AcessoSistema responsavel = null;
        final IncluirAnexoConsignacaoResponse resposta = new IncluirAnexoConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(incluirAnexoConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_INCLUIR_ANEXO_CONSIGNACAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.incluir.anexo.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "incluirDadoConsignacao")
    @ResponsePayload
    public IncluirDadoConsignacaoResponse incluirDadoConsignacao(@RequestPayload IncluirDadoConsignacao incluirDadoConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = IncluirDadoConsignacaoAssembler.toMap(incluirDadoConsignacao);

        AcessoSistema responsavel = null;
        final IncluirDadoConsignacaoResponse resposta = new IncluirDadoConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(incluirDadoConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_INCLUIR_DADO_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_INCLUIR_DADO_CONSIGNACAO);

                    if (DADOS_CONSIGNACAO.equals(nomeReg)) {
                        resposta.getDados().add(DadoConsignacaoAssembler.toDadoConsignacaoV1(paramResposta));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.suspender.consignacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
            parametros.put(OPERACAO, CodedValues.OP_LIQUIDAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LIQUIDAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarDadoConsignacao")
    @ResponsePayload
    public ListarDadoConsignacaoResponse listarDadoConsignacao(@RequestPayload ListarDadoConsignacao listarDadoConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ListarDadoConsignacaoAssembler.toMap(listarDadoConsignacao);

        AcessoSistema responsavel = null;
        final ListarDadoConsignacaoResponse resposta = new ListarDadoConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(listarDadoConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LISTAR_DADO_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LISTAR_DADO_CONSIGNACAO);

                    if (DADOS_CONSIGNACAO.equals(nomeReg)) {
                        resposta.getDados().add(DadoConsignacaoAssembler.toDadoConsignacaoV1(paramResposta));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.suspender.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarSolicitacao")
    @ResponsePayload
    public ListarSolicitacaoResponse listarSolicitacao(@RequestPayload ListarSolicitacao listarSolicitacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ListarSolicitacaoAssembler.toMap(listarSolicitacao);

        AcessoSistema responsavel = null;
        final ListarSolicitacaoResponse resposta = new ListarSolicitacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(listarSolicitacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LISTA_SOLICITACOES);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LISTA_SOLICITACOES);
                    if (SOLICITACAO.equals(nomeReg)) {
                        resposta.getSolicitacoes().add(SolicitacaoAssembler.toSolicitacaoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            parametros.put(OPERACAO, CodedValues.OP_RENEGOCIAR_CONSIGNACAO);
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
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV1(paramResposta));
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
            parametros.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "simularConsignacao")
    @ResponsePayload
    public SimularConsignacaoResponse simularConsignacao(@RequestPayload SimularConsignacao simularConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = SimularConsignacaoAssembler.toMap(simularConsignacao);

        AcessoSistema responsavel = null;
        final SimularConsignacaoResponse resposta = new SimularConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(simularConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO);
                    if (SIMULACAO.equals(nomeReg)) {
                        resposta.getSimulacoes().add(SimulacaoAssembler.toSimulacaoV1(paramResposta));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_SUSPENDER_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_SUSPENDER_CONSIGNACAO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
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
            } else {
                LOG.error(e.getMessage(), e);
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.suspender.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }
}