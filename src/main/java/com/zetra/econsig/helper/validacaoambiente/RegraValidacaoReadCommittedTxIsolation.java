package com.zetra.econsig.helper.validacaoambiente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: RegraValidacaoReadCommittedTxIsolation</p>
 * <p>Description: Regra que verifica se o nível de transação está em Read-Committed.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoReadCommittedTxIsolation implements RegraValidacaoAmbienteInterface {

    /**
     * Método que executa a validação se o nível de transação do banco de dados está em Read-Commited.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação
     */
    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        Map<Boolean, String> resultado = new HashMap<>();
        Connection conn = null;
        try {
            conn = DBHelper.makeConnection();
            int isolationLevel = conn.getTransactionIsolation();
            resultado.put(Boolean.valueOf(isolationLevel == Connection.TRANSACTION_READ_COMMITTED), String.valueOf(isolationLevel));
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
