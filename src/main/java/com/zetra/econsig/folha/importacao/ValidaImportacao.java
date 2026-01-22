package com.zetra.econsig.folha.importacao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ValidaImportacao</p>
 * <p>Description: Realiza a validação da importação de margem/transferidos/retorno/critica
 *  pela presença dos arquivos, total de linhas, tamanho das linhas, etc.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidaImportacao implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaImportacao.class);
    private static final String NOME_CLASSE = ValidaImportacao.class.getName();

    /**
     * Executa as validações necessárias pré-importação de margem, retorno,
     * critica, transferidos, etc.
     * @param tipoEntidade    : CSE/ORG
     * @param codigoEntidade  : Código da entidade, 1 para CSE
     * @param impMargem       : True caso tenha importação de margem
     * @param impTransferidos : True caso tenha importação de transferidos
     * @param impRetorno      : True caso tenha importação de retorno
     * @param impCritica      : True caso tenha importação de crítica
     * @param nomeArqConfValidacao : Nome do arquivo de configuração para validação
     * @param testeExecucao   : Executa apenas para teste, ou seja, não gera o arquivo final
     * @param responsavel     : Usuário responsável
     * @throws ZetraException
     */
    private void validar(String tipoEntidade, String codigoEntidade,
            boolean impMargem, boolean impTransferidos, boolean impRetorno, boolean impCritica,
            String nomeArqConfValidacao, boolean testeExecucao, AcessoSistema responsavel) throws ZetraException {

        if (!tipoEntidade.equalsIgnoreCase("CSE") && !tipoEntidade.equalsIgnoreCase("ORG") && !tipoEntidade.equalsIgnoreCase("EST")) {
            throw new ZetraException("mensagem.erro.tipo.entidade.invalido", responsavel);
        }
        if (tipoEntidade.equalsIgnoreCase("ORG") && TextHelper.isNull(codigoEntidade)) {
            throw new ZetraException("mensagem.erro.informe.codigo.entidade.org", responsavel);
        }
        if (tipoEntidade.equalsIgnoreCase("EST") && TextHelper.isNull(codigoEntidade)) {
            throw new ZetraException("mensagem.erro.informe.codigo.entidade.est", responsavel);
        }
        if (!impMargem && !impTransferidos && !impRetorno && !impCritica) {
            throw new ZetraException("mensagem.erro.opcao.invalida", responsavel);
        }
        if (TextHelper.isNull(nomeArqConfValidacao) || !new File(nomeArqConfValidacao).exists()) {
            throw new ZetraException("mensagem.erro.arquivo.configuracao.validacao.ausente", responsavel);
        }

        // Carrega as configurações de validação
        final Properties confValidacao = new Properties();
        try {
            confValidacao.load(new FileInputStream(nomeArqConfValidacao));
        } catch (final FileNotFoundException ex) {
            throw new ZetraException("mensagem.erro.arquivo.configuracao.validacao.ausente", responsavel, ex);
        } catch (final IOException ex) {
            throw new ZetraException("mensagem.erro.arquivo.configuracao.validacao.invalido", responsavel, ex);
        }

        LOG.info("INICIO");
        boolean temErros = false;

        final String dirBaseSistema = ParamSist.getDiretorioRaizArquivos();
        final List<String> tiposArquivo = new ArrayList<>();
        if (impMargem) {
            tiposArquivo.add("margem");
        }
        if (impTransferidos) {
            tiposArquivo.add("transferidos");
        }
        if (impRetorno) {
            tiposArquivo.add("retorno");
        }
        if (impCritica) {
            tiposArquivo.add("critica");
        }

        ValidaImportacaoSistema validacaoCustomizada = null;
        final String nomeClasseValidacaoCustomizada = confValidacao.getProperty((tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST")? codigoEntidade + "." : "") + "nomeClasseValidacaoCustomizada");
        if (!TextHelper.isNull(nomeClasseValidacaoCustomizada)) {
            validacaoCustomizada = ValidaImportacaoSistemaBase.getValidaImportacaoSistema(nomeClasseValidacaoCustomizada);
        }

        Map<String, String> paramValidacaoArq = null;

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)  || tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP) || tipoEntidade.equals(AcessoSistema.ENTIDADE_EST) || tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            final ValidaImportacaoController validaImportacaoController = ApplicationContextProvider.getApplicationContext().getBean(ValidaImportacaoController.class);
            paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);
        }

        for (final String tipoArquivo : tiposArquivo) {
            String caminhoCompleto = null;
            if (!tipoEntidade.equalsIgnoreCase("EST")) {
                caminhoCompleto = dirBaseSistema
                        + File.separatorChar + tipoArquivo
                        + File.separatorChar + "cse"
                        + File.separatorChar + (tipoEntidade.equalsIgnoreCase("ORG") ? codigoEntidade : "");
            } else {
                caminhoCompleto = dirBaseSistema
                        + File.separatorChar + tipoArquivo
                        + File.separatorChar + "est"
                        + File.separatorChar + codigoEntidade;
            }

            /** ************************************************* **/
            /** Carrega as configurações por tipo de arquivo       */

            final String[] extensoes = trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "extensoes", "TXT,ZIP")).split(","); // Default: TXT,ZIP
            final int diasIdadeMaximaArquivo = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "diasIdadeMaximaArquivo", "3"))); // Default: 3
            final String padraoNomeArquivoBusca = trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "padraoNomeArquivoBusca", null)); // Ex.: ECONSIG_MARGENS_[0-9]{4}_[0-9]{2}_[A|B].TXT
            final String padraoNomeArquivoBusca2 = trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "padraoNomeArquivoBusca2", null)); // Ex.: ECONSIG_MARGENS_[0-9]{4}_[0-9]{2}_[A|B].TXT
            final String padraoNomeArquivoFinal = trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "padraoNomeArquivoFinal", null)); // Ex.: ECONSIG_MARGENS_2010_06.TXT

            final int qtdMinimaArquivos = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "qtdMinimaArquivos", "1"))); // Default: 1
            final int qtdMaximaArquivos = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "qtdMaximaArquivos", "1"))); // Default: 1

            final int qtdMinimaLinhas = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "qtdMinimaLinhas", "1"))); // Default: 1
            final double percentualVarPosQtdLinhas = Double.parseDouble(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "percentualVarPosQtdLinhas", "0.10"))); // Default: 10%;
            final double percentualVarNegQtdLinhas = Double.parseDouble(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "percentualVarNegQtdLinhas", "0.00"))); // Default:  0%;

            final int qtdMaximaLinhasTamanhoInvalido = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "qtdMaximaLinhasTamanhoInvalido", "0"))); // Default: 0
            final int tamanhoMinimoLinha = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "tamanhoMinimoLinha", "1"))); // Default: 1
            final int tamanhoMaximoLinha = Integer.parseInt(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "tamanhoMaximoLinha", "1"))); // Default: 1

            LOG.info("Configuração para arquivos de '" + tipoArquivo + "': ");
            LOG.info(TextHelper.formataMensagem("extensoes", " ", 35, true) + " = " + TextHelper.join(extensoes, ","));
            LOG.info(TextHelper.formataMensagem("diasIdadeMaximaArquivo", " ", 35, true) + " = " + diasIdadeMaximaArquivo);
            LOG.info(TextHelper.formataMensagem("padraoNomeArquivoBusca", " ", 35, true) + " = " + padraoNomeArquivoBusca);
            LOG.info(TextHelper.formataMensagem("padraoNomeArquivoBusca2", " ", 35, true) + " = " + (TextHelper.isNull(padraoNomeArquivoBusca2) ? "Não definido, usando: " + padraoNomeArquivoBusca : padraoNomeArquivoBusca2));
            LOG.info(TextHelper.formataMensagem("padraoNomeArquivoFinal", " ", 35, true) + " = " + padraoNomeArquivoFinal);
            LOG.info(TextHelper.formataMensagem("qtdMinimaArquivos", " ", 35, true) + " = " + qtdMinimaArquivos);
            LOG.info(TextHelper.formataMensagem("qtdMaximaArquivos", " ", 35, true) + " = " + qtdMaximaArquivos);
            LOG.info(TextHelper.formataMensagem("qtdMinimaLinhas", " ", 35, true) + " = " + qtdMinimaLinhas);
            LOG.info(TextHelper.formataMensagem("percentualVarPosQtdLinhas", " ", 35, true) + " = " + percentualVarPosQtdLinhas);
            LOG.info(TextHelper.formataMensagem("percentualVarNegQtdLinhas", " ", 35, true) + " = " + percentualVarNegQtdLinhas);
            LOG.info(TextHelper.formataMensagem("qtdMaximaLinhasTamanhoInvalido", " ", 35, true) + " = " + qtdMaximaLinhasTamanhoInvalido);
            LOG.info(TextHelper.formataMensagem("tamanhoMinimoLinha", " ", 35, true) + " = " + tamanhoMinimoLinha);
            LOG.info(TextHelper.formataMensagem("tamanhoMaximoLinha", " ", 35, true) + " = " + tamanhoMaximoLinha);

            if (tipoArquivo.equals("transferidos")) {
                final double percentualRseAtivosQtdLinhas = Double.parseDouble(trimNullSafe(recuperaValorParametro(tipoEntidade, codigoEntidade, tipoArquivo, paramValidacaoArq, confValidacao, "percentualRseAtivosQtdLinhas", "1.00"))); // Default: 100%;
                LOG.info(TextHelper.formataMensagem("percentualRseAtivosQtdLinhas", " ", 35, true) + " = " + NumberHelper.format(percentualRseAtivosQtdLinhas, "en", 2, 8));
            }
            /** ************************************************* **/

            try {
                // Executa rotina específica por sistema para busca de arquivos em áreas externas
                if (validacaoCustomizada != null) {
                    validacaoCustomizada.aplicarCustomizacoesBuscaArquivos(tipoArquivo, caminhoCompleto);
                }

                // Verifica total de arquivos requeridos
                List<File> arquivos = listarArquivos(caminhoCompleto, TextHelper.isNull(padraoNomeArquivoBusca2) ? padraoNomeArquivoBusca : padraoNomeArquivoBusca2, extensoes, diasIdadeMaximaArquivo);
                LOG.info("Arquivos de '" + tipoArquivo + "' encontrados {" + qtdMinimaArquivos + "," + qtdMaximaArquivos + "}: " + TextHelper.join(arquivos, ","));

                if (arquivos.size() < qtdMinimaArquivos ||
                        arquivos.size() > qtdMaximaArquivos) {
                    throw new ZetraException("mensagem.erro.quantidade.arquivos.fora.esperado", responsavel, tipoArquivo, String.valueOf(qtdMinimaArquivos), String.valueOf(qtdMaximaArquivos));
                }

                // Se não existe nenhum arquivo não é necessário executar o resto da validação
                if (arquivos.size() == 0) {
                	LOG.info("Arquivo para importação de '" + tipoArquivo + "': ");
                } else {
	                // Executa rotina específica por sistema para customizações nos tratamentos dos arquivos
	                // após a validação do total de arquivos
	                if (validacaoCustomizada != null) {
	                    arquivos = validacaoCustomizada.aplicarCustomizacoesPosTotalArquivos(arquivos, tipoArquivo, caminhoCompleto);
	                }

	                // Verifica total de linhas requeridas
	                final int totalLinhas = somarTotalLinhas(arquivos, tamanhoMinimoLinha, tamanhoMaximoLinha, qtdMaximaLinhasTamanhoInvalido);
	                final long qtdMinimaLinhasPermitidas = Math.round(qtdMinimaLinhas * (1 - percentualVarNegQtdLinhas));
	                final long qtdMaximaLinhasPermitidas = Math.round(qtdMinimaLinhas * (1 + percentualVarPosQtdLinhas));
	                LOG.info("Total de linhas {" + qtdMinimaLinhasPermitidas + "," + qtdMaximaLinhasPermitidas + "}: " + totalLinhas);

	                if (totalLinhas < qtdMinimaLinhasPermitidas || totalLinhas > qtdMaximaLinhasPermitidas) {
	                    throw new ZetraException("mensagem.erro.quantidade.linhas.arquivo.fora.esperado", responsavel, tipoArquivo, String.valueOf(qtdMinimaLinhasPermitidas), String.valueOf(qtdMaximaLinhasPermitidas));
	                }

	                // Executa rotina específica por sistema para customizações nos tratamentos dos arquivos
	                // após a validação do total de linhas
	                if (validacaoCustomizada != null) {
	                    arquivos = validacaoCustomizada.aplicarCustomizacoesPosTotalLinhas(arquivos, tipoArquivo, caminhoCompleto, totalLinhas);
	                }

	                // Agrupa conjunto de arquivos de mesmo tipo, aplicando padrão de nomes para armazenamento
	                final String nomeArquivoFinal = substituirPadroesNomeArquivoFinal(padraoNomeArquivoFinal, tipoEntidade, codigoEntidade, responsavel);
	                File arquivoFinal = agruparArquivos(arquivos, tipoArquivo, caminhoCompleto, nomeArquivoFinal, testeExecucao);

	                // Executa rotina para validação do leiaute dos arquivos baseado nos XMLs do sistema
	                validarLeiauteArquivo(arquivoFinal, tipoArquivo, tipoEntidade, codigoEntidade);

	                // Aplica a customização final ao arquivo gerado para importação
	                if (validacaoCustomizada != null) {
	                    arquivoFinal = validacaoCustomizada.aplicarCustomizacoesPosValidacaoLeiaute(arquivoFinal, tipoArquivo, caminhoCompleto);
	                }

	                if (tipoArquivo.equals("transferidos")) {
	                    final ServidorDelegate serDelegate = new ServidorDelegate();
	                    if (serDelegate.qtdLinhasArqTransferidosAcimaPermitido(arquivoFinal.getAbsolutePath(), tipoEntidade, codigoEntidade, responsavel)) {
	                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.cadMargem.qtd.linhas.arq.transferidos.acima.permitido.arg0", responsavel, arquivoFinal.getAbsolutePath()));
	                    }
	                }

	                LOG.info("Arquivo para importação de '" + tipoArquivo + "': " + arquivoFinal.getAbsolutePath());
                }

            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                temErros = true;
            }
        }

        LOG.info("FIM");

        if (temErros) {
            throw new ZetraException("mensagem.erro.foram.encontrados.erros.validacao", responsavel);
        }
    }

    private String recuperaValorParametro(String tipoEntidade, String codigoEntidade, String tipoArquivo, Map<String, String> paramValidacaoArq, Properties confValidacao, String chave, String defaultValue) {
        final String baseNomeVar = (tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST") ? codigoEntidade + "." : "") + tipoArquivo + ".";

        if (paramValidacaoArq != null && !paramValidacaoArq.isEmpty() &&
            paramValidacaoArq.containsKey(tipoArquivo + "." + chave) && !TextHelper.isNull(paramValidacaoArq.get(tipoArquivo + "." + chave))) {
            return paramValidacaoArq.get(tipoArquivo + "." + chave);
        } else {
            if (!TextHelper.isNull(defaultValue)) {
                return confValidacao.getProperty(baseNomeVar + chave, defaultValue);
            } else {
                return confValidacao.getProperty(baseNomeVar + chave);
            }
        }
    }


    /**
     * Lista os arquivos em determinado diretório de acordo com as
     * extensões esperadas definidas pelo array.
     * @param caminhoCompleto   : Caminho completo onde devem estar os arquivos, na raiz deste.
     * @param padraoNomeArquivo : Padrão de nome de arquivo a ser localizado, ou ...
     * @param extensoes         : Extensões esperadas para localização (só utilizada se não tiver padrão de nome)
     * @param diasIdadeMaximaArquivo : Total máximo de dias que o arquivo localizado pode ter de idade
     * @return : Uma lista de arquivos
     * @throws ZetraException
     */
    private List<File> listarArquivos(String caminhoCompleto, final String padraoNomeArquivo, final String[] extensoes, final int diasIdadeMaximaArquivo) throws ZetraException {
        final FileFilter filtroExtArq = arquivo -> {
            // Verifica a idade máxima do arquivo
            if (DateHelper.dayDiff(new Date(arquivo.lastModified())) <= diasIdadeMaximaArquivo) {
                // Se tem a idade máxima correta, verifica o padrão de nome ou as extensões requeridas
                String padrao = "";
                if (!TextHelper.isNull(padraoNomeArquivo)) {
                    padrao = padraoNomeArquivo;
                } else {
                    // Constrói padrão baseado nas extensões permitidas
                    padrao = ".*\\.(" + TextHelper.join(extensoes, "|") + ")";
                }
                // Faz o casamento de padrão ignorando maiúsculas/minúsculas
                final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
                final Matcher m = p.matcher(arquivo.getName());
                return m.matches();
            }
            return false;
        };

        final File dir = new File(caminhoCompleto);
        if (!dir.exists() || !dir.canRead()) {
            throw new ZetraException("mensagem.erro.diretorio.nao.existe", AcessoSistema.getAcessoUsuarioSistema(), caminhoCompleto);
        }

        // Lista os arquivos de acordo com o filtro
        return Arrays.asList(dir.listFiles(filtroExtArq));
    }

    /**
     * Retorna o total de linhas de um conjunto de arquivos, onde a linha
     * deve ter a quantidade de caracteres definida pelo tamanho mínimo e máximo.
     * @param arquivos           : Conjunto de arquivos em formato texto
     * @param tamanhoMinimoLinha : Quantidade de caracteres mínimo para a linha ser contabilizada
     * @param tamanhoMaximoLinha : Quantidade de caracteres máximo para a linha ser contabilizada
     * @param qtdMaximaLinhasTamanhoInvalido : Quantidade máxima de linhas de tamanho inválido por arquivo
     * @return : Total de linhas do conjunto de arquivos
     * @throws ZetraException
     */
    private int somarTotalLinhas(List<File> arquivos, int tamanhoMinimoLinha, int tamanhoMaximoLinha, int qtdMaximaLinhasTamanhoInvalido) throws ZetraException {
        int totalLinhas = 0;

        for (final File arquivo : arquivos) {
            if (arquivo.getName().toLowerCase().endsWith(".zip")) {
                // Cria diretório para extração do conteúdo do arquivo zip
                final String caminhoZip = arquivo.getAbsolutePath() + ".tmp";
                final File caminho = new File(caminhoZip);
                if (!caminho.mkdir()) {
                    throw new ZetraException("mensagem.erro.criar.diretorio.extracao.arquivo", AcessoSistema.getAcessoUsuarioSistema(), arquivo.getAbsolutePath());
                }
                try {
                    // Extrai o conteúdo do arquivo Zip
                    final List<String> nomeArquivosConteudo = FileHelper.unZipAll(arquivo.getAbsolutePath(), caminhoZip);

                    // Cria lista de arquivos para contabilização de linhas
                    final List<File> arquivosConteudoZip = new ArrayList<>(nomeArquivosConteudo.size());
                    for (final String nome : nomeArquivosConteudo) {
                        arquivosConteudoZip.add(new File(nome));
                    }

                    // Soma o total de linhas dos arquivos do conteúdo do zip
                    totalLinhas += somarTotalLinhas(arquivosConteudoZip, tamanhoMinimoLinha, tamanhoMaximoLinha, qtdMaximaLinhasTamanhoInvalido);
                } catch (final IOException ex) {
                    throw new ZetraException("mensagem.erro.extracao.arquivo.contagem.linhas", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivo.getAbsolutePath());
                } finally {
                    try {
                        // Apaga o conteúdo da pasta temporária
                        FileHelper.deleteDir(caminhoZip);
                    } catch (final IOException ex) {
                        throw new ZetraException("mensagem.erro.apagar.diretorio.temporario.extracao", AcessoSistema.getAcessoUsuarioSistema(), ex, caminhoZip);
                    }
                }
            } else {
                // Se é um arquivo texto, então lê o arquivo contabilizando o total de
                // linhas que tem o tamanho esperado.
                int totalLinhasTamanhoInvalido = 0;
                BufferedReader entrada = null;
                try {
                    entrada = new BufferedReader(new FileReader(arquivo));
                    String linha = null;
                    while ((linha = entrada.readLine()) != null) {
                        linha = linha.replaceAll("[^\\p{Print}\\p{InLatin-1 Supplement}]", "");
                        if (linha.length() >= tamanhoMinimoLinha &&
                                linha.length() <= tamanhoMaximoLinha) {
                            totalLinhas++;
                        } else if (linha.length() > 0) {
                            LOG.info("Linha fora do tamanho esperado: " + linha);
                            totalLinhasTamanhoInvalido++;
                        }
                    }
                } catch (final IOException ex) {
                    throw new ZetraException("mensagem.erro.leitura.arquivo.validacao", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivo.getAbsolutePath());
                } finally {
                    if (entrada != null) {
                        try {
                            entrada.close();
                        } catch (final IOException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }

                if (totalLinhasTamanhoInvalido > qtdMaximaLinhasTamanhoInvalido) {
                    throw new ZetraException("mensagem.erro.quantidade.linhas.tamanho.invalido.fora.esperado", AcessoSistema.getAcessoUsuarioSistema(), arquivo.getAbsolutePath(), String.valueOf(totalLinhasTamanhoInvalido), String.valueOf(qtdMaximaLinhasTamanhoInvalido));
                }
            }
        }

        return totalLinhas;
    }

    /**
     * Verifica se no conjunto de arquivos possui algum arquivo compactado, e caso exista, extrai
     * seu conteúdo e substitui o arquivo original pelo arquivo texto.
     * @param arquivos          : Conjunto de arquivos a serem avaliados
     * @param tipoArquivo       : Tipo de arquivo que está sendo agrupado
     * @param caminhoCompleto   : Caminho completo de onde os arquivos foram localizados
     * @param nomeArquivoSaida  : Nome a ser aplicado após a junção do grupo de arquivos
     * @param testeExecucao     : Executa apenas para teste, ou seja, não move os arquivos antigos para .bkpx e o arquivo final terá a extensão .newx
     * @return
     * @throws ZetraException
     */
    private File agruparArquivos(List<File> arquivos, String tipoArquivo, String caminhoCompleto, String nomeArquivoSaida, boolean testeExecucao) throws ZetraException {
        if (TextHelper.isNull(nomeArquivoSaida)) {
            if (arquivos.size() == 1) {
                // Se não tem padrão de nome a ser aplicado sobre o arquivo final
                // e o conjunto só tem um arquivo, então este será considerado o final
                return arquivos.get(0);
            } else {
                throw new ZetraException("mensagem.erro.padrao.nome.agrupamento.nao.informado", AcessoSistema.getAcessoUsuarioSistema(),  tipoArquivo);
            }
        }

        final File arquivoSaida = new File(caminhoCompleto + File.separator + nomeArquivoSaida);
        if (arquivoSaida.exists()) {
            // Se o arquivo já existe, verifica se o conjunto de arquivos só
            // tem um elemento, que será considerado o arquivo final
            if (arquivos.size() == 1) {
                return arquivoSaida;
            } else {
                throw new ZetraException("mensagem.erro.arquivo.entrada.mesmo.nome.ja.existe", AcessoSistema.getAcessoUsuarioSistema(), tipoArquivo, arquivoSaida.getAbsolutePath());
            }
        }

        try (PrintWriter saida = new PrintWriter(new BufferedWriter(new FileWriter(arquivoSaida)))) {
            for (final File arquivo : arquivos) {
                // Se é um arquivo Zip
                if (arquivo.getName().toLowerCase().endsWith(".zip")) {
                    // Cria diretório para extração do conteúdo do arquivo zip
                    final String caminhoZip = arquivo.getAbsolutePath() + ".tmp";
                    final File caminho = new File(caminhoZip);
                    if (!caminho.mkdir()) {
                        throw new ZetraException("mensagem.erro.criar.diretorio.extracao.arquivo", AcessoSistema.getAcessoUsuarioSistema(), arquivo.getAbsolutePath());
                    }
                    try {
                        // Extrai o conteúdo do arquivo Zip
                        final List<String> nomeArquivosConteudo = FileHelper.unZipAll(arquivo.getAbsolutePath(), caminhoZip);

                        // Cria lista de arquivos para contabilização de linhas
                        for (final String nome : nomeArquivosConteudo) {
                            BufferedReader entrada = null;
                            try {
                                entrada = new BufferedReader(new FileReader(nome));
                                String linha = null;
                                while ((linha = entrada.readLine()) != null) {
                                    linha = linha.replaceAll("[^\\p{Print}\\p{InLatin-1 Supplement}]", "");
                                    if (linha.length() > 0) {
                                        saida.println(linha);
                                    }
                                }
                            } finally {
                                if (entrada != null) {
                                    entrada.close();
                                }
                            }
                        }
                    } catch (final IOException ex) {
                        throw new ZetraException("mensagem.erro.extracao.arquivo.concatenacao", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivo.getAbsolutePath());
                    } finally {
                        try {
                            // Apaga o conteúdo da pasta temporária
                            FileHelper.deleteDir(caminhoZip);
                        } catch (final IOException ex) {
                            throw new ZetraException("mensagem.erro.apagar.diretorio.temporario.extracao", AcessoSistema.getAcessoUsuarioSistema(), ex, caminhoZip);
                        }
                    }
                } else {
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new FileReader(arquivo));
                        String linha = null;
                        while ((linha = entrada.readLine()) != null) {
                            linha = linha.replaceAll("[^\\p{Print}\\p{InLatin-1 Supplement}]", "");
                            if (linha.length() > 0) {
                                saida.println(linha);
                            }
                        }
                    } catch (final IOException ex) {
                        throw new ZetraException("mensagem.erro.ler.arquivo.para.concatenacao", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivo.getAbsolutePath());
                    } finally {
                        if (entrada != null) {
                            entrada.close();
                        }
                    }
                }
            }
        } catch (final IOException ex) {
            throw new ZetraException("mensagem.erro.criar.arquivo", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivoSaida.getAbsolutePath());
        }

        if (!testeExecucao) {
            for (final File arquivo : arquivos) {
                arquivo.renameTo(new File(arquivo.getAbsolutePath() + ".bkpx"));
            }
            return arquivoSaida;
        } else {
            // Se execução apenas de teste, não move os originais para .bkpx pois em uma
            // segunda execução, os arquivos originais devem existir por causa da validação
            // do número de arquivos. Além disso, cria o arquivo final com .newx apenas
            // por questão de conferência
            final File arquivoSaidaNew = new File(arquivoSaida.getAbsolutePath() + ".newx");
            if (arquivoSaidaNew.exists()) {
                arquivoSaidaNew.delete();
            }
            arquivoSaida.renameTo(arquivoSaidaNew);
            return arquivoSaidaNew;
        }
    }

    /**
     * Executa rotina para validação do leiaute do arquivo baseado nos XMLs
     * definidos pelos parâmetros de sistema.
     * @param arquivo        : Arquivo a ser validado
     * @param tipoArquivo    : Tipo do arquivo a ser validado
     * @param tipoEntidade   : Tipo de entidade
     * @param codigoEntidade : Código da entidade
     * @throws ZetraException
     */
    private void validarLeiauteArquivo(File arquivo, String tipoArquivo, String tipoEntidade, String codigoEntidade) throws ZetraException {
        try {
            ValidaArquivoEntrada.validarEntradaPadrao(tipoArquivo.toUpperCase(), arquivo.getAbsolutePath(), tipoEntidade, codigoEntidade, false);
        } catch (final ZetraException ex) {
            throw new ZetraException("mensagem.erro.arquivo.invalido.leiaute", AcessoSistema.getAcessoUsuarioSistema(), ex, arquivo.getName());
        }
    }

    /**
     * Determina o nome do arquivo final substituindo os padrões presentes neste
     * de acordo com as possíveis variáveis: <PERIODO>, <ANO_MES_PERIODO>, <ANO_PERIODO>, <MES_PERIODO>, <ORG_ID>, <EST_ID>
     * @param padraoNomeArquivoFinal : Padrão de nome de arquivo
     * @param tipoEntidade           : Tipo de entidade
     * @param codigoEntidade         : Código da entidade
     * @param responsavel            : Usuário responsável
     * @return
     * @throws ZetraException
     */
    public static String substituirPadroesNomeArquivoFinal(String padraoNomeArquivoFinal, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        try {
            String nomeArquivoFinal = padraoNomeArquivoFinal;

            if (!TextHelper.isNull(padraoNomeArquivoFinal)) {

                final String orgCodigo = tipoEntidade.equalsIgnoreCase("ORG") ? codigoEntidade : null;
                final String estCodigo = tipoEntidade.equalsIgnoreCase("EST") ? codigoEntidade : null;

                List<String> orgCodigos = null;
                List<String> estCodigos = null;
                if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    orgCodigos = new ArrayList<>();
                    orgCodigos.add(orgCodigo);
                }
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    estCodigos = new ArrayList<>();
                    estCodigos.add(estCodigo);
                }

                // O padrão de nome de arquivo possui alguma variável de período
                if (padraoNomeArquivoFinal.contains("<PERIODO>") ||
                        padraoNomeArquivoFinal.contains("<ANO_MES_PERIODO>") ||
                        padraoNomeArquivoFinal.contains("<ANO_PERIODO>") ||
                        padraoNomeArquivoFinal.contains("<MES_PERIODO>")) {

                    // Obtém o período que aguarda retorno
                    final ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
                    final String periodo = retDelegate.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);
                    final Date periodoRetorno = DateHelper.parse(periodo, "yyyy-MM-dd");
                    // Realiza a substituição das variáveis
                    final String periodoCompleto = DateHelper.format(periodoRetorno, "yyyy_MM_dd");
                    final String anoMesPeriodo = DateHelper.format(periodoRetorno, "yyyyMM");
                    final String anoPeriodo = DateHelper.format(periodoRetorno, "yyyy");
                    final String mesPeriodo = DateHelper.format(periodoRetorno, "MM");

                    nomeArquivoFinal = nomeArquivoFinal
                        .replaceAll("<PERIODO>", periodoCompleto)
                        .replaceAll("<ANO_MES_PERIODO>", anoMesPeriodo)
                        .replaceAll("<ANO_PERIODO>", anoPeriodo)
                        .replaceAll("<MES_PERIODO>", mesPeriodo);
                }

                // O padrão de nome de arquivo possui variável de ORG ou EST identificador
                if (tipoEntidade.equalsIgnoreCase("ORG") && (
                        padraoNomeArquivoFinal.contains("<ORG_ID>") ||
                        padraoNomeArquivoFinal.contains("<EST_ID>"))) {
                    final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                    final OrgaoTransferObject org = cseDelegate.findOrgao(orgCodigo, responsavel);

                    // Coloca o identificador de órgão no padrão de nomes
                    nomeArquivoFinal = nomeArquivoFinal.replaceAll("<ORG_ID>", org.getOrgIdentificador());

                    if (padraoNomeArquivoFinal.contains("<EST_ID>")) {
                        final EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(org.getEstCodigo(), responsavel);

                        // Coloca o identificador de estabelecimento no padrão de nomes
                        nomeArquivoFinal = nomeArquivoFinal.replaceAll("<EST_ID>", est.getEstIdentificador());
                    }
                } else if (tipoEntidade.equalsIgnoreCase("EST") && padraoNomeArquivoFinal.contains("<EST_ID>")) {
                    final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                    final EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(estCodigo, responsavel);

                    // Coloca o identificador de estabelecimento no padrão de nomes
                    nomeArquivoFinal = nomeArquivoFinal.replaceAll("<EST_ID>", est.getEstIdentificador());
                }
            }

            return nomeArquivoFinal;

        } catch (final Exception ex) {
            throw new ZetraException("mensagem.erro.definir.nome.arquivo.saida", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    private String trimNullSafe(String value) {
        if (value == null) {
            return null;
        } else {
            return value.trim();
        }
    }

    @Override
    public int executar(String[] args) {
        final String instrucoesUso = "USE: java " + NOME_CLASSE + " TIPO_ENTIDADE CODIGO_ENTIDADE TIPO_EXECUCAO ARQUIVO_CONF [-t] \n\n"
                             + "OBS: \n"
                             + " -> TIPO_ENTIDADE   : CSE|ORG|EST \n"
                             + " -> CODIGO_ENTIDADE : código de CSE|ORG|EST \n"
                             + " -> TIPO_EXECUCAO   : Tipo execução da validação (0000 - Onde cada posição é setada com 1 para MARGEM, TRANSFERIDOS, RETORNO e CRITICA) \n"
                             + " -> ARQUIVO_CONF    : Nome do arquivo de configuração para validação, com caminho completo \n"
                             + " -> Opção -t        : Realiza a validação apenas de teste, ou seja, não gera o arquivo final para importação \n"
                             ;

        if (args.length < 4 || args.length > 5) {
            LOG.error(instrucoesUso);
            return -1;

        } else {
            try {
                final String tipoEntidade    = args[0];
                final String codigoEntidade  = args[1];
                final String tipoExecucao    = args[2];
                final String arqConfiguracao = args[3];
                final boolean testeExecucao  = args.length == 5 && args[4].equals("-t");

                if (tipoExecucao.length() != 4) {
                    throw new ZetraException("mensagem.erro.valida.importacao.execucao.invalida", AcessoSistema.getAcessoUsuarioSistema());
                }

                final boolean impMargem       = tipoExecucao.charAt(0) == '1';
                final boolean impTransferidos = tipoExecucao.charAt(1) == '1';
                final boolean impRetorno      = tipoExecucao.charAt(2) == '1';
                final boolean impCritica      = tipoExecucao.charAt(3) == '1';

                validar(tipoEntidade, codigoEntidade, impMargem, impTransferidos, impRetorno, impCritica, arqConfiguracao, testeExecucao, AcessoSistema.getAcessoUsuarioSistema());
                return 0;

            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            }
        }
    }
}
