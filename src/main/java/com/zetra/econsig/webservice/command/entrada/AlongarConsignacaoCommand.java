package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.COR_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlongarConsignacaoParametros;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AlongarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de alongar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlongarConsignacaoCommand extends RequisicaoExternaCommand {

    private ParamSvcTO paramSvcCse;
    private TransferObject autorizacao;
    private TransferObject convenio;
    protected String cft_codigo;
    protected Short ranking;
    protected boolean comSerSenha;
    protected String serSenha;

    public AlongarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if (!ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
            throw new ZetraException("mensagem.sistemaNaoPermiteAlongamento", responsavel);
        }
        validaVerbaPorNaturezaServico(parametros);
        validaCadastroIndice(parametros);
        validaCnvCodigoSvcCodigo(parametros);
        validaCodigoVerba(parametros);

        String cnvCodigoAlongamento = (String) parametros.get(CNV_CODIGO);
        if (TextHelper.isNull(cnvCodigoAlongamento)) {
            throw new ZetraException("mensagem.semRelAlongamento", responsavel);
        }

        // Busca os parâmetros do convênio
        ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        convenio = cnvDelegate.getParamCnv(cnvCodigoAlongamento, responsavel);

        validaDataNascimento(parametros);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);
        List<TransferObject> autorizacoes = (List<TransferObject>) parametros.get(CONSIGNACAO);
        autorizacao = autorizacoes.get(0);

        String svcCodigoOrigem = (String)autorizacao.getAttribute(Columns.SVC_CODIGO);
        buscaParamSvc(svcCodigoOrigem);

        validarInfoBancaria(parametros);
        validaPrazo(parametros, convenio);
        validarCarencia(parametros);
        validarSenhaServidor(parametros);
        checkCadInfFinanceira(parametros);
        validaAdeVlr(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
        AlongarConsignacaoParametros alongarParam = new AlongarConsignacaoParametros();

        alongarParam.setAdeCodigo((String) autorizacao.getAttribute(Columns.ADE_CODIGO));
        alongarParam.setRseCodigo((String) parametros.get(RSE_CODIGO));
        alongarParam.setAdeVlr(BigDecimal.valueOf((Double) parametros.get(VALOR_PARCELA)));
        alongarParam.setCorCodigo((String) parametros.get(COR_CODIGO));
        alongarParam.setAdePrazo((Integer) parametros.get(PRAZO));
        alongarParam.setAdeCarencia((parametros.get(ADE_CARENCIA) != null) ? (Integer) parametros.get(ADE_CARENCIA) : 0);
        alongarParam.setAdeIdentificador((parametros.get(NOVO_ADE_IDENTIFICADOR) != null) ? (String) parametros.get(NOVO_ADE_IDENTIFICADOR):"");
        alongarParam.setCnvCodigo((String) convenio.getAttribute(Columns.CNV_CODIGO));
        alongarParam.setSerSenha(serSenha);
        alongarParam.setComSerSenha(comSerSenha);
        alongarParam.setAdeIndice((String) parametros.get(ADE_INDICE));
        if (parametros.get(ADE_VLR_TAC) != null) {
            alongarParam.setAdeVlrTac((BigDecimal) parametros.get(ADE_VLR_TAC));
        }
        if (parametros.get(ADE_VLR_IOF) != null) {
            alongarParam.setAdeVlrIof((BigDecimal) parametros.get(ADE_VLR_IOF));
        }
        if (parametros.get(VALOR_LIBERADO) != null) {
            alongarParam.setAdeVlrLiquido((BigDecimal) parametros.get(VALOR_LIBERADO));
        }
        if (parametros.get(ADE_VLR_MENS_VINC) != null) {
            alongarParam.setAdeVlrMensVinc((BigDecimal) parametros.get(ADE_VLR_MENS_VINC));
        }
        alongarParam.setAdeBanco((String) parametros.get(RSE_BANCO));
        alongarParam.setAdeAgencia((String) parametros.get(RSE_AGENCIA));
        alongarParam.setAdeConta((String) parametros.get(RSE_CONTA));
        alongarParam.setCdeRanking(ranking);
        alongarParam.setCftCodigo(cft_codigo);

        String adeCodigo = consigDelegate.alongarContrato(alongarParam, responsavel);

        buscaNovaAutorizacao(adeCodigo, parametros);

    }

    private void buscaParamSvc(String svcCodigoAlongamento) throws ZetraException {
        // Busca os parâmetros de serviço
        paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoAlongamento, responsavel);
    }

    private void validaPrazo (Map<CamposAPI, Object> parametros, TransferObject convenio) throws ZetraException {
        // Parâmetros de convênio necessários
        boolean permitePrazoMaiorContSer = (convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null &&
                convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO").equals("S"));

        int prazo = (Integer) parametros.get(PRAZO);
        Integer rsePrazo = (Integer) autorizacao.getAttribute(Columns.RSE_PRAZO);

        if (rsePrazo != null && prazo > rsePrazo && !permitePrazoMaiorContSer) {
            throw new ZetraException("mensagem.erro.prazo.maior.ser", responsavel, String.valueOf(rsePrazo));
        }

        int maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null && !paramSvcCse.getTpsMaxPrazo().equals("")) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;

        if (maxPrazo > 0 && prazo > maxPrazo) {
            throw new ZetraException("mensagem.erro.prazo.maior.svc", responsavel, String.valueOf(maxPrazo));
        }
    }

    private void validarInfoBancaria (Map<CamposAPI, Object> parametros) throws ZetraException {
        boolean serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
        boolean validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();

        if (serInfBancariaObrigatoria && validarInfBancaria) {
            String rseCodigo = (String) parametros.get(RSE_CODIGO);
            ServidorDelegate serDelegate = new ServidorDelegate();
            RegistroServidorTO rseResultTo = serDelegate.findRegistroServidor(rseCodigo, responsavel);
            if (rseResultTo != null) {
                RegistroServidorTO rse = new RegistroServidorTO();
                rse.setRseAgenciaSal(rseResultTo.getRseAgenciaSal());
                rse.setRseBancoSal(rseResultTo.getRseBancoSal());
                rse.setRseContaSal(rseResultTo.getRseContaSal());
                rse.setRseBancoSalAlternativo(rseResultTo.getRseBancoSalAlternativo());
                rse.setRseAgenciaSalAlternativa(rseResultTo.getRseAgenciaSalAlternativa());
                rse.setRseContaSalAlternativa(rseResultTo.getRseContaSalAlternativa());

                Object numBanco = parametros.get(RSE_BANCO);
                Object numAgencia = parametros.get(RSE_AGENCIA);
                Object numConta = parametros.get(RSE_CONTA);

                validarDadosBancariosServidor(serInfBancariaObrigatoria, validarInfBancaria, (String) numBanco, (String) numAgencia, (String) numConta, rse);
            }
        }
    }

    private void validaCadastroIndice (Map<CamposAPI, Object> parametros) throws ZetraException {
        // Verifica se sistema permite cadastro de índice para o serviço
        boolean permiteCadIndice = ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel).toString().equals(CodedValues.TPC_SIM) ? true : false;
        // Verifica se sistema o cadastro de índice é numérico ou alfanumérico
        boolean indiceNumerico = ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString().equals(CodedValues.TPC_SIM) ? true : false;

        if (!permiteCadIndice && !TextHelper.isNull(parametros.get(ADE_INDICE))) {
            throw new ZetraException("mensagem.erro.indice.nao.permitido", responsavel);
        }

        if (indiceNumerico) {
            String indice = (String) parametros.get(ADE_INDICE);
            if (!TextHelper.isNull(indice)) {
                try {
                    Integer.parseInt(indice);
                } catch (NumberFormatException nfex) {
                    throw new ZetraException("mensagem.informe.indice.numerico", responsavel);
                }
            }
        }
    }

    private void validarCarencia (Map<CamposAPI, Object> parametros) throws ZetraException {
        int carenciaMinCse = paramSvcCse.getTpsCarenciaMinima() != null && !paramSvcCse.getTpsCarenciaMinima().equals("") ?  Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
        int carenciaMaxCse = paramSvcCse.getTpsCarenciaMaxima() != null && !paramSvcCse.getTpsCarenciaMaxima().equals("") ?  Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;

        // Parâmetros de convênio necessários
        int carenciaMinima = (convenio.getAttribute("CARENCIA_MINIMA") != null && !convenio.getAttribute("CARENCIA_MINIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
        int carenciaMaxima = (convenio.getAttribute("CARENCIA_MAXIMA") != null && !convenio.getAttribute("CARENCIA_MAXIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

        // Define os valores de carência mínimo e máximo
        int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
        int carenciaMinPermitida = carenciaPermitida[0];
        int carenciaMaxPermitida = carenciaPermitida[1];

        Integer carencia = (Integer) parametros.get(ADE_CARENCIA);

        if ((carencia != null) && ((carencia < carenciaMinPermitida) || (carencia > carenciaMaxPermitida))) {
            if (carenciaMaxPermitida > carenciaMinPermitida) {
                throw new ZetraException("mensagem.erro.carencia.entre.min.max", responsavel, String.valueOf(carenciaMinPermitida), String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida < carenciaMinPermitida) {
                throw new ZetraException("mensagem.erro.carencia.menor.max", responsavel, String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida == carenciaMinPermitida) {
                throw new ZetraException("mensagem.erro.carencia.fixa", responsavel, String.valueOf(carenciaMinPermitida));
            }
        }
    }

    private void validarSenhaServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) autorizacao.getAttribute(Columns.RSE_CODIGO);
        String csaCodigo = (String) autorizacao.getAttribute(Columns.CSA_CODIGO);
        String svcCodigoAlongamento = (String)autorizacao.getAttribute(Columns.SVC_CODIGO);

        boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigoAlongamento, csaCodigo, responsavel);
        serSenha = (String) parametros.get(SER_SENHA);

        String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
        boolean csaValidaSenha = (TextHelper.isNull(tpaCsaValidaSenha) || tpaCsaValidaSenha.equals(CodedValues.TPA_NAO)) ? false : true;

        if (TextHelper.isNull(serSenha) && serSenhaObrigatoria) {
            throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
        }

        if (!TextHelper.isNull(serSenha)) {
            String loginExterno = (String) parametros.get(SER_LOGIN);
            try {
                validarSenhaServidor(rseCodigo, serSenha, true, loginExterno, null, null, responsavel);
                comSerSenha = true;
            } catch (ZetraException ex) {
                // Se a senha é obrigatória então retorna erro
                if (serSenhaObrigatoria || csaValidaSenha) {
                    throw ex;
                }
            }
        }
    }

    private void checkCadInfFinanceira(Map<CamposAPI, Object> parametros) throws ZetraException {
        boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac();
        boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof();
        boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado();
        boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();

        if (permiteCadVlrTac && parametros.get(ADE_VLR_TAC) == null) {
            throw new ZetraException("mensagem.informe.ade.valor.tac", responsavel);
        }

        if (permiteCadVlrIof && parametros.get(ADE_VLR_IOF) == null) {
            throw new ZetraException("mensagem.informe.ade.valor.iof", responsavel);
        }

        if (permiteCadVlrLiqLib && parametros.get(VALOR_LIBERADO) == null) {
            throw new ZetraException("mensagem.informe.ade.valor.liberado", responsavel);
        }

        if (permiteCadVlrMensVinc && parametros.get(ADE_VLR_MENS_VINC) == null) {
            throw new ZetraException("mensagem.informe.ade.valor.mensalidade", responsavel);
        }
    }

    private void validaAdeVlr(Map<CamposAPI, Object> parametros) throws ZetraException {
        Double adeVlr = (Double) parametros.get(VALOR_PARCELA);
        Double adeVlrOld = ((BigDecimal) autorizacao.getAttribute(Columns.ADE_VLR)).doubleValue();

        if (adeVlr.compareTo(adeVlrOld) > 0) {
            throw new ZetraException("mensagem.erro.valor.parcela.maior.atual", responsavel);
        }

        String perMaxParc = paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento() != null && !paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento().equals("") ? paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento() : "1";

        float parcMax = Float.parseFloat(perMaxParc);

        if (adeVlr > (adeVlrOld * parcMax)) {
            try {
                throw new ZetraException("mensagem.erro.valor.parcela.maior.percent.atual", responsavel, NumberHelper.format(parcMax * (Float.valueOf("100.00")).floatValue(), NumberHelper.getLang(), 2, 2));
            } catch (NumberFormatException e) {
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
        }
    }

    /**
     * recupera informações da reserva recém criada para enviar como resposta da requisição externa
     * @param parametros
     * @throws ZetraException
     */
    private void buscaNovaAutorizacao(String adeCodigo, Map<CamposAPI, Object> parametros) throws ZetraException {
        String serCodigo = (String) parametros.get(SER_CODIGO);
        String orgCodigo = (String) parametros.get(ORG_CODIGO);

        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        // Busca a nova autorização
        TransferObject novaAutorizacao = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

        // Busca o servidor
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = serDelegate.findServidor(servidor, responsavel);
        // Pega a descrição do codigo de estado civil
        String serEstCivil = serDelegate.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);

        CustomTransferObject boleto = new CustomTransferObject();
        boleto.setAtributos(servidor.getAtributos()); // Adiciona Informações do servidor
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil); // Adiciona a Descrição do estado civil
        boleto.setAtributos(orgao.getAtributos()); // Adiciona Informações do órgão
        boleto.setAtributos(novaAutorizacao.getAtributos()); // Adiciona Informações da autorização

        // Guarda o boleto no hash para ser consultada na geração do resultado
        parametros.put(CONSIGNACAO, boleto);
    }
}
