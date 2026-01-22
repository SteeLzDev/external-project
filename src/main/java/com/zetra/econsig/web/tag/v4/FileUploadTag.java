package com.zetra.econsig.web.tag.v4;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: FileUploadTag</p>
 * <p>Description: Tag para inclusão de campo upload ajax v4</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FileUploadTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FileUploadTag.class);

    // classe para o div container do campo de arquivo
    private String divClassArquivo = "form-group col-sm-12";
    private String divClassDescricao = "form-group col-sm-12";

    // Nome do campo a ser usado para o arquivo
    private String nomeCampoArquivo = "FILE1";
    // Nome do campo a ser usado para a descrição do anexo AAD_DESCRICAO
    private String nomeCampoDescricao = "AAD_DESCRICAO";

    // Título a ser exibido à frente do campo de arquivo
    private String tituloCampoArquivo;
    // Título a ser exibido à frente do campo de descrição
    private String tituloCampoDescricao;

    // Upload é obrigatório?
    private boolean obrigatorio = false;
    // Exibe o campo de descrição?
    private boolean mostraCampoDescricao = true;

    // Permite upload de múltiplos arquivos?
    private boolean multiplo = true;

    // Extensões permitidas
    private String[] extensoes = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;
    // Tipo arquivo upload
    private String tipoArquivo;

    private boolean botaoVisualizarRemover = false;
    private boolean scriptOnly = false;

    public void setNomeCampoArquivo(String nomeCampoArquivo) {
        this.nomeCampoArquivo = nomeCampoArquivo;
    }

    public void setNomeCampoDescricao(String nomeCampoDescricao) {
        this.nomeCampoDescricao = nomeCampoDescricao;
    }

    public void setTituloCampoArquivo(String tituloCampoArquivo) {
        this.tituloCampoArquivo = tituloCampoArquivo;
    }

    public void setTituloCampoDescricao(String tituloCampoDescricao) {
        this.tituloCampoDescricao = tituloCampoDescricao;
    }

    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public void setMostraCampoDescricao(boolean mostraCampoDescricao) {
        this.mostraCampoDescricao = mostraCampoDescricao;
    }

    public void setExtensoes(String[] extensoes) {
        this.extensoes = extensoes;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public void setMultiplo(boolean multiplo) {
        this.multiplo = multiplo;
    }

    public boolean isBotaoVisualizarRemover() {
        return botaoVisualizarRemover;
    }

    public void setBotaoVisualizarRemover(boolean botaoVisualizarRemover) {
        this.botaoVisualizarRemover = botaoVisualizarRemover;
    }

    public void setScriptOnly(boolean scriptOnly) {
        this.scriptOnly = scriptOnly;
    }

	public void setDivClassArquivo(String divClassArquivo) {
		this.divClassArquivo = divClassArquivo;
	}

	public void setDivClassDescricao(String divClassDescricao) {
		this.divClassDescricao = divClassDescricao;
	}

    @Override
    public int doEndTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            final StringBuilder container = new StringBuilder();
            final String tamMax = (responsavel.getFunCodigo() !=null) && (!CodedValues.FUN_CONSULTAR_SERVIDOR.equals(responsavel.getFunCodigo()) && !CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR.equals(responsavel.getFunCodigo())) ? (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel) : (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_REGISTRO_SERVIDOR, responsavel);

            if (scriptOnly) {
                container.append(geraJavaScript(tamMax, responsavel));
                pageContext.getOut().print(container.toString());
                return EVAL_PAGE;
            }

            // Caso os títulos não sejam informados, obtém a descrição padrão no ApplicationResources
            if (TextHelper.isNull(tituloCampoArquivo)) {
                tituloCampoArquivo = ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel);
            }
            if (TextHelper.isNull(tituloCampoDescricao)) {
                tituloCampoDescricao = ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel);
            }

            String rotuloBotao = ApplicationResourcesHelper.getMessage(multiplo ? "mensagem.informe.arquivo.upload" : "mensagem.informe.arquivo.upload.unico", responsavel);
            String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

            container.append("<div class=\"row\">");
            container.append("<div class=\"").append(divClassArquivo).append("\">");
            container.append("<label for=\"input-").append(nomeCampoArquivo).append("\">").append(tituloCampoArquivo).append("</label>");
            container.append("<div id='paiElementUpload-input-" + nomeCampoArquivo + "' class=\"").append("form-group").append("\">");
            container.append("<div class='content-box' style='margin:0px;padding:0px' id='content-box-" + nomeCampoArquivo + "'>");
            container.append("<div class='clear' style='margin:0px;padding:0px' id='clear-" + nomeCampoArquivo + "'>");
            container.append("<div style='height:25px;position:relative;margin:0px;padding:0px' >");
            if (!"v4".equals(versaoLeiaute)) {
                container.append("<input type='button' style='position:absolute;left:0px;top:0px;width: 100%;' id='upload-btn-" + nomeCampoArquivo + "' class='btn btn-outline-danger clearfix btn-large' value='").append(rotuloBotao).append("'");
            } else {
                container.append("<input type='button' style='position:absolute;left:0px;top:0px;width: 100%;' id='upload-btn-" + nomeCampoArquivo + "' class='btn btn-primary btn-large clearfix' value='").append(rotuloBotao).append("'");
            }
            container.append(" title='").append(ApplicationResourcesHelper.getMessage("mensagem.informe.arquivo.upload.ext", responsavel, TextHelper.join(extensoes,","), tamMax)).append("'>" );
            container.append("</div>");
            container.append("<div id='errormsg-" + nomeCampoArquivo + "' class='clearfix redtext' style='padding-top:10px;padding-bottom:0px;'></div>");
            container.append("<div id='pic-progress-wrap-" + nomeCampoArquivo + "' class='progress-wrap alert alert-info pl-3 pr-3 pt-2 pb-2 mt-4 text-center'>");
            container.append("</div>");
            container.append("<div id='picbox-" + nomeCampoArquivo + "' class='clear' style='padding-top:0px;padding-bottom:10px;'></div>");
            container.append("</div>");
            container.append("</div>");
            container.append("</div>");
            container.append("</div>");
            container.append("</div>");

            if (mostraCampoDescricao) {
                container.append("<div class=\"row\">");
                container.append("<div class=\"").append(divClassDescricao).append("\">");
                container.append("<label for=\"").append(nomeCampoDescricao).append("\">").append(tituloCampoDescricao);
                if (!obrigatorio) {
                    container.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel));
                }
                container.append("</label>");
                container.append("<textarea class=\"form-control\" id=\"").append(nomeCampoDescricao).append("\" name=\"").append(nomeCampoDescricao);
                container.append("\" rows=\"6\" placeholder=\"").append(ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao.arquivo", responsavel)).append("\"></textarea>");
                container.append("</div>");
                container.append("</div>");

            }
            container.append("<input type='hidden' name='" + nomeCampoArquivo + "' id='" + nomeCampoArquivo + "' value='' >");

            // Gera o resultado
            pageContext.getOut().print(container.toString());
            return EVAL_PAGE;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String geraJavaScript(String tamMax, AcessoSistema responsavel) {
        // Caso as extensões não sejam informadas, assume o padrão
        if ((extensoes == null) || (extensoes.length == 0)) {
            extensoes = ".txt,.zip".split(",");
        }

        extensoes = UploadHelper.atualizaExtensoesPermitidas(extensoes, responsavel);
        extensoes = ((TextHelper.join(extensoes,",")).replaceAll("[.]", "")).split(",");

        final StringBuilder code = new StringBuilder();
        code.append("<script type='text/JavaScript'>");
        code.append("var novoLeiaute = \"true\";");
        code.append("</script>");
        code.append("<script type='text/JavaScript'>");
        code.append("  $('#pic-progress-wrap-" + nomeCampoArquivo + "').hide();");
        if (botaoVisualizarRemover && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
            code.append("  uploadAnexo('" + nomeCampoArquivo + "', " + tamMax + ", " + TextHelper.StringArrayToJSONArray(extensoes) + ", '" + tipoArquivo + "', " + multiplo + ", true);");
        } else {
            code.append("  uploadAnexo('" + nomeCampoArquivo + "', " + tamMax + ", " + TextHelper.StringArrayToJSONArray(extensoes) + ", '" + tipoArquivo + "', " + multiplo + ");");
        }
        code.append("  $('#content-box-" + nomeCampoArquivo + "').show();");
        code.append("</script>");


        return code.toString();
    }
}
