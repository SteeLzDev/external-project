package com.zetra.econsig.folha.importacao;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.ObjectFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ValidaIntegracaoConsignataria</p>
 * <p>Description: Valida relatórios de integração de Consignatária se possuem convênios válidos no sistema.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidaIntegracaoConsignataria {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaIntegracaoConsignataria.class);

    private static final int NOME_COM_INFO_ORG_EST = 5;
    private static final int NOME_COM_INFO_EST = 4;

    public static void validar() throws ZetraException {
        Map<String, List<TransferObject>> cnvCsaMap = carregaConvenios();
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String path = ParamSist.getDiretorioRaizArquivos()
                + File.separatorChar + "relatorio"
                + File.separatorChar + "csa"
                + File.separatorChar + "integracao";

        File dirIntegracao = new File(path);
        if (dirIntegracao.exists() && dirIntegracao.isDirectory()) {
            String[] dirList = dirIntegracao.list();

            FileFilter filtro = new FileFilter() {
                @Override
                public boolean accept(File arq) {
                    long fileDateTimeMillis = arq.lastModified();
                    Calendar fileDate = Calendar.getInstance();
                    fileDate.setTimeInMillis(fileDateTimeMillis);
                    int idadeArquivoEmDias = DateHelper.dayDiff(fileDate.getTime());
                    boolean naFaixaDeTempo = (idadeArquivoEmDias <= CodedValues.LIMITE_DIAS_DATA_RELAT_INTEGRACAO_A_VERIFICAR) ? true : false;
                    return naFaixaDeTempo && (arq.getName().toLowerCase().endsWith(".zip") || arq.getName().toLowerCase().endsWith(".txt"));
                }
            };

            StringBuilder listaArqInvalidos = new StringBuilder();
            for (String csaCodigo : dirList) {
                File diretorioCsa = new File(path + File.separatorChar + csaCodigo);
                if (diretorioCsa.isDirectory()) {
                    File[] arquivos = diretorioCsa.listFiles(filtro);
                    if (arquivos != null && arquivos.length > 0) {
                        Map<String, Short> aDeletar = new HashMap<>();
                        boolean csaJaListado = false;

                        String csaIdentificador = null;
                        String csaNome = null;

                        try {
                            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                            ConsignatariaTransferObject csa = csaDelegate.findConsignataria(csaCodigo, responsavel);
                            csaIdentificador = csa.getCsaIdentificador();
                            csaNome = csa.getCsaNome();
                        } catch (Exception ex) {
                            // Consignatária não encontrada: provavelmente um diretório, com
                            // arquivos mas que não represente uma consignatária
                            LOG.error(ex.getMessage(), ex);
                            continue;
                        }

                        for (File relatorioIntegracao : arquivos) {
                            if (!relatorioIntegracao.getName().toLowerCase().startsWith("cst_")) {                                                           
                                Map<String, Object> entrada = new HashMap<>();
                                Tradutor tradutor = criaTradutorRetorno(relatorioIntegracao, csaCodigo, entrada);

                                tradutor.iniciaTraducao();
                                boolean traduz = true;
                                Integer linhaCorrente = 1;

                                while (traduz) {
                                    try {
                                        traduz = tradutor.traduzProximo();
                                        List<TransferObject> convenios = (cnvCsaMap == null ? null : cnvCsaMap.get(csaCodigo));

                                        boolean erro = true;

                                        if (convenios != null && !convenios.isEmpty()) {
                                            Iterator<TransferObject> it = convenios.iterator();
                                            while (it.hasNext()) {
                                                Object codVerbaEntrada = entrada.get("CNV_COD_VERBA");
                                                Object svcIdnEntrada = entrada.get("SVC_IDENTIFICADOR");
                                                TransferObject csaTo = it.next();
                                                String codVerba = (String) csaTo.getAttribute(Columns.CNV_COD_VERBA);
                                                String codVerbaRef = (String) csaTo.getAttribute(Columns.CNV_COD_VERBA_REF);
                                                String codVerbaFerias = (String) csaTo.getAttribute(Columns.CNV_COD_VERBA_FERIAS);
                                                String svcIdentificador = (String) csaTo.getAttribute(Columns.SVC_IDENTIFICADOR);

                                                if ((!TextHelper.isNull(codVerba) && codVerba.equals(codVerbaEntrada)) || (!TextHelper.isNull(codVerbaRef) && codVerbaRef.equals(codVerbaEntrada)) || (!TextHelper.isNull(codVerbaFerias) && codVerbaFerias.equals(codVerbaEntrada))) {
                                                    // se pode repetir código de verba entre serviços, confere o identificador do serviço também
                                                    if (TextHelper.isNull(svcIdnEntrada) || svcIdnEntrada.equals(svcIdentificador)) {
                                                        erro = false;
                                                    }
                                                    break;
                                                }
                                            }

                                            if (erro) {
                                                aDeletar.put(relatorioIntegracao.getAbsolutePath(), linhaCorrente.shortValue());
                                                tradutor.encerraTraducao();
                                                tradutor = null;
                                                if (!csaJaListado) {
                                                    csaJaListado = true;
                                                    listaArqInvalidos.append("<br><br><b>" + csaIdentificador + " - " + csaNome + "</b>");
                                                }
                                                break;
                                            }
                                            linhaCorrente++;
                                        }
                                    } catch (ParserException pex) {
                                        LOG.warn(pex.getMessage() + " arquivo: " + relatorioIntegracao.getName());
                                        aDeletar.put(relatorioIntegracao.getAbsolutePath(), linhaCorrente.shortValue());
                                        tradutor.encerraTraducao();
                                        tradutor = null;
                                        if (!csaJaListado) {
                                            csaJaListado = true;
                                            listaArqInvalidos.append("<br><br><b>" + csaIdentificador + " - " + csaNome + "</b>");
                                        }
                                        break;
                                    }
                                }

                                if (tradutor != null) {
                                    tradutor.encerraTraducao();
                                }
                            }
                        }

                        Iterator<String> it = aDeletar.keySet().iterator();
                        while (it.hasNext()) {
                            String arqInvalidoNome = it.next();
                            FileHelper.rename(arqInvalidoNome, arqInvalidoNome + ".erro");
                            listaArqInvalidos.append("<br>").append(arqInvalidoNome.substring(arqInvalidoNome.lastIndexOf(File.separatorChar) + 1));
                            listaArqInvalidos.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.email.linha.occorencia.codigo.verba.invalido", responsavel)).append(aDeletar.get(arqInvalidoNome));
                        }
                    }
                }
            }

            if (!TextHelper.isNull(listaArqInvalidos.toString())) {
                //envia e-mail para responsável (gestor ou outro) reportando relatórios inválidos
                EnviaEmailHelper.enviarEmailValidacaoIntegracaoCsa(listaArqInvalidos.toString(), null, responsavel);
            }
        }
    }

    private static Map<String, List<TransferObject>> carregaConvenios() throws ZetraException {
        Map<String, List<TransferObject>> cnvMap = null;
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String path = ParamSist.getDiretorioRaizArquivos()
                + File.separatorChar + "relatorio"
                + File.separatorChar + "csa"
                + File.separatorChar + "integracao";

        File dirIntegracao = new File(path);

        if (!dirIntegracao.exists() || !dirIntegracao.isDirectory() || !dirIntegracao.canRead()) {
            LOG.warn("Diretório de relatórios de integração de consignatárias não encontrado.");
        } else {
            String[] dirList = dirIntegracao.list();

            if (dirList == null || dirList.length == 0) {
                LOG.info("Nenhum relatório de integração de consignatária encontrado.");
            } else {
                ConvenioDelegate cnvDelegate = new ConvenioDelegate();
                for (String csaCodigo : dirList) {
                    FileFilter filtro = new FileFilter() {
                        @Override
                        public boolean accept(File arq) {
                            return arq.getName().toLowerCase().endsWith(".zip") || arq.getName().toLowerCase().endsWith(".txt");
                        }
                    };

                    File diretorioCsa = new File(path + File.separatorChar + csaCodigo);
                    if (diretorioCsa.isDirectory()) {
                        File[] arquivos = diretorioCsa.listFiles(filtro);

                        // adiciona ao grupo de convênios apenas das consignatárias que possuem relatórios a verificar
                        if (arquivos != null && arquivos.length > 0) {
                            List<TransferObject> csaCodVerbas = cnvDelegate.recuperaCsaCodVerba(csaCodigo, true, responsavel);

                            if (csaCodVerbas != null && !csaCodVerbas.isEmpty()) {
                                if (cnvMap == null) {
                                    cnvMap = new HashMap<>();
                                }

                                cnvMap.put(csaCodigo, csaCodVerbas);
                            }
                        }
                    }
                }
            }
        }

        return cnvMap;
    }

    /**
     * Cria o tradutor do arquivo de tipo definido pelo parâmetro.
     * @param arquivo    : arquivo de entrada
     * @param csaCodigo  : Código da entidade consignatária
     * @param entrada    : Cache dos dados de entrada
     * @return
     * @throws ZetraException
     */
    private static Tradutor criaTradutorRetorno(File arquivo, String csaCodigo, Map<String, Object> entrada) throws ZetraException {
        ParamSist paramSist = ParamSist.getInstance();
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String path = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "conf" + File.separatorChar;
        String nomeEntradaXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO, responsavel);
        String nomeTradutorXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO, responsavel);

        if (TextHelper.isNull(nomeEntradaXml) || TextHelper.isNull(nomeTradutorXml)) {
            throw new ZetraException("mensagem.erro.configuracao.parametro.arquivo.importacao.retorno", responsavel);
        }

        // Verifica se há informação do identificador do órgão/estabelecimento no nome do arquivo, significando que irá procurar
        // arquivo de configuração customizada para este. Caso contrário busca no diretório padrão de configurações.
        try {
            String entradaXml = null;
            String tradutorXml = null;

            // Exemplo:
            // retorno_EST_ORG_DD-MM-AAAA-HHMMSS_MMAAAA.txt
            // fileNameSplit[0] = prefixo "retorno"
            // fileNameSplit[1] = identificador EST
            // fileNameSplit[2] = identificador ORG
            // fileNameSplit[3] = data geração arquivo
            // fileNameSplit[4] = período

            String[] fileNameSplit = arquivo.getName().split("_");

            if (fileNameSplit.length == NOME_COM_INFO_ORG_EST) {
                String estIdentificador = fileNameSplit[1];
                String orgIdentificador = fileNameSplit[2];
                ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                EstabelecimentoTransferObject est = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);
                OrgaoTransferObject org = cseDelegate.findOrgaoByIdn(orgIdentificador, est.getEstCodigo(), responsavel);

                entradaXml = path + "cse" + File.separatorChar + org.getOrgCodigo() + File.separatorChar + nomeEntradaXml;
                tradutorXml = path + "cse" + File.separatorChar + org.getOrgCodigo() + File.separatorChar + nomeTradutorXml;

            } else if (fileNameSplit.length == NOME_COM_INFO_EST) {
                String estIdentificador = fileNameSplit[1];
                ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                EstabelecimentoTransferObject est = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);

                entradaXml = path + "est" + File.separatorChar + est.getEstCodigo() + File.separatorChar + nomeEntradaXml;
                tradutorXml = path + "est" + File.separatorChar + est.getEstCodigo() + File.separatorChar + nomeTradutorXml;
            }

            if (TextHelper.isNull(entradaXml) || !(new File(entradaXml).exists())) {
                entradaXml = path + nomeEntradaXml;
            }
            if (TextHelper.isNull(tradutorXml) || !(new File(tradutorXml).exists())) {
                tradutorXml = path + nomeTradutorXml;
            }

            String entradaValidacaoIntegracao = gerarXmlEntradaValidacaoIntegracao(entradaXml);
            return criaTradutor(arquivo, entradaValidacaoIntegracao, tradutorXml, entrada);

        } catch (IOException ex) {
            throw new ZetraException("mensagem.erro.relatorio.integracao.consignataria", responsavel, ex);
        } catch (FindException ex) {
            throw new ZetraException("mensagem.erro.orgao.estabelecimento.nao.encontrados", responsavel, ex);
        }
    }

    /**
     * gera XML de entrada simplificada a partir do XML de entrada para o retorno. Caso este tenha Headers e Footers, o novo XML
     * gerado irá retirá-los e manter apenas os parâmetros com os dados do relatório
     * @param nomeArqConfEntrada
     * @return
     * @throws ZetraException
     */
    private static String gerarXmlEntradaValidacaoIntegracao(String nomeArqConfEntrada) throws ZetraException, FileNotFoundException {
        DocumentoTipo documento;
        try {
            documento = XmlHelper.unmarshal(new FileInputStream(nomeArqConfEntrada));

            ObjectFactory factory = new ObjectFactory();
            DocumentoTipo novoDocumento = factory.createDocumentoTipo();

            // Inclui os atributos e parâmetros do layout de entrada XML
            novoDocumento.getParametro().addAll(documento.getParametro());
            novoDocumento.getAtributo().addAll(documento.getAtributo());

            String nomeArqConfEntradaValidacao = nomeArqConfEntrada.replace(".xml", "_validacao.xml");
            XmlHelper.marshal(novoDocumento, new FileOutputStream(nomeArqConfEntradaValidacao));
            return nomeArqConfEntradaValidacao;
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.gerar.configuracao.entrada.relatorio.integracao", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    /**
     * Cria o tradutor do arquivo de tipo genérico.
     * @param nomeArquivo : Nome do arquivo de entrada
     * @param xmlEntrada  : Nome do XML de configuração do leitor
     * @param xmlTradutor : Nome do XML de configuração do tradutor
     * @return
     * @throws ZetraException
     */
    private static Tradutor criaTradutor(File arquivo, String xmlEntrada, String xmlTradutor, Map<String, Object> entrada) throws ZetraException {
        LeitorArquivoTexto leitor;

        if (!new File(xmlEntrada).exists() || !new File(xmlTradutor).exists()) {
            throw new ZetraException("mensagem.erro.arquivos.xml.configuracao.importacao.ausentes", AcessoSistema.getAcessoUsuarioSistema());
        }

        if (!arquivo.exists()) {
            throw new ZetraException("mensagem.erro.arquivo.entrada.importacao.nao.encontrado", AcessoSistema.getAcessoUsuarioSistema());
        }

        // Configura o leitor de acordo com o arquivo de entrada
        if (arquivo.getName().toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(xmlEntrada, arquivo.getAbsolutePath());
        } else {
            leitor = new LeitorArquivoTexto(xmlEntrada, arquivo.getAbsolutePath());
        }

        // Escritor e tradutor
        Escritor escritor = new EscritorMemoria(entrada);
        Tradutor tradutor = new Tradutor(xmlTradutor, leitor, escritor);
        return tradutor;
    }
}
