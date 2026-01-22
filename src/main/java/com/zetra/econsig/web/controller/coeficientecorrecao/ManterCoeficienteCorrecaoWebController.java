package com.zetra.econsig.web.controller.coeficientecorrecao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CoeficienteCorrecaoTransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.coeficiente.CoeficienteCorrecaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterCoeficienteCorrecaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de manutenção de coeficiente de correcao.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/manterCoeficienteCorrecao" }, method = { RequestMethod.POST })
public class ManterCoeficienteCorrecaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterCoeficienteCorrecaoWebController.class);

    @Autowired
    private CoeficienteCorrecaoController coeficienteCorrecaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarTiposCoeficienteCorrecao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            SynchronizerToken.saveToken(request);
        }

        List<TransferObject> tiposCoeficienteCorrecao = null;
        try {
            tiposCoeficienteCorrecao = coeficienteCorrecaoController.lstTipoCoeficienteCorrecao();
        } catch (Exception e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("tiposCoeficienteCorrecao", tiposCoeficienteCorrecao);

        return viewRedirect("jsp/manterCoeficienteCorrecao/listarCoeficienteCorrecao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarCoeficienteCorrecao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam(value = "ccrTccCodigo", required = true, defaultValue = "") String ccrTccCodigo) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return carregarDadosParaVisualizacao(request, session, model, responsavel, ccrTccCodigo);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarCoeficienteCorrecao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String ccrTccCodigo  = JspHelper.verificaVarQryStr(request, "ccrTccCodigo");
        String tccDescricao = JspHelper.verificaVarQryStr(request, "tccDescricao");
        String formaCalcSelec = JspHelper.verificaVarQryStr(request, "formaCalcSelec");

        String ccrMes = JspHelper.verificaVarQryStr(request, "ccrMes0");
        String ccrAno = JspHelper.verificaVarQryStr(request, "ccrAno0");

        try {
            CoeficienteCorrecaoTransferObject ccto = new CoeficienteCorrecaoTransferObject(ccrTccCodigo);

            tccDescricao = JspHelper.verificaVarQryStr(request, "tccDescricao");
            if (tccDescricao != null && !tccDescricao.equals("")) {
                ccto.setTccDescricao(tccDescricao);
            }

            if (formaCalcSelec != null && !formaCalcSelec.equals("")) {
                ccto.setTccFormaCalc(formaCalcSelec);
            }

            if ((ccrMes != null && !ccrMes.equals("")) && (ccrAno != null && !ccrAno.equals(""))) {

                ccrMes = JspHelper.verificaVarQryStr(request, "ccrMes0");
                if(ccrMes != null && !ccrMes.equals("")){
                    ccto.setCcrMes(Short.valueOf(ccrMes));
                }
                ccrAno = JspHelper.verificaVarQryStr(request, "ccrAno0");
                if(ccrAno != null && !ccrAno.equals("")){
                    ccto.setCcrAno(Short.valueOf(ccrAno));
                }

                String ccrVlr = JspHelper.verificaVarQryStr(request, "ccrVlr0");
                if (ccrVlr != null && !ccrVlr.equals("")) {
                    try {
                        ccrVlr = NumberHelper.reformat(ccrVlr, NumberHelper.getLang(), "en", 9, 20);
                    } catch (java.text.ParseException ex2) {
                        LOG.error(ex2.getMessage(), ex2);
                    }
                } else {
                    ccrVlr = "0";
                }
                if (ccrVlr != null && !ccrVlr.equals("")){
                    ccto.setCcrVlr(new BigDecimal(ccrVlr));
                    if (ccto.getCcrVlr().signum() == 0) {
                        // Recurso Técnico necessário para não dar erro no SQL Server
                        ccto.setCcrVlr(new BigDecimal("0"));
                    }
                }

                if (!formaCalcSelec.equals(CodedValues.FORMA_CALCULO_PADRAO)) {
                    String ccrVlrAcumulado = JspHelper.verificaVarQryStr(request, "ccrVlrAcumulado0");
                    if (ccrVlrAcumulado != null && !ccrVlrAcumulado.equals("")) {
                        try {
                            ccrVlrAcumulado = NumberHelper.reformat(ccrVlrAcumulado, NumberHelper.getLang(), "en", 9, 20);
                        } catch (java.text.ParseException ex2) {
                            LOG.error(ex2.getMessage(), ex2);
                        }
                    } else {
                        ccrVlrAcumulado = "0";
                    }
                    if (ccrVlrAcumulado != null && !ccrVlrAcumulado.equals("")){
                        ccto.setCcrVlrAcumulado(new BigDecimal(ccrVlrAcumulado));
                        if (ccto.getCcrVlrAcumulado().signum() == 0) {
                            // Recurso Técnico necessário para não dar erro no SQL Server
                            ccto.setCcrVlrAcumulado(new BigDecimal("0"));
                        }
                    }
                }

                ccrTccCodigo = coeficienteCorrecaoController.createCoeficienteCorrecao(ccto, responsavel);
                ccto.setTccCodigo(ccrTccCodigo);

                // faz update do valor acumulado se a forma de calculo for a padrao
                if (formaCalcSelec.equals(CodedValues.FORMA_CALCULO_PADRAO)) {
                    coeficienteCorrecaoController.updateCoeficienteCorrecaoValorAcumulado(ccto, false, responsavel);
                }
            }

            if (ccto.getTccCodigo() != null) {
                // faz update do tipo de coeficiente pra pegar descricao ou forma de calculo
                ccto.setTccCodigo(ccrTccCodigo);
                try {
                    coeficienteCorrecaoController.updateTipoCoeficienteCorrecao(ccto, responsavel);
                } catch (Exception ex){
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        } catch (Exception e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarDadosParaVisualizacao(request, session, model, responsavel, ccrTccCodigo);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirCoeficienteCorrecao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        boolean exibirDetalheCcr = false;

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            CoeficienteCorrecaoTransferObject ccrRem = new CoeficienteCorrecaoTransferObject(request.getParameter("codigo"));
            String formaCalc = request.getParameter("formaCalc");
            if (request.getParameter("ccrMes") != null && !request.getParameter("ccrMes").equals("") &&
                request.getParameter("ccrAno") != null && !request.getParameter("ccrAno").equals("")) {
                ccrRem.setCcrMes(Short.valueOf(request.getParameter("ccrMes")));
                ccrRem.setCcrAno(Short.valueOf(request.getParameter("ccrAno")));
                exibirDetalheCcr = true;
            }
            coeficienteCorrecaoController.removeCoeficienteCorrecao(ccrRem, responsavel);

            if (formaCalc != null && formaCalc.equals(CodedValues.FORMA_CALCULO_PADRAO)) {
                coeficienteCorrecaoController.updateCoeficienteCorrecaoValorAcumulado(ccrRem, true, responsavel);
            }
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.correcao.excluido.sucesso", responsavel));
        } catch (Exception e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (exibirDetalheCcr) {
            return carregarDadosParaVisualizacao(request, session, model, responsavel, request.getParameter("codigo"));
        } else {
            return listarTiposCoeficienteCorrecao(request, response, session, model);
        }
    }

    private String carregarDadosParaVisualizacao(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, String ccrTccCodigo) throws InstantiationException, IllegalAccessException {
        List<CoeficienteCorrecaoTransferObject> listaCoeficientesCorrecao = null;
        int tamanhoLista = 0;
        String tccDescricao = "";
        String tccFormaCalc = "";

        if (!TextHelper.isNull(ccrTccCodigo)) {
            try {
                listaCoeficientesCorrecao = coeficienteCorrecaoController.lstCoeficienteCorrecao(ccrTccCodigo);
                if (listaCoeficientesCorrecao != null) {
                    tamanhoLista = listaCoeficientesCorrecao.size();
                    tccDescricao = listaCoeficientesCorrecao.get(0).getTccDescricao().toString();
                    tccFormaCalc = listaCoeficientesCorrecao.get(0).getTccFormaCalc().toString();
                }
            } catch (Exception e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(e.getMessage(), e);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        model.addAttribute("listaCoeficientesCorrecao", listaCoeficientesCorrecao);
        model.addAttribute("ccrTccCodigo", ccrTccCodigo);
        model.addAttribute("tccDescricao", tccDescricao);
        model.addAttribute("tccFormaCalc", tccFormaCalc);
        model.addAttribute("tamanhoLista", tamanhoLista);

        return viewRedirect("jsp/manterCoeficienteCorrecao/editarCoeficienteCorrecao", request, session, model, responsavel);
    }
}
