package com.zetra.econsig.helper.email.command;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ModeloEmail;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AbstractEnviarEmailCommand</p>
 * <p>Description: classe command abstrata para EnviarEmail.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractEnviarEmailCommand {

    private static final String MENSAGEM_ERRO_INTERNO_SISTEMA = "mensagem.erroInternoSistema";
    protected AcessoSistema responsavel;

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    /**
     * Método abstrato que as classes filhas deverão implementar para efetivamente executar o envio do e-mail.
     * @throws ViewHelperException
     */
    public abstract void execute() throws ViewHelperException;

    /**
     * Cria um interpolador com o template cujo código foi passado por parâmetro.
     * @param modeloEmailEnum
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    protected ModeloEmailInterpolator getModeloEmailInterpolator(ModeloEmailEnum modeloEmailEnum, AcessoSistema responsavel) throws ViewHelperException {
        try {
            ModeloEmail modeloEmail = null;
            // 1. Busca o template do e-mail
            if (modeloEmailEnum != null) {
                SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
                modeloEmail = sistemaController.findModeloEmail(modeloEmailEnum.getCodigo(), responsavel);
            }
            if (modeloEmail == null) {
                throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
            }
            return new ModeloEmailInterpolator(modeloEmail, responsavel);

        } catch (ConsignanteControllerException e) {
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    /**
     * Adiciona o valor formatado do campo à lista de valores disponíveis.
     * Adicona também o HTML formatado com o rótulo e o valor como alterantiva.
     *
     * @param origem      De onde buscar o valor
     * @param destino     Onde colocar o valor
     * @param nomeDoCampo Qual campo (coluna) usar.
     * @param mapping     Se há um mapeamento a ser usado.
     */
    protected void addValue(TransferObject origem, TransferObject destino, String nomeDoCampo, Map<Object, String> mapping) {
        String valorNoEscape = "";
        Object value = origem.getAttribute(nomeDoCampo);
        if (value != null) {
            if (mapping != null) {
                value = mapping.get(value);
                valorNoEscape = (value == null) ? "" : value.toString();
            } else if (Date.class.isAssignableFrom(value.getClass())) {
                valorNoEscape = DateHelper.format((Date)value, LocaleHelper.getDatePattern());
            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                value = NumberHelper.format(((BigDecimal)value).doubleValue(), NumberHelper.getLang());
                valorNoEscape = (value == null) ? "" : value.toString();
            } else {
                valorNoEscape = value.toString();
            }
        }
        String chave = Columns.getColumnName(nomeDoCampo);
        String chaveNoEscape = chave + "_label_html_noescape";
        destino.setAttribute(chave, value);
        valorNoEscape = "<div class=\"item\"><span class=\"rotulo\"><b>" + Columns.getColumnLabel(nomeDoCampo) + "<span class=\"colon\"></span>:</b></span> <span class=\"valor\">"
                      + valorNoEscape
                      + "</span></div>\n";
        destino.setAttribute(chaveNoEscape, valorNoEscape);
    }
    /**
     * Obtém órgão
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    protected OrgaoTransferObject getOrgao(String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            return cseDelegate.findOrgao(orgCodigo, responsavel);
        } catch (ConsignanteControllerException e) {
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    protected void setDadosTemplateEmail(CustomTransferObject dados) throws ViewHelperException {
        if (dados == null) {
            return;
        }
        StringBuilder urlSistema = new StringBuilder((String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel));

        if (TextHelper.isNull(urlSistema.toString())) {
            throw new ViewHelperException("mensagem.log.erro.valor.incorreto.param.sistema", AcessoSistema.getAcessoUsuarioSistema(), CodedValues.TPC_LINK_ACESSO_SISTEMA);
        }

        urlSistema = !urlSistema.toString().endsWith("/") ? urlSistema.append("/") : urlSistema;
        String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

        dados.setAttribute("url_sistema", urlSistema.toString());
        dados.setAttribute("logoSistema", urlSistema.append("img/logo_sistema.png").toString());
        dados.setAttribute("logoZetra", (versaoLeiaute == null || "v4".equals(versaoLeiaute)) ? urlSistema + "img/logo_empresa_branco.png" : urlSistema + "img/econsig-logo-v5.png");
        dados.setAttribute("logoFacebook", urlSistema.append("img/logo_facebook.png").toString());
        dados.setAttribute("logoInstagram", urlSistema.append("img/logo_instagram.png").toString());
        dados.setAttribute("logoLinkedin", urlSistema.append("img/logo_linkedin.png").toString());
        dados.setAttribute("logoGooglePlay", urlSistema.append("img/logo_googleplay.png").toString());
        dados.setAttribute("logoAppleStore", urlSistema.append("img/logo_applestore.png").toString());
    }
}