package com.zetra.econsig.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.parser.config.ColunaTipo;
import com.zetra.econsig.parser.config.ParametroTipo;

/**
 * <p>Title: EscritorBaseDeDados</p>
 * <p>Description: Implementação de escritor para gravar dados em banco</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EscritorBaseDeDados extends BaseDeDados implements Escritor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EscritorBaseDeDados.class);

    private final String onError;
    private boolean excluir;
    private int linhasAlteradas;
    private boolean erro = false;

    /*
       Caso o escritor deva usar mais de uma tabela na base de dados,
       descricaoCampos irá conter a descrição dos campos, presentes no
       atributo doc, separados por tabela do banco.
     */
    private List<List<ColunaTipo>> descricaoCampos;
    private List<String> nomesTabelas;

    /*
       Usados caso o arquivo de configuração tenha as querys predefinidas.
       preparedStat é um hash, onde a chave é o nome da query e o valor é um objeto
       do tipo PreparedStatement. O nome da query é o nome da tabela mais o tipo
       da query, sendo insert, update ou delete, separados por um _.
     */
    protected Map<String, PreparedStatement> preparedStat;
    /* Ordem apenas diz a ordem de execução dos PreparedStatements */
    protected List<String> ordem;

    public EscritorBaseDeDados(String nomearqconfig) throws ParserException {
        this(nomearqconfig, null);
    }

    public EscritorBaseDeDados(String nomearqconfig, Connection conn) throws ParserException {
        super(nomearqconfig, conn);
        onError = doc.getPropriedades().getSeExiste().toLowerCase();
        descricaoCampos = null;
        excluir = false;
        ordem = null;
        preparedStat = null;
    }

    @Override
    public void escreve(Map<String, Object> informacao) throws ParserException {
        erro = false;
        if (ordem.size() == 0) {
            throw new ParserException("mensagem.erro.escritor.base.dados.configuracao.ausente", AcessoSistema.getAcessoUsuarioSistema());

        } else if ((ordem.size() > 0) && (ordem.size() <= 2) && (doc.getPropriedades().getTabela() != null)) {
            preparaSQLs(informacao);
            insereInformacao(informacao);

        } else {
            // Separa a configuração dos campos por tabela
            if (descricaoCampos == null) {
                configuraPorTabela();
            }

            for (int i = 0; i < descricaoCampos.size(); i++) {
                String tabela = nomesTabelas.get(i);
                doc.getColuna().clear();
                doc.getColuna().addAll(descricaoCampos.get(i));
                doc.getPropriedades().setTabela(tabela);
                preparaSQLs(informacao);
            }

            insereInformacao(informacao);
            doc.getPropriedades().setTabela(null);
        }
    }

    private void preparaSQLs(Map<String, Object> informacao) throws ParserException {

        Object acao = informacao.get("__ACAO__");
        if ((acao != null) && (acao.toString().equals("E"))) {
            excluir = true;
        }

        for (ColunaTipo col : doc.getColuna()) {

            String valor = null;

            // Seta o valor a ser inserido no banco de dados
            if (col.getTipo().equalsIgnoreCase("Chave Primaria")) {
                try {
                    valor = DBHelper.getNextId();
                } catch (MissingPrimaryKeyException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                informacao.put(col.getNome(), valor);
            } else if (col.getTipo().equalsIgnoreCase("Chave Estrangeira")) {
                if (col.getReferencia() != null) {
                    if (informacao.get(col.getReferencia()) != null) {
                        valor = informacao.get(col.getReferencia()).toString();
                    } else {
                        throw new ParserException("mensagem.erro.escritor.base.dados.coluna.nao.encontrada", AcessoSistema.getAcessoUsuarioSistema(), col.getReferencia(), col.getNome());
                    }
                }
            } else {
                valor = (informacao.get(col.getNome()) != null) ? informacao.get(col.getNome()).toString() : null;
            }

            if ((valor == null) || (valor.trim().equals(""))) {
                if (col.getNulo().equalsIgnoreCase("nao") || ((col.getChave() != null) && (col.getChave().equalsIgnoreCase("sim")))) {
                    throw new ParserException("mensagem.erro.escritor.base.dados.atributo.nao.pode.ser.nulo", AcessoSistema.getAcessoUsuarioSistema(), col.getNome());
                } else {
                    valor = (col.getDefault() == null) ? "" : col.getDefault();
                }
            }
            try {

                Object obj = preparedStat.get(col.getTabela() + "_insert");
                PreparedStatement statement_insert = (obj != null) ? (PreparedStatement) obj : null;
                obj = preparedStat.get(col.getTabela() + "_update");
                PreparedStatement statement_update = (obj != null) ? (PreparedStatement) obj : null;
                obj = preparedStat.get(col.getTabela() + "_delete");
                PreparedStatement statement_delete = (obj != null) ? (PreparedStatement) obj : null;

                // Seta os parametros dos prepared statements para a inserção
                if ((statement_insert != null) && (col.getIndiceInsert() > 0)) {
                    setaValorStatement(statement_insert, col.getTipo(), col.getIndiceInsert(), valor);
                }
                // Seta os parametros dos prepared statements para a atualização
                if ((statement_update != null) && (onError.equals("atualizar")) && (col.getIndiceUpdate() > 0)) {
                    setaValorStatement(statement_update, col.getTipo(), col.getIndiceUpdate(), valor);
                }
                // Seta os parametros dos prepared statements para a exclusão
                if ((statement_delete != null) && (excluir) && (col.getIndiceDelete() > 0)) {
                    setaValorStatement(statement_delete, col.getTipo(), col.getIndiceDelete(), valor);
                }

            } catch (SQLException | NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new ParserException("mensagem.erro.escritor.base.dados.indice.atributo.invalido", AcessoSistema.getAcessoUsuarioSistema(), col.getNome());
            }
        }
    }

    private void insereInformacao(Map<String, Object> informacao) throws ParserException {
        if (!excluir) {
            linhasAlteradas = 0;
            for (int i = 0; i < ordem.size(); i++) {
                String nomeSql = ordem.get(i).toString();
                if (nomeSql.endsWith("_insert")) {
                    PreparedStatement pstm;
                    try {
                        pstm = preparedStat.get(ordem.get(i));
                        linhasAlteradas = pstm.executeUpdate();
                        if (linhasAlteradas == 0) {
                           erro = true;
                           LOG.debug(nomeSql + " NAO FEZ O INSERT: " + printMap(informacao));
                        }
                    } catch (SQLException ex) {
                        if (onError.equals("atualizar") && (i + 1 < ordem.size())) {
                            nomeSql = ordem.get(i + 1).toString();
                            if (nomeSql.endsWith("_update")) {
                                try {
                                    pstm = preparedStat.get(nomeSql);
                                    linhasAlteradas = pstm.executeUpdate();
                                    if (linhasAlteradas == 0) {
                                        erro = true;
                                        LOG.debug(nomeSql + " NAO FEZ O INSERT: " + printMap(informacao));
                                    }
                                } catch (SQLException exc) {
                                    erro = true;
                                    LOG.error(ex.getMessage(), ex);
                                    LOG.error("Erro(EscritorBaseDeDados.insereInformacao[" + ordem.get(i).toString() + "]): operação de inserção de registros abortada. " + ex.getMessage());
                                    LOG.error("Erro(EscritorBaseDeDados.insereInformacao[" + nomeSql + "]): operação de atualização de registros abortada. " + exc.getMessage());
                                    throw new ParserException("mensagem.erro.escritor.base.dados.operacao.alteracao.abortada", AcessoSistema.getAcessoUsuarioSistema());
                                }
                            } else {
                                erro = true;
                                LOG.error("Erro(EscritorBaseDeDados.insereInformacao[" + ordem.get(i).toString() + "]): operação de inserção de registros abortada. " + ex.getMessage());
                                throw new ParserException("mensagem.erro.escritor.base.dados.operacao.inclusao.abortada", AcessoSistema.getAcessoUsuarioSistema());
                            }
                        } else if (onError.equals("abortar")) {
                            erro = true;
                            LOG.error("Erro(EscritorBaseDeDados.insereInformacao[" + nomeSql + "]): operação de inserção de registros abortada. " + ex.getMessage());
                            throw new ParserException("mensagem.erro.escritor.base.dados.operacao.inclusao.abortada", AcessoSistema.getAcessoUsuarioSistema());
                        } else if (onError.equals("ignorar")) {
                            erro = true;
                            LOG.debug("IGNORADO: " + printMap(informacao));
                        }
                    }
                }
            }
        } else {
            excluir = false;
            for (int i = ordem.size() - 1; i > 0; i--) {
                String nomeSql = ordem.get(i).toString();
                if (nomeSql.endsWith("_delete")) {
                    PreparedStatement pstm;
                    try {
                        pstm = preparedStat.get(ordem.get(i));
                        pstm.executeUpdate();
                    } catch (SQLException ex) {
                        throw new ParserException("mensagem.erro.escritor.base.dados.operacao.exclusao.abortada", AcessoSistema.getAcessoUsuarioSistema());
                    }
                }
            }
        }
    }

    private String printMap(Map<String, Object> map){
    	if (map == null) {
            return "";
        }
    	StringBuilder res = new StringBuilder();
    	for (Map.Entry<String, Object> entry : map.entrySet()) {
			res.append(entry.getKey());
			res.append(" = [");
			res.append(entry.getValue());
			res.append("], ");
		}
    	if(res.length() < 2) {
            res.append("--");
        }
    	return res.substring(0, res.length()-2).toString();
    }

    private void configuraPorTabela() {

        nomesTabelas = new ArrayList<>();
        descricaoCampos = new ArrayList<>();

        for (ColunaTipo col : doc.getColuna()) {
            String nomeTabela[] = col.getTabela().split(",");
            for (String element : nomeTabela) {
                List<ColunaTipo> lista;
                int index = nomesTabelas.indexOf(element);
                if (index != -1) {
                    lista = descricaoCampos.get(index);
                    lista.add(col);
                } else {
                    lista = new ArrayList<>();
                    lista.add(col);
                    descricaoCampos.add(lista);
                    nomesTabelas.add(element);
                }
            }
        }
    }

    private void setaValorStatement(PreparedStatement stat, String tipo, int indice, String valor) throws SQLException {
        if (!valor.equals("")) {
            if (tipo.equalsIgnoreCase("Numerico")) {
                stat.setDouble(indice, Double.parseDouble(valor));
            } else {
                stat.setString(indice, valor);
            }
        } else {
            stat.setNull(indice, Types.VARCHAR);
        }
    }

    @Override
    public void iniciaEscrita() throws ParserException {

        try {
            abreConexao();

            if (doc.getParametro() != null) {
                preparedStat = new HashMap<>();
                ordem = new ArrayList<>();
                for (ParametroTipo param : doc.getParametro()) {
                    preparedStat.put(param.getNome(), conn.prepareStatement(param.getValor()));
                    ordem.add(param.getNome());
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.escritor.base.dados.configuracao.ausente", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    @Override
    public void encerraEscrita() throws ParserException {
        try {
            if (preparedStat != null) {
                Iterator<String> it = preparedStat.keySet().iterator();
                while (it.hasNext()) {
                    preparedStat.get(it.next()).close();
                }
                preparedStat = null;
            }

            fechaConexao();
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.escritor.base.dados.encerrar.conexao", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    public int getLinhasAlteradas() {
        return linhasAlteradas;
    }

    public boolean possuiErro() {
        return erro;
    }
}