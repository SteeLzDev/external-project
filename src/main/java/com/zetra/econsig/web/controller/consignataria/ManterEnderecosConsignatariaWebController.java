package com.zetra.econsig.web.controller.consignataria;

import java.math.BigDecimal;
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

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.EnderecoConsignataria;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.webclient.googlemaps.GoogleMapsClient;

/**
 * <p>Title: ListarEnderecosConsignatariaWebController</p>
 * <p>Description: Listar registros da tb_endereco_consignataria</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterEnderecosConsignataria" })
public class ManterEnderecosConsignatariaWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterEnderecosConsignatariaWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.colaborador.rescisao.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String listarEnderecosConsigataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            SynchronizerToken.saveToken(request);

            List<TransferObject> listaEnderecos = null;

            String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            int total = consignatariaController.countEnderecoConsignatariaByCsaCodigo(csaCodigo, responsavel);

            int size = JspHelper.LIMITE;
            int offset = request.getParameter("offset") != null ? Integer.parseInt(request.getParameter("offset")) : 0;

            listaEnderecos = consignatariaController.lstEnderecoConsignatariaByCsaCodigo(csaCodigo, size, offset, responsavel);

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);

            configurarPaginador(getLinkAction(), "rotulo.listar.enderecos.consignataria.paginacao.titulo", total, size, requestParams, false, request, model);

            // Seta atributos no model
            model.addAttribute("listaEnderecosConsignataria", listaEnderecos);
            model.addAttribute("CSA_CODIGO", csaCodigo);

            return viewRedirect("jsp/manterConsignataria/listarEnderecosConsignataria", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String novoEnderecoConsigataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            if (TextHelper.isNull(csaCodigo)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.consignataria.informar", responsavel);
            }

            String encCodigo = JspHelper.verificaVarQryStr(request, "ENC_CODIGO");
            EnderecoConsignataria enderecoConsignataria = null;

            if (!TextHelper.isNull(encCodigo)) {
                enderecoConsignataria = consignatariaController.findEnderecoConsignatariaByPKCsaCodigo(encCodigo, csaCodigo, responsavel);
            }

            List<TipoEndereco> tiposEndereco = consignatariaController.listAllTipoEndereco(responsavel);

            // Seta atributos no model
            model.addAttribute("lstTipoEndereco", tiposEndereco);
            model.addAttribute("CSA_CODIGO", csaCodigo);
            model.addAttribute("enderecoConsignataria", enderecoConsignataria);

            return viewRedirect("jsp/manterConsignataria/editarEnderecoConsignataria", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarEnderecoConsigataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ParamSession paramSession = ParamSession.getParamSession(session);
            SynchronizerToken.saveToken(request);

            String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            if (TextHelper.isNull(csaCodigo)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.consignataria.informar", responsavel);
            }

            String encCodigo = JspHelper.verificaVarQryStr(request, "ENC_CODIGO");

            String tieCodigo = JspHelper.verificaVarQryStr(request, "TIE_CODIGO");
            String encCep = JspHelper.verificaVarQryStr(request, "ENC_CEP");
            String encLogradouro = JspHelper.verificaVarQryStr(request, "ENC_LOGRADOURO");
            String encNumero = JspHelper.verificaVarQryStr(request, "ENC_NUMERO");
            String encComplemento = JspHelper.verificaVarQryStr(request, "ENC_COMPLEMENTO");
            String encBairro = JspHelper.verificaVarQryStr(request, "ENC_BAIRRO");
            String encUf = JspHelper.verificaVarQryStr(request, "ENC_UF");
            String encMunicipio = JspHelper.verificaVarQryStr(request, "ENC_MUNICIPIO");

            if (TextHelper.isNull(tieCodigo)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.tipo.endereco.informar", responsavel);
            }

            if (TextHelper.isNull(encCep)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.cep.informar", responsavel);
            }

            if (TextHelper.isNull(encLogradouro)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.logradouro.informar", responsavel);
            }

            if (TextHelper.isNull(encNumero)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.numero.informar", responsavel);
            }

            if (TextHelper.isNull(encBairro)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.bairro.informar", responsavel);
            }

            if (TextHelper.isNull(encUf)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.uf.informar", responsavel);
            }

            if (TextHelper.isNull(encMunicipio)) {
                throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.municipio.informar", responsavel);
            }

            if (TextHelper.isNull(encCodigo)) {
                BigDecimal[] latitudeLongitude = GoogleMapsClient.buscaLatitudeLongitude(encLogradouro, encNumero, encBairro, encMunicipio, encUf, encCep, responsavel);
                consignatariaController.createEnderecoConsignataria(csaCodigo, tieCodigo, encLogradouro, encNumero, encComplemento, encBairro, encMunicipio, encUf, encCep, latitudeLongitude[0], latitudeLongitude[1], responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.consignataria.criado", responsavel));
            } else {
                EnderecoConsignataria enderecoOld = consignatariaController.findEnderecoConsignatariaByPKCsaCodigo(encCodigo, csaCodigo, responsavel);
                EnderecoConsignataria endereco = new EnderecoConsignataria();

                endereco.setEncCodigo(encCodigo);
                Consignataria csa = new Consignataria();
                csa.setCsaCodigo(csaCodigo);
                endereco.setConsignataria(csa);
                TipoEndereco tipoEndereco = new TipoEndereco();
                tipoEndereco.setTieCodigo(tieCodigo);
                endereco.setTipoEndereco(tipoEndereco);
                endereco.setEncLogradouro(encLogradouro);
                endereco.setEncNumero(encNumero);
                endereco.setEncComplemento(encComplemento);
                endereco.setEncBairro(encBairro);
                endereco.setEncMunicipio(encMunicipio);
                endereco.setEncUf(encUf);
                endereco.setEncCep(encCep);

                if (!enderecoOld.equals(endereco)) {
                    BigDecimal[] latitudeLongitude = GoogleMapsClient.buscaLatitudeLongitude(encLogradouro, encNumero, encBairro, encMunicipio, encUf, encCep, responsavel);
                    consignatariaController.updateEnderecoConsignataria(encCodigo, csaCodigo, tieCodigo, encLogradouro, encNumero, encComplemento, encBairro, encMunicipio, encUf, encCep, latitudeLongitude[0], latitudeLongitude[1], responsavel);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.consignataria.atualizado", responsavel));
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirEnderecoConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String encCodigo = JspHelper.verificaVarQryStr(request, "ENC_CODIGO");
            String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            consignatariaController.removeEnderecoConsignataria(encCodigo, csaCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.consignataria.removido", responsavel));

            return listarEnderecosConsigataria(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private String getLinkAction() {
        return "../v3/manterEnderecosConsignataria?acao=iniciar";
    }
}
