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
 * <p>Title: ImportaContrachequesDiamantina</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Prefeitura Municipal de Diamantina.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesDiamantina extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesDiamantina.class);

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
                            // [029-041] 9(13) Conta Favorecido
                            // [042-043] 9(02) Digito Conta
                            // [044-054] 9(11) Cpf Funcionario
                            // [055-068] 9(14) Pis Funcionario
                            // [069-081] 9(13) ident Funcionario
                            // [082-090] 9(09) Ctps  Funcionario
                            // [091-120] x(30) Nome  Funcionario
                            // [121-132] x(12) Matrícula
                            // [133-136] x(4) Código Cargo Funcionario
                            // [137-172] x(40) Cargo Funcionario
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
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.est.org", responsavel, estIdentificador, orgIdentificador, rseMatricula, serCpf));
                                rseCodigo = null;
                                continue;
                            } else if (servidores.size() > 1) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.est.org", responsavel, estIdentificador, orgIdentificador, rseMatricula, serCpf));
                                continue;
                            } else {
                                rseCodigo = servidores.get(0);
                            }

                            // limita o nome ao tamanho disponível na tela para este campo (28)
                            String serNome = linha.substring(90, 118).trim();
                            String codCargo = linha.substring(132, 136).trim();
                            String cargo = codCargo + " - " + linha.substring(136, 172).trim();
                            // limita o cargo ao tamanho disponível na tela para este campo (28)
                            if (cargo.length() > 28) {
                                cargo = cargo.substring(0, 28);
                            }
                            String rseDataAdmissao = DateHelper.reformat(linha.substring(172, 180), "ddMMyyyy", "dd/MM/yyyy");

                            String nivel = linha.substring(180, 182).trim();
                            String padrao = linha.substring(182, 184).trim();
                            String codBanco = linha.substring(19, 23).trim();
                            if (codBanco.length() > 3) {
                                codBanco = codBanco.replaceAll("^0","");
                            }
                            String banco = codBanco + " - " + linha.substring(184, 203).trim();
                            String conta = linha.substring(29, 41).trim().replaceAll("^0+", "") + "-" + linha.substring(41, 43).trim();
                            String lotacao = linha.substring(203, 233).trim();
                            // limita a situação ao tamanho disponível na tela para este campo (11)
                            String situacao = linha.substring(233, 244).trim();

                            conteudo = new StringBuilder();
                            conteudo.append(separador);
                            conteudo.append(String.format("%-80s", "PREFEITURA MUNICIPAL DE DIAMANTINA - CNPJ: 17.754.136/0001-90")).append("\n");

                            conteudo.append(separador);
                            conteudo.append(String.format("%-13s", "NOME"));
                            conteudo.append(String.format("%-29s", serNome));
                            conteudo.append(String.format("%-10s", "MATRÍCULA"));
                            conteudo.append(String.format("%-8s", rseMatricula));
                            conteudo.append(String.format("%-9s", "ADMISSÃO"));
                            conteudo.append(String.format("%-11s", rseDataAdmissao)).append("\n");

                            conteudo.append(String.format("%-13s", "CARGO/FUNÇÃO"));
                            conteudo.append(String.format("%-29s", cargo));
                            conteudo.append(String.format("%-6s", "NÍVEL"));
                            conteudo.append(String.format("%-12s", nivel));
                            conteudo.append(String.format("%-7s", "PADRÃO"));
                            conteudo.append(String.format("%-13s", padrao)).append("\n");

                            conteudo.append(String.format("%-13s", "BANCO"));
                            conteudo.append(String.format("%-24s", banco));
                            conteudo.append(String.format("%-15s", "CONTA BANCÁRIA"));
                            conteudo.append(String.format("%-8s", conta));
                            conteudo.append(String.format("%-9s", "SITUAÇÃO"));
                            conteudo.append(String.format("%-11s", situacao)).append("\n");

                            conteudo.append(String.format("%-13s", "LOTAÇÃO"));
                            conteudo.append(String.format("%-67s", lotacao)).append("\n");

                            conteudo.append(separador);
                            conteudo.append(String.format("%-14s", "CÓDIGO"));
                            conteudo.append(String.format("%-56s", "DESCRIÇÃO"));
                            conteudo.append(String.format("%-10s", "VALOR (R$)")).append("\n");
                        } else if (linha.charAt(0) == '2' && rseCodigo != null) {
                            // Registro debito / credito tipo (2)
                            // [001-001] 9(01) Tipo Registro
                            // [002-005] x(04) Codigo Lancamento
                            // [006-045] x(40) Descricao Lancamento
                            // [046-057] 9(12) Valor
                            // [058-058] 9(1) Fixo
                            // [059-245] x(187) Filler
                            // [246-250] x(05) Sequencia arquivo
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
