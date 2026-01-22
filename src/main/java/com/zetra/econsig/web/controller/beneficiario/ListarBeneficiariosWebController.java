package com.zetra.econsig.web.controller.beneficiario;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: ListarBeneficiariosWebController</p>
 * <p>Description: Listar e consultar beneficiários</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarBeneficiarios" })
public class ListarBeneficiariosWebController extends AbstractConsultarServidorWebController {

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.lista.beneficiarios.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/listarBeneficiarios");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("imageHeader", "i-beneficios");
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String serCodigo = "";
        String link = "";

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        } else if (!JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)).isEmpty()) {
            rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));
            link = getLinkAction(null);
        } else {
            link = getLinkAction(rseCodigo);
        }

        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_ALTERAR_CADASTRO_BENEFICIARIOS);
        boolean editarAenxo = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ANEXO_BENEFICIARIOS);
        boolean titularExiste = false;

        List<TransferObject> beneficiarios = null;

        // Pega o filtro e o tipo do filtro vindo do request
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;

        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("filtro", filtro);
            criterio.setAttribute("filtro_tipo", "" + filtro_tipo);
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);

            int total = beneficiarioController.listarCountBeneficiarios(criterio, responsavel);

            int size = JspHelper.LIMITE;
            int offset = 0;

            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            beneficiarios = beneficiarioController.listarBeneficiarios(criterio, offset, size, responsavel);
            serCodigo = (String) servidorController.findRegistroServidor(rseCodigo, responsavel).getAttribute(Columns.RSE_SER_CODIGO);

            if (beneficiarios !=null && !beneficiarios.isEmpty() ) {
                for (TransferObject beneficiario : beneficiarios ) {
                    String tibCodigo = (String) beneficiario.getAttribute(Columns.TIB_CODIGO);

                    if (!TextHelper.isNull(tibCodigo) && tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        titularExiste = true;
                        break;
                    }
                }
            }

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(link, "rotulo.paginacao.titulo.beneficiario", total, size, requestParams, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            beneficiarios = new ArrayList<>();
        }

        // Seta atributos no model
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("editarAnexo", editarAenxo);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("beneficiarios", beneficiarios);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);
        model.addAttribute(Columns.SER_CODIGO, serCodigo);
        model.addAttribute("titularExiste", titularExiste);

        return viewRedirect("jsp/manterBeneficio/listarBeneficiario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        Beneficiario beneficiario = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);
        List<TransferObject> nacionalidade = beneficiarioController.listarNacionalidade(responsavel);
        List<TransferObject> tipoBeneficiarios = beneficioController.listaTipoBeneficiario(null, responsavel);
        List<TransferObject> grauParentesco = beneficiarioController.listaGrauParentesco(null, responsavel);
        List<TransferObject> estadoCivil = beneficiarioController.listaEstadoCivil(null, responsavel);
        List<TransferObject> motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

        model.addAttribute("podeEditar", false);
        model.addAttribute("novo", false);
        model.addAttribute("beneficiario", beneficiario);
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("estadoCivil", estadoCivil);
        model.addAttribute("motivoDependencia", motivoDependencia);
        model.addAttribute("nacionalidade", nacionalidade);
        model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));

        return viewRedirect("jsp/manterBeneficio/alterarBeneficiario", request, session, model, responsavel);
    }

    private String getLinkAction(String rseCodigo) {
        if (rseCodigo != null) {
            return "../v3/listarBeneficiarios?acao=listar&" + Columns.getColumnName(Columns.RSE_CODIGO) + "=" + rseCodigo;
        }
        return "../v3/listarBeneficiarios?acao=listar";
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return listar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "listar";
    }

}
