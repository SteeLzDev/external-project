package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.COR_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleTokenAcesso;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: ReservarMargemCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de reservar margem</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReservarMargemCommand extends SolicitarReservaCommand {

    public ReservarMargemCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaVerbaPorNaturezaServico(parametros);
        validaCnvCodigoSvcCodigo(parametros);
        validaCodigoVerba(parametros);
        validaDataNascimento(parametros);
        validaIdCorrespondente(parametros);
        validaValorAutorizacao(parametros);
        validaAdePrazo(parametros);
        validaInfoBancaria(parametros);
    }

    @Override
    protected void realizaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final Object adeVlr = parametros.get(ADE_VLR);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String corCodigo = (String) parametros.get(COR_CODIGO);
        Object adePrazo = parametros.get(ADE_PRAZO);
        final Object adeCarencia = parametros.get(ADE_CARENCIA);
        final String cnvCodigo = (String) parametros.get(CNV_CODIGO);
        final Object adeIndice = parametros.get(ADE_INDICE);

        final Object vlrTacAux = parametros.get(ADE_VLR_TAC);
        final BigDecimal adeVlrTac = (vlrTacAux instanceof final Double vlrTacAuxDouble) ? ((vlrTacAuxDouble.equals(Double.NaN)) ? null : BigDecimal.valueOf(vlrTacAuxDouble)) :(BigDecimal) vlrTacAux;

        final Object vlrIofAux = parametros.get(ADE_VLR_IOF);
        final BigDecimal adeVlrIof = (vlrIofAux instanceof final Double vlrIofAuxDouble) ? ((vlrIofAuxDouble.equals(Double.NaN)) ? null : BigDecimal.valueOf(vlrIofAuxDouble)) : (BigDecimal) vlrIofAux;

        final Object vlrLiquidoAux = (parametros.get(ADE_VLR_LIQUIDO) != null) ? parametros.get(ADE_VLR_LIQUIDO):parametros.get(VALOR_LIBERADO);
        final BigDecimal adeVlrLiquido = (vlrLiquidoAux instanceof final Double vlrLiquidoAuxDouble) ? ((vlrLiquidoAuxDouble.equals(Double.NaN)) ? null : BigDecimal.valueOf(vlrLiquidoAuxDouble)) : (BigDecimal) vlrLiquidoAux;

        final Object vlrMensVincAux = parametros.get(ADE_VLR_MENS_VINC);
        final BigDecimal adeVlrMensVinc = (vlrMensVincAux instanceof final Double vlrMensVincAuxDouble) ? ((vlrMensVincAuxDouble.equals(Double.NaN)) ? null : BigDecimal.valueOf(vlrMensVincAuxDouble)) : (BigDecimal) vlrMensVincAux;

        final Object taxaJurosAux = parametros.get(ADE_TAXA_JUROS);
        final BigDecimal adeTaxaJuros = (taxaJurosAux instanceof final Double taxaJurosAuxDouble) ? ((taxaJurosAuxDouble.equals(Double.NaN)) ? null : BigDecimal.valueOf(taxaJurosAuxDouble)) : (BigDecimal) taxaJurosAux;

        final String token = (String) parametros.get(TOKEN);

        // Informações bancárias
        final Object numBanco = parametros.get(RSE_BANCO);
        final Object numAgencia = parametros.get(RSE_AGENCIA);
        final Object numConta = parametros.get(RSE_CONTA);

        final ServidorDelegate servidorDelegate = new ServidorDelegate();
        final CustomTransferObject servidor = servidorDelegate.buscaServidor(rseCodigo, responsavel);

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel)) {
            final String svcCodigo = (String) parametros.get(SVC_CODIGO);
            final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            if (!TextHelper.isNull(paramSvcTo.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcTo.getTpsPercentualBaseCalcDescontoEmFila())) {
                // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                // define a não incidência de margem, não integração com a folha, prazo fixo igual a 1 e status Aguard. Margem
                sadCodigo = CodedValues.SAD_AGUARD_MARGEM;
                adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;
                adeIntFolha = CodedValues.INTEGRA_FOLHA_NAO;
                adePrazo = 1;
            }
        }

        // Monta o objeto de parâmetro da reserva
        final ReservarMargemParametros reservaParam = new ReservarMargemParametros();
        reservaParam.setRseCodigo(rseCodigo);
        reservaParam.setAdeVlr((BigDecimal) adeVlr);
        reservaParam.setCorCodigo(corCodigo);
        reservaParam.setAdePrazo((Integer) adePrazo);
        reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa((Integer) adeCarencia, csaCodigo, (String) servidor.getAttribute(Columns.ORG_CODIGO), responsavel));
        reservaParam.setAdeIdentificador(adeIdentificador);
        reservaParam.setCnvCodigo(cnvCodigo);
        reservaParam.setSadCodigo(sadCodigo);
        reservaParam.setAdeTipoVlr(adeTipoVlr);
        reservaParam.setAdeIntFolha(adeIntFolha);
        reservaParam.setAdeIncMargem(adeIncMargem);
        reservaParam.setAdeIndice((String) adeIndice);
        reservaParam.setAdeVlrTac(adeVlrTac);
        reservaParam.setAdeVlrIof(adeVlrIof);
        reservaParam.setAdeVlrLiquido(adeVlrLiquido);
        reservaParam.setAdeVlrMensVinc(adeVlrMensVinc);
        reservaParam.setSerAtivo(!permiteIncluirAdeServidorExcluido(csaCodigo));
        reservaParam.setCnvAtivo(Boolean.TRUE);
        reservaParam.setSerCnvAtivo(Boolean.TRUE);
        reservaParam.setSvcAtivo(Boolean.TRUE);
        reservaParam.setCsaAtivo(Boolean.TRUE);
        reservaParam.setOrgAtivo(Boolean.TRUE);
        reservaParam.setEstAtivo(Boolean.TRUE);
        reservaParam.setCseAtivo(Boolean.TRUE);
        reservaParam.setValidar(Boolean.FALSE);
        reservaParam.setAcao("RESERVAR");
        reservaParam.setAdeTaxaJuros(adeTaxaJuros);
        reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
        reservaParam.setCftCodigo(cftCodigo);
        reservaParam.setCdeVlrLiberado(vlrLiberado);
        reservaParam.setCdeRanking(ranking);
        reservaParam.setCdeTxtContato("");
        reservaParam.setAdeBanco((String) numBanco);
        reservaParam.setAdeAgencia((String) numAgencia);
        reservaParam.setAdeConta((String) numConta);
        reservaParam.setSerSenha(serSenha);
        reservaParam.setComSerSenha(comSerSenha);
        reservaParam.setValidaAnexo(false);

        determinaPeriodo(parametros, reservaParam);

        registraAnexos(parametros, reservaParam);

        // Faz a reserva de margem
        final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
        adeCodigo = consigDelegate.reservarMargem(reservaParam, responsavel);

        // Se reservou margem o token não pode ser usado novamente.
        ControleTokenAcesso.getInstance().invalidarToken(token);
    }

	private void registraAnexos(Map<CamposAPI, Object> parametros, ReservarMargemParametros reservaParam)
			throws ZetraException {
		if (parametros.containsKey(ANEXO)){
            // Anexa arquivo
		    final Anexo anexo = getAnexo(parametros.get(ANEXO));
            if (anexo != null) {
                try {
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    if (TextHelper.isNull(diretorioRaizArquivos)) {
                        throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
                    }
                    final String idAnexo = Integer.toString(new SecureRandom().nextInt(99999999));
                    final String path = diretorioRaizArquivos + File.separator + "temp" + File.separator + "upload" +
                            File.separatorChar + "anexo" + File.separatorChar + idAnexo;
                    final File file = salvarAnexo(path, anexo, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO);
                    if ((file != null) && file.exists()) {
                    	reservaParam.setAnexo(file);
                    	reservaParam.setIdAnexo(idAnexo);
                    } else {
                    	throw new ZetraException("mensagem.erroInternoSistema", responsavel);
                    }
                } catch (final IOException e) {
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel);
                }
            }
        }
	}

	private void determinaPeriodo(Map<CamposAPI, Object> parametros, ReservarMargemParametros reservaParam)
			throws ZetraException {
		if ((parametros.containsKey(PERIODO)) && ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
        		ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
        		ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel))) {
        	final String strOcaPeriodo = ((String) parametros.get(PERIODO));
        	if (!TextHelper.isNull(strOcaPeriodo)) {
        		try {
        			if (strOcaPeriodo.matches("([0-9]{2})/([0-9]{4})")) {
        				final Date periodo = DateHelper.parsePeriodString(strOcaPeriodo);
        				final String ocaPeriodo = DateHelper.format(periodo, LocaleHelper.FORMATO_DATA_INGLES);

        				reservaParam.setOcaPeriodo(ocaPeriodo);
        			} else {
        				throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
        			}
        		} catch (final ParseException e) {
        			throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
        		}
        	}
        }
	}

    @Override
    protected void preProcessaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessaReserva(parametros);

        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final String svcCodigo = (String) parametros.get(SVC_CODIGO);
        final BigDecimal adeVlr = (BigDecimal) parametros.get(ADE_VLR);

        final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

        // Valor para inclusão além da margem
        final BigDecimal vlrLimiteSemMargem = (!TextHelper.isNull(paramSvcTo.getTpsVlrLimiteAdeSemMargem()) ? new BigDecimal(paramSvcTo.getTpsVlrLimiteAdeSemMargem()) : new BigDecimal("0.00"));

        // Serviço que não incidem em margem não disparam a validação abaixo
        boolean validarMargemReserva = (paramSvcTo.getTpsIncideMargem().shortValue() != CodedValues.INCIDE_MARGEM_NAO.shortValue());

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel) && (!TextHelper.isNull(paramSvcTo.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcTo.getTpsPercentualBaseCalcDescontoEmFila()))) {
            // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
            // então não realiza validação de margem na reserva, pois esta não irá incidir na margem
            validarMargemReserva = false;
        }

        // Se permite incluir contrato para servidor excluído, então não valida margem
        // pois o servidor excluído não terá margem válida
        if (permiteIncluirAdeServidorExcluido(csaCodigo)) {
            validarMargemReserva = false;
        }

        // Se o valor enviado é negativo e permite inclusão de valores negativos,
        // não há necessidade de validar margem pois esta será incrementada
        if ((adeVlr.signum() <= 0) && parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
            validarMargemReserva = false;
        }

        if (validarMargemReserva) {
            /*
            Consulta a Margem do Servidor. A reserva em si já valida a parcela em comparação
            com a margem restante do servidor. Mas no caso em que a parcela for maior que a
            margem, esta consulta prévia elimina todo o restante do processo de reserva,
            aumentando a performance da operação. No caso positivo, aquele que a parcela é
            menor que a margem, o acréscimo do tempo de resposta é muito menor do que o ganho
            no caso negativo.
            */
            final BigDecimal valorAValidar = adeVlr.subtract(vlrLimiteSemMargem);
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            if (!consultarMargemController.servidorTemMargem(rseCodigo, valorAValidar, svcCodigo, true, responsavel)) {
                throw new ZetraException("mensagem.margemInsuficiente", responsavel);
            }
        }
    }

    private void validaIdCorrespondente(Map<CamposAPI, Object> parametros)  throws ZetraException {
        final Object corIdentificador = parametros.get(COR_IDENTIFICADOR);
        String corCodigo = (String) parametros.get(COR_CODIGO);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);

        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        //      Valida o id do correspondente
        if ((corCodigo == null) && (corIdentificador != null) && !"".equals(corIdentificador)) {
            try {
                final CorrespondenteTransferObject cor = csaDelegate.findCorrespondenteByIdn(corIdentificador.toString(), csaCodigo, responsavel);
                corCodigo = cor.getCorCodigo();
                parametros.put(COR_CODIGO, corCodigo);
            } catch (final ConsignatariaControllerException ex) {
                throw new ZetraException(ex);
            }
        }
    }
}
