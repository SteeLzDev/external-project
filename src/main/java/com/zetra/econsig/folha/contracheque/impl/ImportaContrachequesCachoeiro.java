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
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesCachoeiro</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Pref. de Cachoeiro do Itapemerim.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesCachoeiro extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesCachoeiro.class);

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
                    String serCpf = TextHelper.format(campos[8].trim(), "###.###.###-##");
                    String rseMatricula = Integer.valueOf(campos[6].trim()).toString();
                    String orgIdentificador = campos[3].trim().substring(0,3);

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = serCpf;
                    query.orgIdentificador = orgIdentificador;
                    query.ativo = true;

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
        String cnpj = TextHelper.format(campos[0].trim(), "###.###.###/####-##");
        String razaoSocial = campos[1].trim();
        String orgao = campos[2].trim();
        String cargo = campos[4].trim();
        String matricula = campos[6].trim();
        String serNome = campos[7].trim();
        String serCpf = TextHelper.format(campos[8].trim(), "###.###.###-##");
        String mes = campos[9].trim();
        String ano = campos[10].trim();

        String totalProventos = formataValores(campos[11].trim());
        String totalDescontos = formataValores(campos[12].trim());
        String totalLiquido = formataValores(campos[13].trim());
        String salarioBase = formataValores(campos[14].trim());
        String baseFgts = formataValores(campos[15].trim());
        String fgtsMes = formataValores(campos[16].trim());
        String baseIrrf = formataValores(campos[17].trim());
        int faixaIrrf = Integer.parseInt(campos[18].trim());

        StringBuilder proventos = new StringBuilder();
        StringBuilder descontos = new StringBuilder();

        for (int i = 19; i < campos.length; i += 5) {
            if ((i + 5) > campos.length) {
                break;
            }

            String codEvento = campos[i];
            String tipoEvento = campos[i + 1];
            String referencia = campos[i + 2];
            String descEvento = campos[i + 3];
            String vlrEvento = formataValores(campos[i + 4].trim());

            int vlrLength = vlrEvento.toString().length();
            int espacoVlr = 10 - vlrLength;
            if (tipoEvento.equals("P")) {
                proventos.append(descEvento).append(" ").append(codEvento).append(" ").append(referencia).append(" ");
                for (int j = 0; j < espacoVlr; j++) {
                    proventos.append(" ");
                }
                proventos.append(vlrEvento).append("\n");
            } else if (tipoEvento.equals("D")) {
                descontos.append(descEvento).append(" ").append(codEvento).append(" ").append(referencia).append(" ");
                for (int j = 0; j < espacoVlr; j++) {
                    descontos.append(" ");
                }
                descontos.append(vlrEvento).append("\n");
            }
        }

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(razaoSocial).append("\n");
        contracheque.append("CGC.: ").append(cnpj).append("\n");
        contracheque.append("  - DEMONSTRATIVO DE PAGAMENTO -").append("\n");
        contracheque.append("REFERENCIA: ").append(mes).append("/").append(ano).append("\n");
        contracheque.append(orgao).append("\n");
        contracheque.append("MATRIC.: ").append(matricula).append("     ").append("\n");
        contracheque.append("NOME...: ").append(serNome).append("\n");
        contracheque.append("C.P.F..: ").append(serCpf).append("\n");
        contracheque.append("CARGO..: ").append(cargo).append("\n");

        String separador = "---------------------------------------";

        contracheque.append(separador).append("\n");;
        contracheque.append("PROVENTOS").append("            ").append("COD ").append("REF      ").append("VALOR").append("\n");
        contracheque.append("---------").append("            ").append("--- ").append("---      ").append("-----").append("\n");
        contracheque.append(proventos.toString());
        contracheque.append(separador).append("\n");;
        contracheque.append("DESCONTOS").append("            ").append("COD ").append("REF      ").append("VALOR").append("\n");
        contracheque.append("---------").append("            ").append("--- ").append("---      ").append("-----").append("\n");
        contracheque.append(descontos.toString());
        contracheque.append(separador).append("\n");;
        contracheque.append("SALARIO BASE.:");
        int espaco = 25 - salarioBase.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(salarioBase).append("\n");
        contracheque.append("FGTS DO MÊS..:");
        espaco = 25 - fgtsMes.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(fgtsMes).append("\n");
        contracheque.append("BASE DE FGTS.:");
        espaco = 25 - baseFgts.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(baseFgts).append("\n");
        contracheque.append("FAIXA DE IRRF:");
        espaco = 25 - Integer.valueOf(faixaIrrf).toString().length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(faixaIrrf).append("\n");
        contracheque.append("BASE DE IRRF.:");
        espaco = 25 - baseIrrf.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(baseIrrf).append("\n");
        contracheque.append(separador).append("\n");;
        contracheque.append("TOTAL PROVENTOS:");
        espaco = 23 - totalProventos.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(totalProventos).append("\n");
        contracheque.append("TOTAL DESCONTOS:");
        espaco = 23 - totalDescontos.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(totalDescontos).append("\n");
        contracheque.append("TOTAL LIQUIDO..:");
        espaco = 23 - totalLiquido.length();
        for (int j = 0; j < espaco; j++) {
            contracheque.append(" ");
        }
        contracheque.append(totalLiquido).append("\n");
        contracheque.append(separador).append("\n");;

        return contracheque.toString();
    }

    private String formataValores (String valor) throws ParseException {
        StringBuilder formatado = new StringBuilder();
        formatado.append(valor.substring(0, valor.length() - 2)).append(",").append(valor.substring(valor.length() - 2));
        Double vlrDouble = Double.valueOf(NumberHelper.parse(formatado.toString(), NumberHelper.getLang()));
        return NumberHelper.reformat(vlrDouble.toString(), "en", NumberHelper.getLang());
    }

}
