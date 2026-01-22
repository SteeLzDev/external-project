package com.zetra.econsig.web.tag.v4;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: ConfirmarDadosServidorTag</p>
 * <p>Description: Tag para exibição de formulário para confirmação
 * dos dados pessoais do servidor, utilizado na tela de confirmação
 * de reserva e de solicitação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConfirmarDadosServidorTag extends com.zetra.econsig.web.tag.ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarDadosServidorTag.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ServidorController servidorController;

    // Código do servidor
    protected String serCodigo;
    // Código do registro servidor
    protected String rseCodigo;
    // Código da Consignatária que receberá a solicitação do servidor
    protected String csaCodigo;
    // Indica se <table></table> deve ser impresso
    protected boolean table;

    public void setSerCodigo(String serCodigo) {
        this.serCodigo = serCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpSession session = pageContext.getSession();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
            String funCodigo = responsavel.getFunCodigo();
            String paramCidade = "";
            if (!TextHelper.isNull(funCodigo) && (funCodigo.equals(CodedValues.FUN_SIM_CONSIGNACAO) || funCodigo.equals(CodedValues.FUN_SOL_EMPRESTIMO))) {
                paramCidade = "?TIPO=solicitarConsignacao";
            } else if (!TextHelper.isNull(funCodigo) && funCodigo.equals(CodedValues.FUN_SOLICITAR_LEILAO_REVERSO)) {
                paramCidade = "?TIPO=solicitarLeilaoReverso";
            } else if (!TextHelper.isNull(funCodigo) && funCodigo.equals(CodedValues.FUN_SOLICITAR_PORTABILIDADE)) {
                paramCidade = "?TIPO=solicitarPortabilidade";
            }

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, null, responsavel) ||
                ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, CodedValues.TPC_SIM, responsavel)) {

                // Busca o servidor
                ServidorTransferObject servidor = null;
                try {
                    servidor = servidorController.findServidor(serCodigo, responsavel);
                } catch (ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }

                // Busca o servidor
                RegistroServidorTO registroServidor = null;
                try {
                    registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
                } catch (ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }

                // Tag Para Geração de campos
                HTMLInputTag inputTag = new HTMLInputTag();

                if (servidor != null && registroServidor != null) {
                	code.append(abrirCard());
                	code.append(adicionarLegenda(ApplicationResourcesHelper.getMessage("rotulo.simulacao.dados.cadastrais", responsavel)));
                	code.append(abrirLinha());

                	inputTag = new HTMLInputTag();
                    // Cria os campos de entrada de dados
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NOME, responsavel)) {
                        inputTag.setName("SER_NOME");
                        inputTag.setDi("SER_NOME");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlContent(servidor.getSerNome()));
                    	inputTag.setReadonly("true");
                        String servidorNome= inputTag.generateHtml(responsavel);

                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel), "SER_NOME", servidorNome));
                    }

                	inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CPF, responsavel) && !ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                        inputTag.setName("SER_CPF");
                        inputTag.setDi("SER_CPF");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlContent(servidor.getSerCpf()));
                    	inputTag.setReadonly("true");
                        String servidorCpf= inputTag.generateHtml(responsavel);

                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel), "SER_CPF", servidorCpf));
                    }

                    code.append(fecharLinha());

                    code.append("<h3 class=\"legend\">");
                    code.append("<span>");
                    code.append(ApplicationResourcesHelper.getMessage("rotulo.endereco", responsavel));
                    code.append("</span>");
                    code.append("</h3>");

                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)) {
                    	inputTag.setName("SER_END");
                    	inputTag.setDi("SER_END");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerEnd() != null ? servidor.getSerEnd() : ""));
                        inputTag.setSize("32");
                        inputTag.setMask("#*100");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String logradouro= inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.logradouro", responsavel), "SER_END", logradouro));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)) {
                    	inputTag.setName("SER_NRO");
                    	inputTag.setDi("SER_NRO");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerNro() != null ? servidor.getSerNro() : ""));
                        inputTag.setSize("5");
                        inputTag.setMask("#*15");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String numero = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.numero", responsavel),"SER_NRO", numero, "col-sm-3"));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)) {
                        //Input do complemento.
                        inputTag.setName("SER_COMPL");
                        inputTag.setDi("SER_COMPL");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerCompl() != null ? servidor.getSerCompl() : ""));
                        inputTag.setSize("22");
                        inputTag.setMask("#*40");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String complemento = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.complemento", responsavel), "SER_COMPL", complemento, "col-sm-3"));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)) {
                    	inputTag.setName("SER_BAIRRO");
                    	inputTag.setDi("SER_BAIRRO");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerBairro() != null ? servidor.getSerBairro() : ""));
                        inputTag.setSize("32");
                        inputTag.setMask("#*40");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String bairro = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.bairro", responsavel), "SER_BAIRRO", bairro));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)) {
                        inputTag.setName("SER_CIDADE");
                        inputTag.setDi("SER_CIDADE");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerCidade() != null ? servidor.getSerCidade() : ""));
                        inputTag.setSize("32");
                        inputTag.setMask("#*40");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String cidade = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.cidade", responsavel), "SER_CIDADE", cidade));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)) {
                    	String serUf = JspHelper.geraComboUF("SER_UF", "SER_UF", TextHelper.forHtmlContent(servidor.getSerUf()), !ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel), "form-control", responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.estado", responsavel), "SER_UF", serUf));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)) {
                        inputTag.setName("SER_CEP");
                        inputTag.setDi("SER_CEP");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerCep() != null ? servidor.getSerCep() : ""));
                        inputTag.setSize(LocaleHelper.getCepSize().toString());
                        inputTag.setMask(LocaleHelper.getCepMask());
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String cep = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.endereco.cep", responsavel),"SER_CEP", cep));
                    }

                    code.append(fecharLinha());
                    code.append(adicionarLegenda(ApplicationResourcesHelper.getMessage("rotulo.simulacao.dados.complementares", responsavel)));
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();

                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
                    	inputTag.setName("SER_TEL");
                    	inputTag.setDi("SER_TEL");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerTel() != null ? servidor.getSerTel() : ""));
                        inputTag.setSize("32");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String telefone = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel), "SER_TEL", telefone));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel)) {
                    	inputTag.setName("SER_EMAIL");
                    	inputTag.setDi("SER_EMAIL");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerEmail() != null ? servidor.getSerEmail() : ""));
                        inputTag.setSize("35");
                        inputTag.setMask("#*255");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String email = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.email", responsavel), "SER_EMAIL", email));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
                        inputTag.setName("SER_CEL");
                        inputTag.setDi("SER_CEL");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerCelular()));
                        inputTag.setSize("10");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String serCelular = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.celular", responsavel), "SER_CEL", serCelular));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
                    	inputTag.setName("SER_IBAN");
                    	inputTag.setDi("SER_IBAN");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(registroServidor.getRseAgenciaSalAlternativa()));
                        inputTag.setSize("15");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String iban = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.iban", responsavel), "SER_IBAN", iban));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)) {
                    	inputTag.setName("SER_DATA_NASC");
                    	inputTag.setDi("SER_DATA_NASC");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(servidor.getSerDataNasc() != null ? TextHelper.forHtmlAttribute(DateHelper.format(servidor.getSerDataNasc(), LocaleHelper.getDatePattern())) : "");
                        inputTag.setSize("10");
                        inputTag.setMask(LocaleHelper.getDateJavascriptPattern());
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String dataNascimento = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.dataNasc", responsavel), "SER_DATA_NASC", dataNascimento));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                    	inputTag.setName("SER_SEXO");
                    	inputTag.setDi("SER_SEXO_M");
                        inputTag.setType("radio");
                        inputTag.setClasse("form-check-input ml-1");
                        inputTag.setValue("M");
                        inputTag.setChecked("M".equalsIgnoreCase(servidor.getSerSexo()) ? "true" : "false");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String serSexoM = inputTag.generateHtml(responsavel);

                        inputTag.setName("SER_SEXO");
                        inputTag.setDi("SER_SEXO_F");
                        inputTag.setType("radio");
                        inputTag.setClasse("form-check-input ml-1");
                        inputTag.setValue("F");
                        inputTag.setChecked("F".equalsIgnoreCase(servidor.getSerSexo()) ? "true" : "false");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String serSexoF = inputTag.generateHtml(responsavel);


                        code.append("<fieldset id=\"sexo\">");
                        code.append("<div class=\"form-group form-check mt-2 col-sm-6\">");
                        code.append("<div><span for=\"sexo\">").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo", responsavel)).append("</span></div>");
                        code.append("<div class=\"form-check form-check-inline\">");
                        code.append(serSexoM).append("<label class=\"form-check-label labelSemNegrito formatacao ml-1 pr-4 text-nowrap align-text-top\" for=\"SER_SEXO_M\">").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel)).append("</label>");
                        code.append("</div>");
                        code.append("<div class=\"form-check form-check-inline\">");
                        code.append(serSexoF).append("<label class=\"form-check-label labelSemNegrito formatacao ml-1 pr-4 text-nowrap align-text-top\" for=\"SER_SEXO_F\">").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel)).append("</label>");
                        code.append("</div>");
                        code.append("</div>");
                        code.append("</fieldset>");
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
                    	inputTag.setName("SER_NRO_IDT");
                    	inputTag.setDi("SER_NRO_IDT");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerNroIdt()));
                        inputTag.setSize("15");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String serNroIdt = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.cartIdentidade", responsavel), "SER_NRO_IDT", serNroIdt));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
                    	inputTag.setName("SER_DATA_IDT");
                    	inputTag.setDi("SER_DATA_IDT");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(servidor.getSerDataIdt() != null ? TextHelper.forHtmlAttribute(DateHelper.format(servidor.getSerDataIdt(), LocaleHelper.getDatePattern())) : "");
                        inputTag.setSize("10");
                        inputTag.setMask(LocaleHelper.getDateJavascriptPattern());
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String serDataIdt = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.rg.data.emissao", responsavel), "SER_DATA_IDT", serDataIdt));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
                    	inputTag.setName("SER_NACIONALIDADE");
                    	inputTag.setDi("SER_NACIONALIDADE");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerNacionalidade()));
                        inputTag.setSize("20");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String serNacionalidade = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.nacionalidade", responsavel), "SER_NACIONALIDADE", serNacionalidade));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
                    	inputTag.setName("SER_NATURALIDADE");
                    	inputTag.setDi("SER_NATURALIDADE");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(servidor.getSerCidNasc() != null ? servidor.getSerCidNasc() : ""));
                        inputTag.setSize("20");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
                        	inputTag.setReadonly("true");
                        }
                        String serNaturalidade = inputTag.generateHtml(responsavel);
                        String serUfNascimento = JspHelper.geraComboUF("SER_UF_NASCIMENTO", "SER_UF_NASCIMENTO", TextHelper.forHtmlContent(servidor.getSerUfNasc()), !ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel), "form-control", responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.naturalidade", responsavel), "SER_NATURALIDADE", serNaturalidade));
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.uf.nascimento", responsavel), "SER_UF_NASCIMENTO", serUfNascimento));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                        inputTag.setName("SER_DATA_ADMISSAO");
                        inputTag.setDi("SER_DATA_ADMISSAO");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(registroServidor.getRseDataAdmissao() != null ? TextHelper.forHtmlAttribute(DateHelper.format(registroServidor.getRseDataAdmissao(), LocaleHelper.getDatePattern())) : "");
                        inputTag.setSize("10");
                        inputTag.setMask(LocaleHelper.getDateJavascriptPattern());
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String serDataAdmissao = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.dataAdmissao", responsavel), "SER_DATA_ADMISSAO", serDataAdmissao));
                    }

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
                        inputTag.setName("SER_SALARIO");
                        inputTag.setDi("SER_SALARIO");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(registroServidor.getRseSalario() != null ? TextHelper.forHtmlAttribute(NumberHelper.format(registroServidor.getRseSalario().doubleValue(), NumberHelper.getLang())) : "");
                        inputTag.setSize("15");
                        inputTag.setMask("#*20");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String serSalario = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.salario", responsavel), "SER_SALARIO", serSalario));
                    }

                    code.append(fecharLinha());
                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    if (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_MUNICIPIO_LOTACAO, responsavel)) {
                        inputTag.setName("RSE_MUNICIPIO_LOTACAO");
                        inputTag.setDi("RSE_MUNICIPIO_LOTACAO");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setValue(TextHelper.forHtmlAttribute(!TextHelper.isNull(registroServidor.getRseMunicipioLotacao()) ? registroServidor.getRseMunicipioLotacao() : ""));
                        inputTag.setSize("20");
                        inputTag.setMask("#*40");
                        if (!ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_MUNICIPIO_LOTACAO, responsavel)) {
                            inputTag.setReadonly("true");
                        }
                        String municipoLotacao = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.servidor.municipioLotacao", responsavel), "RSE_MUNICIPIO_LOTACAO", municipoLotacao));
                    }

                    code.append(fecharLinha());

                    List<TransferObject> tdaList = (List<TransferObject>) request.getAttribute("tdaList");
                    Map<String,String> dadosAutorizacao = (Map<String,String>) request.getAttribute("dadosAutorizacao");

                    if (tdaList != null && !tdaList.isEmpty()) {
                        for (TransferObject tda : tdaList) {
                            String tdaValor = TextHelper.forHtmlContent(JspHelper.parseValor(request, null, "TDA_" + (String) tda.getAttribute(Columns.TDA_CODIGO), (String) tda.getAttribute(Columns.TDA_DOMINIO)));
                            if (TextHelper.isNull(tdaValor)) {
                                tdaValor = dadosAutorizacao != null && dadosAutorizacao.get(tda.getAttribute(Columns.TDA_CODIGO)) != null ? dadosAutorizacao.get(tda.getAttribute(Columns.TDA_CODIGO)) : "";
                            }

                            ParametroTag paramTag = new ParametroTag();
                            paramTag.setPrefixo("TDA_");
                            paramTag.setDescricao(TextHelper.forHtmlContent(tda.getAttribute(Columns.TDA_DESCRICAO)));
                            paramTag.setCodigo((String) tda.getAttribute(Columns.TDA_CODIGO));
                            paramTag.setDominio((String) tda.getAttribute(Columns.TDA_DOMINIO));
                            paramTag.setValor(tdaValor);
                            paramTag.setDesabilitado(false);
                            code.append(paramTag.geraHtml());
                        }
                    }

                    code.append(abrirLinha());

                    inputTag = new HTMLInputTag();
                    boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
                    if (exigeCodAutorizacaoSMS) {
                    	inputTag.setName("codAutorizacao");
                    	inputTag.setDi("codAutorizacao");
                        inputTag.setType("text");
                        inputTag.setClasse("form-control");
                        inputTag.setSize("8");
                        inputTag.setMask("#*100");
                        inputTag.setReadonly("false");
                        String codAutorizacao = inputTag.generateHtml(responsavel);
                        code.append(montarColuna(ApplicationResourcesHelper.getMessage("rotulo.digite.codigo.autorizacao.enviado.cel", responsavel), "codAutorizacao", codAutorizacao));
                    }

                    code.append(fecharLinha());

                    code.append(fecharCard());

                    pageContext.getOut().print(code.toString());
                    pageContext.include("../cidade/include_campo_cidade_uf_v4.jsp" + paramCidade);
                    code = new StringBuilder();
                }
            } else {
                pageContext.include("../cidade/include_campo_cidade_uf_v4.jsp" + paramCidade);
            }

            pageContext.getOut().print(code.toString());


            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String abrirLinha() {
    	return "<div class=\"row\">";
    }

    protected String fecharLinha() {
    	return "</div>";
    }

    protected String abrirCard () {
        HttpSession session = pageContext.getSession();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean leilaoReverso = (responsavel.getFunCodigo() != null &&
                (responsavel.getFunCodigo().equals(CodedValues.FUN_SOLICITAR_LEILAO_REVERSO) ||
                        responsavel.getFunCodigo().equals(CodedValues.FUN_SOLICITAR_PORTABILIDADE)));

        StringBuilder retorno = new StringBuilder();

        retorno.append("<div class=\"card\">");
        retorno.append("<div class=\"card-header hasIcon\">");
        retorno.append("<span class=\"card-header-icon\"><svg width=\"26\"><use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#i-card-id\"></use></svg></span>");
        retorno.append("<h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.dado.pessoal.plural", responsavel)).append("</h2>");
        retorno.append("</div>");
        retorno.append("<div class=\"card-body\">");

        // Se passou o código da consignatária, exibe os dados de contato
        // para o servidor poder procurar a consignatária
        if (!TextHelper.isNull(csaCodigo) && !leilaoReverso) {
            try {
                // Pega a forma de contato da consignatária
                ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                String csaTxtContato = TextHelper.forHtmlContent(consignataria.getCsaTxtContato());

                if (TextHelper.isNull(csaTxtContato)) {
                    csaTxtContato = ApplicationResourcesHelper.getMessage("mensagem.informacao.instrucoes.contato.csa.nao.cadastradas", responsavel);
                }

                retorno.append("<div class=\"alert alert-warning\" role=\"alert\">");
                retorno.append("<p>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.instrucoes.contato.csa", responsavel)).append("</p>");
                retorno.append("<p class=\"mb-0\"><em>").append(csaTxtContato).append("</em></p>");
                retorno.append("</div>");

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

    	return retorno.toString();
    }

    private String fecharCard() {
        StringBuilder retorno = new StringBuilder();
        retorno.append("</div></div>");
		return retorno.toString();
	}

    private String adicionarLegenda (String texto) {
    	StringBuilder retorno = new StringBuilder();
    	retorno.append("<h3 class=\"legend\"><span>").append(texto).append("</span></h3>");
    	return retorno.toString();
    }

    protected String montarColuna(String descricao, String id, Object valor) {
    	return montarColuna(descricao, id, valor, "col-sm-6");
    }

    protected String montarColuna(String descricao, String id, Object valor, String estilo) {
        StringBuilder retorno = new StringBuilder();

        retorno.append("<div class=\"form-group ").append(estilo).append("\">");
        retorno.append("<label for=\"").append(id).append("\">").append(descricao).append("</label>");
        retorno.append(valor);
        retorno.append("</div>");

        return retorno.toString();
    }
}
