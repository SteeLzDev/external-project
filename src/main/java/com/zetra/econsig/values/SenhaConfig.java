package com.zetra.econsig.values;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

public record SenhaConfig(
        int forcaSenha,
        int nivelForcaSenha,
        String rotuloForcaSenha,
        boolean ignoraSeveridade,
        String mensagemSenha,
        String mensagemSenha1,
        String mensagemSenha2,
        String mensagemSenha3,
        String mensagemErroSenha,
        int tamMinSenha,
        int tamMaxSenha) {

    public static final int FORCA_SENHA_PADRAO = 3;

    public static final int NIVEL_FORCA_SENHA_MUITO_BAIXO = 1;
    public static final int NIVEL_FORCA_SENHA_BAIXO       = 2;
    public static final int NIVEL_FORCA_SENHA_MEDIO       = 3;
    public static final int NIVEL_FORCA_SENHA_ALTO        = 4;
    public static final int NIVEL_FORCA_SENHA_MUITO_ALTO  = 5;

    public static final String ROTULO_NIVEL_SENHA_MUITO_BAIXO = "rotulo.nivel.senha.muito.baixo";
    public static final String ROTULO_NIVEL_SENHA_BAIXO       = "rotulo.nivel.senha.baixo";
    public static final String ROTULO_NIVEL_SENHA_MEDIO       = "rotulo.nivel.senha.medio";
    public static final String ROTULO_NIVEL_SENHA_ALTO        = "rotulo.nivel.senha.alto";
    public static final String ROTULO_NIVEL_SENHA_MUITO_ALTO  = "rotulo.nivel.senha.muito.alto";

    public static final String ROTULO_MUITO_BAIXO = "muito.baixo";
    public static final String ROTULO_BAIXO       = "baixo";
    public static final String ROTULO_MEDIO       = "medio";
    public static final String ROTULO_ALTO        = "alto";
    public static final String ROTULO_MUITO_ALTO  = "muito.alto";

    public static SenhaConfig getSenhaUsuarioConfig(String tipoEntidade, AcessoSistema responsavel) {
        String pwdStrength = String.valueOf(FORCA_SENHA_PADRAO);
        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel).toString() : String.valueOf(FORCA_SENHA_PADRAO);
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel).toString() : String.valueOf(FORCA_SENHA_PADRAO);
        }

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength = 0;
        try {
            intpwdStrength = Integer.valueOf(pwdStrength);
        } catch (final NumberFormatException ex) {
            intpwdStrength = FORCA_SENHA_PADRAO;
        }

        final boolean ignoraSeveridade = intpwdStrength == 0;
        int pwdStrengthLevel = NIVEL_FORCA_SENHA_MUITO_BAIXO; // very weak
        String strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MUITO_BAIXO, responsavel);
        String nivel = ROTULO_MUITO_BAIXO;
        if (intpwdStrength == NIVEL_FORCA_SENHA_BAIXO) { // weak
            pwdStrengthLevel = 16;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_BAIXO, responsavel);
            nivel = ROTULO_BAIXO;
        } else if (intpwdStrength == NIVEL_FORCA_SENHA_MEDIO) { // mediocre
            pwdStrengthLevel = 25;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MEDIO, responsavel);
            nivel = ROTULO_MEDIO;
        } else if (intpwdStrength == NIVEL_FORCA_SENHA_ALTO) { // strong
            pwdStrengthLevel = 35;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_ALTO, responsavel);
            nivel = ROTULO_ALTO;
        } else if (intpwdStrength >= NIVEL_FORCA_SENHA_MUITO_ALTO) { // very strong
            pwdStrengthLevel = 45;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MUITO_ALTO, responsavel);
            nivel = ROTULO_MUITO_ALTO;
        }
        final String chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".geral";
        final String strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
        final String strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
        final String strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
        final String strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
        final String strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);

        int tamMinSenhaUsuario = 6;
        int tamMaxSenhaUsuario = 8;

        try {
            tamMinSenhaUsuario = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel).toString()) : 6;
        } catch (final Exception ex) {
            tamMinSenhaUsuario = 6;
        }
        try {
            tamMaxSenhaUsuario = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel).toString()) : 8;
        } catch (final Exception ex) {
            tamMaxSenhaUsuario = 8;
        }

        return new SenhaConfig(
            intpwdStrength,
            pwdStrengthLevel,
            strpwdStrengthLevel,
            ignoraSeveridade,
            strMensagemSenha,
            strMensagemSenha1,
            strMensagemSenha2,
            strMensagemSenha3,
            strMensagemErroSenha,
            tamMinSenhaUsuario,
            tamMaxSenhaUsuario
        );
    }

    public static SenhaConfig getSenhaServidorConfig(AcessoSistema responsavel) {
        // Nível de Severidade da nova senha dos usuários
        final String pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : String.valueOf(FORCA_SENHA_PADRAO);

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength = 0;
        try {
            intpwdStrength = Integer.valueOf(pwdStrength);
        } catch (final NumberFormatException ex) {
            intpwdStrength = FORCA_SENHA_PADRAO;
        }
        final boolean ignoraSeveridade = intpwdStrength == 0;
        int pwdStrengthLevel = NIVEL_FORCA_SENHA_MUITO_BAIXO; // very weak
        String strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MUITO_BAIXO, responsavel);
        String nivel = ROTULO_MUITO_BAIXO;
        if (intpwdStrength == NIVEL_FORCA_SENHA_BAIXO) { // weak
            pwdStrengthLevel = 16;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_BAIXO, responsavel);
            nivel = ROTULO_BAIXO;
        } else if (intpwdStrength == NIVEL_FORCA_SENHA_MEDIO) { // mediocre
            pwdStrengthLevel = 25;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MEDIO, responsavel);
            nivel = ROTULO_MEDIO;
        } else if (intpwdStrength == NIVEL_FORCA_SENHA_ALTO) { // strong
            pwdStrengthLevel = 35;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_ALTO, responsavel);
            nivel = ROTULO_ALTO;
        } else if (intpwdStrength >= NIVEL_FORCA_SENHA_MUITO_ALTO) { // strong
            pwdStrengthLevel = 45;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage(ROTULO_NIVEL_SENHA_MUITO_ALTO, responsavel);
            nivel = ROTULO_MUITO_ALTO;
        }
        String chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".servidor";
        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
            chave += ".numerica";
        }

        final String strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
        final String strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
        final String strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
        final String strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
        final String strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);

        int tamMinSenhaServidor = 6;
        int tamMaxSenhaServidor = 8;

        try {
            tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
        } catch (final Exception ex) {
            tamMinSenhaServidor = 6;
        }
        try {
            tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
        } catch (final Exception ex) {
            tamMaxSenhaServidor = 8;
        }

        return new SenhaConfig(
            intpwdStrength,
            pwdStrengthLevel,
            strpwdStrengthLevel,
            ignoraSeveridade,
            strMensagemSenha,
            strMensagemSenha1,
            strMensagemSenha2,
            strMensagemSenha3,
            strMensagemErroSenha,
            tamMinSenhaServidor,
            tamMaxSenhaServidor
        );
    }
}
