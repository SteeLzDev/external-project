package com.zetra.econsig.folha.contracheque.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.folha.contracheque.AbstractImportaContraCheques;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesEsmeraldas</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Pref. de Esmeraldas.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesEsmeraldas extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesEsmeraldas.class);

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

        String separador = String.format("%81s", "\n").replace(" ", "-");

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            while ((linha = entrada.readLine()) != null) {
                try {
                    if (!TextHelper.isNull(linha)) {
                        if (linha.charAt(0) == '0') {
                            // Header da empresa tipo (0)
                            // [001-001] 9(01) Tipo Registro
                            // [002-021] x(20) Descricao do arquivo
                            // [022-030] 9(09) Codigo da Empresa
                            // [031-039] 9(09) Numero do Lote
                            // [040-054] 9(15) Inscricao Empresa
                            // [055-062] 9(08) Data Movto DDMMAAAA
                            // [063-063] X(01) referencia da operacao
                            // [064-068] X(05) Fixo cod lancamento
                            // [069-223] X(155) Filler
                            // [224-224] X(01)  Prod<Branco>/Teste <T>
                            // [225-233] X(09) Reservado
                            // [234-245] X(09) Reservado
                            // [246-250] 9(05) Sequencia arquivo

                        } else if (linha.charAt(0) == '1') {
                            // Registro funcionario tipo (1)
                            // [001-001] 9(01) Tipo Registro
                            // [002-002] X(01) referencia da operacao
                            // [003-005] 9(03) tipo de Comprovante
                            // [006-011] 9(06) Mes Ano Referencioa
                            // [012-019] 9(08) Data Liberacao DDMMAAAA
                            // [020-023] 9(04) Banco Favorecido
                            // [023-028] 9(05) agencia Favorecido
                            // [029-041] 9(13) agencia Favorecido
                            // [042-043] 9(02) Digito Conta
                            // [044-054] 9(11) Cpf Funcionario
                            // [055-068] 9(14) Pis Funcionario
                            // [069-081] 9(13) ident Funcionario
                            // [082-090] 9(09) Ctps  Funcionario
                            // [091-120] x(30) Nome  Funcionario
                            // [121-132] x(12) Cargo Funcionario *
                            // [133-172] x(40) Cargo Funcionario
                            // [173-180] x(08) Admissao Funcionario
                            // [181-233] x(53) Filer
                            // [234-245] x(12) reservado
                            // [246-250] 9(05) Sequencia arquivo

                            String estIdentificador = "001";
                            String orgIdentificador = "001";
                            String rseMatricula = String.valueOf(Long.parseLong(linha.substring(120, 132).trim()));
                            String serCpf = TextHelper.format(linha.substring(43, 54), "###.###.###-##");

                            ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                            query.estIdentificador = estIdentificador;
                            query.orgIdentificador = orgIdentificador;
                            query.rseMatricula = rseMatricula;
                            query.serCPF = serCpf;

                            List<String> servidores = query.executarLista();
                            if (servidores == null || servidores.size() == 0) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.est.org", responsavel, rseMatricula, serCpf, orgIdentificador));
                                rseCodigo = null;
                                continue;
                            } else if (servidores.size() > 1) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.est.org", responsavel, rseMatricula, serCpf, orgIdentificador));
                                continue;
                            } else {
                                rseCodigo = servidores.get(0);
                            }

                            String serNome = linha.substring(90, 120).trim();
                            String rseTipo = linha.substring(132, 172).trim();
                            String rseDataAdmissao = DateHelper.reformat(linha.substring(172, 180), "ddMMyyyy", "dd/MM/yyyy");

                            conteudo = new StringBuilder();
                            conteudo.append(separador);
                            conteudo.append(String.format("%-14s", "MATRÍCULA"));
                            conteudo.append(String.format("%-51s", "NOME DO FUNCIONÁRIO"));
                            conteudo.append(String.format("%-15s", "CPF")).append("\n");

                            conteudo.append(String.format("%-14s", rseMatricula));
                            conteudo.append(String.format("%-51s", serNome));
                            conteudo.append(String.format("%-15s", serCpf)).append("\n");

                            conteudo.append(String.format("%-14s", "ADMISSÃO"));
                            conteudo.append(String.format("%-66s", "CARGO")).append("\n");

                            conteudo.append(String.format("%-14s", rseDataAdmissao));
                            conteudo.append(String.format("%-66s", rseTipo)).append("\n");

                            conteudo.append(separador);
                            conteudo.append(String.format("%-14s", "CÓDIGO"));
                            conteudo.append(String.format("%-56s", "DESCRIÇÃO"));
                            conteudo.append(String.format("%-10s", "VALOR (R$)")).append("\n");

                        } else if (linha.charAt(0) == '2' && rseCodigo != null) {
                            // Registro debito / credito tipo (2)
                            // [001-001] 9(01) Tipo Registro
                            // [002-005] x(04) Codigo Lancamento
                            // [006-025] x(20) Descricao Lancamento
                            // [026-037] 9(12) Descricao Lancamento
                            // [038-038] 9(1) Descricao Lancamento
                            // [039-236] x(198) Filler
                            // [234-245] x(09) reservado
                            // [246-250] 9(05) Sequencia arquivo

//                            [001-001] 9(01) Tipo Registro
//                            [002-005] x(04) Codigo Lancamento
//                            [006-045] x(40) Descricao Lancamento
//                            [046-057] 9(12) Valor
//                            [058-058] 9(1) Fixo
//                            [059-245] x(187) Filler
//                            [246-250] x(05) Sequencia arquivo

                            String codigo = linha.substring(1, 5).trim();
                            String descricao = linha.substring(5, 45).trim();
                            String valor = NumberHelper.format((Long.parseLong(linha.substring(45, 57).trim()) / 100.00), "pt");

                            if (conteudo != null) {
                                conteudo.append(String.format("%-14s", codigo));
                                conteudo.append(String.format("%-51s", descricao));
                                conteudo.append(String.format("%15s", valor)).append("\n");
                            }

                        } else if (linha.charAt(0) == '3' && rseCodigo != null) {
                            // Registro menssagem tipo (3)
                            // [001-001] 9(01) Tipo Registro
                            // [002-041] 9(40) Descricao Lancamento
                            // [042-236] X(198) Filler
                            // [237-245] x(09) reservado
                            // [246-250] 9(05) Sequencia arquivo
                            if (conteudo != null) {
                                conteudo.append(separador);
                                conteudo.append(linha.substring(1, 41).trim()).append("\n");
                            }

                        } else if (linha.charAt(0) == '4' && rseCodigo != null) {
                            // Registro informacoes tipo (4)
                            // [001-001] 9(01) Tipo Registro
                            // [002-009] 9(08) Data Pagamento DDMMAAAA
                            // [010-011] 9(02) Qtde Dep IR
                            // [012-013] 9(02) Qtde Dep SF
                            // [014-015] 9(02) Qtde Horas Semanal
                            // [016-027] 9(12) Salario Base
                            // [028-029] 9(02) Qtde Faltas
                            // [030-030] 9(01) Indicador de impressao
                            // [031-038] 9(08) Inicio Periodo Aquisitivo
                            // [039-046] 9(08) Final Periodo Aquisitivo
                            // [047-054] 9(08) Inicio Periodo Gozo
                            // [055-062] 9(08) Final Periodo Gozo
                            // [063-074] 9(12) Valor base INSS
                            // [075-086] 9(12) Valor base INSS 13
                            // [087-098] 9(12) Valor base IRRF
                            // [099-110] 9(12) Valor base IRRF 13
                            // [111-122] 9(12) Valor base IRRF ferias
                            // [123-134] 9(12) Valor base IRRF PPR
                            // [135-146] 9(12) Valor base FGTS
                            // [147-158] 9(12) Valor FGTS
                            // [159-236] X(198) Filler
                            // [237-245] x(09) reservado
                            // [246-250] 9(05) Sequencia arquivo

                            String dataPagamento = linha.substring(1, 9);
                            String dataIniAquisitivo = linha.substring(30, 38);
                            String dataFimAquisitivo = linha.substring(38, 46);
                            String dataIniGozo = linha.substring(46, 54);
                            String dataFimGozo = linha.substring(54, 62);

                            String qtdHorasSemanal = linha.substring(13, 15).trim();
                            String qtdFaltas = linha.substring(27, 29).trim();

                            String vlrSalarioBase = NumberHelper.format((Long.parseLong(linha.substring(15, 27).trim()) / 100.00), "pt");
                            String vlrInssBase = NumberHelper.format((Long.parseLong(linha.substring(62, 74).trim()) / 100.00), "pt");
                            String vlrInssBase13 = NumberHelper.format((Long.parseLong(linha.substring(74, 86).trim()) / 100.00), "pt");
                            String vlrIrrfBase = NumberHelper.format((Long.parseLong(linha.substring(86, 98).trim()) / 100.00), "pt");
                            String vlrIrrfBase13 = NumberHelper.format((Long.parseLong(linha.substring(98, 110).trim()) / 100.00), "pt");
                            String vlrIrrfBaseFerias = NumberHelper.format((Long.parseLong(linha.substring(110, 122).trim()) / 100.00), "pt");
                            String vlrIrrfBasePPR = NumberHelper.format((Long.parseLong(linha.substring(122, 134).trim()) / 100.00), "pt");
                            String vlrFgtsBase = NumberHelper.format((Long.parseLong(linha.substring(134, 146).trim()) / 100.00), "pt");
                            String vlrFgts = NumberHelper.format((Long.parseLong(linha.substring(146, 158).trim()) / 100.00), "pt");

                            if (conteudo != null) {
                                conteudo.append(separador);

                                if (!dataPagamento.equals("00000000")) {
                                    conteudo.append(String.format("%-60s", "DATA PAGAMENTO"));
                                    conteudo.append(String.format("%20s", DateHelper.reformat(dataPagamento, "ddMMyyyy", "dd/MM/yyyy"))).append("\n");
                                }
                                if (!dataIniAquisitivo.equals("00000000")) {
                                    conteudo.append(String.format("%-60s", "INÍCIO PERÍODO AQUISITIVO"));
                                    conteudo.append(String.format("%20s", DateHelper.reformat(dataIniAquisitivo, "ddMMyyyy", "dd/MM/yyyy"))).append("\n");
                                }
                                if (!dataFimAquisitivo.equals("00000000")) {
                                    conteudo.append(String.format("%-60s", "FINAL PERÍODO AQUISITIVO"));
                                    conteudo.append(String.format("%20s", DateHelper.reformat(dataFimAquisitivo, "ddMMyyyy", "dd/MM/yyyy"))).append("\n");
                                }
                                if (!dataIniGozo.equals("00000000")) {
                                    conteudo.append(String.format("%-60s", "INÍCIO PERÍODO GOZO"));
                                    conteudo.append(String.format("%20s", DateHelper.reformat(dataIniGozo, "ddMMyyyy", "dd/MM/yyyy"))).append("\n");
                                }
                                if (!dataFimGozo.equals("00000000")) {
                                    conteudo.append(String.format("%-60s", "FINAL PERÍODO GOZO"));
                                    conteudo.append(String.format("%20s", DateHelper.reformat(dataFimGozo, "ddMMyyyy", "dd/MM/yyyy"))).append("\n");
                                }

                                conteudo.append(String.format("%-60s", "QTDE HORAS SEMANAL"));
                                conteudo.append(String.format("%20s", qtdHorasSemanal)).append("\n");

                                conteudo.append(String.format("%-60s", "QTDE FALTAS"));
                                conteudo.append(String.format("%20s", qtdFaltas)).append("\n");

                                conteudo.append(String.format("%-60s", "SALÁRIO BASE (R$)"));
                                conteudo.append(String.format("%20s", vlrSalarioBase)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE INSS (R$)"));
                                conteudo.append(String.format("%20s", vlrInssBase)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE INSS 13 (R$)"));
                                conteudo.append(String.format("%20s", vlrInssBase13)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE IRRF (R$)"));
                                conteudo.append(String.format("%20s", vlrIrrfBase)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE IRRF 13 (R$)"));
                                conteudo.append(String.format("%20s", vlrIrrfBase13)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE IRRF FÉRIAS (R$)"));
                                conteudo.append(String.format("%20s", vlrIrrfBaseFerias)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE IRRF PPR (R$)"));
                                conteudo.append(String.format("%20s", vlrIrrfBasePPR)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR BASE FGTS (R$)"));
                                conteudo.append(String.format("%20s", vlrFgtsBase)).append("\n");

                                conteudo.append(String.format("%-60s", "VALOR FGTS (R$)"));
                                conteudo.append(String.format("%20s", vlrFgts)).append("\n");
                            }

                        } else if (linha.charAt(0) == '5') {
                            // Registro total de lancamentos tipo (5)
                            // [001-001] 9(01) Tipo Registro
                            // [102-006] 9(05) total lancamnetos
                            // [007-236] X(198) Filler
                            // [237-245] x(09) reservado
                            // [246-250] 9(05) Sequencia arquivo

                            if (conteudo != null && rseCodigo != null) {
                                conteudo.append(separador);
                                criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                            }

                        } else if (linha.charAt(0) == '9') {
                            // Registro total do lote tipo(9)
                            // [001-001] 9(01) Tipo Registro
                            // [002-006] 9(05) total lotes
                            // [007-236] X(198) Filler
                            // [237-245] x(09) reservado
                            // [246-250] 9(05) Sequencia arquivo
                        }
                    }
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage());
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
