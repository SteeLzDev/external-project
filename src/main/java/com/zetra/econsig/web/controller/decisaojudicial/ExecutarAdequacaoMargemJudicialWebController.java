package com.zetra.econsig.web.controller.decisaojudicial;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.consignacao.AlterarConsignacaoWebController;

/**
 * <p>Title: ExecutarAdequacaoMargemJudicialWebController</p>
 * <p>Description: Web Controller para adequação da margem em Decisão Judicial (alteração para menor)</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarAdequacaoMargem" })
public class ExecutarAdequacaoMargemJudicialWebController extends AlterarConsignacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarAdequacaoMargemJudicialWebController.class);

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.opcao.adequacao.margem", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarAdequacaoMargem");
        model.addAttribute("acaoListarCidades", "executarDecisaoJudicial");
        model.addAttribute("tipoArquivoAnexo", "decisao_judicial");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_ANEXO, responsavel));

            model.addAttribute("tipoJusticaObrigatorio", isTipoJusticaObrigatorio(responsavel));
            model.addAttribute("comarcaJusticaObrigatorio", isComarcaJusticaObrigatorio(responsavel));
            model.addAttribute("numeroProcessoObrigatorio", isNumeroProcessoObrigatorio(responsavel));
            model.addAttribute("dataDecisaoObrigatorio", isDataDecisaoObrigatorio(responsavel));
            model.addAttribute("textoDecisaoObrigatorio", isTextoDecisaoObrigatorio(responsavel));
            model.addAttribute("anexoObrigatorio", isAnexoDecisaoObrigatorio(responsavel));
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.permissao.nao.encontrado", responsavel, ex.getMessage());
        }
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ADEQUAR_MARGEM_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        return sadCodigos;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "alterar");

        // Somente consignações que integram folha
        criterio.setAttribute(Columns.ADE_INT_FOLHA, CodedValues.INTEGRA_FOLHA_SIM);

        // Ordenadas pela ordem de desconto em folha de forma reversa
        criterio.setAttribute("TIPO_ORDENACAO", "3");
        return criterio;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/executarAdequacaoMargem?acao=editar";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.adequar.margem.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.adequar.margem", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.adequar.consignacao.margem.clique.aqui", responsavel);
        String msgConfirmacao = "";
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("EXECUTAR_DECISAO_JUDICIAL", CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL, descricao, descricaoCompleta, "editar.gif", "btnAdequarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        return acoes;
    }

    @Override
    protected boolean usuarioTemPermissaoAlteracaoAvancada(AcessoSistema responsavel) {
        // Usuário com permissão para decisão judicial tem permissão de alteração avançada
        return true;
    }

    /**
     * Permite customizações na alteração de consignação em classes que estendem este Web Controller
     * @param autdes
     * @param responsavel
     */
    @Override
    protected void configurarAlteracaoConsignacao(CustomTransferObject autdes, HttpServletRequest request, Model model, AcessoSistema responsavel) {
        // Não permite aumentar valor ou prazo
        model.addAttribute("permiteAumentarVlr", Boolean.FALSE);
        model.addAttribute("permiteAumentarPrz", Boolean.FALSE);

        // Omite campos de informações financeiras
        model.addAttribute("permiteCadVlrTac", Boolean.FALSE);
        model.addAttribute("permiteCadVlrIof", Boolean.FALSE);
        model.addAttribute("permiteCadVlrLiqLib", Boolean.FALSE);
        model.addAttribute("permiteCadVlrMensVinc", Boolean.FALSE);
        model.addAttribute("permiteVlrLiqTxJuros", Boolean.FALSE);
        model.addAttribute("boolTpsSegPrestamista", Boolean.FALSE);
        model.addAttribute("podeAlterarCarencia", Boolean.FALSE);
        model.addAttribute("prazosPossiveis", new HashSet<Integer>());

        // Oculta opções avançadas, e deixa fixo conforme a operação de adequação à margem
        model.addAttribute("omitirOpcoesAvancadas", Boolean.TRUE);

        model.addAttribute("opcaoValidaMargemFixo", Boolean.FALSE);
        model.addAttribute("opcaoValidaTaxaFixo", Boolean.FALSE);
        model.addAttribute("opcaoValidaLimiteAdeFixo", Boolean.FALSE);
        model.addAttribute("opcaoValorPrazoSemLimiteFixo", Boolean.FALSE); // Não pode aumentar
        model.addAttribute("opcaoPermiteAltEntidadesBloqueadasFixo", Boolean.TRUE);
        model.addAttribute("opcaoExigeSenhaAltAvancadaFixo", Boolean.FALSE);
        model.addAttribute("opcaoAfetaMargemFixo", Boolean.TRUE);
        model.addAttribute("opcaoInsereOcorrenciaFixo", Boolean.TRUE);

        String nseCodigo = (String) autdes.getAttribute(Columns.NSE_CODIGO);
        if (CodedValues.NSE_EMPRESTIMO.equals(nseCodigo) || CodedValues.NSE_FINANCIAMENTO.equals(nseCodigo) || CodedValues.NSE_AUXILIO_FINANCEIRO.equals(nseCodigo)
                || CodedValues.NSE_SEGURO.equals(nseCodigo) || CodedValues.NSE_PREVIDENCIA.equals(nseCodigo)) {
            model.addAttribute("opcaoNovoContratoDifFixo", Boolean.TRUE);
        }
    }

    @Override
    protected boolean usuarioPodeAnexarAlteracao(AcessoSistema responsavel) {
        // Em alteração judicial sempre terá opção de incluir anexos
        return true;
    }

    @Override
    protected String tratarConsignacaoNaoEncontrada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return "forward:/v3/executarDecisaoJudicial?acao=iniciar";
    }
}
