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
 * <p>Title: ImportaContrachequesMPAC</p>
 * <p>Description: Classe para execução da importação de arquivos de contracheques para Ministério Público do Acre</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26289 $
 * $Date: 2020-02-21 15:08:57 -0300 (sex, 21 fev 2020) $
 */
public class ImportaContrachequesMPAC extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesMPAC.class);

    /**
     * Realiza a importação do arquivo de contracheques
     * @param nomeArquivo    : nome do arquivo contendo os contracheques
     * @param periodo        : período dos contracheques
     * @param responsavel    : responsável pela importação
     * @param tipoEntidade   : CSE/EST/ORG (Atualmente não é utilizado)
     * @param codigoEntidade : código de acordo com tipoEntidade (Atualmente não é utilizado)
     * @throws ImportaContrachequesException
     */
    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = null;
        StringBuilder conteudo = null;
        String rseCodigo = null;

        String rseMatricula = null;

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            while ((linha = entrada.readLine()) != null) {
                if (!linha.isBlank()) {
                    //Linha que inicia com a frase MINISTERIO PUBLICO DO ESTADO DO ACRE
                    if (linha.substring(0, 36).equals("MINISTERIO PUBLICO DO ESTADO DO ACRE")) {
                        //INCLUIR VALIDAÇÃO
                        if (conteudo != null) {
                            // Se já existe um contracheque, então grava o registro anterior de contracheque
                            criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                            conteudo.setLength(0);
                        } else {
                            conteudo = new StringBuilder();
                        }
                        conteudo.append(TextHelper.rtrim(linha)).append("\n");

                        //Linha que inicia com RUA de RUA MARECHAL DEODORO 472 (Endereço do Ministério do Est AC)
                    } else if (linha.substring(0, 3).equals("RUA") && conteudo != null) {
                        conteudo.append(TextHelper.rtrim(linha)).append("\n");

                        //Linha que contém a MATRÍCULA do servidor
                    } else if (TextHelper.isNum(linha.trim().charAt(0)) && !TextHelper.isNum(linha.trim().substring(linha.trim().length() - 1)) && conteudo != null) {

                        // MAT:  4 ou 5 caracteres, com possível zero à esquerda -
                        String matricula = linha.substring(1, 6);
                        rseMatricula = matricula.trim();

                        conteudo.append(TextHelper.rtrim(linha)).append("\n");
                        if (!TextHelper.isNull(rseMatricula) && !rseMatricula.isEmpty()) {
                            ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                            query.rseMatricula = rseMatricula;

                            List<String> servidores = query.executarLista();
                            if (servidores == null || servidores.isEmpty()) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico", responsavel, rseMatricula));
                                continue;
                            } else if (servidores.size() > 1) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo", responsavel, rseMatricula));
                                continue;
                            } else {
                                rseCodigo = servidores.get(0);
                            }
                        }
                        // Linha que contém a LOTAÇÃO do servidor
                    } else if (linha.substring(1, 8).equals("LOTACAO") && conteudo != null) {
                        conteudo.append(TextHelper.rtrim(linha)).append("\n");
                        //Linha que contém os lançamentos no contracheque
                    } else if (TextHelper.isNum(linha.trim().charAt(linha.trim().length() - 1)) && conteudo != null) {
                        conteudo.append(TextHelper.rtrim(linha)).append("\n");
                    }
                    // Linha vazia - append no contracheque
                } else if (conteudo != null) {
                    conteudo.append("\n");

                }
            }

            if (!TextHelper.isNull(rseCodigo)) {
                //Grava o registro do contracheque final
                criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
            } else {
                throw new ImportaContrachequesException("mensagem.erro.contracheque.pesquisar.rse", responsavel);
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
