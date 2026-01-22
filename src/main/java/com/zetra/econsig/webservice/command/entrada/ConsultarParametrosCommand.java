package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;
import static com.zetra.econsig.webservice.CamposAPI.SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarParametrosCommand</p>
 * <p>Description:classe command que trata requisição externa ao eConsig de consultar parâmetros</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarParametrosCommand extends RequisicaoExternaCommand {

    public static final String SVC_DESCRICAO = "SERVICO";
    public static final String TAM_MIN_MATR_SRV = "TAM_MIN_MATR_SERVIDOR";
    public static final String TAMANHO_MATRICULA_MAX = "TAMANHO_MAX_MATRICULA_SERVIDOR";
    public static final String REQUER_MATRICULA_E_CPF = "REQUER_MATRICULA_E_CPF_PESQUISA";
    public static final String VALIDA_CPF_PESQUISA_SERVIDOR = "VALIDA_CPF_PESQUISA_SERVIDOR";
    public static final String REDUZ_VLR_ADE_MARGEM_NEG = "REDUZ_VLR_ADE_MARGEM_NEGATIVA";
    public static final String EXIGE_SENHA_SERVIDOR_CONS_MARGEM = "EXIGE_SENHA_SERVIDOR_CONS_MARGEM";
    public static final String VALIDAR_INF_BANCARIA_NA_RESERVA = "VALIDAR_INF_BANCARIA_NA_RESERVA";
    public static final String INFO_BANCARIA_OBRIGATORIA = "INFO_BANCARIA_OBRIGATORIA";
    public static final String EXIGE_CADASTRO_VALOR_TAC = "EXIGE_CADASTRO_VALOR_TAC";
    public static final String QTD_MAX_PARCELAS = "QTD_MAX_PARCELAS";
    public static final String EXIGE_CAD_VLR_MENSALIDADE_VINC = "EXIGE_CAD_VLR_MENSALIDADE_VINC";
    public static final String EXIGE_CAD_VLR_LIQUIDO_LIBERADO = "EXIGE_CAD_VLR_LIQUIDO_LIBERADO";
    public static final String EXIGE_CAD_VALOR_IOF = "EXIGE_CAD_VALOR_IOF";
    public static final String VALIDA_DATA_NASCIMENTO_NA_RESERVA = "VALIDA_DATA_NASCIMENTO_NA_RESERVA";
    public static final String SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA = "SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA";
    public static final String EXIGE_SENHA_ALTERACAO_CONTRATOS = "EXIGE_SENHA_SERVIDOR_ALTERACAO_CONTRATOS";
    public static final String PERMITE_ALTERACAO_CONTRATOS = "PERMITE_ALTERACAO_CONTRATOS";
    public static final String PERMITE_RENEGOCIACAO = "PERMITE_RENEGOCIACAO";
    public static final String MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO = "MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO";
    public static final String VISUALIZA_MARGEM = "VISUALIZA_MARGEM";
    public static final String VISUALIZA_MARGEM_NEGATIVA = "VISUALIZA_MARGEM_NEGATIVA";
    public static final String DIA_DE_CORTE = "DIA_DE_CORTE";
    public static final String PERIODO_ATUAL = "PERIODO_ATUAL";
    public static final String PERMITE_COMPRAR_CONTRATOS = "PERMITE_COMPRAR_CONTRATOS";
    public static final String DIAS_INFO_SALDO_DEVEDOR = "DIAS_INFO_SALDO_DEVEDOR";
    public static final String DIAS_INFO_PG_SALDO_DEVEDOR = "DIAS_INFO_PG_SALDO_DEVEDOR";
    public static final String DIAS_APRV_SALDO_DEVEDOR = "DIAS_APRV_SALDO_DEVEDOR";
    public static final String DIAS_PARA_LIQUIDAR_CONTRATO = "DIAS_PARA_LIQUIDAR_CONTRATO";

    public ConsultarParametrosCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String csaCodigo = (String) parametros.get(CSA_CODIGO);
        String svcCodigo = (String) parametros.get(SVC_CODIGO);
        
        TransferObject servico = null;

        try {
            ServicoDelegate serDelegate = new ServicoDelegate();
            servico = serDelegate.findServico(svcCodigo);
        } catch (ServicoControllerException e) {
            throw new ZetraException("mensagem.erro.servico.nao.encontrado", responsavel);
        }
        
        TransferObject paramSistSet = recuperarParametrosSistema();

        CustomTransferObject paramSet = new CustomTransferObject(paramSistSet);
        
        paramSet.setAttribute(SVC_DESCRICAO, servico.getAttribute(Columns.SVC_DESCRICAO));

        //Recupera identificador do órgão a pesquisar
        String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        CustomTransferObject orgao = null;
        if (!TextHelper.isNull(orgIdentificador)) {
            OrgaoTransferObject filtro = new OrgaoTransferObject();
            filtro.setOrgIdentificador(orgIdentificador);
            filtro.setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

            List<TransferObject> orgaos = cseDelegate.lstOrgaos(filtro, responsavel);

            // se encontrou um órgão distinto, recupera seus valores
            if (orgaos != null && !orgaos.isEmpty() && orgaos.size() == 1) {
                orgao = (CustomTransferObject) orgaos.get(0);
            } else {
                throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }
        }

        String orgCodigo = (orgao != null) ? (String) orgao.getAttribute(Columns.ORG_CODIGO) : null;
        // recupera dia de corte do sistema
        Integer diaCorte = PeriodoHelper.getInstance().getProximoDiaCorte(orgCodigo, responsavel);
        paramSet.setAttribute(DIA_DE_CORTE, diaCorte.shortValue());
        // recupera período atual
        Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
        paramSet.setAttribute(PERIODO_ATUAL, periodoAtual);

        // lista de parâmetros de serviço CSE a consultar
        ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

        // lista de parâmetros de serviço CSA a consultar que sobrepoem os param. de serviço CSE.
        List<String> paramsInfoBancaria = new ArrayList<>();
        paramsInfoBancaria.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);

        List<TransferObject> paramSvcCSaList = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, paramsInfoBancaria, false, responsavel);

        paramSet.setAttribute(VALIDAR_INF_BANCARIA_NA_RESERVA, paramSvcCse.isTpsValidarInfBancariaNaReserva());
        paramSet.setAttribute(INFO_BANCARIA_OBRIGATORIA, paramSvcCse.isTpsInfBancariaObrigatoria());
        paramSet.setAttribute(EXIGE_CADASTRO_VALOR_TAC, paramSvcCse.isTpsCadValorTac());
        paramSet.setAttribute(QTD_MAX_PARCELAS, paramSvcCse.getTpsMaxPrazo());
        paramSet.setAttribute(EXIGE_CAD_VLR_MENSALIDADE_VINC, paramSvcCse.isTpsCadValorMensalidadeVinc());
        paramSet.setAttribute(EXIGE_CAD_VLR_LIQUIDO_LIBERADO, paramSvcCse.isTpsCadValorLiquidoLiberado());
        paramSet.setAttribute(EXIGE_CAD_VALOR_IOF, paramSvcCse.isTpsCadValorIof());
        paramSet.setAttribute(VALIDA_DATA_NASCIMENTO_NA_RESERVA, paramSvcCse.isTpsValidarDataNascimentoNaReserva());
        paramSet.setAttribute(SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA, paramSvcCse.isTpsSerSenhaObrigatoriaCsa());
        if (paramSvcCSaList != null && !paramSvcCSaList.isEmpty()) {
            for (TransferObject paramSvcCsa : paramSvcCSaList) {
                if (paramSvcCsa.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)) {
                    Object pscVlr = paramSvcCsa.getAttribute(Columns.PSC_VLR);
                    if (!TextHelper.isNull(pscVlr)) {
                        paramSet.setAttribute(SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA, pscVlr.equals("N") ? false:true);
                    }
                }
            }
        }
        paramSet.setAttribute(EXIGE_SENHA_ALTERACAO_CONTRATOS, !paramSvcCse.getTpsExigeSenhaAlteracaoContratos().equals(CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS));
        paramSet.setAttribute(PERMITE_ALTERACAO_CONTRATOS, paramSvcCse.isTpsPermiteAlteracaoContratos());
        paramSet.setAttribute(PERMITE_RENEGOCIACAO, paramSvcCse.isTpsPermiteRenegociacao());
        paramSet.setAttribute(MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO, paramSvcCse.getTpsMinimoPrdPagasRenegociacao());
        paramSet.setAttribute(DIAS_INFO_SALDO_DEVEDOR, !TextHelper.isNull(paramSvcCse.getTpsDiasInfSaldoDvControleCompra()) ? Short.valueOf(paramSvcCse.getTpsDiasInfSaldoDvControleCompra()) : null);
        paramSet.setAttribute(DIAS_INFO_PG_SALDO_DEVEDOR, !TextHelper.isNull(paramSvcCse.getTpsDiasInfPgtSaldoControleCompra()) ? Short.valueOf(paramSvcCse.getTpsDiasInfPgtSaldoControleCompra()) : null);
        paramSet.setAttribute(DIAS_APRV_SALDO_DEVEDOR, !TextHelper.isNull(paramSvcCse.getTpsDiasAprSaldoDvControleCompra()) ? Short.valueOf(paramSvcCse.getTpsDiasAprSaldoDvControleCompra()) : null);
        paramSet.setAttribute(DIAS_PARA_LIQUIDAR_CONTRATO, !TextHelper.isNull(paramSvcCse.getTpsDiasLiquidacaoAdeControleCompra()) ? Short.valueOf(paramSvcCse.getTpsDiasLiquidacaoAdeControleCompra()) : null);

        //retorna a margem incidente do serviço
        Short marCodigo = paramSvcCse.getTpsIncideMargem();
        MargemTO marTO = MargemHelper.getInstance().getMargem(marCodigo, responsavel);
        ExibeMargem exibeMargem = new ExibeMargem(marTO, responsavel);
        paramSet.setAttribute(VISUALIZA_MARGEM, exibeMargem.isExibeValor());
        paramSet.setAttribute(VISUALIZA_MARGEM_NEGATIVA, exibeMargem.isSemRestricao());

        parametros.put(PARAMETRO_SET, paramSet);

    }

    private void recuperaSvcCodigo (Map<CamposAPI, Object> parametros) throws ZetraException {
        Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        Object svcIdentificador = parametros.get(SERVICO_CODIGO);
        String csaCodigo = (String) parametros.get(CSA_CODIGO);
        ConvenioDelegate cnvDelegate = new ConvenioDelegate();

        String svcCodigo = null; // Código do serviço na reserva de margem e na simulação

        List<TransferObject> servicos = cnvDelegate.getSvcByCodVerbaSvcIdentificador((String)svcIdentificador, (String)cnvCodVerba, null, csaCodigo, true, responsavel);
        if (servicos.size() == 0) {
            throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
        } else if (servicos.size() == 1) {
            CustomTransferObject convenio = (CustomTransferObject) servicos.get(0);
            svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
        } else if (svcIdentificador != null && !svcIdentificador.equals("")) {
            for (TransferObject servico : servicos) {
                if (servico.getAttribute(Columns.SVC_IDENTIFICADOR).equals(svcIdentificador)) {
                    svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                    break;
                }
            }
            if (svcCodigo == null || svcCodigo.equals("")) {
                throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
            }
        } else {
            Map<Object, TransferObject> svcCodigosDistintos = new HashMap<>();

            for (Object servico: servicos) {
                if (!svcCodigosDistintos.containsKey(((TransferObject) servico).getAttribute(Columns.SVC_CODIGO))) {
                    svcCodigosDistintos.put(((TransferObject) servico).getAttribute(Columns.SVC_CODIGO),(TransferObject) servico);
                }
            }

            if (svcCodigosDistintos.size() == 1) {
                Collection<TransferObject> svcVlr = svcCodigosDistintos.values();
                TransferObject convenio = svcVlr.iterator().next();
                svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
            } else {
                parametros.put(SERVICOS, Arrays.asList(svcCodigosDistintos.values().toArray()));
                throw new ZetraException("mensagem.maisDeUmServicoEncontrado", responsavel);
            }
        }

        parametros.put(SVC_CODIGO, svcCodigo);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);
        recuperaSvcCodigo(parametros);
    }

    protected CustomTransferObject recuperarParametrosSistema () throws ZetraException {

        CustomTransferObject paramSet = new CustomTransferObject();

        ParamSist paramSist = ParamSist.getInstance();

        // recupera param. tam máximo matrícula servidor
        Object tamMaxMatrSrv = paramSist.getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel);
        // pela documentação, o tam máximo da matrícula = 0 significa que não tem limite máximo pro tamanho da matrícula.
        int tamMaxMatrSrvVlr = 0;
        if (!TextHelper.isNull(tamMaxMatrSrv)) {
            try {
                tamMaxMatrSrvVlr = Integer.valueOf(tamMaxMatrSrv.toString());
            } catch (NumberFormatException ne) {
                throw new ZetraException("mensagem.erro.parametro.sistema.matricula.tam.max.invalido", responsavel);
            }
        }
        paramSet.setAttribute(TAMANHO_MATRICULA_MAX, tamMaxMatrSrvVlr);

        //recupera param. tam mínimo matrícula servidor
        Object tamMinMatrSrv = paramSist.getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
        int tamMinMatrSrvVlr = 0;
        if (!TextHelper.isNull(tamMinMatrSrv)) {
            try {
                tamMinMatrSrvVlr = Integer.valueOf(tamMinMatrSrv.toString());
            } catch (NumberFormatException ne) {
                throw new ZetraException("mensagem.erro.parametro.sistema.matricula.tam.min.invalido", responsavel);
            }
        }
        paramSet.setAttribute(TAM_MIN_MATR_SRV, tamMinMatrSrvVlr);

        // valida CPF na pesquisa
        Object validaCpfPesquisa = paramSist.getParam(CodedValues.TPC_VALIDA_CPF_PESQ_SERVIDOR, responsavel);
        boolean validaCpfPesquisaVlr = (validaCpfPesquisa != null && validaCpfPesquisa.equals("N")) ? false:true;
        paramSet.setAttribute(VALIDA_CPF_PESQUISA_SERVIDOR, validaCpfPesquisaVlr);

        // recupera param. de redução de valor de ade com margem negativa
        boolean alteraAutMargemNegativa = ParamSist.getBoolParamSist(CodedValues.TPC_REDUCAO_VLR_ADE_MARGEM_NEG, responsavel);
        paramSet.setAttribute(REDUZ_VLR_ADE_MARGEM_NEG, alteraAutMargemNegativa);

        // recupera param. senha servidor obrigatória p/ consultar margem
        boolean exigeSenhaSerConsMargem = ParamSist.getBoolParamSist(CodedValues.TPC_SENHA_SER_ACESSAR_CONS_MARGEM, responsavel);
        paramSet.setAttribute(EXIGE_SENHA_SERVIDOR_CONS_MARGEM, exigeSenhaSerConsMargem);

        // recupera param. permissão de compra de contratos
        boolean permiteCompraContrato = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
        paramSet.setAttribute(PERMITE_COMPRAR_CONTRATOS, permiteCompraContrato);

        String diaCorte = (String) paramSist.getParam(CodedValues.TPC_DIA_CORTE, responsavel);
        paramSet.setAttribute(DIA_DE_CORTE, TextHelper.isNum(diaCorte) ? Short.valueOf(diaCorte) : null);

        // recupera param. exige matrícula e cpf para pesquisa
        boolean exigeMatrCpfPesquisa = parametroController.requerMatriculaCpf(responsavel);
        paramSet.setAttribute(REQUER_MATRICULA_E_CPF, exigeMatrCpfPesquisa);

        return paramSet;

    }
}
