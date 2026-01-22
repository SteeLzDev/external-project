package com.zetra.econsig.web.controller.contratobeneficio;

import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AlterarContratoBeneficioWebController</p>
 * <p>Description: Web Controller do fluxo de visualizar e alterar dados do contrato beneficio</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarContratoBeneficio" })
public class AlterarContratoBeneficioWebController extends AbstractWebController {

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ParametroController parametroController;

    /**
     *
     * @param cbeCodigo
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=visualizar" })
    public String visualizarContratoBeneficio(@RequestParam(value = "cbeCodigo", required = false) String cbeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean exclusaoManual = JspHelper.verificaVarQryStr(request, "exclusaoManual").equals("true");
        boolean inclusaoManual = JspHelper.verificaVarQryStr(request, "inclusaoManual").equals("true");
        boolean cancelarInclusao = JspHelper.verificaVarQryStr(request, "cancelarInclusao").equals("true");

        TransferObject contratoBeneficio;
        try {
            contratoBeneficio = contratoBeneficioController.listarContratosBeneficiosMensalidadeEdicaoTela(cbeCodigo, responsavel);
        } catch (ContratoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Caso o parâmetro não exista, o valor default será o tamanho do campo: #*40
        String tpsMascaraNumeroContratoBeneficio = "#*40";
        // Parâmetros de Serviço necessários
        List<String> parametros = new ArrayList<>();
        parametros.add(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO);
        // Recupera parâmetro de máscara do número do contrato de benefícios
        List<TransferObject> paramSvcCsa = null;
        try {
            String svcCodigo = (String) contratoBeneficio.getAttribute(Columns.SVC_CODIGO);
            String csaCodigo = (String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO);
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, parametros, false, responsavel);
            for (TransferObject to : paramSvcCsa) {
                if (to.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO) && !TextHelper.isNull(to.getAttribute(Columns.PSC_VLR))) {
                    tpsMascaraNumeroContratoBeneficio = (String) to.getAttribute(Columns.PSC_VLR);
                }
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("tpsMascaraNumeroContratoBeneficio", tpsMascaraNumeroContratoBeneficio);
        model.addAttribute("contratoBeneficio", contratoBeneficio);
        model.addAttribute("funEditarContratoBeneficioAvancado", responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO));
        model.addAttribute("funEditarContratoBeneficio", responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO));

        if (exclusaoManual && responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO)) {
            return viewRedirect("jsp/editarContratoBeneficio/editarContratoBeneficioFluxoManual", request, session, model, responsavel);
        } else if((exclusaoManual || inclusaoManual) && !responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.contrato.beneficio.permissao.exclusao.manual", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else if (inclusaoManual && responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO)) {
            model.addAttribute("inclusaoManual", inclusaoManual);
            return viewRedirect("jsp/editarContratoBeneficio/editarContratoBeneficioFluxoManual", request, session, model, responsavel);
        } else if (cancelarInclusao && responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO)) {
            model.addAttribute("cancelarInclusao", cancelarInclusao);
            return viewRedirect("jsp/editarContratoBeneficio/editarContratoBeneficioFluxoManual", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/editarContratoBeneficio/visualizarContratoBeneficio", request, session, model, responsavel);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=salvar" })
    public String salvarContratoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TransferObject contratoBeneficioTo;
        try {
            // Recuperando dados do contrato da tela.
            String cbeCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO));
            String cbeNumero = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_NUMERO));
            String cbeDataInicioVigencia = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA));
            String cbeDataFimVigencia = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA));
            String cbeDataCancelamento = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO));

            // Recuperando dados que tem que ser gravada na aut desconto
            String dad34 = JspHelper.verificaVarQryStr(request, "benAdesaoPlanoExFuncionario");
            String dad35 = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO);
            String dad36 = JspHelper.verificaVarQryStr(request, "benContribuiuPlano");
            String dad37 = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO);

            // Montando um hashmap para ser passado como valor para o metodo.
            CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
            dados.setAttribute(Columns.CBE_NUMERO, cbeNumero);
            dados.setAttribute(Columns.CBE_DATA_INICIO_VIGENCIA, cbeDataInicioVigencia);
            dados.setAttribute(Columns.CBE_DATA_FIM_VIGENCIA, cbeDataFimVigencia);
            dados.setAttribute(Columns.CBE_DATA_CANCELAMENTO, cbeDataCancelamento);

            dados.setAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO, dad34);
            dados.setAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO, dad35);
            dados.setAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO, dad36);
            dados.setAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO, dad37);

            // Atualizando os dados.
            contratoBeneficioController.updateAnalisandoFuncaoEDadosAutorizacao(dados, responsavel);

            // Recuperando os novos dados para serem listado na tela.
            contratoBeneficioTo = contratoBeneficioController.listarContratosBeneficiosMensalidadeEdicaoTela(cbeCodigo, responsavel);

        } catch (ContratoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("contratoBeneficio", contratoBeneficioTo);
        model.addAttribute("funEditarContratoBeneficioAvancado", responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO));
        model.addAttribute("funEditarContratoBeneficio", responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO));

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.margem.exibicao.sucesso", responsavel));
        return viewRedirect("jsp/editarContratoBeneficio/visualizarContratoBeneficio", request, session, model, responsavel);
    }

    /**
    * Exclusão Manual do contrato de benefício, ou seja, não há necessidade do arquivo de operadora para a exclusão.
    * @param request
    * @param response
    * @param session
    * @param model
    * @return
    */
    @RequestMapping(params = { "acao=salvarExclusaoManual" })
    public String salvarExclusaoManual(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean cancelarInclusao = JspHelper.verificaVarQryStr(request, "cancelarInclusao").equals("true");

        CustomTransferObject criterio = new CustomTransferObject();
        try {
            // Recuperando dados do contrato da tela.
            String cbeDataFim = !cancelarInclusao ? JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA)) : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO));
            String cbeDataInicioVigencia = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA));
            String bfcCodigo = JspHelper.verificaVarQryStr(request, "BFC_CODIGO");
            String tibCodigo = JspHelper.verificaVarQryStr(request, "TIB_CODIGO");
            String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
            String benCodigo = JspHelper.verificaVarQryStr(request, "BEN_CODIGO");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDataFim = DateHelper.reformat(cbeDataFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd HH:mm:ss");
            Date dataFim = sdfData.parse(strDataFim);

            String strDataInicioVigencia = DateHelper.reformat(cbeDataInicioVigencia, LocaleHelper.getDatePattern(), "yyyy-MM-dd HH:mm:ss");
            Date dataInicioVigencia = sdfData.parse(strDataInicioVigencia);

            if (TextHelper.isNull(dataFim) || TextHelper.isNull(dataInicioVigencia)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.contrato.beneficio.datas.vazio.exclusao.manual", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Se for titular então preciso buscar todo o grupo familiar.
            if (!tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
            }

            criterio.setAttribute(Columns.SER_CODIGO, serCodigo);
            criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);

            List<TransferObject> beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            // Liquidando Contrato Beneficio
            contratoBeneficioController.exclusaoManual(beneficiarios, dataInicioVigencia, dataFim, cancelarInclusao, responsavel);

            if(!cancelarInclusao) {
                //Caso não seja titular, é preciso calcular o subsídio e ordenação do grupo familiar
                if (!tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                    List<String> rseCodigos = new ArrayList<>();
                    rseCodigos.add(rseCodigo);

                    String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
                    Date periodoAtualBeneficio = PeriodoHelper.getInstance().getPeriodoBeneficioAtual(orgCodigo, responsavel);

                    calcularSubsidioBeneficioController.calcularSubsidioContratosBeneficios(periodoAtualBeneficio, false, null, "RSE", rseCodigos, responsavel);
                }
            }
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.contrato.beneficio.confirma.exclusao.sucesso", responsavel));

            return "forward:/v3/relacaoBeneficios?acao=listar&RSE_CODIGO=" + rseCodigo + "&_skip_history_=true&"+ SynchronizerToken.generateToken4URL(request);

        } catch (ContratoBeneficioControllerException | ParseException | BeneficioControllerException | PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
    /**
    * Inclusão Manual do contrato de benefício, ou seja, não há necessidade do arquivo de operadora para a exclusão.
    * @param request
    * @param response
    * @param session
    * @param model
    * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
    */
    @RequestMapping(params = { "acao=salvarInclusaoManual" })
    public String salvarInclusaoManual(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        CustomTransferObject criterio = new CustomTransferObject();
        try {
            // Recuperando dados do contrato da tela.
            String cbeNumero = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_NUMERO));
            String cbeDataInicioVigencia = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA));
            String bfcCodigo = JspHelper.verificaVarQryStr(request, "BFC_CODIGO");
            String tibCodigo = JspHelper.verificaVarQryStr(request, "TIB_CODIGO");
            String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
            String benCodigo = JspHelper.verificaVarQryStr(request, "BEN_CODIGO");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String nseDescricao = JspHelper.verificaVarQryStr(request, "NSE_DESCRICAO");

            java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDataInicioVigencia = DateHelper.reformat(cbeDataInicioVigencia, LocaleHelper.getDatePattern(), "yyyy-MM-dd HH:mm:ss");
            Date dataInicioVigencia = sdfData.parse(strDataInicioVigencia);

            if (TextHelper.isNull(dataInicioVigencia)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.contrato.beneficio.data.inicio.vigencia.informar", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Agregado só pode participar do fluxo se o parâmetro 708 estiver ativo
            if (TipoBeneficiarioEnum.AGREGADO.tibCodigo.equals(tibCodigo) && !ParamSist.paramEquals(CodedValues.TPC_MOD_BENEFICIO_PERMITE_AGREGADO, CodedValues.TPC_SIM, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.contrato.beneficio.inclusao.manual.agregado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
            criterio.setAttribute(Columns.SER_CODIGO, serCodigo);
            criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);

            List<TransferObject> beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            // É preciso analisar se existe algum contrato ativo ou aguardando exclusão operadora deste beneficiário que esteja na mesma natureza
            for (TransferObject contrato : beneficiarios) {
                String situacao = (String) contrato.getAttribute(Columns.SCB_CODIGO);
                String nse = (String) contrato.getAttribute(Columns.NSE_DESCRICAO);

                if (nse.equals(nseDescricao) && (StatusContratoBeneficioEnum.ATIVO.getCodigo().equals(situacao) || StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo().equals(situacao))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.contrato.beneficio.inclusao.manual.agregado", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Incluindo Contrato Beneficio
            contratoBeneficioController.inclusaoManual(beneficiarios, dataInicioVigencia, cbeNumero, responsavel);

            List<String> rseCodigos = new ArrayList<>();
            rseCodigos.add(rseCodigo);

            String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
            Date periodoAtualBeneficio = PeriodoHelper.getInstance().getPeriodoBeneficioAtual(orgCodigo, responsavel);

            // Calcular Subsídio do grupo familiar para qualquer inclusão.
            calcularSubsidioBeneficioController.calcularSubsidioContratosBeneficios(periodoAtualBeneficio, false, null, "RSE", rseCodigos, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.contrato.beneficio.confirma.inclusao.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ContratoBeneficioControllerException | ParseException | BeneficioControllerException | PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
