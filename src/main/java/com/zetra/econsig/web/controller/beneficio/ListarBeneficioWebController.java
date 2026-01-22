package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarBeneficioWebController</p>
 * <p>Description: Listar e consultar benefícios</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarBeneficio" })
public class ListarBeneficioWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

    @Autowired
    private NaturezaServicoController naturezaServicoController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.lista.beneficios.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());

    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_ALTERAR_CADASTRO_BENEFICIOS);
            List<TransferObject> beneficios = null;

            // Pega o filtro e o tipo do filtro vindo do request
            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;

            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.NCA_CODIGO, NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo());
            criterio.setAttribute("filtro", filtro);
            criterio.setAttribute("filtro_tipo", "" + filtro_tipo);

            int total = beneficioController.lstCountBeneficioCsaOperadora(criterio, responsavel);

            int size = JspHelper.LIMITE;
            int offset = 0;

            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            beneficios = beneficioController.lstBeneficioCsaOperadoraPaginacao(criterio, offset, size, responsavel);

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.beneficio", total, size, requestParams, false, request, model);

            // Seta atributos no model
            model.addAttribute("podeEditar", podeEditar);
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("beneficios", beneficios);

            return viewRedirect("jsp/manterBeneficio/listarBeneficio", request, session, model, responsavel);

        } catch (BeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String ben_codigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO));
        Beneficio beneficio = null;
        List<Consignataria> operadoras = new ArrayList<>();
        List<NaturezaServico> naturezas = new ArrayList<>();

        try {
            beneficio = beneficioController.findBeneficioByCodigo(ben_codigo, responsavel);

            operadoras = consignatariaController.lstConsignatariaByNcaCodigo(NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo(), responsavel);

            naturezas = naturezaServicoController.listaNaturezas(responsavel);

        } catch (BeneficioControllerException | ConsignatariaControllerException | NaturezaServicoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<CustomTransferObject> operadorasBeneficios = new ArrayList<>();

        for (Consignataria consig : operadoras) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.CSA_IDENTIFICADOR, consig.getCsaIdentificador());
            cto.setAttribute(Columns.CSA_CODIGO, consig.getCsaCodigo());
            cto.setAttribute(Columns.CSA_NOME, consig.getCsaNome());
            operadorasBeneficios.add(cto);
        }

        List<CustomTransferObject> naturezasServico = new ArrayList<>();

        for (NaturezaServico natureza : naturezas) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.NSE_CODIGO, natureza.getNseCodigo());
            cto.setAttribute(Columns.NSE_DESCRICAO, natureza.getNseDescricao());
            naturezasServico.add(cto);
        }

        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", false);
        model.addAttribute("beneficio", beneficio);
        model.addAttribute("operadoras", operadorasBeneficios);
        model.addAttribute("naturezas", naturezasServico);

        return viewRedirect("jsp/manterBeneficio/alterarBeneficio", request, session, model, responsavel);
    }

    private String getLinkAction() {
        return "../v3/listarBeneficio?acao=listar";
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String modificarBloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);
        String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
        String link = linkVoltar.equals("") ? paramSession.getLastHistory() : linkVoltar;
        link = SynchronizerToken.updateTokenInURL(link, request);
        String benCodigo = request.getParameter("ben_codigo");
        String status = request.getParameter("status");
        String csaNome = request.getParameter("csa_nome");

        if (status != null) {
            try {
                Beneficio beneficio = beneficioController.findBeneficioByCodigo(benCodigo, responsavel);
                boolean bloqueado = status.equals(CodedValues.STS_INATIVO.toString());

                if (bloqueado) {
                    beneficioController.desbloqueioBeneficio(beneficio, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.beneficio.desbloqueado.csa", responsavel, TextHelper.forHtmlContent(beneficio.getBenDescricao()), TextHelper.forHtmlContent(csaNome)));

                } else {
                    beneficioController.bloqueioBeneficio(beneficio, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.beneficio.bloqueado.csa", responsavel, TextHelper.forHtmlContent(beneficio.getBenDescricao()), TextHelper.forHtmlContent(csaNome)));
                }

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }
}
