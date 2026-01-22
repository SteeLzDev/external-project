package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ReimplantarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Reimplantar Consignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reimplantarConsignacao" })
public class ReimplantarConsignacaoWebController extends AbstractWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Busca a autorização
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        Short adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
        Date adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
        String adeTipoVlr = (String) autdes.getAttribute(Columns.ADE_TIPO_VLR);
        String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr);
        BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
        BigDecimal adeVlrPrevisto = adeVlr;

        // Verifica se exige motivo da operacao, tanto o parâmetro de sistema quanto a função
        boolean exigeMotivo = (ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REIMP_CONSIGNACAO, responsavel));

        // Verifica se pode alterar o número do contrato no reimplante manual (deve possuir também a classe de geração de número de ade)
        boolean permiteAlterarNumeroAde = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_NUMERO_REIMP_MANUAL, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_GERADOR_ADE_NUMERO, responsavel)));

        // Verifica se permite reimplante com redução do valor da parcela
        boolean permiteReducaoValorParcela = (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR, CodedValues.TPC_SIM, responsavel));

        if (permiteReducaoValorParcela) {
            // Obtém a margem do servidor, para verificar se pode realizar reimplante com redução do valor
            // da parcela, para que esta se adeque à margem
            MargemDisponivel margemDisponivel = null;
            try {
                margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, responsavel);
            } catch (ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Se a margem está negativa, permite redução do valor da parcela
            permiteReducaoValorParcela = (margemDisponivel.getMargemRestante().signum() < 0);
            // Determina o valor previsto para a parcela, abatendo a margem já negativa
            if (permiteReducaoValorParcela) {
                adeVlrPrevisto = adeVlr.add(margemDisponivel.getMargemRestante());
                // Se o valor estimado for negativo, não permite a redução do valor de parcela
                permiteReducaoValorParcela = (adeVlrPrevisto.signum() > 0);
            }
        }

        Set<Date> periodos = null;
        try {
            periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);
        } catch (PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("autdes", autdes);
        model.addAttribute("exigeMotivo", exigeMotivo);
        model.addAttribute("permiteAlterarNumeroAde", permiteAlterarNumeroAde);
        model.addAttribute("permiteReducaoValorParcela", permiteReducaoValorParcela);
        model.addAttribute("labelTipoVlr", labelTipoVlr);
        model.addAttribute("adeVlrPrevisto", NumberHelper.format(adeVlrPrevisto.doubleValue(), NumberHelper.getLang()));
        model.addAttribute("periodos", periodos);

        return viewRedirect("jsp/reimplantarConsignacao/reimplantarConsignacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=reimplantar" })
    public String reimplantar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ServicoTransferObject servico = convenioController.findServicoByAdeCodigo(adeCodigo, responsavel);
            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_REIMP_CONSIGNACAO, responsavel.getUsuCodigo(), servico.getSvcCodigo())) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Verifica se exige motivo da operacao, tanto o parâmetro de sistema quanto a função
            boolean exigeMotivo = (ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REIMP_CONSIGNACAO, responsavel));

            //Verifica se pode alterar o número do contrato no reimplante manual (deve possuir também a classe de geração de número de ade)
            boolean permiteAlterarNumeroAde = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_NUMERO_REIMP_MANUAL, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_GERADOR_ADE_NUMERO, responsavel)));

            //Verifica se permite reimplante com redução do valor da parcela
            boolean permiteReducaoValorParcela = (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR, CodedValues.TPC_SIM, responsavel));

            try {
                CustomTransferObject tmo = null;
                String obsOca = null;

                boolean alterarNumeroAde = (permiteAlterarNumeroAde && JspHelper.verificaVarQryStr(request, "alterarNumeroAde").equals("true"));
                boolean reduzirValorAde = (permiteReducaoValorParcela && JspHelper.verificaVarQryStr(request, "reduzirValorAde").equals("true"));

                if (!exigeMotivo) {
                    obsOca = JspHelper.verificaVarQryStr(request, "obs");
                } else {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                    tmo.setAttribute(Columns.OCA_PERIODO, JspHelper.verificaVarQryStr(request, "OCA_PERIODO"));
                }


                CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                Date adeAnoMesIniOld = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                reimplantarConsignacaoController.reimplantar(adeCodigo, obsOca, tmo, alterarNumeroAde, reduzirValorAde, false, responsavel);

                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                String rseCodigo = (String) autdes.getAttribute(Columns.RSE_CODIGO);
                Short incMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);

                Date adeAnoMesIniNew = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                MargemRegistroServidor mrsRse = consultarMargemController.getMargemRegistroServidor(rseCodigo, incMargem, responsavel);

                if(mrsRse !=null && adeAnoMesIniNew.compareTo(adeAnoMesIniOld) > 0 && mrsRse.getMrsPeriodoIni() != null && adeAnoMesIniNew.compareTo(mrsRse.getMrsPeriodoIni()) >=0 ) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reimplantar.consignacao.concluido.sucesso", responsavel));

                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                }
            } catch (AutorizacaoControllerException mae) {
                session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
            }

            ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
