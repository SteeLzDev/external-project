package com.zetra.econsig.web.controller.despesacomum;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.DespesaComumControllerException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.EnderecoConjuntoHabitacionalControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargem;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargemFactory;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.DespesaComumController;
import com.zetra.econsig.service.sdp.DespesaIndividualController;
import com.zetra.econsig.service.sdp.EnderecoConjuntoHabitacionalController;
import com.zetra.econsig.service.sdp.PermissionarioController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;
import com.zetra.econsig.values.StatusDespesaComumEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: LancarDespesaComumWebController</p>
 * <p>Description: Controlador Web para caso de uso lançar despesa comum</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25329 $
 * $Date: 2020-07-05 18:15:21 -0300 (Dom, 05 jul 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/lancarDespesaComum" })
public class LancarDespesaComumWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LancarDespesaComumWebController.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private DespesaComumController despesaComumController;

    @Autowired
    private EnderecoConjuntoHabitacionalController enderecoConjuntoHabitacionalController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private ServicoController servicoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String tipo = "despesa_comum";
        boolean isConsultaDespesaComum = tipo.equals("cons_despesa_comum");

        String titulo = "";

        if (isConsultaDespesaComum) {
            titulo = ApplicationResourcesHelper.getMessage("rotulo.despesa.comum.consultar.titulo", responsavel).toUpperCase();
        } else {
            titulo = ApplicationResourcesHelper.getMessage("rotulo.despesa.comum", responsavel).toUpperCase();
        }

        String csaCodigo = responsavel.getCsaCodigo();

        if (TextHelper.isNull(csaCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.usuario", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
        String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
        List<TransferObject> despesasComuns = new ArrayList<>();

        if (isConsultaDespesaComum && (!TextHelper.isNull(echCodigo) || !TextHelper.isNull(plaCodigo))) {
            TransferObject criterios = new CustomTransferObject();
            criterios.setAttribute(Columns.DEC_ECH_CODIGO, echCodigo);
            criterios.setAttribute(Columns.DEC_PLA_CODIGO, plaCodigo);

            int total = despesaComumController.countDespesasComuns(criterios, responsavel);
            if (total > 0) {
                despesasComuns = despesaComumController.findDespesasComuns(criterios, responsavel);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.despesa.comum.nao.encontrado", responsavel));
            }
        }

        String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");

        List<TransferObject> enderecos = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ECH_CSA_CODIGO, csaCodigo);
            enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterio, -1, -1, responsavel);
        } catch (EnderecoConjuntoHabitacionalControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            enderecos = new ArrayList<>();
        }

        if (enderecos == null || enderecos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.enderecos.nao.cadastrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> planos = null;

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            if (tipo.equals("despesa_comum")) {
                criterio.setAttribute(Columns.NPL_CODIGO, false); // evita listar planos de taxa de uso no lançamento
            }
            planos = planoDescontoController.lstPlanoDesconto(criterio, responsavel);
        } catch (PlanoDescontoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            planos = new ArrayList<>();
        }

        if (planos == null || planos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.planos.nao.cadastrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("tipo", tipo);
        model.addAttribute("isConsultaDespesaComum", isConsultaDespesaComum);
        model.addAttribute("titulo", titulo);
        model.addAttribute("echCodigo", echCodigo);
        model.addAttribute("plaCodigo", plaCodigo);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("despesasComuns", despesasComuns);
        model.addAttribute("enderecos", enderecos);
        model.addAttribute("planos", planos);

        return viewRedirect("jsp/manterDespesaComum/pesquisarDespesaComum", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=inserirDados" })
    public String inserirDados(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipo = JspHelper.verificaVarQryStr(request, "tipo").toLowerCase();

        String decData = JspHelper.verificaVarQryStr(request, "decData");
        if (TextHelper.isNull(decData)) {
            decData = DateHelper.format(DateHelper.getSystemDate(), LocaleHelper.getDatePattern());
        }

        String nextLinkRet = "../v3/lancarDespesaComum?acao=iniciar&tipo=" + tipo;
        String linkRet = "../v3/lancarDespesaComum?acao=iniciar&tipo=" + tipo;

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.despesa.comum", responsavel);

        String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
        CustomTransferObject criterioEnd = new CustomTransferObject();
        criterioEnd.setAttribute(Columns.ECH_CODIGO, echCodigo);
        List<TransferObject> enderecos = null;
        try {
            enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterioEnd, -1, -1, responsavel);
        } catch (EnderecoConjuntoHabitacionalControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (enderecos == null || enderecos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.endereco.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TransferObject endereco = enderecos.get(0);

        String svcCodigo = null;
        String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
        TransferObject plano = null;
        TransferObject criterioPla = new CustomTransferObject();
        criterioPla.setAttribute(Columns.PLA_CODIGO, plaCodigo);
        try {
            plano = planoDescontoController.buscaPlanoDesconto(criterioPla, responsavel);
            svcCodigo = plano.getAttribute(Columns.SVC_CODIGO).toString();
        } catch (PlanoDescontoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.plano.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (plano != null && plano.getAttribute(Columns.NPL_CODIGO).equals(NaturezaPlanoEnum.TAXA_USO.getCodigo())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.incluir.despesa.comum.plano.taxa.uso", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> postos = null;
        try {
            postos = postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel);
            svcCodigo = plano.getAttribute(Columns.SVC_CODIGO).toString();
        } catch (PostoRegistroServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.plano.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Seleciona o serviço
        CustomTransferObject servico = null;
        try {
            servico = servicoController.findServico(svcCodigo);
        } catch (ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.servico.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (servico == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.servico.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String vlrCache = JspHelper.verificaVarQryStr(request, "cache");
        Map<String, String> cache = JspHelper.recuperaParametro(vlrCache, "|", "(");

        String csaNome = responsavel.getNomeEntidade();
        CustomTransferObject convenio = null;

        //Lista dos parâmetros de plano necessários
        Map<String, String> parametrosPlano = new HashMap<>();
        try {
            List<TransferObject> lstParamPlano = parametroController.selectParamPlano(plaCodigo, responsavel);

            for (TransferObject ppl : lstParamPlano) {
                String paramName = (String) ppl.getAttribute(Columns.TPP_CODIGO);
                String paramVlr = (String) ppl.getAttribute(Columns.PPL_VALOR);

                parametrosPlano.put(paramName, paramVlr);
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.recuperar.parametros.plano", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        boolean descontoPosto = parametrosPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && parametrosPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_SIM);

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Se tem simulação, seleciona lista de prazos para o serviço
        String arPrazos = null;

        boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);

        if (temSimulacaoConsignacao || paramSvcCse.isTpsValidarTaxaJuros()) {
            // Seleciona prazos ativos.
            try {
                // pega os prazos referente ao numero de prestacoes
                List<?> prazos = new ArrayList<>(); // simDelegate.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, responsavel);
                Iterator<?> it = prazos.iterator();
                CustomTransferObject prazo = null;
                String cnvCodigo = null, przVlr = null;
                if (it.hasNext()) {
                    arPrazos = "var arPrazos = [";
                }

                while (it.hasNext()) {
                    prazo = (CustomTransferObject) it.next();
                    cnvCodigo = prazo.getAttribute(Columns.CNV_CODIGO).toString();
                    przVlr = prazo.getAttribute(Columns.PRZ_VLR).toString();
                    arPrazos += "['" + przVlr + "', '" + przVlr + "', '" + cnvCodigo + "']";

                    if (it.hasNext()) {
                        arPrazos += ",";
                    } else {
                        arPrazos += "];";
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        //***********************************************/
        // Parâmetros de Serviço

        String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
        boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
        if (alteraAdeVlr && parametrosPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO)) {
            alteraAdeVlr = parametrosPlano.get(CodedValues.TPP_VLR_FIXO_PLANO).equals(CodedValues.PLANO_VALOR_ALTERAVEL); // Habilita ou nao campo de valor da reserva dependendo da configuração do plano
        }
        String adeVlrPadrao = (paramSvcCse.getTpsAdeVlr() != null && !paramSvcCse.getTpsAdeVlr().equals("")) ? NumberHelper.reformat(paramSvcCse.getTpsAdeVlr(), "en", NumberHelper.getLang()) : "0"; // Valor da prestação fixo para o serviço
        if (paramSvcCse.isTpsAlteraAdeVlr() && parametrosPlano.containsKey(CodedValues.TPP_VLR_PLANO)) {
            String valorPlano = parametrosPlano.get(CodedValues.TPP_VLR_PLANO);
            adeVlrPadrao = !TextHelper.isNull(valorPlano) ? NumberHelper.reformat(valorPlano, "en", NumberHelper.getLang()) : adeVlrPadrao; // Valor da prestação fixo para o plano
        }
        boolean prazoFixo = paramSvcCse.isTpsPrazoFixo();
        if (!prazoFixo && parametrosPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO)) {
            prazoFixo = parametrosPlano.get(CodedValues.TPP_PRAZO_FIXO_PLANO).equals(CodedValues.PLANO_PRAZO_FIXO_SIM);
        }
        if (prazoFixo) {
            arPrazos = null;
        }
        String maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null && !paramSvcCse.getTpsMaxPrazo().equals("")) ? paramSvcCse.getTpsMaxPrazo() : "-1";
        if (maxPrazo.equals("-1") || (!paramSvcCse.isTpsPrazoFixo() && prazoFixo)) {
            String maxPrazoPlano = parametrosPlano.get(CodedValues.TPP_PRAZO_MAX_PLANO);
            maxPrazo = (!TextHelper.isNull(maxPrazoPlano)) ? maxPrazoPlano : "-1";

        }
        String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

        // Parâmetro com nome da classe java, que implementa a interface ProcessaReservaMargem
        String classeProcReserva = paramSvcCse.getTpsClasseJavaProcEspecificoReserva();

        //***********************************************/
        // Parâmetros de Convênio
        String endDescricao = endereco.getAttribute(Columns.ECH_IDENTIFICADOR).toString() + " - " + endereco.getAttribute(Columns.ECH_DESCRICAO).toString();
        String plaDescricao = plano.getAttribute(Columns.PLA_IDENTIFICADOR).toString() + " - " + plano.getAttribute(Columns.PLA_DESCRICAO).toString();
        String svcIdentificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
        String svcDescricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
        String descricao = svcIdentificador + " - " + svcDescricao;
        String cnvCodigo = convenio != null ? convenio.getAttribute(Columns.CNV_CODIGO).toString() : "";

        // Se o serviço possui processamento específico de reserva, cria a classe de execução
        ProcessaReservaMargem processador = null;
        if (classeProcReserva != null) {
            try {
                processador = ProcessaReservaMargemFactory.getProcessador(classeProcReserva);
            } catch (ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        //recupera os valores de plano de desconto
        List<TransferObject> lstParamPla = null;
        HashMap<String, String> hshParamPlano = new HashMap<>();
        if (!TextHelper.isNull(plaCodigo)) {
            try {
                lstParamPla = parametroController.selectParamPlano(plaCodigo, responsavel);

                Iterator<?> itParam = lstParamPla.iterator();
                while (itParam.hasNext()) {
                    CustomTransferObject ctoParam = (CustomTransferObject) itParam.next();

                    String paramName = (String) ctoParam.getAttribute(Columns.TPP_CODIGO);
                    String paramVlr = (String) ctoParam.getAttribute(Columns.PPL_VALOR);

                    hshParamPlano.put(paramName, paramVlr);
                }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                lstParamPla = new ArrayList<>();
            }
        }

        String adeCarencia = !JspHelper.verificaVarQryStr(request, "adeCarencia").equals("") ? JspHelper.verificaVarQryStr(request, "adeCarencia") : "0";
        String planoPorDesconto = hshParamPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && hshParamPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_SIM) ? "true" : "false";
        String rateio = hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) && hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_SEM_RATEIO) ? "nada" : hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) && hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_PERMISSIONARIO) ? "perm" : hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) && hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_UNIDADE) ? "uni" : "";
        String tppIndice = hshParamPlano.get(CodedValues.TPP_INDICE_PLANO);

        model.addAttribute("tipo", tipo);
        model.addAttribute("decData", decData);
        model.addAttribute("nextLinkRet", nextLinkRet);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("titulo", titulo);
        model.addAttribute("echCodigo", echCodigo);
        model.addAttribute("plaCodigo", plaCodigo);
        model.addAttribute("endereco", endereco);
        model.addAttribute("postos", postos);
        model.addAttribute("cache", cache);
        model.addAttribute("descontoPosto", descontoPosto);
        model.addAttribute("arPrazos", arPrazos);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("tipoVlr", tipoVlr);
        model.addAttribute("alteraAdeVlr", alteraAdeVlr);
        model.addAttribute("adeVlrPadrao", adeVlrPadrao);
        model.addAttribute("prazoFixo", prazoFixo);
        model.addAttribute("maxPrazo", maxPrazo);
        model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
        model.addAttribute("endDescricao", endDescricao);
        model.addAttribute("plaDescricao", plaDescricao);
        model.addAttribute("descricao", descricao);
        model.addAttribute("cnvCodigo", cnvCodigo);
        model.addAttribute("processador", processador);
        model.addAttribute("hshParamPlano", hshParamPlano);
        model.addAttribute("tppIndice", tppIndice);
        model.addAttribute("adeCarencia", adeCarencia);
        model.addAttribute("planoPorDesconto", planoPorDesconto);
        model.addAttribute("rateio", rateio);

        return viewRedirect("jsp/manterDespesaComum/inserirDadosDespesaComum", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=listarPermissionario" })
    public String listarPermissionario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        String csaNome = responsavel.getNomeEntidade();
        String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
        String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
        String posCodigo = JspHelper.verificaVarQryStr(request, "POS_CODIGO");
        String svcCodigo = null;
        String svcDescricao = null;
        Date decData = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "decData")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "decData"), LocaleHelper.getDatePattern()) : null);
        String adePrazo = JspHelper.verificaVarQryStr(request, "adePrazo");
        String adeSemPrazo = JspHelper.verificaVarQryStr(request, "adeSemPrazo");
        String adeValor = JspHelper.verificaVarQryStr(request, "adeVlr");
        String adeIdentificador = JspHelper.verificaVarQryStr(request, "adeIdentificador");
        String adeCarencia = JspHelper.verificaVarQryStr(request, "adeCarencia");
        String indice = JspHelper.verificaVarQryStr(request, "indice") != null ? JspHelper.verificaVarQryStr(request, "indice") : "";
        String planoPorDesconto = JspHelper.verificaVarQryStr(request, "planoPorDesconto");
        String rateio = JspHelper.verificaVarQryStr(request, "rateio");

        CustomTransferObject criterioEnd = new CustomTransferObject();
        criterioEnd.setAttribute(Columns.ECH_CODIGO, echCodigo);
        List<TransferObject> enderecos = null;
        try {
            enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterioEnd, -1, -1, responsavel);
        } catch (EnderecoConjuntoHabitacionalControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (enderecos == null || enderecos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.endereco.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TransferObject endereco = enderecos.get(0);

        TransferObject plano = null;
        TransferObject criterioPla = new CustomTransferObject();
        criterioPla.setAttribute(Columns.PLA_CODIGO, plaCodigo);
        try {
            plano = planoDescontoController.buscaPlanoDesconto(criterioPla, responsavel);
            svcCodigo = plano.getAttribute(Columns.SVC_CODIGO).toString();
        } catch (PlanoDescontoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.plano.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Seleciona o serviço
        CustomTransferObject servico = null;
        try {
            servico = servicoController.findServico(svcCodigo);
            svcDescricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
        } catch (ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.servico.despesa", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> permissionarios = null;
        try {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.PRM_ATIVO, CodedValues.STS_ATIVO);
            criterio.setAttribute(Columns.ECH_CODIGO, echCodigo);
            criterio.setAttribute(Columns.POS_CODIGO, posCodigo);
            if (!TextHelper.isNull(decData)) {
                criterio.setAttribute(Columns.DEC_DATA, decData);
            }

            int total = permissionarioController.countPermissionarios(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            if (total < 1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.endereco.vazio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            permissionarios = permissionarioController.lstPermissionarios(criterio, offset, size, responsavel);

        } catch (PermissionarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            permissionarios = new ArrayList<>();
        }

        TransferObject posto = null;
        if (!TextHelper.isNull(posCodigo)) {
            try {
                posto = postoRegistroServidorController.buscaPosto(posCodigo, responsavel);
            } catch (PostoRegistroServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        String postoDescricao = posto != null && !TextHelper.isNull(posto.getAttribute(Columns.POS_DESCRICAO)) ? posto.getAttribute(Columns.POS_DESCRICAO).toString() : "";
        String endDescricao = endereco.getAttribute(Columns.ECH_IDENTIFICADOR).toString() + " - " + endereco.getAttribute(Columns.ECH_DESCRICAO).toString();
        String plaDescricao = plano.getAttribute(Columns.PLA_IDENTIFICADOR).toString() + " - " + plano.getAttribute(Columns.PLA_DESCRICAO).toString();
        String svcIdentificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
        svcDescricao = svcIdentificador + " - " + servico.getAttribute(Columns.SVC_DESCRICAO).toString();

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        int maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null && !paramSvcCse.getTpsMaxPrazo().equals("")) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;
        String labelTipoValor = ParamSvcTO.getDescricaoTpsTipoVlr(paramSvcCse.getTpsTipoVlr());
        String labelAdePrazo = (maxPrazo == 0) ? ApplicationResourcesHelper.getMessage("rotulo.plano.indeterminado", responsavel) : adePrazo;
        String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

        model.addAttribute("tipo", tipo);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("echCodigo", echCodigo);
        model.addAttribute("plaCodigo", plaCodigo);
        model.addAttribute("posCodigo", posCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("decData", decData);
        model.addAttribute("adePrazo", adePrazo);
        model.addAttribute("adeSemPrazo", adeSemPrazo);
        model.addAttribute("adeValor", adeValor);
        model.addAttribute("adeIdentificador", adeIdentificador);
        model.addAttribute("adeCarencia", adeCarencia);
        model.addAttribute("indice", indice);
        model.addAttribute("planoPorDesconto", planoPorDesconto);
        model.addAttribute("rateio", rateio);
        model.addAttribute("permissionarios", permissionarios);
        model.addAttribute("postoDescricao", postoDescricao);
        model.addAttribute("endDescricao", endDescricao);
        model.addAttribute("plaDescricao", plaDescricao);
        model.addAttribute("labelTipoValor", labelTipoValor);
        model.addAttribute("labelAdePrazo", labelAdePrazo);
        model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
        model.addAttribute("endereco", endereco);

        return viewRedirect("jsp/manterDespesaComum/listarPermissionarioDespesaComum", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=lancarDespesaComum" })
    public String lancarDespesa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");

        //Sincroniza a sessão do usuário para evitar duplo request
        synchronized (session) {
            if (tipo.equalsIgnoreCase("despesa_comum")) {
                String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
                String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
                String posCodigo = JspHelper.verificaVarQryStr(request, "POS_CODIGO");
                Date decData = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "decData")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "decData"), LocaleHelper.getDatePattern()) : null);
                String adeValor = JspHelper.verificaVarQryStr(request, "adeVlr");
                adeValor = NumberHelper.reformat(adeValor.toString(), NumberHelper.getLang(), "en");
                String adeCarencia = JspHelper.verificaVarQryStr(request, "adeCarencia");

                Integer adePrazo = null;
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "adePrazo"))) {
                    adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "adePrazo"));
                }

                CustomTransferObject criterioEnd = new CustomTransferObject();
                criterioEnd.setAttribute(Columns.ECH_CODIGO, echCodigo);

                List<TransferObject> enderecos = null;
                try {
                    enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterioEnd, -1, -1, responsavel);
                } catch (EnderecoConjuntoHabitacionalControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                if (enderecos == null || enderecos.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.localizar.endereco.despesa", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                try {
                    TransferObject despesaComum = new CustomTransferObject();
                    despesaComum.setAttribute(Columns.DEC_ECH_CODIGO, echCodigo);
                    despesaComum.setAttribute(Columns.DEC_PLA_CODIGO, plaCodigo);
                    despesaComum.setAttribute(Columns.DEC_POS_CODIGO, posCodigo);
                    despesaComum.setAttribute(Columns.DEC_DATA, decData);
                    despesaComum.setAttribute(Columns.DEC_VALOR, new BigDecimal(adeValor));
                    despesaComum.setAttribute(Columns.DEC_PRAZO, adePrazo);
                    despesaComum.setAttribute(Columns.DEC_IDENTIFICADOR, JspHelper.verificaVarQryStr(request, "adeIdentificador"));

                    String decCodigo = despesaComumController.createDespesaComum(despesaComum, adeCarencia, responsavel);

                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.incluir.despesa.comum.sucesso", responsavel));
                    return "forward:/v3/lancarDespesaComum?acao=editarDespesaComum&tipo=" + tipo + "&decCodigo=" + decCodigo + "&" + SynchronizerToken.generateToken4URL(request);

                } catch (DespesaComumControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.despesa.comum.incluir.motivo.arg0", responsavel, ex.getMessage()));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editarDespesaComum" })
    public String editarDespesaComum(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String reqColumnsStr = "";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        String csaNome = responsavel.getNomeEntidade();
        String decCodigo = JspHelper.verificaVarQryStr(request, "decCodigo");
        String cancelar = JspHelper.verificaVarQryStr(request, "cancelar");

        TransferObject despesaComum = despesaComumController.findDespesaComum(decCodigo, responsavel);
        String statusDespesaComum = (String) despesaComum.getAttribute(Columns.SDC_CODIGO);

        if (TextHelper.isNull(decCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(cancelar) && cancelar.equals("1") && responsavel.temPermissao(CodedValues.FUN_CANCELAR_DESPESA_COMUM) && statusDespesaComum.equals(StatusDespesaComumEnum.ATIVO.getCodigo())) {
            try {
                despesaComumController.cancelarDespesaComum(decCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.despesa.comum.sucesso", responsavel));
                despesaComum = despesaComumController.findDespesaComum(decCodigo, responsavel);
                statusDespesaComum = (String) despesaComum.getAttribute(Columns.SDC_CODIGO);
            } catch (DespesaComumControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        List<TransferObject> despesasIndividuais = null;
        try {
            despesasIndividuais = despesaIndividualController.findDespesasIndividuais(decCodigo, responsavel);
        } catch (DespesaIndividualControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        List<TransferObject> hist = despesaComumController.findOcorrencias(decCodigo, responsavel);

        model.addAttribute("msgErro", msgErro);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("decCodigo", decCodigo);
        model.addAttribute("despesaComum", despesaComum);
        model.addAttribute("statusDespesaComum", statusDespesaComum);
        model.addAttribute("despesasIndividuais", despesasIndividuais);
        model.addAttribute("hist", hist);

        return viewRedirect("jsp/manterDespesaComum/editarDespesaComum", request, session, model, responsavel);

    }
}
