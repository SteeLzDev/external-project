package com.zetra.econsig.job.process;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.ParcelasProcessadasFuturasBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * <p>Title: ProcessaRelatorioParcelasProcessadasFuturas</p>
 * <p>Description: Classe para processamento de relatorio de parcelas processadas e futuras
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ProcessaRelatorioParcelasProcessadasFuturas extends ProcessaRelatorio{
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioParcelasProcessadasFuturas.class);

    public ProcessaRelatorioParcelasProcessadasFuturas(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        final HashMap<String, Object> parameters = new HashMap<>();

        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.parcelas.processadas.futuras", responsavel), responsavel, parameterMap, null));
        final StringBuilder subTitulo = new StringBuilder();

        final String strIniPeriodo = getParametro("periodoIni", parameterMap);
        final String strFimPeriodo = getParametro("periodoFim", parameterMap);

        final Map<String, String> datas = getFiltroPeriodoIniFim(parameterMap);
        final String paramIniPeriodo = datas.get("PERIODO_INICIAL");
        final String paramFimPeriodo = datas.get("PERIODO_FINAL");

        final boolean obrDataInclusaoPage = Boolean.parseBoolean(getParametro("obrDataInclusaoPage", parameterMap));
        if(obrDataInclusaoPage) {
            if ((!TextHelper.isNull(paramIniPeriodo) && TextHelper.isNull(paramFimPeriodo)) ||
                (TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo)) ||
                (TextHelper.isNull(paramIniPeriodo) && TextHelper.isNull(paramFimPeriodo))) {

                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
                return;
            }
        }

        final StringBuilder titulo = new StringBuilder().append(relatorio.getTitulo()).append(" - ");
        if (!TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo)) {
            titulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));
        }

        final List<String> sadCodigos = new ArrayList<>(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_LIQUIDADO_CONCLUIDO);

        final List<String> origensAdes = getFiltroOrigemContrato(parameterMap, subTitulo, nome, session, responsavel);
        final String[] motivoTerminoAde = parameterMap.get("chkTermino");
        final List<String> motivoTerminoAdes = motivoTerminoAde != null ? Arrays.asList(motivoTerminoAde) : null;

        if((motivoTerminoAdes != null) && (origensAdes != null)) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente", responsavel);
            return;

        }

        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> corCodigos = new ArrayList<>();
        if (responsavel.isCor()) {
            corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
        }

        final String sboCodigo = getFiltroSboCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String uniCodigo = getFiltroUniCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String strFormato = getStrFormato();
        final Boolean tmoDecisaoJudicial = getFiltroDecisaoJudicial(parameterMap, subTitulo, nome, session, responsavel);

        final List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> orgCodigos = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String rseMatricula = getFiltroRseMatricula(parameterMap, subTitulo, nome, session, responsavel);
        final String serCpf = getFiltroCpf(parameterMap, subTitulo, nome, session, responsavel);
        subTitulo.append(formataSubtitulo(rseMatricula, serCpf).toString());

        if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.matricula.ou.cpf", responsavel);
            return;
        }

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

        final String estCodigo = ("EST".equals(tipoEntidade)) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);

        criterio.setAttribute("responsavel", responsavel);
        criterio.setAttribute("RSE_MATRICULA", rseMatricula);
        criterio.setAttribute("SER_CPF", serCpf);
        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
        criterio.setAttribute("EST_CODIGO", estCodigo);
        criterio.setAttribute("ORG_CODIGO", orgCodigos);
        criterio.setAttribute("SBO_CODIGO", sboCodigo);
        criterio.setAttribute("UNI_CODIGO", uniCodigo);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("NSE_CODIGO", nseCodigos);
        criterio.setAttribute("SAD_CODIGO", sadCodigos);
        criterio.setAttribute("ORIGEM_ADE", origensAdes);
        criterio.setAttribute("TERMINO_ADE", motivoTerminoAdes);
        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
        criterio.setAttribute("TMO_DECISAO_JUDICIAL", tmoDecisaoJudicial);
        if (parameterMap.containsKey("corCodigo")) {
            final String[] correspondentes = (parameterMap.get("corCodigo"));
            if(!"".equals(correspondentes[0])) {
                if ((correspondentes.length == 0) || "-1".equals(correspondentes[0].substring(0, 2))) {
                    corCodigos.add("-1");
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                    for(final String cor : correspondentes){
                        final String [] correspondente = cor.split(";");
                        corCodigos.add(correspondente[0]);
                        subTitulo.append(correspondente[2]).append(", ");
                    }
                    subTitulo.deleteCharAt(subTitulo.length()-2);
                }
                subTitulo.append(System.getProperty("line.separator"));
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }
        }
        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
        }

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        try {
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            final String reportName = reportController.makeReport(getStrFormato(), parameters, relatorio, getDataSource(criterio, responsavel), responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);
        }catch(ZetraException | IOException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    private JRDataSource getDataSource(TransferObject criterio, AcessoSistema responsavel) throws ZetraException {
        final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
        final List<TransferObject> parcelas = parcelaController.adesParcelasFuturasByPeriodo(criterio, responsavel);
        final List<ParcelasProcessadasFuturasBean> totalParcelas = new ArrayList<>();
        final Map<String, TransferObject> adeCodigoMaxParcela = new HashMap<>();
        List<ParcelasProcessadasFuturasBean> parcelasFuturas = new ArrayList<>();

        //Criação das linhas do relatório cujo as parcelas estão nas tabelas tb_parcela_desconto e tb_parcela_desconto_periodo
        for (final TransferObject parcela : parcelas) {
            final String tipo = Integer.parseInt((String) parcela.getAttribute(Columns.ADE_PRAZO)) == 1 ? "A" : "P";
            final ParcelasProcessadasFuturasBean linhaParcelaRelatorio = new ParcelasProcessadasFuturasBean((Long) parcela.getAttribute(Columns.ADE_NUMERO),
                    (Date) parcela.getAttribute(Columns.ADE_DATA), (String) parcela.getAttribute(Columns.SPD_DESCRICAO),
                    (String) parcela.getAttribute(Columns.USU_NOME), tipo, (Short) parcela.getAttribute(Columns.PRD_NUMERO),
                    Integer.parseInt((String) parcela.getAttribute(Columns.ADE_PRAZO)), (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_PREVISTO),
                    DateHelper.toPeriodString((Date) parcela.getAttribute(Columns.PRD_DATA_DESCONTO)), (String) parcela.getAttribute(Columns.CNV_COD_VERBA));

            totalParcelas.add(linhaParcelaRelatorio);

            //Armazenando as ADE's e sua ultima parcela para em seguida fazer a criação das parcelas futuras a partir da ultima encontrada
            if (!adeCodigoMaxParcela.containsKey(parcela.getAttribute(Columns.ADE_CODIGO)) || (Integer.valueOf((Short) adeCodigoMaxParcela.get(parcela.getAttribute(Columns.ADE_CODIGO)).getAttribute(Columns.PRD_NUMERO)) < Integer.valueOf((Short) parcela.getAttribute(Columns.PRD_NUMERO)))) {
                adeCodigoMaxParcela.put((String) parcela.getAttribute(Columns.ADE_CODIGO), parcela);
            }
        }

        parcelasFuturas = inserirParcelasFuturas(criterio, adeCodigoMaxParcela, PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema()));
        totalParcelas.addAll(parcelasFuturas);

        //Ordena a lista total de parcelas por Verba, Data Inclusão, Ade Numero e Numero Parcela
        totalParcelas.sort(Comparator.comparing(ParcelasProcessadasFuturasBean::getCnvCodVerba).thenComparing(ParcelasProcessadasFuturasBean::getDataInclusao).thenComparing(ParcelasProcessadasFuturasBean::getAdeNumero).thenComparing(ParcelasProcessadasFuturasBean::getNumParcela));

        return new JRBeanCollectionDataSource(totalParcelas);
    }

    private List<ParcelasProcessadasFuturasBean> inserirParcelasFuturas(TransferObject criterio, Map<String, TransferObject> adeCodigoMaxParcelas, boolean mensal) throws ZetraException {
        try {
            final ReimplantarConsignacaoController reimplantarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(ReimplantarConsignacaoController.class);
            final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
            final List<ParcelasProcessadasFuturasBean> retorno = new ArrayList<>();

            //Para cada ADE que teve uma parcela adicionada ao relatório, é necessário verificar se haverá parcelas futuras para o contrato.
            for (final Map.Entry<String, TransferObject> adeCodigoMaxParcela : adeCodigoMaxParcelas.entrySet()) {
                final TransferObject parcela = adeCodigoMaxParcela.getValue();
                final boolean preservaParcela = reimplantarConsignacaoController.sistemaPreservaParcela(adeCodigoMaxParcela.getKey(), responsavel);
                final String sadCodigo = (String) parcela.getAttribute(Columns.ADE_SAD_CODIGO);
                int parcelaNumero = Integer.valueOf((Short) parcela.getAttribute(Columns.PRD_NUMERO)) + 1;
                final int prazo = Integer.parseInt((String) parcela.getAttribute(Columns.ADE_PRAZO));
                final Date adeAnoMesFim = (Date) parcela.getAttribute(Columns.ADE_ANO_MES_FIM);
                Date ultimaParcela = (Date) parcela.getAttribute(Columns.PRD_DATA_DESCONTO);
                final Date filtroDataFim = DateHelper.parse((String) criterio.getAttribute("DATA_FIM"), "yyyy-MM-dd");
                Date dataLimite = null;

                if(preservaParcela) {
                    //verificar se a quantidade de parcelas pagas é igual ao prazo. Se não for, calcular a quantidade de parcelas restantes = PRAZO - PAGAS - QTD em processamento
                    //e exibir parcelas futuras de acordo com a quantidade de parcelas restantes até o período fim de geração do relatório.
                    final int parcelasRestantes = prazo - parcelaNumero - 1;
                    dataLimite = parcelasRestantes <= 0 ? ultimaParcela : mensal ? DateHelper.addMonths(ultimaParcela, parcelasRestantes) : PeriodoHelper.getInstance().adicionarPeriodoQuinzenal(ultimaParcela, parcelasRestantes);
                    dataLimite = dataLimite.after(filtroDataFim) ? filtroDataFim : dataLimite;

                }else {
                    //exibir parcelas futuras de acordo com a quantidade de parcelas restantes do período atual até a data fim de geração do relatório e que estão dentro da data fim do contrato,
                    //desde que a data da ultima parcela não seja superior a data fim do contrato
                    dataLimite = filtroDataFim.before(adeAnoMesFim) ? filtroDataFim : ultimaParcela.after(adeAnoMesFim) ? ultimaParcela : adeAnoMesFim;
                }

                //Caso a ADE já tenha sido liquidada,suspensa ou cancelada antes da data limite não será necessário gerar parcelas posteriores ao periodo da ocorrência.
                if(CodedValues.SAD_CODIGOS_LIQUIDADO_CONCLUIDO.contains(sadCodigo) || CodedValues.SAD_CODIGOS_SUSPENSOS.contains(sadCodigo) || CodedValues.SAD_CANCELADA.equals(parcela.getAttribute(Columns.ADE_SAD_CODIGO))) {
                  //Caso exista uma ocorrência anterior a dataLimite, essa será considerada a dataLimite
                    final String tocCodigo = CodedValues.SAD_CANCELADA.equals(sadCodigo) ? CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA : (CodedValues.SAD_LIQUIDADA.equals(sadCodigo) || CodedValues.SAD_CONCLUIDO.equals(sadCodigo)) ? CodedValues.TOC_TARIF_LIQUIDACAO : CodedValues.TOC_SUSPENSAO_CONTRATO;
                    final Date dataOcorrenciaLiquidacao = parcelaController.dataLimiteOcorrencia(adeCodigoMaxParcela.getKey(), tocCodigo);
                    dataLimite = !TextHelper.isNull(dataOcorrenciaLiquidacao) && (dataLimite.after(dataOcorrenciaLiquidacao) || dataLimite.equals(dataOcorrenciaLiquidacao)) ? mensal ? DateHelper.addMonths(dataOcorrenciaLiquidacao, -1) : PeriodoHelper.getInstance().adicionarPeriodoQuinzenal(dataOcorrenciaLiquidacao, -1): dataLimite;
                    dataLimite = dataLimite.before(ultimaParcela) ? ultimaParcela : dataLimite;
                }

                //Cria as parcelas futuras de acordo com o calculo da data limite
                while (ultimaParcela.before(dataLimite)) {
                    final String tipo = Integer.parseInt((String) parcela.getAttribute(Columns.ADE_PRAZO)) == 1 ? "A" : "P";
                    final String parcelaDesconto = mensal ? DateHelper.toPeriodString(DateHelper.addMonths(ultimaParcela, 1)) : DateHelper.toPeriodString(PeriodoHelper.getInstance().adicionarPeriodoQuinzenal(ultimaParcela, 1));
                    final ParcelasProcessadasFuturasBean registro = new ParcelasProcessadasFuturasBean((Long) parcela.getAttribute(Columns.ADE_NUMERO),
                            (Date) parcela.getAttribute(Columns.ADE_DATA), ApplicationResourcesHelper.getMessage("mensagem.relatorio.parcelas.processadas.futuras", responsavel),
                            (String) parcela.getAttribute(Columns.USU_NOME), tipo, parcelaNumero, Integer.parseInt((String) parcela.getAttribute(Columns.ADE_PRAZO)),
                            (BigDecimal) parcela.getAttribute(Columns.ADE_VLR), parcelaDesconto, (String) parcela.getAttribute(Columns.CNV_COD_VERBA));

                    retorno.add(registro);
                    parcelaNumero += 1;
                    ultimaParcela = mensal ? DateHelper.addMonths(ultimaParcela, 1) : PeriodoHelper.getInstance().adicionarPeriodoQuinzenal(ultimaParcela, 1);
                }
            }

            return retorno;
        } catch (final ParseException ex) {
            LOG.error("Não foi possível inserir parcelas futuras", ex);
            throw new ReportControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private StringBuilder formataSubtitulo(String rseMatricula, String serCpf) {
        final PesquisarServidorController pesquisaServidor = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
        final StringBuilder subTitulo = new StringBuilder();

        try {
            final List<TransferObject> servidor = pesquisaServidor.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, rseMatricula, serCpf, responsavel);
            if (!TextHelper.isNull(servidor) && (servidor.size() > 0)) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular.arg0", responsavel, (String) servidor.get(0).getAttribute(Columns.SER_NOME)));
                if (!TextHelper.isNull(serCpf) && TextHelper.isNull(rseMatricula)) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, (String) servidor.get(0).getAttribute(Columns.RSE_MATRICULA)));
                } else if (TextHelper.isNull(serCpf) && !TextHelper.isNull(rseMatricula)) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, (String) servidor.get(0).getAttribute(Columns.SER_CPF)));
                }
            }
        } catch (final ServidorControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
        return subTitulo;
    }
}