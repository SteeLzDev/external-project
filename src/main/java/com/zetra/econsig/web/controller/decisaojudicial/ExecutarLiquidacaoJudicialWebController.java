package com.zetra.econsig.web.controller.decisaojudicial;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.consignacao.LiquidarConsignacaoWebController;

/**
 * <p>Title: ExecutarDecisaoJudicialWebController</p>
 * <p>Description: Web Controller para liquidação de consignação em Decisão Judicial</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarLiquidacaoJudicial" })
public class ExecutarLiquidacaoJudicialWebController extends LiquidarConsignacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarLiquidacaoJudicialWebController.class);

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.opcao.liquidar.consignacao", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarLiquidacaoJudicial");
        model.addAttribute("acaoListarCidades", "executarDecisaoJudicial");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_ANEXO, responsavel));

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
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.DECISAO_JUDICIAL_LIQUIDAR_CONSIGNACAO_ANEXO, responsavel);
    }

    @Override
    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.confirmarLiquidacao(request, response, session, model);
    }

    @Override
    protected boolean temPermissaoAnexarLiquidar(AcessoSistema responsavel) {
        return true;
    }
}
