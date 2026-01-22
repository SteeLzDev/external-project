package com.zetra.econsig.web.controller.rescisao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarColaboradoresVerbaRescisoriaWebController</p>
 * <p>Description: Listar colaboradores que estão no processo de rescisão contratual e são passíveis de informação de verba rescisória</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarColaboradoresVerbaRescisoria" })
public class ListarColaboradoresVerbaRescisoriaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarColaboradoresVerbaRescisoriaWebController.class);

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    VerbaRescisoriaController verbaRescisoriaController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.colaborador.verba.rescisoria.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String listarColaboradoresRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            int total = verbaRescisoriaController.countColaboradoresReterVerbaRescisoria(responsavel);
            int size = JspHelper.LIMITE;
            int offset = request.getParameter("offset") != null ? Integer.parseInt(request.getParameter("offset")) : 0;

            HashMap<String, Boolean> hashExisteOcoSaldoInsuficiente = new HashMap<>();

            boolean exibeProcessados = JspHelper.verificaVarQryStr(request, "oculto").equals("true");
            List<TransferObject> listaRseRescisao = verbaRescisoriaController.listarColaboradoresReterVerbaRescisoria(offset, size, responsavel);

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.listar.colaborador.rescisao.paginacao.titulo", total, size, requestParams, false, request, model);

            if(ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_SER_AUT_PENDENTE_SALDO_INSUF_VERBA_RESCISORIA, responsavel)) {
                List<AutDesconto> contratosSaldoInsuficiente = verbaRescisoriaController.listarConsignacoesReterVerbaRescisoriaSaldoInsuficiente(null, responsavel);
                for (AutDesconto ade: contratosSaldoInsuficiente) {
                    String rseCodigo = ade.getRegistroServidor().getRseCodigo();
                    if(hashExisteOcoSaldoInsuficiente.isEmpty() || hashExisteOcoSaldoInsuficiente.get(rseCodigo) == null) {
                        hashExisteOcoSaldoInsuficiente.put(rseCodigo, Boolean.TRUE);
                    }
                }
            }

            /* se as consignatárias informaram todos os saldos devedores das suas consignações ativas (CodedValues.SAD_CODIGOS_ATIVOS)
             * e da natureza empréstimo (nse_codigo = '1') daquele colaborador
             * ou o prazo para esta informação esteja vencido
             * exibir opção para prosseguir para a tela de informação do valor da verba rescisória. */
            for (TransferObject registroServidor : listaRseRescisao) {
                boolean todosSaldosInformados = true;
                boolean solicitacoesVencidas = true;

                String svrCodigo = (String) registroServidor.getAttribute(Columns.SVR_CODIGO);
                String rseCodigo = (String) registroServidor.getAttribute(Columns.RSE_CODIGO);

                //caso esteja concluído não pode reter verba rescisória
                if (!TextHelper.isNull(svrCodigo) && svrCodigo.equals(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo())) {
                    todosSaldosInformados = false;
                    solicitacoesVencidas = false;
                } else {


                    List<TransferObject> ades = pesquisarConsignacaoController.pesquisaAutorizacaoPorRseSadNse(rseCodigo, CodedValues.SAD_CODIGOS_ATIVOS, Arrays.asList(CodedValues.NSE_EMPRESTIMO), responsavel);

                    for (TransferObject ade : ades) {

                        String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
                        SaldoDevedorTransferObject saldoDevedor = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);

                        // caso não tenha saldo devedor ou esteja vencido deve ser informado o saldo devedor
                        if (saldoDevedor == null || (saldoDevedor.getSdvDataValidade() != null && saldoDevedor.getSdvDataValidade().before(new Date()))) {
                            todosSaldosInformados = false;
                        }

                        List<SolicitacaoAutorizacao> solicitacoes = saldoDevedorController.lstSolicitacaoSaldoExclusao(adeCodigo, responsavel);

                        for (SolicitacaoAutorizacao solicitacao : solicitacoes) {
                            // caso a solicitação esteja pendente e ainda não venceu
                            if (solicitacao.getStatusSolicitacao().getSsoCodigo().equals(StatusSolicitacaoEnum.PENDENTE.getCodigo()) &&
                                    solicitacao.getSoaDataValidade() != null && solicitacao.getSoaDataValidade().after(new Date())) {
                                solicitacoesVencidas = false;
                            }
                        }
                    }
                }

                if(ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_SER_AUT_PENDENTE_SALDO_INSUF_VERBA_RESCISORIA, responsavel) && !hashExisteOcoSaldoInsuficiente.isEmpty() && hashExisteOcoSaldoInsuficiente.get(rseCodigo) !=null ) {
                    registroServidor.setAttribute("PODE_ENVIAR_EMAIL_SER", Boolean.TRUE);
                } else {
                    registroServidor.setAttribute("PODE_ENVIAR_EMAIL_SER", Boolean.FALSE);
                }

                // se todos os saldos foram informados ou as todas solicitações estiverem vencidas
                // deixa prosseguir para tela para reter verba
                registroServidor.setAttribute("PODE_RETER_VERBA", todosSaldosInformados || solicitacoesVencidas);
            }

            // Seta atributos no model
            model.addAttribute("listaRseRescisao", listaRseRescisao);
            model.addAttribute("exibeProcessados", exibeProcessados);

            return viewRedirect("jsp/rescisao/listarColaboradoresVerbaRescisoria", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=enviaEmailSer" })
    public String enviaEmailServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");

            if(TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios.campos", responsavel, ApplicationResourcesHelper.getMessage("rotulo.registro.servidor.singular", responsavel)));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            EnviaEmailHelper.enviarEmailSerVerbaRescisoriaSaldoInsuficiente(rseCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.email.servidor.verba.rescisoria.saldo.insuficiente.sucesso", responsavel));

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listarColaboradoresRescisao(request, response, session, model);
    }

    private String getLinkAction() {
        return "../v3/listarColaboradoresVerbaRescisoria?acao=iniciar";
    }
}
