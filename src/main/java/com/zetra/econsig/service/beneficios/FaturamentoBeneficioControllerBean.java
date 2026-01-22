package com.zetra.econsig.service.beneficios;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorListTO;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ImportaNotaFiscalArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.ValidacaoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.entity.ArquivoFaturamentoBen;
import com.zetra.econsig.persistence.entity.ArquivoFaturamentoBeneficioHome;
import com.zetra.econsig.persistence.entity.ArquivoPreviaOperadoraHome;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficio;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioHome;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioNf;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioNfHome;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.persistence.entity.TipoLancamentoHome;
import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioMncResiduoQuery;
import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioPrincipalQuery;
import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioQuery;
import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarFaturamentoBeneficioQuery;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FaturamentoBeneficioController</p>
 * <p>Description: Controller para faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: tadeu.cruz $
 * $Revision: 25571 $
 * $Date: 2018-10-10 13:59:39 -0300 (Qua, 10 out 2018) $
 */
@Service
@Transactional
public class FaturamentoBeneficioControllerBean implements FaturamentoBeneficioController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FaturamentoBeneficioControllerBean.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    private FaturamentoBeneficio findByPrimaryKey(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            return FaturamentoBeneficioHome.findByPrimaryKey(fatCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FaturamentoBeneficioControllerException("mensagem.erro.faturamento.beneficio.nao.encontrado", responsavel);
        }
    }

    @Override
    public List<TransferObject> findFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            ListarFaturamentoBeneficioQuery query = new ListarFaturamentoBeneficioQuery();

            if (criterio != null) {
                Object fatCodigo = criterio.getAttribute(Columns.FAT_CODIGO);
                if (fatCodigo != null) {
                	query.fatCodigo = (String) fatCodigo;
                }

                Object csaCodigo = criterio.getAttribute(Columns.CSA_CODIGO);
                if (csaCodigo != null) {
                	query.csaCodigo = (String) csaCodigo;
                }

                Object fatPeriodo = criterio.getAttribute(Columns.FAT_PERIODO);
                if (fatPeriodo != null) {
                	query.fatPeriodo = (Date) fatPeriodo;
                }
            }

            return query.executarDTO();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> findArquivosFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {

        try {

            ListarArquivoFaturamentoBeneficioQuery query = new ListarArquivoFaturamentoBeneficioQuery();


            Integer afbCodigo = (Integer) criterio.getAttribute(Columns.AFB_CODIGO);
            if (afbCodigo != null) {
            	query.afbCodigo = afbCodigo;
            }

            String fatCodigo = (String) criterio.getAttribute(Columns.FAT_CODIGO);
            if (!TextHelper.isNull(fatCodigo)) {
            	query.fatCodigo = fatCodigo;
            }

            String rseMatricula = (String) criterio.getAttribute(Columns.AFB_RSE_MATRICULA);
            if (!TextHelper.isNull(rseMatricula)) {
            	query.rseMatricula = rseMatricula;
            }

            String cbeNumero = (String) criterio.getAttribute(Columns.CBE_NUMERO);
            if (!TextHelper.isNull(cbeNumero)) {
            	query.cbeNumero = cbeNumero;
            }

            String bfcCpf = (String) criterio.getAttribute(Columns.BFC_CPF);
            if (!TextHelper.isNull(bfcCpf)) {
            	query.bfcCpf = bfcCpf;
            }

            if (criterio.getAtributos().containsKey("OFFSET")) {
            	int offset = (int)criterio.getAttribute("OFFSET");
            	query.firstResult = offset;
            }

            if (criterio.getAtributos().containsKey("SIZE")) {
            	int size = (int) criterio.getAttribute("SIZE");
            	query.maxResults = size;
            }

            return query.executarDTO();

        } catch (Exception e) {
            throw new FaturamentoBeneficioControllerException(e);
        }


    }

    @Override
    public int countArquivosFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {

        try {

            ListarArquivoFaturamentoBeneficioQuery query = new ListarArquivoFaturamentoBeneficioQuery();
            query.contador = true;

            String fatCodigo = (String) criterio.getAttribute(Columns.FAT_CODIGO);
            if (!TextHelper.isNull(fatCodigo)) {
            	query.fatCodigo = fatCodigo;
            }

            String rseMatricula = (String) criterio.getAttribute(Columns.AFB_RSE_MATRICULA);
            if (!TextHelper.isNull(rseMatricula)) {
            	query.rseMatricula = rseMatricula;
            }

            String cbeNumero = (String) criterio.getAttribute(Columns.CBE_NUMERO);
            if (!TextHelper.isNull(cbeNumero)) {
            	query.cbeNumero = cbeNumero;
            }

            String bfcCpf = (String) criterio.getAttribute(Columns.BFC_CPF);
            if (!TextHelper.isNull(bfcCpf)) {
            	query.bfcCpf = bfcCpf;
            }

            return query.executarContador();

        } catch (Exception e) {
            throw new FaturamentoBeneficioControllerException(e);
        }

    }

    @Override
    public TransferObject salvarArquivoFaturamento(TransferObject af, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {

    	try {

    		Integer id = (Integer) af.getAttribute(Columns.AFB_CODIGO);
    		BigDecimal afbValorSubsidio = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_SUBSIDIO);
    		BigDecimal afbValorRealizado = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_REALIZADO);
    		BigDecimal afbValorNaoRealizado = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_NAO_REALIZADO);
    		BigDecimal afbValorTotal = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_TOTAL);


    		ArquivoFaturamentoBen bean = new ArquivoFaturamentoBen();
    		if (id != null) {
    			bean = ArquivoFaturamentoBeneficioHome.findByPrimaryKey(id);
    		}

			bean.setAfbValorSubsidio(afbValorSubsidio);
			bean.setAfbValorRealizado(afbValorRealizado);
			bean.setAfbValorNaoRealizado(afbValorNaoRealizado);
			bean.setAfbValorTotal(afbValorTotal);

    		bean = ArquivoFaturamentoBeneficioHome.save(bean);

    		Beneficio beneficio = bean.getAutDesconto().getContratoBeneficio().getBeneficio();

    		af = new CustomTransferObject();

    		af.setAttribute(Columns.AFB_CODIGO, bean.getAfbCodigo());
    		af.setAttribute(Columns.AFB_RSE_MATRICULA, bean.getRseMatricula());
    		af.setAttribute(Columns.FAT_PERIODO, bean.getFaturamentoBeneficio().getFatPeriodo());
    		af.setAttribute(Columns.CBE_NUMERO, bean.getCbeNumero());
    		af.setAttribute(Columns.BFC_CPF, bean.getBfcCpf());
    		af.setAttribute(Columns.TLA_CODIGO, bean.getTipoLancamento().getTlaCodigo());
    		af.setAttribute(Columns.TLA_DESCRICAO, bean.getTipoLancamento().getTlaDescricao());
			af.setAttribute(Columns.BEN_DESCRICAO, beneficio.getBenDescricao());
			af.setAttribute(Columns.CSA_NOME, beneficio.getConsignataria().getCsaNome());
    		af.setAttribute(Columns.AFB_VALOR_SUBSIDIO, bean.getAfbValorSubsidio());
    		af.setAttribute(Columns.AFB_VALOR_REALIZADO, bean.getAfbValorRealizado());
    		af.setAttribute(Columns.AFB_VALOR_NAO_REALIZADO, bean.getAfbValorNaoRealizado());
    		af.setAttribute(Columns.AFB_VALOR_TOTAL, bean.getAfbValorTotal());

    		return af;

    	} catch (Exception e) {
    		throw new FaturamentoBeneficioControllerException(e);
    	}

    }

    @Override
    public void excluirArquivoFaturamento(Integer afbCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
    	try {
    		ArquivoFaturamentoBen obj = new ArquivoFaturamentoBen();
    		obj.setAfbCodigo(afbCodigo);
    		ArquivoFaturamentoBeneficioHome.remove(obj);
    	} catch (Exception e) {
    		throw new FaturamentoBeneficioControllerException(e);
    	}
    }

    @Override
    public List<TipoLancamento> listarTipoLancamento(AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
    	try {
    		return TipoLancamentoHome.listar();
    	} catch (Exception e) {
    		throw new FaturamentoBeneficioControllerException(e);
    	}
    }

    @Override
    public String gerarArquivoFaturamentoPrincipal(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            if (TextHelper.isNull(fatCodigo)) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.faturamento.beneficio.nao.encontrado", responsavel);
            }

            FaturamentoBeneficio faturamento = findByPrimaryKey(fatCodigo, responsavel);
            String csaCodigo = faturamento.getConsignataria().getCsaCodigo();
            Date fatPeriodo = faturamento.getFatPeriodo();

            ConsignanteTransferObject consignante = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);

            String cseNome = consignante.getCseNome();
            String csaIdentificador = consignataria.getCsaIdentificador();
            String agora = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
            String strPeriodo = DateHelper.format(fatPeriodo, "yyyyMMdd");

            List<String> arquivos = new ArrayList<>();
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathConf = absolutePath + File.separatorChar + "conf" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar;
            String pathFile = absolutePath + File.separatorChar + "beneficio" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + fatCodigo + File.separatorChar;

            String tradutorArqConf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_ARQ_FATURAMENTO_BENEFICIO, responsavel);
            String saidaArqConf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_ARQ_FATURAMENTO_BENEFICIO, responsavel);

            String tradutorResiduosArqConf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_ARQ_RESIDUO_FAT_BENEFICIO, responsavel);
            String saidaResiduosArqConf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_ARQ_RESIDUO_FAT_BENEFICIO, responsavel);

            if (TextHelper.isNull(tradutorArqConf) || TextHelper.isNull(saidaArqConf) ||
                TextHelper.isNull(tradutorResiduosArqConf) || TextHelper.isNull(saidaResiduosArqConf)) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.arquivos.configuracao.faturamento.beneficio.ausentes", responsavel);
            }

            File file = new File(pathFile);
            if (!file.exists()) {
                file.mkdirs();
            }

            // Seta diretório padrão arquivos XML
            tradutorArqConf = pathConf + tradutorArqConf;
            saidaArqConf = pathConf + saidaArqConf;
            tradutorResiduosArqConf = pathConf + tradutorResiduosArqConf;
            saidaResiduosArqConf = pathConf + saidaResiduosArqConf;

            // Verifica se arquivos de configuração existem
            File arqConfTradutor = new File(tradutorArqConf);
            File arqConfSaida = new File(saidaArqConf);
            File arqConfResiduoTradutor = new File(tradutorResiduosArqConf);
            File arqConfResiduoSaida = new File(saidaResiduosArqConf);

            if (!arqConfTradutor.exists() || !arqConfSaida.exists() ||
                !arqConfResiduoTradutor.exists() || !arqConfResiduoSaida.exists()) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.arquivos.configuracao.faturamento.beneficio.ausentes", responsavel);
            }

            Leitor leitor = null;
            Escritor escritor = null;
            Tradutor tradutor = null;

            // Recupera dados arquivo faturamento relatório principal
            LOG.debug("INICIO GERANDO RELATORIO DE FATURAMENTO PRINCIPAL: " + DateHelper.getSystemDatetime());
            ListarArquivoFaturamentoBeneficioPrincipalQuery queryPrincipal = new ListarArquivoFaturamentoBeneficioPrincipalQuery();
            queryPrincipal.fatCodigo = fatCodigo;
            List<TransferObject> principal = queryPrincipal.executarDTO();

            if (principal != null && !principal.isEmpty()) {
                LOG.debug("QUANTIDADE DE REGISTROS:" + principal.size());
                String arqPrincipal = pathFile + csaIdentificador + "_" + strPeriodo + "_" + agora + ".txt";

                // Gerar arquivo de faturamento principal
                leitor = new LeitorListTO(principal);
                escritor = new EscritorArquivoTexto(saidaArqConf, arqPrincipal);
                tradutor = new Tradutor(tradutorArqConf, leitor, escritor);
                tradutor.traduz();
                arquivos.add(arqPrincipal);
            }
            LOG.debug("FIM GERANDO RELATORIO DE FATURAMENTO PRINCIPAL: " + DateHelper.getSystemDatetime());

            BigDecimal vlrMinEnvioBoleto = BigDecimal.ZERO;
            try {
                String strVlrMinEnvioBoleto = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VLR_MIN_ENVIO_BOLETO_FATURAMENTO_BENEFICIO, responsavel);
                if (!TextHelper.isNull(strVlrMinEnvioBoleto)) {
                    vlrMinEnvioBoleto = new BigDecimal(strVlrMinEnvioBoleto);
                }
            } catch (ParametroControllerException e) {
                LOG.error("Não foi possível recuperar o valor mínimo para gerar relatório de margem não consignável.", e);
            }

            // Recupera dados arquivo faturamento relatório margem não consignável e resíduos
            if (vlrMinEnvioBoleto.compareTo(BigDecimal.ZERO) > 0) {
                LOG.debug("INICIO GERANDO RELATORIO DE FATURAMENTO MMC/RESIDUO: " + DateHelper.getSystemDatetime());
                ListarArquivoFaturamentoBeneficioMncResiduoQuery queryMncResiduos = new ListarArquivoFaturamentoBeneficioMncResiduoQuery();
                queryMncResiduos.fatCodigo = fatCodigo;
                queryMncResiduos.pcsVlr = vlrMinEnvioBoleto;
                List<TransferObject> mnc = queryMncResiduos.executarDTO();

                if (mnc != null && !mnc.isEmpty()) {
                    LOG.debug("QUANTIDADE DE REGISTROS:" + mnc.size());
                    List<TransferObject> residuos = new ArrayList<>();
                    residuos.addAll(mnc);

                    String arqMNC = pathFile + "MNC_" + csaIdentificador + "_" + strPeriodo + "_" + agora + ".txt";

                    // Gerar arquivo de margem não consignável
                    leitor = new LeitorListTO(mnc);
                    escritor = new EscritorArquivoTexto(saidaArqConf, arqMNC);
                    tradutor = new Tradutor(tradutorArqConf, leitor, escritor);
                    tradutor.traduz();
                    arquivos.add(arqMNC);

                    String arqResiduos = pathFile + "RESIDUO_" + csaIdentificador + "_" + strPeriodo + "_" + agora + ".txt";

                    // Gerar arquivo de resíduos
                    leitor = new LeitorListTO(residuos);
                    escritor = new EscritorArquivoTexto(saidaResiduosArqConf, arqResiduos);
                    tradutor = new Tradutor(tradutorResiduosArqConf, leitor, escritor);
                    tradutor.traduz();
                    arquivos.add(arqResiduos);
                }
                LOG.debug("FIM GERANDO RELATORIO DE FATURAMENTO MMC/RESIDUO: " + DateHelper.getSystemDatetime());
            }

            // Recupera dados arquivo faturamento relatório de créditos
            LOG.debug("INICIO GERANDO RELATORIO DE FATURAMENTO CREDITO: " + DateHelper.getSystemDatetime());
            ListarArquivoFaturamentoBeneficioPrincipalQuery queryCredito = new ListarArquivoFaturamentoBeneficioPrincipalQuery();
            queryCredito.fatCodigo = fatCodigo;
            queryCredito.creditos = true;
            List<TransferObject> creditos = queryCredito.executarDTO();

            if (creditos != null && !creditos.isEmpty()) {
                LOG.debug("QUANTIDADE DE REGISTROS:" + creditos.size());
                String arqCreditos = pathFile + "CREDITO_" + csaIdentificador + "_" + strPeriodo + "_" + agora + ".txt";

                // Gerar arquivo de faturamento de créditos
                leitor = new LeitorListTO(creditos);
                escritor = new EscritorArquivoTexto(saidaArqConf, arqCreditos);
                tradutor = new Tradutor(tradutorArqConf, leitor, escritor);
                tradutor.traduz();
                arquivos.add(arqCreditos);
            }
            LOG.debug("FIM GERANDO RELATORIO DE FATURAMENTO CREDITO: " + DateHelper.getSystemDatetime());

            // Compactar arquivos
            String nomeArqSaidaZip = pathFile + cseNome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "_" + strPeriodo + "_" + agora + ".zip";
            try {
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("COMPACTA ARQUIVOS FATURAMENTO: " + DateHelper.getSystemDatetime());
                FileHelper.zip(arquivos, nomeArqSaidaZip);
                LOG.debug("FIM - COMPACTA ARQUIVOS FATURAMENTO: " + DateHelper.getSystemDatetime());

                for (String arquivo : arquivos) {
                    FileHelper.delete(arquivo);
                }
            } catch (IOException ex) {
                LOG.error("Não foi possível compactar os relatórios gerados.", ex);
            }

            if(ParamSist.getBoolParamSist(CodedValues.TPC_GERAR_DADOS_NOTAS_FISCAIS_PERIODO_FATURAMENTO, responsavel)) {

                LOG.debug("INICIO GERANDO NOTAS FISCAIS DE FATURAMENTO CREDITO: " + DateHelper.getSystemDatetime());
                gerarNotasFiscaisFaturamento(fatCodigo, responsavel);
                LOG.debug("FIM GERANDO NOTAS FISCAIS DE FATURAMENTO CREDITO: " + DateHelper.getSystemDatetime());

            }

            return nomeArqSaidaZip;

        } catch (ConsignanteControllerException | ConsignatariaControllerException | HQueryException | ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public String validarPreviaFaturamento(String fatCodigo, List<String> arquivosPrevia, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            if (TextHelper.isNull(fatCodigo)) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.faturamento.beneficio.nao.encontrado", responsavel);
            }

            DAOFactory daoFactory = DAOFactory.getDAOFactory();
            ValidacaoFaturamentoBeneficioDAO validarFatBenDAO = daoFactory.getValidacaoFaturamentoBeneficioDAO();

            FaturamentoBeneficio faturamento = findByPrimaryKey(fatCodigo, responsavel);
            String csaCodigo = faturamento.getConsignataria().getCsaCodigo();
            Date fatPeriodo = faturamento.getFatPeriodo();

            ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);

            String csaIdentificador = consignataria.getCsaIdentificador();
            String agora = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
            String strPeriodo = DateHelper.format(fatPeriodo, "yyyyMMdd");

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathConf = absolutePath + File.separatorChar + "conf" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar;
            String pathFile = absolutePath + File.separatorChar + "beneficio" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + fatCodigo + File.separatorChar;

            String tradutorArqConf = "exp_arq_previa_faturamento_beneficio_tradutor.xml";
            String saidaArqConf = "exp_arq_previa_faturamento_beneficio_saida.xml";

            if (TextHelper.isNull(tradutorArqConf) || TextHelper.isNull(saidaArqConf)) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.arquivos.configuracao.faturamento.beneficio.ausentes", responsavel);
            }

            File file = new File(pathFile);
            if (!file.exists()) {
                file.mkdirs();
            }

            // Seta diretório padrão arquivos XML
            tradutorArqConf = pathConf + tradutorArqConf;
            saidaArqConf = pathConf + saidaArqConf;

            // Verifica se arquivos de configuração existem
            File arqConfTradutor = new File(tradutorArqConf);
            File arqConfSaida = new File(saidaArqConf);

            if (!arqConfTradutor.exists() || !arqConfSaida.exists()) {
                throw new FaturamentoBeneficioControllerException("mensagem.erro.arquivos.configuracao.faturamento.beneficio.ausentes", responsavel);
            }

            // Verifica se a validação deve utilizar arquivos de prévia de faturamento
            boolean validarPrevia = (arquivosPrevia != null && !arquivosPrevia.isEmpty());

            // Caso possua prévia, limpar a tabela de prévia
            if (validarPrevia) {
                LOG.debug("INICIO LIMPA PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
                validarFatBenDAO.apagarPreviaFaturamentoOperadora(fatCodigo);
                LOG.debug("FIM LIMPA PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
            }

            // Caso possua prévia, carrega arquivos de prévia para a tabela
            if (validarPrevia) {
                LOG.debug("INICIO CARREGA PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
                // Recupera os arquivos de configuração para importação de prévia de faturamento de benefícios
                String nomeArqXmlEntrada = pathConf + "imp_arq_previa_faturamento_beneficio_entrada.xml";
                String nomeArqXmlTradutor = pathConf + "imp_arq_previa_faturamento_beneficio_tradutor.xml";

                // Path de prévia
                String pathPrevia = absolutePath + File.separatorChar + "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;

                Leitor l = null;
                Escritor e = null;
                Tradutor t = null;

                BatchManager batchManager = new BatchManager(SessionUtil.getSession());
                for (String apoNomeArquivo : arquivosPrevia) {
                    // Localiza arquivos no path de prévia
                    String arquivoPrevia = pathPrevia + File.separatorChar + apoNomeArquivo;
                    File arq = new File(arquivoPrevia);
                    if (arq.exists()) {
                        // Executa importação do arquivo para a tabela de arquivos de prévia de faturamento
                        l = new LeitorArquivoTexto(nomeArqXmlEntrada, arquivoPrevia);
                        e = new EscritorMemoria(new HashMap<>());
                        t = new Tradutor(nomeArqXmlTradutor, l, e);

                        t.iniciaTraducao(true);
                        int qtdeLinhas = 0;

                        while (t.traduzProximo()) {
                            Map<String, Object> valoresMap = t.getDados();

                            if (valoresMap != null) {
                                String apoOperacao = (String) valoresMap.get("APO_OPERACAO"); // !TextHelper.isNull(valoresMap.get("APO_OPERACAO")) ? valoresMap.get("APO_OPERACAO").charAt(0) : null;
                                Date apoPeriodoFaturamento = !TextHelper.isNull(valoresMap.get("APO_PERIODO_FATURAMENTO")) ? DateHelper.parse((String) valoresMap.get("APO_PERIODO_FATURAMENTO"), "yyyy-MM-dd") : null;
                                Date apoDataInclusao = !TextHelper.isNull(valoresMap.get("APO_DATA_INCLUSAO")) ? DateHelper.parse((String) valoresMap.get("APO_DATA_INCLUSAO"), "yyyy-MM-dd") : null;
                                Date apoDataExclusao = !TextHelper.isNull(valoresMap.get("APO_DATA_EXCLUSAO")) ? DateHelper.parse((String) valoresMap.get("APO_DATA_EXCLUSAO"), "yyyy-MM-dd") : null;
                                String cbeNumero = (String) valoresMap.get("CBE_NUMERO");
                                String benCodigoRegistro = (String) valoresMap.get("BEN_CODIGO_REGISTRO");
                                String rseMatricula = (String) valoresMap.get("RSE_MATRICULA");
                                String benCodigoContrato = (String) valoresMap.get("BEN_CODIGO_CONTRATO");
                                BigDecimal apoValorDebito = !TextHelper.isNull(valoresMap.get("APO_VALOR_DEBITO")) ? new BigDecimal((String) valoresMap.get("APO_VALOR_DEBITO")) : null;
                                String apoTipoLancamento = (String) valoresMap.get("APO_TIPO_LANCAMENTO");
                                String apoReajusteFaixaEtaria = (String) valoresMap.get("APO_REAJUSTE_FAIXA_ETARIA"); // !TextHelper.isNull(valoresMap.get("APO_REAJUSTE_FAIXA_ETARIA")) ? valoresMap.get("APO_REAJUSTE_FAIXA_ETARIA").charAt(0) : null;
                                String apoReajusteAnual = (String) valoresMap.get("APO_REAJUSTE_ANUAL"); // !TextHelper.isNull(valoresMap.get("APO_REAJUSTE_ANUAL")) ? valoresMap.get("APO_REAJUSTE_ANUAL").charAt(0) : null;
                                String apoNumeroLote = (String) valoresMap.get("APO_NUMERO_LOTE");
                                String apoItemLote = (String) valoresMap.get("APO_ITEM_LOTE");
                                BigDecimal apoValorSubsidio = !TextHelper.isNull(valoresMap.get("APO_VALOR_SUBSIDIO")) ? new BigDecimal((String) valoresMap.get("APO_VALOR_SUBSIDIO")) : null;
                                BigDecimal apoValorRealizado = !TextHelper.isNull(valoresMap.get("APO_VALOR_REALIZADO")) ? new BigDecimal((String) valoresMap.get("APO_VALOR_REALIZADO")) : null;
                                BigDecimal apoValorNaoRealizado = !TextHelper.isNull(valoresMap.get("APO_VALOR_NAO_REALIZADO")) ? new BigDecimal((String) valoresMap.get("APO_VALOR_NAO_REALIZADO")) : null;
                                BigDecimal apoValorTotal = !TextHelper.isNull(valoresMap.get("APO_VALOR_TOTAL")) ? new BigDecimal((String) valoresMap.get("APO_VALOR_TOTAL")) : null;
                                Date apoPeriodoCobranca = !TextHelper.isNull(valoresMap.get("APO_PERIODO_COBRANCA")) ? DateHelper.parse((String) valoresMap.get("APO_PERIODO_COBRANCA"), "yyyy-MM-dd") : null;

                                ArquivoPreviaOperadoraHome.create(csaCodigo, apoNomeArquivo, apoOperacao, apoPeriodoFaturamento,
                                        apoDataInclusao, apoDataExclusao, cbeNumero, benCodigoRegistro, rseMatricula, benCodigoContrato,
                                        apoValorDebito, apoTipoLancamento, apoReajusteFaixaEtaria, apoReajusteAnual, apoNumeroLote,
                                        apoItemLote, apoValorSubsidio, apoValorRealizado, apoValorNaoRealizado, apoValorTotal,
                                        apoPeriodoCobranca);
                            }

                            if (++qtdeLinhas % 1000 == 0) {
                                LOG.debug("LINHAS LIDAS = " + qtdeLinhas);
                            }

                            batchManager.iterate();
                        }
                        LOG.debug("TOTAL DE LINHAS LIDAS = " + qtdeLinhas);

                        // Encerra tradução
                        try {
                            t.encerraTraducao();
                        } catch (ParserException pe) {
                            LOG.error(pe.getMessage(), pe);
                        }
                    }
                }
                LOG.debug("FIM CARREGA PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
            }

            // Caso possua prévia, copia os campos de lote para a tabela de prévia
            if (validarPrevia) {
                LOG.debug("INICIO COPIAR VALORES LOTE VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
                validarFatBenDAO.copiarCamposLoteFaturamentoBeneficio(fatCodigo);
                LOG.debug("FIM COPIAR VALORES LOTE VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
            }

            // Validar prévia do faturamento
            LOG.debug("INICIO CRITICA VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
            List<TransferObject> principal = validarFatBenDAO.validarFaturamentoBeneficio(fatCodigo, validarPrevia);
            String arqPrincipal = "";

            if (principal != null && !principal.isEmpty()) {
                LOG.debug("QUANTIDADE DE REGISTROS:" + principal.size());
                arqPrincipal = pathFile + "critica_" + csaIdentificador + "_" + strPeriodo + "_" + agora + ".txt";

                LOG.debug("INICIO GERA ARQUIVO DE VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
                // Gerar arquivo de faturamento principal
                Leitor leitor = new LeitorListTO(principal);
                Escritor escritor = new EscritorArquivoTexto(saidaArqConf, arqPrincipal);
                Tradutor tradutor = new Tradutor(tradutorArqConf, leitor, escritor);
                tradutor.traduz();
                LOG.debug("FIM GERA ARQUIVO DE VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());
            }
            LOG.debug("FIM CRITICA VALIDAÇÃO DE PRÉVIA DE FATURAMENTO: " + DateHelper.getSystemDatetime());

            return arqPrincipal;

        } catch (CreateException | DAOException | ConsignatariaControllerException | ParserException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public List<FaturamentoBeneficioNf> listarFaturamentoBeneficioNfPorIdFaturamentoBeneficio(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            return FaturamentoBeneficioNfHome.findByFatCodigo(fatCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public FaturamentoBeneficioNf salvarFaturamentoBeneficioNf(FaturamentoBeneficioNf faturamentoBeneficioNf, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            return FaturamentoBeneficioNfHome.save(faturamentoBeneficioNf);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public FaturamentoBeneficioNf findFaturamentoBeneficioNf(String fnfCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            return FaturamentoBeneficioNfHome.findByPrimaryKey(fnfCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    @Override
    public FaturamentoBeneficioNf excluirFaturamentoBeneficioNf(String fnfCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        try {
            FaturamentoBeneficioNf fnf = FaturamentoBeneficioNfHome.findByPrimaryKey(fnfCodigo);
            FaturamentoBeneficioNfHome.remove(fnf);
            return fnf;
        } catch (RemoveException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaturamentoBeneficioControllerException(ex);
        }
    }

    private void gerarNotasFiscaisFaturamento(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {

        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        ImportaNotaFiscalArquivoFaturamentoBeneficioDAO notaFiscalArquivoDAO = daoFactory.getImportaNotaFiscalArquivoFaturamentoBeneficioDAO();

        notaFiscalArquivoDAO.deletaTabelasTemporarias();
        notaFiscalArquivoDAO.deletarNotasFiscaisAnteriores(fatCodigo);

        notaFiscalArquivoDAO.criarTabelaTemporariaLancamentosCredito(fatCodigo);
        notaFiscalArquivoDAO.criarTabelaTemporariaLancamentosDebito(fatCodigo);
        notaFiscalArquivoDAO.gerarNotasFiscaisArquivoFaturamentoBeneficioSubsidio(fatCodigo);
        notaFiscalArquivoDAO.gerarNotasFiscaisArquivoFaturamentoBeneficioMcMnc(fatCodigo);
        notaFiscalArquivoDAO.gerarNotasFiscaisArquivoFaturamentoBeneficioCopart(fatCodigo);

    }
}
