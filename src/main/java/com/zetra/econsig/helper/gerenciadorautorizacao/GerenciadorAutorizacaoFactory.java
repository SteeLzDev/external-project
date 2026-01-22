package com.zetra.econsig.helper.gerenciadorautorizacao;

import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GerenciadorAutorizacaoFactory</p>
 * <p>Description: Factory para criação de classe de manutenção de autorização
 * específica para cada serviço.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerenciadorAutorizacaoFactory {
    /**
     * Obtém o nome da classe de adminstração de autorização.
     * @param classe Classe gerenciadora de autorização
     * @return
     */
    public static String getClassNameGerenciadorAutorizacao(Class<? extends GerenciadorAutorizacao> classe) {
        return classe.getName();
    }

    /**
     * Obtém uma instância da classe gerenciadora de autorização de acordo com seu nome.
     * @param className Nome da classe.
     * @param sessionContext Contexto da sessão.
     * @return
     * @throws GerenciadorAutorizacaoException
     */
    public static GerenciadorAutorizacao getGerenciadorAutorizacao(String className) throws GerenciadorAutorizacaoException {
        try {
            Object gerenciadorAutorizacao = Class.forName(className).getDeclaredConstructor().newInstance();
            return (GerenciadorAutorizacao) gerenciadorAutorizacao;
        } catch (Exception ex) {
            throw new GerenciadorAutorizacaoException("mensagem.erro.recuperar.gerenciador.autorizacao", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }
}
