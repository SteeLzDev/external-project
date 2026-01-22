package com.zetra.econsig.helper.validacaoambiente;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: RegraValidacaoAcessoExcessivoSistemaArquivos</p>
 * <p>Description: Regra que verifica se o acesso ao sistema de arquivos é muito permissivo.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoAcessoExcessivoSistemaArquivos implements RegraValidacaoAmbienteInterface {

    /**
     * Método que executa a validação se o acesso ao sistema de arquivos é muito excessivo
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação
     */
    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        String[] paths = {
                "/",
                "/root",
                "/var/lib/mysql",
                "/etc/passwd",
                "/etc/shadow",
                "/etc/group",
                "/etc/gshadow",
                "/etc/systemd",
                "/etc/init",
                "/etc/init.d",
                "/proc/cmdline",
        };
        StringBuilder messages = new StringBuilder();
        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.canWrite()) {
                if (file.isDirectory()) {
                    messages.append(ApplicationResourcesHelper.getMessage("mensagem.erro.ambiente.acesso.excessivo.diretorio", responsavel, path)).append(System.lineSeparator());
                } else {
                    messages.append(ApplicationResourcesHelper.getMessage("mensagem.erro.ambiente.acesso.excessivo.arquivo", responsavel, path)).append(System.lineSeparator());
                }
            }
        }

        Map<Boolean, String> resultado = new HashMap<>();
        if (!messages.isEmpty()) {
            resultado.put(Boolean.FALSE, messages.toString());
        } else {
            resultado.put(Boolean.TRUE, ApplicationResourcesHelper.getMessage("rotulo.status.ok", responsavel));
        }
        return resultado;
    }
}
