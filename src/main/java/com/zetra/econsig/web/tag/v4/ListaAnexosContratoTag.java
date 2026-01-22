package com.zetra.econsig.web.tag.v4;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaAnexosContratoTag</p>
 * <p>Description: Tag para listagem dos anexos de uma consignação para leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAnexosContratoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaAnexosContratoTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Nome do atributo que contém os dados dao consignação
    private String name;
    // Escopo do atributo que contém os dados da consignação
    private String scope;
    // Tipo de operação (consultar/alterar/...)
    private String type;
    // Se tem ação na listagem
    private boolean temAcao = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setTemAcao(boolean temAcao) {
        this.temAcao = temAcao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            // Obtém a lista com os anexos da consignação
            List<TransferObject> anexos = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (anexos != null && anexos.size() > 0) {
                if (table) {
                    code.append(abrirTabela(responsavel));
                }

                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");

                Iterator<TransferObject> it = anexos.iterator();
                TransferObject anexo = null;
                String adeCodigo, aadData, aadResponsavel, aadNome, aadDescricao, dataReserva, tamanho, aadPeriodo;
                boolean aadAtivo;
                String cssLinha = "Li";
                while (it.hasNext()) {
                    anexo = it.next();

                    anexo = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) anexo, null, responsavel);

                    adeCodigo = anexo.getAttribute(Columns.ADE_CODIGO).toString();
                    aadData = DateHelper.toDateTimeString((Date) anexo.getAttribute(Columns.AAD_DATA));
                    aadResponsavel = anexo.getAttribute(Columns.USU_LOGIN).toString();
                    aadNome = anexo.getAttribute(Columns.AAD_NOME).toString();
                    aadDescricao = anexo.getAttribute(Columns.AAD_DESCRICAO).toString();
                    aadAtivo = ((Short) anexo.getAttribute(Columns.AAD_ATIVO)).equals(CodedValues.STS_ATIVO);
                    aadPeriodo = DateHelper.toDateString((Date) anexo.getAttribute(Columns.AAD_PERIODO));

                    dataReserva = DateHelper.format((Date) anexo.getAttribute(Columns.ADE_DATA), "yyyyMMdd");
                    tamanho = getTamanhoArquivoAnexo(adeCodigo, aadNome, dataReserva, responsavel);

                    code.append(montarLinhaLista(dataReserva, adeCodigo, aadData, aadResponsavel, aadNome, aadDescricao, aadPeriodo, tamanho, aadAtivo, cssLinha, request, responsavel));
                    cssLinha = (cssLinha.equalsIgnoreCase("Li") ? "Lp" : "Li");
                }

                code.append("</tbody>");
                if (table) {
                    code.append(fecharTabela(responsavel));
                }

            } else if (type != null && type.equalsIgnoreCase("alterar")) {
                if (table) {
                    code.append(abrirTabela(responsavel));
                }
                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");
                code.append(montarLinhaResultadoVazio(responsavel));
                code.append("</tbody>");
                if (table) {
                    code.append(fecharTabela(responsavel));
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String abrirTabela(AcessoSistema responsavel) {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    private String fecharTabela(AcessoSistema responsavel) {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String subTitulo = (String) request.getAttribute("_paginacaoSubTitulo");

        String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.consignacao.lista.anexos.consignacao", responsavel);
        String tfoot = "";

        if (subTitulo != null) {
            tfoot = "<tfoot>\n" +
                    "  <tr><td colspan=\"5\">"+textoRodape+" - <span class=\"font-italic\">"+subTitulo+"</span></td></tr>"+
                    "</tfoot>\n" +
                    "</table>\n";
        } else {
            tfoot = "<tfoot>\n" +
                    "  <tr><td colspan=\"5\">"+textoRodape+"</td></tr>"+
                    "</tfoot>\n" +
                    "</table>\n";
        }
        return tfoot;
    }

    private String montarCabecalho(AcessoSistema responsavel) {
        String cabecalhoPadrao = "";

        if(temAcao) {
            cabecalhoPadrao = "<thead>\n" +
                    "  <tr class=\"selecionarLinha\">\n" +
                    (((!responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO)) || (responsavel.isSer() && (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) || responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)))) ?
					"	 <th scope=\"col\" width=\"3%\" class=\"colunaUnica\" style=\"display: none;\">" +
                    " 		<div class=\"form-check\">" +
                    "  			<input type=\"checkbox\" class=\"form-check-input ml-0\" name=\"checkAll_chkADE\" id=\"checkAll_chkADE\" data-bs-toggle=\"tooltip\" data-original-title=\"\" alt=\"\" title=\"\">" +
                    "   	</div>" +
                    "	 </th> " : "" ) +
                    "    <th id=\"dataAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.data.anexo", responsavel) + "</th>\n" +
                    "    <th id=\"responsavelAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.responsavel", responsavel) + "</th>\n" +
                    "    <th id=\"arquivoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.nome", responsavel) + "</th>\n" +
                    "    <th id=\"descricaoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.descricao", responsavel) + "</th>\n" +
                    "    <th id=\"periodoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.periodo", responsavel) + "</th>\n" +
                    "    <th id= \"situacao\">"+ApplicationResourcesHelper.getMessage("rotulo.situacao",responsavel) + "</th>\n"+
                    "    <th id=\"acao\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel) + "</th>\n" +
                    "  </tr>\n" +
                    "</thead>\n" +
                    "<tbody>\n"
                    ;
        }else {
            cabecalhoPadrao = "<thead>\n" +
                                    "  <tr>\n" +
                                    "    <th id=\"dataAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.data.anexo", responsavel) + "</th>\n" +
                                    "    <th id=\"responsavelAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.responsavel", responsavel) + "</th>\n" +
                                    "    <th id=\"descricaoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.descricao", responsavel) + "</th>\n" +
                                    "    <th id=\"periodoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.periodo", responsavel) + "</th>\n" +
                                    "    <th id=\"arquivoAnexo\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.nome", responsavel) + "</th>\n" +
                                    "  </tr>\n" +
                                    "</thead>\n"
                                    ;

        }

        return cabecalhoPadrao;
    }

    private String montarLinhaLista(String dataReserva, String adeCodigo, String aadData, String aadResponsavel, String aadNome, String aadDescricao, String aadPeriodo, String tamanho, boolean aadAtivo, String cssLinha, HttpServletRequest request, AcessoSistema responsavel) {
        String acaoFormulario = (String) request.getAttribute("acaoFormulario");
        String bloquear = SynchronizerToken.updateTokenInURL(acaoFormulario + "?acao=" + (aadAtivo ? "bloquear" : "ativar") + "&NOME_ARQ=" + TextHelper.forJavaScriptAttribute(aadNome) + "&ADE_CODIGO=" + TextHelper.forJavaScriptAttribute(adeCodigo) + "&_skip_history_=true", request);
        String linhaLista = "";

        // Replicar as alterações do POST do download para as duas condicionais
        if (temAcao) {
            linhaLista = " <tr>\n" +
            		(((!responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO)) || (responsavel.isSer() && (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) || responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)))) ?
            		"    <td class=\"colunaUnica\" aria-label=\"\" title=\"\" data-bs-toggle=\"tooltip\" data-original-title=\"\" style=\"display: none;\"> " +
            		"    	<div class=\"form-check\"> " +
                    "    		<input type=\"checkbox\" class=\"form-check-input ml-0\" name=\"chkAadNome\" value=\""+ TextHelper.forHtmlAttribute(aadNome) +"\" onclick =\"escolhechk('" + TextHelper.forJavaScriptAttribute(aadNome) +"')\"> " +
                    "  		</div> " +
                	"    </td> " : "" ) +
                    "    <td header=\"dataAnexo\">" + TextHelper.forHtmlContent(aadData) + "</td>\n" +
                    "    <td header=\"responsavelAnexo\">" + TextHelper.forHtmlContent(aadResponsavel) + "</td>\n" +
                    "    <td class=\"text-nowrap\" header=\"arquivoAnexo\" id=\"nomeAnexoContrato\">\n" +TextHelper.forHtmlContent(aadNome) + "</td>\n" +
                    "    <td header=\"descricaoAnexo\">" + TextHelper.forHtmlContent(aadDescricao) + "</td>\n" +
                    "    <td header=\"periodoAnexo\">" + TextHelper.forHtmlContent(aadPeriodo) + "</td>\n" +
                    "    <td " +(aadAtivo ?  "\"\""  : "class=\"block\"")+">"+(aadAtivo ? ""+ApplicationResourcesHelper.getMessage("rotulo.anexo.desbloqueado",responsavel)+"" : ""+ApplicationResourcesHelper.getMessage("rotulo.anexo.bloqueado",responsavel)+"") +"</td>"+
                    "    <td class=\"acoes\">"+
                    "     <div class=\"actions\">"+
                    "        <div class=\"dropdown\">" +
                    "           <a class=\"dropdown-toggle ico-action\" href=\"#\" role=\"button\" id=\"userMenu\" data-bs-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                    "           <div class=\"form-inline\">" +
                    "				<span class=\"mr-1\" data-bs-toggle=\"tooltip\" title=\"\" data-original-title=\""+ApplicationResourcesHelper.getMessage("rotulo.mais.acoes",responsavel)+"\" aria-label=\""+ApplicationResourcesHelper.getMessage("rotulo.mais.acoes",responsavel)+"\"> <svg>" +
					"					<svg> <use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#i-engrenagem\"></use></svg> " +
					"				</span>" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes",responsavel)+"" +
                    "            </div>" +
                    "        </a>" +
                    "        <div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"userMenu\" x-placement=\"bottom-end\" style=\"position: absolute; transform: translate3d(76px, 19px, 0px); top: 0px; left: 0px; will-change: transform;\">" +
                    "           <a class=\"dropdown-item\" href=\"#\" data-bs-toggle=\"modal\" onclick=\"show_descricao('" + TextHelper.forJavaScriptAttribute(aadNome) +"','" + TextHelper.forJavaScriptAttribute(aadDescricao) + "')\" >"+ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel)+"</a>"+
                    "           <a class=\"dropdown-item\" href=\"#\" onClick=\"postData('" + TextHelper.forJavaScriptAttribute(bloquear) + "')\">"+(aadAtivo ? ""+ApplicationResourcesHelper.getMessage("rotulo.acoes.bloquear",responsavel)+"" : ""+ApplicationResourcesHelper.getMessage("rotulo.acoes.desbloquear",responsavel)+"")+"</a>" +
                    (((!responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO)) || (responsavel.isSer() && (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) || responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)))) ? "           <a class=\"dropdown-item\" href=\"#\" onClick=\"excluir_anexo('" + TextHelper.forJavaScriptAttribute(aadNome) + "')\">"+ApplicationResourcesHelper.getMessage("rotulo.acoes.remover",responsavel)+"</a> " : "" ) +
                    "           <a class=\"dropdown-item\" href=\"#\" onClick=\"verificarDownload('" + adeCodigo + "', '" + aadNome + "', '" + dataReserva + "');\">"+ApplicationResourcesHelper.getMessage("rotulo.acoes.download", responsavel)+"</a>" +
                    (((!responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO)) || (responsavel.isSer() && (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) || responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)))) ? "    	   <a class=\"dropdown-item\" style=\"cursor:pointer\" onclick =\"escolhechk('" + TextHelper.forJavaScriptAttribute(aadNome) +"','Selecionar',this)\">"+ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar",responsavel)+"</a>" : "")+
                    "         </div>" +
                    "       </div>"+
                    "     </div>"+
                    "    </td>"+
                    "  </tr>" +
                    "</tbody>\n"
                    ;

        } else {
            linhaLista = " <tr>\n" +
                    "    <td header=\"dataAnexo\">" + TextHelper.forHtmlContent(aadData) + "</td>\n" +
                    "    <td header=\"responsavelAnexo\">" + TextHelper.forHtmlContent(aadResponsavel) + "</td>\n" +
                    "    <td header=\"descricaoAnexo\">" + TextHelper.forHtmlContent(aadDescricao) + "</td>\n" +
                    "    <td header=\"periodoAnexo\">" + TextHelper.forHtmlContent(aadPeriodo) + "</td>\n" +
                    "    <td class=\"text-nowrap\" header=\"arquivoAnexo\" id=\"nomeAnexoContrato\">\n" +
                    "      <a href=\"#no-back\" onClick=\"verificarDownload('" + adeCodigo + "', '" + aadNome + "', '" + dataReserva + "');\">\n" +
                    TextHelper.forHtmlContent(aadNome)+
                    "      </a>\n" +
                    "    </td>\n" +
                    "  </tr>\n"
                    ;
        }

        return linhaLista;
    }

    private String montarLinhaResultadoVazio(AcessoSistema responsavel) {
        return "  <tr>\n" +
               "    <td class=\"sem-registro\" colspan=\"100%\">&nbsp;" + ApplicationResourcesHelper.getMessage("mensagem.anexo.erro.nenhum.registro", responsavel) + "</td>\n" +
               "  </tr>\n"
               ;
    }

    private static String getTamanhoArquivoAnexo(String adeCodigo, String aadNome, String dataReserva, AcessoSistema responsavel) {
        String tamanho = "";
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String nomeArqAnexo = absolutePath + File.separatorChar + "anexo" + File.separatorChar + dataReserva + File.separatorChar + adeCodigo + File.separatorChar + aadNome;
        File arquivoAnexo = new File(nomeArqAnexo);
        if (arquivoAnexo.exists() && arquivoAnexo.canRead()) {
            if (arquivoAnexo.length() > 1024.00) {
                tamanho = Math.round(arquivoAnexo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
            } else {
                tamanho = arquivoAnexo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
            }
        }
        return tamanho;
    }
}
