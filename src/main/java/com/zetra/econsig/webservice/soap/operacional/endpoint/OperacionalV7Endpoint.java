package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.ANEXO_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_ID;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V7_0;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AlterarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AnexoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CadastrarUsuarioAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DesliquidarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.DownloadAnexosConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.EditarStatusServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.EditarStatusUsuarioAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.IncluirAnexoConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ValidarDadosBancariosServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.AlterarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v7.AlterarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.CadastrarUsuario;
import com.zetra.econsig.webservice.soap.operacional.v7.CadastrarUsuarioResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.ConsultarMargem;
import com.zetra.econsig.webservice.soap.operacional.v7.ConsultarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.DesliquidarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v7.DesliquidarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.DownloadAnexosConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v7.DownloadAnexosConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusServidor;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusServidorResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusUsuario;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusUsuarioResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.IncluirAnexoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v7.IncluirAnexoConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v7.Servico;
import com.zetra.econsig.webservice.soap.operacional.v7.ValidarDadosBancariosServidor;
import com.zetra.econsig.webservice.soap.operacional.v7.ValidarDadosBancariosServidorResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 7.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV7Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV7Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v7_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
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
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV7(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV7(paramResposta));
                   } else if (RESUMO.equals(nomeReg)) {
                       resposta.getResumos().add(ResumoAssembler.toResumoV7(paramResposta));
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
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.alterar.consignacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarUsuario")
    @ResponsePayload
    public CadastrarUsuarioResponse cadastrarUsuario(@RequestPayload CadastrarUsuario cadastrarUsuario) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = CadastrarUsuarioAssembler.toMap(cadastrarUsuario);

        AcessoSistema responsavel = null;
        final CadastrarUsuarioResponse resposta = new CadastrarUsuarioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarUsuario.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_USUARIO_OPERACIONAL);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                    if (sucesso) {
                        resposta.setEntidadeCodigo(factory.createCadastrarUsuarioResponseEntidadeCodigo((String) paramResposta.get(ENTIDADE_ID)));
                        resposta.setEntidadeNome(factory.createCadastrarUsuarioResponseEntidadeNome((String) paramResposta.get(ENTIDADE_NOME)));
                        resposta.setNomeUsuario(factory.createCadastrarUsuarioResponseNomeUsuario((String) paramResposta.get(USU_NOME)));
                        resposta.setLoginUsuario(factory.createCadastrarUsuarioResponseLoginUsuario((String) paramResposta.get(USU_LOGIN)));
                        if (!TextHelper.isNull(paramResposta.get(USU_SENHA))) {
                            resposta.setSenhaUsuario(factory.createCadastrarUsuarioResponseSenhaUsuario((String) paramResposta.get(USU_SENHA)));
                        }
                    }
                } else {
                    paramResposta.put(OPERACAO, CodedValues.OP_CADASTRAR_USUARIO_OPERACIONAL);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.usuario.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V7_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V7_0);

                    if (SERVIDOR_V7_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV7(paramResposta, responsavel));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV7(paramResposta));
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
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "desliquidarConsignacao")
    @ResponsePayload
    public DesliquidarConsignacaoResponse desliquidarConsignacao(@RequestPayload DesliquidarConsignacao desliquidarConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = DesliquidarConsignacaoAssembler.toMap(desliquidarConsignacao);

        AcessoSistema responsavel = null;
        final DesliquidarConsignacaoResponse resposta = new DesliquidarConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(desliquidarConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_DESLIQUIDAR_CONTRATO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DESLIQUIDAR_CONTRATO);
                    if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg) || BOLETO_V6_0.equals(nomeReg) || CONSIGNACAO_V6_0.equals(nomeReg)) {
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV7(paramResposta, responsavel)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV7(paramResposta));
                   } else if (RESUMO.equals(nomeReg)) {
                       resposta.getResumos().add(ResumoAssembler.toResumoV7(paramResposta));
                   } else if (SERVICO.equals(nomeReg)) {
                       final Servico servico = new Servico();
                       // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
                       BeanUtils.copyProperties(servico, ServicoAssembler.toServicoV1(paramResposta));
                       resposta.getServicos().add(servico);
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
        } catch (final NumberFormatException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.consignacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "downloadAnexosConsignacao")
    @ResponsePayload
    public DownloadAnexosConsignacaoResponse downloadAnexosConsignacao(@RequestPayload DownloadAnexosConsignacao downloadAnexosConsignacao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = DownloadAnexosConsignacaoAssembler.toMap(downloadAnexosConsignacao);

        AcessoSistema responsavel = null;
        final DownloadAnexosConsignacaoResponse resposta = new DownloadAnexosConsignacaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(downloadAnexosConsignacao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_DOWNLOAD_ANEXOS_CONSIGNACAO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_DOWNLOAD_ANEXOS_CONSIGNACAO);
                    if (ANEXO_CONSIGNACAO.equals(nomeReg)) {
                        resposta.getAnexos().add(AnexoConsignacaoAssembler.toAnexoConsignacaoV7(paramResposta, responsavel));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editarStatusServidor")
    @ResponsePayload
    public EditarStatusServidorResponse editarStatusServidor(@RequestPayload EditarStatusServidor editarStatusServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = EditarStatusServidorAssembler.toMap(editarStatusServidor);

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
            parametros.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_SERVIDOR);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_SERVIDOR);
                    if (SERVIDOR_V7_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV7(paramResposta, responsavel));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editarStatusUsuario")
    @ResponsePayload
    public EditarStatusUsuarioResponse editarStatusUsuario(@RequestPayload EditarStatusUsuario editarStatusUsuario) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = EditarStatusUsuarioAssembler.toMap(editarStatusUsuario);

        AcessoSistema responsavel = null;
        final EditarStatusUsuarioResponse resposta = new EditarStatusUsuarioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(editarStatusUsuario.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_USUARIO);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_EDITAR_STATUS_USUARIO);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.editar.status.usuario.arg0", responsavel, e.getMessage()));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "validarDadosBancariosServidor")
    @ResponsePayload
    public ValidarDadosBancariosServidorResponse validarDadosBancariosServidor(@RequestPayload ValidarDadosBancariosServidor validarDadosBancariosServidor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ValidarDadosBancariosServidorAssembler.toMap(validarDadosBancariosServidor);

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
            parametros.put(OPERACAO, CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER);
                    if (SERVIDOR_V7_0.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV7(paramResposta, responsavel));
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
}