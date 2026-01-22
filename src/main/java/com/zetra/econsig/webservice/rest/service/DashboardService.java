package com.zetra.econsig.webservice.rest.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.persistence.entity.ArquivoUsuario;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.DashboardRestEnum;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: DashboardService</p>
 * <p>Description: Service para chamadas REST para contrução dos dashboards</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/dashboard")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DashboardService extends RestService {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashboardService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/dashboard")
    public Response dashConsignataria() {

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final AutorizacaoDelegate autDelegate = new AutorizacaoDelegate();
        final ServidorDelegate serDelegate = new ServidorDelegate();

        final List<Map<String, Object>> retorno = new ArrayList<>();

        // String MARGEM_TOTAL_CONSIGNADA = "total_consignada";
        // String MARGEM_TOTAL_DISPONIVEL = "total_disponivel";
        /// String TOTAL_SERVIDOR_POR_ORGAO = "total_servidor_por_orgao";
        final String PATTERN_VALOR_MONETARIO = ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern", (AcessoSistema) null);

        BigDecimal margemTotalConsignada = new BigDecimal("0");
        final List<TransferObject> margemTotalDisponivel = new ArrayList<>();
        List<TransferObject> totalServidorPorOrgao = new ArrayList<>();

        try {
            if (responsavel.isCseSup() || responsavel.isOrg() || responsavel.isCsa() || responsavel.isCor()) {
                //(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, int offset, int count, CustomTransferObject criterio, AcessoSistema responsavel)
                margemTotalConsignada = autDelegate.obtemTotalValorContratosAtivos(responsavel);
                Map<String, Object> item = new HashMap<>();
                item.put("codigo", DashboardRestEnum.MARGEM_TOTAL_CONSIGNADA);
                item.put("titulo", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.titulo.margem.total.consignada", responsavel) + " (" + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + ")");
                item.put("descricao", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.descricao.margem.total.consignada", responsavel));
                item.put("tipo", DashboardRestEnum.TIPO_VALOR.getCodigo());
                item.put("dados", NumberHelper.formata(margemTotalConsignada.doubleValue(), PATTERN_VALOR_MONETARIO));

                retorno.add(item);

                //2 - Soma das margens restantes dos servidores cujo orgão tenha convênio com a consignatária
                final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
                final Map<String, String> resultado = consultarMargemController.consultarMargemDisponivelTotal(responsavel);

                for (final Map.Entry<String, String> margem : resultado.entrySet()) {
                    final TransferObject obj = new CustomTransferObject();
                    obj.setAttribute("nome", margem.getKey());
                    obj.setAttribute("valor", NumberHelper.formata(Double.parseDouble(margem.getValue()), PATTERN_VALOR_MONETARIO));
                    margemTotalDisponivel.add(obj);
                }

                final List<String> filter = Arrays.asList("nome", "valor");

                item = new HashMap<>();
                item.put("codigo", DashboardRestEnum.MARGEM_TOTAL_DISPONIVEL);
                item.put("titulo", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.titulo.margem.total.disponivel", responsavel) + " (" + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + ")");
                item.put("descricao", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.descricao.margem.total.disponivel", responsavel));
                item.put("tipo", DashboardRestEnum.TIPO_TABELA.getCodigo());
                item.put("dados", transformTOs(margemTotalDisponivel, filter));

                retorno.add(item);

                //3 - Quantidade de servidores ativos ou bloqueados por órgão.
                totalServidorPorOrgao = serDelegate.countQtdeServidorPorOrg(responsavel);

                item = new HashMap<>();
                item.put("codigo", DashboardRestEnum.TOTAL_SERVIDOR_POR_ORGAO);
                item.put("titulo", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.titulo.quantidade.servidor.por.orgao", responsavel));
                item.put("descricao", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.descricao.quantidade.servidor.por.orgao", responsavel));
                item.put("tipo", DashboardRestEnum.TIPO_TABELA.getCodigo());
                item.put("dados", transformTOs(totalServidorPorOrgao, filter));

                retorno.add(item);

                // Adiciona informações do usuário
                item = new HashMap<>();
                item.put("usuCodigo", responsavel.getUsuCodigo());
                item.put("usuLogin", responsavel.getUsuLogin());
                item.put("usuNome", responsavel.getUsuNome());
                item.put("usuCpf", responsavel.getUsuCpf());
                item.put("usuEmail", responsavel.getUsuEmail());
                item.put("codigoEntidade", responsavel.getCodigoEntidade());
                item.put("nomeEntidade", responsavel.getNomeEntidade());
                item.put("tipoEntidade", responsavel.getTipoEntidade());

                int diasNotificacaoExpiracaoSenha = 0;
                if (responsavel.isCsaCor()) {
                    diasNotificacaoExpiracaoSenha = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSA_COR, 0, responsavel);
                }

                if (diasNotificacaoExpiracaoSenha > 0) {
                    item.put("diasExpiracaoSenha", diasNotificacaoExpiracaoSenha);
                }

                retorno.add(item);
                
                if (responsavel.isCsa()) {
                    //Lista de consignações ativas por orgao
                    List<TransferObject> totalAdePorOrgao = autDelegate.listaTotalConsignacaoAtivasPorOrgao(responsavel);
                    item = new HashMap<>();
                    item.put("codigo", DashboardRestEnum.TOTAL_ADE_POR_ORGAO);
                    item.put("titulo", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.titulo.total.ade.ativas.por.orgao", responsavel));
                    item.put("descricao", ApplicationResourcesHelper.getMessage("mensagem.rest.dashboard.descricao.total.ade.ativas.por.orgao", responsavel));
                    item.put("tipo", DashboardRestEnum.TIPO_LISTA.getCodigo());
                    item.put("dados", transformTOs(totalAdePorOrgao, null));
                    retorno.add(item);
                }
                
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

        } catch (AutorizacaoControllerException | ServidorControllerException | BeansException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/dashboardCse")
    public Response dashboardCse() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final UsuarioRestResponse usuResponse = new UsuarioRestResponse();

        try {
            if (!responsavel.isCse()) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            } else {
                final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                usuResponse.token = usuarioController.gerarChaveSessaoUsuario(responsavel.getUsuCodigo(), responsavel);
                final UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

                final String operacoesTotp = usuario.getUsuOperacoesValidacaoTotp();
                final boolean isOperacaoValidacaoTotp = (operacoesTotp != null) && (operacoesTotp.contains(OperacaoValidacaoTotpEnum.AUTENTICACAO_SISTEMA.getCodigo()) ||
                                                                                    operacoesTotp.contains(OperacaoValidacaoTotpEnum.AMBOS.getCodigo()));

                final int diasNotificacaoExpiracaoSenha = ParamSist.getIntParamSist(
                                                                                    CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSE_ORG,
                                                                                    0,
                                                                                    responsavel);

                if (diasNotificacaoExpiracaoSenha > 0) {
                    usuResponse.diasExpiracaoSenha = diasNotificacaoExpiracaoSenha;
                }

                usuResponse.requiresTOTP = isOperacaoValidacaoTotp;
                usuResponse.usuCodigo = responsavel.getUsuCodigo();
                usuResponse.cpf = usuario.getUsuCPF() != null ? usuario.getUsuCPF() : null;
                usuResponse.nome = usuario.getUsuNome() != null ? usuario.getUsuNome() : null;
                usuResponse.telefone = usuario.getUsuTel() != null ? usuario.getUsuTel() : null;
                usuResponse.dataUltAcesso = usuario.getUsuDataUltAcesso() != null ? usuario.getUsuDataUltAcesso().toString() : null;
                usuResponse.email = usuario.getUsuEmail() != null ? usuario.getUsuEmail() : null;

                final Collection<ArquivoUsuario> lstArqs = usuarioController.findArquivoUsuario(responsavel.getUsuCodigo(), TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo(), responsavel);
                usuResponse.imagem = (lstArqs != null) && !lstArqs.isEmpty() ? Base64.encodeBase64String(lstArqs.iterator().next().getAusConteudo()) : null;

                final ConsignanteTransferObject cse = consignanteController.findConsignante(responsavel.getCodigoEntidade(), responsavel);
                responsavel.setNomeEntidade(cse.getCseNome());
                responsavel.setIdEntidade(cse.getCseIdentificador());
                usuResponse.tipoEntidade = responsavel.getTipoEntidade();
                usuResponse.codigoEntidade = responsavel.getCodigoEntidade();
                usuResponse.nomeEntidade = responsavel.getNomeEntidade();
                usuResponse.codigoEntidadePai = responsavel.getCodigoEntidadePai();
                usuResponse.nomeEntidadePai = responsavel.getNomeEntidadePai();
                usuResponse.naturezasCsa = consignatariaController.lstNatureza();
                usuResponse.camposSistema = new HashMap<>();
                usuResponse.permissoes = new ArrayList<>();

                final String[] campos = {
                                          FieldKeysConstants.EDT_SERVIDOR_NOME,
                                          FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO,
                                          FieldKeysConstants.EDT_SERVIDOR_CPF,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO,
                                          FieldKeysConstants.EDT_SERVIDOR_EMAIL,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO,
                                          FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EST
                };

                for (final String campo : campos) {
                    if (ShowFieldHelper.isRequired(campo, responsavel)) {
                        usuResponse.camposSistema.put(campo, "O");
                    } else if (ShowFieldHelper.canEdit(campo, responsavel)) {
                        usuResponse.camposSistema.put(campo, "S");
                    }
                }

                final String[] funcoes = {
                                           CodedValues.FUN_CONS_USU_SERVIDORES,
                                           CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL,
                                           CodedValues.FUN_CONSULTAR_SERVIDOR,
                                           CodedValues.FUN_CONS_MARGEM,
                                           CodedValues.FUN_CONS_CONSIGNATARIAS,
                                           CodedValues.FUN_CONS_CONSIGNACAO,
                                           CodedValues.FUN_LISTAR_SOLICITACAO_SALDO_DEVEDOR
                };

                for (final String funcao : funcoes) {
                    if (usuarioController.usuarioTemPermissao(responsavel.getUsuCodigo(), funcao, responsavel.getTipoEntidade(), responsavel)) {
                        usuResponse.permissoes.add(funcao);
                    }
                }
            }
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
        return Response.status(Response.Status.OK).entity(usuResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

}
