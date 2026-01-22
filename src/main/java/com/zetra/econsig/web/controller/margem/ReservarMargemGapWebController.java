package com.zetra.econsig.web.controller.margem;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.GAPHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReservarMargemGapWebController</p>
 * <p>Description: Controlador Web para o casos de uso de reserva de margem GAP.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reservarMargemGap" })
public class ReservarMargemGapWebController extends ReservarMargemWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservarMargemGapWebController.class);

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=confirmarReserva" })
    public String confirmar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Parâmetros de requisição
            String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
            String adeIdentificador = JspHelper.verificaVarQryStr(request, "adeIdentificador");
            String numBanco = JspHelper.verificaVarQryStr(request, "numBanco");
            String numAgencia = JspHelper.verificaVarQryStr(request, "numAgencia");
            String numConta = JspHelper.verificaVarQryStr(request, "numConta");

            String[] incMargemGap = request.getParameterValues("incMargem");
            if (incMargemGap == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            Map<String, Integer> mapMargemGap = new HashMap<>();
            for (int i = 0; i < incMargemGap.length; i++) {
                mapMargemGap.put(incMargemGap[i], Integer.valueOf(i));
            }

            String csaNome = "";
            String csaCodigo = "";

            if (responsavel.isCseSupOrg() || responsavel.isSer()) {
                csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
                ConsignatariaTransferObject csaTO = consignatariaController.findConsignataria(csaCodigo, responsavel);
                csaNome = csaTO.getCsaIdentificador() + " - " + csaTO.getCsaNome();
            } else if (responsavel.isCsaCor()) {
                csaCodigo = responsavel.getCsaCodigo();
                if (responsavel.isCsa()) {
                    csaNome = responsavel.getNomeEntidade();
                } else {
                    csaNome = responsavel.getNomeEntidadePai();
                }
            }

            String corNome = "";
            String corCodigo = "";
            if (!responsavel.isCor()) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                if (!corCodigo.equals("")) {
                    String cor[] = corCodigo.split(";");
                    corNome = cor[1] + " - " + cor[2];
                    corCodigo = cor[0];
                }
            } else {
                corCodigo = responsavel.getCodigoEntidade();
                corNome = responsavel.getNomeEntidade();
            }

            // Verifica se as entidades não estão bloqueadas
            try {
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, true, true, true, null, null, null, null, 0, null, adeIdentificador, null, "RESERVAR", true, false, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o Convênio
            CustomTransferObject convenio = null;
            try {
                convenio = convenioController.getParamCnv(cnvCodigo, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
            String orgCodigo = convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString();
            String svcIdentificador = convenio.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
            String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO) != null ? convenio.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
            String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
            String descricao = (cnvCodVerba.length() > 0 ? cnvCodVerba : svcIdentificador) + " - " + svcDescricao;

            // Busca o servidor
            CustomTransferObject servidor = null;
            try {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            } catch (ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String estIdentificador = servidor.getAttribute(Columns.EST_IDENTIFICADOR).toString();
            String estNome = servidor.getAttribute(Columns.EST_NOME).toString();
            String orgIdentificador = servidor.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
            String orgNome = servidor.getAttribute(Columns.ORG_NOME).toString();
            String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
            String serNome = servidor.getAttribute(Columns.SER_NOME).toString();
            String ser_cpf = servidor.getAttribute(Columns.SER_CPF) != null ? servidor.getAttribute(Columns.SER_CPF).toString() : "";
            String categoria = servidor.getAttribute(Columns.RSE_TIPO) != null ? servidor.getAttribute(Columns.RSE_TIPO).toString() : "";
            String cod_cargo = servidor.getAttribute(Columns.CRS_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.CRS_IDENTIFICADOR).toString() : "";
            String cargo = servidor.getAttribute(Columns.CRS_DESCRICAO) != null ? servidor.getAttribute(Columns.CRS_DESCRICAO).toString() : "";
            String codPadrao = servidor.getAttribute(Columns.PRS_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.PRS_IDENTIFICADOR).toString() : "";
            String padrao = servidor.getAttribute(Columns.PRS_DESCRICAO) != null ? servidor.getAttribute(Columns.PRS_DESCRICAO).toString() : "";
            String codSubOrgao = servidor.getAttribute(Columns.SBO_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.SBO_IDENTIFICADOR).toString() : "";
            String subOrgao = servidor.getAttribute(Columns.SBO_DESCRICAO) != null ? servidor.getAttribute(Columns.SBO_DESCRICAO).toString() : "";
            String codUnidade = servidor.getAttribute(Columns.UNI_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.UNI_IDENTIFICADOR).toString() : "";
            String unidade = servidor.getAttribute(Columns.UNI_DESCRICAO) != null ? servidor.getAttribute(Columns.UNI_DESCRICAO).toString() : "";
            String dataAdmissao = servidor.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.reformat(servidor.getAttribute(Columns.RSE_DATA_ADMISSAO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            Integer rsePrazo = (Integer) servidor.getAttribute(Columns.RSE_PRAZO);
            String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            String rseBancoSal = servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), "0", JspHelper.ESQ) : "";
            String rseAgenciaSal = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString(), "0", JspHelper.ESQ) : "";
            String rseContaSal = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString(), "0", JspHelper.ESQ) : "";

            if (!categoria.equals("") && rsePrazo != null) {
                categoria += " - " + rsePrazo + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel);
            } else if (rsePrazo != null) {
                categoria = rsePrazo.toString() + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel);
            }

            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            Short incMargem = paramSvcCse.getTpsIncideMargem();
            boolean serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
            boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            boolean validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();
            Integer mesInicioDesconto = paramSvcCse.getTpsMesInicioDescontoGap() != null && !paramSvcCse.getTpsMesInicioDescontoGap().equals("") ? Integer.valueOf(paramSvcCse.getTpsMesInicioDescontoGap()) : null;
            boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

            if (validarDataNasc) {
                // Valida a data de nascimento do servidor de acordo com a data informada pelo usuário
                String paramDataNasc = JspHelper.verificaVarQryStr(request, "dataNasc");
                if (!paramDataNasc.equals(serDataNasc)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.dataNascNaoConfere", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Valida informações bancárias
            String msgInfBancarias = "";
            if (serInfBancariaObrigatoria) {
                // Se as informações bancárias são obrigatórias e devem ser válidas,
                // então valida as informações digitadas pelo usuário
                boolean naoConferem = (!TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(numAgencia)) || !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(numConta)));
                if (naoConferem) {
                    if (validarInfBancaria) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    } else {
                        msgInfBancarias = JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("rotulo.atencao", responsavel) + ": " + ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel), "100%", CodedValues.MSG_ALERT);
                    }
                } else {
                    msgInfBancarias = JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaCorreta", responsavel), "100%", CodedValues.MSG_INFO);
                }
            }

            // Busca as margens para o registro servidor associadas ao serviço
            List<TransferObject> lstMargem = null;
            try {
                lstMargem = GAPHelper.lstMargemReservaGap(rseCodigo, orgCodigo, incMargem, mesInicioDesconto, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (lstMargem != null && !lstMargem.isEmpty()) {
                int i = 0;
                boolean temReserva = false;
                boolean temMargemRest = false;
                String marCodigo = null;
                Iterator<TransferObject> itMargem = lstMargem.iterator();
                TransferObject margem = null;
                while (itMargem.hasNext()) {
                    margem = itMargem.next();
                    marCodigo = margem.getAttribute(Columns.MAR_CODIGO).toString();
                    temReserva = (margem.getAttribute(Columns.ADE_CODIGO) != null && !margem.getAttribute(Columns.ADE_CODIGO).equals(""));
                    temMargemRest = (margem.getAttribute(Columns.MRS_MARGEM_REST) != null && ((BigDecimal) margem.getAttribute(Columns.MRS_MARGEM_REST)).doubleValue() > 0);

                    if (!temReserva && temMargemRest) {
                        // Possível de seleção, verifica se foi selecionado
                        if (mapMargemGap.get(marCodigo) != null) {
                            // Foi selecionado, verifica se está na ordem correta
                            if (mapMargemGap.get(marCodigo).intValue() != i) {
                                // Não está na ordem certa, retorna erro para usuário
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.gap.reservar.prestacoes.ordem.crescente", responsavel, svcDescricao));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                        i++;
                    }
                }
            }

            String dirImgServidores = (ParamSist.getInstance().getParam(CodedValues.TPC_DIR_IMG_SERVIDORES, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_DIR_IMG_SERVIDORES, responsavel).toString() : "";
            String fileName = "";
            String imagem = "";
            if (dirImgServidores != null && !dirImgServidores.equals("")) {
                imagem = servidorController.buscaImgServidor(servidor.getAttribute(Columns.SER_CPF).toString(), rseCodigo, responsavel);
                fileName = dirImgServidores + File.separatorChar + imagem;

                File dir = new File(fileName);
                if ((!dir.exists() && !dir.mkdirs()) || (imagem == null || imagem.equals(""))) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.gap.erro.criacao.diretorio", responsavel, dir.getAbsolutePath()));
                }
            }

            //String usuLoginResponsavel = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel).getUsuLogin();

            model.addAttribute("estIdentificador", estIdentificador);
            model.addAttribute("estNome", estNome);
            model.addAttribute("orgIdentificador", orgIdentificador);
            model.addAttribute("orgNome", orgNome);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNome", serNome);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("adeIdentificador", adeIdentificador);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta", numConta);
            model.addAttribute("mapMargemGap", mapMargemGap);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("corNome", corNome);
            model.addAttribute("corCodigo", corCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("descricao", descricao);
            model.addAttribute("ser_cpf", ser_cpf);
            model.addAttribute("categoria", categoria);
            model.addAttribute("cod_cargo", cod_cargo);
            model.addAttribute("cargo", cargo);
            model.addAttribute("codPadrao", codPadrao);
            model.addAttribute("padrao", padrao);
            model.addAttribute("codSubOrgao", codSubOrgao);
            model.addAttribute("subOrgao", subOrgao);
            model.addAttribute("codUnidade", codUnidade);
            model.addAttribute("unidade", unidade);
            model.addAttribute("dataAdmissao", dataAdmissao);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);
            model.addAttribute("msgInfBancarias", msgInfBancarias);
            model.addAttribute("lstMargem", lstMargem);
            model.addAttribute("fileName", fileName);
            model.addAttribute("msgInfBancarias", msgInfBancarias);

            return viewRedirect("jsp/reservarMargemGap/confirmarReservaGap", request, session, model, responsavel);

        } catch (NumberFormatException | ConsignatariaControllerException | ParametroControllerException | ServidorControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void validarValoresObrigatorios(HttpServletRequest request, String rseCodigo, String csaCodigo, String orgCodigo, String cnvCodigo, String svcCodigo, Object adeValor, AcessoSistema responsavel) throws ViewHelperException {
        String[] incMargemGap = request.getParameterValues("incMargem");

        if (cnvCodigo.equals("") || csaCodigo.equals("") || rseCodigo.equals("") || svcCodigo.equals("") || incMargemGap == null || incMargemGap.length == 0) {
            throw new ViewHelperException("mensagem.erro.interno.contate.administrador", responsavel);
        }
    }

}
