package com.zetra.econsig.web.controller.leilao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FiltroLeilaoSolicitacaoTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.AcompanharLeilaoModel;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AcompanharLeilaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Acompanhar Leilão reverso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28168 $
 * $Date: 2019-11-05 17:02:21 -0300 (ter, 05 nov 2019) $
 */
@Controller
@RequestMapping(value = { "/v3/acompanharLeilao" })
public class AcompanharLeilaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AcompanharLeilaoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        // Busca os convênios da consignatária para participação no leilão, e caso não tenha, exibe mensagem que não tem convênio ativo.
        try {
            final List<TransferObject> lstConvenios = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "leilao", responsavel);
            if (lstConvenios == null || lstConvenios.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioAtivo", responsavel));
                request.setAttribute("tipo", "principal");
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final Boolean pesquisarAttr = (Boolean) model.asMap().get("pesquisar");
        boolean pesquisar = (pesquisarAttr != null) ? pesquisarAttr : JspHelper.verificaVarQryStr(request, "pesquisar").equals("true");
        if (responsavel.isSer()) {
            pesquisar = true;
        }

        // Verifica as permissões do usuário para exibição dos ícones
        final boolean podeEdtProposta = responsavel.temPermissao(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO);
        final boolean podeConsultarAde = responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNACAO);
        final boolean temRiscoPelaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, responsavel);
        final boolean podecadastrarRisco = temRiscoPelaCsa && responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_CADASTRO_RISCO_SERVIDOR_CSA);

        final String filtro = JspHelper.verificaVarQryStr(request, "filtro");
        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final String rsePontuacaoFiltro = JspHelper.verificaVarQryStr(request, "RSE_PONTUACAO");
        final String arrRiscoFiltro = JspHelper.verificaVarQryStr(request, "ARR_RISCO");
        final String rseMargemLivreFiltro = JspHelper.verificaVarQryStr(request, "RSE_MARGEM_LIVRE");

        final String dataAberturaIni = JspHelper.verificaVarQryStr(request, "dataAberturaIni");
        final String dataAberturaFim = JspHelper.verificaVarQryStr(request, "dataAberturaFim");

        // Monta lista de parâmetros através dos parâmetros de request
        final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        // Ignora os parâmetros abaixo
        parameterMap.remove("offset");
        parameterMap.remove("back");

        List<TransferObject> lstResultado = (List<TransferObject>) model.asMap().get("lstResultado");

        if (pesquisar && lstResultado == null) {
            lstResultado = pesquisar(request, session, model, responsavel, filtro, tipo, dataAberturaIni, dataAberturaFim, parameterMap);
        }

        int colspan = 0;
        if (podeEdtProposta) {
            colspan++;
        }
        if (podecadastrarRisco) {
            colspan++;
        }
        if (podeConsultarAde && (responsavel.isCseSupOrg() || responsavel.isSer())) {
            colspan++;
        }

        // Verifica mensagem de leilões não concretizados para o servidor
        Object paramQtdMaxLeilaoNaoConcretizado = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_LEILOES_CANCELADOS_PARA_BLOQUEIO_SER, responsavel);
        int qtdMaxLeilaoNaoConcretizado = !TextHelper.isNull(paramQtdMaxLeilaoNaoConcretizado) ? Integer.parseInt(paramQtdMaxLeilaoNaoConcretizado.toString()) : 0;
        if (responsavel.isSer() && qtdMaxLeilaoNaoConcretizado > 1) {
            int qtdLeilaoNaoConcretizado = -1;
            try {
                qtdLeilaoNaoConcretizado = leilaoSolicitacaoController.countSolicitacaoLeilaoCanceladoParaBloqueio(responsavel.getRseCodigo(), responsavel);
            } catch (LeilaoSolicitacaoControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            }
            if (qtdLeilaoNaoConcretizado > 0) {
                Object paramQtdDiasBloqSer = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_SERVIDOR_COM_LEILAO_CANCELADO, AcessoSistema.getAcessoUsuarioSistema());
                int qtdDiasBloqSer = (!TextHelper.isNull(paramQtdDiasBloqSer) && TextHelper.isNum(paramQtdDiasBloqSer)) ? Integer.parseInt(paramQtdDiasBloqSer.toString()) : 0;

                String msgLeilaoNaoConcretizado = ApplicationResourcesHelper.getMessage("mensagem.info.servidor.leilao.nao.concretizado", responsavel, String.valueOf(qtdLeilaoNaoConcretizado), String.valueOf(qtdMaxLeilaoNaoConcretizado), String.valueOf(qtdDiasBloqSer));
                session.setAttribute(CodedValues.MSG_ALERT, msgLeilaoNaoConcretizado);
            }
        }

        final AcompanharLeilaoModel acompanharLeilaoModel = new AcompanharLeilaoModel();

        acompanharLeilaoModel.setFiltro(filtro);
        acompanharLeilaoModel.setTipo(tipo);
        acompanharLeilaoModel.setDataAberturaIni(dataAberturaIni);
        acompanharLeilaoModel.setDataAberturaFim(dataAberturaFim);
        acompanharLeilaoModel.setRsePontuacaoFiltro(rsePontuacaoFiltro);
        acompanharLeilaoModel.setTemRiscoPelaCsa(temRiscoPelaCsa);
        acompanharLeilaoModel.setArrRiscoFiltro(arrRiscoFiltro);
        acompanharLeilaoModel.setRseMargemLivreFiltro(rseMargemLivreFiltro);
        acompanharLeilaoModel.setLstResultado(lstResultado);
        acompanharLeilaoModel.setColspan(colspan);
        acompanharLeilaoModel.setPodeEdtProposta(podeEdtProposta);
        acompanharLeilaoModel.setPodecadastrarRisco(podecadastrarRisco);
        acompanharLeilaoModel.setPodeConsultarAde(podeConsultarAde);

        if (!responsavel.isSer()) {
            //mostra combo para selecao de posto do servidor
            List<TransferObject> postos = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                postos = postoRegistroServidorController.lstPostoRegistroServidor(criterio, -1, -1, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                postos = new ArrayList<>();
            }

            acompanharLeilaoModel.setPostos(postos);
        }

        model.addAttribute("acompanharLeilaoModel", acompanharLeilaoModel);

        return viewRedirect("jsp/leilao/acompanharLeilaoReverso", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String filtro = JspHelper.verificaVarQryStr(request, "filtro");
        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final String dataAberturaIni = JspHelper.verificaVarQryStr(request, "dataAberturaIni");
        final String dataAberturaFim = JspHelper.verificaVarQryStr(request, "dataAberturaFim");

        // Monta lista de parâmetros através dos parâmetros de request
        final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        // Ignora os parâmetros abaixo
        parameterMap.remove("offset");
        parameterMap.remove("back");

        final List<TransferObject> lstResultado = pesquisar(request, session, model, responsavel, filtro, tipo, dataAberturaIni, dataAberturaFim, parameterMap);
        model.addAttribute("pesquisar", false);
        model.addAttribute("lstResultado", lstResultado);

        return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciarCriacaoFiltro" })
    public String iniciarCriacaoFiltro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean temRiscoPelaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, responsavel);

        final String filtro = JspHelper.verificaVarQryStr(request, "filtro");
        final String rsePontuacaoFiltro = JspHelper.verificaVarQryStr(request, "RSE_PONTUACAO");
        final String arrRiscoFiltro = JspHelper.verificaVarQryStr(request, "ARR_RISCO");
        final String dataAberturaIni = JspHelper.verificaVarQryStr(request, "dataAberturaIni");
        final String dataAberturaFim = JspHelper.verificaVarQryStr(request, "dataAberturaFim");
        final String rseMargemLivreFiltro = JspHelper.verificaVarQryStr(request, "RSE_MARGEM_LIVRE");
        final String posCodigo = JspHelper.verificaVarQryStr(request, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR);

        // Preenche o email do usuário, caso este esteja cadastrado
        String email = null;
        try {
            UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (TextHelper.isNull(usuario.getUsuEmail())) {
                email = TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "email"));
            } else {
                email = usuario.getUsuEmail();
            }

            boolean desabilitado = !ShowFieldHelper.canEdit(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, responsavel);
            model.addAttribute("desabilitado", desabilitado);
        } catch (ZetraException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!responsavel.isSer()) {//mostra combo para selecao de posto do servidor
            List<TransferObject> postos = null;
            try {
                final CustomTransferObject criterio = new CustomTransferObject();
                postos = postoRegistroServidorController.lstPostoRegistroServidor(criterio,-1,-1,responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                postos = new ArrayList<>();
            }

            model.addAttribute("postos", postos);
        }

        model.addAttribute("email", email);
        model.addAttribute("filtro", filtro);
        model.addAttribute("dataAberturaIni", dataAberturaIni);
        model.addAttribute("dataAberturaFim", dataAberturaFim);
        model.addAttribute("rsePontuacaoFiltro", rsePontuacaoFiltro);
        model.addAttribute("temRiscoPelaCsa", temRiscoPelaCsa);
        model.addAttribute("arrRiscoFiltro", arrRiscoFiltro);
        model.addAttribute("posCodigo", posCodigo);
        model.addAttribute("rseMargemLivreFiltro", rseMargemLivreFiltro);

        return viewRedirect("jsp/leilao/editarFiltroAcompanhamentoLeilao", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvarFiltro" })
    public String salvarFiltro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String rseMargemLivreFiltro = JspHelper.verificaVarQryStr(request, "RSE_MARGEM_LIVRE");
            final FiltroLeilaoSolicitacaoTO filtroTO = new FiltroLeilaoSolicitacaoTO();
            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "descricao"))) {
                filtroTO.setFlsDescricao(JspHelper.verificaVarQryStr(request, "descricao"));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
                throw new IllegalArgumentException();
            }
            if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, "email"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
                throw new IllegalArgumentException();
            }
            if (TextHelper.isEmailValid(JspHelper.verificaVarQryStr(request, "email"))) {
                filtroTO.setFlsEmailNotificacao(JspHelper.verificaVarQryStr(request, "email"));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.email.valido", responsavel));
                throw new IllegalArgumentException();
            }

            filtroTO.setFlsTipoPesquisa(JspHelper.verificaVarQryStr(request, "filtro"));

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "dataAberturaIni"))) {
                Date dataIni = DateHelper.parse(JspHelper.verificaVarQryStr(request, "dataAberturaIni"), LocaleHelper.getDatePattern());
                filtroTO.setFlsDataAberturaInicial(dataIni);
            }
            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "dataAberturaFim"))) {
                Date dataFim = DateHelper.parse(JspHelper.verificaVarQryStr(request, "dataAberturaFim"), LocaleHelper.getDatePattern());
                filtroTO.setFlsDataAberturaFinal(dataFim);
            }
            if (TextHelper.isNum(JspHelper.verificaVarQryStr(request, "horasFimLeilao"))) {
                Short horas = Short.valueOf(JspHelper.verificaVarQryStr(request, "horasFimLeilao"));
                filtroTO.setFlsHorasEncerramento(horas);
            }
            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR))) {
                filtroTO.setPosCodigo(JspHelper.verificaVarQryStr(request, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR));
            }

            if (TextHelper.isNum(JspHelper.verificaVarQryStr(request, "RSE_PONTUACAO"))) {
                Integer pontuacaoMin = Integer.valueOf(JspHelper.verificaVarQryStr(request, "RSE_PONTUACAO"));
                filtroTO.setFlsPontuacaoMinima(pontuacaoMin);
            }

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ARR_RISCO"))) {
                filtroTO.setFlsAnaliseRisco(JspHelper.verificaVarQryStr(request, "ARR_RISCO"));
            }

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CID_CODIGO"))) {
                filtroTO.setCidCodigo(JspHelper.verificaVarQryStr(request, "CID_CODIGO"));
            }

            if (!TextHelper.isNull(rseMargemLivreFiltro)) {
                filtroTO.setFlsMargemLivreMax(Integer.valueOf(rseMargemLivreFiltro));
            }

            filtroTO.setFlsMatricula(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"));
            filtroTO.setFlsCpf(JspHelper.verificaVarQryStr(request, "SER_CPF"));
            filtroTO.setUsuCodigo(responsavel.getUsuCodigo());

            leilaoSolicitacaoController.criarFiltroLeilaoSolicitacao(filtroTO, responsavel);

            final ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/acompanharLeilao?acao=iniciar", request)));
            return "jsp/redirecionador/redirecionar";
        } catch (IllegalArgumentException | LeilaoSolicitacaoControllerException | ParseException ex) {
            // Não é necessário fazer nada, só setar a mensagem de erro no session
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return iniciarCriacaoFiltro(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=visualizarFiltro" })
    public String visualizarFiltro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<TransferObject> lstResultado = null;

        try {
            lstResultado = leilaoSolicitacaoController.listarFiltros(responsavel);
        } catch (LeilaoSolicitacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("lstResultado", lstResultado);

        return viewRedirect("jsp/leilao/listarFiltrosAcompanhamentoLeilao", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=excluirFiltro" })
    public String excluirFiltro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String codigo = JspHelper.verificaVarQryStr(request, "codigo");

        try {
            leilaoSolicitacaoController.excluirFiltroLeilaoSolicitacao(codigo, responsavel);
        } catch (LeilaoSolicitacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        return visualizarFiltro(request, response, session, model);
    }

    private List<TransferObject> pesquisar(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, String filtro, String tipo, String dataAberturaIni, String dataAberturaFim, Map<String, String[]> parameterMap) {
        List<TransferObject> lstResultado = null;

        try {
            final TransferObject criteriosPesquisa = new CustomTransferObject();

            // Para servidor lista somente leilões abertos
            if (responsavel.isSer()) {
                criteriosPesquisa.setAttribute("filtro", "4");

            } else {
                if (responsavel.isCsaCor()) {
                    criteriosPesquisa.setAttribute("tipo", tipo);
                } else {
                    criteriosPesquisa.setAttribute("tipo", "0");
                }

                if (responsavel.isCsaCor()) {
                    criteriosPesquisa.setAttribute("filtro", filtro);
                } else {
                    criteriosPesquisa.setAttribute("filtro", "4");
                }

                if (!TextHelper.isNull(dataAberturaIni)) {
                    criteriosPesquisa.setAttribute("dataAberturaIni", dataAberturaIni);
                }
                if (!TextHelper.isNull(dataAberturaFim)) {
                    criteriosPesquisa.setAttribute("dataAberturaFim", dataAberturaFim);
                }

                if (!TextHelper.isNull(request.getParameter("horasFimLeilao"))) {
                    criteriosPesquisa.setAttribute("horasFimLeilao", request.getParameter("horasFimLeilao"));
                }

                if (!TextHelper.isNull(request.getParameter("RSE_MATRICULA"))) {
                    criteriosPesquisa.setAttribute("RSE_MATRICULA", request.getParameter("RSE_MATRICULA"));
                }

                if (!TextHelper.isNull(request.getParameter("SER_CPF"))) {
                    criteriosPesquisa.setAttribute("SER_CPF", request.getParameter("SER_CPF"));
                }

                if (!TextHelper.isNull(request.getParameter("CID_CODIGO")) && !TextHelper.isNull(request.getParameter("CID_NOME"))) {
                    criteriosPesquisa.setAttribute("CID_CODIGO", request.getParameter("CID_CODIGO"));
                }

                if (!TextHelper.isNull(request.getParameter("RSE_PONTUACAO"))) {
                    criteriosPesquisa.setAttribute("RSE_PONTUACAO", request.getParameter("RSE_PONTUACAO"));
                }

                if (!TextHelper.isNull(request.getParameter("ARR_RISCO"))) {
                    criteriosPesquisa.setAttribute("ARR_RISCO", request.getParameter("ARR_RISCO"));
                }

                if (!TextHelper.isNull(request.getParameter("RSE_MARGEM_LIVRE"))) {
                    criteriosPesquisa.setAttribute("RSE_MARGEM_LIVRE", request.getParameter("RSE_MARGEM_LIVRE"));
                }

                if (!TextHelper.isNull(request.getParameter(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR))) {
                    criteriosPesquisa.setAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, request.getParameter(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR));
                }

                if (!TextHelper.isNull(request.getParameter("ORDENACAO_AUX"))) {
                    criteriosPesquisa.setAttribute("ORDENACAO", request.getParameter("ORDENACAO_AUX"));
                }

            }

            final int total = leilaoSolicitacaoController.contarLeilaoSolicitacao(criteriosPesquisa, responsavel);

            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

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

            // Monta link de paginação
            configurarPaginador("../v3/acompanharLeilao?acao=pesquisar&filtro=" + filtro + "&", "rotulo.editar.proposta.leilao.listar.proposta", total, size, requestParams, false, request, model);

            if (total > 0) {
                // Realiza a pesquisa
                lstResultado = leilaoSolicitacaoController.acompanharLeilaoSolicitacao(criteriosPesquisa, offset, size, responsavel);
            } else {
                lstResultado = new ArrayList<>();
            }
        } catch (LeilaoSolicitacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        return lstResultado;
    }
}
