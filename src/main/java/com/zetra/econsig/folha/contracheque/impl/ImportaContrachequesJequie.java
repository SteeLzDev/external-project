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
 * <p>Title: ImportaContrachequesJequie</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Prefeitura Municipal de Jequié (BA).</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesJequie extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesJequie.class);

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
                    String serCpf = TextHelper.format(campos[11].trim(), "###.###.###-##");
                    String rseMatricula = Integer.valueOf(campos[4].trim()).toString();
                    String orgIdentificador = campos[0].equals("PREFEITURA MUNICIPAL DE JEQUIE") ? "11" : "0";

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = serCpf;
                    query.orgIdentificador = orgIdentificador;
                    query.ativo = getAtivo();

                    List<String> servidores = query.executarLista();
                    if (servidores == null || servidores.size() == 0) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.cpf.secretaria", responsavel, rseMatricula, serCpf, orgIdentificador));
                        continue;
                    } else if (servidores.size() > 1) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.cpf.secretaria", responsavel, rseMatricula, serCpf, orgIdentificador));
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

    private String formataContracheque(String [] campos) throws ParseException {
        //String cnpj = TextHelper.format(campos[0].trim(), "###.###.###/####-##");
        String razaoSocial = campos[0].trim();
        //String orgao = campos[2].trim();
        String cargo = TextHelper.isNull(campos[10]) ? "" : campos[10].split(":")[1].trim();
        String matricula = campos[4].trim();
        String serNome = campos[5].trim();
        String serCpf = TextHelper.format(campos[11].trim(), "###.###.###-##");
        String mes = campos[6].trim().split("/")[0];
        String ano = campos[6].trim().split("/")[1];

        String totalProventos = campos[98].trim();
        String totalDescontos = campos[99].trim();
        String totalLiquido = campos[100].trim();
        String baseInss = campos[101].trim();
        String salarioBase = campos[102].trim();
        String baseIrrf = campos[103].trim();

        String banco = campos[110].trim();
        String agencia = campos[111].trim();
        String conta = campos[112].trim();

        //String baseFgts = campos[15].trim();
        //String fgtsMes = campos[16].trim();
        //String faixaIrrf = campos[18].trim();

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(razaoSocial).append("\n");
        //contracheque.append("CGC.: ").append(cnpj).append("\n");
        contracheque.append("  - DEMONSTRATIVO DE PAGAMENTO -").append("\n");
        contracheque.append("REFERENCIA: ").append(mes).append("/").append(ano).append("\n");
        //contracheque.append(orgao).append("\n");
        contracheque.append("MATRIC.: ").append(matricula).append("     ").append("\n");
        contracheque.append("NOME...: ").append(serNome).append("\n");
        contracheque.append("C.P.F..: ").append(serCpf).append("\n");
        contracheque.append("CARGO..: ").append(cargo).append("\n");
        contracheque.append(String.format("BANCO..: %-25.25s  AGENCIA: %-5.5s  CONTA: %-12.12s\n", banco, agencia, conta));

        String separador = "-----------------------------------------------------------------------";
        contracheque.append(separador).append("\n");

        StringBuilder proventos = new StringBuilder();
        StringBuilder descontos = new StringBuilder();
        String formato = "%-39.39s %3.3s %13.13s %13.13s \n";

        for (int i = 13; i < campos.length; i += 5) {
            if ( i > 95) {
                break;
            }

            String codEvento = campos[i];
            String nomeEvento = campos[i + 1];
            String referencia = campos[i + 2];
            String vlrProvento = campos[i + 3];
            String vlrDesconto = campos[i + 4];

            if (!vlrProvento.equals("")) {
                proventos.append(String.format(formato, nomeEvento, codEvento, referencia, vlrProvento));
            } else if (!vlrDesconto.equals("")) {
                descontos.append(String.format(formato, nomeEvento, codEvento, referencia, vlrDesconto));
            }
        }

        contracheque.append(String.format(formato, "PROVENTOS","COD","REF","VALOR"));
        contracheque.append(String.format(formato, "---------","---","-------------","-------------"));
        contracheque.append(proventos.toString());
        contracheque.append(separador).append("\n");
        contracheque.append(String.format(formato, "DESCONTOS","COD","REF","VALOR"));
        contracheque.append(String.format(formato, "---------","---","-------------","-------------"));
        contracheque.append(descontos.toString());
        contracheque.append(separador).append("\n");

        formato = "%-17.17s %13.13s \n";
        contracheque.append(String.format(formato, "SALARIO BASE...:", salarioBase));
        contracheque.append(String.format(formato, "BASE INSS......:", baseInss));
        //contracheque.append(String.format(formato, "BASE DE FGTS..:", baseFgts));
        contracheque.append(String.format(formato, "BASE DE IRRF...:", baseIrrf));

        //contracheque.append(String.format(formato, "FGTS DO MÊS...:", fgtsMes));
        //contracheque.append(String.format(formato, "FAIXA DE IRRF.:", faixaIrrf));
        contracheque.append(String.format(formato, "TOTAL PROVENTOS:", totalProventos));
        contracheque.append(String.format(formato, "TOTAL DESCONTOS:", totalDescontos));
        contracheque.append(String.format(formato, "TOTAL LIQUIDO..:", totalLiquido));

        contracheque.append(separador).append("\n");;
        return contracheque.toString();
    }
}