package com.zetra.econsig.web.controller.rest;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.ocorrencia.ListaHistoricoOcorrenciaAdeViewHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAde;
import com.zetra.econsig.service.ocorrencia.HistoricoOcorrenciaAdeController;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BuscarHistoricoOcorrenciaAdeRestController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BuscarHistoricoOcorrenciaAdeRestController.class);

    @Autowired
    private HistoricoOcorrenciaAdeController historicoOcorrenciaAdeController;

    @PostMapping(value= "/v3/buscaHistoricoOcorrenciaAde")
    public String buscarHistoricoOcorrenciaAde(HttpServletRequest request) throws JspException, ZetraException{
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String ocaCodigo = request.getParameter("ocaCodigo");
        List<HistoricoOcorrenciaAde> historico;
        try {
            historico = historicoOcorrenciaAdeController.buscarHistoricoOcorrenciaAdeByOcaCodigo(ocaCodigo, responsavel);
        } catch (ZetraException e) {
            LOG.error(e.getMessage());
            throw new ZetraException(e);
        }

        return ListaHistoricoOcorrenciaAdeViewHelper.constroiView(historico, responsavel);
    }
}
