package com.zetra.econsig.folha.contracheque.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.folha.contracheque.AbstractImportaContraCheques;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

/**
 * <p>Title: ImportaContrachequesGrupoMascarello</p>
 * <p>Description: Implementação da rotina de importação de arquivo de contracheques para
 * o sistema do Grupo Mascarello.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesGrupoMascarello extends AbstractImportaContraCheques {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesGrupoMascarello.class);

    /**
     * Realiza a importação do arquivo de contracheques
     * @param nomeArquivo    : nome do arquivo contendo os contracheques
     * @param periodo        : período dos contracheques
     * @param responsavel    : responsável pela importação
     * @param tipoEntidade   : CSE/EST/ORG
     * @param codigoEntidade : código de acordo com tipoEntidade
     * @throws ImportaContrachequesException
     */
    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {

        BufferedReader entrada = null;
        String linha = null;
        StringBuilder titulo = null;
        StringBuilder conteudo = null;
        String rseCodigo = null;

        String separador = String.format("%40s", "\n").replace(" ", "-");

        try {
            entrada = new BufferedReader(new FileReader(nomeArquivo));
            String nomeEmpresa = "";
            while ((linha = entrada.readLine()) != null) {
                try {
                    if (!TextHelper.isNull(linha)) {
                        if (linha.charAt(0) == '0') {
                            // 01 Identificação do tipo de registro     001 001 01 - Num 0 F002
                            // 02 (Reservado)                           002 009 08 - Alfa Brancos F021
                            // 03 Identificação do tipo de serviço      010 011 02 - Num
                            // 04 Descrição do tipo de serviço          012 026 15 - Alfa Crédito s c/c F006
                            // 05 Nome ou Razão Social da Empresa       027 046 20 - Alfa F013
                            // 06 Ident do Arquivo de Contracheque      047 058 12 - Alfa CONTRACHEQUE F023
                            // 07 Nome reduzido da empresa              059 066 08 -
                            // 08 (Reservado)                           067 076 10
                            // 09 Código do banco na Compensação        077 079 03 - Num F001
                            // 10 Nome do Banco                         080 094 15 - Alfa F014
                            // 11 Data de geração do arquivo            095 100 06 - Num F017
                            // 12 Densidade de Gravação do Arquivo      101 105 05 - Num 1600 F020
                            // 13 Unidade de densidade de gravação      106 108 03 - Alfa BPI F019
                            // 14 Data de Disponibilização              109 116 08 - Num
                            // 15 Codigo do Convênio                    117 120 04
                            // 16 (Reservado)                           121 189 69
                            // 17 Número da remessa                     190 194 05 - Num F025
                            // 18 Número sequencial do registro no LOTE 195 200 06 - Num F018

                            nomeEmpresa = linha.substring(26, 46).trim();

                        } else if (linha.charAt(0) == '3') {
                            // 01 Identificação do tipo de registro     001 001 01 - Num 3 F002
                            // 02 Texto do Demonstrativo de Pagamento   002 041 40 - Alfa H001
                            // 03 Identificação do tipo de contracheque 042 044 03 - Num 01 F028
                            // 04 (Reservado)                           045 049 05 - Alfa Brancos F021
                            // 05 Sequência interna do texto            050 054 05 - Num H002
                            // 06 (Reservado)                           055 062 08 - Alfa Brancos F021
                            // 07 Número da agência do creditado        063 066 04 - Num F008
                            // 08 (Reservado)                           067 073 07 - Num F021
                            // 09 DAC (dígito fornecido pelo banco)     074 074 01 - Num F011
                            // 10 Reservado Banco                       075 082 08 - Alfa Brancos F021
                            // 11 (Reservado)                           083 094 12 - Num F008
                            // 12 Nome da empresa                       095 114 20 - Alfa F013
                            // 13 Data de crédito                       115 120 06 - Num F015
                            // 14 Mês/Ano de referência                 121 124 04 - Num H004
                            // 15 Ident / Matrícula do Funcionário      125 139 15 - Alfa H003
                            // 16 (Reservado)                           140 145 6 - Alfa Brancos F021
                            // 17 Número da conta do creditado          146 157 12 - Num F010
                            // 18 (Reservado)                           158 169 12 - Alfa Brancos F021
                            // 19 Código do Convênio                    170 189 20 - Alfa F029
                            // 20 (Reservado)                           190 194 5 - Alfa Brancos F021
                            // 21 Número Sequencial do registro no LOTE 195 200 06 - Num F018


                            // se sequencia interna for igual a 1 indica início de um contracheque
                            int sequenciaInterna = Integer.parseInt(linha.substring(49, 54).trim());

                            // cria o título dos contracheques apenas uma vez
                            if (titulo == null) {
                                String dataString = linha.substring(122, 124).trim() + "-" + linha.substring(120, 122).trim() + "-01";
                                DateFormat sdfp = new SimpleDateFormat("yy-MM-dd");
                                Date d = sdfp.parse(dataString);
                                DateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
                                String date = sdff.format(d);
                                Date periodoDate = DateHelper.parse(date, "yyyy-MM-dd");
                                String periodoString = DateHelper.getMonthName(periodoDate) + "/" + DateHelper.getYear(periodoDate);

                                titulo = new StringBuilder();
                                titulo.append(separador);
                                titulo.append(String.format("%-40s", nomeEmpresa)).append("\n");
                                titulo.append(String.format("%-40s", "Recibo de Pagamento de " + periodoString)).append("\n");
                                titulo.append(separador);
                            }

                            // inicia contracheque
                            if (sequenciaInterna == 1) {
                                // importa o contracheque anterior, caso exista
                                if (conteudo != null && rseCodigo != null) {
                                    conteudo.append(separador);
                                    criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                                }
                                rseCodigo = null;
                                conteudo = new StringBuilder();
                                conteudo.append(titulo);

                                // recupera o registro servidor
                                String estIdentificador = null;
                                String orgIdentificador = null;
                                if (!TextHelper.isNull(tipoEntidade)) {
                                    if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                                        Estabelecimento est = EstabelecimentoHome.findByPrimaryKey(codigoEntidade);
                                        estIdentificador = est.getEstIdentificador();
                                    } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                                        Orgao org = OrgaoHome.findByPrimaryKey(codigoEntidade);
                                        orgIdentificador = org.getOrgIdentificador();
                                    }
                                }

                                String rseMatricula = String.valueOf(Long.parseLong(linha.substring(124, 139).trim()));
                                ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
                                query.estIdentificador = estIdentificador;
                                query.orgIdentificador = orgIdentificador;
                                query.rseMatricula = rseMatricula;

                                List<String> servidores = query.executarLista();
                                if (servidores == null || servidores.size() == 0) {
                                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.unico.est.org", responsavel, rseMatricula, "", orgIdentificador));
                                    rseCodigo = null;
                                    continue;
                                } else if (servidores.size() > 1) {
                                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.servidor.multiplo.est.org", responsavel, rseMatricula, "", orgIdentificador));
                                    continue;
                                } else {
                                    rseCodigo = servidores.get(0);
                                }
                            }

                            String texto = linha.substring(1, 41);
                            conteudo.append(String.format("%-40s", texto)).append("\n");

                        } else if (linha.charAt(0) == '9') {
                            // 01 Identificação do tipo de registro 001 001 01 - Num 9 F002
                            // 02 (Reservado)                       002 194 193 - Alfa Brancos F021
                            // 03 Número seq. do registro no LOTE   195 200 06 - Num F018
                            if (conteudo != null && rseCodigo != null) {
                                conteudo.append(separador);
                                criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
                            }
                        }
                    }
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage());
                }
            }
        } catch (FindException ex) {
            throw new ImportaContrachequesException("mensagem.erroInternoSistema", responsavel, ex);
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
