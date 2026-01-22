package com.zetra.econsig.web.controller.historico;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.VisualizarHistoricoDTO;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDescontoHelper;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VisualizarHistoricoWebController</p>
 * <p>Description: Controlador Web para o caso de uso visualizar historico.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$ $Revision$ $Date: 2018-06-11 11:03:06 -0300
 * (Seg, 11 jun 2018) $
 */
@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/visualizarHistorico"})
public class VisualizarHistoricoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarHistoricoWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        String rseCodigo = "";
        boolean carregaAtributos = true;
        boolean exigeCaptcha = false;
        boolean exibeCaptcha = false;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;
        boolean podeConsultar = true;
        final boolean defVisual = responsavel.isDeficienteVisual();

        if (!defVisual) {
            exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            exibeCaptcha = !exibeCaptchaAvancado;
        } else {
            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        }
        final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaHistorico")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptchaHistorico") : "N";

        if (responsavel.isSer()) {
            podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());
        }
        if (!podeConsultar && "S".equals(validaRecaptcha)) {
            if (!defVisual) {
                if (exibeCaptcha) {
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                            && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, "codigoCapHistorico"))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        exigeCaptcha = true;
                        carregaAtributos = false;
                    } else {
                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                    }
                } else if (exibeCaptchaAvancado) {
                    final String remoteAddr = request.getRemoteAddr();

                    if (!isValidCaptcha(request.getParameter("g-recaptcha-response_principal"), remoteAddr, responsavel)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        exigeCaptcha = true;
                        carregaAtributos = false;
                    } else {
                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                    }
                }
            } else {
                final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                if (exigeCaptchaDeficiente) {
                    final String captchaAnswer = JspHelper.verificaVarQryStr(request, "codigoCapHistorico");

                    if (captchaAnswer == null) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        exigeCaptcha = true;
                        carregaAtributos = false;
                    }

                    final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        exigeCaptcha = true;
                        carregaAtributos = false;
                    } else {
                        session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                    }
                }
            }
        } else if (!podeConsultar && "N".equals(validaRecaptcha)) {
            exigeCaptcha = true;
            carregaAtributos = false;
        }

        if (carregaAtributos) {
            if (!responsavel.isSer()) {
                // Valida o token de sessão para evitar a chamada direta da operação
                if (!SynchronizerToken.isTokenValid(request)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                SynchronizerToken.saveToken(request);
                rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            } else {
                rseCodigo = responsavel.getRseCodigo();
            }

            if ((!responsavel.isCseSupOrg() && !responsavel.isSer()) || TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os dados e as margens do servidor
            CustomTransferObject servidor = null;
            List<MargemTO> margensServidor = null;
            try {
                // Busca os dados do servidor
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                // Busca as margens do servidor
                margensServidor = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, true, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Dados para paginação
            int total = 0;

            // Pesquisa o histórico de margem
            List<TransferObject> historico = null;
            try {
                // Define os critérios de pesquisa avançada
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("marCodigo", request.getParameter("marCodigo"));
                criterio.setAttribute("adeNumero", request.getParameter("adeNumero"));
                criterio.setAttribute("periodoIni", request.getParameter("periodoIni"));
                criterio.setAttribute("periodoFim", request.getParameter("periodoFim"));
                criterio.setAttribute("hmrOperacao", request.getParameter("hmrOperacao"));

                // Obtem o total de registros
                total = servidorController.countHistoricoMargem(rseCodigo, criterio, responsavel);

                if (total > 0) {
                    // Se encontrou algum histórico, retorna os registros para o detalhe
                    final int size = JspHelper.LIMITE;
                    int offset = 0;
                    try {
                        offset = Integer.parseInt(request.getParameter("offset"));
                    } catch (final Exception ex) {
                    }
                    // Executa a pesquisa paginada do histórico
                    historico = servidorController.pesquisarHistoricoMargem(rseCodigo, offset, size, criterio, responsavel);
                }

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("senha");
                params.remove("serAutorizacao");
                params.remove("cryptedPasswordFieldName");
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                final List<String> requestParams = new ArrayList<>(params);

                final String linkListagem = "../v3/visualizarHistorico?acao=iniciar";
                configurarPaginador(linkListagem, "rotulo.paginacao.titulo.mensagem", total, JspHelper.LIMITE, requestParams, false, request, model);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<MargemTO> margens = null;
            try {
                margens = margemController.lstMargemRaiz(responsavel);

                // Remove o MAR_CODIGO = 0 (TODO Fazer isso na pesquisa)
                int i = -1;
                for (i = 0; i < margens.size(); i++) {
                    final MargemTO margemTO = margens.get(i);
                    if (margemTO.getMarCodigo().equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        break;
                    }
                }
                if ((i >= 0) && (i < margens.size())) {
                    margens.remove(i);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            final List<VisualizarHistoricoDTO> visualizarHistoricoLst = new ArrayList<>();

            if ((historico != null) && (historico.size() > 0)) {

                TransferObject registro = null;

                String adeVlr, labelTipoVlr, adeNumero, adeCodigo;
                String tocCodigo, tocDescricao, ocaObs, ocaTmo;
                String marDescricao;
                String hmrOperacao, hmrData;
                String hmrMargemAntes, hmrMargemDepois;
                String descricao = "";
                OperacaoHistoricoMargemEnum operacaoEnum;

                final Iterator<TransferObject> it = historico.iterator();
                while (it.hasNext()) {
                    registro = it.next();

                    marDescricao = registro.getAttribute(Columns.MAR_DESCRICAO).toString();
                    hmrData = DateHelper.toDateTimeString((java.sql.Timestamp) registro.getAttribute(Columns.HMR_DATA));
                    hmrOperacao = registro.getAttribute(Columns.HMR_OPERACAO).toString();
                    operacaoEnum = OperacaoHistoricoMargemEnum.recuperaOperacaoHistoricoMargemEnum(hmrOperacao);
                    if (!operacaoEnum.equals(OperacaoHistoricoMargemEnum.CONSIGNACAO)) {
                        descricao = operacaoEnum.getDescricao();
                    }

                    hmrMargemAntes = registro.getAttribute(Columns.HMR_MARGEM_ANTES) != null ? NumberHelper.reformat(registro.getAttribute(Columns.HMR_MARGEM_ANTES).toString(), "en", NumberHelper.getLang()) : "0,00";
                    hmrMargemDepois = registro.getAttribute(Columns.HMR_MARGEM_DEPOIS) != null ? NumberHelper.reformat(registro.getAttribute(Columns.HMR_MARGEM_DEPOIS).toString(), "en", NumberHelper.getLang()) : "0,00";

                    adeNumero = registro.getAttribute(Columns.ADE_NUMERO) != null ? registro.getAttribute(Columns.ADE_NUMERO).toString() : "";
                    adeCodigo = registro.getAttribute(Columns.ADE_CODIGO) != null ? registro.getAttribute(Columns.ADE_CODIGO).toString() : "";
                    tocCodigo = registro.getAttribute(Columns.TOC_CODIGO) != null ? registro.getAttribute(Columns.TOC_CODIGO).toString() : "";
                    tocDescricao = registro.getAttribute(Columns.TOC_DESCRICAO) != null ? registro.getAttribute(Columns.TOC_DESCRICAO).toString() : "";
                    ocaObs = registro.getAttribute(Columns.OCA_OBS) != null ? registro.getAttribute(Columns.OCA_OBS).toString() : "";
                    ocaTmo = registro.getAttribute(Columns.TMO_DESCRICAO) != null ? registro.getAttribute(Columns.TMO_DESCRICAO).toString() : "";

                    if (!"".equals(adeCodigo)) {
                        if (CodedValues.TOC_INFORMACAO.equals(tocCodigo)) {
                            if (ocaObs.startsWith(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.prefixo", responsavel))) {
                                descricao = StatusAutorizacaoDescontoHelper.formataOcaObsHtml(ocaObs, ocaTmo, responsavel);
                            } else {
                                descricao = tocDescricao + ": " + ocaObs;
                            }
                        } else if (CodedValues.TOC_TARIF_RESERVA.equals(tocCodigo)) {
                            descricao = ApplicationResourcesHelper.getMessage("mensagem.historico.margem.ocorrencia.inclusao", responsavel);
                        } else {
                            descricao = tocDescricao + ": " + ocaObs;
                        }
                    }

                    labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr((String) registro.getAttribute(Columns.ADE_TIPO_VLR));
                    adeVlr = registro.getAttribute(Columns.ADE_VLR) != null ? registro.getAttribute(Columns.ADE_VLR).toString() : "";
                    if (!"".equals(adeVlr)) {
                        adeVlr = NumberHelper.format(Double.parseDouble(adeVlr), NumberHelper.getLang());
                    } else {
                        labelTipoVlr = "";
                    }

                    visualizarHistoricoLst.add(new VisualizarHistoricoDTO(hmrData, marDescricao, descricao, adeNumero, labelTipoVlr, adeVlr, hmrMargemAntes, hmrMargemDepois, adeCodigo, hmrOperacao, rseCodigo));

                }
            }
            model.addAttribute("servidor", servidor);
            model.addAttribute("margensServidor", margensServidor);
            model.addAttribute("margens", margens);
            model.addAttribute("visualizarHistoricoLst", visualizarHistoricoLst);

        }
        String destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
        if (destinoBotaoVoltar.contains("consultarMargem")) {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
        } else {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
        }

        //Exibe botao que leva ao rodapé
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("carregaAtributos", carregaAtributos);
        model.addAttribute("exigeCaptcha", exigeCaptcha);
        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("paramSession", paramSession);
        model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);

        return viewRedirect("jsp/visualizarHistorico/visualizarHistorico", request, session, model, responsavel);
    }

}
