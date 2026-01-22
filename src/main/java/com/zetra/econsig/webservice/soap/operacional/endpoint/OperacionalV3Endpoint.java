package com.zetra.econsig.webservice.soap.operacional.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
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
import com.zetra.econsig.webservice.soap.operacional.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ParcelaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.AlterarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.CadastrarTaxaDeJurosAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ConsultarParcelaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.LiquidarConsignacaoAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.LiquidarParcelaAssembler;
import com.zetra.econsig.webservice.soap.operacional.assembler.operation.ReservarMargemAssembler;
import com.zetra.econsig.webservice.soap.operacional.v3.AlterarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v3.AlterarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.CadastrarTaxaDeJuros;
import com.zetra.econsig.webservice.soap.operacional.v3.CadastrarTaxaDeJurosResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.ConsultarMargem;
import com.zetra.econsig.webservice.soap.operacional.v3.ConsultarMargemResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.ConsultarParcela;
import com.zetra.econsig.webservice.soap.operacional.v3.ConsultarParcelaResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.InfoMargem;
import com.zetra.econsig.webservice.soap.operacional.v3.LiquidarConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v3.LiquidarConsignacaoResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.LiquidarParcela;
import com.zetra.econsig.webservice.soap.operacional.v3.LiquidarParcelaResponse;
import com.zetra.econsig.webservice.soap.operacional.v3.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem;
import com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargemResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: HostaHostEndpoint</p>
 * <p>Description: Endpoint SOAP para o serviço HostaHost versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class OperacionalV3Endpoint extends OperacionalEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OperacionalV3Endpoint.class);

    private static final String NAMESPACE_URI = "HostaHostService-v3_0";

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
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV3(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV3(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV3(paramResposta));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarTaxaDeJuros")
    @ResponsePayload
    public CadastrarTaxaDeJurosResponse cadastrarTaxaDeJuros(@RequestPayload CadastrarTaxaDeJuros cadastrarTaxaDeJuros) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = CadastrarTaxaDeJurosAssembler.toMap(cadastrarTaxaDeJuros);

        AcessoSistema responsavel = null;
        final CadastrarTaxaDeJurosResponse resposta = new CadastrarTaxaDeJurosResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarTaxaDeJuros.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_TAXA_JUROS);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CADASTRAR_TAXA_JUROS);
                    if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV3(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.taxa.juros.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.taxa.juros.arg0", responsavel, e.getMessage()));
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
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V3_0);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_MARGEM_V3_0);

                    if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV3(paramResposta));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV3(paramResposta));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarParcela")
    @ResponsePayload
    public ConsultarParcelaResponse consultarParcela(@RequestPayload ConsultarParcela consultarParcela) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ConsultarParcelaAssembler.toMap(consultarParcela);

        AcessoSistema responsavel = null;
        final ConsultarParcelaResponse resposta = new ConsultarParcelaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarParcela.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PARCELA);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_CONSULTAR_PARCELA);
                    if (PARCELA.equals(nomeReg)) {
                        resposta.getParcelas().add(ParcelaAssembler.toParcelaV3(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV3(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.parcela.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.parcela.arg0", responsavel, e.getMessage()));
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
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV3(paramResposta)));
                    } else if (HISTORICO.equals(nomeReg)) {
                        resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV3(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV3(paramResposta));
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
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.consignacao.arg0", responsavel, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "liquidarParcela")
    @ResponsePayload
    public LiquidarParcelaResponse liquidarParcela(@RequestPayload LiquidarParcela liquidarParcela) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = LiquidarParcelaAssembler.toMap(liquidarParcela);

        AcessoSistema responsavel = null;
        final LiquidarParcelaResponse resposta = new LiquidarParcelaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(liquidarParcela.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LIQUIDAR_PARCELA);
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
                    paramResposta.put(OPERACAO, CodedValues.OP_LIQUIDAR_PARCELA);
                    if (PARCELA.equals(nomeReg)) {
                        resposta.getParcelas().add(ParcelaAssembler.toParcelaV3(paramResposta));
                    } else if (RESUMO.equals(nomeReg)) {
                        resposta.getResumos().add(ResumoAssembler.toResumoV3(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.parcela.arg0", responsavel, e.getMessage()));
            }
        } catch (final NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.liquidar.parcela.arg0", responsavel, e.getMessage()));
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
                        resposta.setBoleto(factory.createConsultarConsignacaoResponseBoleto(BoletoAssembler.toBoletoV3(paramResposta)));
                    } else if (SERVICO.equals(nomeReg)) {
                        resposta.getServicos().add(ServicoAssembler.toServicoV3(paramResposta));
                    } else if (SERVIDOR.equals(nomeReg)) {
                        resposta.getServidores().add(ServidorAssembler.toServidorV3(paramResposta));
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
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.reservar.margem.arg0", responsavel, e.getMessage()));
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
                } else if (dataAdmissao instanceof final Date value) {
                    info.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(value, false));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }

        if (!TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
            info.setPrazoServidor(Integer.valueOf(paramResposta.get(RSE_PRAZO).toString()));
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
}