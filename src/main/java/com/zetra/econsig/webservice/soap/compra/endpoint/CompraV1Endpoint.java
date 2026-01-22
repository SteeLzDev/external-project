package com.zetra.econsig.webservice.soap.compra.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.INFO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
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
import com.zetra.econsig.webservice.soap.compra.assembler.BoletoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.HistoricoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.InfoCompraAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.ResumoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.AcompanharCompraContratoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.CancelarCompraAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.ComprarContratoAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.ConsultarConsignacaoParaCompraAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.InformarPagamentoSaldoDevedorAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.InformarSaldoDevedorAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.LiquidarCompraAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.RejeitarPgSaldoDevedorAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.RetirarContratoDaCompraAssembler;
import com.zetra.econsig.webservice.soap.compra.assembler.operation.SolicitarRecalculoSaldoDevedorAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.AcompanharCompraContrato;
import com.zetra.econsig.webservice.soap.compra.v1.AcompanharCompraContratoResponse;
import com.zetra.econsig.webservice.soap.compra.v1.CancelarCompra;
import com.zetra.econsig.webservice.soap.compra.v1.CancelarCompraResponse;
import com.zetra.econsig.webservice.soap.compra.v1.ComprarContrato;
import com.zetra.econsig.webservice.soap.compra.v1.ComprarContratoResponse;
import com.zetra.econsig.webservice.soap.compra.v1.ConsultarConsignacaoParaCompra;
import com.zetra.econsig.webservice.soap.compra.v1.ConsultarConsignacaoParaCompraResponse;
import com.zetra.econsig.webservice.soap.compra.v1.InformarPagamentoSaldoDevedor;
import com.zetra.econsig.webservice.soap.compra.v1.InformarPagamentoSaldoDevedorResponse;
import com.zetra.econsig.webservice.soap.compra.v1.InformarSaldoDevedor;
import com.zetra.econsig.webservice.soap.compra.v1.InformarSaldoDevedorResponse;
import com.zetra.econsig.webservice.soap.compra.v1.LiquidarCompra;
import com.zetra.econsig.webservice.soap.compra.v1.LiquidarCompraResponse;
import com.zetra.econsig.webservice.soap.compra.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.compra.v1.RejeitarPgSaldoDevedor;
import com.zetra.econsig.webservice.soap.compra.v1.RejeitarPgSaldoDevedorResponse;
import com.zetra.econsig.webservice.soap.compra.v1.RetirarContratoDaCompra;
import com.zetra.econsig.webservice.soap.compra.v1.RetirarContratoDaCompraResponse;
import com.zetra.econsig.webservice.soap.compra.v1.SolicitarRecalculoSaldoDevedor;
import com.zetra.econsig.webservice.soap.compra.v1.SolicitarRecalculoSaldoDevedorResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: CompraV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Compra versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class CompraV1Endpoint extends CompraEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CompraV1Endpoint.class);

    private static final String NAMESPACE_URI = "CompraService-v1_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "acompanharCompraContrato")
    @ResponsePayload
    public AcompanharCompraContratoResponse acompanharCompraContrato(@RequestPayload AcompanharCompraContrato acompanharCompraContrato) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = AcompanharCompraContratoAssembler.toMap(acompanharCompraContrato);

        AcessoSistema responsavel = null;

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(acompanharCompraContrato.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            final AcompanharCompraContratoResponse resposta = new AcompanharCompraContratoResponse();

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_ACOMPANHAR_COMPRA);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            final AcompanharCompraContratoResponse resposta = new AcompanharCompraContratoResponse();

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (INFO_COMPRAS.equals(nomeReg)) {
                    resposta.getInfoCompras().add(InfoCompraAssembler.toInfoCompraV1(paramResposta));
                }
            }

            return resposta;
        } catch (final ZetraException e) {
            if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioNaoTemPermissao".equals(e.getMessageKey())) {
                final AcompanharCompraContratoResponse resposta = new AcompanharCompraContratoResponse();

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarCompra")
    @ResponsePayload
    public CancelarCompraResponse cancelarCompra(@RequestPayload CancelarCompra cancelarCompra) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CancelarCompraAssembler.toMap(cancelarCompra);

        AcessoSistema responsavel = null;
        final CancelarCompraResponse resposta = new CancelarCompraResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cancelarCompra.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CANCELAR_CONTRATO_COMPRA);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.cancelar.compra.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "comprarContrato")
    @ResponsePayload
    public ComprarContratoResponse comprarContrato(@RequestPayload ComprarContrato comprarContrato) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ComprarContratoAssembler.toMap(comprarContrato);

        AcessoSistema responsavel = null;
        final ComprarContratoResponse resposta = new ComprarContratoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(comprarContrato.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_COMPRAR_CONTRATO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                    resposta.setBoleto(factory.createComprarContratoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                } else if (HISTORICO.equals(nomeReg)) {
                    resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                } else if (RESUMO.equals(nomeReg)) {
                    resposta.getResumos().add(ResumoAssembler.toResumoV1(paramResposta));
                } else if (SERVICO.equals(nomeReg)) {
                    resposta.getServicos().add(ServicoAssembler.toServicoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacar.comprar.contrato.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarConsignacaoParaCompra")
    @ResponsePayload
    public ConsultarConsignacaoParaCompraResponse consultarConsignacaoParaCompra(@RequestPayload ConsultarConsignacaoParaCompra consultarConsignacaoParaCompra) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ConsultarConsignacaoParaCompraAssembler.toMap(consultarConsignacaoParaCompra);

        AcessoSistema responsavel = null;
        final ConsultarConsignacaoParaCompraResponse resposta = new ConsultarConsignacaoParaCompraResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarConsignacaoParaCompra.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (BOLETO.equals(nomeReg) || CONSIGNACAO.equals(nomeReg)) {
                    resposta.setBoleto(factory.createComprarContratoResponseBoleto(BoletoAssembler.toBoletoV1(paramResposta)));
                } else if (HISTORICO.equals(nomeReg)) {
                    resposta.getHistoricos().add(HistoricoAssembler.toHistoricoV1(paramResposta));
                } else if (RESUMO.equals(nomeReg)) {
                    resposta.getResumos().add(ResumoAssembler.toResumoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.consultar.consignacao.compra.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "informarPagamentoSaldoDevedor")
    @ResponsePayload
    public InformarPagamentoSaldoDevedorResponse informarPagamentoSaldoDevedor(@RequestPayload InformarPagamentoSaldoDevedor informarPagamentoSaldoDevedor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = InformarPagamentoSaldoDevedorAssembler.toMap(informarPagamentoSaldoDevedor);

        AcessoSistema responsavel = null;
        final InformarPagamentoSaldoDevedorResponse resposta = new InformarPagamentoSaldoDevedorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(informarPagamentoSaldoDevedor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_INF_PG_SALDO_DEVEDOR);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.informar.pagamento.saldo.devedor.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "informarSaldoDevedor")
    @ResponsePayload
    public InformarSaldoDevedorResponse informarSaldoDevedor(@RequestPayload InformarSaldoDevedor informarSaldoDevedor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = InformarSaldoDevedorAssembler.toMap(informarSaldoDevedor);

        AcessoSistema responsavel = null;
        final InformarSaldoDevedorResponse resposta = new InformarSaldoDevedorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(informarSaldoDevedor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_EDT_SALDO_DEVEDOR);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.informar.saldo.devedor.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "liquidarCompra")
    @ResponsePayload
    public LiquidarCompraResponse liquidarCompra(@RequestPayload LiquidarCompra liquidarCompra) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = LiquidarCompraAssembler.toMap(liquidarCompra);

        AcessoSistema responsavel = null;
        final LiquidarCompraResponse resposta = new LiquidarCompraResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(liquidarCompra.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.liquidar.compra.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "rejeitarPgSaldoDevedor")
    @ResponsePayload
    public RejeitarPgSaldoDevedorResponse rejeitarPgSaldoDevedor(@RequestPayload RejeitarPgSaldoDevedor rejeitarPgSaldoDevedor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = RejeitarPgSaldoDevedorAssembler.toMap(rejeitarPgSaldoDevedor);

        AcessoSistema responsavel = null;
        final RejeitarPgSaldoDevedorResponse resposta = new RejeitarPgSaldoDevedorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(rejeitarPgSaldoDevedor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_REJ_PG_SALDO_DEVEDOR);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.rejeitar.pagamento.saldo.devedor.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "retirarContratoDaCompra")
    @ResponsePayload
    public RetirarContratoDaCompraResponse retirarContratoDaCompra(@RequestPayload RetirarContratoDaCompra retirarContratoDaCompra) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = RetirarContratoDaCompraAssembler.toMap(retirarContratoDaCompra);

        AcessoSistema responsavel = null;
        final RetirarContratoDaCompraResponse resposta = new RetirarContratoDaCompraResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(retirarContratoDaCompra.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_RETIRAR_CONTRATO_COMPRA);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.operacao.retirar.compra.contrato.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "solicitarRecalculoSaldoDevedor")
    @ResponsePayload
    public SolicitarRecalculoSaldoDevedorResponse solicitarRecalculoSaldoDevedor(@RequestPayload SolicitarRecalculoSaldoDevedor recalcularSaldoDevedor) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = SolicitarRecalculoSaldoDevedorAssembler.toMap(recalcularSaldoDevedor);

        AcessoSistema responsavel = null;
        final SolicitarRecalculoSaldoDevedorResponse resposta = new SolicitarRecalculoSaldoDevedorResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(recalcularSaldoDevedor.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.detalhar.solicitar.recalculo.saldo.devedor.arg0", responsavel, e.getMessage()));
            }
        }
    }
}