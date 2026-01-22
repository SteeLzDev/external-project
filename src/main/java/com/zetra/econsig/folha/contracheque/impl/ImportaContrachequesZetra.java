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
 * <p>Title: ImportaContrachequesZetra</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o portal do funcionário da Zetra.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesZetra extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesZetra.class);

    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {
        BufferedReader entrada = null;
        String linha = null;
        String rseCodigo = null;

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            int countLinha = 0;
            while ((linha = entrada.readLine()) != null) {
                String [] campos = linha.split(";");

                countLinha++;
                try {
                    // Início de um contracheque: Zetra (separador ";")
                    // rseMatricula: campo de índice 10 (a matrícula e o nome do servidor estão no mesmo campo separados por hifen)
                    String [] servidor = campos[10].split("-");
                    String rseMatricula = Integer.valueOf(servidor[0].trim()).toString();
                    String [] campoCNPJ = campos[5].split(":");
                    String cnpj = null;
                    if (campoCNPJ.length == 2) {
                        cnpj = campoCNPJ[1].trim();
                    }

                    // o registro do servidor será pesquisado apenas pela matrícula, já que o cpf não está presente no contracheque
                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.orgCnpj = cnpj;

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
                        String conteudo = formataContracheque(campos);
                        criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo);
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

    private String formataContracheque(String [] campos) throws ParseException {
        int primeiraColunaDetalhe = 20;
        int ultimaColunaDetalhe = 91;
        int qtdeColunasDetalhe = 6;
        int qtdeMinItensDetalhe = 12;
    	int largura = 88;

    	String razaoSocial = campos[0].trim();
        String orgaoNome = campos[1].trim();
    	String unidade = campos[2].trim();
    	String referenciaContracheque = campos[3].trim();
    	String cnpj = campos[5].trim();
    	String cargo = campos[6].trim();
    	String depto = campos[7].trim();
    	String mesAno = campos[8].trim();
    	String serMatriculaNome = campos[10].trim();
    	String categoria = campos[11].trim();
    	String cbo = campos[12].trim();
        String dtAdmissao = campos[13].trim();

        // valores
        String totalProventosDesc = campos[94].trim();
        String totalProventosVlr = campos[95].trim();

        String totalDescontosDesc = campos[99].trim();
        String totalDescontosVlr = campos[101].trim();

        String totalLiquidoDesc = campos[103].trim();
        String totalLiquidoVlr = campos[105].trim();

        String [] salarioBase = campos[107].split(":");
        String salarioBaseDesc = salarioBase[0].trim().toString().concat(":");
        String salarioBaseVlr = "";
        if (salarioBase != null && salarioBase.length == 2) {
            salarioBaseVlr = salarioBase[1].trim();
        }

        String [] baseInss = campos[111].split(":");
        String baseInssDesc = baseInss[0].trim().toString().concat(":");
        String baseInssVlr = "";
        if (baseInss != null && baseInss.length == 2) {
            baseInssVlr = baseInss[1].trim();
        }

        String [] baseIrrf = campos[109].split(":");
        String baseIrrfDesc = baseIrrf[0].trim().toString().concat(":");
        String baseIrrfVlr = "";
        if (baseIrrf != null && baseIrrf.length == 2) {
            baseIrrfVlr = baseIrrf[1].trim();
        }

        String [] baseFgts = campos[108].split(":");
        String baseFgtsDesc = baseFgts[0].trim().toString().concat(":");
        String baseFgtsVlr = "";
        if (baseFgts != null && baseFgts.length == 2) {
            baseFgtsVlr = baseFgts[1].trim();
        }

        String [] fgtsMes = campos[112].split(":");
        String fgtsMesDesc = fgtsMes[0].trim().toString().concat(":");
        String fgtsMesVlr = "";
        if (fgtsMes != null && fgtsMes.length == 2) {
            fgtsMesVlr = fgtsMes[1].trim();
        }

        String [] faixaIrrf = campos[113].split(":");
        String faixaIrrfDesc = faixaIrrf[0].trim().toString().concat(":");
        String faixaIrrfVlr = "";
        if (faixaIrrf != null && faixaIrrf.length == 2) {
            faixaIrrfVlr = faixaIrrf[1].trim();
        }

        StringBuilder detalhe = new StringBuilder();

        int qtdeLancamentos = 0;
        for (int i = primeiraColunaDetalhe; i < ultimaColunaDetalhe; i += qtdeColunasDetalhe) {
            if ((i + qtdeColunasDetalhe) > campos.length) {
                break;
            }

            String codEvento = campos[i + 1].trim();
            if (TextHelper.isNull(codEvento)) {
            	continue;
            }
            String descEvento = campos[i + 2];
            String referencia = campos[i + 3];
            String vlrVencEvento = campos[i + 4].trim();
            String vlrDescEvento = campos[i + 5].trim();

            detalhe.append(String.format("%6s", codEvento)).append(" ");
            detalhe.append(String.format("%-30s", (descEvento.toString().length() < 30 ? descEvento : descEvento.substring(0, 29))));
            detalhe.append(String.format("%16s", referencia)).append(" ");
            detalhe.append(String.format("%16s", vlrVencEvento)).append(" ");
            detalhe.append(String.format("%16s", vlrDescEvento)).append("\n");
            qtdeLancamentos++;
        }
        // garante quantidade mínima de linhas no detalhe do contracheque para manter um tamanho mínimo
        for (int i = qtdeLancamentos; i <= qtdeMinItensDetalhe; i++) {
            detalhe.append("\n");
        }

        String separador = String.format("%" + largura + "s", "\n").replace(" ", "-");

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(String.format("%-30s", razaoSocial));
        contracheque.append(String.format("%-27s", orgaoNome));
        contracheque.append(String.format("%-15s", unidade));
        contracheque.append(String.format("%-15s", referenciaContracheque)).append("\n");

        contracheque.append(String.format("%-30s", cnpj));
        contracheque.append(String.format("%-27s", cargo));
        contracheque.append(String.format("%-15s", depto));
        contracheque.append(String.format("%-15s", mesAno)).append("\n");

        contracheque.append(String.format("%-30s", (serMatriculaNome.toString().length() < 30 ? serMatriculaNome : serMatriculaNome.substring(0, 29))));
        contracheque.append(String.format("%-27s", categoria));
        contracheque.append(String.format("%-15s", cbo));
        contracheque.append(String.format("%-15s", dtAdmissao)).append("\n");

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
        contracheque.append(String.format("%53s", totalProventosDesc));
        contracheque.append(String.format("%17s", totalProventosVlr));
        contracheque.append(String.format("%17s", ""));
        contracheque.append("\n");
        // total descontos
        contracheque.append(String.format("%53s", totalDescontosDesc));
        contracheque.append(String.format("%17s", ""));
        contracheque.append(String.format("%17s", totalDescontosVlr));
        contracheque.append("\n");
        // total liquido
        contracheque.append(String.format("%53s", totalLiquidoDesc));
        contracheque.append(String.format("%17s", ""));
        contracheque.append(String.format("%17s", totalLiquidoVlr));
        contracheque.append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%-18s", salarioBaseDesc));
        contracheque.append(String.format("%10s", salarioBaseVlr)).append(" ");
        contracheque.append(String.format("%-18s", baseFgtsDesc));
        contracheque.append(String.format("%10s", baseFgtsVlr)).append(" ");
        contracheque.append(String.format("%-18s", baseIrrfDesc));
        contracheque.append(String.format("%10s", baseIrrfVlr)).append("\n");

        contracheque.append(String.format("%-18s", baseInssDesc));
        contracheque.append(String.format("%10s", baseInssVlr)).append(" ");
        contracheque.append(String.format("%-18s", fgtsMesDesc));
        contracheque.append(String.format("%10s", fgtsMesVlr)).append(" ");
        contracheque.append(String.format("%-18s", faixaIrrfDesc));
        contracheque.append(String.format("%10s", faixaIrrfVlr)).append("\n");

        contracheque.append(separador);

        return contracheque.toString();
    }
}
