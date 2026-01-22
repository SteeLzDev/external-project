package com.zetra.econsig.web.controller.convenio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: MantemConvenioWebController</p>
 * <p>Description: Controlador Web para edição de convênios.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24979 $
 * $Date: 2018-07-12 17:02:40 -0300 (qui, 12 jul 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/mantemConvenio" })
public class MantemConvenioWebController extends ControlePaginacaoWebController {

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        // Serviço (obrigatório)
        String svc_codigo = JspHelper.verificaVarQryStr(request, "svc_codigo");
        String svc_descricao = JspHelper.verificaVarQryStr(request, "svc_descricao");
        String svc_identificador = JspHelper.verificaVarQryStr(request, "svc_identificador");
        svc_descricao = (!svc_descricao.equals("") && !svc_identificador.equals("")) ? svc_identificador + " - " + svc_descricao : svc_identificador + svc_descricao;

        if (TextHelper.isNull(svc_codigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Consignatária ou Órgão
        String csa_codigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        String org_codigo = JspHelper.verificaVarQryStr(request, "org_codigo");
        String csa_nome = JspHelper.verificaVarQryStr(request, "csa_nome");
        String org_nome = JspHelper.verificaVarQryStr(request, "org_nome");

        boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
        boolean exigeMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CONVENIOS, responsavel);

        // Paramentro para saber se a coluna de valor de referencia deve ser exibida
        boolean utilizaCodVerbRef = ParamSist.getBoolParamSist(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, responsavel);
        // Paramentro para saber se a coluna de verba de férias ser exibida
        boolean temProcessamentoFerias = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, responsavel);
        // DESENV-9759: informando codVerba de benefícios
        boolean temModuloBeneficio = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, responsavel);
        // Parâmetro para exibir botão responsável por levar para o Rodapé da pagina
        boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        List<TransferObject> convenios = null;
        try {
            convenios = convenioController.getCnvScvCodigo(svc_codigo, csa_codigo, org_codigo, responsavel);
        } catch (ConvenioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            convenios = new ArrayList<>();
        }

        List<TransferObject> lstOcorrencias = null;
        String linkListagem = "../v3/mantemConvenio?acao=iniciar";
        try {
            int total = convenioController.countOcorrenciaConvenio(svc_codigo, csa_codigo, org_codigo, null, responsavel);
            if (total > 0) {
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                lstOcorrencias = convenioController.lstOcorrenciaConvenio(svc_codigo, csa_codigo, org_codigo, null, offset, size, responsavel);
                List<String> listParams = Arrays.asList(new String[] { "svc_codigo", "svc_descricao", "svc_identificador", "csa_codigo", "org_codigo", "csa_nome", "org_nome" });
                configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, listParams, false, request, model);
            }
        } catch (ConvenioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            lstOcorrencias = new ArrayList<>();
        }

        model.addAttribute("convenios", convenios);
        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("svc_descricao", svc_descricao);
        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("csa_nome", csa_nome);
        model.addAttribute("org_codigo", org_codigo);
        model.addAttribute("org_nome", org_nome);
        model.addAttribute("podeEditarCnv", podeEditarCnv);
        model.addAttribute("exigeMotivoOperacao", exigeMotivoOperacao);
        model.addAttribute("utilizaCodVerbRef", utilizaCodVerbRef);
        model.addAttribute("temProcessamentoFerias", temProcessamentoFerias);
        model.addAttribute("temModuloBeneficio", temModuloBeneficio);
        model.addAttribute("titulo", ApplicationResourcesHelper.getMessage("rotulo.convenio.manutencao.titulo", responsavel).toUpperCase());
        model.addAttribute("lstOcorrencias", lstOcorrencias);
        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

        return viewRedirect("jsp/manterConvenio/listarConvenios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);

        String operacao = JspHelper.verificaVarQryStr(request, "operacao");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "org_codigo");
        String svcCodigo = JspHelper.verificaVarQryStr(request, "svc_codigo");
        String svcDescricao = JspHelper.verificaVarQryStr(request, "svc_descricao");
        String tipoCodigo = JspHelper.verificaVarQryStr(request, "tipoCodigo");

        // DESENV-10880: habilita envio de e-mail para a CSA quando o codigo da verba do serviço for alterado.
        List<TransferObject> conveniosAnteriores = null;
        String lstEmailCadastrado = null;
        List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA);
        // Busca os emails cadastrados para este parametro de serviço.
        List<TransferObject> parametros;
        try {
            parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            if (parametros != null) {
                TransferObject paramSvcCsa;
                String codigoParametro, valorParametro;
                Iterator<TransferObject> itParametros = parametros.iterator();
                while (itParametros.hasNext()) {
                    paramSvcCsa = itParametros.next();
                    codigoParametro = (String) paramSvcCsa.getAttribute(Columns.TPS_CODIGO);
                    valorParametro = (String) paramSvcCsa.getAttribute(Columns.PSC_VLR);
                    if (codigoParametro.equals(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA)) {
                        lstEmailCadastrado = valorParametro;
                    }
                }
            }

        } catch (ParametroControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
        }

        if (lstEmailCadastrado != null && !lstEmailCadastrado.isEmpty()) {
            //Cria a lista de conveniosAnteriores antes de atualizar as informações
            try {
                conveniosAnteriores = convenioController.getCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, responsavel);
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                conveniosAnteriores = new ArrayList<>();
            }
        }

        // Motivo e observação da operação
        String tmoCodigo = null;
        String ocoObs = null;
        if (FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CONVENIOS, responsavel)) {
            tmoCodigo = request.getParameter("TMO_CODIGO");
            ocoObs = request.getParameter("ADE_OBS");
        }

        if ((TextHelper.isNull(csaCodigo) && TextHelper.isNull(orgCodigo)) || TextHelper.isNull(operacao) || TextHelper.isNull(svcCodigo) || !podeEditarCnv) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return iniciar(request, response, session, model);
        }

        List<List<String>> lstLstConvenios = new ArrayList<>();

        if (operacao.equals("inserir")) {
            try {
                if (responsavel.isOrg()) {
                    orgCodigo = responsavel.getCodigoEntidade();
                }
                List<TransferObject> codVerbaTO = convenioController.getCnvCodVerbaInativo(svcCodigo, csaCodigo, responsavel);
                String codverba = codVerbaTO.isEmpty() ? null : (String) codVerbaTO.get(0).getAttribute(Columns.CNV_COD_VERBA);
                lstLstConvenios.add(convenioController.createConvenio(svcCodigo, csaCodigo, orgCodigo, codverba, null, null, responsavel));
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.convenio.salvas.sucesso", responsavel));
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

        } else if (operacao.equals("bloquear")) {
            try {
                String status = JspHelper.verificaVarQryStr(request, "status");
                String valor = (status.equals("1") ? CodedValues.SCV_ATIVO : CodedValues.SCV_INATIVO);
                convenioController.setCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, valor, false, null, responsavel);
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

        } else if (operacao.equals("editar")) {
            try {
                // Verifica se o sistema permite código de verba duplicado
                boolean permiteRepetirCodVerba = ParamSist.paramEquals(CodedValues.TPC_PODE_REPETIR_COD_VERBA, CodedValues.TPC_SIM, responsavel);

                if (!TextHelper.isNull(csaCodigo)) {
                    // Busca parâmetro a nivel de CSA para determinar se a verba pode estar duplicada
                    try {
                        List<TransferObject> paramCsaList = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_PODE_REPETIR_COD_VERBA, responsavel);
                        if (!paramCsaList.isEmpty()) {
                            TransferObject paramCsa = paramCsaList.get(0);
                            String pcsVlr = (paramCsa != null ? (String) paramCsa.getAttribute(Columns.PCS_VLR) : null);
                            if (!TextHelper.isNull(pcsVlr)) {
                                permiteRepetirCodVerba = pcsVlr.equals("S");
                            }
                        }
                    } catch (ParametroControllerException e) {
                        session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                        return iniciar(request, response, session, model);
                    }
                }

                String[] codigos = null;
                if (!TextHelper.isNull(csaCodigo)) {
                    if (TextHelper.isNull(tipoCodigo)) {
                        codigos = request.getParameterValues("ORG_CODIGO");
                    } else if (tipoCodigo.equals("ORG_CODIGO")) {
                        codigos = request.getParameterValues("selecionarCheckBox");
                    }
                } else if (TextHelper.isNull(tipoCodigo)) {
                    codigos = request.getParameterValues("CSA_CODIGO");
                } else if (tipoCodigo.equals("CSA_CODIGO")) {
                    codigos = request.getParameterValues("selecionarCheckBox");
                }

                if (codigos != null) {
                    // Mensagem de erro caso existam códigos de verba duplicados
                    StringBuilder mensagemVerbaDuplicada = new StringBuilder();
                    // Verifica se algum código de verba entrado já é utilizado por outra consignatária
                    Set<String> codVerbaJaListados = new HashSet<>();

                    for (String codigo : codigos) {
                        String codVerba = request.getParameter("cv_" + codigo);

                        if (!TextHelper.isNull(codVerba) && !codVerbaJaListados.contains(codVerba)) {
                            if (!TextHelper.isNull(csaCodigo)) {
                                // Só adiciona o código de verba na lista dos já validados se estivermos na manutenção
                                // de convênios da CSA, porque se for na manutenção de convênios do ORG temos que validar
                                // cada combinação de verba e CSA
                                codVerbaJaListados.add(codVerba);
                            }

                            List<String> listCsas = convenioController.csaPorCodVerba(codVerba, !TextHelper.isNull(csaCodigo) ? csaCodigo : codigo);
                            if (listCsas != null && !listCsas.isEmpty()) {
                                mensagemVerbaDuplicada.append(ApplicationResourcesHelper.getMessage("mensagem.convenio.uso.codigo.verba", responsavel, codVerba));
                                mensagemVerbaDuplicada.append(" ").append(TextHelper.join(listCsas, ", ")).append("<br>");
                            }
                        }
                    }

                    // Verfica se uma verba de férias entrada não é utilizada como codigo de verba
                    if (ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, responsavel)) {
                        Set<String> codVerbaFeriasJaListados = new HashSet<>();
                        for (String codigo : codigos) {
                            String codVerbaFerias = request.getParameter("ferias_" + codigo);

                            if (!TextHelper.isNull(codVerbaFerias) && !codVerbaFeriasJaListados.contains(codVerbaFerias)) {
                                if (!TextHelper.isNull(csaCodigo)) {
                                    codVerbaFeriasJaListados.add(codVerbaFerias);
                                }

                                List<String> listCsas = convenioController.csaPorCodVerba(codVerbaFerias, !TextHelper.isNull(csaCodigo) ? csaCodigo : codigo);
                                if (listCsas != null && !listCsas.isEmpty()) {
                                    mensagemVerbaDuplicada.append(ApplicationResourcesHelper.getMessage("mensagem.convenio.uso.codigo.verba.ferias", responsavel, codVerbaFerias));
                                    mensagemVerbaDuplicada.append(" ").append(TextHelper.join(listCsas, ", ")).append("<br>");
                                }
                            }
                        }
                    }

                    if (!mensagemVerbaDuplicada.isEmpty()) {
                        if (!permiteRepetirCodVerba) {
                            // Se não permite repetir verba, então verifica se já está sendo utilizada, e caso positivo, retorna erro
                            session.setAttribute(CodedValues.MSG_ERRO, mensagemVerbaDuplicada.toString());
                            return iniciar(request, response, session, model);
                        } else {
                            // Se pode repetir, emite mensagem apenas de alerta
                            session.setAttribute(CodedValues.MSG_ALERT, mensagemVerbaDuplicada.toString());
                        }
                    }
                }

                // Cria os convênios especificados
                List<TransferObject> todosConvenios = new ArrayList<>();

                if (!TextHelper.isNull(csaCodigo)) {
                    if (codigos != null) {
                        for (String codigo : codigos) {
                            CustomTransferObject cnvTO = new CustomTransferObject();
                            cnvTO.setAttribute(Columns.ORG_CODIGO, codigo);
                            cnvTO.setAttribute(Columns.ORG_NOME, "org_" + codigo);
                            cnvTO.setAttribute(Columns.CNV_CODIGO, request.getParameter("cnv_codigo_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA, request.getParameter("cv_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_REF, request.getParameter("ref_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_FERIAS, request.getParameter("ferias_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_DIRF, request.getParameter("dirf_" + codigo));
                            cnvTO.setAttribute("NOVO_STATUS", CodedValues.SCV_ATIVO);
                            todosConvenios.add(cnvTO);
                        }
                    }

                    List<TransferObject> orgsABloquear = null;
                    if (!todosConvenios.isEmpty()) {
                        orgsABloquear = convenioController.lstCodEntidadesCnvNotInList(csaCodigo, orgCodigo, svcCodigo, Arrays.asList(codigos), responsavel);
                    } else {
                        orgsABloquear = convenioController.lstCodEntidadesCnvNotInList(csaCodigo, orgCodigo, svcCodigo, null, responsavel);
                    }
                    for (TransferObject orgao : orgsABloquear) {
                        String orgCodigoAux = (String) orgao.getAttribute(Columns.ORG_CODIGO);

                        orgao.setAttribute("NOVO_STATUS", CodedValues.SCV_INATIVO);
                        orgao.setAttribute(Columns.CNV_CODIGO, request.getParameter("cnv_codigo_" + orgCodigoAux));
                        orgao.setAttribute(Columns.CNV_COD_VERBA, request.getParameter("cv_" + orgCodigoAux));
                        orgao.setAttribute(Columns.CNV_COD_VERBA_REF, request.getParameter("ref_" + orgCodigoAux));
                        orgao.setAttribute(Columns.CNV_COD_VERBA_FERIAS, request.getParameter("ferias_" + orgCodigoAux));
                        orgao.setAttribute(Columns.CNV_COD_VERBA_DIRF, request.getParameter("dirf_" + orgCodigoAux));
                        todosConvenios.add(orgao);
                    }

                    // Cria os convênios selecionados.
                    lstLstConvenios = convenioController.createConvenios(svcCodigo, csaCodigo, orgCodigo, todosConvenios, tmoCodigo, ocoObs, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.convenio.salvas.sucesso", responsavel));

                } else if (TextHelper.isNull(csaCodigo)) {
                    if (codigos != null) {
                        for (String codigo : codigos) {
                            CustomTransferObject cnvTO = new CustomTransferObject();
                            cnvTO.setAttribute(Columns.CSA_CODIGO, codigo);
                            cnvTO.setAttribute(Columns.CSA_NOME, "csa_" + codigo);
                            cnvTO.setAttribute(Columns.CNV_CODIGO, request.getParameter("cnv_codigo_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA, request.getParameter("cv_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_REF, request.getParameter("ref_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_FERIAS, request.getParameter("ferias_" + codigo));
                            cnvTO.setAttribute(Columns.CNV_COD_VERBA_DIRF, request.getParameter("dirf_" + codigo));
                            cnvTO.setAttribute("NOVO_STATUS", CodedValues.SCV_ATIVO);
                            todosConvenios.add(cnvTO);
                        }
                    }
                    List<TransferObject> csasABloquear = null;
                    if (!todosConvenios.isEmpty()) {
                        csasABloquear = convenioController.lstCodEntidadesCnvNotInList(csaCodigo, orgCodigo, svcCodigo, Arrays.asList(codigos), responsavel);
                    } else {
                        csasABloquear = convenioController.lstCodEntidadesCnvNotInList(csaCodigo, orgCodigo, svcCodigo, null, responsavel);
                    }
                    for (TransferObject consignataria : csasABloquear) {
                        String csaCodigoAux = (String) consignataria.getAttribute(Columns.CSA_CODIGO);

                        consignataria.setAttribute("NOVO_STATUS", CodedValues.SCV_INATIVO);
                        consignataria.setAttribute(Columns.CNV_CODIGO, request.getParameter("cnv_codigo_" + csaCodigoAux));
                        consignataria.setAttribute(Columns.CNV_COD_VERBA, request.getParameter("cv_" + csaCodigoAux));
                        consignataria.setAttribute(Columns.CNV_COD_VERBA_REF, request.getParameter("ref_" + csaCodigoAux));
                        consignataria.setAttribute(Columns.CNV_COD_VERBA_FERIAS, request.getParameter("ferias_" + csaCodigoAux));
                        consignataria.setAttribute(Columns.CNV_COD_VERBA_DIRF, request.getParameter("dirf_" + csaCodigoAux));
                        todosConvenios.add(consignataria);
                    }

                    // Cria os convênios selecionados
                    lstLstConvenios = convenioController.createConvenios(svcCodigo, csaCodigo, orgCodigo, todosConvenios, tmoCodigo, ocoObs, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.convenio.salvas.sucesso", responsavel));
                }
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return iniciar(request, response, session, model);
        }

        if (!lstLstConvenios.isEmpty()) {
            List<String> cnv = new ArrayList<>();
            for (List<String> lstConvenios : lstLstConvenios) {
                for (String convenio : lstConvenios) {
                    cnv.add(convenio);
                }
            }
            try {
                convenioController.setParamQuantidadeDefault(cnv);
            } catch (ConvenioControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return iniciar(request, response, session, model);
            }
        }

        //se o parametro de servico 274 estiver habilitado, envia e-mail para a CSA: SE ela tiver cadastrado o e-mail, para o recebimento desta notificação
        if (lstEmailCadastrado != null && !lstEmailCadastrado.isEmpty()) {
            try {
                List<TransferObject> conveniosAtuais = null;
                try {
                    conveniosAtuais = convenioController.getCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, responsavel);
                } catch (ConvenioControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    conveniosAtuais = new ArrayList<>();
                }
                EnviaEmailHelper.enviaNotifAlterCodVerbaConvCSA(responsavel, lstEmailCadastrado, conveniosAnteriores, conveniosAtuais, svcCodigo, svcDescricao);
            } catch (ViewHelperException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            }
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=edtPrioridadeCnv" })
    public String editarPrioridadeCnv(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
        boolean podeConsultarCnv = responsavel.temPermissao(CodedValues.FUN_CONS_CONVENIOS);

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        if (svcCodigo == null || svcCodigo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.convenio.informar.servico", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String linkRetorno = JspHelper.verificaVarQryStr(request, "linkRetorno");

        ServicoTransferObject servico = null;
        List<TransferObject> lstConvenio = null;
        try {
            servico = convenioController.findServico(svcCodigo, responsavel);

            int total = convenioController.countCnvBySvcCodigo(svcCodigo, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            lstConvenio = convenioController.lstCnvBySvcCodigo(svcCodigo, null, offset, size, responsavel);

            String linkListagem = "../v3/mantemConvenio?acao=edtPrioridadeCnv";
            List<String> listParams = Arrays.asList(new String[] { "SVC_CODIGO" });
            configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, listParams, false, request, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("servico", servico);
        model.addAttribute("SVC_CODIGO", svcCodigo);
        model.addAttribute("lstConvenio", lstConvenio);
        model.addAttribute("podeEditarCnv", podeEditarCnv);
        model.addAttribute("podeConsultarCnv", podeConsultarCnv);
        model.addAttribute("linkRetorno", linkRetorno);

        return viewRedirect("jsp/manterConvenio/editarPrioridadeConvenios", request, session, model, responsavel);
    }

}
