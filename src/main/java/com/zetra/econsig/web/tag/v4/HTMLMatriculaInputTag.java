package com.zetra.econsig.web.tag.v4;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.HTMLInputTag;

/**
 * <p>Title: HTMLMatriculaInputTag</p>
 * <p>Description: TAG HTML para construção de campos de MATRÍCULA.</p>
 * <p>Copyright: Copyright (c) 2003-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLMatriculaInputTag extends HTMLInputTag {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HTMLMatriculaInputTag.class);

    private boolean scriptOnly;
    private String description;
    private String textHelpKey;

    @Override
    public void setName(String name) {
        super.setName(name);
        super.setDi(name);
    }

    public void setTextHelpKey(String textHelpKey) {
        this.textHelpKey = textHelpKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScriptOnly(boolean scriptOnly) {
        this.scriptOnly = scriptOnly;
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        try {
            // Se servidor não possui matrícula ou a chave de FieldsPermission foi informada e está configurada
            // para não exibir o campo, então retorna resultado vazio.
            if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, CodedValues.TPC_NAO, responsavel) ||
                    (!TextHelper.isNull(configKey) && !ShowFieldHelper.showField(configKey, responsavel))) {
                return "";
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            return "";
        }

        // Quantidade mínima e máxima de dígitos da matrícula a ser informado
        int tamMinMatricula = 0;
        int tamMaxMatricula = 0;

        try {
            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
            tamMinMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
            param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel);
            tamMaxMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
        } catch (Exception ex) {
        }

        if (scriptOnly) {
            return montarScript(tamMinMatricula, tamMaxMatricula, responsavel);
        }

        // Máscara do campo de matrícula
        String maskMatricula = "#*20";
        Object matriculaNumerica = ParamSist.getInstance().getParam(CodedValues.TPC_MATRICULA_NUMERICA, responsavel);
        if ((matriculaNumerica != null) && (matriculaNumerica.equals("S"))) {
            maskMatricula = "#D20";
        }

        // Define valores padronizados para o campo MATRICULA
        if (TextHelper.isNull(name)) {
            setName("RSE_MATRICULA");
        }
        if (TextHelper.isNull(type)) {
            setType("text");
        }
        if (TextHelper.isNull(classe)) {
            setClasse("form-control");
        }
        if (TextHelper.isNull(mask)) {
            setMask(maskMatricula);
        }
        if (TextHelper.isNull(maxlength)) {
            setMaxlength(tamMaxMatricula > 0 ? String.valueOf(tamMaxMatricula) : "20");
        }

        if (TextHelper.isNull(onBlur)) {
            setOnBlur("vfRseMatricula()");
        }

        // Recupera o valor do campo pelo nome e define o valor padrão
        if (TextHelper.isNull(value)) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            setValue(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, name)));
        }
        String textoAjuda = null;
        if (!TextHelper.isNull(textHelpKey)) {
            textoAjuda = ApplicationResourcesHelper.getMessage(textHelpKey, responsavel);
        }

        // Constrói a linha da tabela contendo a descrição e o campo
        if (TextHelper.isNull(description)) {
            description = ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel);
        }
        try {
            if (!TextHelper.isNull(configKey) && ShowFieldHelper.isRequired(configKey, responsavel)) {
                description = "*" + description;
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        String campo = super.generateHtml(responsavel);
        return montarLinha(description, campo, name, textoAjuda);
    }

    protected String montarLinha(String descricao, String campo, String nomeCampo, String textoAjuda) {
        return "\n<label for=\"" + nomeCampo + "\">" + descricao + "</label>"
             + "\n" + campo;
    }

    protected String montarScript(int tamMinMatricula, int tamMaxMatricula, AcessoSistema responsavel) {

        String msgMatriculaMenor = ApplicationResourcesHelper.getMessage("mensagem.erro.matricula.tamanho.min", responsavel, String.valueOf(tamMinMatricula));
        String msgMatriculaMaior = ApplicationResourcesHelper.getMessage("mensagem.erro.matricula.tamanho.max", responsavel, String.valueOf(tamMaxMatricula));

        return "\n  <script type=\"text/JavaScript\">\n" +

               "\n  function vfRseMatricula(validaForm) {"+
               "\n" +
               "\n    if(validaForm === undefined){" +
               "\n      validaForm = false;" +
               "\n    }"+
               "\n" +
               "\n    var matriculaField = document.getElementById('RSE_MATRICULA');" +
               "\n    var matricula = matriculaField.value;" +
               "\n    var tamMinMatricula = " + tamMinMatricula + ";" +
               "\n    var tamMaxMatricula = " + tamMaxMatricula + ";" +

               "\n    if (matricula != ''){" +
               "\n       if (validaForm) {" +

               "\n          if(matricula.length < tamMinMatricula){"+
               "\n              alert('" + msgMatriculaMenor + "');" +
               "\n              if (QualNavegador() == \"NE\") {" +
               "\n                  globalvar = matriculaField;" +
               "\n                  setTimeout(\"globalvar.focus()\",0);" +
               "\n              }"+
               "\n              else"+
               "\n                  matriculaField.focus();" +
               "\n          }"+
               "\n          else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){"+
               "\n              alert('" + msgMatriculaMaior + "');" +
               "\n              if (QualNavegador() == \"NE\") {" +
               "\n                  globalvar = matriculaField;" +
               "\n                  setTimeout(\"globalvar.focus()\",0);" +
               "\n              }"+
               "\n              else"+
               "\n                  matriculaField.focus();" +
               "\n          }"+
               "\n          else{"+
               "\n              matriculaField.style.color = 'black';" +
               "\n              return true;" +
               "\n          }"+
               "\n       }"+
               "\n       else {"+
               "\n              if(matricula.length < tamMinMatricula){"+
               "\n                  matriculaField.style.color = 'red';" +
               "\n                  return false;" +
               "\n              }"+
               "\n              else if (tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){"+
               "\n                  matriculaField.style.color = 'red';" +
               "\n                  return false;" +
               "\n              }"+
               "\n              else{"+
               "\n                  matriculaField.style.color = 'black';" +
               "\n                  return true;" +
               "\n              }"+
               "\n       }"+
               "\n     }//if (matricula != '')"+
               "\n     else {"+
               "\n      matriculaField.style.color = 'black';" +
               "\n      return true;"+
               "\n     }"+
               "\n   }"+
               "\n  </script>";
    }

    @Override
    protected void clean() {
        super.clean();
        description = null;
        textHelpKey = null;
        scriptOnly = false;
    }
}
