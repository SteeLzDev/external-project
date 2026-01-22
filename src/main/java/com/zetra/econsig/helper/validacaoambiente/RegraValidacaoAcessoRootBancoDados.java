package com.zetra.econsig.helper.validacaoambiente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: RegraValidacaoAcessoRootBancoDados</p>
 * <p>Description: Regra que verifica se o acesso ao banco de dados é feito com usuário ROOT.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoAcessoRootBancoDados implements RegraValidacaoAmbienteInterface {

    /**
     * Método que executa a conexão ao banco de dados é feita com usuário ROOT. À princípio
     * valida apenas para MySQL.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação
     */
    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        Map<Boolean, String> resultado = new HashMap<>();
        Connection conn = null;
        try {
            conn = DBHelper.makeConnection();
            boolean acessoRoot = false;
            String username = conn.getMetaData().getUserName();
            if (!TextHelper.isNull(username)) {
                // MySQL
                acessoRoot = (username.toLowerCase().startsWith("root@"));
            }
            resultado.put(!acessoRoot, username);
        } catch (SQLException e) {
            throw new ValidacaoAmbienteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        } finally {
            if (conn != null) {
                DBHelper.releaseConnection(conn);
            }
        }
        return resultado;
    }
}
