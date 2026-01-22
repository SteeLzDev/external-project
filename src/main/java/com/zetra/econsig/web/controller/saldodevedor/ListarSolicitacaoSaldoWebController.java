package com.zetra.econsig.web.controller.saldodevedor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractListarTodasConsignacoesWebController;

/**
 * <p>Title: ListarSolicitacaoSaldoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ListarSolicitacaoSaldo.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarSolicitacaoSaldo" })
public class ListarSolicitacaoSaldoWebController extends AbstractListarTodasConsignacoesWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarSolicitacaoSaldoWebController.class);

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        carregarListaServico(request, session, model, responsavel);

        // Habilita exibição de campo para filtro pela data de solicitação do saldo
        model.addAttribute("exibirFiltroDataSolicitacao", Boolean.TRUE);
        // Habilita exibição de campo para filtro pelo tipo do saldo
        model.addAttribute("exibirFiltroTipoSaldo", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        String tipoSolicitacaoSaldo = JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo");
        String chaveTituloPagina = "";
        if (tipoSolicitacaoSaldo.equals("liq")) {
            chaveTituloPagina = "rotulo.listar.solicitacao.saldo.devedor.liquidacao.titulo";
        } else if (tipoSolicitacaoSaldo.equals("exc")) {
            chaveTituloPagina = "rotulo.listar.solicitacao.saldo.devedor.exclusao.titulo";
        } else {
            chaveTituloPagina = "rotulo.listar.solicitacao.saldo.devedor.titulo";
        }

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage(chaveTituloPagina, responsavel));
        model.addAttribute("acaoFormulario", "../v3/listarSolicitacaoSaldo");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        String situacaoSolicitacaoSaldo = JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo");
        if (situacaoSolicitacaoSaldo.equals("2") && responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO)) {
            // Adiciona opção para liquidar consignação
            String link = "../v3/liquidarConsignacao?acao=efetivarAcao&opt=l";
            String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar.abreviado", responsavel);
            String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.clique.aqui", responsavel);
            String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel);
            String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao.nao.reverter.renegociacao", responsavel);

            acoes.add(new AcaoConsignacao("LIQ_CONTRATO", CodedValues.FUN_LIQ_CONTRATO, descricao, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null));

        } else if (responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER)) {
            // Adiciona opção para editar saldo devedor
            String link = "../v3/editarSaldoDevedorSolicitacao?acao=iniciar&tipo=solicitacao_saldo";
            String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.informar.saldo.abreviado", responsavel);
            String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.informar.saldo", responsavel);
            String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.saldo.devedor.clique.aqui", responsavel);
            String msgConfirmacao = "";
            String msgAdicionalConfirmacao = "";

            acoes.add(new AcaoConsignacao("EDT_SALDO_DEVEDOR", CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER, descricao, descricaoCompleta, "saldo_devedor.gif", "btnEditarSaldo", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));
        }

        // Adiciona o editar consignação
        String link = "../v3/listarSolicitacaoSaldo?acao=detalharConsignacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitacao_saldo");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        // Filtro de solicitação de saldo
        String tipoSolicitacaoSaldo = JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo");
        String situacaoSolicitacaoSaldo = JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo");
        String diasSolicitacaoSaldo = JspHelper.verificaVarQryStr(request, "diasSolicitacaoSaldo");
        String diasSolicitacaoSaldoPagaAnexo = JspHelper.verificaVarQryStr(request, "diasSolicitacaoSaldoPagaAnexo");

        // Busca saldo devedor ainda não informado.
        criterio.setAttribute("infSaldoDevedor", tipoSolicitacaoSaldo);
        if (situacaoSolicitacaoSaldo.equals("0")) {
            criterio.setAttribute("diasSolicitacaoSaldo", "-1");
            criterio.setAttribute("diasSolicitacaoSaldoPagaAnexo", "-1");
        } else if (situacaoSolicitacaoSaldo.equals("1")) {
            criterio.setAttribute("diasSolicitacaoSaldo", (!TextHelper.isNum(diasSolicitacaoSaldo) ? "0" : diasSolicitacaoSaldo));
        } else if (situacaoSolicitacaoSaldo.equals("2")) {
            criterio.setAttribute("diasSolicitacaoSaldoPagaAnexo", (!TextHelper.isNum(diasSolicitacaoSaldoPagaAnexo) ? "0" : diasSolicitacaoSaldoPagaAnexo));
        }

        try {
            String ocaDataIni = JspHelper.verificaVarQryStr(request, "ocaDataIni");
            if (!ocaDataIni.equals("") ) {
                ocaDataIni = DateHelper.reformat(ocaDataIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("ocaDataIni", ocaDataIni);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String ocaDataFim = JspHelper.verificaVarQryStr(request, "ocaDataFim");
            if (!ocaDataFim.equals("")) {
                ocaDataFim = DateHelper.reformat(ocaDataFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("ocaDataFim", ocaDataFim);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }
}
