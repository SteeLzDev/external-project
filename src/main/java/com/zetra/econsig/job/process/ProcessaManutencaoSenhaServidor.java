package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaManutencaoSenhaServidor</p>
 * <p>Description: Processo de geração e ativação automática de senhas</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaManutencaoSenhaServidor extends Processo {

    public static final String GERAR_SENHA  = "_PROCESSO_GERACAO_SENHAS_";
    public static final String ATIVAR_SENHA = "_PROCESSO_ATIVAR_SENHAS_";
    private final String tipoOperacao;
    private final AcessoSistema responsavel;

    public ProcessaManutencaoSenhaServidor(String tipoOperacao, AcessoSistema responsavel) {
        this.responsavel = responsavel;
        this.tipoOperacao = tipoOperacao;
    }

    @Override
    protected void executar() {
        try {
            UsuarioDelegate usuDelegate = new UsuarioDelegate();

            if (tipoOperacao.equals(GERAR_SENHA)) {
                String senhaAberta = usuDelegate.gerarSenhasUsuServidores(responsavel);
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.geracao.senhas.sucesso", responsavel) + " " +
                           ApplicationResourcesHelper.getMessage("mensagem.informacao.senha.para.acesso.arquivo.zip.arg0", responsavel, "<font class=\"novaSenha\">" + senhaAberta + "</font>");
            } else if (tipoOperacao.equals(ATIVAR_SENHA)) {
                usuDelegate.ativarSenhasUsuServidores(responsavel);
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.novas.senhas.ativadas", responsavel);
            }

        } catch (UsuarioControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.geracao.novas.senhas.usuarios.servidores", responsavel) + "<br>"
                + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }

    }

}
