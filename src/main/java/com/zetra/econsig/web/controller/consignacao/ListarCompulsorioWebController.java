package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarCompulsorioWebController</p>
 * <p>Description: Controlador Web para o caso de uso listar compulsório.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarCompulsorio" })
public class ListarCompulsorioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarCompulsorioWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=alterar" })
    public String iniciarAlterarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=reservar" })
    public String iniciarReservarMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");

            // Busca os parâmetros de sistema necessários
            Object objTemControleEstoque = ParamSist.getInstance().getParam(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, responsavel);
            Object objTemControleCompulsorios = ParamSist.getInstance().getParam(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, responsavel);
            boolean temControleCompulsorios = (objTemControleEstoque != null && objTemControleEstoque.equals(CodedValues.TPC_SIM) &&
                                               objTemControleCompulsorios != null && objTemControleCompulsorios.equals(CodedValues.TPC_SIM));

            if (!temControleCompulsorios) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.compulsorios.possui.controle", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os dados do servidor
            CustomTransferObject servidor = null;
            try {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os dados do serviço
            CustomTransferObject servico = null;
            try {
                servico = convenioController.findServico(svcCodigo, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (servico == null || servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String svcPrioridade = servico.getAttribute(Columns.SVC_PRIORIDADE) != null ? servico.getAttribute(Columns.SVC_PRIORIDADE).toString() : null;
            String serNome = servidor.getAttribute(Columns.SER_NOME) + " - " + servidor.getAttribute(Columns.RSE_MATRICULA);

            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            Short incMargem = paramSvcCse.getTpsIncideMargem();
            boolean servicoCompulsorio = paramSvcCse.isTpsServicoCompulsorio();

            if (!servicoCompulsorio) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.compulsorios.servico.compulsorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Pesquisa os contratos
            List<TransferObject> ades = null;
            BigDecimal margemDisponivelCompulsorio = new BigDecimal("0.00");
            try {
                ades = pesquisarConsignacaoController.pesquisarContratosIncComp(rseCodigo, svcCodigo, svcPrioridade, responsavel);
                boolean controlaMargem = !ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel);
                margemDisponivelCompulsorio = consultarMargemController.getMargemDisponivelCompulsorio(rseCodigo, svcCodigo, svcPrioridade, incMargem, controlaMargem, adeCodigo, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("serNome", serNome);
            model.addAttribute("ades", ades);
            model.addAttribute("margemDisponivelCompulsorio", margemDisponivelCompulsorio);

            return viewRedirect("jsp/consultarConsignacao/listarCompulsorio", request, session, model, responsavel);

        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.compulsorios.titulo", responsavel));
    }

}
