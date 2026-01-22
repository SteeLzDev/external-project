package com.zetra.econsig.web.controller.folha;

import java.text.ParseException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.folha.DadosProcessamentoSemBloqueio;
import com.zetra.econsig.helper.folha.RecuperaDadosProcessamentoSemBloqueioViewHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaBlocosProcessamentoFolha;
import com.zetra.econsig.job.process.ProcessaInterromperProcessamentoFolha;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/dashboardProcessamento" })
public class VisualizarDashboardProcessamentoWebController extends AbstractWebController {

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        DadosProcessamentoSemBloqueio dadosProcessamento = null;
        String orgaoIdentificadorProcessamento = null;
        String orgaoIdentificadorVariacaoMargem = null;
        try {
            // recupera o órgão do filtro de processamento, caso tenha sido selecionado
            if (JspHelper.verificaVarQryStr(request, "orgaoIdentificadorProcessamento") != null && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "orgaoIdentificadorProcessamento"))) {
                orgaoIdentificadorProcessamento = JspHelper.verificaVarQryStr(request, "orgaoIdentificadorProcessamento");
            }

            // recupera o órgão do filtro de variação de margem, caso tenha sido selecionado
            if (JspHelper.verificaVarQryStr(request, "orgaoIdentificadorVariacaoMargem") != null && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "orgaoIdentificadorVariacaoMargem"))) {
                orgaoIdentificadorVariacaoMargem = JspHelper.verificaVarQryStr(request, "orgaoIdentificadorVariacaoMargem");
            }

            dadosProcessamento = RecuperaDadosProcessamentoSemBloqueioViewHelper.getDadosProcessamentoBloco(orgaoIdentificadorProcessamento, orgaoIdentificadorVariacaoMargem, responsavel);


            model.addAttribute("temBlocoProcessamento", dadosProcessamento.isTemBlocoProcessamento());
            model.addAttribute("temProcessoRodando", dadosProcessamento.isTemProcessoRodando());

            // carrega lista de órgãos para filtro e adiciona no model
            carregarListaOrgao(request, session, model, responsavel);

            String bprPeriodo = dadosProcessamento.getBprPeriodo();
            if (!TextHelper.isNull(bprPeriodo)) {
                bprPeriodo = DateHelper.reformat(bprPeriodo, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            }
            model.addAttribute("periodo", bprPeriodo);

            model.addAttribute("estimativaTermino", String.format("%02d", dadosProcessamento.getEstimativaTerminoHoras()) + ":" + String.format("%02d", dadosProcessamento.getEstimativaTerminoMinutos()));

            model.addAttribute("percentualBlocosProcessados", dadosProcessamento.getPercentualBlocosProcessados());
            model.addAttribute("percentualBlocosProcessadosMargem", dadosProcessamento.getPercentualBlocosProcessadosMargem());
            model.addAttribute("percentualBlocosProcessadosRetorno", dadosProcessamento.getPercentualBlocosProcessadosRetorno());
            model.addAttribute("percentualBlocosProcessadosComErro", dadosProcessamento.getPercentualBlocosProcessadosComErro());
            model.addAttribute("percentualBlocosProcessadosRejeitados", dadosProcessamento.getPercentualBlocosProcessadosRejeitados());

            model.addAttribute("orgaoIdentificadorProcessamento", orgaoIdentificadorProcessamento);
            model.addAttribute("orgaoIdentificadorVariacaoMargem", orgaoIdentificadorVariacaoMargem);

            model.addAttribute("dadosMediaMargem", dadosProcessamento.getDadosMediaMargem());

            model.addAttribute("podeInterromperExecucao", responsavel.temPermissao(CodedValues.FUN_INTERROMPER_PROCESSAMENTO_FOLHA));

        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ViewHelperException ex) {
            // informa ao usuário que nenhum bloco de processamento foi encontrado
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

            if (!TextHelper.isNull(orgaoIdentificadorProcessamento) || !TextHelper.isNull(orgaoIdentificadorVariacaoMargem)) {
                model.addAttribute("temProcessoRodando", false);
                String linkRet = "../v3/dashboardProcessamento?acao=iniciar";
                model.addAttribute("linkRet", linkRet);
            }
        }

        return viewRedirect("jsp/visualizarDashboardProcessamento/visualizarDashboardProcessamento", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=interromperExecucao" })
    public String interromperExecucao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // verifica se tem processo ativo
            if (!ControladorProcessos.getInstance().processoAtivo(ProcessaBlocosProcessamentoFolha.CHAVE)) {
                 session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.exibir.dashboard.processamento.nao.encontrado", responsavel));
                 return iniciar(request, response, session, model);
            }

            // Período de processamento em execução
            String strPeriodo = JspHelper.verificaVarQryStr(request, "periodo");
            Date bprPeriodo = DateHelper.parse(strPeriodo, LocaleHelper.getDatePattern());

            // Motivo de interrupção
            String observacao = JspHelper.verificaVarQryStr(request, "observacao");
            if (TextHelper.isNull(observacao)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.observacao.processamento.folha.interromper", responsavel));
                return iniciar(request, response, session, model);
            }

            // Inicia o processo de interrupção do processamento
            ProcessaInterromperProcessamentoFolha processo = new ProcessaInterromperProcessamentoFolha(bprPeriodo, observacao, responsavel);
            processo.start();
            ControladorProcessos.getInstance().incluir(ProcessaInterromperProcessamentoFolha.CHAVE, processo);

        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }
}
