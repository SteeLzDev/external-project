package com.zetra.econsig.web.controller.margem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.parametro.ParamSist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCalculoMargem;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;
/**
 * <p>Title: RecalcularMargemGeralWebController</p>
 * <p>Description: Controlador Web para o caso de uso Recalcular Margem Geral.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 29011 $
 * $Date: 2020-03-25 13:25:44 -0300 (qui, 25 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/recalcularMargemGeral" })
public class RecalcularMargemGeralWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecalcularMargemGeralWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @SuppressWarnings("unchecked")
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Como vem da página inicial apenas salva o token
        SynchronizerToken.saveToken(request);

        String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
        // Verifica se existe algum processo rodando para o usuário
        boolean temProcessoRodando = !TextHelper.isNull(request.getAttribute("temProcessoRodando")) ? (boolean) request.getAttribute("temProcessoRodando") : ControladorProcessos.getInstance().verificar(chave, session);

        // Se veio do método Confirmar -> Já carregou os dados, logo não precisa carregar novamente
        if (!temProcessoRodando) {
            carregarDadosRecalculoMargemGeral(responsavel, request);
        }

        String direction = JspHelper.verificaVarQryStr(request, "direction");
        String tipoEntidade = (String) request.getAttribute("tipoEntidade");
        String codigoEntidade = (String) request.getAttribute("codigoEntidade");
        String estCodigo = (String) request.getAttribute("estCodigo");
        String orgCodigo = (String) request.getAttribute("orgCodigo");
        TransferObject criterio = (TransferObject) request.getAttribute("criterio");
        List<TransferObject> lstEstabelecimentos = (List<TransferObject>) request.getAttribute("lstEstabelecimentos");
        List<TransferObject> lstOrgaos = (List<TransferObject>) request.getAttribute("lstOrgaos");


        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        for (Map.Entry<String, String[]> parametro : parameterMap.entrySet()) {
            if (parametro.getKey().equals("acao")) {
                parametro.setValue(new String[] { "iniciar" });
            }
        }
        String linkRefresh = SynchronizerToken.updateTokenInURL(JspHelper.makeURL("../v3/recalcularMargemGeral", parameterMap), request);

        // Exibe Botao que leva ao rodapé
        boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("direction", direction);
        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("chave", chave);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("tipoEntidade", tipoEntidade);
        model.addAttribute("codigoEntidade", codigoEntidade);
        model.addAttribute("estCodigo", estCodigo);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("criterio", criterio);
        model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);
        model.addAttribute("lstOrgaos", lstOrgaos);
        model.addAttribute("linkRefresh", linkRefresh);

        return viewRedirect("jsp/recalcularMargem/recalcularMargemGeral", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmarRecalculoMargemGeral(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
        model.addAttribute("direction", request.getAttribute( "direction"));

        if (!temProcessoRodando) {
            carregarDadosRecalculoMargemGeral(responsavel, request);

            String tipoEntidade = (String) request.getAttribute("tipoEntidade");
            String codigoEntidade = (String) request.getAttribute("codigoEntidade");

            // Criar processo de recálculo de margem
            ProcessaCalculoMargem processo = new ProcessaCalculoMargem(tipoEntidade, codigoEntidade, responsavel);
            processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.recalcula.margem.titulo", responsavel));
            processo.start();
            ControladorProcessos.getInstance().incluir(chave, processo);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.recalcula.margem.processando", responsavel));
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        }

        return iniciar(request, response, session, model);
    }

    public void carregarDadosRecalculoMargemGeral(AcessoSistema responsavel, HttpServletRequest request) {
        String tipoEntidade = responsavel.isSup() ? AcessoSistema.ENTIDADE_CSE : responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            tipoEntidade = AcessoSistema.ENTIDADE_EST;
            codigoEntidade = responsavel.getCodigoEntidadePai();
        }

        String estCodigo = JspHelper.verificaVarQryStr(request, "estCodigo");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");
        TransferObject criterio = null;

        if (responsavel.isCseSup()) {
            // Usuário de Consignante e Suporte podem editar calendário das demais entidades
            if (!estCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = estCodigo;
            } else if (!orgCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            }
        } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.EST_CODIGO, responsavel.getCodigoEntidadePai());

            // Usuário de órgão com acesso ao estabelecimento pode editar o calendário
            // do estabelecimento ou de um dos órgãos do estabelecimento
            if (!orgCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            } else {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }
        }
        List<TransferObject> lstEstabelecimentos = new ArrayList<>();
        List<TransferObject> lstOrgaos = new ArrayList<>();

        try {
            lstEstabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);
            lstOrgaos = consignanteController.lstOrgaos(criterio, responsavel);
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        request.setAttribute("tipoEntidade", tipoEntidade);
        request.setAttribute("codigoEntidade", codigoEntidade);
        request.setAttribute("estCodigo", estCodigo);
        request.setAttribute("orgCodigo", orgCodigo);
        request.setAttribute("criterio", criterio);
        request.setAttribute("lstEstabelecimentos", lstEstabelecimentos);
        request.setAttribute("lstOrgaos", lstOrgaos);

    }
}
