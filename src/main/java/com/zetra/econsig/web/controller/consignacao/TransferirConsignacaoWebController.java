package com.zetra.econsig.web.controller.consignacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TransferirConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Transferir consignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$ $Revision$ $Date: 2018-06-14 14:06:41 -0300
 * (Qui, 14 jun 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/transferirConsignacao" })
public class TransferirConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TransferirConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private TransferirConsignacaoController transferirConsignacaoController;

    @Override
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if ((!responsavel.isCseSupOrg() && !responsavel.isCsa()) || (!responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Quantidade mínima de dígitos da matrícula a ser informado
            int tamanhoMatricula = 0;
            // Quantidade máxima de dígitos da matrícula a ser informado
            int tamMaxMatricula = 0;
            try {
                Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
                tamanhoMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
                param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel);
                tamMaxMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
            } catch (Exception ex) {
            }

            // Máscara do campo de matrícula
            String maskMatricula = "#*20";
            Object matriculaNumerica = ParamSist.getInstance().getParam(CodedValues.TPC_MATRICULA_NUMERICA, responsavel);
            if ((matriculaNumerica != null) && (matriculaNumerica.equals("S"))) {
                maskMatricula = "#D20";
            }

            model.addAttribute("maskMatricula", maskMatricula);
            model.addAttribute("tamMaxMatricula", tamMaxMatricula);
            model.addAttribute("tamanhoMatricula", tamanhoMatricula);
            model.addAttribute("maskMatricula", maskMatricula);

            return viewRedirect("jsp/transferirConsignacao/transferirConsignacao", request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if ((!responsavel.isCseSupOrg() && !responsavel.isCsa()) || (!responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseMatriculaOri = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA_ORI");
            String rseMatriculaDes = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA_DES");

            List<TransferObject> servidoresOri = new ArrayList<>();
            List<TransferObject> servidoresDes = new ArrayList<>();

            if (!TextHelper.isNull(rseMatriculaOri) && !TextHelper.isNull(rseMatriculaDes)) {
                // Define qual entidade está fazendo a pesquisa de servidor
                String tipoEntidade = responsavel.getTipoEntidade();
                String codigo = responsavel.getCodigoEntidade();

                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    tipoEntidade = "EST";
                    codigo = responsavel.getCodigoEntidadePai();
                }

                try {
                    servidoresOri = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigo, null, null, rseMatriculaOri, null, responsavel, false, null, false);
                    servidoresDes = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigo, null, null, rseMatriculaDes, null, responsavel, false, null, false);
                } catch (ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }

            model.addAttribute("rseMatriculaOri", rseMatriculaOri);
            model.addAttribute("rseMatriculaDes", rseMatriculaDes);
            model.addAttribute("servidoresOri", servidoresOri);
            model.addAttribute("servidoresDes", servidoresDes);

            return iniciar(request, response, session, model);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=listarConsignacoes" })
    public String listarConsignacoes(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if ((!responsavel.isCseSupOrg() && !responsavel.isCsa()) || !responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseCodigoOri = JspHelper.verificaVarQryStr(request, "RSE_CODIGO_ORI");
            String rseCodigoDes = JspHelper.verificaVarQryStr(request, "RSE_CODIGO_DES");
            String adeNumero    = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
            List<String> adeNumeros = null;
            if (request.getParameterValues("ADE_NUMERO_LIST") != null) {
                adeNumeros = Arrays.asList(request.getParameterValues("ADE_NUMERO_LIST"));
            } else {
                adeNumeros = new ArrayList<>();
            }

            List<String> sadCodigos = null;

            if (request.getParameterValues("SAD_CODIGO") != null) {
                sadCodigos = Arrays.asList(request.getParameterValues("SAD_CODIGO"));
            } else {
                sadCodigos = new ArrayList<>();
            }

            if (TextHelper.isNull(rseCodigoOri) || TextHelper.isNull(rseCodigoDes)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.origem.destino", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else if (rseCodigoOri.equals(rseCodigoDes)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.origem.destino.distintos", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject servidorOri = null;
            CustomTransferObject servidorDes = null;
            String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.transferirAdes", responsavel);
            try {
                servidorOri = pesquisarServidorController.buscaServidor(rseCodigoOri, responsavel);
                servidorDes = pesquisarServidorController.buscaServidor(rseCodigoDes, responsavel);

                if (servidorOri != null && servidorDes != null) {
                    String serCpfOri = (String) servidorOri.getAttribute(Columns.SER_CPF);
                    String serCpfDes = (String) servidorDes.getAttribute(Columns.SER_CPF);
                    if (!TextHelper.isNull(serCpfOri) && !TextHelper.isNull(serCpfDes) && !serCpfOri.equals(serCpfDes)) {
                        if (responsavel.isCsa()) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.aviso.servidores.cpf.diferentes", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.servidores.cpf.diferentes", responsavel));
                        msgConfirmacao = msgConfirmacao + " " + ApplicationResourcesHelper.getMessage("mensagem.aviso.servidores.cpf.diferentes", responsavel);
                    }
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.obter.servidores.transferir.consignacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } catch (ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Salva um novo token
            SynchronizerToken.saveToken(request);

            int total = 0;
            List<TransferObject> ades = new ArrayList<>();
            try {
                // Define qual entidade está fazendo a pesquisa de servidor
                String tipoEntidade = responsavel.getTipoEntidade();
                String codigo = responsavel.getCodigoEntidade();

                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    tipoEntidade = "EST";
                    codigo = responsavel.getCodigoEntidadePai();
                } else if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    tipoEntidade = "CSA";
                    codigo = responsavel.getCodigoEntidadePai();
                }


                ArrayList<String>  adeList = new ArrayList<>();
                if (!TextHelper.isNull(adeNumero)) {
                    adeList.add(adeNumero);
                }
                if (!TextHelper.isNull(adeNumeros)) {
                    adeList.addAll(adeNumeros);
                }

                ArrayList<String> sadCodigoList = new ArrayList<>();
                if (!TextHelper.isNull(sadCodigos)) {
                    sadCodigoList.addAll(sadCodigos);
                }


                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("TIPO_OPERACAO", "consultar");
                criterio.setAttribute("transferencia", true);
                TransferObject criteriosPesqAvancada = recuperarCriteriosPesquisaAvancada(request, responsavel);
                if (criteriosPesqAvancada != null) {
                criterio.setAtributos(criteriosPesqAvancada.getAtributos());
                }

                total = pesquisarConsignacaoController.countPesquisaAutorizacao(tipoEntidade, codigo, rseCodigoOri, adeList, null, sadCodigoList, new ArrayList<String>(), criterio, responsavel);
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }
                ades = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigo, rseCodigoOri, adeList, null, sadCodigoList, new ArrayList<String>(), offset, size, criterio, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            parameterMap.remove("offset");
            parameterMap.remove("back");

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");

            List<String> requestParams = new ArrayList<>(params);

            String linkListagem = "../v3/transferirConsignacao";
            configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.integracao", total, JspHelper.LIMITE, requestParams, false, request, model);

            model.addAttribute("rseCodigoOri", rseCodigoOri);
            model.addAttribute("rseCodigoDes", rseCodigoDes);
            model.addAttribute("servidorOri", servidorOri);
            model.addAttribute("servidorDes", servidorDes);
            model.addAttribute("ades", ades);
            model.addAttribute("msgConfirmacao", msgConfirmacao);

            return viewRedirect("jsp/transferirConsignacao/listarConsignacoes", request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServidorControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String urlDestino = "../v3/transferirConsignacao?acao=transferir";
            String[] adeCodigoArray = request.getParameterValues("chkAdeCodigo");

            boolean transferirTodos = "S".equals(request.getParameter("transferirTodos"));

            List<String> adeCodigoList = null;
            if (transferirTodos) {
                // Se deve transferir todos, recupera os ADE_NUMEROs informados na pesquisa
                adeCodigoList = request.getParameterValues("ADE_CODIGO_NUMBER") != null ? Arrays.asList(request.getParameterValues("ADE_CODIGO_NUMBER")) : null;
            } else {
                // Se deve transferir os selecionados, recupera os ADE_NUMEROs selecionados
                adeCodigoList = Arrays.asList(adeCodigoArray);
            }

            if (adeCodigoList == null) {
                adeCodigoList = new ArrayList<>();
            }

            if (!transferirTodos && adeCodigoList.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.nenhum.registro.selecionado", responsavel));
                return iniciar(request, response, session, model);
            }

            String funCodigo = CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL;
            if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
                // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
                return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
            } else {
                boolean exigeSenhaServidor = (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSA, CodedValues.TPC_SIM, responsavel)) ||
                        (responsavel.isCseSupOrg() &&  ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSE, CodedValues.TPC_SIM, responsavel));

                model.addAttribute("exigeSenhaServidor", exigeSenhaServidor);
                model.addAttribute("rseCodigo", JspHelper.verificaVarQryStr(request, "RSE_CODIGO_DES"));

                return informarMotivoOperacao(CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL, urlDestino, adeCodigoList.toArray(new String[0]), request, response, session, model);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.transferir.consignacao.titulo", responsavel));
        model.addAttribute("nomeCampo", "chkAdeCodigo");
    }

    @RequestMapping(params = { "acao=transferir" })
    public String transferir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServidorControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if ((!responsavel.isCseSupOrg() && !responsavel.isCsa()) || (!responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_CONSIGNACAO_PARCIAL))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseCodigoOri = JspHelper.verificaVarQryStr(request, "RSE_CODIGO_ORI");
            String rseCodigoDes = JspHelper.verificaVarQryStr(request, "RSE_CODIGO_DES");

            CustomTransferObject servidorOri = pesquisarServidorController.buscaServidor(rseCodigoOri, responsavel);

            if (servidorOri != null) {
                String[] adeCodigoArray = request.getParameterValues("chkAdeCodigo");
                if (adeCodigoArray != null && adeCodigoArray.length > 0) {
                    boolean comSenhaServidor = false;
                    try {
                        boolean exigeSenhaServidor = (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSA, CodedValues.TPC_SIM, responsavel)) ||
                                (responsavel.isCseSupOrg() &&  ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSE, CodedValues.TPC_SIM, responsavel));

                        if (exigeSenhaServidor) {
                            SenhaHelper.validarSenha(request, rseCodigoDes, null, true, true, responsavel);
                            comSenhaServidor = true;
                        }
                    } catch (ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    List<String> adeCodigoList = Arrays.asList(adeCodigoArray);
                    String rseMatriculaOri = (String) servidorOri.getAttribute(Columns.RSE_MATRICULA);
                    String orgIdentificadorOri = (String) servidorOri.getAttribute(Columns.ORG_IDENTIFICADOR);

                    try {
                        // envia o motivo de transferência, se houver
                        CustomTransferObject tipoMotivoOperacao = null;
                        if (request.getParameter("TMO_CODIGO") != null) {
                            tipoMotivoOperacao = new CustomTransferObject();
                            tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                            tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                        }

                        // Transfere os contratos entre servidores
                        boolean possuiRelacionamento = transferirConsignacaoController.transfereAdeServidores(adeCodigoList, rseCodigoOri, rseCodigoDes, rseMatriculaOri, orgIdentificadorOri, tipoMotivoOperacao, comSenhaServidor, responsavel);

                        String tipoEntidade = "RSE";
                        List<String> codigoEntidades = new ArrayList<>();
                        codigoEntidades.add(rseCodigoOri);
                        codigoEntidades.add(rseCodigoDes);

                        // Recalcula a Margem dos servidores. Esse recalculo deveria ficar dentro de
                        // transfereAdeServidores,
                        // mas la ele nao estava enxergando o ultimo contrato transferido, causando
                        // inconsistencia.
                        margemController.recalculaMargemComHistorico(tipoEntidade, codigoEntidades, responsavel);

                        String mensagem = ApplicationResourcesHelper.getMessage("mensagem.transferir.consignacao.sucesso", responsavel);
                        if (possuiRelacionamento) {
                            mensagem = mensagem + "<BR>" + ApplicationResourcesHelper.getMessage("mensagem.transferir.consignacao.relacionamentos.transferidos", responsavel);
                        }

                        // Seta mensagem de sucesso na sessão do usuário
                        session.setAttribute(CodedValues.MSG_INFO, mensagem);

                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            for (String element : adeCodigoArray) {
                                autorizacaoController.criaOcorrenciaADE(element, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                            }
                            session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                            session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                        }

                    } catch (AutorizacaoControllerException | MargemControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.um.contrato.transferencia", responsavel));
                }
            }

            return listarConsignacoes(request, response, session, model);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
    @Override
    protected TransferObject recuperarCriteriosPesquisaAvancada(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();

        try {
            String adeAnoMesIni = JspHelper.verificaVarQryStr(request, "ADE_ANO_MES_INI");
            if (!adeAnoMesIni.equals("")) {
                adeAnoMesIni = DateHelper.format(DateHelper.parsePeriodString(adeAnoMesIni), "yyyy-MM-dd");
                criterio.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
            }

            String tipoOcorrenciaPeriodo = JspHelper.verificaVarQryStr(request, "tipoOcorrenciaPeriodo");
            if (!tipoOcorrenciaPeriodo.equals("")) {
                criterio.setAttribute("tipoOcorrenciaPeriodo", tipoOcorrenciaPeriodo);
            }

            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("")) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
            String adeIntFolha = JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA");
            if (!adeIntFolha.equals("")) {
                criterio.setAttribute(Columns.ADE_INT_FOLHA, adeIntFolha);
            }
            String adeIncMargem = JspHelper.verificaVarQryStr(request, "ADE_INC_MARGEM");
            if (!adeIncMargem.equals("")) {
                criterio.setAttribute(Columns.ADE_INC_MARGEM, adeIncMargem);
            }
            String adeIndice = JspHelper.verificaVarQryStr(request, "ADE_INDICE");
            if (!adeIndice.equals("")) {
                criterio.setAttribute(Columns.ADE_INDICE, adeIndice);
            }
            String[] srsCodigo = request.getParameterValues("SRS_CODIGO");
            List<String> rseSrsCodigo = new ArrayList<>();
            if (srsCodigo != null) {
                for (String element : srsCodigo) {
                    String[] aux = element.split(";");
                    rseSrsCodigo.add(aux[0]);
                }
                if (rseSrsCodigo.size() > 0) {
                    criterio.setAttribute(Columns.RSE_SRS_CODIGO, rseSrsCodigo);
                }
            }

            criterio.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
            criterio.setAttribute(Columns.TGC_CODIGO, JspHelper.verificaVarQryStr(request, "TGC_CODIGO"));
            criterio.setAttribute(Columns.CSA_CODIGO, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
            criterio.setAttribute(Columns.COR_CODIGO, JspHelper.verificaVarQryStr(request, "COR_CODIGO"));
            criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            criterio.setAttribute(Columns.TGS_CODIGO, JspHelper.verificaVarQryStr(request, "TGS_CODIGO"));
            criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
            criterio.setAttribute(Columns.CNV_COD_VERBA, JspHelper.verificaVarQryStr(request, "CNV_COD_VERBA"));
            criterio.setAttribute(Columns.SER_CPF, JspHelper.verificaVarQryStr(request, "SER_CPF"));
            criterio.setAttribute(Columns.RSE_MATRICULA, JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"));

            if (request.getParameter("infSaldoDevedor") != null) {
                // Busca pela informação de saldo devedor
                criterio.setAttribute("infSaldoDevedor", request.getParameter("infSaldoDevedor"));
            }

            criterio.setAttribute("TIPO_ORDENACAO", request.getParameter("tipoOrdenacao"));
            if (request.getParameter("ORDENACAO_AUX") != null) {
                criterio.setAttribute("ORDENACAO", request.getParameter("ORDENACAO_AUX"));
            }

            if (responsavel.isCsaCor() && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "adePropria"))) {
                criterio.setAttribute("adePropria", true);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }
}
