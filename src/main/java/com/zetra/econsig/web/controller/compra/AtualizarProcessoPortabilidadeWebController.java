package com.zetra.econsig.web.controller.compra;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/atualizarProcessoPortabilidade" })
public class AtualizarProcessoPortabilidadeWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizarProcessoPortabilidadeWebController.class);

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (request.getParameter("ADE_CODIGO") != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String adeCodigo = request.getParameter("ADE_CODIGO");

        String adeCodigoDestino = null;
        List<TransferObject> adeRelList = null;
        try {
            adeRelList = pesquisarConsignacaoController.pesquisarConsignacaoRelacionamento(adeCodigo, null, null, null, null, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (adeRelList != null && !adeRelList.isEmpty()) {
            CustomTransferObject relObjct = (CustomTransferObject) adeRelList.get(0);
            adeCodigoDestino = (String) relObjct.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.contrato.destino.compra.nao.encontrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //Busca o contrato a ser alterado
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigoDestino, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();

        //Verifica se o sistema está configurado para trabalhar com o CET.
        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        //Parâmetros de Sistema Necessários
        //Verifica se o sistema permite cadastro de índice
        boolean permiteCadIndice = ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel).toString().equals(CodedValues.TPC_SIM);
        //Índice cadastrado automaticamente
        boolean indiceSomenteAutomatico = ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel);

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Short adeIncMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
        String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
        boolean serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
        String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
        int maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null && !paramSvcCse.getTpsMaxPrazo().equals("")) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;
        boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac();
        boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof();
        boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();
        boolean boolTpsSegPrestamista = paramSvcCse.isTpsExigeSeguroPrestamista();
        boolean permiteVlrLiqTxJuros = paramSvcCse.isTpsVlrLiqTaxaJuros();
        String exigeSenhaServidor = paramSvcCse.getTpsExigeSenhaAlteracaoContratos();
        String adeVlrPadrao = "";

        String adeIndice = (autdes.getAttribute(Columns.ADE_INDICE) != null) ? autdes.getAttribute(Columns.ADE_INDICE).toString() : "";
        int prazo = autdes.getAttribute(Columns.ADE_PRAZO) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRAZO)).intValue() : -1;
        int pagas = autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS)).intValue() : 0;
        int prazoRest = prazo - pagas;

        String mensagem = "";
        if (autdes.getAttribute(Columns.PRD_ADE_CODIGO) != null) {
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel) + "\n";
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel));
        }

        //Otbém a margem do servidor
        MargemDisponivel margemDisponivel = null;
        try {
            margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, responsavel);
        } catch (ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        BigDecimal margemRestOld = margemDisponivel.getMargemRestante();

        //Margem restante é a margem + o valor do contrato
        BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
        BigDecimal margemRestNew = (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) ? margemRestOld.add(adeVlr) : margemRestOld;

        //Periodicidade do contrato
        String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);

        //Se tipo valor igual a margem total, coloca no campo de adeVlr o
        //valor da margem disponível para o serviço
        if (tipoVlr.equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)) {
            adeVlrPadrao = margemRestNew.toString();
        }

        String adeVlrAtual = !adeVlrPadrao.equals("") ? adeVlrPadrao : adeVlr.toString();
        adeVlrAtual = NumberHelper.reformat(adeVlrAtual, "en", NumberHelper.getLang());
        Date anoMesIni = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
        Integer valorAdeCarencia = null;

        try {
            valorAdeCarencia = PeriodoHelper.getInstance().calcularCarencia(orgCodigo, anoMesIni, (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE), responsavel);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);

        model.addAttribute("mascaraLogin", mascaraLogin);
        model.addAttribute("valorAdeCarencia", valorAdeCarencia);
        model.addAttribute("adeCodigoDestino", adeCodigoDestino);
        model.addAttribute("autdes", autdes);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("temCET", temCET);
        model.addAttribute("permiteCadIndice", permiteCadIndice);
        model.addAttribute("indiceSomenteAutomatico", indiceSomenteAutomatico);
        model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
        model.addAttribute("labelTipoVlr", labelTipoVlr);
        model.addAttribute("maxPrazo", maxPrazo);
        model.addAttribute("permiteCadVlrTac", permiteCadVlrTac);
        model.addAttribute("permiteCadVlrIof", permiteCadVlrIof);
        model.addAttribute("permiteCadVlrMensVinc", permiteCadVlrMensVinc);
        model.addAttribute("boolTpsSegPrestamista", boolTpsSegPrestamista);
        model.addAttribute("permiteVlrLiqTxJuros", permiteVlrLiqTxJuros);
        model.addAttribute("exigeSenhaServidor", exigeSenhaServidor);
        model.addAttribute("adeVlrAtual", adeVlrAtual);
        model.addAttribute("adeIndice", adeIndice);
        model.addAttribute("prazo", prazo);
        model.addAttribute("prazoRest", prazoRest);
        model.addAttribute("mensagem", mensagem);
        model.addAttribute("adeVlr", adeVlr);
        model.addAttribute("adePeriodicidade", adePeriodicidade);

        return viewRedirect("jsp/atualizarProcessoPortabilidade/atualizarAdeCompra", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=atualizarContrato" })
    public String atualizarContrato(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (request.getParameter("ADE_CODIGO") != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //Executa a alteração do contrato
        try {

            BigDecimal adeVlrLiquido = null;
            BigDecimal adeVlr = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlr"), NumberHelper.getLang(), "en"));

            String adeCodigoDestino = JspHelper.verificaVarQryStr(request, "adeUpdate");
            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigoDestino, responsavel);

            String exigeSenhaServidor = JspHelper.verificaVarQryStr(request, "exigeSenhaServidor");
            String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();

            if (!JspHelper.verificaVarQryStr(request, "adeVlrLiquido").equals("")) {
                adeVlrLiquido = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrLiquido"), NumberHelper.getLang(), "en"));
            }

            if (exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS)) {

                if (TextHelper.isNull(request.getParameter("serAutorizacao"))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else {
                    SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, true, false, responsavel);
                }
            }

            String senha = null;
            String serLogin = null;

            // Obtém a Senha criptografada
            if (session.getAttribute("serAutorizacao") != null) {
                // Se o parâmetro com a Senha está na sessão, então dá preferencia para ele
                senha = (String) session.getAttribute("serAutorizacao");
                serLogin = (String) session.getAttribute("serLogin");
                session.removeAttribute("serAutorizacao");
            } else {
                // A senha não está na sessão, então pode estar no request
                senha = request.getParameter("serAutorizacao");
                serLogin = request.getParameter("serLogin");
            }

            String senhaAberta = null;
            if (senha != null && !senha.equals("")) {
                KeyPair keyPair = LoginHelper.getRSAKeyPair(request);

                try {
                    senhaAberta = RSA.decrypt(senha, keyPair.getPrivate());
                } catch (BadPaddingException e) {
                    // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                    throw new ViewHelperException("mensagem.senha.servidor.consulta.invalida", responsavel);
                }
            }

            AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigoDestino, adeVlr, (Integer) autdes.getAttribute(Columns.ADE_PRAZO), null, null, null, null, adeVlrLiquido, null, null, null, null, serLogin, senhaAberta);

            alterarParam.setAdePeriodicidade((String) autdes.getAttribute(Columns.ADE_PERIODICIDADE));

            alterarConsignacaoController.atualizarConsignacao(alterarParam, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.saldo.devedor.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (AutorizacaoControllerException | ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
