package com.zetra.econsig.web.tag.v4;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ParametroTag</p>
 * <p>Description: Tag genérica para exibição dos campos de parâmetro layout v4.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParametroTag.class);

    public String prefixo = "TDAVLR_";

    /**
     * O domínio dos parâmetros são os seguintes:
     * - SN: Domínio Sim/Não, que significa S/N;
     * - INT: Números inteiros de 4 dígitos no máximo;
     * - FLOAT / MONETARIO: Números decimais, onde o monetário é limitado a 2 casas decimais;
     * - DATA: DD/DD/DDDD;
     * - ANOMES: DDDDDD;
     * - MES: DD (entre 1 e 12);
     * - DIA: DD (entre 1 e 31);
     * - ALFA: Texto qualquer;
     *
     * - ESCOLHA/SELECAO: campos de escolha única, onde o primeiro é "radiobox" e o segundo um "selectbox", ex:
     *        ESCOLHA[1;2;3;4]
     *        ESCOLHA[1=Fraco;2=Médio;3=Forte;4=Muito Forte]
     *        ESCOLHA[1=SIM;0=NÃO]
     *        SELECAO[D=Dia do Mês;S=Dia da Semana;U=Dias Úteis]
     *
     */

    // Codigo do Parâmetro de Serviço
    protected String codigo;
    // Descrição do Parâmetro de Serviço
    protected String descricao;
    // Chave do ApplicationResources para a descrição do parâmetro de Serviço
    protected String chaveDescricao;
    // Domínio do parâmetro de Serviço
    protected String dominio;
    // Valor atual do parâmetro de Serviço
    protected String valor;
    // Valor default do parâmetro de Serviço, caso o atual seja nulo
    protected String valorPadrao;
    // O valor original do parâmetro para os casos de renegociação e alongamento
    protected String valorOriginal;
    //  tamanho do campo na tela
    protected int size;
    //  quantidade máxima de caracteres
    protected int maxSize;
    // Indica se está desabilitado o campo
    protected boolean desabilitado;
    // Indica se quando o campo está desabilitado deve ser exibido o campo ou somente o conteúdo
    protected boolean omiteCampoDesabilitado;
    // Evento ao ser clicado
    protected String onClick;
    // Indica se quando o campo está true a formatação tem que ser da classe dt não div
    protected boolean dte;

    public boolean isDte() {
        return dte;
    }

    public void setDte(boolean dte) {
        this.dte = dte;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setChaveDescricao(String chaveDescricao) {
        this.chaveDescricao = chaveDescricao;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setValorPadrao(String valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public void setDesabilitado(boolean desabilitado) {
        this.desabilitado = desabilitado;
    }

    public void setOmiteCampoDesabilitado(boolean omiteCampoDesabilitado) {
        this.omiteCampoDesabilitado = omiteCampoDesabilitado;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setPrefixo(String prefixo) {
        if (prefixo == null || !prefixo.endsWith("_")) {
            throw new UnsupportedOperationException("o campo prefixo necessita ter o caracter \"_\" no final.");
        }
        this.prefixo = prefixo;
    }

    public String getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(String valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (!TextHelper.isNull(chaveDescricao)) {
                descricao = ApplicationResourcesHelper.getMessage(chaveDescricao, responsavel);
            }

            // Gera o resultado
            pageContext.getOut().print(geraHtml());

            return EVAL_PAGE;

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String geraHtml() {
        StringBuilder code = new StringBuilder();
        if (desabilitado && omiteCampoDesabilitado) {
            code.append("<dt class=\"col-6\">").append(descricao).append(":</dt>");
            code.append("<dd class=\"col-6\">").append(geraCampoParametro()).append("</dd>");
        } else if (isDte()) {
            code.append("<dt class=\"col-6\">");
            code.append("<label for=\"").append(TextHelper.forHtmlAttribute(prefixo + codigo)).append("\"").append(" >");
            code.append(descricao).append("</label>");
            code.append(":</dt>");
            code.append("<dd class=\"col-6\">").append(geraCampoParametro()).append("</dd>");
        } else {
            boolean isRadio = !TextHelper.isNull(dominio) && dominio.equals("SN");
            code.append("<div class=\"row\">");

            if (isRadio) {
                code.append("<div class=\"col-md-6 mb-3\" role=\"radiogroup\" aria-labelledby=\"").append(prefixo + codigo).append("\">");
                code.append("<div class=\"form-group my-0\">");
                code.append("<span id=\"").append(prefixo + codigo).append("\">").append(descricao).append("</span>");
                code.append("</div>");
                code.append("<div class=\"form-check mt-2\">");

            } else {
                code.append("<div class=\"form-group col-sm-6\">");
                code.append("<label for=\"").append(TextHelper.forHtmlAttribute(prefixo + codigo)).append("\"").append(" >");
                code.append(descricao).append("</label>");
            }

            code.append(geraCampoParametro());
            if (isRadio) {
                code.append("</div>");
            }
            code.append("</div>");
            code.append("</div>");
        }
        return code.toString();
    }

    private String geraCampoParametro() {
        return geraCampoParametroSimples(dominio, prefixo + codigo, valor, valorPadrao, valorOriginal, desabilitado, omiteCampoDesabilitado, onClick, size, maxSize, "form-control", descricao);
    }

    private static String geraCampoParametroSimples(String dominio, String nome, String valor, String valorPadrao, String valorOriginal, boolean desabilitado, boolean omiteCampoDesabilitado, String onClick, int size, int maxSize, String cssClass, String placeHolder) {
        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        if (!TextHelper.isNull(dominio)) {

            boolean dominioSN = false;
            if (dominio.equals("SN")) {
                // SN: Domínio Sim/Não, que significa S/N no caso de parâmetros gerais
                dominioSN = true;
            }

            if (!TextHelper.isNull(valor)) {
                // Verifica se o valor informado está no formato esperado
                try {
                    if (dominio.equals("INT")) {
                        Long.parseLong(valor.trim());
                    } else if (dominio.equals("FLOAT") || dominio.equals("MONETARIO")) {
                        Double.parseDouble(valor.trim());
                    } else if (dominio.equals("DIA") || dominio.equals("MES")) {
                        Short.parseShort(valor.trim());
                    }
                } catch (NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    valor = "";
                }
            }

            if (TextHelper.isNull(valor) && !TextHelper.isNull(valorPadrao)) {
                // Se o valor é nulo, verifica se o padrão não é para assumí-lo
                valor = valorPadrao;
            }

            //Se o valor continua nulo, setamos como vazio.
            if (TextHelper.isNull(valor)) {
                valor = "";
            }

            // Muda o padrão do tamanho para ficar melhor nas páginas
            if (dominio.equals("ALFA")) {
                size = (size<=0) ? 40 : size;
            }

            // Executa a criação do campo HTML para edição do parâmetro
            code.append("\n");
            if (desabilitado && omiteCampoDesabilitado) {
                if (dominioSN) {
                    code.append(valor.equals("S") ? ApplicationResourcesHelper.getMessage("rotulo.sim", null) : ApplicationResourcesHelper.getMessage("rotulo.nao", null));
                } else {
                    code.append(TextHelper.forHtmlContent(valor));
                }
                code.append("<input type=\"hidden\"  name=\"").append(TextHelper.forHtmlAttribute(nome));
                code.append("\" value=\"").append(TextHelper.forHtmlAttribute(valor)).append("\"/>");
            } else if (TextHelper.isNull(cssClass)) {
                code.append(JspHelper.montaValor(TextHelper.forHtmlAttribute(nome), dominio, TextHelper.forHtmlAttribute(valor), !desabilitado, onClick, size, maxSize));
            } else {
                code.append(JspHelper.montaValor(TextHelper.forHtmlAttribute(nome), dominio, TextHelper.forHtmlAttribute(valor), !desabilitado, onClick, size, maxSize, cssClass, placeHolder, placeHolder));
            }
            if (!TextHelper.isNull(valorOriginal)) {
                code.append("<span class=\"rotulo\">&nbsp;" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.atual", null)).append(": </span> ");
                if (dominioSN) {
                    code.append(valorOriginal.equals("S") ? ApplicationResourcesHelper.getMessage("rotulo.sim", null) : ApplicationResourcesHelper.getMessage("rotulo.nao", null));
                } else {
                    code.append(TextHelper.forHtmlContent(valorOriginal));
                }
            }
            code.append("\n");
        }

        return code.toString();
    }
}
