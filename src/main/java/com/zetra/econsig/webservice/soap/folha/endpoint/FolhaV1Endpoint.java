package com.zetra.econsig.webservice.soap.folha.endpoint;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_INTEGRACAO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.MOVIMENTO_FINANCEIRO;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.PERFIL_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_CADASTRADO;
import static com.zetra.econsig.webservice.CamposAPI.USU_DATA_EXP_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContextHolder;

import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.ConsignatariaAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.ConvenioAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.EstabelecimentoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.MovimentoFinanceiroAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.OrgaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.PerfilAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.ServicoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.ServidorAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.AtualizarCalendarioFolhaAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.AtualizarMargemAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarConsignatariaAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarConvenioAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarEstabelecimentoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarOrgaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarServicoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.CadastrarUsuarioAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarConsignatariaAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarConvenioAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarEstabelecimentoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarMovimentoFinanceiroAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarOrgaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarPerfilUsuarioAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ConsultarServicoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.DownloadArquivoIntegracaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.EnviarArquivoIntegracaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ListarArquivoIntegracaoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ModificarConsignanteAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ModificarParametroServicoAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ModificarParametroSistemaAssembler;
import com.zetra.econsig.webservice.soap.folha.assembler.operation.ModificarUsuarioAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Arquivo;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarCalendarioFolha;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarCalendarioFolhaResponse;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarMargem;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarMargemResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConsignataria;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConsignatariaResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConvenio;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConvenioResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarEstabelecimento;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarEstabelecimentoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarOrgao;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarOrgaoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarServico;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarServicoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarUsuario;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarUsuarioResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConsignataria;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConsignatariaResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConvenio;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConvenioResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarEstabelecimento;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarEstabelecimentoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarMovimentoFinanceiro;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarMovimentoFinanceiroResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarOrgao;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarOrgaoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarPerfilUsuario;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarPerfilUsuarioResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarServico;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarServicoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.DownloadArquivoIntegracao;
import com.zetra.econsig.webservice.soap.folha.v1.DownloadArquivoIntegracaoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.EnviarArquivoIntegracao;
import com.zetra.econsig.webservice.soap.folha.v1.EnviarArquivoIntegracaoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ListarArquivoIntegracao;
import com.zetra.econsig.webservice.soap.folha.v1.ListarArquivoIntegracaoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarConsignante;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarConsignanteResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroServico;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroServicoResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroSistema;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroSistemaResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarUsuario;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarUsuarioResponse;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: FolhaV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Folha versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class FolhaV1Endpoint extends FolhaEndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FolhaV1Endpoint.class);

    private static final String NAMESPACE_URI = "FolhaService-v1_0";

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(parametros, NAMESPACE_URI, responsavel);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "atualizarCalendarioFolha")
    @ResponsePayload
    public AtualizarCalendarioFolhaResponse atualizarCalendarioFolha(@RequestPayload AtualizarCalendarioFolha atualizarCalendarioFolha) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = AtualizarCalendarioFolhaAssembler.toMap(atualizarCalendarioFolha);

        AcessoSistema responsavel = null;
        final AtualizarCalendarioFolhaResponse resposta = new AtualizarCalendarioFolhaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(atualizarCalendarioFolha.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_CALENDARIO_FOLHA);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.modificar.parametro.sistema.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "atualizarMargem")
    @ResponsePayload
    public AtualizarMargemResponse atualizarMargem(@RequestPayload AtualizarMargem atualizarMargem) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = AtualizarMargemAssembler.toMap(atualizarMargem);

        AcessoSistema responsavel = null;
        final AtualizarMargemResponse resposta = new AtualizarMargemResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(atualizarMargem.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_ATUALIZAR_MARGEM);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (SERVIDOR.equals(nomeReg)) {
                    resposta.getServidores().add(ServidorAssembler.toServidorV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.atualizar.margem.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarConsignataria")
    @ResponsePayload
    public CadastrarConsignatariaResponse cadastrarConsignataria(@RequestPayload CadastrarConsignataria cadastrarConsignataria) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = CadastrarConsignatariaAssembler.toMap(cadastrarConsignataria);

        AcessoSistema responsavel = null;
        final CadastrarConsignatariaResponse resposta = new CadastrarConsignatariaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarConsignataria.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_CONSIGNATARIA);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.consignataria.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarConvenio")
    @ResponsePayload
    public CadastrarConvenioResponse cadastrarConvenio(@RequestPayload CadastrarConvenio cadastrarConvenio)  {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CadastrarConvenioAssembler.toMap(cadastrarConvenio);

        AcessoSistema responsavel = null;
        final CadastrarConvenioResponse resposta = new CadastrarConvenioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarConvenio.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_VERBA);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.convenio.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarEstabelecimento")
    @ResponsePayload
    public CadastrarEstabelecimentoResponse cadastrarEstabelecimento(@RequestPayload CadastrarEstabelecimento cadastrarEstabelecimento) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CadastrarEstabelecimentoAssembler.toMap(cadastrarEstabelecimento);

        AcessoSistema responsavel = null;
        final CadastrarEstabelecimentoResponse resposta = new CadastrarEstabelecimentoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarEstabelecimento.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_ESTABELECIMENTO);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.estabelecimento.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarOrgao")
    @ResponsePayload
    public CadastrarOrgaoResponse cadastrarOrgao(@RequestPayload CadastrarOrgao cadastrarOrgao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CadastrarOrgaoAssembler.toMap(cadastrarOrgao);

        AcessoSistema responsavel = null;
        final CadastrarOrgaoResponse resposta = new CadastrarOrgaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarOrgao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_ORGAO);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.orgao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cadastrarServico")
    @ResponsePayload
    public CadastrarServicoResponse cadastrarServico(@RequestPayload CadastrarServico cadastrarServico) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final Map<CamposAPI, Object> parametros = CadastrarServicoAssembler.toMap(cadastrarServico);

        AcessoSistema responsavel = null;
        final CadastrarServicoResponse resposta = new CadastrarServicoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(cadastrarServico.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_SERVICO);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.servico.arg0", responsavel, e.getMessage()));
            }
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
            parametros.put(OPERACAO, CodedValues.OP_CADASTRAR_USUARIO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                    @SuppressWarnings("unchecked")
                    final Map<CamposAPI, Object> usuario = (Map<CamposAPI, Object>) paramResposta.get(USUARIO_CADASTRADO);
                    if (usuario != null) {
                        resposta.setEntidadeCodigo((String) usuario.get(ENTIDADE_CODIGO));
                        resposta.setEntidadeNome((String) usuario.get(ENTIDADE_NOME));
                        resposta.setNome((String) usuario.get(USU_NOME));
                        resposta.setLogin((String) usuario.get(USU_LOGIN));
                        resposta.setSenha(factory.createCadastrarUsuarioResponseSenha((String) usuario.get(USU_SENHA)));
                        resposta.setDataExpiracao(BaseAssembler.toXMLGregorianCalendar((Date) usuario.get(USU_DATA_EXP_SENHA), false));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.usuario.arg0", responsavel, e.getMessage()));
            }
        } catch (final DatatypeConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.usuario.arg0", (AcessoSistema) null, e.getMessage()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarConsignataria")
    @ResponsePayload
    public ConsultarConsignatariaResponse consultarConsignataria(@RequestPayload ConsultarConsignataria consultarConsignataria) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarConsignatariaAssembler.toMap(consultarConsignataria);

        AcessoSistema responsavel = null;
        final ConsultarConsignatariaResponse resposta = new ConsultarConsignatariaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarConsignataria.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_CONSIGNATARIA);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (CONSIGNATARIA.equals(nomeReg)) {
                    resposta.getConsignatarias().add(ConsignatariaAssembler.toConsignatariaV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.consignataria.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarConvenio")
    @ResponsePayload
    public ConsultarConvenioResponse consultarConvenio(@RequestPayload ConsultarConvenio consultarConvenio) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarConvenioAssembler.toMap(consultarConvenio);

        AcessoSistema responsavel = null;
        final ConsultarConvenioResponse resposta = new ConsultarConvenioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarConvenio.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_VERBA);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (CONVENIO.equals(nomeReg)) {
                    resposta.getConvenios().add(ConvenioAssembler.toConvenioV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.convenio.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarEstabelecimento")
    @ResponsePayload
    public ConsultarEstabelecimentoResponse consultarEstabelecimento(@RequestPayload ConsultarEstabelecimento consultarEstabelecimento) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarEstabelecimentoAssembler.toMap(consultarEstabelecimento);

        AcessoSistema responsavel = null;
        final ConsultarEstabelecimentoResponse resposta = new ConsultarEstabelecimentoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarEstabelecimento.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_ESTABELECIMENTO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (ESTABELECIMENTO.equals(nomeReg)) {
                    resposta.getEstabelecimentos().add(EstabelecimentoAssembler.toEstabelecimentoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.estabelecimento.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarMovimentoFinanceiro")
    @ResponsePayload
    public ConsultarMovimentoFinanceiroResponse consultarMovimentoFinanceiro(@RequestPayload ConsultarMovimentoFinanceiro consultarMovimentoFinanceiro) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarMovimentoFinanceiroAssembler.toMap(consultarMovimentoFinanceiro);

        AcessoSistema responsavel = null;
        final ConsultarMovimentoFinanceiroResponse resposta = new ConsultarMovimentoFinanceiroResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarMovimentoFinanceiro.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_MOVIMENTO_FINANCEIRO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (MOVIMENTO_FINANCEIRO.equals(nomeReg)) {
                    resposta.getMovimento().add(MovimentoFinanceiroAssembler.toMovimentoFinanceiroV1(paramResposta));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.movimento.financeiro.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarOrgao")
    @ResponsePayload
    public ConsultarOrgaoResponse consultarOrgao(@RequestPayload ConsultarOrgao consultarOrgao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarOrgaoAssembler.toMap(consultarOrgao);

        AcessoSistema responsavel = null;
        final ConsultarOrgaoResponse resposta = new ConsultarOrgaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarOrgao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_ORGAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (ORGAO.equals(nomeReg)) {
                    resposta.getOrgaos().add(OrgaoAssembler.toOrgaoV1(paramResposta));
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.orgao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarPerfilUsuario")
    @ResponsePayload
    public ConsultarPerfilUsuarioResponse consultarPerfilUsuario(@RequestPayload ConsultarPerfilUsuario consultarPerfilUsuario) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarPerfilUsuarioAssembler.toMap(consultarPerfilUsuario);

        AcessoSistema responsavel = null;
        final ConsultarPerfilUsuarioResponse resposta = new ConsultarPerfilUsuarioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarPerfilUsuario.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_PERFIL_USUARIO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (PERFIL_USUARIO.equals(nomeReg)) {
                    resposta.getPerfil().add(PerfilAssembler.toPerfilV1(paramResposta, responsavel));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.perfil.usuario.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarServico")
    @ResponsePayload
    public ConsultarServicoResponse consultarServico(@RequestPayload ConsultarServico consultarServico) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = ConsultarServicoAssembler.toMap(consultarServico);

        AcessoSistema responsavel = null;
        final ConsultarServicoResponse resposta = new ConsultarServicoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarServico.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_CONSULTAR_SERVICO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                } else if (CONSULTA_SERVICO.equals(nomeReg)) {
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.servico.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "downloadArquivoIntegracao")
    @ResponsePayload
    public DownloadArquivoIntegracaoResponse downloadArquivoIntegracao(@RequestPayload DownloadArquivoIntegracao downloadArquivoIntegracao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = DownloadArquivoIntegracaoAssembler.toMap(downloadArquivoIntegracao);

        AcessoSistema responsavel = null;
        final DownloadArquivoIntegracaoResponse resposta = new DownloadArquivoIntegracaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(downloadArquivoIntegracao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_DOWNLOAD_ARQUIVO_INTEGRACAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro : listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));
                    if (paramResposta.get(ARQUIVO_INTEGRACAO) != null) {
                        resposta.setArquivo(factory.createDownloadArquivoIntegracaoResponseArquivo((byte[]) paramResposta.get(ARQUIVO_INTEGRACAO)));
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.enviar.arquivo.integracao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "enviarArquivoIntegracao")
    @ResponsePayload
    public EnviarArquivoIntegracaoResponse enviarArquivoIntegracao(@RequestPayload EnviarArquivoIntegracao enviarArquivoIntegracao) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final Map<CamposAPI, Object> parametros = EnviarArquivoIntegracaoAssembler.toMap(enviarArquivoIntegracao);

        AcessoSistema responsavel = null;
        final EnviarArquivoIntegracaoResponse resposta = new EnviarArquivoIntegracaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(enviarArquivoIntegracao.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {

            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_ENVIAR_ARQUIVO_INTEGRACAO);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.enviar.arquivo.integracao.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarArquivoIntegracao")
    @ResponsePayload
    public ListarArquivoIntegracaoResponse listarArquivoIntegracao(@RequestPayload ListarArquivoIntegracao consultarPerfilUsuario) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ListarArquivoIntegracaoAssembler.toMap(consultarPerfilUsuario);

        AcessoSistema responsavel = null;
        final ListarArquivoIntegracaoResponse resposta = new ListarArquivoIntegracaoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(consultarPerfilUsuario.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_LISTAR_ARQUIVO_INTEGRACAO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                    @SuppressWarnings("unchecked")
                    final List<ArquivoDownload> arquivosIntegracao = (List<ArquivoDownload>) paramResposta.get(ARQUIVO_INTEGRACAO);
                    if ((arquivosIntegracao != null) && !arquivosIntegracao.isEmpty()) {
                        for (final ArquivoDownload arquivoIntegracao : arquivosIntegracao) {
                            final String entidade = arquivoIntegracao.getEntidade();
                            String estIdentificador = "integracao".equals(parametros.get(TIPO_ARQUIVO)) && !TextHelper.isNull(parametros.get(CODIGO_ESTABELECIMENTO)) ? (String) parametros.get(CODIGO_ESTABELECIMENTO) : "";
                            String orgIdentificador = !TextHelper.isNull(estIdentificador) && !TextHelper.isNull(parametros.get(CODIGO_ORGAO)) ? (String) parametros.get(CODIGO_ORGAO) : "";

                            if (!TextHelper.isNull(entidade)) {
                                if (entidade.contains(" - ")) {
                                    final String[] codigos = entidade.split(" - ");
                                    if ((codigos != null) && (codigos.length > 1)) {
                                        orgIdentificador = codigos[0];
                                        estIdentificador = codigos[1];
                                    }
                                } else {
                                    estIdentificador = entidade;
                                }
                            }
                            final Arquivo arquivo = new Arquivo();
                            arquivo.setNome(arquivoIntegracao.getNomeOriginal());
                            arquivo.setDataModificacao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse(arquivoIntegracao.getData(), LocaleHelper.getDateTimePattern()), true));
                            arquivo.setTamanho(arquivoIntegracao.getTamanho());
                            arquivo.setCodigoEstabelecimento(factory.createArquivoCodigoEstabelecimento(estIdentificador));
                            arquivo.setCodigoOrgao(factory.createArquivoCodigoOrgao(orgIdentificador));

                            resposta.getArquivo().add(arquivo);
                        }
                    }
                }
            }

            return resposta;
        } catch (final ParseException | DatatypeConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.listar.arquivo.integracao.arg0", responsavel, e.getMessage()));
        } catch (final ZetraException e) {
            if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioNaoTemPermissao".equals(e.getMessageKey())) {
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_SEM_PERMISSAO_PARA_ACAO));
                resposta.setSucesso(false);
                resposta.setMensagem(e.getMessage());
                LOG.error(e.getMessage(), e);

                return resposta;
            } else {
                LOG.error(e.getMessage(), e);
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.perfil.usuario.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "modificarConsignante")
    @ResponsePayload
    public ModificarConsignanteResponse modificarConsignante(@RequestPayload ModificarConsignante modificarConsignante) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ModificarConsignanteAssembler.toMap(modificarConsignante);

        AcessoSistema responsavel = null;
        final ModificarConsignanteResponse resposta = new ModificarConsignanteResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(modificarConsignante.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_MODIFICAR_CONSIGNANTE);
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
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.cadastrar.usuario.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "modificarParametroServico")
    @ResponsePayload
    public ModificarParametroServicoResponse modificarParametroServico(@RequestPayload ModificarParametroServico modificarParametroServico) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ModificarParametroServicoAssembler.toMap(modificarParametroServico);

        AcessoSistema responsavel = null;
        final ModificarParametroServicoResponse resposta = new ModificarParametroServicoResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(modificarParametroServico.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_MODIFICAR_PARAM_SERVICO);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.modificar.parametro.servico.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "modificarParametroSistema")
    @ResponsePayload
    public ModificarParametroSistemaResponse modificarParametroSistema(@RequestPayload ModificarParametroSistema modificarParametroSistema) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        
        final Map<CamposAPI, Object> parametros = ModificarParametroSistemaAssembler.toMap(modificarParametroSistema);

        AcessoSistema responsavel = null;
        final ModificarParametroSistemaResponse resposta = new ModificarParametroSistemaResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(modificarParametroSistema.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_MODIFICAR_PARAM_SISTEMA);
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
                throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.modificar.parametro.sistema.arg0", responsavel, e.getMessage()));
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "modificarUsuario")
    @ResponsePayload
    public ModificarUsuarioResponse modificarUsuario(@RequestPayload ModificarUsuario modificarUsuario) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());
        final ObjectFactory factory = new ObjectFactory();

        final Map<CamposAPI, Object> parametros = ModificarUsuarioAssembler.toMap(modificarUsuario);

        AcessoSistema responsavel = null;
        final ModificarUsuarioResponse resposta = new ModificarUsuarioResponse();

        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(modificarUsuario.getUsuario(), remoteAddr, remotePort);
        } catch (final ZetraException e1) {
            resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_OU_SENHA_INVALIDOS));
            resposta.setSucesso(false);
            resposta.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", responsavel));
            LOG.error(e1.getMessage(), e1);

            return resposta;
        }

        try {
            parametros.put(OPERACAO, CodedValues.OP_MODIFICAR_USUARIO);
            final List<RegistroRespostaRequisicaoExterna> listRespostas = executaOperacao(parametros, responsavel);

            for (final RegistroRespostaRequisicaoExterna registro: listRespostas) {
                final CamposAPI nomeReg = registro.getNome();
                final Map<CamposAPI, Object> paramResposta = registro.getAtributos();
                if (RESULTADO.equals(nomeReg)) {
                    resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, (String) paramResposta.get(COD_RETORNO)));
                    final boolean sucesso = ((paramResposta.get(SUCESSO) != null) && "S".equals(paramResposta.get(SUCESSO)));
                    resposta.setSucesso(sucesso);
                    resposta.setMensagem((String) paramResposta.get(MENSAGEM));

                    @SuppressWarnings("unchecked")
                    final Map<CamposAPI, Object> usuario = (Map<CamposAPI, Object>) paramResposta.get(USUARIO_CADASTRADO);
                    if (usuario != null) {
                        resposta.setEntidadeCodigo((String) usuario.get(ENTIDADE_CODIGO));
                        resposta.setEntidadeNome((String) usuario.get(ENTIDADE_NOME));
                        resposta.setNome((String) usuario.get(USU_NOME));
                        resposta.setLogin((String) usuario.get(USU_LOGIN));
                        resposta.setSenha(factory.createModificarUsuarioResponseSenha((String) usuario.get(USU_SENHA)));
                        resposta.setDataExpiracao(BaseAssembler.toXMLGregorianCalendar((Date) usuario.get(USU_DATA_EXP_SENHA), false));
                    }
                }
            }

            return resposta;
        } catch (final DatatypeConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.modificar.usuario.arg0", responsavel, e.getMessage()));
       } catch (final ZetraException e) {
            if (!TextHelper.isNull(e.getMessageKey()) && "mensagem.usuarioNaoTemPermissao".equals(e.getMessageKey())) {
                resposta.setCodRetorno(createCodRetorno(NAMESPACE_URI, USUARIO_SEM_PERMISSAO_PARA_ACAO));
                resposta.setSucesso(false);
                resposta.setMensagem(e.getMessage());
                LOG.error(e.getMessage(), e);

                return resposta;
            } else {
                LOG.error(e.getMessage(), e);
                throw new java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.modificar.usuario.arg0", responsavel, e.getMessage()));
            }
        }
    }
}