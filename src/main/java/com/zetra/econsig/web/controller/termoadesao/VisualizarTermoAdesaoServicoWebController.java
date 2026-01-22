package com.zetra.econsig.web.controller.termoadesao;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VisualizarTermoAdesaoServicoWebController</p>
 * <p>Description: Controlador Web para o caso de uso termo de adesão de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/visualizarTermoAdesaoServico" }, method = { RequestMethod.POST })
public class VisualizarTermoAdesaoServicoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarTermoAdesaoServicoWebController.class);

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            SynchronizerToken.saveToken(request);
            final String msgTermoAdesao = gerarTermoAdesao(responsavel, request, session, model);
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.de.adesao.titulo", responsavel));
            model.addAttribute("msgTermoAdesao", msgTermoAdesao);
            model.addAttribute("termoAdesaoBeneficio", Boolean.TRUE);


        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/visualizarTermoAdesaoServico/visualizarTermoAdesao", request, session, model, responsavel);
    }

    private String gerarTermoAdesao(AcessoSistema responsavel, HttpServletRequest request, HttpSession session, Model model) throws Exception {

        final String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));

        final String serCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO));

        final String termoAdesao = "termo_adesao_beneficio_v4.msg";
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "termo_de_adesao" + File.separatorChar + termoAdesao;

        String msgTermoAdesao = FileHelper.readAll(absolutePath);

        if (msgTermoAdesao == null) {
            throw new ZetraException("mensagem.erro.beneficio.termoadesao.arquivo.nao.encontrado", responsavel);
        }

        final Map<String, String> chavesValores = new HashMap<>();

        chavesValores.put("<@nome_usuario>", responsavel.getUsuNome());

        final String perfil = usuarioController.findUsuarioPerfil(responsavel.getUsuCodigo(), responsavel);
        chavesValores.put("<@perfil_usuario>", perfil);

        final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, serCodigo, false, false, responsavel);

        final String orgNome = (String) servidor.getAttribute(Columns.ORG_NOME);
        chavesValores.put("<@orgao>", orgNome);

        final String orgCnpj = (String) servidor.getAttribute(Columns.ORG_CNPJ);
        chavesValores.put("<@cnpj>", orgCnpj);

        final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
        chavesValores.put("<@nome_servidor>", serNome);

        final String cpf = (String) servidor.getAttribute(Columns.SER_CPF);
        chavesValores.put("<@cpf_servidor>", cpf);

        final String matricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
        chavesValores.put("<@matricula_servidor>", matricula);

        final String tabelaBeneficios = gerarTabelaBeneficios(rseCodigo, responsavel, servidor, request, session, model);
        chavesValores.put("<@tabela_titular_dependentes>", tabelaBeneficios);

        msgTermoAdesao = replaceChaves(msgTermoAdesao, chavesValores);

        return msgTermoAdesao;

    }

    private String gerarTabelaBeneficios(String rseCodigo, AcessoSistema responsavel, CustomTransferObject servidor, HttpServletRequest request, HttpSession session, Model model) throws Exception {
        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("contratosAtivos", "true");
        if (responsavel.isSer()) {
            criterio.setAttribute(Columns.SER_CODIGO, responsavel.getSerCodigo());
            criterio.setAttribute("isFluxoServidor", true);
        } else {
            criterio.setAttribute("isFluxoServidor", false);
            criterio.setAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
        }

        final List<TransferObject> relacaoBeneficios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

        final StringBuilder builder = new StringBuilder();

        for (final TransferObject relacaoBeneficio : relacaoBeneficios) {

            final Object benCodigo = relacaoBeneficio.getAttribute(Columns.BEN_CODIGO);

            final String benDescricao = (String) relacaoBeneficio.getAttribute(Columns.BEN_DESCRICAO);
            builder.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.beneficiocontratado", responsavel)).append(":</b> ").append(benDescricao).append("  ");
            final String csaNome = (String) relacaoBeneficio.getAttribute(Columns.CSA_NOME);
            builder.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.operadora", responsavel)).append(":</b> ").append(csaNome).append("  ");
            final String nseDescricao = (String) relacaoBeneficio.getAttribute(Columns.NSE_DESCRICAO);
            builder.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.plano", responsavel)).append(":</b> ").append(nseDescricao).append("  ");
            builder.append("<br>");
            builder.append("<table class=\"tabela_adesao\">");
            builder.append("	<thead>");
            builder.append("		<tr>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.nome", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.cpf", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.grauparentesco", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.datanascimento", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.valorplano", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.valorsubsidio", responsavel)).append("</th>");
            builder.append("			<th>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.valorpago", responsavel)).append("</th>");
            builder.append("		</tr>");
            builder.append("	</thead>");
            builder.append("	<tbody>");

            criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);
            final List<TransferObject> relacaoBeneficiosCompleto = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            double valorTotal = 0;

            final String moeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
            for (final TransferObject relacaoBeneficioCompleto : relacaoBeneficiosCompleto) {

                builder.append("	<tr>");

                final String bfcNome = (String) relacaoBeneficioCompleto.getAttribute(Columns.BFC_NOME);
                builder.append("<td>").append(bfcNome).append("</td>");

                final String bfcCpf = (String) relacaoBeneficioCompleto.getAttribute(Columns.BFC_CPF);
                builder.append("<td>").append(bfcCpf).append("</td>");

                final String tibCodigo = (String) relacaoBeneficioCompleto.getAttribute(Columns.TIB_CODIGO);

                if (CodedValues.TIB_TITULAR.equals(tibCodigo)) {
                    builder.append("<td>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.titular", responsavel)).append("</td>");
                } else {
                    final String grpDescricao = (String) relacaoBeneficioCompleto.getAttribute(Columns.GRP_DESCRICAO);
                    builder.append("<td>").append(grpDescricao != null ? grpDescricao : "").append("</td>");
                }

                final String bfcDataNascimento = DateHelper.format((Date) relacaoBeneficioCompleto.getAttribute(Columns.BFC_DATA_NASCIMENTO), "dd/MM/yyyy");
                builder.append("<td>").append(bfcDataNascimento).append("</td>");

                final String valorMensalidade = NumberHelper.format(((BigDecimal) relacaoBeneficioCompleto.getAttribute(Columns.CBE_VALOR_TOTAL)).doubleValue(), NumberHelper.getLang());
                builder.append("<td>").append(moeda).append(" ").append(valorMensalidade).append("</td>");

                final String valorSubsidio = NumberHelper.format(((BigDecimal) relacaoBeneficioCompleto.getAttribute(Columns.CBE_VALOR_SUBSIDIO)).doubleValue(), NumberHelper.getLang());
                builder.append("<td>").append(moeda).append(" ").append(valorSubsidio).append("</td>");

                final double adeVlr = ((BigDecimal) relacaoBeneficioCompleto.getAttribute(Columns.ADE_VLR)).doubleValue();
                final String valorFinal = NumberHelper.format(adeVlr, NumberHelper.getLang());
                builder.append("<td>").append(moeda).append(" ").append(valorFinal).append("</td>");

                valorTotal += adeVlr;

                builder.append("	</tr>");

            }

            builder.append("	<tr>");
            builder.append("<td colspan=\"6\">");
            builder.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.termoadesao.valortotal", responsavel)).append("</b>");
            builder.append("</td>");
            builder.append("<td>");
            builder.append(moeda).append(" ");
            builder.append(NumberHelper.format(valorTotal, NumberHelper.getLang()));
            builder.append("</td>");
            builder.append("	</tr>");
            builder.append("	</tbody>");
            builder.append("</table>");
            builder.append("<br><br>");

        }

        return builder.toString();
    }

    private String replaceChaves(String msgTermoAdesao, Map<String, String> chavesValores) {

        final Set<Entry<String, String>> entrySet = chavesValores.entrySet();

        for (final Entry<String, String> entry : entrySet) {
            final String value = entry.getValue();
            if (value != null) {
                msgTermoAdesao = msgTermoAdesao.replaceAll(entry.getKey(), Matcher.quoteReplacement(value));
            } else {
                msgTermoAdesao = msgTermoAdesao.replaceAll(entry.getKey(), "");
            }
        }

        return msgTermoAdesao;
    }
}
