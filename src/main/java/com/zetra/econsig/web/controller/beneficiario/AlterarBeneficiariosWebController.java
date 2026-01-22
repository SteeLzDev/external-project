package com.zetra.econsig.web.controller.beneficiario;

import java.text.SimpleDateFormat;
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
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.EstadoCivil;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.Nacionalidade;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.EstadoCivilEnum;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AlterarBeneficiariosWebController</p>
 * <p>Description:Alterar beneficiários(Editar, novo e excluir)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarBeneficiarios" })
public class AlterarBeneficiariosWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarBeneficiariosWebController.class);

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private BeneficioController beneficioController;

    @RequestMapping(params = { "acao=novo" })
    public String novoBeneficiario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<TransferObject> nacionalidade = beneficiarioController.listarNacionalidade(responsavel);
        List<TransferObject> tipoBeneficiarios = beneficioController.listaTipoBeneficiario(null, responsavel);
        List<TransferObject> grauParentesco = beneficiarioController.listaGrauParentesco(null, responsavel);
        List<TransferObject> estadoCivil = beneficiarioController.listaEstadoCivil(null, responsavel);
        List<TransferObject> motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

        //Remove o tipo titular da lista
        int cont = 0;
        for (TransferObject tib : tipoBeneficiarios) {
            if (tib.getAttribute(Columns.TIB_CODIGO).equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                tipoBeneficiarios.remove(cont);
                break;
            }
            cont++;
        }

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        criterio.setAttribute(Columns.TIB_CODIGO, TipoBeneficiarioEnum.TITULAR.tibCodigo);

        preencherDadoContato(criterio, model, responsavel);

        boolean reserva = JspHelper.verificaVarQryStr(request, "reserva") != null && JspHelper.verificaVarQryStr(request, "reserva").equals("S");

        model.addAttribute("voltarReserva", reserva);
        model.addAttribute("podeEditar", true);
        model.addAttribute("novo", true);
        model.addAttribute("beneficiario", new Beneficiario());
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("estadoCivil", estadoCivil);
        model.addAttribute("motivoDependencia", motivoDependencia);
        model.addAttribute("nacionalidade", nacionalidade);
        model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));

        return viewRedirect("jsp/manterBeneficio/alterarBeneficiario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarBeneficiario(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, @RequestParam(value = "serCodigo", required = false) String serCodigo, @RequestParam(value = "rseCodigo", required = false) String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, FindException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (bfcCodigo != null) {
            if (bfcCodigo.equals("")) {
                bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
            }
        } else {
            bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        }

        if (rseCodigo != null) {
            if (rseCodigo.equals("")) {
                rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            }
        } else {
            rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        }
        if (serCodigo != null) {
            if (serCodigo.equals("")) {
                serCodigo = (responsavel.isSer() ? responsavel.getSerCodigo() : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            }
        } else {
            serCodigo = (responsavel.isSer() ? responsavel.getSerCodigo(): JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
        }

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        criterio.setAttribute(Columns.TIB_CODIGO, TipoBeneficiarioEnum.TITULAR.tibCodigo);

        preencherDadoContato(criterio, model, responsavel);

        Beneficiario beneficiario = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);

        //valida escalação de usuário.
        if (responsavel.isSer() && !responsavel.getSerCodigo().equals(beneficiario.getServidor().getSerCodigo())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<ContratoBeneficio> cbes = beneficiarioController.findContratoBeneficioByBeneficiarioAndTntCodigoAndSadCodigo(bfcCodigo, CodedValues.TNT_BENEFICIO_MENSALIDADE, CodedValues.SAD_CODIGOS_ATIVOS, responsavel);
        List<TransferObject> nacionalidade = beneficiarioController.listarNacionalidade(responsavel);
        List<TransferObject> tipoBeneficiarios = beneficioController.listaTipoBeneficiario(null, responsavel);
        List<TransferObject> grauParentesco = beneficiarioController.listaGrauParentesco(null, responsavel);
        List<TransferObject> estadoCivil = beneficiarioController.listaEstadoCivil(null, responsavel);
        List<TransferObject> motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

        //Remove o tipo titular da lista se não for titular
        if (beneficiario.getTipoBeneficiario().getTibCodigo().compareTo(TipoBeneficiarioEnum.TITULAR.tibCodigo) != 0) {
            int cont = 0;
            for (TransferObject tib : tipoBeneficiarios) {
                if (tib.getAttribute(Columns.TIB_CODIGO).equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                    tipoBeneficiarios.remove(cont);
                    break;

                }
                cont++;
            }
        }

        model.addAttribute("podeEditar", responsavel.temPermissao(CodedValues.FUN_ALTERAR_CADASTRO_BENEFICIARIOS));
        model.addAttribute("novo", false);
        model.addAttribute("beneficiario", beneficiario);
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("estadoCivil", estadoCivil);
        model.addAttribute("motivoDependencia", motivoDependencia);
        model.addAttribute("contratoBeneficio", cbes);
        model.addAttribute("nacionalidade", nacionalidade);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);
        model.addAttribute(Columns.SER_CODIGO, serCodigo);

        return viewRedirect("jsp/manterBeneficio/alterarBeneficiario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarBeneficiario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSession paramSession = ParamSession.getParamSession(session);
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && responsavel.temPermissao(CodedValues.FUN_ALTERAR_CADASTRO_BENEFICIARIOS)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Beneficiario beneficiario = new Beneficiario();

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        String rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
        String serCodigo = (responsavel.isSer() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
        boolean bfcTitular = JspHelper.verificaVarQryStr(request, "BFC_TITULAR").equals(CodedValues.TPC_SIM);

        String termoCienciaCheckbox = JspHelper.verificaVarQryStr(request, "termoCienciaCheckbox");
        String termoCienciaMaeCheckbox = JspHelper.verificaVarQryStr(request, "termoCienciaMaeCheckbox");

        // Analisando os campos
        String nome = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME));
        String cpf = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CPF));

        boolean beneficiarioNovo = !bfcCodigo.isEmpty() ? Boolean.FALSE : Boolean.TRUE;

        boolean cpfJaExisteNoGrupoFamiliar = cpfBeneficiarioExisteNoGrupoFamiliar(cpf, rseCodigo, beneficiarioNovo, bfcCodigo, responsavel);

        if (cpfJaExisteNoGrupoFamiliar) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.beneficiario.existente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String rg = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_RG));
        rg = !rg.equals("") ? rg : null;
        String sexo = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SEXO)).equals("") ? JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SEXO)) : null;
        boolean usaTelTitular = !TextHelper.isNull(request.getParameter("usarTelTitular")) && request.getParameter("usarTelTitular").equals(CodedValues.TPC_SIM);
        boolean usaCelTitular = !TextHelper.isNull(request.getParameter("usarCelTitular")) && request.getParameter("usarCelTitular").equals(CodedValues.TPC_SIM);
        String telefone = null;
        String celular = null;
        if (usaTelTitular && !TextHelper.isNull(rseCodigo)) {
            telefone = buscarTelTitular(rseCodigo, responsavel);
            if (TextHelper.isNull(telefone)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.telefone.titular.nulo", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            telefone = JspHelper.verificaVarQryStr(request, "BFC_DDD_TELEFONE") + JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_TELEFONE));
        }
        if (usaCelTitular && !TextHelper.isNull(rseCodigo)) {
            celular = buscarCelTitular(rseCodigo, responsavel);
            if (TextHelper.isNull(celular)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.celular.titular.nulo", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            celular = JspHelper.verificaVarQryStr(request, "BFC_DDD_CELULAR") + JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CELULAR));
        }
        String nomeMae = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME_MAE));
        Date dataNascimento = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)).equals("") ? formato.parse(DateHelper.reformat(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;
        String estadoCivil = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.EST_CIVIL_CODIGO)).equals("") ? JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.EST_CIVIL_CODIGO)) : null;
        Date bfcDataCasamento = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)).equals("") ? formato.parse(DateHelper.reformat(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;
        Date bfcDataObito = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_OBITO)).equals("") ? formato.parse(DateHelper.reformat(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_OBITO)), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;

        TipoBeneficiario tipoBeneficiario = new TipoBeneficiario();
        tipoBeneficiario.setTibCodigo(JspHelper.verificaVarQryStr(request,Columns.getColumnName(Columns.TIB_CODIGO)));

        Nacionalidade nac = null;
        String nacCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.NAC_CODIGO));
        if (!TextHelper.isNull(nacCodigo)) {
            nac = new Nacionalidade(nacCodigo);
        }

        GrauParentesco grauParentesco = null;
        String grpCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO));
        if (!TextHelper.isNull(grpCodigo)) {
            grauParentesco = new GrauParentesco(grpCodigo);
        }

        Servidor servidor = new Servidor();
        servidor.setSerCodigo(serCodigo);

        // realizando as validações
        if ((estadoCivil != null && !estadoCivil.toString().equals(EstadoCivilEnum.CASADO.getCodigo())) && (grpCodigo != null && grpCodigo.equals(GrauParentescoEnum.COMPANHEIRO.getCodigo()) || grpCodigo.equals(GrauParentescoEnum.CONJUGE.getCodigo()))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.erro.estado.civil.invalido", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String cpfAux = cpf.replaceAll("[^0-9]", "");
        if (!TextHelper.cpfOk(cpfAux)) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.cpf.beneficiario.invalido", responsavel);
        }

        String[] tokenNome = nome.split(" ");
        for (String token : tokenNome) {
            token = token.replaceAll("[^A-Za-z]", "");
            if (token.length() <= 1 && TextHelper.isNull(termoCienciaCheckbox)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nome.beneficiario.abreviado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else if (token.length() <= 1 && TextHelper.isNull(termoCienciaMaeCheckbox)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nome.mae.beneficiario.abreviado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        int telefoneTamanho = Integer.parseInt(LocaleHelper.getTelefoneSize());
        int celularTamanho = Integer.parseInt(LocaleHelper.getCelularSize());

        // removendo a mascara
        if (!TextHelper.isNull(telefone)) {
            telefone = telefone.replaceAll("-", "");
            if (telefone.length() <= telefoneTamanho) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.telefone.invalido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        }
        if (!TextHelper.isNull(celular)) {
            celular = celular.replaceAll("-", "");
            if (celular.length() <= celularTamanho) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.celular.invalido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        }

        if (!TextHelper.isNull(tipoBeneficiario.getTibCodigo()) && tipoBeneficiario.getTibCodigo().compareTo(TipoBeneficiarioEnum.TITULAR.tibCodigo) != 0 && grauParentesco == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.grau.parentesco.informar", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Analisando o motivo de dependencia
        MotivoDependencia motivoDependencia = new MotivoDependencia();
        Date bfcExcecaoDependenciaIni = null;
        Date bfcExcecaoDependenciaFim = null;
        String subsidioConcedido = null;
        String subsidioConcedidoMotivo = null;

        if (responsavel.isSup() || responsavel.isCsa()) {
            if (!JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.MDE_CODIGO)).equals("")) {
                motivoDependencia.setMdeCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.MDE_CODIGO)));
            } else {
                motivoDependencia = null;
            }

            bfcExcecaoDependenciaIni = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_INI)).equals("") ? formato.parse(DateHelper.reformat(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_INI)), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;
            bfcExcecaoDependenciaFim = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_FIM)).equals("") ? formato.parse(DateHelper.reformat(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_FIM)), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;
            subsidioConcedido = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)).equals("") ? JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)) : CodedValues.TPC_NAO;
            if (CodedValues.TPC_SIM.equals(subsidioConcedido)) {
                subsidioConcedidoMotivo = !JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)).equals("") ? JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)) : null;
            }
        } else if (responsavel.isSer()) {
            motivoDependencia = null;
        }

        boolean camposObrigatoriosOk = validaCamposObrigatorios(telefone, celular, bfcTitular, request, responsavel);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
        List<TransferObject> beneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);
        boolean titularExiste = false;

        if (beneficiarios !=null && !beneficiarios.isEmpty() ) {
            for (TransferObject analiseBeneficiario : beneficiarios ) {
                String tibCodigo = (String) analiseBeneficiario.getAttribute(Columns.TIB_CODIGO);

                if (!TextHelper.isNull(tibCodigo) && tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                    titularExiste = true;
                    break;
                }
            }
        }


        if (!titularExiste) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.beneficiario.titular.nao.existe", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (camposObrigatoriosOk) {
            if (bfcCodigo.isEmpty()) {
                bfcDataObito = null;
                try {
                    bfcCodigo = beneficiarioController.create(servidor, tipoBeneficiario, motivoDependencia, Short.MAX_VALUE, nome, cpf, rg, sexo, telefone, celular, nomeMae, grauParentesco, dataNascimento, estadoCivil, subsidioConcedido, subsidioConcedidoMotivo, bfcExcecaoDependenciaIni, bfcExcecaoDependenciaFim, new StatusBeneficiario(StatusBeneficiarioEnum.ATIVO.sbeCodigo), nac, bfcDataCasamento, bfcDataObito, responsavel).getBfcCodigo();

                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.novo.beneficario.exibicao.sucesso", responsavel));
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } else {
                // Atualiza o beneficiário já existente
                beneficiario = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);

                //valida escalação de usuário.
                if (responsavel.isSer() && !responsavel.getSerCodigo().equals(beneficiario.getServidor().getSerCodigo())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (beneficiario.getTipoBeneficiario() != null && beneficiario.getTipoBeneficiario().getTibCodigo().compareTo(TipoBeneficiarioEnum.TITULAR.tibCodigo) == 0) {
                    tipoBeneficiario = beneficiario.getTipoBeneficiario();
                }

                if (!TextHelper.isNull(estadoCivil)) {
                    EstadoCivil estCvl = new EstadoCivil();
                    estCvl.setEstCvlCodigo(estadoCivil.toString());
                    beneficiario.setBfcEstadoCivil(!estCvl.getEstCvlCodigo().equals("") ? estCvl.getEstCvlCodigo() : null);
                }

                beneficiario.setTipoBeneficiario(tipoBeneficiario);
                beneficiario.setGrauParentesco(grauParentesco);
                beneficiario.setNacionalidade(nac);
                beneficiario.setBfcNome(nome);
                beneficiario.setBfcCpf(cpf);
                beneficiario.setBfcRg(rg);
                beneficiario.setBfcSexo(sexo);
                beneficiario.setBfcTelefone(telefone);
                beneficiario.setBfcCelular(celular);
                beneficiario.setBfcNomeMae(nomeMae);
                beneficiario.setBfcDataNascimento(dataNascimento);
                beneficiario.setBfcDataCasamento(bfcDataCasamento);

                if ((responsavel.isCseSupOrg() || responsavel.isCsaCor()) && responsavel.temPermissao(CodedValues.FUN_EDITAR_CADASTRO_BENEFICIARIO_AVANCADA)) {
                    beneficiario.setBfcDataObito(bfcDataObito);
                }

                if (responsavel.isCseSupOrg() || responsavel.isCsaCor()) {
                    beneficiario.setMotivoDependencia(motivoDependencia);
                    beneficiario.setBfcExcecaoDependenciaIni(bfcExcecaoDependenciaIni);
                    beneficiario.setBfcExcecaoDependenciaFim(bfcExcecaoDependenciaFim);
                    beneficiario.setBfcSubsidioConcedido(subsidioConcedido);
                    beneficiario.setBfcSubsidioConcedidoMotivo(subsidioConcedidoMotivo);
                }

                try {
                    beneficiarioController.update(beneficiario, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.margem.exibicao.sucesso", responsavel));
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));

        try {
            Beneficiario beneficiario = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);
            List<ContratoBeneficio> cbes = beneficiarioController.findContratoBeneficioByBeneficiarioAndTntCodigoAndSadCodigo(bfcCodigo, CodedValues.TNT_BENEFICIO_MENSALIDADE, CodedValues.SAD_CODIGOS_ATIVOS, responsavel);

            //valida escalação de usuário.
            if (responsavel.isSer() && !responsavel.getSerCodigo().equals(beneficiario.getServidor().getSerCodigo())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && cbes == null) {
                beneficiarioController.remove(beneficiario, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.beneficiario.exibicao.sucesso", responsavel));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.erro.remover.beneficiario.ativo", responsavel));
            }

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficiario.erro.remover.beneficiario", responsavel));
        }
        return "forward:/v3/listarBeneficiarios?acao=listar&_skip_history_=true";
    }

    private boolean cpfBeneficiarioExisteNoGrupoFamiliar(String cpf, String rseCodigo, boolean beneficiarioNovo, String bfcCodigo, AcessoSistema responsavel) throws BeneficioControllerException {

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
        List<TransferObject> beneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);

        List<String> cpfsBeneficiariosGrupoFamiliar = new ArrayList<>();
        List<String> bfcsBeneficiariosGrupoFamiliar = new ArrayList<>();

        for (TransferObject beneficiario : beneficiarios) {
            cpfsBeneficiariosGrupoFamiliar.add(beneficiario.getAttribute(Columns.BFC_CPF).toString());
            bfcsBeneficiariosGrupoFamiliar.add(beneficiario.getAttribute(Columns.BFC_CODIGO).toString());
        }

        if (!beneficiarioNovo) {
            int posicaoCpfVerificacao = 0;
            for (int i = 0; i < bfcsBeneficiariosGrupoFamiliar.size(); i++) {
                if (bfcCodigo.equals(bfcsBeneficiariosGrupoFamiliar.get(i))) {
                    // Encontra a posição na lista de CPFs para ver se o CPF é o mesmo ou diferente
                    posicaoCpfVerificacao = i;
                    break;
                }
            }

            if (!cpf.equals(cpfsBeneficiariosGrupoFamiliar.get(posicaoCpfVerificacao))) {
                //Se o CPF novo não for igual ao que já estava cadastrado, verifica se este novo já tem na lista.
                return (!cpfsBeneficiariosGrupoFamiliar.isEmpty() && cpfsBeneficiariosGrupoFamiliar.contains(cpf));
            } else {
                int cpfIguais = 0;
                for (String cpfBeneficiario : cpfsBeneficiariosGrupoFamiliar) {
                    if (cpf.equals(cpfBeneficiario)) {
                        cpfIguais++;
                    }

                    if (cpfIguais == 2) {
                        return true;
                    }
                }

            }
            return false;
        }

        return (!cpfsBeneficiariosGrupoFamiliar.isEmpty() && cpfsBeneficiariosGrupoFamiliar.contains(cpf));
    }

    private boolean validaCamposObrigatorios(String telefone, String celular, boolean bfcTitular, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        boolean camposObrigatoriosOk = true;
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TIPO, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CPF, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CPF))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CPF)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_SEXO, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SEXO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SEXO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE, responsavel) && (TextHelper.isNull(telefone) || (telefone.isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR, responsavel) && (TextHelper.isNull(celular) || (celular.isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME_MAE, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME_MAE))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_NOME_MAE)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_DATA_NASCIMENTO, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NACIONALIDADE, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.NAC_CODIGO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.NAC_CODIGO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_ESTADO_CIVIL, responsavel) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.EST_CIVIL_CODIGO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.EST_CIVIL_CODIGO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_BENEFICIARIO_GRAU_PARENTESCO, responsavel) && (!bfcTitular && TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO))) || (!bfcTitular && (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO)).isEmpty())))) {
            camposObrigatoriosOk = false;
        }
        if ((!JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO)).isEmpty() && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO))) && (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO)).equals(GrauParentescoEnum.COMPANHEIRO.getCodigo())) || JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.GRP_CODIGO)).equals(GrauParentescoEnum.CONJUGE.getCodigo())) && (TextHelper.isNull(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_CASAMENTO))) || (JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)).isEmpty()))) {
            camposObrigatoriosOk = false;
        }
        return camposObrigatoriosOk;
    }

    private String buscarTelTitular(String rseCodigo, AcessoSistema responsavel) {
        String telefone = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.TIB_CODIGO, TipoBeneficiarioEnum.TITULAR.tibCodigo);
            List<TransferObject> beneficiarioTitular = beneficiarioController.listarBeneficiarios(criterio, responsavel);
            telefone = (String) beneficiarioTitular.get(0).getAttribute(Columns.BFC_TELEFONE);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage());
        }
        return telefone;
    }

    private String buscarCelTitular(String rseCodigo, AcessoSistema responsavel) {
        String celular = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("filtro_tipo", "" + -1);
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.TIB_CODIGO, TipoBeneficiarioEnum.TITULAR.tibCodigo);
            List<TransferObject> beneficiarioTitular = beneficiarioController.listarBeneficiarios(criterio, 0, JspHelper.LIMITE, responsavel);
            celular = (String) beneficiarioTitular.get(0).getAttribute(Columns.BFC_CELULAR);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage());
        }
        return celular;
    }

   private void preencherDadoContato(CustomTransferObject criterio, Model model, AcessoSistema responsavel) throws BeneficioControllerException {

       List<TransferObject> beneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);

       if (beneficiarios != null && !beneficiarios.isEmpty()) {
           String telServidorCompleto = beneficiarios.get(0) != null && !beneficiarios.isEmpty() ? (beneficiarios.get(0).getAttribute(Columns.BFC_TELEFONE)!= null ? beneficiarios.get(0).getAttribute(Columns.BFC_TELEFONE).toString() : "") : "";
           String celServidorCompleto = beneficiarios.get(0) != null && !beneficiarios.isEmpty() ? (beneficiarios.get(0).getAttribute(Columns.BFC_CELULAR)!= null ? beneficiarios.get(0).getAttribute(Columns.BFC_CELULAR).toString() : "") : "";

           String dddTelServidor = !TextHelper.isNull(telServidorCompleto) ? telServidorCompleto.substring(0,2) : "";
           String telServidor = !TextHelper.isNull(telServidorCompleto) && telServidorCompleto.length() >= 10 ? telServidorCompleto.substring(2,10) : (!TextHelper.isNull(telServidorCompleto) && telServidorCompleto.length() < 10 ? telServidorCompleto : "");
           String dddCelServidor = !TextHelper.isNull(celServidorCompleto) ? celServidorCompleto.substring(0,2) : "";
           String celServidor = !TextHelper.isNull(celServidorCompleto) && celServidorCompleto.length() >= 11 ? celServidorCompleto.substring(2,11) : (!TextHelper.isNull(celServidorCompleto) && celServidorCompleto.length() < 11 ? celServidorCompleto : "");

           int telefoneTamanho = Integer.parseInt(LocaleHelper.getTelefoneSize()) + 2;
           int celularTamanho = Integer.parseInt(LocaleHelper.getCelularSize()) + 2;
           boolean telInvalido = false;
           boolean celInvalido = false;

           // removendo a mascara
           if (!TextHelper.isNull(telServidorCompleto)) {
               telServidorCompleto = telServidorCompleto.replaceAll("-", "");
               if (telServidorCompleto.length() != telefoneTamanho) {
                   telInvalido = true;
               }
           }
           if (!TextHelper.isNull(celServidorCompleto)) {
               celServidorCompleto = celServidorCompleto.replaceAll("-", "");
               if (celServidorCompleto.length() != celularTamanho) {
                   celInvalido = true;
               }
           }

           model.addAttribute("dddTelServidor", dddTelServidor);
           model.addAttribute("telServidor", telServidor);
           model.addAttribute("dddCelServidor", dddCelServidor);
           model.addAttribute("celServidor", celServidor);
           model.addAttribute("telInvalido", telInvalido);
           model.addAttribute("celInvalido", celInvalido);
       }
   }

    @RequestMapping(params = { "acao=novoReserva" })
    public String novoBeneficiarioReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return novoBeneficiario(request, response, session, model);
    }

    @RequestMapping(params = { "acao=salvarReserva" })
    public String salvarBeneficiarioReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return salvarBeneficiario(request, response, session, model);
    }
}
