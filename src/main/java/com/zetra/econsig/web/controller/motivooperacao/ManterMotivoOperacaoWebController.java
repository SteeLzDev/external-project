package com.zetra.econsig.web.controller.motivooperacao;

import java.util.ArrayList;
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
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.log.LogController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>
 * Title: ManterMotivoOperacaoWebController
 * </p>
 * <p>
 * Description: Controlador Web responsável por manter motivo operacao.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2017
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author$ $Revision$ $Date: 2019-02-20 15:15:09 -0300
 * (Qua, 20 fev 2019) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/motivoOperacao" })
public class ManterMotivoOperacaoWebController extends ControlePaginacaoWebController {

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private LogController logController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            // Página inicial
            SynchronizerToken.saveToken(request);

            List<TransferObject> motivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(null, null, responsavel);

            model.addAttribute("motivoOperacao", motivoOperacao);

            return viewRedirect("jsp/manterMotivoOperacao/listarMotivoOperacao", request, session, model, responsavel);

        } catch (TipoMotivoOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            List<String> tenCodigos = new ArrayList<>();
            tenCodigos.add(Log.GERAL);
            tenCodigos.add(Log.USUARIO);
            tenCodigos.add(Log.AUTORIZACAO);
            tenCodigos.add(Log.REGISTRO_SERVIDOR);
            tenCodigos.add(Log.SERVICO);
            tenCodigos.add(Log.CONVENIO);
            tenCodigos.add(Log.DISPENSA_VALIDACAO_DIGITAL);
            tenCodigos.add(Log.CONSIGNATARIA);
            tenCodigos.add(Log.CORRESPONDENTE);
            List<TransferObject> tiposEntidade = logController.lstTipoEntidade(tenCodigos);

            String tmoCodigo = request.getParameter("tmoCodigo");

            TipoMotivoOperacaoTransferObject motivoOperacao = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                try {
                    motivoOperacao = tipoMotivoOperacaoController.findMotivoOperacao(tmoCodigo, responsavel);
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return iniciar(request, response, session, model);
                }
            }

            model.addAttribute("motivoOperacao", motivoOperacao);
            model.addAttribute("tiposEntidade", tiposEntidade);

            return viewRedirect("jsp/manterMotivoOperacao/editarMotivoOperacao", request, session, model, responsavel);

        } catch (LogControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String tmoCodigo = request.getParameter("tmoCodigo");

            List<String> tenCodigos = new ArrayList<>();
            tenCodigos.add(Log.GERAL);
            tenCodigos.add(Log.USUARIO);
            tenCodigos.add(Log.AUTORIZACAO);
            tenCodigos.add(Log.REGISTRO_SERVIDOR);
            tenCodigos.add(Log.SERVICO);
            tenCodigos.add(Log.CONVENIO);
            tenCodigos.add(Log.DISPENSA_VALIDACAO_DIGITAL);
            tenCodigos.add(Log.CONSIGNATARIA);
            tenCodigos.add(Log.CORRESPONDENTE);
            List<TransferObject> tiposEntidade = logController.lstTipoEntidade(tenCodigos);

            TipoMotivoOperacaoTransferObject motivoOperacao = null;

            if (!TextHelper.isNull(tmoCodigo)) {

                // Atualiza o motivo
                motivoOperacao = new TipoMotivoOperacaoTransferObject(tmoCodigo);
                motivoOperacao.setTmoDescricao(JspHelper.verificaVarQryStr(request, "tmoDescricao"));
                motivoOperacao.setTmoIdentificador(JspHelper.verificaVarQryStr(request, "tmoIdentificador"));
                motivoOperacao.setTenCodigo(JspHelper.verificaVarQryStr(request, "tenCodigo"));
                motivoOperacao.setTmoExigeObs(JspHelper.verificaVarQryStr(request, "tmoExigeObs"));
                motivoOperacao.setTmoDecisalJudicial(JspHelper.verificaVarQryStr(request, "tmoDecisaoJudicial"));

                tipoMotivoOperacaoController.updateMotivoOperacao(motivoOperacao, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.alterar.tipo.motivo.sucesso", responsavel));

            } else {

                // Insere o motivo
                motivoOperacao = new TipoMotivoOperacaoTransferObject();
                motivoOperacao.setTmoIdentificador(JspHelper.verificaVarQryStr(request, "tmoIdentificador"));
                motivoOperacao.setTmoDescricao(JspHelper.verificaVarQryStr(request, "tmoDescricao"));
                motivoOperacao.setTenCodigo(JspHelper.verificaVarQryStr(request, "tenCodigo"));
                motivoOperacao.setTmoExigeObs(JspHelper.verificaVarQryStr(request, "tmoExigeObs"));
                motivoOperacao.setTmoDecisalJudicial(JspHelper.verificaVarQryStr(request, "tmoDecisaoJudicial"));

                tmoCodigo = tipoMotivoOperacaoController.createMotivoOperacao(motivoOperacao, responsavel);
                motivoOperacao.setTmoCodigo(tmoCodigo);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.criar.tipo.motivo.sucesso", responsavel));

            }

            model.addAttribute("motivoOperacao", motivoOperacao);
            model.addAttribute("tiposEntidade", tiposEntidade);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterMotivoOperacao/editarMotivoOperacao", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String tmoCodigo = request.getParameter("codigo");

            // Exclui o serviço
            TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(tmoCodigo);
            tipoMotivoOperacaoController.removeMotivoOperacao(motivoOperacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.excluir.tipo.motivo.sucesso", responsavel));

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/motivoOperacao?acao=iniciar", request)));
        return "jsp/redirecionador/redirecionar";

    }

    @RequestMapping(params = { "acao=alterarStatus" })
    public String alterarStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String tmoCodigo = request.getParameter("codigo");

            // Altera o status
            Short status = Short.valueOf(JspHelper.verificaVarQryStr(request, "status"));
            status = status.equals(CodedValues.STS_ATIVO) ? CodedValues.STS_INDISP : CodedValues.STS_ATIVO;

            TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(tmoCodigo);
            motivoOperacao.setTmoAtivo(status);
            tipoMotivoOperacaoController.updateMotivoOperacao(motivoOperacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, (status.equals(CodedValues.STS_ATIVO) ? ApplicationResourcesHelper.getMessage("rotulo.desbloquear.tipo.motivo.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.bloquear.tipo.motivo.sucesso", responsavel)));

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/motivoOperacao?acao=iniciar", request)));
        return "jsp/redirecionador/redirecionar";

    }

}
