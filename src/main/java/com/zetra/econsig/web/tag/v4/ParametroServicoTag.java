package com.zetra.econsig.web.tag.v4;

import java.text.ParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class ParametroServicoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParametroServicoTag.class);

    public static final String PREFIXO_PSE = "PSEVLR_";

    /**
     * O domínio dos parâmetros são os seguintes:
     * - SN: Domínio Sim/Não, que significa 1/0 no caso de parâmetros de serviço;
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
     * - Campos compostos, Vlr e VlrRef, ex:
     *        COMPOSTO{Positiva:FLOAT|Negativa:FLOAT}
     *        COMPOSTO{INT|ESCOLHA[1=Sim;0=Não]}
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
    //  tamanho do campo na tela
    protected int size;
    //  quantidade máxima de caracteres
    protected int maxSize;
    // Indica se está desabilitado o campo
    protected boolean desabilitado;
    // Evento ao ser clicado
    protected String onClick;

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

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (!TextHelper.isNull(chaveDescricao)) {
                descricao = ApplicationResourcesHelper.getMessage(chaveDescricao, responsavel);
            }
            String code = "";
            code += "<div class='row'>";
            code += geraCampoParametro(responsavel);
            code += "</div>";

            pageContext.getOut().print(code);

            return EVAL_PAGE;

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String geraCampoParametro(AcessoSistema responsavel) throws ParseException {
        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        if (!TextHelper.isNull(dominio)) {
            //abre a div da coluna//inclui o label da linha//abre a div do campo
            StringBuilder codeInternoSuperior = new StringBuilder();
            //fecha as duas divs abertas acima
            StringBuilder codeInternoInferior = new StringBuilder();

            String descricaoSemBR = descricao.replace("<br>", " ");

            if (dominio.equals("SN") || dominio.startsWith("ESCOLHA")) {
                codeInternoSuperior.append("<div class='form-group mb-1 col-sm-12 col-md-12' role='radiogroup' aria-labelledby='").append(PREFIXO_PSE + codigo).append("'>");
                codeInternoSuperior.append("  <span id='").append(PREFIXO_PSE + codigo).append("'>").append(descricaoSemBR).append("</span>");
                codeInternoSuperior.append("  <br/>");
                if(dominio.equals("SN")){
                    codeInternoSuperior.append("  <div class=\"form-check form-check-inline\">");
                    codeInternoInferior.append("  </div>");
                }
                codeInternoInferior.append("</div>");
            } else if (!dominio.startsWith("COMPOSTO") && !dominio.equals("SN") && !dominio.startsWith("ESCOLHA")) {
                //abre div
                codeInternoSuperior.append("<div class='form-group col-sm-12 col-md-4'>");
                codeInternoSuperior.append("  <label for='").append(PREFIXO_PSE + codigo).append("'>").append(descricaoSemBR).append("</label>");
                //fecha div
                codeInternoInferior.append("</div>");
            }

            if (dominio.startsWith("COMPOSTO")) {
                String codeComposto = "";
                String campo = "";
                String[] listaDominiosSimples = dominio.substring(dominio.indexOf('{') + 1, dominio.indexOf('}')).split("\\|");
                String[] valoresSimples = valor != null ? valor.split(";") : new String[listaDominiosSimples.length];

                codeComposto += "<div class=\"col-sm-12 col-md-12\">"
                             +  "<h3 class=\"legend\" id=\"" + descricaoSemBR  + "\"><span>" + descricaoSemBR + "</span> </h3>"
                             +  "<div class='form-group'>"
                             +  "<div class='row' aria-labelledby=\"" + descricaoSemBR + "\">"
                             ;

                for (int i = 0; i < listaDominiosSimples.length; i++) {
                    String dominioSimples = listaDominiosSimples[i];
                    String textoDominioSimples = "";
                    String sufixo = (i == 0 ? "_VLR" : "_VLR_REF");
                    String valorSimples = (valoresSimples.length <= i || valoresSimples[i] == null || valoresSimples[i].equals("-1") ? "" : valoresSimples[i]);
                    if (dominioSimples.indexOf(':') != -1) {
                        textoDominioSimples = listaDominiosSimples[i].substring(0, listaDominiosSimples[i].indexOf(':'));
                        textoDominioSimples.replace("<br>", "");
                        dominioSimples = listaDominiosSimples[i].substring(listaDominiosSimples[i].indexOf(':') + 1);
                        dominioSimples.replace("<br>", "");
                    }
                    codeComposto += "<div class='col-sm-12 col-md-4 mt-1 align-bottom'>";
                    if (!TextHelper.isNull(textoDominioSimples)) {
                        codeComposto += "<label for='" + PREFIXO_PSE + codigo + sufixo + "'>" + textoDominioSimples + "</label>";
                    }
                    String placeholder = !TextHelper.isNull(textoDominioSimples) ? textoDominioSimples : descricaoSemBR;
                    campo = geraCampoParametroSimples(dominioSimples, PREFIXO_PSE + codigo + sufixo, valorSimples, valorPadrao, desabilitado, onClick, size, maxSize, placeholder, responsavel);
                    campo = adicionaEventoOnBlurComposto(campo, PREFIXO_PSE + codigo);
                    codeComposto += campo;
                    codeComposto += "</div>";
                }
                codeComposto += "<input name=\"" + PREFIXO_PSE + codigo + "\" type=\"hidden\" value=\"" + valor + "\">"
                              + "</div>"
                              + "</div>"
                              + "</div>"
                        ;

                code.append(codeComposto);
            } else {
                code.append(codeInternoSuperior);
                code.append(geraCampoParametroSimples(dominio, PREFIXO_PSE + codigo, valor, valorPadrao, desabilitado, onClick, size, maxSize, descricaoSemBR, responsavel));
                code.append(codeInternoInferior);
            }
        }
        return code.toString();
    }

    private static String geraCampoParametroSimples(String dominio, String nome, String valor, String valorPadrao, boolean desabilitado, String onClick, int size, int maxSize, String descricao, AcessoSistema responsavel) throws ParseException {

        //formata o campo para v4.
        StringBuilder ret = new StringBuilder("");

        if (!TextHelper.isNull(dominio)) {
            if (TextHelper.isNull(valor) && !TextHelper.isNull(valorPadrao)) {
                // Se o valor é nulo, verifica se o padrão não é para assumí-lo
                valor = valorPadrao;
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
                    } else if (dominio.equals("SN")) {
                        if (valor.equals("S")){
                            valor = "1";
                        } else if (valor.equals("N")){
                            valor = "0";
                        }
                    }
                } catch (NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    valor = "";
                }
            }

            // SN: campo "radiobox" de escolha única entre Sim(S) e Não(N)s
            if (dominio.equals("SN")) {
                //refere-se ao campo que será montado na classe JspHelper.montaValor;
                String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", (AcessoSistema) null);
                String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", (AcessoSistema) null);

                //campo sim
                ret.append("<input name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (desabilitado ? "disabled" : "") + " type=\"radio\" value=\"1\" ");
                ret.append(" id=\"").append(TextHelper.forHtmlAttribute(nome + rotuloSim)).append("\"");
                ret.append(" class='form-check-input'");
                ret.append((valor.equals("1")) ? "checked" : "");
                ret.append((onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : "") + " onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">");
                ret.append("<label class=\"form-check-label labelSemNegrito\"");
                ret.append(" for=\"").append(TextHelper.forHtmlAttribute(nome + rotuloSim)).append("\">");
                ret.append(rotuloSim);
                ret.append("</label>");
                ret.append("</div>");
                ret.append("<div class=\"form-check form-check-inline\">");
                //campo não
                ret.append("<input name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (desabilitado ? "disabled" : "") + " type=\"radio\" value=\"0\" ");
                ret.append(" id=\"").append(TextHelper.forHtmlAttribute(nome + rotuloNao)).append("\"");
                ret.append(" class='form-check-input'");
                ret.append(((valor.equals("0")) ? "checked" : "") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : "") + " onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">");
                ret.append("<label class=\"form-check-label labelSemNegrito \"");
                ret.append(" for=\"").append(TextHelper.forHtmlAttribute(nome + rotuloNao)).append("\">");
                ret.append(rotuloNao);
                ret.append("</label>");
            }

            //Seleção/radio
            if (dominio.startsWith("ESCOLHA") || dominio.startsWith("SELECAO")) {
                if (dominio.startsWith("SELECAO")) {
                    // Abre o campo "select", com a opção vazia
                    ret.append("<select name='" + TextHelper.forHtmlAttribute(nome) + "' id='" + TextHelper.forHtmlAttribute(nome) + "' class='form-control form-select' " + (desabilitado ? "disabled" : "") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : "") + " onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">" + "<option value=\"\" " + (TextHelper.isNull(valor) ? "selected" : "") + ">" + ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", (AcessoSistema) null) + "</option>");
                }

                String[] opcoes = dominio.substring(dominio.indexOf('[') + 1, dominio.indexOf(']')).split(";");
                for (String opcoe : opcoes) {
                    String nomeOpcao = opcoe;
                    String valorOpcao = opcoe;
                    if (opcoe.indexOf('=') != -1) {
                        valorOpcao = opcoe.substring(0, opcoe.indexOf('='));
                        nomeOpcao = opcoe.substring(opcoe.indexOf('=') + 1);
                    }
                    if (dominio.startsWith("SELECAO")) {
                        ret.append("<option value=\"" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "\" " + (valor.equals(valorOpcao.trim()) ? "selected" : "") + ">" + TextHelper.forHtmlContent(nomeOpcao.trim()) + "</option>");
                    } else {
                        ret.append("<div class='form-check form-check-inline'>");
                        ret.append("<input class='form-check-input' name='" + TextHelper.forHtmlAttribute(nome) + "' " + (desabilitado ? "disabled" : ""));
                        ret.append(" id='" + TextHelper.forHtmlAttribute(nome) + "' ");
                        ret.append(" type='radio' value='" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "' " + ((valor.equals(valorOpcao.trim())) ? "checked" : ""));
                        ret.append((onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : ""));
                        ret.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">");
                        ret.append("<label class='form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top'");
                        ret.append(" for='").append(TextHelper.forHtmlAttribute(nome)).append("'>");
                        ret.append(TextHelper.forHtmlContent(nomeOpcao.trim())+"</label>");
                        ret.append("</div>");
                    }
                }
                if (dominio.startsWith("SELECAO")) {
                    // Fecha o campo "select"
                    ret.append("</select>");
                }

            }
            //campos de data, texto e numero:
            else if (!dominio.startsWith("SELECAO") && !dominio.equals("SN") && !dominio.startsWith("ESCOLHA")) {
                // Campos de tipo "text" que possuem máscara e tamanho definidos pelo domínio:
                String mask = "", maxlength = "", onblur = "fout(this);ValidaMascara(this);";
                int tamanho = size;
                int qtdMaxCaracteres = maxSize;
                String tipo = "";
                String placeholder = ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, descricao);
                if (dominio.equals("DATA")) {
                    mask = LocaleHelper.getDateJavascriptPattern();
                    size = (tamanho <= 0) ? 11 : tamanho;
                    maxlength = "10";
                    tipo = "text";
                } else if (dominio.equals("ANOMES")) {
                    mask = "DDDDDD";
                    size = (tamanho <= 0) ? 8 : tamanho;
                    maxlength = "6";
                    tipo = "text";
                } else if (dominio.equals("MES")) {
                    mask = "DD";
                    size = (tamanho <= 0) ? 2 : tamanho;
                    maxlength = "2";
                    tipo = "text";
                    onblur += "if (this.value != '' && (isNaN(this.value) || this.value<01 || this.value>12)) { alert('" + ApplicationResourcesHelper.getMessage("mensagem.intervalo.permitido.mes", (AcessoSistema) null) + "'); this.focus(); return false; }";
                } else if (dominio.equals("DIA")) {
                    mask = "DD";
                    size = (tamanho <= 0) ? 2 : tamanho;
                    maxlength = "2";
                    tipo = "text";
                    onblur += "if (this.value != '' && (isNaN(this.value) || this.value<01 || this.value>31)) { alert('" + ApplicationResourcesHelper.getMessage("mensagem.intervalo.permitido.dia", (AcessoSistema) null) + "'); this.value = ''; this.focus(); return false; }";
                } else if (dominio.equals("INT")) {
                    size = (tamanho <= 0) ? 10 : tamanho;
                    maxlength = (qtdMaxCaracteres <= 0) ? "4" : String.valueOf(qtdMaxCaracteres);
                    mask = "#D" + maxlength;
                    tipo = "number";
                } else if (dominio.equals("MONETARIO") || dominio.equals("FLOAT")) {
                    size = (tamanho <= 0) ? 10 : tamanho;
                    maxlength = (qtdMaxCaracteres <= 0) ? "20" : String.valueOf(qtdMaxCaracteres);
                    mask = "#F" + maxlength;
                    tipo = "text";
                    onblur = "if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" + onblur;
                    if (!TextHelper.isNull(valor)) {
                        try {
                            valor = NumberHelper.reformat(valor, "en", NumberHelper.getLang());
                        } catch (ParseException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                } else if (dominio.equals("ALFA")) {
                    size = (tamanho <= 0) ? 60 : tamanho;
                    maxlength = (qtdMaxCaracteres <= 0) ? "255" : String.valueOf(qtdMaxCaracteres);
                    mask = "#*" + maxlength;
                    tipo = "text";
                }

                ret.append("<input " + (desabilitado ? "disabled" : ""));
                ret.append(" type='" + TextHelper.forHtmlAttribute(tipo) + "' ");
                ret.append(" class='form-control'");
                ret.append(" name='" + TextHelper.forHtmlAttribute(nome) + "' ");
                ret.append(" id='" + TextHelper.forHtmlAttribute(nome) + "' ");
                ret.append(" value='" + TextHelper.forHtmlAttribute(valor) + "' ");
                ret.append(" size='" + TextHelper.forHtmlAttribute(size) + "' ");
                ret.append(" maxlength='" + TextHelper.forHtmlAttribute(maxlength) + "' ");
                ret.append((onClick != null ? " onClick='" + TextHelper.forJavaScriptAttribute(onClick) + "'" : ""));
                ret.append(" placeHolder='" + TextHelper.forHtmlAttribute(placeholder) + "' ");
                ret.append(" onFocus='SetarEventoMascara(this,\"" + TextHelper.forJavaScriptAttribute(mask) + "\",true);' ");
                ret.append(" onBlur=\"" + onblur + "\" ");
                ret.append(">");
            }
        }
        return ret.toString();
    }

    private static String adicionaEventoOnBlurComposto(String campo, String nomeCampo) {
        String append = "setHidden(f0." + nomeCampo + ", (getFieldValue(f0." + nomeCampo + "_VLR) != '' ? getFieldValue(f0." + nomeCampo + "_VLR) : '-1') + ';' + (getFieldValue(f0." + nomeCampo + "_VLR_REF) != '' ? getFieldValue(f0." + nomeCampo + "_VLR_REF) : '-1'));";
        return campo.replaceAll("onBlur=\"([^\"]*)\"", "onBlur=\"$1" + append + "\"");
    }
}
