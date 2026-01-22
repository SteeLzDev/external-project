package com.zetra.econsig.web.controller.usuario.servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: DetalharUsuarioServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso detalhar usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/detalharUsuarioServidor" })
public class DetalharUsuarioServidorWebController extends ControlePaginacaoWebController {

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String rse_codigo = null;
        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String orgIdentificador = JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
        String estIdentificador = JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR");

        RegistroServidorTO rse = new RegistroServidorTO();
        rse.setRseMatricula(rseMatricula);
        rse.setOrgCodigo(orgCodigo);

        RegistroServidorTO registro = null;
        CustomTransferObject servInfo = null;
        OrgaoTransferObject orgTO = null;
        EstabelecimentoTransferObject est = null;
        UsuarioTransferObject usuario = null;
        String login = "";

        try {
            registro = servidorController.findRegistroServidor(rse, responsavel);
            rse_codigo = registro.getRseCodigo();
            servInfo = pesquisarServidorController.buscaServidor(rse_codigo, responsavel);
            orgTO = consignanteController.findOrgao(registro.getOrgCodigo(), responsavel);
            est = consignanteController.findEstabelecimento(orgTO.getEstCodigo(), responsavel);
            if (orgIdentificador.equals("")) {
                orgIdentificador = orgTO.getOrgIdentificador();
            }
            if (estIdentificador.equals("")) {
                estIdentificador = est.getEstIdentificador();
            }

            // Verifica parâmetro que indica a forma do login de usuário servidor
            boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
            if (loginComEstOrg) {
                login = estIdentificador + "-" + orgIdentificador + "-" + rseMatricula;
            } else {
                login = estIdentificador + "-" + rseMatricula;
            }
            usuario = usuarioController.findUsuarioByLogin(login, responsavel);
        } catch (ServidorControllerException | UsuarioControllerException | ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> hist = null;
        try {
            CustomTransferObject filtro = new CustomTransferObject();
            filtro.setAttribute(Columns.OUS_USU_CODIGO, usuario.getUsuCodigo());

            int total = usuarioController.countOcorrenciaUsuario(filtro, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            hist = usuarioController.lstOcorrenciaUsuario(filtro, offset, size, responsavel);

            String linkPaginacaoAction = "../v3/detalharUsuarioServidor?acao=iniciar";
            List<String> listParams = Arrays.asList(new String [] {"RSE_MATRICULA", "SER_CPF", "ORG_IDENTIFICADOR", "ORG_CODIGO", "EST_IDENTIFICADOR"});
            configurarPaginador(linkPaginacaoAction, "rotulo.servidor.usuario.detalhe.titulo", total, size, listParams, false, request, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            hist = new ArrayList<>();
        }

        model.addAttribute("hist", hist);
        model.addAttribute("usuario", usuario);
        model.addAttribute("servInfo", servInfo);

        return viewRedirect("jsp/editarUsuarioServidor/detalharUsuarioServidor", request, session, model, responsavel);
    }
}
