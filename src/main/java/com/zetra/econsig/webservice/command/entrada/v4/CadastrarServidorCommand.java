package com.zetra.econsig.webservice.command.entrada.v4;

import static com.zetra.econsig.webservice.CamposAPI.CRS_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRS_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_ASSOCIADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BASE_CALCULO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CATEGORIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CLT;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_CTC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_FIM_ENGAJAMENTO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_LIMITE_PERMANENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_COMP;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_FACU;
import static com.zetra.econsig.webservice.CamposAPI.RSE_ESTABILIZADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA_INST;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MUNICIPIO_LOTACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OBS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OUTROS_DESCONTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRACA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SBO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V4_0;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V8_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.SER_DDD_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_DDD_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_CONJUGE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MEIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_TITULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.UNI_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.VRS_IDENTIFICADOR;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor;

/**
 * <p>Title: CadastrarServidorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar/atualizar servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarServidorCommand extends RequisicaoExternaCommand {

    public CadastrarServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // constantes de margem/ins_servidor_para_reserva.jsp
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel) && TextHelper.isNull(parametros.get(SER_EST_CIVIL))) {
            throw new ZetraException("mensagem.informe.servidor.estado.civil", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_NACIONALIDADE))) {
            throw new ZetraException("mensagem.informe.servidor.nacionalidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel) && TextHelper.isNull(parametros.get(SER_CART_PROF))) {
            throw new ZetraException("mensagem.informe.servidor.cart.trabalho", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel) && TextHelper.isNull(parametros.get(SER_PIS))) {
            throw new ZetraException("mensagem.informe.servidor.pis", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel) && TextHelper.isNull(parametros.get(SER_NOME))) {
            throw new ZetraException("mensagem.informe.servidor.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO, responsavel) && TextHelper.isNull(parametros.get(SER_TITULACAO))) {
            throw new ZetraException("mensagem.informe.servidor.tratamento.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel) && TextHelper.isNull(parametros.get(SER_PRIMEIRO_NOME))) {
            throw new ZetraException("mensagem.informe.servidor.primeiro.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME, responsavel) && TextHelper.isNull(parametros.get(SER_NOME_MEIO))) {
            throw new ZetraException("mensagem.informe.servidor.meio.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME, responsavel) && TextHelper.isNull(parametros.get(SER_ULTIMO_NOME))) {
            throw new ZetraException("mensagem.informe.servidor.ultimo.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel) && TextHelper.isNull(parametros.get(SER_CPF))) {
            throw new ZetraException("mensagem.informe.servidor.cpf", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel) && TextHelper.isNull(parametros.get(SER_NOME_PAI))) {
            throw new ZetraException("mensagem.informe.servidor.nome.pai", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel) && TextHelper.isNull(parametros.get(SER_NOME_MAE))) {
            throw new ZetraException("mensagem.informe.servidor.nome.mae", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel) && TextHelper.isNull(parametros.get(SER_DATA_NASCIMENTO))) {
            throw new ZetraException("mensagem.informe.servidor.data.nascimento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_NRO_IDT))) {
            throw new ZetraException("mensagem.informe.servidor.identidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_EMISSOR_IDT))) {
            throw new ZetraException("mensagem.informe.servidor.emissor.identidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_UF_IDT))) {
            throw new ZetraException("mensagem.informe.servidor.uf.identidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_DATA_IDT))) {
            throw new ZetraException("mensagem.informe.servidor.data.emissao.identidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel) && TextHelper.isNull(parametros.get(SER_END))) {
            throw new ZetraException("mensagem.informe.servidor.logradouro", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel) && TextHelper.isNull(parametros.get(SER_COMPL))) {
            throw new ZetraException("mensagem.informe.servidor.complemento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel) && TextHelper.isNull(parametros.get(SER_BAIRRO))) {
            throw new ZetraException("mensagem.informe.servidor.bairro", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel) && TextHelper.isNull(parametros.get(SER_CIDADE))) {
            throw new ZetraException("mensagem.informe.servidor.cidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel) && TextHelper.isNull(parametros.get(SER_UF))) {
            throw new ZetraException("mensagem.informe.servidor.estado", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel) && TextHelper.isNull(parametros.get(SER_CEP))) {
            throw new ZetraException("mensagem.informe.servidor.cep", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE, responsavel) && TextHelper.isNull(parametros.get(SER_DDD_TEL))) {
            throw new ZetraException("mensagem.informe.servidor.ddd.telefone", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel) && TextHelper.isNull(parametros.get(SER_TEL))) {
            throw new ZetraException("mensagem.informe.servidor.telefone", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel) && TextHelper.isNull(parametros.get(SER_EMAIL))) {
            throw new ZetraException("mensagem.informe.servidor.email", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel) && TextHelper.isNull(parametros.get(SER_SEXO))) {
            throw new ZetraException("mensagem.informe.servidor.sexo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel) && TextHelper.isNull(parametros.get(SER_NRO))) {
            throw new ZetraException("mensagem.informe.servidor.numero", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR, responsavel) && TextHelper.isNull(parametros.get(SER_DDD_CELULAR))) {
            throw new ZetraException("mensagem.informe.servidor.ddd.celular", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel) && TextHelper.isNull(parametros.get(SER_CELULAR))) {
            throw new ZetraException("mensagem.informe.servidor.celular", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SUB_ORGAO, responsavel) && TextHelper.isNull(parametros.get(SBO_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.sub.orgao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UNIDADE, responsavel) && TextHelper.isNull(parametros.get(UNI_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.unidade", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel) && TextHelper.isNull(parametros.get(PRS_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.padrao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel) && TextHelper.isNull(parametros.get(RSE_ESTABILIZADO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.estabilizado", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_FIM_ENGAJAMENTO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.engajado", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_LIMITE_PERMANENCIA))) {
            throw new ZetraException("mensagem.informe.registro.servidor.data.limite.permanencia", responsavel);
        }
        //OBS: Tipo, Posto e Capacidade Civil foram ignorados por servirem somente à Aeronáutica
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel) && TextHelper.isNull(parametros.get(RSE_BANCO_SAL_2))) {
            throw new ZetraException("mensagem.informe.registro.servidor.banco.alternativo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel) && TextHelper.isNull(parametros.get(RSE_AGENCIA_SAL_2))) {
            throw new ZetraException("mensagem.informe.registro.servidor.agencia.alternativa", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel) && TextHelper.isNull(parametros.get(RSE_CONTA_SAL_2))) {
            throw new ZetraException("mensagem.informe.registro.servidor.conta.alternativa", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel) && TextHelper.isNull(parametros.get(RSE_CATEGORIA))) {
            throw new ZetraException("mensagem.informe.registro.servidor.categoria", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel) && TextHelper.isNull(parametros.get(CRS_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.cargo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel) && TextHelper.isNull(parametros.get(RSE_CLT))) {
            throw new ZetraException("mensagem.informe.registro.servidor.sindicalizado", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_ADMISSAO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.data.admissao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel) && (TextHelper.isNull(parametros.get(RSE_PRAZO)) || (Integer.valueOf(-1)).equals(parametros.get(RSE_PRAZO)))) {
            throw new ZetraException("mensagem.informe.registro.servidor.prazo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel) && TextHelper.isNull(parametros.get(VRS_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.vinculo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) && TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
            throw new ZetraException("mensagem.informe.registro.servidor.matricula", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABELECIMENTO, responsavel) && TextHelper.isNull(parametros.get(EST_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.estabelecimento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel) && TextHelper.isNull(parametros.get(ORG_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.orgao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel) && TextHelper.isNull(parametros.get(SITUACAO_SERVIDOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.situacao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel) && TextHelper.isNull(parametros.get(RSE_BANCO_SAL))) {
            throw new ZetraException("mensagem.informe.registro.servidor.banco", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel) && TextHelper.isNull(parametros.get(RSE_AGENCIA_SAL))) {
            throw new ZetraException("mensagem.informe.registro.servidor.agencia", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel) && TextHelper.isNull(parametros.get(RSE_CONTA_SAL))) {
            throw new ZetraException("mensagem.informe.registro.servidor.conta", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel) && TextHelper.isNull(parametros.get(RSE_OBS))) {
            throw new ZetraException("mensagem.informe.registro.servidor.obs", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel) && TextHelper.isNull(parametros.get(RSE_MUNICIPIO_LOTACAO))) {
            throw new ZetraException("mensagem.informe.servidor.municipio.lotacao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel) && TextHelper.isNull(parametros.get(RSE_PRACA))) {
            throw new ZetraException("mensagem.informe.registro.servidor.praca", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) && TextHelper.isNull(parametros.get(RSE_SALARIO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.salario", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) && TextHelper.isNull(parametros.get(RSE_PROVENTOS))) {
            throw new ZetraException("mensagem.informe.registro.servidor.salario.proventos", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) && TextHelper.isNull(parametros.get(SER_CID_NASC))) {
            throw new ZetraException("mensagem.informe.cidade.nascimento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) && TextHelper.isNull(parametros.get(RSE_DESCONTOS_COMP))) {
            throw new ZetraException("mensagem.informe.registro.servidor.descontos.compulsorios", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) && TextHelper.isNull(parametros.get(RSE_DESCONTOS_FACU))) {
            throw new ZetraException("mensagem.informe.registro.servidor.descontos.facultativos", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) && TextHelper.isNull(parametros.get(RSE_OUTROS_DESCONTOS))) {
            throw new ZetraException("mensagem.informe.registro.servidor.outros.descontos", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel) && TextHelper.isNull(parametros.get(RSE_ASSOCIADO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.associado", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) && TextHelper.isNull(parametros.get(RSE_BASE_CALCULO))) {
            throw new ZetraException("mensagem.informe.registro.servidor.base.calculo", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) && TextHelper.isNull(parametros.get(RSE_MATRICULA_INST))) {
            throw new ZetraException("mensagem.informe.registro.servidor.matricula.institucional", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_CTC))) {
            throw new ZetraException("mensagem.informe.registro.servidor.data.contracheque", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) && TextHelper.isNull(parametros.get(SER_NOME_CONJUGE))) {
            throw new ZetraException("mensagem.informe.servidor.nome.conjuge", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) && TextHelper.isNull(parametros.get(SER_UF_NASC))) {
            throw new ZetraException("mensagem.informe.servidor.uf.nascimento", responsavel);
        }


    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final ServidorDelegate serDelegate = new ServidorDelegate();

        final ServidorTransferObject servidor = new ServidorTransferObject();
        final RegistroServidorTO registroServidor = new RegistroServidorTO();

        // Recupera os dados do cadastro do servidor

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel)) {
            servidor.setSerCpf((String) parametros.get(SER_CPF));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel)) {
            servidor.setSerNomePai((String) parametros.get(SER_NOME_PAI));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel)) {
            servidor.setSerNomeMae((String) parametros.get(SER_NOME_MAE));
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel)) {
            final Object serDataNasc = parametros.get(SER_DATA_NASCIMENTO);
            if (serDataNasc != null) {
                servidor.setSerDataNasc(DateHelper.toSQLDate((java.util.Date) serDataNasc));
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO, responsavel)) {
            servidor.setSerTitulacao((String) parametros.get(SER_TITULACAO));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel)) {
            servidor.setSerPrimeiroNome((String) parametros.get(SER_PRIMEIRO_NOME));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME, responsavel)) {
            servidor.setSerNomeMeio((String) parametros.get(SER_NOME_MEIO));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME, responsavel)) {
            servidor.setSerUltimoNome((String) parametros.get(SER_ULTIMO_NOME));
        }

        final String serNomeCompleto = (!TextHelper.isNull(parametros.get(SER_NOME)) &&
                                  ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel)) ?
                                  (String) parametros.get(SER_NOME) :
                                  JspHelper.montaSerNome(servidor.getSerTitulacao(), servidor.getSerPrimeiroNome(), servidor.getSerNomeMeio(),
                                  servidor.getSerUltimoNome());
        servidor.setSerNome(serNomeCompleto);

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel)) {
            servidor.setSerNacionalidade((String) parametros.get(SER_NACIONALIDADE));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel)) {
            servidor.setSerSexo((String) parametros.get(SER_SEXO));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel)) {
            servidor.setSerEstCivil((String) parametros.get(SER_EST_CIVIL));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel)) {
            servidor.setSerNroIdt((String) parametros.get(SER_NRO_IDT));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel)) {
            servidor.setSerEmissorIdt((String) parametros.get(SER_EMISSOR_IDT));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel)) {
            servidor.setSerUfIdt((String) parametros.get(SER_UF_IDT));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel)) {
            final Object serDataIdt = parametros.get(SER_DATA_IDT);
            if (serDataIdt != null) {
                servidor.setSerDataIdt(DateHelper.toSQLDate((java.util.Date) serDataIdt));
            }
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel)) {
            servidor.setSerCartProf((String) parametros.get(SER_CART_PROF));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel)) {
            servidor.setSerPis((String) parametros.get(SER_PIS));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel)) {
            servidor.setSerEnd((String) parametros.get(SER_END));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel)) {
            servidor.setSerNro((String) parametros.get(SER_NRO));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel)) {
            servidor.setSerCompl((String) parametros.get(SER_COMPL));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel)) {
            servidor.setSerBairro((String) parametros.get(SER_BAIRRO));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel)) {
            servidor.setSerCidade((String) parametros.get(SER_CIDADE));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel)) {
            servidor.setSerUf((String) parametros.get(SER_UF));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel)) {
            servidor.setSerCep((String) parametros.get(SER_CEP));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel)) {
            servidor.setSerEmail((String) parametros.get(SER_EMAIL));
        }

        String serTelDdd = null;
        String serTel = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE, responsavel) && (parametros.get(SER_DDD_TEL) != null)) {
            serTelDdd = (String) parametros.get(SER_DDD_TEL);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel)) {
            serTel = (String) parametros.get(SER_TEL);
            if (serTel != null) {
                serTel = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serTel));
            }
        }
        if (!TextHelper.isNull(serTelDdd) && !TextHelper.isNull(serTel)) {
            servidor.setSerTel(serTelDdd + "-" + serTel);
        } else if (!TextHelper.isNull(serTel)) {
            servidor.setSerTel(serTel);
        } else if (!TextHelper.isNull(serTelDdd)) {
            servidor.setSerTel(serTelDdd);
        }


        String serCelularDdd = null;
        String serCelular = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR, responsavel) && (parametros.get(SER_DDD_CELULAR) != null)) {
            serCelularDdd = (String) parametros.get(SER_DDD_CELULAR);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel)) {
            serCelular = (String) parametros.get(SER_CELULAR);
            if (serCelular != null) {
                serCelular = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serCelular));
            }
        }
        if (!TextHelper.isNull(serCelularDdd) && !TextHelper.isNull(serCelular)) {
            servidor.setSerCelular(serCelularDdd + "-" + serCelular);
        } else if (!TextHelper.isNull(serCelularDdd) || !TextHelper.isNull(serCelular)) {
            servidor.setSerCelular(serCelular);
        }

        servidor.setSerDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

        //SRS_CODIGO`) REFERENCES `tb_status_registro_servidor`
        SituacaoServidor situacaoServidor = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel)) {
            situacaoServidor = (SituacaoServidor) parametros.get(SITUACAO_SERVIDOR);
        }
        if ((situacaoServidor != null) &&
               (situacaoServidor.getAtivo() || situacaoServidor.getBloqueado() || situacaoServidor.getExcluido() ||
                situacaoServidor.getFalecido() || situacaoServidor.getPendente())) {
            if (situacaoServidor.getAtivo()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
            } else if (situacaoServidor.getBloqueado()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_BLOQUEADO);
            } else if (situacaoServidor.getExcluido()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
            } else if (situacaoServidor.getFalecido()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_FALECIDO);
            } else if (situacaoServidor.getPendente()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_PENDENTE);
            }
        } else if (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) {
            registroServidor.setSrsCodigo(CodedValues.SRS_PENDENTE);
        } else {
            registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel)) {
            registroServidor.setRseTipo((String) parametros.get(RSE_CATEGORIA));
        }

        // (vrs_codigo`) REFERENCES `tb_vinculo_registro_servidor`
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel)) {
            final String vrsIdentificador = (String) parametros.get(VRS_IDENTIFICADOR);
            if (!TextHelper.isNull(vrsIdentificador)) {
                final List<TransferObject> listVincRegSer = serDelegate.findVincRegistroServidor(vrsIdentificador, true, responsavel);
                if ((listVincRegSer != null) && (listVincRegSer.size() == 1)) {
                    registroServidor.setVrsCodigo((String) listVincRegSer.get(0).getAttribute(Columns.VRS_CODIGO));
                } else {
                    throw new ZetraException("mensagem.erro.vinculo.registro.servidor.nao.encontrado", responsavel);
                }
            }
        }

        // (`crs_codigo`) REFERENCES `tb_cargo_registro_servidor`
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel)) {
            final String crsIdentificador = (String) parametros.get(CRS_IDENTIFICADOR);
            if (!TextHelper.isNull(crsIdentificador)) {
                final List<TransferObject> listCargoRegSer = serDelegate.findCargoByIdentificador(crsIdentificador, responsavel);
                if ((listCargoRegSer != null) && (listCargoRegSer.size() == 1)) {
                    registroServidor.setCrsCodigo((String) listCargoRegSer.get(0).getAttribute(Columns.CRS_CODIGO));
                } else {
                    throw new ZetraException("mensagem.erro.cargo.nao.encontrado", responsavel);
                }
            }
        }

        // (`prs_codigo`) REFERENCES `tb_padrao_registro_servidor
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel)) {
            final String prsIdentificador = (String) parametros.get(PRS_IDENTIFICADOR);
            if (!TextHelper.isNull(prsIdentificador)) {
                final List<TransferObject> listPadrao = serDelegate.findPadraoByIdentificador(prsIdentificador, responsavel);
                if ((listPadrao != null) && (listPadrao.size() == 1)) {
                    registroServidor.setPrsCodigo((String) listPadrao.get(0).getAttribute(Columns.PRS_CODIGO));
                } else {
                    throw new ZetraException("mensagem.erro.padrao.nao.encontrado", responsavel);
                }
            }
        }

        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String estCodigo = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABELECIMENTO, responsavel) && !TextHelper.isNull(estIdentificador)) {
            final EstabelecimentoTransferObject estabelecimento = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);
            if (estabelecimento == null) {
                throw new ZetraException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
            }
            estCodigo = estabelecimento.getEstCodigo();
        }

        // Carrega o orgão através de seu identificador
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String orgCodigo = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel)) {
            if (estCodigo != null) {
                final OrgaoTransferObject orgao = cseDelegate.findOrgaoByIdn(orgIdentificador, estCodigo, responsavel);
                if (orgao==null) {
                    throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
                }
                orgCodigo = orgao.getOrgCodigo();
                registroServidor.setOrgCodigo(orgCodigo);
            } else {
                final OrgaoTransferObject criterio = new OrgaoTransferObject();
                criterio.setOrgIdentificador(orgIdentificador);
                final List<TransferObject> orgaos = cseDelegate.lstOrgaos(criterio, responsavel);
                if ((orgaos == null) || (orgaos.isEmpty())) {
                    throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
                } else if (orgaos.size() > 1) {
                    throw new ZetraException("mensagem.erro.multiplos.orgaos.encontrados", responsavel);
                } else {
                    orgCodigo = (String) orgaos.get(0).getAttribute(Columns.ORG_CODIGO);
                    registroServidor.setOrgCodigo(orgCodigo);
                }
            }
        }


        // (`sbo_codigo`) REFERENCES `tb_sub_orgao`
        final String sboIdentificador = (String) parametros.get(SBO_IDENTIFICADOR);
        String sboCodigo = null;
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SUB_ORGAO, responsavel) && !TextHelper.isNull(sboIdentificador)) {
            final List<TransferObject> subOrgaos = serDelegate.findSubOrgaoByIdentificador(sboIdentificador, orgCodigo, responsavel);
            if ((subOrgaos == null) || (subOrgaos.isEmpty())) {
                throw new ZetraException("mensagem.erro.sub.orgao.nao.encontrado", responsavel);
            } else if (subOrgaos.size() > 1) {
                throw new ZetraException("mensagem.erro.multiplos.sub.orgaos.encontrados", responsavel);
            } else {
                sboCodigo = (String) subOrgaos.get(0).getAttribute(Columns.SBO_CODIGO);
                registroServidor.setSboCodigo(sboCodigo);
            }
        }

        // (`uni_codigo`) REFERENCES `tb_unidade`
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UNIDADE, responsavel)) {
            final String uniIdentificador = (String) parametros.get(UNI_IDENTIFICADOR);
            if (!TextHelper.isNull(uniIdentificador)) {
                final List<TransferObject> unidadeList = serDelegate.findUnidadeByIdentificador(uniIdentificador, sboCodigo, responsavel);
                if ((unidadeList == null) || (unidadeList.isEmpty())) {
                    throw new ZetraException("mensagem.erro.unidade.nao.encontrada", responsavel);
                } else if (unidadeList.size() > 1) {
                    throw new ZetraException("mensagem.erro.multiplas.unidades.encontradas", responsavel);
                } else {
                    registroServidor.setUniCodigo((String) unidadeList.get(0).getAttribute(Columns.UNI_CODIGO));
                }
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel)) {
            if (Boolean.TRUE.equals(parametros.get(RSE_CLT))) {
                registroServidor.setRseCLT("S");
            } else {
                registroServidor.setRseCLT("N");
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel)) {
            if (Boolean.TRUE.equals(parametros.get(RSE_ESTABILIZADO))) {
                registroServidor.setRseEstabilizado("S");
            } else {
                registroServidor.setRseEstabilizado("N");
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel)) {
            registroServidor.setRseMunicipioLotacao((String) parametros.get(RSE_MUNICIPIO_LOTACAO));
        }


        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel)) {
            final Object rsePrazo = parametros.get(RSE_PRAZO);
            if ((rsePrazo == null) || rsePrazo.equals(Integer.valueOf(-1))) {
                registroServidor.setRsePrazo(null);
            } else {
                registroServidor.setRsePrazo(Integer.valueOf(rsePrazo.toString()));
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel)) {
            final Object rseDataAdmissao = parametros.get(RSE_DATA_ADMISSAO);
            if (rseDataAdmissao != null) {
                registroServidor.setRseDataAdmissao(new java.sql.Timestamp(((Date) rseDataAdmissao).getTime()));
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel)) {
            registroServidor.setRseBancoSal((String) parametros.get(RSE_BANCO_SAL));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel)) {
            registroServidor.setRseAgenciaSal((String) parametros.get(RSE_AGENCIA_SAL));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel)) {
            registroServidor.setRseContaSal((String) parametros.get(RSE_CONTA_SAL));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel)) {
            registroServidor.setRseBancoSalAlternativo((String) parametros.get(RSE_BANCO_SAL_2));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel)) {
            registroServidor.setRseAgenciaSalAlternativa((String) parametros.get(RSE_AGENCIA_SAL_2));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel)) {
            registroServidor.setRseContaSalAlternativa((String) parametros.get(RSE_CONTA_SAL_2));
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel)) {
            final Object dataFimEngajamento = parametros.get(RSE_DATA_FIM_ENGAJAMENTO);
            if (dataFimEngajamento != null) {
                registroServidor.setRseDataFimEngajamento(new java.sql.Timestamp(((Date) dataFimEngajamento).getTime()));
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel)) {
            final Object dataLimitePermanencia = parametros.get(RSE_DATA_LIMITE_PERMANENCIA);
            if (dataLimitePermanencia != null) {
                registroServidor.setRseDataLimitePermanencia(new java.sql.Timestamp(((Date) dataLimitePermanencia).getTime()));
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel)) {
            registroServidor.setRseObs((String) parametros.get(RSE_OBS));
        }

        // Salva as praças do registro servidor
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel)) {
            String rsePraca = (String) parametros.get(RSE_PRACA);
            if (!TextHelper.isNull(rsePraca)) {
                rsePraca = rsePraca.replace("\r\n", ";").replace('\r', ';').replace('\n', ';').replace(',', ';').replace(";;", ";");
            }
            registroServidor.setRsePraca(rsePraca);
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) && !TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
            if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                try {
                    final long matricula = Long.parseLong(parametros.get(RSE_MATRICULA).toString());
                    if (matricula <= 0) {
                        throw new ZetraException("mensagem.erro.matricula.invalida", responsavel);
                    }
                    registroServidor.setRseMatricula(Long.toString(matricula));
                } catch (final NumberFormatException ex) {
                    throw new ZetraException("mensagem.erro.matricula.invalida", responsavel, ex);
                }
            } else {
                registroServidor.setRseMatricula(parametros.get(RSE_MATRICULA).toString());
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) &&
                (parametros.get(RSE_SALARIO) != null) && !((Double) parametros.get(RSE_SALARIO)).isNaN()) {
            registroServidor.setRseSalario(BigDecimal.valueOf((Double) parametros.get(RSE_SALARIO)));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) &&
                (parametros.get(RSE_PROVENTOS) != null) && !((Double) parametros.get(RSE_PROVENTOS)).isNaN()) {
            registroServidor.setRseProventos(BigDecimal.valueOf((Double) parametros.get(RSE_PROVENTOS)));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) &&
                (parametros.get(RSE_DESCONTOS_COMP) != null) && !((Double) parametros.get(RSE_DESCONTOS_COMP)).isNaN()) {
            registroServidor.setRseDescontosComp(BigDecimal.valueOf((Double) parametros.get(RSE_DESCONTOS_COMP)));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) &&
                (parametros.get(RSE_DESCONTOS_FACU) != null) && !((Double) parametros.get(RSE_DESCONTOS_FACU)).isNaN()) {
            registroServidor.setRseDescontosFacu(BigDecimal.valueOf((Double) parametros.get(RSE_DESCONTOS_FACU)));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) &&
                (parametros.get(RSE_OUTROS_DESCONTOS) != null) && !((Double) parametros.get(RSE_OUTROS_DESCONTOS)).isNaN()) {
            registroServidor.setRseOutrosDescontos(BigDecimal.valueOf((Double) parametros.get(RSE_OUTROS_DESCONTOS)));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) &&
                (parametros.get(RSE_BASE_CALCULO) != null) && !((Double) parametros.get(RSE_BASE_CALCULO)).isNaN()) {
            registroServidor.setRseBaseCalculo(BigDecimal.valueOf((Double) parametros.get(RSE_BASE_CALCULO)));
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel) && (parametros.get(RSE_DATA_CTC) != null)) {
            registroServidor.setRseDataContracheque(new java.sql.Date(((Date) parametros.get(RSE_DATA_CTC)).getTime()));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel)) {
            if (Boolean.TRUE.equals(parametros.get(RSE_ASSOCIADO))) {
                registroServidor.setRseAssociado("S");
            } else {
                registroServidor.setRseAssociado("N");
            }
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel)) {
            registroServidor.setRseMatriculaInst((String) parametros.get(RSE_MATRICULA_INST));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel)) {
            servidor.setSerUfNasc((String) parametros.get(SER_UF_NASC));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel)) {
            servidor.setSerCidNasc((String) parametros.get(SER_CID_NASC));
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel)) {
            servidor.setSerNomeConjuge((String) parametros.get(SER_NOME_CONJUGE));
        }

        //seta rse_margem e rse_margem_rest como 0 pois não podem ser null
        registroServidor.setRseMargem(BigDecimal.ZERO);
        registroServidor.setRseMargemRest(BigDecimal.ZERO);
        registroServidor.setRseMargemUsada(BigDecimal.ZERO);

        // Seta código do usuário que está alterando e a data de alteração
        registroServidor.setUsuCodigo(responsavel.getUsuCodigo());
        registroServidor.setRseDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

        final String serCodigo = serDelegate.cadastrarServidor(servidor, registroServidor, responsavel);

        final TransferObject registroServidorIncluido = serDelegate.getRegistroServidorPelaMatricula(serCodigo, registroServidor.getOrgCodigo(), null, registroServidor.getRseMatricula(), responsavel);
        final CustomTransferObject servidorIncluido = serDelegate.buscaServidor((String) registroServidorIncluido.getAttribute(Columns.RSE_CODIGO), serCodigo, responsavel);


        if (CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equals(parametros.get(OPERACAO))) {
            parametros.put(SERVIDOR_V8_0, servidorIncluido);
        } else {
            parametros.put(SERVIDOR_V4_0, servidorIncluido);
        }
    }
}