package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SISTEMA;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.PAGINACAO;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.PERFIL_CONSIGNADO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V8_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALIDA_DOCUMENTACAO;
import static com.zetra.econsig.webservice.CamposAPI.RCO_CAMPO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.RCO_CAMPO_VALOR;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarPerfilConsignadoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarRegrasAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarPerfilConsignado;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarPerfilConsignadoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarRegras;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarRegrasResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.PerfilConsignado;
import com.zetra.econsig.webservice.soap.operacional.v8.Servidor;
import org.apache.commons.beanutils.BeanUtils;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.PaginacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ParametroSetAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ParcelaConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.SimulacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CadastrarServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarParametrosAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarValidacaoDocumentacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DetalharConsultaConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.EditarSaldoDevedorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.EditarStatusServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.IncluirAnexoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.InserirSolicitacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ListarParcelasAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ListarSolicitacaoSaldoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.PesquisarServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReativarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReservarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.SimularConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ValidarDadosBancariosServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarParametros;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarParametrosResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.CadastrarServidor;
import com.zetra.econsig.webservice.soap.operacional.v8.CadastrarServidorResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarMargem;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarValidacaoDocumentacao;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarValidacaoDocumentacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.DetalharConsultaConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v8.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.EditarSaldoDevedor;
import com.zetra.econsig.webservice.soap.operacional.v8.EditarSaldoDevedorResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.EditarStatusServidor;
import com.zetra.econsig.webservice.soap.operacional.v8.EditarStatusServidorResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.IncluirAnexoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v8.IncluirAnexoConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.InserirSolicitacao;
import com.zetra.econsig.webservice.soap.operacional.v8.InserirSolicitacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarParcelas;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarParcelasResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarSolicitacaoSaldo;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarSolicitacaoSaldoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v8.PesquisarServidor;
import com.zetra.econsig.webservice.soap.operacional.v8.PesquisarServidorResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ReativarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v8.ReativarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.RegraConvenio;
import com.zetra.econsig.webservice.soap.operacional.v8.ReservarMargem;
import com.zetra.econsig.webservice.soap.operacional.v8.ReservarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.SimularConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v8.SimularConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v8.ValidarDadosBancariosServidor;
import com.zetra.econsig.webservice.soap.operacional.v8.ValidarDadosBancariosServidorResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 8.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV8Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV8Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v8_0";

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
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_SERVIDOR_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR_V8_0);
                    if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.setServidor(factory.createCadastrarServidorResponseServidor(ServidorAssembler.toServidorV8(paramResposta, responsavel)));
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0);

                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV8(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV8(paramResposta));
                    } else if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.consignacao.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V8_0);

                    if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "detalharConsultaConsignacao")
    @ResponsePayload
    public DetalharConsultaConsignacaoResponse detalharConsultaConsignacao(@RequestPayload DetalharConsultaConsignacao detalharConsultaConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao detalharConsultaConsignacaoV4 = new com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao();
        try {
            final com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory factoryV4 = new com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory();
            final com.zetra.econsig.webservice.soap.operacional.v4.SituacaoContrato situacaoContratoV4 = new com.zetra.econsig.webservice.soap.operacional.v4.SituacaoContrato();
            if ((detalharConsultaConsignacao.getSituacaoContrato() != null) && (detalharConsultaConsignacao.getSituacaoContrato().getValue() != null)) {
                BeanUtils.copyProperties(situacaoContratoV4, detalharConsultaConsignacao.getSituacaoContrato());
            }
            BeanUtils.copyProperties(detalharConsultaConsignacaoV4, detalharConsultaConsignacao);
            detalharConsultaConsignacaoV4.setSituacaoContrato(factoryV4.createDetalharConsultaConsignacaoSituacaoContrato(situacaoContratoV4));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.consulta.consignacao.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = DetalharConsultaConsignacaoAssembler.toMap(detalharConsultaConsignacaoV4);

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
            parametros.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DETALHAR_CONSULTA_ADE_V8_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV8(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV8(paramResposta));
                    } else if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.consulta.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editarStatusServidor")
    @ResponsePayload
    public EditarStatusServidorResponse editarStatusServidor(@RequestPayload EditarStatusServidor editarStatusServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusServidor editarStatusServidorV7 = new com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusServidor();
        try {
            final com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory factoryV7 = new com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory();
            final com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor situacaoV7 = new com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor();
            if ((editarStatusServidor.getSituacao() != null) && (editarStatusServidor.getSituacao().getValue() != null)) {
                BeanUtils.copyProperties(situacaoV7, editarStatusServidor.getSituacao().getValue());
            }
            BeanUtils.copyProperties(editarStatusServidorV7, editarStatusServidor);
            editarStatusServidorV7.setSituacao(factoryV7.createEditarStatusServidorSituacao(situacaoV7));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.servidor.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = EditarStatusServidorAssembler.toMap(editarStatusServidorV7);

        AcessoSistema responsavel = null;
        final EditarStatusServidorResponse resposta = new EditarStatusServidorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(editarStatusServidor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0);
                    if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.incluir.anexo.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserirSolicitacao")
    @ResponsePayload
    public InserirSolicitacaoResponse inserirSolicitacao(@RequestPayload InserirSolicitacao inserirSolicitacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao inserirSolicitacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao();
        try {
            BeanUtils.copyProperties(inserirSolicitacaoV1, inserirSolicitacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = InserirSolicitacaoAssembler.toMap(inserirSolicitacaoV1);

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
            parametros.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_INSERIR_SOLICITACAO_V8_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV8(paramResposta, responsavel)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV8(paramResposta));
                    } else if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.inserir.solicitacao.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR_V8_0);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            int countSer = 0;
            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
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
                    paramResposta.put(OPERACAO, CodedValues.OP_PESQUISAR_SERVIDOR_V8_0);

                    if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.pesquisar.servidor.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reservarMargem")
    @ResponsePayload
    public ReservarMargemResponse reservarMargem(@RequestPayload ReservarMargem reservarMargem) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem reservarMargemV3 = new com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem();
        try {
            BeanUtils.copyProperties(reservarMargemV3, reservarMargem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reservar.margem.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = ReservarMargemAssembler.toMap(reservarMargemV3);

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
            parametros.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_RESERVAR_MARGEM_V8_0);
                    if (BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV8(paramResposta, responsavel)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV8(paramResposta));
                    } else if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reservar.margem.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "simularConsignacao")
    @ResponsePayload
    public SimularConsignacaoResponse simularConsignacao(@RequestPayload SimularConsignacao simularConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final com.zetra.econsig.webservice.soap.operacional.v1.SimularConsignacao simularConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.SimularConsignacao();
        try {
            BeanUtils.copyProperties(simularConsignacaoV1, simularConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = SimularConsignacaoAssembler.toMap(simularConsignacaoV1);

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
            parametros.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0);
                    if (SIMULACAO.equals(nomeReg)) {
                        resposta.getSimulacoes().add(SimulacaoAssembler.toSimulacaoV8(paramResposta));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV8(paramResposta));
                    } else if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "validarDadosBancariosServidor")
    @ResponsePayload
    public ValidarDadosBancariosServidorResponse validarDadosBancariosServidor(@RequestPayload ValidarDadosBancariosServidor validarDadosBancariosServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final com.zetra.econsig.webservice.soap.operacional.v7.ValidarDadosBancariosServidor validarDadosBancariosServidorV7 = new com.zetra.econsig.webservice.soap.operacional.v7.ValidarDadosBancariosServidor();
        try {
            BeanUtils.copyProperties(validarDadosBancariosServidorV7, validarDadosBancariosServidor);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.simular.consignacao.arg0", null, e.getMessage()));
        }
        final Map<CamposAPI, Object> parametros = ValidarDadosBancariosServidorAssembler.toMap(validarDadosBancariosServidorV7);

        AcessoSistema responsavel = null;
        final ValidarDadosBancariosServidorResponse resposta = new ValidarDadosBancariosServidorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(validarDadosBancariosServidor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0);
                    if (SERVIDOR_V8_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV8(paramResposta, responsavel));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarSolicitacaoSaldo")
    @ResponsePayload
    public ListarSolicitacaoSaldoResponse listarSolicitacaoSaldo(@RequestPayload ListarSolicitacaoSaldo listarSolicitacaoSaldo) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ListarSolicitacaoSaldoAssembler.toMap(listarSolicitacaoSaldo);

        AcessoSistema responsavel = null;
        final ListarSolicitacaoSaldoResponse resposta = new ListarSolicitacaoSaldoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(listarSolicitacaoSaldo.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LISTAR_SOLICITACAO_SALDO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LISTAR_SOLICITACAO_SALDO);
                    if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.saldo.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editarSaldoDevedor")
    @ResponsePayload
    public EditarSaldoDevedorResponse editarSaldoDevedor(@RequestPayload EditarSaldoDevedor editarSaldoDevedor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = EditarSaldoDevedorAssembler.toMap(editarSaldoDevedor);

        AcessoSistema responsavel = null;
        final EditarSaldoDevedorResponse resposta = new EditarSaldoDevedorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(editarSaldoDevedor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_EDITAR_SALDO_DEVEDOR);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_EDITAR_SALDO_DEVEDOR);
                    if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.saldo.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarParcelas")
    @ResponsePayload
    public ListarParcelasResponse listarParcelas(@RequestPayload ListarParcelas listarParcelas) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ListarParcelasAssembler.toMap(listarParcelas);

        AcessoSistema responsavel = null;
        final ListarParcelasResponse resposta = new ListarParcelasResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(listarParcelas.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LISTAR_PARCELAS);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                } else if (PAGINACAO.equals(nomeReg)) {
                    resposta.getPaginacao().add(PaginacaoAssembler.toPaginacaoV8(paramResposta));
                } else if (PARCELA_CONSIGNACAO.equals(nomeReg)) {
                    resposta.getParcelaConsignacao().add(ParcelaConsignacaoAssembler.toParcelaConsignacaoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.saldo.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO_V8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_REATIVAR_CONSIGNACAO_V8_0);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV8(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV8(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reativar.consignacao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarValidacaoDocumentacao")
    @ResponsePayload
    public ConsultarValidacaoDocumentacaoResponse consultarValidacaoDocumentacao(@RequestPayload ConsultarValidacaoDocumentacao consultarValidacaoDocumentacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ConsultarValidacaoDocumentacaoAssembler.toMap(consultarValidacaoDocumentacao);

        AcessoSistema responsavel = null;
        final ConsultarValidacaoDocumentacaoResponse resposta = new ConsultarValidacaoDocumentacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarValidacaoDocumentacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_VALIDACAO_DOCUMENTACAO_v8_0);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                } else if (PAGINACAO.equals(nomeReg)) {
                    resposta.getPaginacao().add(PaginacaoAssembler.toPaginacaoV8(paramResposta));
                } else if (VALIDA_DOCUMENTACAO.equals(nomeReg)) {
                    resposta.getValidarDocumentacao().add(ConsultarValidacaoDocumentacaoAssembler.toValidarDocumentacaoV8(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.saldo.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarPerfilConsignado")
    @ResponsePayload
    public ConsultarPerfilConsignadoResponse consultarPerfilConsignado(@RequestPayload ConsultarPerfilConsignado consultarPerfilConsignado) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ConsultarPerfilConsignadoAssembler.toMap(consultarPerfilConsignado);

        AcessoSistema responsavel = null;
        final ConsultarPerfilConsignadoResponse resposta = new ConsultarPerfilConsignadoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarPerfilConsignado.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nome = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nome)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                } else if (SERVIDOR.equals(nome)) {
                    Servidor servidor = new Servidor();
                    servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
                    servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
                    servidor.setServidor((String) paramResposta.get(SERVIDOR));
                    servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
                    servidor.setCpf((String) paramResposta.get(SER_CPF));
                    servidor.setOrgao((String) paramResposta.get(ORGAO));
                    servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
                    resposta.getServidores().add(servidor);
                } else {
                    PerfilConsignado perfilConsignado = new PerfilConsignado();
                    perfilConsignado.setServidor((String) paramResposta.get(SERVIDOR));
                    perfilConsignado.setCpf((String) paramResposta.get(SER_CPF));
                    perfilConsignado.setEstabelecimento((String) paramResposta.get(EST_NOME));
                    perfilConsignado.setEstabelecimentoCodigo((String) paramResposta.get(EST_CODIGO));
                    perfilConsignado.setOrgao((String) paramResposta.get(ORG_NOME));
                    perfilConsignado.setOrgaoCodigo((String) paramResposta.get(ORG_CODIGO));
                    perfilConsignado.setMatricula((String) paramResposta.get(RSE_MATRICULA));
                    perfilConsignado.setPerfil(paramResposta.get(PERFIL_CONSIGNADO) != null ? paramResposta.get(PERFIL_CONSIGNADO).toString() : "");
                    resposta.getPerfilConsignado().add(perfilConsignado);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.solicitacao.saldo.arg0", responsavel, e.getMessage()));
            }
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarRegras")
    @ResponsePayload
    public ConsultarRegrasResponse consultarRegras(@RequestPayload ConsultarRegras consultarRegras) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = ConsultarRegrasAssembler.toMap(consultarRegras);

        AcessoSistema responsavel = null;
        final ConsultarRegrasResponse resposta = new ConsultarRegrasResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarRegras.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_REGRAS);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nome = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nome)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                } else {
                    RegraConvenio regra = new RegraConvenio();
                    regra.setDescricao((String) paramResposta.get(RCO_CAMPO_NOME));
                    regra.setValor((String) paramResposta.get(RCO_CAMPO_VALOR));
                    
                    resposta.getRegra().add(regra);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.regras.arg0", responsavel, e.getMessage()));
            }
        }
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

            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS_v8_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_PARAMETROS_v8_0);
                    
                    if (PARAMETRO_SET.equals(nomeReg)) {
                        resposta.setParametroSet(factory.createConsultarParametrosResponseParametroSet(ParametroSetAssembler.toParametroSetV8(paramResposta)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV8(paramResposta));
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
