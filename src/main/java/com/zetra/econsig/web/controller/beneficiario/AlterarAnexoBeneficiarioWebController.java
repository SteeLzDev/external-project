package com.zetra.econsig.web.controller.beneficiario;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
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
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoBeneficiario;
import com.zetra.econsig.persistence.entity.AnexoBeneficiarioId;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AlterarAnexoBeneficiarioWebController</p>
 * <p>Description: Alterar informações do anexo de beneficiários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarAnexoBeneficiario" })
public class AlterarAnexoBeneficiarioWebController extends AbstractWebController {

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=novo" })
    public String novoAnexoBeneficiario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, HistoricoArquivoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_BENEFICIARIOS);

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));

        List<TransferObject> tipoDocumento = historicoArquivoController.lstTiposArquivoByTarCodigos(montaListTarCodigos(), responsavel);

        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("novo", true);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("anexo", new AnexoBeneficiario());
        model.addAttribute(Columns.BFC_CODIGO, bfcCodigo);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);

        return viewRedirect("jsp/manterBeneficio/alterarAnexoBeneficiario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarAnexoBeneficiario(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, @RequestParam(value = "abfNome", required = false) String abfNome, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, HistoricoArquivoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_BENEFICIARIOS);
        if (bfcCodigo != null && abfNome != null) {
            if (bfcCodigo.equals("") && abfNome.equals("")) {
                bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
                abfNome = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ABF_NOME));
            }
        } else {
            bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
            abfNome = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ABF_NOME));
        }

        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));
        AnexoBeneficiarioId anexoId = new AnexoBeneficiarioId();
        anexoId.setAbfNome(abfNome);
        anexoId.setBfcCodigo(bfcCodigo);

        AnexoBeneficiario anexo = beneficiarioController.findAnexoBeneficiarioByPrimaryKey(anexoId, responsavel);

        List<TransferObject> tipoDocumento = historicoArquivoController.lstTiposArquivoByTarCodigos(montaListTarCodigos(), responsavel);

        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("novo", false);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("anexo", anexo);
        model.addAttribute(Columns.BFC_CODIGO, bfcCodigo);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);

        return viewRedirect("jsp/manterBeneficio/alterarAnexoBeneficiario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarAnexoBeneficiario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        int maxSize = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_ANEXO_BENEFICIARIO, responsavel) != null ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_ANEXO_BENEFICIARIO, responsavel).toString()) : 0;
        maxSize = maxSize * 1024 * 1024;

        UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (Throwable ex) {
            String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        String abfDescricao = uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.ABF_DESCRICAO));
        String tipo = uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.TAR_CODIGO));
        String dataValidade = (uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.ABF_DATA_VALIDADE)) != null ? uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.ABF_DATA_VALIDADE)) : "");
        String abfNome = TextHelper.removeAccent((uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.ABF_NOME)) != null ? uploadHelper.getValorCampoFormulario(Columns.getColumnName(Columns.ABF_NOME)) : JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ABF_NOME))));
        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));

        // Cria as entidades que serão usadas no AnexoBeneficiario
        TipoArquivo tar = new TipoArquivo();
        tar.setTarCodigo(tipo);

        Beneficiario bfc = new Beneficiario();
        bfc.setBfcCodigo(bfcCodigo);

        Usuario usu = new Usuario();
        usu.setUsuCodigo(responsavel.getUsuCodigo());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date abfDataValidade = null;

        if (!dataValidade.equals("")) {
            abfDataValidade = df.parse(dataValidade);
        }

        AnexoBeneficiarioId anexoId = new AnexoBeneficiarioId();
        anexoId.setAbfNome(abfNome);
        anexoId.setBfcCodigo(bfcCodigo);

        // Verifica se já existe um anexo
        AnexoBeneficiario anexo = beneficiarioController.findAnexoBeneficiarioByPrimaryKey(anexoId, responsavel);

        //DESENV-15448 - Buscar matricula e descrição do documento
        CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
        String rseMatricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);

        List<String> tarCodigos = new ArrayList<>();
        tarCodigos.add(tipo);
        List<TransferObject> lstTipoArquivos = historicoArquivoController.lstTiposArquivoByTarCodigos(tarCodigos, responsavel);

        String tarDescricao = (String) lstTipoArquivos.get(0).getAttribute(Columns.TAR_DESCRICAO);

        // Novo anexo
        if (anexo == null) {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
            int total = beneficiarioController.listarCountAnexosBeneficiario(criterio, responsavel);
            int qtdMaxima = ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_ANEXO_BENEFICIARIO, responsavel) != null ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_ANEXO_BENEFICIARIO, responsavel).toString()) : 0;

            if (total < qtdMaxima) {
                try {
                    // Monta o path do novo arquivo
                    Date date = new Date();
                    String dataAtual = new SimpleDateFormat("yyyyMMdd").format(date);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date abfDataNova = dateFormat.parse(dateFormat.format(date));

                    String path = "beneficiario" + java.io.File.separatorChar + dataAtual + java.io.File.separatorChar + bfcCodigo;

                    abfNome = new SimpleDateFormat("yyyyMMddHHmmSSS").format(new Date()).toString() + "_" + tarDescricao.replaceAll(" ", "") + "_" + rseMatricula;
                    java.io.File arq = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, TextHelper.removeAccent(abfNome));
                    String extensao = arq.getName().substring(arq.getName().lastIndexOf("."), arq.getName().length());
                    abfNome += extensao;
                    abfNome = TextHelper.removeAccent(abfNome);

                    anexo = new AnexoBeneficiario();
                    anexoId.setAbfNome(abfNome);
                    anexo.setId(anexoId);
                    anexo.setTipoArquivo(tar);
                    anexo.setAbfNome(abfNome);
                    anexo.setAbfAtivo(CodedValues.STS_ATIVO);
                    anexo.setAbfDescricao(abfDescricao);
                    anexo.setAbfData(abfDataNova);
                    anexo.setAbfDataValidade(abfDataValidade);
                    anexo.setBeneficiario(bfc);
                    anexo.setUsuario(usu);
                    anexo.setAbfIpAcesso(responsavel.getIpUsuario());

                    beneficiarioController.createAnexoBeneficiario(anexo, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.beneficiario.sucesso", responsavel));
                } catch (ZetraException e) {
                    session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                    // Repassa o token salvo, pois o método irá revalidar o token
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    return novoAnexoBeneficiario(request, response, session, model);
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.anexo.beneficiario.quantidade.maior", responsavel));
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return novoAnexoBeneficiario(request, response, session, model);
            }
        } else {
            try {
                anexo.setTipoArquivo(tar);
                anexo.setUsuario(usu);
                anexo.setBeneficiario(bfc);
                anexo.setAbfDescricao(abfDescricao);
                anexo.setAbfDataValidade(abfDataValidade);
                anexo.setAbfIpAcesso(responsavel.getIpUsuario());

                beneficiarioController.updateAnexoBeneficiario(anexo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.beneficiario.editar", responsavel));
            } catch (ZetraException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return novoAnexoBeneficiario(request, response, session, model);
            }
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return editarAnexoBeneficiario(bfcCodigo, abfNome, request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String removerAnexoBeneficiario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, HistoricoArquivoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        String abfNome = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ABF_NOME));
        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));

        AnexoBeneficiarioId anexoId = new AnexoBeneficiarioId();
        anexoId.setAbfNome(abfNome);
        anexoId.setBfcCodigo(bfcCodigo);

        AnexoBeneficiario anexo = beneficiarioController.findAnexoBeneficiarioByPrimaryKey(anexoId, responsavel);

        // Monta o path do arquivo
        String path = "beneficiario" + java.io.File.separatorChar + new SimpleDateFormat("yyyyMMdd").format(anexo.getAbfData()) + java.io.File.separatorChar + bfcCodigo;

        try {
            File aux = new File(ParamSist.getDiretorioRaizArquivos() + java.io.File.separatorChar + path + java.io.File.separatorChar + anexo.getAbfNome());

            if (aux.exists()) {
                aux.delete();
            }

            beneficiarioController.removeAnexoBeneficiario(anexo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.deletado.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.anexo.deletado.erro", responsavel));
        }
        model.addAttribute(Columns.BFC_CODIGO, bfcCodigo);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return "forward:/v3/listarAnexoBeneficiario?acao=listar&_skip_history_=true";
    }

    private List<String> montaListTarCodigos() {
        List<String> tarCodigos = new ArrayList<>();
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_RG.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_CPF.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_COMPROVANTE_RESIDENCIA.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_CASAMENTO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_UNIAO_ESTAVEL.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_NASCIMENTO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_MATRICULA_FREQUENCIA_ESCOLAR.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_TUTELA_CURATELA.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_CARENCIA.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ATESTADO_MEDICO.getCodigo());

        return tarCodigos;
    }
}
