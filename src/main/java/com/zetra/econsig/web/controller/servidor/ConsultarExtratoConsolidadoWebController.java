package com.zetra.econsig.web.controller.servidor;

import java.util.ArrayList;
import java.util.List;

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
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ExtratoConsolidadoServidor;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConsultarExtratoConsolidadoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ConsultarExtratoConsolidado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarExtratoConsolidado" })
public class ConsultarExtratoConsolidadoWebController extends AbstractConsultarServidorWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        return pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "pesquisarConsignacao";
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Carregar a lista de consignatárias
        if (responsavel.isCseSupOrg() || responsavel.isSer()) {
            try {
                List<TransferObject> lstConsignataria = convenioController.getCsaCnvAtivo(null, responsavel.getOrgCodigo(), responsavel);
                model.addAttribute("lstConsignatariaMultipla", lstConsignataria);
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        // Carregar a lista de serviços
        try {
            List<TransferObject> lstServico = null;

            if (responsavel.isCsaCor() || responsavel.isOrg()) {
                CustomTransferObject criterio = new CustomTransferObject();
                if (responsavel.isCsaCor()) {
                    criterio.setAttribute(Columns.CNV_CSA_CODIGO, responsavel.getCsaCodigo());
                } else if (responsavel.isOrg()) {
                    criterio.setAttribute(Columns.CNV_ORG_CODIGO, responsavel.getOrgCodigo());
                }
                criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.NOT_EQUAL_KEY + CodedValues.SCV_INATIVO);
                lstServico = convenioController.listCnvScvCodigo(criterio, responsavel);
            } else {
                lstServico = convenioController.lstServicos(null, responsavel);
            }
            model.addAttribute("lstServicoMultiplo", lstServico);
        } catch (ConvenioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Filtros de CSA e SVC são opcionais
        model.addAttribute("filtrosOpcionais", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=pesquisarConsignacao" })
    public String pesquisarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");

        if(adeNumeros != null) {
            for(String adeNum : adeNumeros) {
                if(!adeNum.matches("^[0-9]+$")) {
                    throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, adeNum);
                }
            }
        }

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }
        if (TextHelper.isNull(rseCodigo) && TextHelper.isNull(adeNumero) && (adeNumeros == null || adeNumeros.length == 0)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.campo", responsavel));
            return iniciar(request, response, session, model);
        }

        // Necessário quando vem direto da consulta de servidor, ou seja, só tem um servidor no resultado.
        // O RSE_CODIGO ainda não existe no request Parameters
        model.addAttribute("RSE_CODIGO", rseCodigo);
        request.setAttribute("RSE_CODIGO", rseCodigo);

        configurarPagina(request, session, model, responsavel);

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        // TODO Remover quando o caso de uso for refatorado
        model.addAttribute("_skip_history_", Boolean.TRUE);

        // Redireciona para a página de listagem
        return exibirExtratoConsolidado(request, session, model, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.extrato.consolidado.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarExtratoConsolidado");
        model.addAttribute("imageHeader", "i-operacional");
        if (responsavel.isSer()) {
            model.addAttribute("proximaOperacao", "pesquisarConsignacao");
        }
    }

    private String exibirExtratoConsolidado(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
        String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");

        List<Long> adeNumeroList = new ArrayList<>();
        try {
            if (!TextHelper.isNull(adeNumero)) {
                adeNumeroList.add(Long.parseLong(adeNumero));
            }
            if (adeNumeros != null && adeNumeros.length > 0) {
                for (String numero : adeNumeros) {
                    adeNumeroList.add(Long.parseLong(numero));
                }
            }
        } catch (NumberFormatException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.valor.numerico.generico", responsavel));
        }

        String rseCodigo = (request.getAttribute("RSE_CODIGO") != null ? request.getAttribute("RSE_CODIGO").toString() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO"));
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        if (TextHelper.isNull(rseCodigo) && adeNumeroList.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String data = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());

        // Filtro de serviços
        List<String> svcCodigos = new ArrayList<>();
        String servicos = "";
        if (request.getParameterValues("SVC_CODIGO") != null && !TextHelper.isNull(request.getParameterValues("SVC_CODIGO")[0])) {
            String svcs[] = request.getParameterValues("SVC_CODIGO");
            for (String svc : svcs) {
                svcCodigos.add(svc);
                try {
                    servicos += convenioController.findServico(svc, responsavel).getSvcDescricao() + ", ";
                } catch (ConvenioControllerException e) {
                    session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                }
            }
            servicos = servicos.substring(0, servicos.length() - 2);
        }

        // Filtro de consignataria
        List<String> csaCodigos = new ArrayList<>();
        String consignatarias = "";
        if (request.getParameterValues("CSA_CODIGO") != null && !TextHelper.isNull(request.getParameterValues("CSA_CODIGO")[0])) {
            String csas[] = request.getParameterValues("CSA_CODIGO");
            try {
                for (String csa : csas) {
                    csaCodigos.add(csa);
                    consignatarias += consignatariaController.findConsignataria(csa, responsavel).getCsaNome() + ", ";
                }
            } catch (ConsignatariaControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            }
            consignatarias = consignatarias.substring(0, consignatarias.length() - 2);
        }

        String cseNome = null;
        if (responsavel.isCseSup()) {
            if (responsavel.getNomeEntidade() != null) {
                cseNome = responsavel.getNomeEntidade();
            }
        } else {
            if (LoginHelper.getCseNome(responsavel) != null) {
                cseNome = LoginHelper.getCseNome(responsavel);
            }
            if (responsavel.isCsa()) {
                csaCodigos.add(responsavel.getCodigoEntidade());
                consignatarias = responsavel.getNomeEntidade();
            } else if (responsavel.isCor()) {
                csaCodigos.add(responsavel.getCodigoEntidadePai());
                consignatarias = responsavel.getNomeEntidadePai();
            }
        }

        // Pesquisa os contratos do servidor
        List<TransferObject> ades = new ArrayList<>();
        List<TransferObject> parcelas = new ArrayList<>();
        try {
            List<String> sadAtivos = new ArrayList<>();
            sadAtivos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);
            sadAtivos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
            sadAtivos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
            parcelas = pesquisarConsignacaoController.pesquisarContratosComParcela(rseCodigo, sadAtivos, svcCodigos, csaCodigos, adeNumeroList, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica se os contratos são do mesmo servidor, e obtém os dados do servidor
        String rseMatricula = null;
        String serCpf = null;
        String serNome = null;
        try {
            if (TextHelper.isNull(rseCodigo)) {
                if (parcelas == null || parcelas.isEmpty()) {
                    throw new ServidorControllerException("mensagem.consultar.consignacao.erro.nenhum.registro", responsavel);
                }
                String rseCodigoAnterior = "";
                for (TransferObject ade : parcelas) {
                    rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();
                    if (!TextHelper.isNull(rseCodigoAnterior) && !rseCodigo.equals(rseCodigoAnterior)) {
                        // Lança erro ao usuário
                        throw new ServidorControllerException("mensagem.erro.multiplo.servidor.nao.permitido", responsavel);
                    }
                    rseCodigoAnterior = rseCodigo;
                }
            }

            if (parcelas != null && !parcelas.isEmpty()) {
                TransferObject servidor = parcelas.get(0);
                serNome = servidor.getAttribute(Columns.SER_NOME).toString();
                rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
            } else {
                // Busca os dados do servidor
                TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                serNome = servidor.getAttribute(Columns.SER_NOME).toString();
                rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
            }
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Cria a lista de contratos na ordem de pagamento da 1a. parcela
        for (int i = 0; i < parcelas.size(); i++) {
            TransferObject to = parcelas.get(i);
            Short prdNumero = (Short) to.getAttribute(Columns.PRD_NUMERO);
            if (prdNumero == 1) {
                ades.add(to);
            }
        }

        ExtratoConsolidadoServidor extractConsolidadeoSer = new ExtratoConsolidadoServidor();
        extractConsolidadeoSer.setAdes(ades);
        extractConsolidadeoSer.setConsignatarias(consignatarias);
        extractConsolidadeoSer.setCseNome(cseNome);
        extractConsolidadeoSer.setData(data);
        extractConsolidadeoSer.setParcelas(parcelas);
        extractConsolidadeoSer.setRseMatricula(rseMatricula);
        extractConsolidadeoSer.setSerCpf(serCpf);
        extractConsolidadeoSer.setSerNome(serNome);
        extractConsolidadeoSer.setServicos(servicos);
        extractConsolidadeoSer.setSvcCodigos(svcCodigos);

        model.addAttribute("extractConsolidadeoSer", extractConsolidadeoSer);

        return viewRedirect("jsp/editarServidor/listarExtratoConsolidado", request, session, model, responsavel);
    }
}
