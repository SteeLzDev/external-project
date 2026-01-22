package com.zetra.econsig.web.tag;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: HTMLPasswordTag</p>
 * <p>Description: TAG HTML para construção de campos de senha, com criptografia via JavaScript.</p>
 * <p>Copyright: Copyright (c) 2003-2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLPasswordTag extends HTMLInputTag {
    private static final long serialVersionUID = 16L;

    private String cryptedfield;
    private String cryptedPasswordFieldName;
    private String keyPairName;
    private String isSenhaServidor;

    @Override
    public void setName(String name) {
        super.setId(name);
        super.setName(name);
    }

    public void setCryptedPasswordFieldName(String cryptedPasswordFieldName) {
        this.cryptedPasswordFieldName = cryptedPasswordFieldName;
    }

    public void setCryptedfield(String cryptedField) {
        cryptedfield = cryptedField;
    }

    public void setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
    }

    public void setIsSenhaServidor(String isSenhaServidor) {
        this.isSenhaServidor = isSenhaServidor;
    }

    public String generateHtml(PageContext pageContext, AcessoSistema responsavel) {
        this.pageContext = pageContext;
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        responsavel = JspHelper.getAcessoSistema(request);
        return generateHtml(responsavel);
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        Object tamanhoMaxSenha = null;

        if (!TextHelper.isNull(isSenhaServidor) && isSenhaServidor.equals("true")) {
            tamanhoMaxSenha = !TextHelper.isNull(maxlength) ? maxlength : ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());
        } else {
            tamanhoMaxSenha = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, AcessoSistema.getAcessoUsuarioSistema());
        }

        setMaxlength(!TextHelper.isNull(tamanhoMaxSenha) ? tamanhoMaxSenha.toString() : "8");
        super.setType("password");

        // Se não tem máscara definda, define uma que aceita qualquer caractere até o limite de 40
        // de modo que seja incluído os javascript de pular campo ao pressionar Enter
        if (TextHelper.isNull(mask)) {
            super.setMask("#*40");
        }

        // Campos que podem ser alterados via SET
        if (TextHelper.isNull(name)) {
            setName("senha_aberta");
        }
        if (TextHelper.isNull(cryptedPasswordFieldName)) {
            setCryptedPasswordFieldName("cryptedPasswordFieldName");
        }
        if (TextHelper.isNull(cryptedfield)) {
            setCryptedfield("senha_criptografada");
        }

        KeyPair keyPair = null;
        if (TextHelper.isNull(keyPairName)) {
            keyPair = LoginHelper.getRSAKeyPair((HttpServletRequest) pageContext.getRequest());
        } else {
            keyPair = LoginHelper.getRSAKeyPair((HttpServletRequest) pageContext.getRequest(), keyPairName);
        }

        StringBuilder html = new StringBuilder();

        // Campo que guarda no nome do campo com a senha criptografada
        HTMLInputTag cryptedPasswordFieldName = new HTMLInputTag();
        cryptedPasswordFieldName.setType("hidden");
        cryptedPasswordFieldName.setName(this.cryptedPasswordFieldName);
        cryptedPasswordFieldName.setId(this.cryptedPasswordFieldName);
        cryptedPasswordFieldName.setValue(cryptedfield);

        // Campo com a senha criptografada.
        HTMLInputTag cryptedPasswordField = new HTMLInputTag();
        cryptedPasswordField.setType("hidden");
        cryptedPasswordField.setName(cryptedfield);
        cryptedPasswordField.setId(cryptedfield);

        html.append(super.generateHtml(responsavel));
        html.append(cryptedPasswordFieldName.generateHtml(responsavel));
        html.append(cryptedPasswordField.generateHtml(responsavel));

        // Scripts
        html.append("<script language=\"JavaScript\" type=\"text/javascript\" src=\"../js/rsa/jsbn.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"").append("></script>");
        html.append("<script language=\"JavaScript\" type=\"text/javascript\" src=\"../js/rsa/prng4.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"").append("></script>");
        html.append("<script language=\"JavaScript\" type=\"text/javascript\" src=\"../js/rsa/rng.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"").append("></script>");
        html.append("<script language=\"JavaScript\" type=\"text/javascript\" src=\"../js/rsa/rsa.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"").append("></script>");
        html.append("<script language=\"JavaScript\" type=\"text/javascript\" src=\"../js/rsa/base64.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"").append("></script>");

        html.append("<script language=\"JavaScript\" type=\"text/JavaScript\">");
        html.append("var chave_publica_modulo   = '" + ((RSAPublicKey) keyPair.getPublic()).getModulus().toString(16) + "';");
        html.append("var chave_publica_expoente = '" + ((RSAPublicKey) keyPair.getPublic()).getPublicExponent().toString(16) + "';");
        html.append("</script>\n");


        return html.toString();
    }

    @Override
    protected void clean() {
        super.clean();
        cryptedfield = null;
        cryptedPasswordFieldName = null;
        keyPairName = null;
        isSenhaServidor = null;
    }
}
