package com.zetra.econsig.folha.contracheque.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.folha.contracheque.AbstractImportaContraCheques;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesJaboatao</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Pref. de Jaboatão.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesJaboatao extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesJaboatao.class);

    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = null;
        StringBuilder conteudo = null;
        String rseCodigo = null;

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            while ((linha = entrada.readLine()) != null) {
                if (linha.charAt(22) == '1') {
                    if (conteudo != null) {
                        // Se já existe um contracheque, então grava o registro anterior de contracheque
                        criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                        conteudo.setLength(0);
                    } else {
                        conteudo = new StringBuilder();
                    }

                    // Início de um contracheque: Pref. Jaboatao
                    // MAT:  6 caracteres
                    // CPF:  11 caracteres
                    String rseMatricula = String.valueOf(Long.parseLong(linha.substring(14, 20)));
                    String serCpf = TextHelper.format(linha.substring(80, 91), "###.###.###-##");

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = serCpf;
                    query.ativo = getAtivo();

                    List<String> servidores = query.executarLista();
                    if (servidores == null || servidores.size() == 0) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.cpf", responsavel, rseMatricula, serCpf));
                        conteudo = null;
                    } else if (servidores.size() > 1) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.cpf", responsavel, rseMatricula, serCpf));
                        conteudo = null;
                    } else {
                        rseCodigo = servidores.get(0);
                    }

                } else if (linha.charAt(22) == '2') {
                    // Linha de conteúdo de um contracheque
                    if (conteudo != null) {
                        conteudo.append(TextHelper.rtrim(linha.substring(23, 71))).append("\n");
                    }

                } else if (linha.charAt(22) == '9') {
                    // Término do arquivo, grava o último registro de contracheque
                    if (conteudo != null) {
                        criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                        conteudo.setLength(0);
                    }
                }

            }

        } catch (FileNotFoundException ex) {
            throw new ImportaContrachequesException("mensagem.erro.contracheque.arquivo.invalido", responsavel, ex);
        } catch (IOException ex) {
            throw new ImportaContrachequesException("mensagem.erro.contracheque.processar.arquivo", responsavel, ex, nomeArquivo);
        } catch (HQueryException ex) {
            throw new ImportaContrachequesException("mensagem.erro.contracheque.pesquisar.rse", responsavel, ex);
        } finally {
            try {
                if (entrada != null) {
                    entrada.close();
                }
            } catch (IOException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.fechar.arquivo", responsavel, nomeArquivo), ex);
            }
        }
    }
}
