package com.zetra.econsig.persistence.interceptor;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

import com.zetra.econsig.persistence.dao.DAOFactory;

/**
 * <p>Title: IndexHintInterceptor</p>
 * <p>Description: Interceptor que modifica a query de acordo com os Hints de
 * índices configurados no arquivo de propriedades IndexHint.properties</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class IndexHintInterceptor {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IndexHintInterceptor.class);

    private final Properties indexHints;
    private final String[] sqlReservedWords;

    public IndexHintInterceptor() {
        LOG.debug("Index Hint Hibernate Interceptor Created.");

        // Preenche lista de palavras reservadas e ordena (para busca binária)
        sqlReservedWords = new String[]{"select","from","where","group","order","having","limit","on","and","or","join","inner","left","right","cross","full","outer"};
        Arrays.sort(sqlReservedWords);

        // Carrega o arquivo de configuração dos Hints
        indexHints = new Properties();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("IndexHint.properties");
            if (in != null) {
                indexHints.load(in);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public String onPrepareStatement(String sql) {

        int startPoint = sql.indexOf("/*");
        int endPoint   = sql.indexOf("*/");
        String queryName = "";
        String queryString = sql;
        if (startPoint >= 0 && endPoint <= sql.length() && endPoint > startPoint) {
            try {
                queryName = sql.substring(startPoint + 2, endPoint).trim();
                queryString = sql.substring(endPoint + 2).trim();
            } catch (StringIndexOutOfBoundsException ex) {
                LOG.error("Query não está no formato esperado para tratamento de índices: " + sql);
            }
        }
        // Realiza a substituição do hint
        if (indexHints.containsKey(queryName)) {
            if (DAOFactory.isOracle()) {
                String hint = indexHints.get(queryName).toString();
                /* Oracle: o hint deve ser adicionado após a instrução select
                 * O arquivo de configuração deve ser preenchido com a sintaxe de hint do oracle sem a tag de comentário
                 */
                StringBuilder changedQueryString = new StringBuilder();
                String sqlSelectWord = "select";
                if (queryString.toLowerCase().indexOf(sqlSelectWord.toLowerCase()) != -1) {
                    changedQueryString.append(queryString.substring(0, queryString.toLowerCase().indexOf(sqlSelectWord.toLowerCase()) + sqlSelectWord.length()));
                    changedQueryString.append(generateIndexHint(hint, ""));
                    changedQueryString.append(queryString.substring(queryString.toLowerCase().indexOf(sqlSelectWord.toLowerCase()) + sqlSelectWord.length() + 1, queryString.length()));
                    queryString = changedQueryString.toString();
                }
            } else {
                String[] hints = indexHints.get(queryName).toString().split(";");
                for (String hint2 : hints) {
                    if (hint2.indexOf(':') != -1 && hint2.indexOf('.') != -1 && hint2.indexOf(':') < hint2.indexOf('.')) {
                        String hint  = hint2.substring(0, hint2.indexOf(':'));
                        String table = hint2.substring(hint2.indexOf(':') + 1, hint2.indexOf('.'));
                        String index = hint2.substring(hint2.indexOf('.') + 1);

                        StringBuilder changedQueryString = new StringBuilder();
                        StringTokenizer st = new StringTokenizer(queryString);
                        boolean changed = false;
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            if (token.equalsIgnoreCase(table) && !changed) {
                                // Adiciona a tabela
                                changedQueryString.append(token).append(" ");
                                if (st.hasMoreTokens()) {
                                    // Adiciona o apelido da tabela, caso exista
                                    token = st.nextToken();
                                    if (Arrays.binarySearch(sqlReservedWords, token) < 0) {
                                        // Se não é uma palavra reservada, então é um apelido
                                        changedQueryString.append(token).append(" ");
                                        // Adiciona o hint do indice
                                        changedQueryString.append(generateIndexHint(hint, index));
                                    } else {
                                        // Adiciona o hint do indice
                                        changedQueryString.append(generateIndexHint(hint, index));
                                        // Se é palavra reservada, adiciona após o hint
                                        changedQueryString.append(token).append(" ");
                                    }
                                } else {
                                    // Adiciona o hint do indice
                                    changedQueryString.append(generateIndexHint(hint, index));
                                }
                                // Garante que apenas a primeira ocorrencia da tabela será substituida
                                changed = true;
                            } else {
                                // Adiciona o token
                                changedQueryString.append(token).append(" ");
                            }
                        }
                        queryString = changedQueryString.toString();
                    } else {
                        LOG.error("Sintaxe incorreta para Hint de índice na query " + queryName);
                    }
                }
            }
            LOG.debug("QUERY COM INDEX HINT: " + queryString);
        }
        return queryString;
    }

    /**
     * Cria a cláusula de Index Hint de acordo com o tipo de banco de dados
     * @param hint  : force, ignore ou use. No caso do oracle, hint será a instrução completa.
     * @param index : nome do indice.
     * @return
     */
    private String generateIndexHint(String hint, String index) {
        if (DAOFactory.isMysql()) {
            return hint + " index (" + index + ") ";
        } else if (DAOFactory.isOracle()) {
            return " /*+ " + hint + " */ ";
        } else {
            return "";
        }
    }
}
