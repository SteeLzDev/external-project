package com.zetra.econsig.helper.saldodevedor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

/**
 * <p>Title: ImportarSaldoDevedorHelper.java</p>
 * <p>Description: Helper Class para importação de saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportarSaldoDevedorHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarSaldoDevedorHelper.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;

    public static final String COMPLEMENTO_DEFAULT = " ";

    /** Totalizadores de registros */
    private int totalSaldoDevedorInformado;

    private int totalRegistros;

    private int totalProblema;

    /** Objetos para tradução do arquivo de entrada */
    private LeitorArquivoTexto leitor;

    private Escritor escritor;

    private Tradutor tradutor;

    private final AcessoSistema responsavel;

    public ImportarSaldoDevedorHelper(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String importar(String nomeArquivo, String csaCodigo, boolean validar, AcessoSistema responsavel) throws ViewHelperException {
        String tipoEntidade = AcessoSistema.ENTIDADE_CSA;
        String codigoEntidade = csaCodigo;

        // Inicializa os delegates necessarios
        AutorizacaoDelegate adeDelegate;
        ConsignatariaDelegate csaDelegate;
        ParametroDelegate parDelegate;
        SaldoDevedorDelegate sdvDelegate;
        ServidorDelegate serDelegate;
        try {
            adeDelegate = new AutorizacaoDelegate();
            csaDelegate = new ConsignatariaDelegate();
            parDelegate = new ParametroDelegate();
            sdvDelegate = new SaldoDevedorDelegate();
            serDelegate = new ServidorDelegate();
        } catch (ConsignatariaControllerException | SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.falhaComunicacao", responsavel);
        }

        try {
            csaDelegate.findConsignataria(csaCodigo, responsavel);
        } catch (ConsignatariaControllerException e) {
            throw new ViewHelperException("mensagem.consignatariaNaoInformada", responsavel);
        }

        // Verifica sistema de arquivo
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathSaldoDevedor = absolutePath + File.separatorChar + "saldodevedor" + File.separatorChar + "csa" + File.separatorChar;

        // Verifica se o caminho para a gravação existe
        File dir = new java.io.File(pathSaldoDevedor);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel);
        }

        // Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        if (sistemaBloqueado()) {
            throw new ViewHelperException("mensagem.erro.sistema.bloqueado.inativo", responsavel);
        }

        // Recupera parâmetros de configuração do sistema
        String pathSaldoDevedorDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar;

        // Recupera layout de importação do saldo devedor
        String nomeArqXmlEntrada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_SALDO_DEVEDOR, responsavel);
        String nomeArqXmlTradutor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_SALDO_DEVEDOR, responsavel);

        if (TextHelper.isNull(nomeArqXmlEntrada) || TextHelper.isNull(nomeArqXmlTradutor)) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.saldo.devedor.ausentes", responsavel);
        }

        String entradaImpSaldoDevedor = null;
        String tradutorImpSaldoDevedor = null;

        String entradaImpSaldoDevedorDefault = pathSaldoDevedorDefault + nomeArqXmlEntrada;
        String tradutorImpSaldoDevedorDefault = pathSaldoDevedorDefault + nomeArqXmlTradutor;

        File arqConfEntradaDefault = new File(entradaImpSaldoDevedorDefault);
        File arqConfTradutorDefault = new File(tradutorImpSaldoDevedorDefault);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.saldo.devedor.ausentes", responsavel);
        } else {
            entradaImpSaldoDevedor = entradaImpSaldoDevedorDefault;
            tradutorImpSaldoDevedor = tradutorImpSaldoDevedorDefault;
        }

        String fileName = pathSaldoDevedor + csaCodigo + File.separatorChar + nomeArquivo;

        // Verifica se o arquivo existe
        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new ViewHelperException("mensagem.erro.arquivo.nao.encontrado", responsavel, nomeArquivo);
        }

        if(!validar) {
            // Renomeia o arquivo antes de iniciar o processamento
            FileHelper.rename(fileName, fileName + ".prc");
            fileName += ".prc";
        }

        // Configura o leitor de acordo com o arquivo de entrada
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".zip.prc")) {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTextoZip(entradaImpSaldoDevedor, fileName);
        } else {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTexto(entradaImpSaldoDevedor, fileName);
        }

        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        HashMap<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        escritor = new EscritorMemoria(entrada);
        tradutor = new Tradutor(tradutorImpSaldoDevedor, leitor, escritor);

        try {
            // Grava Log para auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_SALDO_DEVEDOR, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivo));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(fileName))));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.leiaute", responsavel, nomeArqXmlEntrada, nomeArqXmlTradutor));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }

        // 3. Processamento das linhas do arquivo.
        String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();

        List<String> critica = new ArrayList<>();
        try {
            ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);
            if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
                critica.add(ApplicationResourcesHelper.getMessage("rotulo.critica.operacao.temporariamente.indisponivel", responsavel, restricao.getDescricao()));
            }
        } catch (ZetraException e) {
            critica.add(ApplicationResourcesHelper.getMessage("mensagem.erro.critica.operacao.temporariamente.indisponivel", responsavel));
        }

        // Inclui histórico do arquivo
        Long harCodigo = null;
        boolean gerouException = false;
        if(!validar) {
            try {
                String harObs = "";
                String harResultado = CodedValues.STS_INATIVO.toString();
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                harCodigo = hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_SALDO_DEVEDOR, fileName, harObs, null, null, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                LOG.error("Não foi possível inserir o histórico do arquivo de saldo devedor '" + nomeArquivo + "'.", ex);
            }
        }

        boolean isCompra = false;
        boolean isSolicitacaoSaldo = !isCompra;
        boolean exigeMultiplosSaldos = ParamSist.paramEquals(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, CodedValues.TPC_SIM, responsavel);
        boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);
        boolean infSaldoDevedorOpcional = ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        String msgErro;
        SaldoDevedorTransferObject saldoDevedorTO = null;
        TransferObject saldosDevedoresMultiplos = null;
        ParamSvcTO paramSvc = null;

        try {
            tradutor.iniciaTraducao(true);
        } catch (ParserException ex) {
            LOG.error("Erro em iniciar tradução.", ex);
            throw new ViewHelperException(ex);
        }

        boolean proximo = true;
        try {
            // Faz o loop de cada linha do arquivo para realizar as traduções
            while (proximo) {
                try {
                    proximo = tradutor.traduzProximo();
                    if (!proximo) {
                        break;
                    }

                    msgErro = "";

                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        // Realiza a validação de segurança contra ataque de XSS nos campos do lote
                        for (String key : entrada.keySet()) {
                            Object value = entrada.get(key);
                            if (value instanceof String) {
                                // Se for String, realiza o tratamento anti-XSS
                                entrada.put(key, XSSPreventionFilter.stripXSS((String) value));
                            }
                        }

                        // 3.1 Recuperar os campos que podem estar presentes no arquivo de entrada.
                        String adeNumero = (String) entrada.get("ADE_NUMERO");
                        String adeIdentificador = (String) entrada.get("ADE_IDENTIFICADOR");
                        String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
                        String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
                        String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                        String serCpf = (String) entrada.get("SER_CPF");
                        BigDecimal valorSaldoDevedor = !TextHelper.isNull(entrada.get("VALOR_SALDO_DEVEDOR")) ? NumberHelper.parseDecimal(entrada.get("VALOR_SALDO_DEVEDOR").toString()) : null;
                        BigDecimal valorSaldoDevedorDesc = !TextHelper.isNull(entrada.get("VALOR_SALDO_DEVEDOR_DESCONTO")) ? NumberHelper.parseDecimal(entrada.get("VALOR_SALDO_DEVEDOR_DESCONTO").toString()) : null;
                        String banco = (String) entrada.get("CODIGO_BANCO");
                        String agencia = (String) entrada.get("AGENCIA");
                        String conta = (String) entrada.get("CONTA");
                        String nomeFavorecido = (String) entrada.get("NOME_FAVORECICO");
                        String cnpjFavorecido = (String) entrada.get("CNPJ_FAVORECIDO");
                        Date dataValidadeSaldoDevedor = !TextHelper.isNull(entrada.get("DATA_VALIDADE_SALDO_DEVEDOR")) ? DateHelper.parse(entrada.get("DATA_VALIDADE_SALDO_DEVEDOR").toString(), "yyyy-MM-dd") : null;

                        BigDecimal valorSaldoDevedor1 = !TextHelper.isNull(entrada.get("VALOR_SALDO_DEVEDOR_1")) ? NumberHelper.parseDecimal(entrada.get("VALOR_SALDO_DEVEDOR_1").toString()) : null;
                        Date dataSaldoDevedor1 = !TextHelper.isNull(entrada.get("DATA_SALDO_DEVEDOR_1")) ? DateHelper.parse(entrada.get("DATA_SALDO_DEVEDOR_1").toString(), "yyyy-MM-dd") : null;
                        BigDecimal valorSaldoDevedor2 = !TextHelper.isNull(entrada.get("VALOR_SALDO_DEVEDOR_2")) ? NumberHelper.parseDecimal(entrada.get("VALOR_SALDO_DEVEDOR_2").toString()) : null;
                        Date dataSaldoDevedor2 = !TextHelper.isNull(entrada.get("DATA_SALDO_DEVEDOR_2")) ? DateHelper.parse(entrada.get("DATA_SALDO_DEVEDOR_2").toString(), "yyyy-MM-dd") : null;
                        BigDecimal valorSaldoDevedor3 = !TextHelper.isNull(entrada.get("VALOR_SALDO_DEVEDOR_3")) ? NumberHelper.parseDecimal(entrada.get("VALOR_SALDO_DEVEDOR_3").toString()) : null;
                        Date dataSaldoDevedor3 = !TextHelper.isNull(entrada.get("DATA_SALDO_DEVEDOR_3")) ? DateHelper.parse(entrada.get("DATA_SALDO_DEVEDOR_3").toString(), "yyyy-MM-dd") : null;
                        String qtdePrestacoes = (String) entrada.get("QTDE_PRESTACOES");

                        String observacao = (String) entrada.get("OBSERVACAO");
                        String detalhe = (String) entrada.get("DETALHE");
                        String numeroContrato = (String) entrada.get("NUMERO_CONTRATO");
                        String sdvLinkBoleto = (String) entrada.get("LINK_BOLETO");

                        //3.2. Validar se a linha processada possui as informações mínimas para a pesquisa do contrato e informação do saldo.
                        // Valida ade número ou identificador obrigatório
                        if (TextHelper.isNull(adeNumero) && TextHelper.isNull(adeIdentificador)) {
                            LOG.debug("A ade número ou identificador devem ser informados.");
                            throw new ViewHelperException("mensagem.informe.ade.numero.ou.identificador", responsavel);
                        }

                        // Valida matrícula ou cpf obrigatório
                        if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
                            LOG.debug("A matrícula e/ou o CPF do servidor devem ser informados.");
                            throw new ViewHelperException("mensagem.informe.matricula.cpf.servidor", responsavel);
                        }

                        // Listar o registro servidor
                        List<TransferObject> lstServidor = serDelegate.pesquisaServidor(tipoEntidade, codigoEntidade, estIdentificador, orgIdentificador, rseMatricula, serCpf, responsavel, false, null, false, null);

                        if (lstServidor == null || lstServidor.isEmpty()) {
                            throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
                        } else if (lstServidor.size() > 1) {
                            throw new AutorizacaoControllerException("mensagem.multiplosServidoresEncontrados", responsavel);
                        }

                        TransferObject servidor = lstServidor.get(0);

                        // Busca as informações sobre a consignação
                        TransferObject autdes = null;
                        try {
                            String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
                            List<String> adeNumeros = null;

                            if (!TextHelper.isNull(adeNumero)) {
                                adeNumeros = new ArrayList<>();
                                adeNumeros.add(adeNumero);
                            }

                            // Situações que permitem informação de saldo devedor
                            List<String> sadCodigos = new ArrayList<>();
                            sadCodigos.add(CodedValues.SAD_DEFERIDA);
                            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                            sadCodigos.add(CodedValues.SAD_ESTOQUE);
                            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
                            sadCodigos.add(CodedValues.SAD_EMCARENCIA);
                            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);

                            // Consignação com solicitação de saldo
                            CustomTransferObject criterio = new CustomTransferObject();
                            criterio.setAttribute("TIPO_OPERACAO", "solicitacao_saldo");

                            List<TransferObject> consignacoes = adeDelegate.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeros, TextHelper.objectToStringList(adeIdentificador), sadCodigos, null, -1, -1, criterio, responsavel);

                            if (consignacoes == null || consignacoes.isEmpty()) {
                                throw new ViewHelperException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                            } else if (consignacoes.size() > 1) {
                                throw new ViewHelperException("mensagem.maisDeUmaConsignacaoEncontrada", responsavel);
                            }

                            autdes = consignacoes.get(0);
                        } catch (AutorizacaoControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                            totalProblema++;
                            msgErro = ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                            critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        }

                        String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
                        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();

                        SaldoDevedorController saldoDevedorController = ApplicationContextProvider.getApplicationContext().getBean(SaldoDevedorController.class);
                        if(!saldoDevedorController.temSolicitacaoSaldoDevedor(adeCodigo, false, responsavel)) {
                            critica.add(leitor.getLinha() + delimitador + ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.nao.possui.solicitacao.de.saldo.devedor.de.servidor", responsavel));
                            throw new ViewHelperException("mensagem.erro.consignacao.nao.possui.solicitacao.de.saldo.devedor.de.servidor", responsavel);
                        }

                        // Busca os parâmetros de serviço necessários
                        try {
                            List<String> tpsCodigo = new ArrayList<>();
                            tpsCodigo.add(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO);
                            tpsCodigo.add(CodedValues.TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE);
                            tpsCodigo.add(CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR);
                            tpsCodigo.add(CodedValues.TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC);
                            tpsCodigo.add(CodedValues.TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO);
                            tpsCodigo.add(CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO);
                            tpsCodigo.add(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP);
                            paramSvc = parDelegate.selectParamSvcCse(svcCodigo, tpsCodigo, responsavel);
                        } catch (ParametroControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                            totalProblema++;
                            msgErro = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel);
                            critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        }

                        boolean numeroContratoObrigatorio = isCompra ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorCompra() : (isSolicitacaoSaldo ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorSolicSaldo() : false);
                        boolean exigeValorComDesconto = temModuloFinancDividaCartao && isSolicitacaoSaldo && !TextHelper.isNull(paramSvc.getTpsPercentualMinimoDescontoVlrSaldo());

                        if (numeroContratoObrigatorio && TextHelper.isNull(numeroContrato)) {
                            throw new ViewHelperException("mensagem.informe.numero.contrato", responsavel);
                        }

                        if (exigeMultiplosSaldos) {
                            if (TextHelper.isNull(valorSaldoDevedor1)) {
                                throw new ViewHelperException("mensagem.informe.sdv.valor.primeiro.vencimento", responsavel);
                            }
                            if (valorSaldoDevedor1.signum() <= 0) {
                                throw new ViewHelperException("mensagem.erro.primeiro.saldo.devedor.maior.zero", responsavel);
                            }
                            if (TextHelper.isNull(dataSaldoDevedor1)) {
                                throw new ViewHelperException("mensagem.informe.sdv.data.primeiro.vencimento", responsavel);
                            }
                            if (TextHelper.isNull(valorSaldoDevedor2)) {
                                throw new ViewHelperException("mensagem.informe.sdv.valor.segundo.vencimento", responsavel);
                            }
                            if (valorSaldoDevedor2.signum() <= 0) {
                                throw new ViewHelperException("mensagem.erro.segundo.saldo.devedor.maior.zero", responsavel);
                            }
                            if (TextHelper.isNull(dataSaldoDevedor2)) {
                                throw new ViewHelperException("mensagem.informe.sdv.data.segundo.vencimento", responsavel);
                            }
                            if (TextHelper.isNull(valorSaldoDevedor3)) {
                                throw new ViewHelperException("mensagem.informe.sdv.valor.terceiro.vencimento", responsavel);
                            }
                            if (valorSaldoDevedor3.signum() <= 0) {
                                throw new ViewHelperException("mensagem.erro.terceiro.saldo.devedor.maior.zero", responsavel);
                            }
                            if (TextHelper.isNull(dataSaldoDevedor3)) {
                                throw new ViewHelperException("mensagem.informe.sdv.valor.terceiro.vencimento", responsavel);
                            }
                            if (TextHelper.isNull(qtdePrestacoes)) {
                                throw new ViewHelperException("mensagem.informe.sdv.qtde.parcelas", responsavel);
                            }

                        } else {
                            if (TextHelper.isNull(valorSaldoDevedor)) {
                                throw new ViewHelperException("mensagem.erro.saldo.devedor.valor.incorreto", responsavel);
                            }
                            if (valorSaldoDevedor.signum() <= 0) {
                                throw new ViewHelperException("mensagem.erro.saldo.devedor.maior.zero", responsavel);
                            }
                        }

                        if (exigeValorComDesconto) {
                            if (TextHelper.isNull(valorSaldoDevedorDesc)) {
                                throw new ViewHelperException("mensagem.erro.saldo.devedor.desconto.valor.incorreto", responsavel);
                            }
                            if (valorSaldoDevedorDesc.signum() <= 0) {
                                throw new ViewHelperException("mensagem.erro.saldo.devedor.desconto.maior.zero", responsavel);
                            }
                        }

                        if (!infSaldoDevedorOpcional) {
                            if (TextHelper.isNull(banco)) {
                                throw new ViewHelperException("mensagem.informe.banco.deposito", responsavel);
                            }
                            if (TextHelper.isNull(agencia)) {
                                throw new ViewHelperException("mensagem.informe.agencia.deposito", responsavel);
                            }
                            if (TextHelper.isNull(conta)) {
                                throw new ViewHelperException("mensagem.informe.conta.deposito", responsavel);
                            }
                            if (TextHelper.isNull(nomeFavorecido)) {
                                throw new ViewHelperException("mensagem.informe.favorecido.deposito", responsavel);
                            }
                            if (TextHelper.isNull(cnpjFavorecido)) {
                                throw new ViewHelperException("mensagem.informe.cnpj.deposito", responsavel);
                            }
                        }

                        //3.3. Verificar se o status do contrato permite informação de saldo devedor de acordo com as regras do caso de uso de informar saldo devedor.
                        // Cria TO com os dados do saldo devedor
                        saldoDevedorTO = new SaldoDevedorTransferObject();
                        saldoDevedorTO.setAdeCodigo(adeCodigo);
                        saldoDevedorTO.setUsuCodigo((responsavel != null ? responsavel.getUsuCodigo() : null));
                        saldoDevedorTO.setBcoCodigo(!TextHelper.isNull(banco) ? Short.valueOf(banco) : null);
                        saldoDevedorTO.setSdvAgencia(agencia != null ? agencia : "");
                        saldoDevedorTO.setSdvConta(conta != null ? conta : "");
                        saldoDevedorTO.setSdvNomeFavorecido(nomeFavorecido != null ? nomeFavorecido : "");
                        saldoDevedorTO.setSdvCnpj(cnpjFavorecido != null ? cnpjFavorecido : "");
                        saldoDevedorTO.setObs(observacao);
                        saldoDevedorTO.setSdvValor(exigeMultiplosSaldos ? valorSaldoDevedor1 : valorSaldoDevedor);
                        saldoDevedorTO.setSdvValorComDesconto(valorSaldoDevedorDesc);
                        saldoDevedorTO.setSdvDataValidade(dataValidadeSaldoDevedor);
                        saldoDevedorTO.setSdvNumeroContrato(!TextHelper.isNull(numeroContrato) ? numeroContrato : null);
                        saldoDevedorTO.setSdvLinkBoletoQuitacao(sdvLinkBoleto);

                        // Cria TO com os dados dos múltiplos saldos devedores
                        if (exigeMultiplosSaldos) {
                            String dataCadastro = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());

                            String strDataSaldoDevedor1 = LocaleHelper.getDateFormat().format(dataSaldoDevedor1);
                            String strDataSaldoDevedor2 = LocaleHelper.getDateFormat().format(dataSaldoDevedor2);
                            String strDataSaldoDevedor3 = LocaleHelper.getDateFormat().format(dataSaldoDevedor3);
                            String strValorSaldoDevedor1 =  NumberHelper.reformat(valorSaldoDevedor1.toString(), "en", "en");
                            String strValorSaldoDevedor2 = NumberHelper.reformat(valorSaldoDevedor2.toString(), "en", "en");
                            String strValorSaldoDevedor3 = NumberHelper.reformat(valorSaldoDevedor3.toString(), "en", "en");

                            StringBuilder obs = new StringBuilder();
                            obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.qtde.prd.liquidada", responsavel)).append(":").append(qtdePrestacoes);
                            obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, strDataSaldoDevedor1, strValorSaldoDevedor1));
                            obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, strDataSaldoDevedor2, strValorSaldoDevedor2));
                            obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, strDataSaldoDevedor3, strValorSaldoDevedor3));
                            if (!TextHelper.isNull(observacao)) {
                                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.observacao.abreviado", responsavel).toUpperCase()).append(": ").append(observacao);
                            }

                            // Valores a serem usados na rotina padrão.
                            saldoDevedorTO.setObs(obs.toString());

                            saldosDevedoresMultiplos = new CustomTransferObject();
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATACADASTRO, dataCadastro);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES, qtdePrestacoes);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO1, strDataSaldoDevedor1);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO1, strValorSaldoDevedor1);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO2, strDataSaldoDevedor2);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO2, strValorSaldoDevedor2);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO3, strDataSaldoDevedor3);
                            saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO3, strValorSaldoDevedor3);
                        }

                        // TODO Verificar futuramente se existe necessidade de implementação de propostas na importação
                        List<PropostaPagamentoDividaTO> propostasPgtSaldo = null;

                        // Se limite saldo cadastrado e permite saldo fora da faixa limite, então chama rotina para validação
                        // de saldo, e caso retorne falso, o saldo está acima da faixa limite, portanto a consignatária
                        // deve informar os detalhes do cálculo do saldo.
                        boolean limitaSaldoDevedor = responsavel.isCseSupOrg() ? paramSvc.isTpsLimitaSaldoDevedorCadastradoCseOrgSup() : paramSvc.isTpsLimitaSaldoDevedorCadastrado();
                        if (limitaSaldoDevedor && paramSvc.isTpsPermiteSaldoForaFaixaLimite()) {
                            if (!sdvDelegate.validarSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, responsavel)) {
                                if (TextHelper.isNull(detalhe)) {
                                    // Se não foi informado os detalhes, então retorna para que o usuário possa preencher este campo.
                                    throw new ViewHelperException("mensagem.erro.saldo.devedor.limite.invalido", responsavel);
                                } else {
                                    // Se já foi informado o detalhe, então prossegue a atualização das informações
                                    detalhe = detalhe.replaceAll("\r\n", "<BR>").replaceAll("\n", "<BR>");
                                    saldoDevedorTO.setObs(saldoDevedorTO.getObs() + "<BR><B>" + ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.detalhe", responsavel).toUpperCase() + ":</B> " + detalhe);
                                }
                            }
                        }
                        if(!validar) {
                            //3.4. Atualizar o saldo devedor do contrato e criar ocorrência de informação de saldo devedor. Verificar a possibilidade de reutilizar os métodos existentes para atualização de saldo devedor.
                            // Atualiza as informações
                            if (sdvDelegate.getSaldoDevedor(adeCodigo, responsavel) == null) {
                                sdvDelegate.createSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, isCompra, true, responsavel);
                            } else {
                                sdvDelegate.updateSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, isCompra, true, responsavel);
                            }
                        }
                        totalSaldoDevedorInformado++;

                        if(!validar) {
                            // Verifica se a consignatária pode ser desbloqueada automaticamente
                            csaDelegate.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                        }

                    } else {
                        totalProblema++;
                        msgErro = entrada.get("LINHA_INVALIDA").toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("mensagem.linhaInvalida", responsavel) : entrada.get("LINHA_INVALIDA").toString();
                        critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }

                } catch (ParserException ex) {
                    gerouException = true;
                    if (ex.getMessageKey().indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1 || ex.getMessageKey().indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1) {
                        throw new ViewHelperException(ex);
                    }

                    LOG.error("Erro de parser na Importação de saldo devedor: " + ex.getMessage(), ex);

                    StringBuilder mensagem = new StringBuilder(ex.getMessage());
                    if (ex instanceof ZetraException) {
                        ZetraException ze = ex;
                        mensagem = new StringBuilder(ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE));
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem.append(": ").append(ex.getMessage());
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem.toString()));

                } catch (Exception ex) {
                    StringBuilder mensagem = new StringBuilder(ex.getMessage());
                    if (ex instanceof ZetraException) {
                        ZetraException ze = (ZetraException) ex;
                        mensagem = new StringBuilder(ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE));
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem.append(": ").append(ex.getMessage());
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem.toString()));
                }

                totalRegistros++;
            }

        } finally {
            if(!validar) {
                //3.6. Ao final do processamento, renomear o arquivo para identificar que já foi processado.
                // Renomeia o arquivo processado para .ok
                FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
            }

            if (harCodigo != null) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, null, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível alterar o histórico do arquivo de saldo devedor '" + nomeArquivo + "'.", e);
                }
            }

            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        //3.5. Em caso de problemas no processamento, criar arquivo de crítica com as linhas que não foram processadas.
        String nomeArqSaida;
        String nomeArqSaidaTxt;
        String nomeArqSaidaZip;
        try {
            if (!critica.isEmpty()) {
                // Grava arquivo contendo as parcelas não encontradas no sistema
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                String pathSaida = pathSaldoDevedor + csaCodigo + File.separatorChar;
                File diretorio = new File(pathSaida);
                if (!diretorio.exists() && !diretorio.mkdirs()) {
                    throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, diretorio.getAbsolutePath());
                }

                if(!validar) {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                } else {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel);
                }
                nomeArqSaida += nomeArquivo + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (leitor.getLinhaHeader() != null && !leitor.getLinhaHeader().trim().equals("")) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));
                if (leitor.getLinhaFooter() != null && !leitor.getLinhaFooter().trim().equals("")) {
                    // Imprime a linha de footer no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null));
                }
                arqSaida.close();

                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);

                // log do resultado geral da importação
                logResumoProcessamento();

                return nomeArqSaidaZip;
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }

        // log do resultado geral da importação
        logResumoProcessamento();

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return null;
    }

    private void logResumoProcessamento() {
        LogDelegate log = null;
        log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_SALDO_DEVEDOR, Log.LOG_INFORMACAO);
        try {
            log.add(ApplicationResourcesHelper.getMessage("mensagem.arquivo.saldo.devedor.resumo.importacao", responsavel, String.valueOf(totalRegistros), String.valueOf(totalSaldoDevedorInformado), String.valueOf(totalProblema)));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        // Concatena a mensagem de erro no final da linha de entrada
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    /**
     * Verifica se o sistema está bloqueado.
     * @return
     */
    protected boolean sistemaBloqueado() {
        boolean bloqueado = false;
        try {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            Short codigo = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            bloqueado = (codigo.equals(CodedValues.STS_INDISP) || codigo.equals(CodedValues.STS_INATIVO));
        } catch (ConsignanteControllerException e1) {
            LOG.error("Não foi possível verificar bloqueio do sistema. " + e1.getMessage());
        }
        return bloqueado;
    }
}
