package com.zetra.econsig.helper.validacaoambiente;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: RegraValidacaoVersaoJava</p>
 * <p>Description: Regra que verifica se a versão do Java é igual ou superior à definida nas constantes da classe JAVA_MAJOR_VERSION e JAVA_MINOR_VERSION</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoVersaoJava implements RegraValidacaoAmbienteInterface {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraValidacaoVersaoJava.class);

    // Mínima versão do Java suportada
    private static final int JAVA_MAJOR_VERSION = 17;

    /**
     * Método que executa a validação da versão do Java se é equivalente a VERSAO_JAVA.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     */
    @Override
    public Map<Boolean, String> executar() {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        Map<Boolean, String> resultado = new HashMap<>();
        final String versaoCorrente = System.getProperty("java.version");
        LOG.debug("JVM: " + versaoCorrente);

        final String[] partesVersao = versaoCorrente != null ? versaoCorrente.split("\\.") : new String[0];
        final String major = partesVersao.length > 0 ? partesVersao[0] : null;

        if (major != null && NumberUtils.isNumber(major)) {
            if (Integer.parseInt(major) >= JAVA_MAJOR_VERSION) {
                resultado.put(Boolean.TRUE, ApplicationResourcesHelper.getMessage("mensagem.info.ambiente.versao.java.superior", responsavel, versaoCorrente, String.valueOf(JAVA_MAJOR_VERSION)));
                return resultado;
            } else {
                resultado.put(Boolean.FALSE, ApplicationResourcesHelper.getMessage("mensagem.erro.ambiente.versao.java.inferior", responsavel, versaoCorrente, String.valueOf(JAVA_MAJOR_VERSION)));
                return resultado;
            }
        }

        resultado.put(Boolean.FALSE, ApplicationResourcesHelper.getMessage("mensagem.erro.ambiente.versao.java.invalida", responsavel, versaoCorrente));
        return resultado;
    }
}
