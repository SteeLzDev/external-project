package com.zetra.econsig.folha.contracheque.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.folha.contracheque.AbstractImportaContraCheques;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesParanaiba</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o portal sistema de Paranaíba.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesParanaiba extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesParanaiba.class);

    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = "";
        String rseCodigo = null;

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            int countLinha = 0;
            String[] header = null;
            String[] footer = null;
            List<String[]> verbas = new ArrayList<>();
            while ((linha = entrada.readLine()) != null) { // Percorre o arquivo até o final de linha
                // O primeiro campo identifica cabeçalho (valor 1), descontos e proventos (valor 2) e rodapé (valor 3)
                if (linha.startsWith("1;")) {
                    header = linha.split(";");
                    verbas.clear();
                    continue;
                } else if (linha.startsWith("2;")) {
                    verbas.add(linha.split(";"));
                    continue;
                } else if (linha.startsWith("3;")) {
                    footer = linha.split(";");
                } else { // Ignora linha desconhecida
                    continue;
                }

                countLinha++; // Número de registros

                try {
                    // Início de um contracheque: Paranaiba (separador ";")
                    String rseMatricula = header[2];
                    String cpf = TextHelper.format(header[15], "###.###.###-##"); ;

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = cpf;


                    List<String> servidores = query.executarLista();
                    if (servidores == null || servidores.size() == 0) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico", responsavel, rseMatricula));
                        continue;
                    } else if (servidores.size() > 1) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo", responsavel, rseMatricula));
                        continue;
                    } else {
                        rseCodigo = servidores.get(0);
                    }

                    try {
                        String conteudo = formataContracheque(header,footer,verbas);
                        criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo);
                        header=null; footer=null; verbas.clear();
                    } catch (ParseException e) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.valor", responsavel, rseMatricula));
                        continue;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.linha", responsavel,String.valueOf(countLinha)));
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

    private String formataContracheque(String [] header, String[] footer, List<String[]> verbas) throws ParseException {
        int qtdeMinItensDetalhe = 12;
    	int largura = 88;

    	String razaoSocial = "PREFEITURA MUN DE CARMO DO PARANAIBA";
        String cnpj = "18.602.029/0001-09";
    	String referenciaContracheque = header[6].trim();
    	String cargo = header[9].trim();
    	String serMatriculaNome = header[2].trim() + " " + header[3].trim();
    	String categoria = header[19].trim();
    	String dotacao = header[1];
        String dtAdmissao = header[7].trim();
        String nivel = header[16];
        String agencia = header[10];

        // valores
        String totalProventosDesc = "Total Vencimentos";
        String totalProventosVlr = footer[3].trim();

        String totalDescontosDesc = "Total Descontos";
        String totalDescontosVlr = footer[4].trim();

        String totalLiquidoDesc = "Valor Líquido";
        String totalLiquidoVlr = footer[7].trim();

        String salarioBaseDesc = "Salário Base";
        String salarioBaseVlr = footer[9].trim();

        String baseInssDesc = "Base Cont. Prev.";
        String baseInssVlr = footer[10].trim();;

        String baseIrrfDesc = "Base Calc. IRRF";
        String baseIrrfVlr = footer[13].trim();

        String baseFgtsDesc = "Base Dep. FGTS";
        String baseFgtsVlr = footer[11].trim();

        String fgtsMesDesc = "Valor FGTS";
        String fgtsMesVlr = footer[12].trim();;

        String faixaIrrfDesc = "Faixa IRRF";
        String faixaIrrfVlr = footer[15].trim();

        StringBuilder detalhe = new StringBuilder();
        StringBuilder vencimento = new StringBuilder();
        StringBuilder desconto = new StringBuilder();

        Iterator<String[]> it = verbas.iterator();
        int qtdeLancamentos = 0;
        String[] linhaVerbas = null;
        while (it.hasNext()) {
            // Cada linha com verba pode possuir 2 verbas, um vencimento e um desconto
            linhaVerbas = it.next();

            // Primeira verba da linha (vencimento)
            String descEvento = null;
            String referencia = null;
            String vlrVencEvento = null;
            String vlrDescEvento = null;
            String codVerba = linhaVerbas[3].trim();
            if (codVerba != null && !codVerba.isEmpty()) {
                qtdeLancamentos++;
                descEvento = linhaVerbas[4].trim();
                referencia = linhaVerbas[5].trim();
                vlrVencEvento = linhaVerbas[7].trim();
                vlrDescEvento = "";

                vencimento.append(String.format("%6s", codVerba)).append(" ");
                vencimento.append(String.format("%-30s", (descEvento.toString().length() < 30 ? descEvento : descEvento.substring(0, 29))));
                vencimento.append(String.format("%16s", referencia)).append(" ");
                vencimento.append(String.format("%16s", formataValores(vlrVencEvento))).append(" ");
                vencimento.append(String.format("%16s", "")).append("\n");
            }

            // Segunda verba da linha (desconto)
            codVerba = linhaVerbas[8].trim();
            if (codVerba != null && !codVerba.isEmpty()) {
                qtdeLancamentos++;
                descEvento = linhaVerbas[9].trim();
                referencia = linhaVerbas[10].trim();
                vlrVencEvento = "";
                vlrDescEvento = linhaVerbas[12].trim();

                desconto.append(String.format("%6s", codVerba)).append(" ");
                desconto.append(String.format("%-30s", (descEvento.toString().length() < 30 ? descEvento : descEvento.substring(0, 29))));
                desconto.append(String.format("%16s", referencia)).append(" ");
                desconto.append(String.format("%16s", " "));
                desconto.append(String.format("%16s", formataValores(vlrDescEvento))).append("\n");
            }
        }
        detalhe.append(vencimento).append(desconto);
        // garante quantidade mínima de linhas no detalhe do contracheque para manter um tamanho mínimo
        for (int i = qtdeLancamentos; i <= qtdeMinItensDetalhe; i++) {
            detalhe.append("\n");
        }

        String separador = String.format("%" + largura + "s", "\n").replace(" ", "-");

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(String.format("%-57s", razaoSocial));
        contracheque.append(String.format("Periodo: %-15s", referenciaContracheque)).append("\n");
        contracheque.append(String.format("%-30s", "CGC: " + cnpj)).append("\n");
        contracheque.append(separador);

        contracheque.append(String.format("%-57s", "Funcionário " + categoria));
        contracheque.append(String.format("%-15s", "Dotação         " + dotacao)).append("\n");
        contracheque.append(String.format("%-57s", (serMatriculaNome.toString().length() < 57 ? serMatriculaNome : serMatriculaNome.substring(0, 56))));
        contracheque.append(String.format("%-15s", "Admissão        " + dtAdmissao)).append("\n");
        contracheque.append(String.format("%-57s", "          " + cargo));
        contracheque.append(String.format("%-15s", "Nível/Grau      " + nivel)).append("\n");
        contracheque.append(String.format("%-57s", ""));

        //Agencia pode vir vazio
        Integer agenciaInteger = null;
        if (!agencia.trim().equals("") && !agencia.trim().equals("-")) {
            agenciaInteger = Integer.parseInt(agencia.split("-")[0]);
        }

        contracheque.append(String.format("%-15s", "CAIXA FEDERAL   " + (agenciaInteger != null ? agenciaInteger : ""))).append("\n");

        contracheque.append(separador);

        // detalhe
        contracheque.append(String.format("%6s", "CÓD")).append(" ");
        contracheque.append(String.format("%-30s", "DESCRIÇÃO"));
        contracheque.append(String.format("%16s", "REFERÊNCIA")).append(" ");
        contracheque.append(String.format("%16s", "VENCIMENTOS")).append(" ");
        contracheque.append(String.format("%16s", "DESCONTOS")).append("\n");

        contracheque.append(separador);

        contracheque.append(detalhe.toString());

        contracheque.append(separador);

        // total vencimentos
        contracheque.append(String.format("%23s", totalProventosDesc));
        contracheque.append(String.format("%17s", formataValores(totalProventosVlr)));

        // total descontos
        contracheque.append(String.format("%23s", totalDescontosDesc));
        contracheque.append(String.format("%17s", formataValores(totalDescontosVlr)));
        contracheque.append("\n");

        // total liquido
        contracheque.append(String.format("%40s", ""));
        contracheque.append(String.format("%23s", totalLiquidoDesc));
        contracheque.append(String.format("%17s", formataValores(totalLiquidoVlr)));
        contracheque.append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%23s", salarioBaseDesc));
        contracheque.append(String.format("%17s", formataValores(salarioBaseVlr)));
        contracheque.append(String.format("%23s", baseInssDesc));
        contracheque.append(String.format("%17s", formataValores(baseInssVlr))).append("\n");

        contracheque.append(String.format("%23s", baseFgtsDesc));
        contracheque.append(String.format("%17s", formataValores(baseFgtsVlr)));
        contracheque.append(String.format("%23s", fgtsMesDesc));
        contracheque.append(String.format("%17s", formataValores(fgtsMesVlr))).append("\n");

        contracheque.append(String.format("%23s", baseIrrfDesc));
        contracheque.append(String.format("%17s", formataValores(baseIrrfVlr)));
        contracheque.append(String.format("%23s", faixaIrrfDesc));
        contracheque.append(String.format("%17s", formataValores(faixaIrrfVlr))).append(" \n");

        contracheque.append(separador);

        return contracheque.toString();
    }

    private String formataValores (String valor) throws ParseException {
        StringBuilder formatado = new StringBuilder();
        formatado.append(valor.substring(0, valor.length() - 2)).append(",").append(valor.substring(valor.length() - 2));
        Double vlrDouble = Double.valueOf(NumberHelper.parse(formatado.toString(), NumberHelper.getLang()));
        return NumberHelper.reformat(vlrDouble.toString(), "en", NumberHelper.getLang());
    }
}
