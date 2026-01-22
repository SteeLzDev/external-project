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
 * <p>Title: ImportaContrachequesLafepe</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema do Laboratório Farmacêutico do Estado de Pernambuco (LAFEPE).</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesLafepe extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesSaoMiguelCampos.class);

    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = null;
        int tamanhoLinha = 0;
        String linhaCabecalho = null;
        int tamanhoLinhaCabecalho = 0;
        StringBuilder conteudo = null;
        String rseCodigo = null;
        String bloco = null;
        int linhaBloco = 0;
        String separador = String.format("%81s", "\n").replace(" ", "-");

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            while ((linha = entrada.readLine()) != null) {
                if (!TextHelper.isNull(linha)) {
                    if (linha.charAt(0) == '2') {
                        bloco = "2";
                        linhaBloco = 1;
                    } else if (linha.charAt(0) == '3') {
                        bloco = "3";
                        linhaBloco = 1;
                    } else if (linha.charAt(0) == '4') {
                        bloco = "4";
                        linhaBloco = 1;
                    } else if (linha.charAt(0) == '5') {
                        bloco = "5";
                        linhaBloco = 1;
                    }

                    if (TextHelper.isNull(bloco)) {
                        continue;
                    }

                    if (bloco.equals("2")) {
                        // Campo           Tipo            Tam Ini Fim Descrição                                   Salta linha
                        // Tipo Registro   Numérico        01  01  02  Fixo: 2                                     NÃO
                        // Nome da Empresa Alfanumérico    37  02  39  Fixo: LAB. FARMACEUTICO DO ESTADO DE PE S/A SIM
                        // Endereco        Alfanumérico    26  02  28  Fixo: LARGO DE DOIS IRMAOS, 1117            SIM
                        // CNPJ            Alfanumérico    14  02  16  CNPJ: 10877926000113                        SIM
                        // Filler          Alfanumérico    05  01  06  Brancos (espaços)                           SIM
                        if (linhaBloco == 1) {
                            conteudo = new StringBuilder();
                            conteudo.append(separador);
                            String nomeEmpresa = linha.substring(1).trim();
                            conteudo.append(String.format("%-80s", nomeEmpresa)).append("\n");
                        } else if (linhaBloco == 2) {
                            String endereco = linha.substring(1).trim();
                            conteudo.append(String.format("%-80s", endereco)).append("\n");
                        } else if (linhaBloco == 3) {
                            String cnpj = linha.substring(1).trim();
                            conteudo.append(String.format("%-80s", cnpj)).append("\n");
                        }
                    } else if (bloco.equals("3")) {
                        // Campo                 Tipo            Tam Ini Fim Descrição                                          Salta linha
                        // Tipo Registro         Numérico        01  01  02  Fixo: 3                                            NÃO
                        // Nome da Empresa       Alfanumérico    37  02  39  Fixo: LAB. FARMACEUTICO DO ESTADO DE PE S/A        NÃO
                        // CNPJ                  Alfanumérico    18  40  58  Fixo: 10.877.926/0001-13                           NÃO
                        // Período               Alfanumérico    06  59  65  Formato: 062015                                    NÃO
                        // Data de Admissão      Data            10  68  78  Formato data: 09/01/2015                           SIM
                        // Matricula             Alfanumérico    06  02  08  Matricula do servidor                              NÃO
                        // Nome do Servidor      Alfanumérico    30  09  39  Nome do servidor                                   NÃO
                        // Cargo                 Alfanumérico    30  40  70  Cargo do servidor                                  NÃO
                        // CBO                   Alfanumérico    05  71  76  Código de referência                               SIM
                        // Sigla Setor alocado   Alfanumérico    30  02  32  Sigla e nome do setor: Exemplo: PRES- PRESIDENCIA  NÃO
                        // Centro de custo       Alfanumérico    04  33  37  Código do centro de custo                          NÃO
                        // Código do Banco       Alfanumérico    03  39  42  Código do Banco: Exemplo 033                       NÃO
                        // Nº Agência            Alfanumérico    04  44  48  Número da Agência                                  NÃO
                        // Nº Conta              Alfanumérico    09  51  60  Número da conta                                    NÃO
                        // Dep. SF               Alfanumérico    02  71  73  Código de referência                               NÃO
                        // Dep. IR               Alfanumérico    02  75  77  Código de referência                               SIM
                        // Código de verba       Alfanumérico    03  02  05  Código de verba: 101                               NÃO
                        // Descrição de verba    Alfanumérico    30  07  37  Código do centro de custo                          NÃO
                        // Valor Referência      Alfanumérico    06  40  46  Formato: 999,99                                    NÃO
                        // Valor provento        Alfanumérico    09  52  61  Formato: 99.999,99                                 NÃO
                        // Valor desconto        Alfanumérico    09  67  76  Formato: 99.999,99                                 SIM
                        if (linhaBloco == 1) {
                            String nomeEmpresa = linha.substring(1,38).trim();
                            String cnpj = linha.substring(39,57).trim();
                            conteudo.append(String.format("%-40s", nomeEmpresa));
                            conteudo.append(String.format("%40s", cnpj)).append("\n");

                            String strPeriodo = linha.substring(58,60).trim() + "/" + linha.substring(60,64).trim();
                            String dtAdmissao = linha.substring(67,77).trim();
                            conteudo.append(String.format("%-40s", strPeriodo));
                            conteudo.append(String.format("%40s", dtAdmissao)).append("\n");
                        } else if (linhaBloco == 2) {
                            String rseMatricula = linha.substring(1,7).trim();
                            ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                            query.rseMatricula = rseMatricula;

                            List<String> servidores = query.executarLista();
                            if (servidores == null || servidores.size() == 0) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.est.org", responsavel, rseMatricula, "", ""));
                                rseCodigo = null;
                                bloco = null;
                                continue;
                            } else if (servidores.size() > 1) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.est.org", responsavel, rseMatricula, "", ""));
                                rseCodigo = null;
                                bloco = null;
                                continue;
                            } else {
                                rseCodigo = servidores.get(0);
                            }
                            String serNome = linha.substring(8,38).trim();
                            String cargo = linha.substring(39,69).trim();

                            if (conteudo != null) {
                                conteudo.append(separador);
                                conteudo.append(String.format("%-14s", "MATRÍCULA"));
                                conteudo.append(String.format("%-40s", "NOME"));
                                conteudo.append(String.format("%-26s", "CARGO")).append("\n");

                                conteudo.append(String.format("%-14s", rseMatricula));
                                conteudo.append(String.format("%-40s", serNome));
                                conteudo.append(String.format("%-26s", cargo)).append("\n");
                            }
                        } else if (linhaBloco == 3) {
                            String linhaSetor = linha.substring(1).trim();
                            if (conteudo != null) {
                                conteudo.append(separador);
                                conteudo.append(String.format("%-80s", linhaSetor)).append("\n");
                                conteudo.append(separador);
                                // cabecalho do corpo do contracheque
                                linhaCabecalho = (String.format("%4s" , "CÓD"));
                                linhaCabecalho += (String.format("%-36s", "  DESCRIÇÃO"));
                                linhaCabecalho +=(String.format("%-5s" , "REF"));
                                linhaCabecalho +=(String.format("%15s", "PROVENTOS(R$)"));
                                linhaCabecalho +=(String.format("%15s", "DESCONTOS(R$)"));
                                tamanhoLinhaCabecalho = linhaCabecalho.length();
                        		conteudo.append(linhaCabecalho).append("\n");
                            }
                        } else {
                            if (conteudo != null) {
                            	//Valida se a linha e o cabeçalho possuem o mesmo tamanho, caso não possuam preenche o restante da linha com espaço vazio
                            	tamanhoLinha = linha.length();
                            	if (tamanhoLinha < tamanhoLinhaCabecalho) {
                            		int quantidadeEspaco = tamanhoLinhaCabecalho - tamanhoLinha;
                            		for (int i = 0; i < quantidadeEspaco; i++) {
                            			linha += " ";
                            		}
                            	}
                                conteudo.append(linha).append("\n");
                            }
                        }
                    } else if (bloco.equals("4") && rseCodigo != null) {
                        // Campo                 Tipo            Tam Ini Fim Descrição          Salta linha
                        // Tipo Registro         Alfanumérico    01  01  02  Fixo: 4            NÃO
                        // Total dos Proventos   Alfanumérico    09  52  61  Formato: 99.999,99 NÃO
                        // Total dos Descontos   Alfanumérico    09  67  76  Formato: 99.999,99 SIM
                        // Cálculo para salário  Alfanumérico    09  52  61  Formato: 99.999,99 NÃO
                        // Total dos Descontos   Alfanumérico    09  67  76  Formato: 99.999,99 SIM
                        if (conteudo != null) {
                            conteudo.append("\n");
                            conteudo.append(linha.substring(1)).append("\n");
                        }
                    } else if (bloco.equals("5")) {
                        // Campo                      Tipo            Tam Ini Fim Descrição           Salta linha
                        // Tipo Registro              Alfanumérico    01  01  02  Fixo: 5             NÃO
                        // Salário base               Alfanumérico    09  03  12  Formato: 99.999,99  NÃO
                        // Salário Contribuição INSS  Alfanumérico    09  17  26  Formato: 99.999,99  NÃO
                        // Base de Calculo FGTS       Alfanumérico    09  32  41  Formato: 99.999,99  NÃO
                        // FGTS do Mês                Alfanumérico    06  52  58  Formato: 999,99     NÃO
                        // Base de Calculo IFFR       Alfanumérico    09  68  77  Formato: 99.999,99  SIM
                        if (conteudo != null && rseCodigo != null) {
                            conteudo.append(separador);
                            conteudo.append(linha.substring(1)).append("\n");
                            conteudo.append(separador);
                            criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                        }
                        // reinicia o bloco
                        bloco = null;
                    }
                    // incrementa linhas dos blocos
                    linhaBloco++;
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