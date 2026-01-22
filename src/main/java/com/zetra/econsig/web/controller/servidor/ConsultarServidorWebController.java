package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.NotificacaoUsuario;
import com.zetra.econsig.persistence.entity.NotificacaoUsuarioId;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.notificacao.NotificacaoUsuarioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConsultarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarServidor" })
public class ConsultarServidorWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarServidorWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private NotificacaoUsuarioController notificacaoUsuarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final ParamSession paramSession = ParamSession.getParamSession(session);
            String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");

            if (TextHelper.isNull(linkRet)) {
                linkRet = (SynchronizerToken.updateTokenInURL((responsavel.isSer()) ? "../v3/carregarPrincipal" : paramSession.getLastHistory(), request));
            } else {
                linkRet = linkRet.replace('$', '?').replace('(', '=').replace('|', '&');
                linkRet += ((linkRet.indexOf("?") > -1) ? "&" : "?") + SynchronizerToken.generateToken4URL(request);
            }
            final String detalheAut = JspHelper.verificaVarQryStr(request, "detalheAut");
            String linkRetorno = JspHelper.verificaVarQryStr(request, "linkRet");

            if (!TextHelper.isNull(detalheAut)) {
                linkRet = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            }

            if (TextHelper.isNull(linkRetorno)) {
                linkRetorno = linkRet.replace('?', '$').replace('&', '|').replace('=', '(');
            } else {
                linkRet = SynchronizerToken.updateTokenInURL(linkRetorno.replace('$', '?').replace('|', '&').replace('(', '='), request);
            }

            final String acao = JspHelper.verificaVarQryStr(request, "acao");

            String readOnly = "false";
            String readOnlyRse = "false";

            if ((!responsavel.isCseSupOrg() && !responsavel.isSer()) || (!responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR))) {
                readOnly = "true";
            }

            if (responsavel.isSer()) {
                rseCodigo = responsavel.getRseCodigo();
            }

            final ServidorTransferObject servidor = buscaServidor(rseCodigo, session, responsavel);
            final String serCodigo = servidor.getSerCodigo();
            final String serEmail = (servidor.getSerEmail() != null ? servidor.getSerEmail() : "");

            List<TransferObject> registros = null;
            try {
                String org_codigo = null;
                String est_codigo = null;
                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    est_codigo = responsavel.getCodigoEntidadePai();
                } else if (responsavel.isOrg()) {
                    org_codigo = responsavel.getCodigoEntidade();
                }

                registros = servidorController.lstRegistroServidor(serCodigo, org_codigo, est_codigo, responsavel);

            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                registros = null;
            }

            List<TransferObject> listEstadoCivil = null;
            try {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, responsavel)) {
                    // Pega os códigos de estado civil
                    listEstadoCivil = servidorController.getEstCivil(responsavel);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> listNivelEscolaridade = null;
            try {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE, responsavel)) {
                    // Pega os códigos do nível de escolaridade
                    listNivelEscolaridade = servidorController.getNivelEscolaridade(responsavel);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> listTipoHabitacao = null;
            try {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, responsavel)) {
                    // Pega os códigos do tipo de Habidacao
                    listTipoHabitacao = servidorController.getTipoHabitacao(responsavel);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            RegistroServidorTO rseSelecionado = null;
            try {
                rseSelecionado = servidorController.findRegistroServidor(rseCodigo, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // bloqueios de serviços por servidor
            Map<String, Long> bloqueioServico = new HashMap<>();
            try {
                bloqueioServico = parametroController.getBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Sobrepõe valor do parâmetro de servico
            final List<TransferObject> paramSvcRse = parametroController.lstTipoParamSvcSobrepoe(responsavel);
            final boolean sobrepoeParamSvc = ((paramSvcRse != null) && !paramSvcRse.isEmpty());

            // bloqueios de naturezas de serviço por servidor
            Map<String, Long> bloqueioNaturezaServico = new HashMap<>();
            try {
                bloqueioNaturezaServico = parametroController.getBloqueioNseRegistroServidor(rseCodigo, null, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // bloqueios de consignatárias por servidor
            Map<String, Long> bloqueioConsignataria = new HashMap<>();
            try {
            	bloqueioConsignataria = parametroController.getBloqueioCsaRegistroServidor(rseCodigo, null, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }


            // bloqueios de convênios por servidor
            final String csaCodigo = responsavel.getCsaCodigo();
            CustomTransferObject bloqueioServidor = null;
            try {
                bloqueioServidor = parametroController.getBloqueioCnvRegistroServidor(rseCodigo, csaCodigo, null, Boolean.TRUE, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Busca a lista de margens do servidor
            List<MargemTO> margens = new ArrayList<>();
            try {
                margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, false, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean obrigInfTodosDadosSer = ParamSist.paramEquals(CodedValues.TPC_OBRIG_INF_TODOS_DADOS_EDT_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean habilitaSaldoDevedorExclusaoServidor = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean exigeDetalheExclusaoSer = ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel);
            final boolean exigeMotivo = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_SERVIDOR, responsavel);

            final boolean podeEditarRegServidor = responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR);
            final boolean podeEdtCnvRegServidor = responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR);
            final boolean podeBloquearCsaRegServidor = responsavel.temPermissao(CodedValues.FUN_BLOQUEAR_CSA_PARA_SERVIDOR);
            final boolean podeConsRegServidor = responsavel.isSer() ? responsavel.temPermissao(CodedValues.FUN_CONS_DADOS_CADASTRAIS_SERVIDOR) : responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SERVIDOR);
            final boolean podeConsCnvRegServidor = responsavel.isSer() ? responsavel.temPermissao(CodedValues.FUN_CONS_DADOS_CADASTRAIS_SERVIDOR) : responsavel.temPermissao(CodedValues.FUN_CONS_CNV_REG_SERVIDOR);
            final boolean podeAnexarFoto = responsavel.temPermissao(CodedValues.FUN_ANEXAR_FOTO);

            final Map<String, String> acoes = new LinkedHashMap<>();
            if (podeEditarRegServidor || podeConsRegServidor || ((registros != null) && (registros.size() > 0))) {
                if (podeConsRegServidor) {
                    acoes.put("listarOcorrencias", ApplicationResourcesHelper.getMessage("rotulo.acao.listar.ocorrencias.servidor", responsavel));
                }
                if (podeEdtCnvRegServidor || podeConsCnvRegServidor) {
                    int svcBloqueados = 0;
                    if (bloqueioServico.size() > 0) {
                        svcBloqueados = (bloqueioServico.get("B") != null) ? bloqueioServico.get("B").intValue() : 0;
                    }
                    String msgBloquearDesbloquearServicos = "";
                    if (svcBloqueados == 0) {
                        msgBloquearDesbloquearServicos = ApplicationResourcesHelper.getMessage("rotulo.acao.bloquear.servicos.servidor", responsavel);
                    } else {
                        msgBloquearDesbloquearServicos = ApplicationResourcesHelper.getMessage("rotulo.acao.desbloquear.servicos.servidor", responsavel);
                    }
                    if (podeEdtCnvRegServidor) {
                        acoes.put("bloqServicos", msgBloquearDesbloquearServicos);
                    } else {
                        acoes.put("consultarServicos", ApplicationResourcesHelper.getMessage("rotulo.acao.consultar.servicos.servidor", responsavel));
                    }

                    int nseBloqueados = 0;
                    if (bloqueioNaturezaServico.size() > 0) {
                        nseBloqueados = (bloqueioNaturezaServico.get("B") != null) ? bloqueioNaturezaServico.get("B").intValue() : 0;
                    }
                    String msgBloquearDesbloquearNaturezaServicos = "";
                    if (nseBloqueados == 0) {
                        msgBloquearDesbloquearNaturezaServicos = ApplicationResourcesHelper.getMessage("rotulo.acao.bloquear.nat.servicos.servidor", responsavel);
                    } else {
                        msgBloquearDesbloquearNaturezaServicos = ApplicationResourcesHelper.getMessage("rotulo.acao.desbloquear.nat.servicos.servidor", responsavel);
                    }

                    if (podeEdtCnvRegServidor) {
                        acoes.put("bloqNatServicos", msgBloquearDesbloquearNaturezaServicos);
                    } else {
                        acoes.put("consultarNatServicos", ApplicationResourcesHelper.getMessage("rotulo.acao.consultar.nat.servicos.servidor", responsavel));
                    }

                    int csaBloqueadas = 0;
                    if (bloqueioConsignataria.size() > 0) {
                    	csaBloqueadas = (bloqueioConsignataria.get("B") != null) ? bloqueioConsignataria.get("B").intValue() : 0;
                    }
                    String msgBloquearDesbloquearConsignatarias = "";
                    if (csaBloqueadas == 0) {
                    	msgBloquearDesbloquearConsignatarias = ApplicationResourcesHelper.getMessage("rotulo.acao.bloquear.consignatarias.servidor", responsavel);
                    } else {
                    	msgBloquearDesbloquearConsignatarias = ApplicationResourcesHelper.getMessage("rotulo.acao.desbloquear.consignatarias.servidor", responsavel);
                    }

                    if (podeBloquearCsaRegServidor) {
                        acoes.put("bloqConsignatarias", msgBloquearDesbloquearConsignatarias);
                    }

                    int cnvBloqueados = 0;

                    if (bloqueioServidor != null) {
                        cnvBloqueados = bloqueioServidor.getAttribute("B") != null ? ((Integer) bloqueioServidor.getAttribute("B")) : 0;
                    }
                    String msgBloquearDesbloquearConvenios = "";
                    if (cnvBloqueados == 0) {
                        msgBloquearDesbloquearConvenios = "rotulo.acao.bloquear.convenios.servidor";
                    } else {
                        msgBloquearDesbloquearConvenios = "rotulo.acao.desbloquear.convenios.servidor";
                    }

                    if (podeEdtCnvRegServidor) {
                        acoes.put("bloqConvenios", ApplicationResourcesHelper.getMessage(msgBloquearDesbloquearConvenios, responsavel));
                    } else {
                        acoes.put("consultarConvenios", ApplicationResourcesHelper.getMessage("rotulo.acao.consultar.convenios.servidor", responsavel));
                    }

                    if (sobrepoeParamSvc) {
                        if (!responsavel.isSer()) {
                            acoes.put("sobrepoeRse", ApplicationResourcesHelper.getMessage("rotulo.acao.param.svc.sobrepoe.rse", responsavel));
                        }
                    }
                }
                if (responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_MARGEM)) {
                    acoes.put("transferirMargem", ApplicationResourcesHelper.getMessage("rotulo.acao.transferir.margens.servidor", responsavel));
                }

                if (responsavel.isSup()) {
                    //DESENV-14302 Para que o usuário possa editar endereçoServidor, o mesmo tem que possuir permissão para Consultar/Listar EndereçoServidor e também permissão para Editar EndereçoServidor
                    //A primeira tela para edição de endereçoServidor é a tela de listarEndereços que tem como Acesso Recurso a função 425-FUN_CONSULTAR_ENDERECO_SERVIDOR.
                    if (responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR) && responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ENDERECO_SERVIDOR)) {
                        acoes.put("listarEndereco", ApplicationResourcesHelper.getMessage("rotulo.servidor.manutencao.endereco.editar.item.menu", responsavel));
                    } else if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ENDERECO_SERVIDOR)) {
                        acoes.put("listarEndereco", ApplicationResourcesHelper.getMessage("rotulo.servidor.manutencao.endereco.consultar.item.menu", responsavel));
                    }
                }

                if (responsavel.temPermissao(CodedValues.FUN_CONS_CONTRACHEQUE)) {
                    acoes.put("consContraCheques", ApplicationResourcesHelper.getMessage("rotulo.acao.consultar.contracheques.servidor", responsavel));
                }
                if (habilitaSaldoDevedorExclusaoServidor && responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEV_EXCLUSAO_SER)) {
                    acoes.put("solicitarSaldoDevedor", ApplicationResourcesHelper.getMessage("rotulo.acao.solicitar.saldo.servidor", responsavel));
                }

                if (responsavel.temPermissao(CodedValues.FUN_CAD_DISPENSA_VALIDACAO_DIGITAL_SER)) {
                    acoes.put("cadastrarDispensaValidacaoDigitalServidor", ApplicationResourcesHelper.getMessage("rotulo.acao.cadastrar.dispensa.validacao.digital.servidor", responsavel));
                }

                if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ANEXOS_REGISTRO_SERVIDOR) && !responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
                    acoes.put("consultarAnexoRse", ApplicationResourcesHelper.getMessage("rotulo.servidor.anexo.consultar", responsavel));
                } else if (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
                    acoes.put("editarAnexoRse", ApplicationResourcesHelper.getMessage("rotulo.servidor.anexo.editar", responsavel));
                }

                if (responsavel.temPermissao(CodedValues.FUN_EDT_COMPOSICAO_MARGEM_SER)) {
                    acoes.put("editarCompMargem", ApplicationResourcesHelper.getMessage("rotulo.acao.editar.composicao.margem.servidor", responsavel));
                }

                if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_FUNCOES_ENVIO_EMAIL)) {
                    acoes.put("editarFuncoesEnvioEmail", ApplicationResourcesHelper.getMessage("rotulo.acao.funcoes.envio.email", responsavel));
                }

                if (responsavel.temPermissao(CodedValues.FUN_OCULTAR_RSE_PARA_CSA)) {
                    acoes.put("ocultarRegistroSerCsa", ApplicationResourcesHelper.getMessage("rotulo.acao.ocultar.servidor.csa", responsavel));
                }
            }

            if (!responsavel.isSer()) {
                for (final TransferObject rseCorrente : registros) {
                    final String rseCodigoCorrente = (String) rseCorrente.getAttribute(Columns.RSE_CODIGO);
                    if (rseCodigoCorrente.equals(rseCodigo)) {
                        continue;
                    }
                    final String orgIdCorrente = (String) rseCorrente.getAttribute(Columns.ORG_IDENTIFICADOR);
                    String orgNomeCorrente = (String) rseCorrente.getAttribute(Columns.ORG_NOME);
                    final String rseMatriculaCorrente = (String) rseCorrente.getAttribute(Columns.RSE_MATRICULA);

                    orgNomeCorrente = TextHelper.forHtmlContent(orgNomeCorrente);
                    final String labelCombo = ApplicationResourcesHelper.getMessage("rotulo.acao.editar.servidor.orgao", responsavel, rseMatriculaCorrente, orgIdCorrente, orgNomeCorrente);

                    acoes.put(rseCodigoCorrente, labelCombo);
                }
            }

            if(responsavel.isCseSupOrg()){
                if(responsavel.temPermissao(CodedValues.FUN_CONS_USU_SERVIDORES)){
                    acoes.put("consultarServidor", ApplicationResourcesHelper.getMessage("rotulo.consultar.usuario.servidor", responsavel));
                }
            }

            if(podeAnexarFoto){
                final String anexarFoto = ApplicationResourcesHelper.getMessage("rotulo.acao.anexar.foto", responsavel);
                acoes.put("anexarFoto", anexarFoto);
            }

            if ((!responsavel.isCseSupOrg()) || (!responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR))) {
                readOnlyRse = "true";
            }

            final String termoUsoEmail = montarTermoUsoEmail(responsavel);
            model.addAttribute("termoUsoEmail", termoUsoEmail);

            //DESENV-8327: Se campo estiver visível no sistema, lista para ser configurado para o servidor em qual margem
            //             todas consignações irão incidir
            if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, responsavel)) {
                final List<MargemTO> margensRaiz = margemController.lstMargemRaiz(responsavel);
                model.addAttribute("margensRaiz", margensRaiz);
            }

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) && ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, responsavel)) {
                final CustomTransferObject usuarioSer = pesquisarServidorController.buscaUsuarioServidorBySerCodigo(servidor.getSerCodigo(), responsavel);
                final String usuCodigo = usuarioSer.getAttribute(Columns.USU_CODIGO).toString();

                final NotificacaoUsuarioId id = new NotificacaoUsuarioId();
                id.setTnoCodigo(TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo());
                id.setUsuCodigo(usuCodigo);

                final NotificacaoUsuario notificacaoUsuario = notificacaoUsuarioController.find(usuCodigo, TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo(), responsavel);
                final boolean notificacaoAtiva =  ((notificacaoUsuario != null) && (notificacaoUsuario.getNusAtivo() != null) && notificacaoUsuario.getNusAtivo().equals(CodedValues.STS_ATIVO));
                model.addAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, notificacaoAtiva ? "S" : "N");
            }

            // Exibe Botao que leva ao rodapé
            final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            model.addAttribute("obrigInfTodosDadosSer", obrigInfTodosDadosSer);
            model.addAttribute("habilitaSaldoDevedorExclusaoServidor", habilitaSaldoDevedorExclusaoServidor);
            model.addAttribute("exigeDetalheExclusaoSer", exigeDetalheExclusaoSer);
            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("linkRet", linkRet);
            model.addAttribute("linkRetorno", linkRetorno);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("servidor", servidor);
            model.addAttribute("acao", acao);
            model.addAttribute("readOnly", readOnly);
            model.addAttribute("readOnlyRse", readOnlyRse);
            model.addAttribute("listEstadoCivil", listEstadoCivil);
            model.addAttribute("listNivelEscolaridade", listNivelEscolaridade);
            model.addAttribute("listTipoHabitacao", listTipoHabitacao);
            model.addAttribute("rseSelecionado", rseSelecionado);
            model.addAttribute("bloqueioServico", bloqueioServico);
            model.addAttribute("bloqueioNaturezaServico", bloqueioNaturezaServico);
            model.addAttribute("bloqueioConsignataria", bloqueioConsignataria);
            model.addAttribute("bloqueioServidor", bloqueioServidor);
            model.addAttribute("margens", margens);
            model.addAttribute("acoes", acoes);
            model.addAttribute("serEmail", serEmail);
            model.addAttribute("camposObrigatorios", verificarCamposForm(request, session, true, responsavel));

            final TransferObject funcao = usuarioController.getFuncao(CodedValues.FUN_EDT_SERVIDOR, responsavel);
            boolean operacaoSensivel = false;

            if (responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_OP_FILA_AUTORIZACAO)) {
                operacaoSensivel = false;
            } else if (responsavel.isSup()) {
                operacaoSensivel = "F".equals(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP));
            } else if (responsavel.isCse()) {
                operacaoSensivel = "F".equals(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE));
            } else if (responsavel.isOrg()) {
                operacaoSensivel = "F".equals(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG));
            } else if (responsavel.isCsa()) {
                operacaoSensivel = "F".equals(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA));
            } else if (responsavel.isCor()) {
                operacaoSensivel = "F".equals(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR));
            } else {
                operacaoSensivel = false;
            }

            model.addAttribute("operacaoSensivel", operacaoSensivel);

            //Carrega dados adicionais - tabela tb_dados_servidor
            carregarDadosAdicionaisServidor(serCodigo, request, responsavel);

            // Carrega as entidades adicionais para a tela de edição
            recuperarDadosAdicionaisRseServidor(rseSelecionado, request, responsavel);

            return viewRedirect("jsp/editarServidor/editarServidor", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private void carregarDadosAdicionaisServidor(String serCodigo, HttpServletRequest request, AcessoSistema responsavel) throws ServidorControllerException {
        final List<TransferObject> listaDadosAdicionaisServidor = servidorController.lstTipoDadoAdicionalServidorQuery(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, responsavel);

        if ((listaDadosAdicionaisServidor != null) && !listaDadosAdicionaisServidor.isEmpty()) {
            request.setAttribute("listaDadosAdicionaisServidor", listaDadosAdicionaisServidor);

            final Map<String, String> dadosAutorizacao = new HashMap<>();
            for (final TransferObject tda : listaDadosAdicionaisServidor) {
                final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                final String tdaValor = servidorController.getValorDadoServidor(serCodigo, tdaCodigo);
                dadosAutorizacao.put(tdaCodigo, tdaValor);
            }
            request.setAttribute("dadosAutorizacao", dadosAutorizacao);
        }
    }

    private String montarTermoUsoEmail(AcessoSistema responsavel) {
        String termoUsoEmail = "";
        if (ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CONFIRMA_LEITURA_TERMO_MUDANCA_EMAIL, responsavel)) {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "termo_de_uso" + File.separatorChar;
            absolutePath += CodedNames.TEMPLATE_TERMO_CADASTRO_EMAIL_SERVIDOR;

            final File file = new File(absolutePath);
            if ((file != null) && file.isFile() && file.exists()) {
                termoUsoEmail = FileHelper.readAll(absolutePath);
                termoUsoEmail = termoUsoEmail.replaceAll("\\r\\n|\\r|\\n", "");
            }
        }
        return termoUsoEmail;
    }

    protected String verificarCamposForm(HttpServletRequest request, HttpSession session, boolean retornaCamposObrigatorios, AcessoSistema responsavel) throws ZetraException {
        final boolean permiteEdtCpfServidor = ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean obrigInfTodosDadosSer = ParamSist.paramEquals(CodedValues.TPC_OBRIG_INF_TODOS_DADOS_EDT_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        String msgErro;
        String reqColumnsStr = "SER_CODIGO" + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME : "");
        if (permiteEdtCpfServidor && !omiteCpfServidor) {
            reqColumnsStr += (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CPF, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_CPF : "");
        }
        if (obrigInfTodosDadosSer) {
            reqColumnsStr += (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME_PAI : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_TITULACAO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_TITULACAO : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME_MAE : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_SEXO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_SEXO : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_BAIRRO : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_CIDADE : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_UF : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_CEP : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_TELEFONE : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_CELULAR : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, responsavel) ? "|" + FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE : "");
        }

        if(retornaCamposObrigatorios) {
            return reqColumnsStr;
        }
        msgErro = JspHelper.verificaCamposForm(request, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        return msgErro;
    }

    protected ServidorTransferObject buscaServidor(String rseCodigo, HttpSession session, AcessoSistema responsavel) throws InstantiationException, IllegalAccessException, ServidorControllerException {
        return servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        return consultar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "consultar";
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.servidor.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarServidor");
        model.addAttribute("omitirAdeNumero", true);
    }

    @Override
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_CADASTRAR_SERVIDOR)) {
            // Se não encontrou nenhum servidor e o usuário gestor tem permissão de incluir novo, redireciona para a página de inclusão
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.servidor.encontrado.cadastrar.servidor", responsavel));
            final String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/cadastrarServidor?acao=iniciar", request));
            request.setAttribute("url64", link);
            return "jsp/redirecionador/redirecionar";
        }
        return super.tratarSevidorNaoEncontrado(request, response, session, model);
    }

}
