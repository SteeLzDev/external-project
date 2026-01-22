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
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesPaulista</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para o sistema da Prefeitura. da cidade do Paulista.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesPaulista extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesPaulista.class);

    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = null;
        String rseCodigo = null;

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            int countLinha = 0;
            while ((linha = entrada.readLine()) != null) {
                linha = linha.replaceAll("\"", "");
                String[] campos = linha.split(",");

                countLinha++;
                try {
                    // Início de um contracheque: Prefeitura da cidade do Paulista (separador "\""\" - ",")
                    // rseMatricula: campo de índice 4
                    // serCpf: campo de índice 5

                    // Definir parâmetros
                    String serCpf = TextHelper.format(campos[12].replace("CPF:", "").trim(), "###.###.###-##");
                    String rseMatricula = Integer.valueOf(campos[10].trim()).toString();
                    String orgIdentificador = "";

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = serCpf;

                    List<String> servidores = query.executarLista();
                    if (servidores == null || servidores.size() == 0) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.cpf", responsavel, rseMatricula, serCpf));
                        continue;
                    } else if (servidores.size() > 1) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.cpf", responsavel, rseMatricula, serCpf));
                        continue;
                    } else {
                        rseCodigo = servidores.get(0);
                    }

                    try {
                        String conteudo = formataContracheque(campos);
                        criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo);
                    } catch (ParseException e) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.valor.cpf.secretaria", responsavel, rseMatricula, serCpf, orgIdentificador));
                        continue;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.linha", responsavel, String.valueOf(countLinha)));
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

    private String formataContracheque(String[] campos) throws ParseException {
        int largura = 120;

        String razaoSocial = campos[2].trim();
        String orgaoNome = campos[19].trim();
        String cnpj = TextHelper.format(campos[3].replaceAll("CNPJ:", "").trim(), "##.###.###/####-##");
        String cargo = campos[28].trim();
        String cbo = campos[55].trim();
        String classe = "";
        String nivel = "";
        String matricula = campos[10].trim();
        String serNome = campos[17].trim();
        String serCpf = TextHelper.format(campos[12].replace("CPF:", "").trim(), "###.###.###-##");
        String mesAno = campos[34].trim();
        String dtPagamento = campos[35].trim();
        String dtAdmissao = !TextHelper.isNull(campos[36]) ? campos[36].replaceAll("ao", "ão").trim() : "";
        String conta = campos[30].trim();
        String endereco1 = campos[4].trim() + ", " + campos[5].trim();
        String endereco2 = campos[6].trim() + " - " + campos[7].trim() + " - " + campos[8].trim();

        String totalVencimentos = campos[43].trim() + "," + campos[44].trim();
        String totalDescontos = campos[52].trim() + "," + campos[53].trim();
        String totalLiquido = campos[50].trim() + "," + campos[51].trim();
        String salarioBase = campos[13].trim() + "," + campos[14].trim();
        String basePrevidencia = campos[21].trim() + "," + campos[22].trim();
        String baseIrrf = campos[46].trim() + "," + campos[47].trim();
        String baseFgts = campos[24].trim() + "," + campos[25].trim();
        String fgtsMes = campos[37].trim() + "," + campos[38].trim();

        String mensagem = campos[0].trim();

        StringBuilder detalhe = new StringBuilder();

        int qtdeLancamentos = 0;
        // garante pelo menos sete linhas no detalhe do contracheque para manter um tamanho mínimo
        for (int i = qtdeLancamentos; i <= 7; i++) {
            detalhe.append("\n");
        }

        String separador = String.format("%" + largura + "s", "\n").replace(" ", "-");

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(String.format("%-47s", razaoSocial));
        contracheque.append(String.format("%72s", "RECIBO DE PAGAMENTO DE SALÁRIO")).append("\n");

        contracheque.append(String.format("%-34s", "CNPJ: " + cnpj));
        contracheque.append(String.format("%85s", mesAno)).append("\n");

        contracheque.append(String.format("%-40s", endereco1));
        contracheque.append(String.format("%79s", dtPagamento)).append("\n");

        contracheque.append(String.format("%-40s", endereco2));
        contracheque.append(String.format("%72s", dtAdmissao)).append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%-10s", "MATRÍCULA"));
        contracheque.append(String.format("%-46s", "NOME DO FUNCIONÁRIO"));
        contracheque.append(String.format("%-30s", "CARGO"));
        contracheque.append(String.format("%11s", "CBO"));
        contracheque.append(String.format("%11s", "CLASSE"));
        contracheque.append(String.format("%11s", "NÍVEL")).append("\n");

        contracheque.append(String.format("%-10s", matricula));
        contracheque.append(String.format("%-46s", (serNome.toString().length() < 46 ? serNome : serNome.substring(0, 45))));
        contracheque.append(String.format("%-30s", (cargo.toString().length() < 30 ? cargo : cargo.substring(0, 29))));
        contracheque.append(String.format("%11s", cbo));
        contracheque.append(String.format("%11s", classe));
        contracheque.append(String.format("%11s", nivel)).append("\n");

        contracheque.append(String.format("%-19s", "CPF: " + serCpf));
        contracheque.append(String.format("%50s", (orgaoNome.toString().length() < 50 ? orgaoNome : orgaoNome.substring(0, 49))));
        contracheque.append(String.format("%50s", (conta.toString().length() < 50 ? conta : conta.substring(0, 49)))).append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%5s", "CÓD.")).append(" ");
        contracheque.append(String.format("%-41s", "DESCRIÇÃO"));
        contracheque.append(String.format("%-10s", "REFERÊNCIA"));
        contracheque.append(String.format("%30s", "VENCIMENTOS"));
        contracheque.append(String.format("%30s", "DESCONTOS")).append("\n");

        contracheque.append(detalhe.toString());

        contracheque.append(separador);

        contracheque.append(String.format("%-60s", (mensagem.toString().length() < 60 ? mensagem.trim() : mensagem.substring(0, 59).trim())));
        contracheque.append(String.format("%37s", "TOTAL VENCIMENTOS"));
        contracheque.append(String.format("%20s", "TOTAL DESCONTOS")).append("\n");
        contracheque.append(String.format("%-60s", (mensagem.toString().length() > 60 ? mensagem.substring(59, Math.min(119, mensagem.toString().length())).trim() : "")));
        contracheque.append(String.format("%37s", totalVencimentos));
        contracheque.append(String.format("%20s", totalDescontos)).append("\n");
        contracheque.append(String.format("%-60s", (mensagem.toString().length() > 120 ? mensagem.substring(119, Math.min(179, mensagem.toString().length())).trim() : "")));
        contracheque.append(String.format("%37s", "VALOR LÍQUIDO ->"));
        contracheque.append(String.format("%20s", totalLiquido)).append("\n");
        contracheque.append(String.format("%-117s", (mensagem.toString().length() > 180 ? mensagem.substring(179, Math.min(239, mensagem.toString().length())).trim() : ""))).append("\n");
        contracheque.append(String.format("%-117s", (mensagem.toString().length() > 240 ? mensagem.substring(239, Math.min(299, mensagem.toString().length())).trim() : ""))).append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%-13s", "SALÁRIO BASE"));
        contracheque.append(String.format("%27s", "BASE CÁLC. PREVIDÊNCIA"));
        contracheque.append(String.format("%25s", "BASE CÁLC. FGTS"));
        contracheque.append(String.format("%25s", "FGTS DO MÊS"));
        contracheque.append(String.format("%25s", "BASE CÁLC. IRRF")).append("\n");

        contracheque.append(String.format("%11s", salarioBase));
        contracheque.append(String.format("%29s", basePrevidencia));
        contracheque.append(String.format("%25s", baseFgts));
        contracheque.append(String.format("%25s", fgtsMes));
        contracheque.append(String.format("%25s", baseIrrf)).append("\n");

        contracheque.append(separador);

        return contracheque.toString();
    }
}
