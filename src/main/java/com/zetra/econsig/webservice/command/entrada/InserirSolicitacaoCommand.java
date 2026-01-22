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
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.COR_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MUNICIPIO_LOTACAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TERMO_ACEITE;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;

import java.math.BigDecimal;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleTokenAcesso;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: InserirSolicitacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de inserir solicitação de reserva</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InserirSolicitacaoCommand extends SolicitarReservaCommand {

    public InserirSolicitacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaCnvCodigoSvcCodigo(parametros);
        validaCodigoVerba(parametros);
        validaValorAutorizacao(parametros);
        validaAdePrazo(parametros);
        validaDataNascimento(parametros);
        validaPresencaSenhaServidor(parametros);
        validaDadosServidor(parametros);
        validaPermissoes(parametros);
    }

    @Override
    protected void realizaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object adeVlr = parametros.get(ADE_VLR);
        String corCodigo = (String) parametros.get(COR_CODIGO);
        Object adePrazo = parametros.get(ADE_PRAZO);
        Object adeCarencia = parametros.get(ADE_CARENCIA);
        String cnvCodigo = (String) parametros.get(CNV_CODIGO);
        String svcCodigo = (String) parametros.get(SVC_CODIGO);
        //    Indice
        Object adeIndice = parametros.get(ADE_INDICE);
        BigDecimal adeVlrTac = (BigDecimal) parametros.get(ADE_VLR_TAC);
        BigDecimal adeVlrIof = (BigDecimal) parametros.get(ADE_VLR_IOF);
        BigDecimal adeVlrLiquido = ((BigDecimal) parametros.get(ADE_VLR_LIQUIDO) != null) ? (BigDecimal) parametros.get(ADE_VLR_LIQUIDO) : (BigDecimal) parametros.get(VALOR_LIBERADO);
        BigDecimal adeVlrMensVinc = (BigDecimal) parametros.get(ADE_VLR_MENS_VINC);
        BigDecimal adeTaxaJuros = (BigDecimal) parametros.get(ADE_TAXA_JUROS);
        String token = (String) parametros.get(TOKEN);

        // Informações bancárias
        Object numBanco = parametros.get(RSE_BANCO);
        Object numAgencia = parametros.get(RSE_AGENCIA);
        Object numConta = parametros.get(RSE_CONTA);

        Object objTermoAceite = parametros.get(TERMO_ACEITE);
        Boolean termoAceite = !TextHelper.isNull(objTermoAceite) && Boolean.parseBoolean(objTermoAceite.toString());

        // Monta o objeto de parâmetro da reserva
        ReservarMargemParametros reservaParam = new ReservarMargemParametros();

        ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

        ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        ConvenioTransferObject convenio = cnvDelegate.findByPrimaryKey(cnvCodigo, responsavel);

        reservaParam.setRseCodigo(rseCodigo);
        reservaParam.setAdeVlr((BigDecimal) adeVlr);
        reservaParam.setCorCodigo(corCodigo);
        reservaParam.setAdePrazo((Integer) adePrazo);
        reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa((Integer) adeCarencia, convenio.getCsaCodigo(), convenio.getOrgCodigo(), responsavel));
        reservaParam.setAdeIdentificador(adeIdentificador.toString());
        reservaParam.setCnvCodigo(cnvCodigo);
        reservaParam.setAdeIndice((String) adeIndice);
        reservaParam.setAdeVlrTac(adeVlrTac);
        reservaParam.setAdeVlrIof(adeVlrIof);
        reservaParam.setAdeVlrLiquido(adeVlrLiquido);
        reservaParam.setAdeVlrMensVinc(adeVlrMensVinc);
        reservaParam.setSerAtivo(Boolean.TRUE);
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
        reservaParam.setComSerSenha(Boolean.valueOf(comSerSenha));
        reservaParam.setValidaAnexo(false);
        // telefone informado na solicitação deve ser salvo como dado de autorização TDA_SOLICITACAO_TEL_SERVIDOR
        reservaParam.setTdaTelSolicitacaoSer((String) parametros.get(SER_TEL));
        // Termo de aceite
        reservaParam.setExigenciaConfirmacaoLeitura(termoAceite.toString());

        // Monta dados do servidor para ser alterado
        String serCodigo = (String) parametros.get(SER_CODIGO);
        // Parâmetros da requisição de solicitação de reserva
        Object serEnd = parametros.get(SER_END);
        Object nro = parametros.get(SER_NRO);
        String serNro = nro != null ? nro.toString() : null;
        Object serCompl = parametros.get(SER_COMPL);
        Object serBairro = parametros.get(SER_BAIRRO);
        Object serCidade = parametros.get(SER_CIDADE);
        Object serUf = parametros.get(SER_UF);
        Object serCep = parametros.get(SER_CEP);

        ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
        servidorUpd.setSerEnd((String) serEnd);
        servidorUpd.setSerCompl((String) serCompl);
        servidorUpd.setSerBairro((String) serBairro);
        servidorUpd.setSerCidade((String) serCidade);
        servidorUpd.setSerUf((String) serUf);
        servidorUpd.setSerCep((String) serCep);
        servidorUpd.setSerNro(serNro);

        if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
            servidorUpd.setSerTel((String) parametros.get(SER_TEL));
        }

        // Monta dados do registro servidor para ser alterado
        Object rseMunicipioLotacao = parametros.get(RSE_MUNICIPIO_LOTACAO);
        RegistroServidorTO registroServidorUpd = new RegistroServidorTO(rseCodigo);
        registroServidorUpd.setRseMunicipioLotacao((String) rseMunicipioLotacao);

        // Faz a reserva de margem
        try {
            adeCodigo = consigDelegate.solicitarReservaMargem(reservaParam, servidorUpd, registroServidorUpd, svcCodigo, responsavel);
            // inclui ocorrência de operação via host a host para módulo servidor
            if (responsavel.isSer()) {
                consigDelegate.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_OPERACAO_HOST_A_HOST, ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.solicitacao.host.a.host", responsavel), responsavel);
            }
        } catch (AutorizacaoControllerException e) {
            throw e;
        }

        // Se reservou margem o token não pode ser usado novamente.
        ControleTokenAcesso.getInstance().invalidarToken(token);

    }

    @Override
    protected void preProcessaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessaReserva(parametros);

        String svcCodigo = (String) parametros.get(SVC_CODIGO);
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object adeVlr = parametros.get(ADE_VLR);

        ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);

        /*
        Consulta a Margem do Servidor. A reserva em si já valida a parcela em comparação
        com a margem restante do servidor. Mas no caso em que a parcela for maior que a
        margem, esta consulta prévia elimina todo o restante do processo de reserva,
        aumentando a performance da operação. No caso positivo, aquele que a parcela é
        menor que a margem, o acréscimo do tempo de resposta é muito menor do que o ganho
        no caso negativo.
         */
        if (!consultarMargemController.servidorTemMargem(rseCodigo, (BigDecimal) adeVlr, svcCodigo, true, responsavel)) {
            throw new ZetraException("mensagem.margemInsuficiente", responsavel);
        }
    }

    private void validaDadosServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        /* OBS: NÃO SÃO OBRIGATÓRIOS
        // Parâmetros da requisição de solicitação de reserva
        Object serEnd = parametros.get(ENDERECO);
        Object serBairro = parametros.get(BAIRRO);
        Object serCidade = parametros.get(CIDADE);
        Object serUf = parametros.get(UF);
        Object serCep = parametros.get(CEP);

        if (serEnd == null || serEnd.equals("")) {
            throw new ZetraException("mensagem.informe.servidor.logradouro", responsavel);
        }
        if (serBairro == null || serBairro.equals("")) {
            throw new ZetraException("mensagem.informe.servidor.bairro", responsavel);
        }
        if (serCidade == null || serCidade.equals("")) {
            throw new ZetraException("mensagem.informe.servidor.cidade", responsavel);
        }
        if (serUf == null || serUf.equals("")) {
            throw new ZetraException("mensagem.informe.servidor.estado", responsavel);
        }
        if (serCep == null || serCep.equals("")) {
            throw new ZetraException("mensagem.informe.servidor.cep", responsavel);
        }
         */

        Object serTel = parametros.get(SER_TEL);
        boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel) ||
                ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);
        if (exigeTelefone && TextHelper.isNull(serTel)) {
            throw new ZetraException("mensagem.informe.servidor.telefone", responsavel);
        }

        Object rseMunicipioLotacao = parametros.get(RSE_MUNICIPIO_LOTACAO);
        boolean exigeMunicipioLotacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
        if (exigeMunicipioLotacao && TextHelper.isNull(rseMunicipioLotacao)) {
            throw new ZetraException("mensagem.informe.servidor.municipio.lotacao", responsavel);
        }
    }
}
