package com.zetra.econsig.web.controller.decisaojudicial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.consignacao.AlterarConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ExecutarAlteracaoJudicialWebController</p>
 * <p>Description: Web Controller para alteração de consignação em Decisão Judicial (alteração para maior)</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarAlteracaoJudicial" })
public class ExecutarAlteracaoJudicialWebController  extends AlterarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarAlteracaoJudicialWebController.class);

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.opcao.alterar.consignacao", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarAlteracaoJudicial");
        model.addAttribute("acaoListarCidades", "executarDecisaoJudicial");
        model.addAttribute("tipoArquivoAnexo", "decisao_judicial");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_ANEXO, responsavel));

            model.addAttribute("tipoJusticaObrigatorio", isTipoJusticaObrigatorio(responsavel));
            model.addAttribute("comarcaJusticaObrigatorio", isComarcaJusticaObrigatorio(responsavel));
            model.addAttribute("numeroProcessoObrigatorio", isNumeroProcessoObrigatorio(responsavel));
            model.addAttribute("dataDecisaoObrigatorio", isDataDecisaoObrigatorio(responsavel));
            model.addAttribute("textoDecisaoObrigatorio", isTextoDecisaoObrigatorio(responsavel));
            model.addAttribute("anexoObrigatorio", isAnexoDecisaoObrigatorio(responsavel));
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.permissao.nao.encontrado", responsavel, ex.getMessage());
        }
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_ALTERAR_CONSIGNACAO_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        return sadCodigos;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "alterar");

        // Somente consignações que integram folha
        criterio.setAttribute(Columns.ADE_INT_FOLHA, CodedValues.INTEGRA_FOLHA_SIM);

        // Ordenadas pela ordem de desconto em folha de forma reversa
        criterio.setAttribute("TIPO_ORDENACAO", "3");
        return criterio;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        final String link = "../v3/executarAlteracaoJudicial?acao=editar";
        final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.alterar.abreviado", responsavel);
        final String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.alterar", responsavel);
        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alterar.consignacao.clique.aqui", responsavel);
        final String msgConfirmacao = "";
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("EXECUTAR_DECISAO_JUDICIAL", CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL, descricao, descricaoCompleta, "editar.gif", "btnAlterarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

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
        // Permite aumentar valor ou prazo
        model.addAttribute("permiteAumentarVlr", Boolean.TRUE);
        model.addAttribute("permiteAumentarPrz", Boolean.TRUE);

        // Não permite reduzir valor
        model.addAttribute("permiteReduzirVlr", Boolean.FALSE);

        // Omite campos de informações financeiras
        model.addAttribute("permiteCadVlrTac", Boolean.FALSE);
        model.addAttribute("permiteCadVlrIof", Boolean.FALSE);
        model.addAttribute("permiteCadVlrLiqLib", Boolean.FALSE);
        model.addAttribute("permiteCadVlrMensVinc", Boolean.FALSE);
        model.addAttribute("permiteVlrLiqTxJuros", Boolean.FALSE);
        model.addAttribute("boolTpsSegPrestamista", Boolean.FALSE);
        model.addAttribute("podeAlterarCarencia", Boolean.FALSE);
        model.addAttribute("prazosPossiveis", new HashSet<>());

        // Oculta opções avançadas, e deixa fixo conforme a operação de adequação à margem
        model.addAttribute("omitirOpcoesAvancadas", Boolean.TRUE);

        model.addAttribute("opcaoValidaMargemFixo", Boolean.FALSE);
        model.addAttribute("opcaoValidaTaxaFixo", Boolean.FALSE);
        model.addAttribute("opcaoValidaLimiteAdeFixo", Boolean.FALSE);
        model.addAttribute("opcaoValorPrazoSemLimiteFixo", Boolean.TRUE);
        model.addAttribute("opcaoPermiteAltEntidadesBloqueadasFixo", Boolean.TRUE);
        model.addAttribute("opcaoExigeSenhaAltAvancadaFixo", Boolean.FALSE);
        model.addAttribute("opcaoAfetaMargemFixo", Boolean.TRUE);
        model.addAttribute("opcaoInsereOcorrenciaFixo", Boolean.TRUE);
        model.addAttribute("opcaoNovoContratoDifFixo", Boolean.FALSE);

        try {
            // Busca se a consignação possui relacionamento de alteração avançada com contrato suspenso
            final String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
            final List<TransferObject> adesRelacionamento = alterarConsignacaoController.possuiRelacionamentoAlteracaoJudicial(adeCodigo, responsavel);
            if ((adesRelacionamento != null) && !adesRelacionamento.isEmpty()) {
                model.addAttribute("opcaoLiquidaRelacionamentoJudicialFixo", Boolean.TRUE);
                model.addAttribute("adeSuspensa", adesRelacionamento.get(0).getAttribute(Columns.ADE_NUMERO));
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            request.getSession().setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
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
