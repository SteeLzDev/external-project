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
 * <p>Title: ImportaContrachequesSaoMiguelCampos</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema da Pref. de São Miguel dos Campos.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesSaoMiguelCampos extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesSaoMiguelCampos.class);

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
                    // Início de um contracheque: Pref. São Miguel dos Campos (separador ";")
                    // rseMatricula: campo de índice 4
                    // serCpf: campo de índice 11
                    // orgIdentificador: campo de índice 114
                    String serCpf = TextHelper.format(campos[11].trim(), "###.###.###-##");
                    String rseMatricula = Integer.valueOf(campos[4].trim()).toString();
                    String orgIdentificador = campos[116].trim().toString();
                    orgIdentificador = orgIdentificador.replace(".", "").replace("-", "").replace("/", "");

                    ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                    query.rseMatricula = rseMatricula;
                    query.serCPF = serCpf;
                    query.orgIdentificador = orgIdentificador;
                    query.orgIdentificadorSemMascara = true;

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

    private String formataContracheque(String [] campos) throws ParseException {
    	int ultimaColunaDetalhe = 97;
    	int largura = 88;

    	String razaoSocial = campos[0].trim();
    	String categoria = campos[7].trim();
    	String orgaoNome = campos[117].trim();
    	String cnpj = TextHelper.format(campos[119].trim(), "##.###.###/####-##");
    	String cargo = campos[10].trim();
    	String nivel = campos[120].trim();
        String classe = campos[121].trim();
        String matricula = campos[4].trim();
        String serNome = campos[5].trim();
        String serCpf = TextHelper.format(campos[11].trim(), "###.###.###-##");
        String mesAno = campos[6].trim();
        String dtAdmissao = campos[3].trim();
        String conta = campos[112].trim();
        String margem = campos[106].trim();

        String totalProventos = campos[98].trim();
        String totalDescontos = campos[99].trim();
        String totalLiquido = campos[100].trim();
        String salarioBase = campos[102].trim();
        String baseInss = campos[101].trim();
        String baseIrrf = campos[103].trim();
        String baseFgts = campos[115].trim();
        String fgtsMes = campos[118].trim();

        String mensagem1 = campos[107].trim();
        String mensagem2 = campos[108].trim();
        String mensagem3 = campos[109].trim();

        StringBuilder detalhe = new StringBuilder();

        int qtdeLancamentos = 0;
        for (int i = 13; i < ultimaColunaDetalhe; i += 5) {
            if ((i + 5) > campos.length) {
                break;
            }
            String codEvento = campos[i].trim();
            if (TextHelper.isNull(codEvento)) {
            	continue;
            }
            String descEvento = campos[i + 1];
            String referencia = campos[i + 2];
            String vlrVencEvento = campos[i + 3].trim();
            String vlrDescEvento = campos[i + 4].trim();

            detalhe.append(String.format("%5s", codEvento)).append(" ");
            detalhe.append(String.format("%-31s", (descEvento.toString().length() < 31 ? descEvento : descEvento.substring(0, 30))));
            detalhe.append(String.format("%10s", referencia));
            detalhe.append(String.format("%20s", vlrVencEvento));
            detalhe.append(String.format("%20s", vlrDescEvento)).append("\n");
            qtdeLancamentos++;
        }
        // garante pelo menos sete linhas no detalhe do contracheque para manter um tamanho mínimo
        for (int i = qtdeLancamentos; i <= 7; i++) {
            detalhe.append("\n");
        }

        String separador = String.format("%" + largura + "s", "\n").replace(" ", "-");

        StringBuilder contracheque = new StringBuilder();
        contracheque.append(razaoSocial);
        contracheque.append(String.format("%40s", "RECIBO DE PAGAMENTO DE SALÁRIO")).append("\n");

        contracheque.append(String.format("%-62s", orgaoNome));
        contracheque.append(String.format("%25s", "CNPJ: " + cnpj)).append("\n");

        contracheque.append(String.format("%-40s", (cargo.toString().length() < 40 ? cargo : cargo.substring(0, 39))));
        contracheque.append(String.format("%-23s", (categoria.toString().length() < 23 ? categoria : categoria.substring(0, 22))));
        contracheque.append(String.format("%24s", mesAno)).append("\n");

        contracheque.append(String.format("%-87s", "NÍVEL - CLASSE: " + nivel + " - " + classe)).append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%-10s", "CÓDIGO"));
        contracheque.append(String.format("%-49s", "NOME DO FUNCIONÁRIO"));
        contracheque.append(String.format("%-17s", "CPF"));
        contracheque.append(String.format("%11s", "ADMISSÃO")).append("\n");

        contracheque.append(String.format("%-10s", matricula));
        contracheque.append(String.format("%-49s", (serNome.toString().length() < 49 ? serNome : serNome.substring(0, 48))));
        contracheque.append(String.format("%-17s", serCpf));
        contracheque.append(String.format("%11s", dtAdmissao)).append("\n");

        contracheque.append(separador);

        contracheque.append(String.format("%5s", "CÓD")).append(" ");
        contracheque.append(String.format("%-31s", "DESCRIÇÃO"));
        contracheque.append(String.format("%-10s", "REFERÊNCIA"));
        contracheque.append(String.format("%20s", "VENCIMENTOS"));
        contracheque.append(String.format("%20s", "DESCONTOS")).append("\n");

        contracheque.append(detalhe.toString());

        contracheque.append(separador);

        contracheque.append(String.format("%67s", "TOTAL VENCIMENTOS"));
        contracheque.append(String.format("%20s", "TOTAL DESCONTOS")).append("\n");
        contracheque.append(String.format("%67s", totalProventos));
        contracheque.append(String.format("%20s", totalDescontos)).append("\n");

        contracheque.append(separador);
        contracheque.append(String.format("%-37s", "Líquido creditado na conta " + conta));
        contracheque.append(String.format("%30s", "VALOR LÍQUIDO ->"));
        contracheque.append(String.format("%20s", totalLiquido)).append("\n");
        contracheque.append(String.format("%-87s", "Margem de consignação: " + margem)).append("\n");

        if (!TextHelper.isNull(mensagem1)) {
            contracheque.append(String.format("%-87s", (mensagem1.toString().length() < 87 ? mensagem1 : mensagem1.substring(0, 86)))).append("\n");
        }
        if (!TextHelper.isNull(mensagem2)) {
            contracheque.append(String.format("%-87s", (mensagem2.toString().length() < 87 ? mensagem2 : mensagem2.substring(0, 86)))).append("\n");
        }
        if (!TextHelper.isNull(mensagem3)) {
            contracheque.append(String.format("%-87s", (mensagem3.toString().length() < 87 ? mensagem3 : mensagem3.substring(0, 86)))).append("\n");
        }

        contracheque.append(separador);

        contracheque.append(String.format("%14s", "SALARIO BASE"));
        contracheque.append(String.format("%20s", "SAL. CONTR. INSS"));
        contracheque.append(String.format("%19s", "BASE CÁLC. FGTS"));
        contracheque.append(String.format("%15s", "FGTS DO MÊS"));
        contracheque.append(String.format("%19s", "BASE CÁLC. IRRF")).append("\n");

        contracheque.append(String.format("%14s", salarioBase));
        contracheque.append(String.format("%20s", baseInss));
        contracheque.append(String.format("%19s", baseFgts));
        contracheque.append(String.format("%15s", fgtsMes));
        contracheque.append(String.format("%19s", baseIrrf)).append("\n");

        return contracheque.toString();
    }
}
