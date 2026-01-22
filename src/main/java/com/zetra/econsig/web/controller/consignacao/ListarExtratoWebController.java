package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
/**
 * <p>Title: ListarExtratoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Extrato de consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarExtrato" })
public class ListarExtratoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarExtratoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_EXTRATO_CSA_COR, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String mesAno = JspHelper.verificaVarQryStr(request, "mesAno");
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            String tocCodigo = JspHelper.verificaVarQryStr(request, "tocCodigo");

            Date dataIni = null;
            Date dataFim = null;

            if (!TextHelper.isNull(periodoIni)) {
                dataIni = DateHelper.parse(periodoIni, LocaleHelper.getDatePattern());
            }

            if (!TextHelper.isNull(periodoFim)) {
                dataFim = DateHelper.parse(periodoFim, LocaleHelper.getDatePattern());
            }

            List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
            tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);
            tocCodigos.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);

            List<TransferObject> lstTipoOcorrencia = sistemaController.lstTipoOcorrencia(tocCodigos, responsavel);

            if(!TextHelper.isNull(tocCodigo)) {
                tocCodigos.clear();
                tocCodigos.add(tocCodigo);
            }

            String csaCodigo = null;
            String corCodigo = null;

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                corCodigo = responsavel.getCodigoEntidade();
            }

            int total = consignatariaController.countContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigos, dataIni, dataFim, mesAno, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            BigDecimal valorTotal = BigDecimal.ZERO;

            List<String> tocCodigosSum = new ArrayList<>();

            if (!TextHelper.isNull(tocCodigo)) {
                tocCodigosSum.add(tocCodigo);

                valorTotal = consignatariaController.sumContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigosSum, dataIni, dataFim, mesAno, responsavel);

                if (valorTotal != null && (tocCodigosSum.contains(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO) || tocCodigosSum.contains(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA))) {
                    // caso a busca tenha sido por cancelamentos os valores retornados serão negativos
                    valorTotal = valorTotal.negate();
                }

            } else {
                tocCodigosSum.add(CodedValues.TOC_TARIF_RESERVA);
                tocCodigosSum.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);

                valorTotal = consignatariaController.sumContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigosSum, dataIni, dataFim, mesAno, responsavel);

                tocCodigosSum.clear();
                tocCodigosSum.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
                tocCodigosSum.add(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);

                BigDecimal valorSubtrair = consignatariaController.sumContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigosSum, dataIni, dataFim, mesAno, responsavel);

                if (valorTotal != null && valorSubtrair != null) {
                    valorTotal = valorTotal.subtract(valorSubtrair);
                }
            }

            List<TransferObject> transacoes = consignatariaController.lstContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigos, dataIni, dataFim, mesAno, offset, size, responsavel);

            List<String> requestParams = new ArrayList<>();
            requestParams.add("mesAno");
            requestParams.add("periodoIni");
            requestParams.add("periodoFim");
            requestParams.add("tocCodigo");

            String linkAction = "../v3/listarExtrato";
            configurarPaginador(linkAction, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

            List<String> dozeMesesPraTras = new ArrayList<>();
            Date hoje = new Date();
            dozeMesesPraTras.add(DateHelper.toPeriodString(hoje));

            for (int i = 1; i < 12; i++) {
                Date mesAnterior = DateHelper.dateAdd(hoje, "MES", -i);
                dozeMesesPraTras.add(DateHelper.toPeriodString(mesAnterior));
            }

            if(responsavel.isCsa()) {
                List<Correspondente> correspondentes = null;

                correspondentes = consignatariaController.findCorrespondenteByCsaCodigo(responsavel.getCodigoEntidade(), responsavel);
                // Essa coluna deve ser exibida apenas para usuários de consignatárias que possuem correspondentes cadastrados
                model.addAttribute("csaTemCorrespondentes", correspondentes != null && !correspondentes.isEmpty());
            }

            model.addAttribute("dozeMesesPraTras", dozeMesesPraTras);
            model.addAttribute("transacoes", transacoes);
            model.addAttribute("mesAnoSelecionado", mesAno);
            model.addAttribute("periodoIni", periodoIni);
            model.addAttribute("periodoFim", periodoFim);
            model.addAttribute("somaTransDia", valorTotal);
            model.addAttribute("lstTipoOcorrencia", lstTipoOcorrencia);
            model.addAttribute("tocCodigoSelecionado", tocCodigo);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarConsignacao/listarExtratoCsaCor", request, session, model, responsavel);
    }
}
