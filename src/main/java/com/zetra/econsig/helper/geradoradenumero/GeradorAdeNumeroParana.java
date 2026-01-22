package com.zetra.econsig.helper.geradoradenumero;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.helper.sistema.DBHelper;
/**
 * <p>Title: GeradorAdeNumeroParana</p>
 * <p>Description: Implementação para o Paraná do gerador de AdeNumero.</p>
 * <p>Copyright: Copyright (c) 2004-2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorAdeNumeroParana implements GeradorAdeNumero {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorAdeNumeroParana.class);

    private Map<String, Long> cache = null;

    public GeradorAdeNumeroParana() {
        cache = new HashMap<>();
    }

    @Override
    public synchronized Long getNext(String vcoCodigo, Date anoMesIni) {
        int ano = Calendar.getInstance().get(Calendar.YEAR);
        String key = "ANO-" + ano;

        // Busca o proximo numero
        Long next = cache.get(key);
        if (next == null) {
            // Se não existe no cache então busca no banco de dados.
            next = findAdeNumero(ano);
        }
        // Salva o próximo pois o atual será usado.
        cache.put(key, Long.valueOf(next.longValue() + 1));

        // Calcula o digito verificador
        /*
            VALOR1 = TOINT(MID(NUM,0,1))
            VALOR2 = TOINT(MID(NUM,1,1))
            VALOR3 = TOINT(MID(NUM,2,1))
            VALOR4 = TOINT(MID(NUM,3,1))
            VALOR5 = TOINT(MID(NUM,4,1))
            VALOR6 = TOINT(MID(NUM,5,1))
            VALOR7 = TOINT(MID(NUM,6,1))

            SUMA = (VALOR1 * 2) + (VALOR2 * 7) + (VALOR3 * 6) + (VALOR4 * 5) + (VALOR5 * 4) + (VALOR6 * 3) + (VALOR7 * 2)

            RESTO = DIV(SUMA,11)
            RESTO = 11 - RESTO

            IF RESTO = 10 or RESTO = 11 THEN
                RESTO = 0
            ENDIF

            vNroConsig2 = NUM + TOSTRING(RESTO,0)
         */
        long soma = 0;
        double numero = next.doubleValue();
        int fator[] = {-1, 2, 3, 4, 5, 6, 7, 2};
        for (int i=1; i<=7; i++) {
            soma += (numero % 10) * fator[i];
            numero = ((numero - numero % 10) / 10);
        }
        long digito = soma % 11;
        digito = 11 - digito;
        if (digito == 10 || digito == 11) {
            digito = 0;
        }

        /*
         * Formata o ade_numero como NNNNNNNNDAAAA
         * - NNNNNNNN é um contador crescente que começa com 5000001 a cada ano
         * - D é um digito verificador
         * - AAAA é o ano
         */
        StringBuilder adeNumero = new StringBuilder();
        adeNumero.append(next).append(digito).append(ano);

        return Long.valueOf(adeNumero.toString());
    }

    private Long findAdeNumero(int ano) {
        // Valor inicial básico
        Long adeNumero = Long.valueOf(1);

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            StringBuilder query = new StringBuilder();
            query.append("SELECT IFNULL((MAX(ade_numero) DIV 100000), 5000000) + 1 AS next ");
            query.append("FROM tb_aut_desconto ");
            query.append("WHERE tb_aut_desconto.ade_numero%10000 = ").append(ano);

            LOG.debug("ADE_NUMERO_PARANA: " + query.toString());
            rs = stat.executeQuery(query.toString());

            if (rs.next()) {
                adeNumero = Long.valueOf(rs.getLong("next"));
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stat != null) {
                    stat.close();
                }
                if (conn != null) {
                    DBHelper.releaseConnection(conn);
                }
            } catch (SQLException ex) {
            }
        }
        return adeNumero;
    }
}
