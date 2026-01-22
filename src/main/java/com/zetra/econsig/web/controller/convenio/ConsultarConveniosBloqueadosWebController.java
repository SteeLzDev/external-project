package com.zetra.econsig.web.controller.convenio;

import java.util.List;

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
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ConsultarConveniosBloqueadosWebController</p>
 * <p>Description: Controlador Web para o caso de uso consultar convênios bloqueados para o servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 28016 $
 * $Date: 2019-10-11 17:56:22 -0300 (sex, 11 out 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarConveniosBloqueados" })
public class ConsultarConveniosBloqueadosWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarConveniosBloqueadosWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        SynchronizerToken.saveToken(request);

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }
        boolean cancelar = false;
        if (JspHelper.verificaVarQryStr(request, "CANCELAR").equalsIgnoreCase("true")) {
            cancelar = true;
        }

        CustomTransferObject servidor = null;
        List<TransferObject> servicosBloqueados = null;
        List<TransferObject> conveniosBloqueados = null;
        List<TransferObject> naturezasServicoBloqueados = null;
        try {
            // Busca os dados do servidor
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            // Pesquisa os serviços bloqueados
            servicosBloqueados = parametroController.lstBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);

            // Pesquisa os convênios bloqueados
            conveniosBloqueados = servidorController.selectConvenioBloqueados(rseCodigo, responsavel.getOrgCodigo(), responsavel.getCsaCodigo(), responsavel);

            //Pesquisa os serviços bloqueados
            naturezasServicoBloqueados = parametroController.lstBloqueioNseRegistroServidor(rseCodigo, null, responsavel);
        } catch (ServidorControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String destinoBotaoVoltar = null;

        ParamSession paramSession = ParamSession.getParamSession(session);
        destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
        if (destinoBotaoVoltar.contains("consultarMargem")) {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
        } else {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
        }

        model.addAttribute("servicosBloqueados", servicosBloqueados);
        model.addAttribute("servidor", servidor);
        model.addAttribute("conveniosBloqueados", conveniosBloqueados);
        model.addAttribute("naturezasServicoBloqueados", naturezasServicoBloqueados);
        model.addAttribute("cancelar", cancelar);
        model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);

        return viewRedirect("jsp/manterConvenio/consultarConveniosBloqueados", request, session, model, responsavel);
    }

}
