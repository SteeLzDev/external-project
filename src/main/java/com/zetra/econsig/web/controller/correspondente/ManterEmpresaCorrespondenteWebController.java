package com.zetra.econsig.web.controller.correspondente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p> * Title: ManterCorrespondenteWebController * </p>
 * <p> * Description: Controlador Web para manter corresposndente. * </p>
 * <p> * Copyright: Copyright (c) 2002-2017 * </p>
 * <p> * Company: ZetraSoft * </p>
 * $Author: igor.lucas $
 * $Revision: 30120 $
 * $Date: 2020-08-12 14:15:47 -0300 (qua, 12 ago 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterEmpresaCorrespondente" })
public class ManterEmpresaCorrespondenteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterCorrespondenteWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean cadastroEmpresaCorrespondente = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);
        boolean podeConsultar = responsavel.temPermissao(CodedValues.FUN_CONS_EMPRESA_CORRESPONDENTE);
        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_EMPRESA_CORRESPONDENTE);

        if (!cadastroEmpresaCorrespondente || (!podeConsultar && !podeEditar)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.lista.empresa.correspondente.titulo", responsavel);

        List<?> empresas = null;

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
          filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // Bloqueado
            if (filtro_tipo == 0) {
              criterio.setAttribute(Columns.ECO_ATIVO, CodedValues.STS_INATIVO);
            // Desbloqueado
            } else if (filtro_tipo == 1) {
              criterio.setAttribute(Columns.ECO_ATIVO, CodedValues.STS_ATIVO);
            // Outros
            } else if (!filtro.equals("") && filtro_tipo != -1) {
              String campo = null;
              switch (filtro_tipo) {
                case 2: campo = Columns.ECO_IDENTIFICADOR; break;
                case 3: campo = Columns.ECO_NOME; break;
                case 4: campo = Columns.ECO_CNPJ; break;
                default:
                  session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                  return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
              }
              criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);

            }

            if (!filtro2.isEmpty()) {
                criterio.setAttribute(Columns.ECO_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
            }

            int total = consignatariaController.countEmpresaCorrespondente(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
              offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {}

            empresas = consignatariaController.lstEmpresaCorrespondente(criterio, offset, size, responsavel);

            String link = "../v3/manterEmpresaCorrespondente?acao=iniciar&FILTRO=" + filtro + "&FILTRO_TIPO=" + filtro_tipo;
            configurarPaginador(link, "rotulo.paginacao.titulo.consignataria", total, size, null, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            empresas = new ArrayList<>();
        }

        model.addAttribute("titulo", titulo);
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro2", filtro2);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("podeConsultar", podeConsultar);
        model.addAttribute("empresas", empresas);

        return viewRedirect("jsp/manterEmpresaCorrespondente/listarEmpresaCorrespondente", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=modificaEmpresa" })
    public String modificaEmpresa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String operacao = request.getParameter("operacao");
        String ecoCodigo  = request.getParameter("ECO_CODIGO");

        if (operacao != null && ecoCodigo != null) {

            if (operacao.equals("excluir")) {
              // Exclui a Empresa Correspondente
              try {
                consignatariaController.removeEmpresaCorrespondente(ecoCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.excluido.sucesso", responsavel));
              } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
              }
            } else if (operacao.equals("bloquear")) {
              // Bloqueia a Empresa Correspondente e seus correspondentes associados
              try {
                String ativo = request.getParameter("ECO_ATIVO");
                boolean telaEdicao = TextHelper.isNull(request.getParameter("telaEdicao")) ? false : true;
                ativo = (ativo != null && ativo.equals("1")) ? "0" : "1";
                CustomTransferObject to = new CustomTransferObject();
                to.setAttribute(Columns.ECO_CODIGO, ecoCodigo);
                to.setAttribute(Columns.ECO_ATIVO, Short.valueOf(ativo));

                consignatariaController.updateEmpresaCorrespondente(to, responsavel);
                if (ativo.equals("0")) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.bloqueado.sucesso", responsavel));
                } else {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.desbloqueado.sucesso", responsavel));
                }
                if(telaEdicao) {
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    return detalhar(request, response, session, model);
                }
              } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
              }
            }
          }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);
        boolean cadastroEmpresaCorrespondente = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);
        boolean podeConsultar = responsavel.temPermissao(CodedValues.FUN_CONS_EMPRESA_CORRESPONDENTE);
        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_EMPRESA_CORRESPONDENTE);

        if (!cadastroEmpresaCorrespondente || (!podeConsultar && !podeEditar)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.manutencao.empresa.correspondente.titulo", responsavel);

        String eco_codigo = null;
        if (request.getParameter("ECO_CODIGO") != null) {
            eco_codigo = JspHelper.verificaVarQryStr(request, "ECO_CODIGO");
        }

        String reqColumnsStr = "ECO_IDENTIFICADOR|ECO_NOME|ECO_CNPJ";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        if (request.getParameter("MM_update") != null && msgErro.length() == 0) {

            CustomTransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.ECO_NOME, JspHelper.verificaVarQryStr(request, "ECO_NOME"));
            to.setAttribute(Columns.ECO_CNPJ, JspHelper.verificaVarQryStr(request, "ECO_CNPJ"));
            to.setAttribute(Columns.ECO_EMAIL, JspHelper.verificaVarQryStr(request, "ECO_EMAIL"));
            to.setAttribute(Columns.ECO_RESPONSAVEL, JspHelper.verificaVarQryStr(request, "ECO_RESPONSAVEL"));
            to.setAttribute(Columns.ECO_RESPONSAVEL_2, JspHelper.verificaVarQryStr(request, "ECO_RESPONSAVEL_2"));
            to.setAttribute(Columns.ECO_RESPONSAVEL_3, JspHelper.verificaVarQryStr(request, "ECO_RESPONSAVEL_3"));
            to.setAttribute(Columns.ECO_RESP_CARGO, JspHelper.verificaVarQryStr(request, "ECO_RESP_CARGO"));
            to.setAttribute(Columns.ECO_RESP_CARGO_2, JspHelper.verificaVarQryStr(request, "ECO_RESP_CARGO_2"));
            to.setAttribute(Columns.ECO_RESP_CARGO_3, JspHelper.verificaVarQryStr(request, "ECO_RESP_CARGO_3"));
            to.setAttribute(Columns.ECO_RESP_TELEFONE, JspHelper.verificaVarQryStr(request, "ECO_RESP_TELEFONE"));
            to.setAttribute(Columns.ECO_RESP_TELEFONE_2, JspHelper.verificaVarQryStr(request, "ECO_RESP_TELEFONE_2"));
            to.setAttribute(Columns.ECO_RESP_TELEFONE_3, JspHelper.verificaVarQryStr(request, "ECO_RESP_TELEFONE_3"));
            to.setAttribute(Columns.ECO_LOGRADOURO, JspHelper.verificaVarQryStr(request, "ECO_LOGRADOURO"));
            if (!JspHelper.verificaVarQryStr(request, "ECO_NRO").equals("")) {
                to.setAttribute(Columns.ECO_NRO, Integer.valueOf(JspHelper.verificaVarQryStr(request, "ECO_NRO")));
            }
            to.setAttribute(Columns.ECO_COMPL, JspHelper.verificaVarQryStr(request, "ECO_COMPL"));
            to.setAttribute(Columns.ECO_BAIRRO, JspHelper.verificaVarQryStr(request, "ECO_BAIRRO"));
            to.setAttribute(Columns.ECO_CIDADE, JspHelper.verificaVarQryStr(request, "ECO_CIDADE"));
            to.setAttribute(Columns.ECO_UF, JspHelper.verificaVarQryStr(request, "ECO_UF"));
            to.setAttribute(Columns.ECO_CEP, JspHelper.verificaVarQryStr(request, "ECO_CEP"));
            to.setAttribute(Columns.ECO_TEL, JspHelper.verificaVarQryStr(request, "ECO_TEL"));
            to.setAttribute(Columns.ECO_FAX, JspHelper.verificaVarQryStr(request, "ECO_FAX"));
            to.setAttribute(Columns.ECO_IDENTIFICADOR, JspHelper.verificaVarQryStr(request, "ECO_IDENTIFICADOR"));

            if (eco_codigo != null) {
                to.setAttribute(Columns.ECO_CODIGO, eco_codigo);
                try {
                    consignatariaController.updateEmpresaCorrespondente(to, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.alterado.sucesso", responsavel));
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            } else {
                to.setAttribute(Columns.ECO_ATIVO, CodedValues.STS_ATIVO);
                try {
                    eco_codigo = consignatariaController.createEmpresaCorrespondente(to, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.criado.sucesso", responsavel));

                    // Colocando um endereço no paramSession
                    Map<String, String[]> parametros = new HashMap<>();
                    parametros.put("tipo", new String[]{"consultar"});
                    parametros.put("ECO_CODIGO", new String[]{eco_codigo});
                    String link = request.getRequestURI();
                    paramSession.addHistory(link, parametros);

                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        TransferObject empresa = null;
        String eco_identificador = null;
        String eco_nome = null;
        String eco_ativo = null;
        try {
          if (eco_codigo != null) {
              CustomTransferObject criterio = new CustomTransferObject();
              criterio.setAttribute(Columns.ECO_CODIGO, eco_codigo);

              empresa = consignatariaController.findEmpresaCorrespondente(criterio, responsavel);
              eco_identificador = (String) empresa.getAttribute(Columns.ECO_IDENTIFICADOR);
              eco_nome = (String) empresa.getAttribute(Columns.ECO_NOME);
              eco_ativo = empresa.getAttribute(Columns.ECO_ATIVO) != null? empresa.getAttribute(Columns.ECO_ATIVO).toString(): "";
          }
        } catch (Exception ex) {
          LOG.error(ex.getMessage(), ex);
          session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
          return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("eco_codigo", eco_codigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("empresa", empresa);
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("eco_ativo", eco_ativo);
        model.addAttribute("eco_nome", eco_nome);
        model.addAttribute("eco_identificador", eco_identificador);
        model.addAttribute("msgErro", msgErro);

        return viewRedirect("jsp/manterEmpresaCorrespondente/editarEmpresaCorrespondente", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=detalhar" })
    public String detalhar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return editar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=detalharAssociacao" })
    public String detalharAssociacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
       return associar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=associar" })
    public String associar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        boolean cadastroEmpresaCorrespondente = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);
        boolean podeConsultar = responsavel.temPermissao(CodedValues.FUN_CONS_EMPRESA_CORRESPONDENTE);
        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_EMPRESA_CORRESPONDENTE);
        boolean podeConsultarCor = responsavel.temPermissao(CodedValues.FUN_CONS_CORRESPONDENTES);
        boolean podeEditarCor = responsavel.temPermissao(responsavel.isCor() ? CodedValues.FUN_EDT_CORRESPONDENTE : CodedValues.FUN_EDT_CORRESPONDENTES);

        if (!cadastroEmpresaCorrespondente || (!podeConsultar && !podeEditar)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.associa.empresa.correspondente.titulo", responsavel);

        String eco_codigo = null;
        if (!TextHelper.isNull(request.getParameter("ECO_CODIGO"))) {
            eco_codigo = JspHelper.verificaVarQryStr(request, "ECO_CODIGO");
        }

        String csa_codigo = null;
        if (!TextHelper.isNull(request.getParameter("CSA_CODIGO"))) {
            csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        }

        String cor_codigo = null;
        if (!TextHelper.isNull(request.getParameter("COR_CODIGO"))) {
            cor_codigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
        }

        TransferObject empresa = null;
        String eco_identificador = null;
        String eco_nome = null;
        try {
            if (eco_codigo != null) {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ECO_CODIGO, eco_codigo);

                empresa = consignatariaController.findEmpresaCorrespondente(criterio, responsavel);
                eco_identificador = (String) empresa.getAttribute(Columns.ECO_IDENTIFICADOR);
                eco_nome = (String) empresa.getAttribute(Columns.ECO_NOME);
            }
            if (empresa == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.obter.dados.empresa.correspondente", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
          } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
          }

        boolean escondeCampoId = true;
        if (request.getParameter("MM_update") != null && empresa != null && csa_codigo != null) {

            if (JspHelper.verificaVarQryStr(request, "MM_update").equals("carregar")) {
                escondeCampoId = false;

            } else if (JspHelper.verificaVarQryStr(request, "MM_update").equals("salvar")) {
                try {
                    CustomTransferObject criterio = (CustomTransferObject) empresa;
                    if (cor_codigo != null) {
                        criterio.setAttribute(Columns.COR_CODIGO, cor_codigo);

                    } else {
                        escondeCampoId = false;

                        String cor_identificador = !TextHelper.isNull(request.getParameter("COR_IDENTIFICADOR"))? (String) JspHelper.verificaVarQryStr(request, "COR_IDENTIFICADOR"): eco_identificador;
                        criterio.setAttribute(Columns.COR_IDENTIFICADOR, cor_identificador);
                        criterio.setAttribute(Columns.COR_CSA_CODIGO, csa_codigo);
                    }

                    consignatariaController.associaEmpresaCorrespondente(criterio, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.alteracoes.sucesso", responsavel));
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

        } else if (request.getParameter("operacao") != null && JspHelper.verificaVarQryStr(request, "operacao").equals("excluir") && cor_codigo != null) {

            try {
                CorrespondenteTransferObject correspondente = new CorrespondenteTransferObject();
                correspondente.setAttribute(Columns.COR_CODIGO, cor_codigo);
                correspondente.setEcoCodigo(null);

                consignatariaController.updateCorrespondente(correspondente, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.alteracoes.sucesso", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        List<?> consignatarias = new ArrayList<>();
        try {
            consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
          } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
          }

        List<?> correspondentes = new ArrayList<>();
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            if (csa_codigo != null) {
                criterio.setAttribute(Columns.COR_CSA_CODIGO, csa_codigo);
                criterio.setAttribute(Columns.COR_ECO_CODIGO, eco_codigo);
                List<Short> statusCor = new ArrayList<>();
                statusCor.add(CodedValues.STS_ATIVO);
                statusCor.add(CodedValues.STS_INATIVO);
                statusCor.add(CodedValues.STS_INATIVO_CSE);
                statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.COR_ATIVO, statusCor);
                correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
            }
          } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
          }

        List<?> associacoes = new ArrayList<>();
        try {
            CustomTransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.ECO_CODIGO, eco_codigo);
            associacoes = consignatariaController.lstAssociacaoEmpresaCorrespondente(to, responsavel);
          } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
          }

        model.addAttribute("associacoes", associacoes);
        model.addAttribute("titulo", titulo);
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("eco_identificador", eco_identificador);
        model.addAttribute("eco_nome", eco_nome);
        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("cor_codigo", cor_codigo);
        model.addAttribute("correspondentes", correspondentes);
        model.addAttribute("escondeCampoId", escondeCampoId);
        model.addAttribute("eco_codigo", eco_codigo);
        model.addAttribute("podeEditarCor", podeEditarCor);
        model.addAttribute("podeConsultarCor", podeConsultarCor);

        return viewRedirect("jsp/manterEmpresaCorrespondente/associaEmpresaCorrespondente", request, session, model, responsavel);

    }
}
