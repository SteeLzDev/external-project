package com.zetra.econsig.folha.exportacao.validacao;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorBaseDeDados;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoMovFinDAO;

/**
 * <p>Title: ImportaArquivo</p>
 * <p>Description: Importa o arquivo gerado para uma tabela temporária.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportarArquivo {
    private String nomeArquivo;
    private String nomeArquivoConfEntrada;
    private String nomeArquivoConfTradutor;
    private String nomeArquivoConfSaida;
    
    public ImportarArquivo(String nomeArquivo, String nomeArquivoConfEntrada, String nomeArquivoConfTradutor, String nomeArquivoConfSaida) {
        this.nomeArquivo = nomeArquivo;
        this.nomeArquivoConfEntrada= nomeArquivoConfEntrada;
        this.nomeArquivoConfTradutor = nomeArquivoConfTradutor;
        this.nomeArquivoConfSaida = nomeArquivoConfSaida;
    }
    
    public void executar(boolean inserirHistoricoMovimento) throws ZetraException {
        HistoricoMovFinDAO hmfDAO = DAOFactory.getDAOFactory().getHistoricoMovFinDAO();
        
        // Limpa a tabela com o conteúdo do arquivo
        hmfDAO.limparTabelaArquivo();
        
        // Configura o leitor de acordo com o arquivo de entrada
        LeitorArquivoTexto leitor = null;
        if (nomeArquivo.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(nomeArquivoConfEntrada, nomeArquivo);
        } else {
            leitor = new LeitorArquivoTexto(nomeArquivoConfEntrada, nomeArquivo);
        }

        // Importa o arquivo de movimento.
        Escritor escritor = new EscritorBaseDeDados(nomeArquivoConfSaida);
        Tradutor tradutor = new Tradutor(nomeArquivoConfTradutor, leitor, escritor);
        tradutor.traduz();
        tradutor.encerraTraducao();
        
        // Atualizar campos com os códigos internos.
        hmfDAO.atualizarCamposTabelaArquivo();
        
        if (inserirHistoricoMovimento) {
            // Insere o histórico de movimento para o arquivo atual
            hmfDAO.inserirHistoricoTabelaArquivo();
        }
    }
    
    public void inserirHistoricoMovimento() throws ZetraException {
        HistoricoMovFinDAO hmfDAO = DAOFactory.getDAOFactory().getHistoricoMovFinDAO();
        // Insere o histórico de movimento para o arquivo carregado na tabela
        hmfDAO.inserirHistoricoTabelaArquivo();
    }
}
