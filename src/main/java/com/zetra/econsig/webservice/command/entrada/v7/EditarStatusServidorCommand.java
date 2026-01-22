package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ULT_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PEDIDO_DEMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V7_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor;

/**
 * <p>Title: EditarStatusServidorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de editar status de servidor</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EditarStatusServidorCommand extends RequisicaoExternaCommand {

    public EditarStatusServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // Situação é obrigatório independente da configuração de campos
        final SituacaoServidor situacao = (SituacaoServidor) parametros.get(SITUACAO_SERVIDOR);
        if ((situacao == null) || (!situacao.getAtivo() && !situacao.getBloqueado() && !situacao.getExcluido() && !situacao.getFalecido() && !situacao.getPendente())) {
            throw new ZetraException("mensagem.informe.registro.servidor.situacao", responsavel);
        }

        // Se pesquisa pela matrícula, não precisa informar nome, sobrenome e data de nascimento, mesmo sendo obrigatórios na campo sistema
        final boolean pesquisaPelaMatricula = !TextHelper.isNull(parametros.get(RSE_MATRICULA)) && ShowFieldHelper.showField(FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA, responsavel);

        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_NOME, responsavel) && TextHelper.isNull(parametros.get(SER_PRIMEIRO_NOME)) && !pesquisaPelaMatricula) {
            throw new ZetraException("mensagem.informe.servidor.primeiro.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME, responsavel) && TextHelper.isNull(parametros.get(SER_ULTIMO_NOME)) && !pesquisaPelaMatricula) {
            throw new ZetraException("mensagem.informe.servidor.ultimo.nome", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel) && TextHelper.isNull(parametros.get(SER_CPF))) {
            throw new ZetraException("mensagem.informe.servidor.cpf", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC, responsavel) && TextHelper.isNull(parametros.get(SER_DATA_NASC)) && !pesquisaPelaMatricula) {
            throw new ZetraException("mensagem.informe.servidor.data.nascimento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel) && TextHelper.isNull(parametros.get(EST_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.estabelecimento", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel) && TextHelper.isNull(parametros.get(ORG_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.informe.registro.servidor.orgao", responsavel);
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA, responsavel) && TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
            throw new ZetraException("mensagem.informe.registro.servidor.matricula", responsavel);
        }

        if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel) && (situacao.getBloqueado() || situacao.getExcluido())) {
            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_SAIDA))) {
                throw new ZetraException("mensagem.erro.rse.informe.data.saida", responsavel);
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_ULT_SALARIO))) {
                throw new ZetraException("mensagem.erro.rse.informe.data.ult.salario", responsavel);
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_RETORNO)) && situacao.getBloqueado()) {
                throw new ZetraException("mensagem.erro.rse.informe.data.retorno", responsavel);
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel) && TextHelper.isNull(parametros.get(RSE_PEDIDO_DEMISSAO)) && situacao.getExcluido()) {
                throw new ZetraException("mensagem.erro.pedido.demissao.obrigatorio", responsavel);
            }
            if (TextHelper.isNull(parametros.get(TMO_IDENTIFICADOR))) {
                throw new ZetraException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }
            if (TextHelper.isNull(parametros.get(TMO_OBS))) {
                throw new ZetraException("mensagem.informe.oca.observacao", responsavel);
            }
        } else {
            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_SAIDA))) {
                throw new ZetraException("mensagem.erro.rse.informe.data.saida", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_ULT_SALARIO))) {
                throw new ZetraException("mensagem.erro.rse.informe.data.ult.salario", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel) && TextHelper.isNull(parametros.get(RSE_DATA_RETORNO)) && situacao.getBloqueado()) {
                throw new ZetraException("mensagem.erro.rse.informe.data.retorno", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel) && TextHelper.isNull(parametros.get(RSE_PEDIDO_DEMISSAO)) && situacao.getExcluido()) {
                throw new ZetraException("mensagem.erro.pedido.demissao.obrigatorio", responsavel);
            }
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel) && situacao.getAtivo() && (TextHelper.isNull(parametros.get(RSE_SALARIO)) || ((Double) parametros.get(RSE_SALARIO)).isNaN())) {
            throw new ZetraException("mensagem.informe.registro.servidor.salario", responsavel);
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel) && situacao.getAtivo() && (TextHelper.isNull(parametros.get(RSE_PROVENTOS)) || ((Double) parametros.get(RSE_PROVENTOS)).isNaN())) {
            throw new ZetraException("mensagem.informe.registro.servidor.proventos", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final ServidorDelegate serDelegate = new ServidorDelegate();

        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        final String rseMatricula = (String) parametros.get(RSE_MATRICULA);

        final String serCpf = (String) parametros.get(SER_CPF);
        final String primeiroNome = (String) parametros.get(SER_PRIMEIRO_NOME);
        final String ultimoNome = (String) parametros.get(SER_ULTIMO_NOME);

        final TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("NOME", primeiroNome);
        criterios.setAttribute("SOBRENOME", ultimoNome);

        if (!TextHelper.isNull(parametros.get(SER_DATA_NASC))) {
            final Date serDataNascimento = (Date) parametros.get(SER_DATA_NASC);
            criterios.setAttribute("serDataNascimento", serDataNascimento);
        }

        final List<TransferObject> lstServidores = serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), estIdentificador, orgIdentificador, rseMatricula, serCpf, -1, -1, responsavel, false, null, false, null, criterios);

        if ((lstServidores == null) || lstServidores.isEmpty()) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        } else if (lstServidores.size() > 1) {
            parametros.put(SERVIDORES, lstServidores);

            throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
        } else {
            final TransferObject servidor = lstServidores.get(0);
            final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);

            final RegistroServidorTO registroServidor = serDelegate.findRegistroServidor(rseCodigo, responsavel);

            // Valores a serem atualizados
            final SituacaoServidor situacao = (SituacaoServidor) parametros.get(SITUACAO_SERVIDOR);
            final Date dataSaida = (Date) parametros.get(RSE_DATA_SAIDA);
            final Date dataUltSalario = (Date) parametros.get(RSE_DATA_ULT_SALARIO);
            final Date dataRetorno = (Date) parametros.get(RSE_DATA_RETORNO);
            final Boolean pedidoDemissao = (Boolean) parametros.get(RSE_PEDIDO_DEMISSAO);
            final Double salario = (Double) parametros.get(RSE_SALARIO);
            final Double proventos = (Double) parametros.get(RSE_PROVENTOS);

            if (situacao != null) {
                if (Boolean.TRUE.equals(situacao.getAtivo())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
                } else if (Boolean.TRUE.equals(situacao.getBloqueado())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_BLOQUEADO);
                } else if (Boolean.TRUE.equals(situacao.getExcluido())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
                } else if (Boolean.TRUE.equals(situacao.getFalecido())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_FALECIDO);
                } else if (Boolean.TRUE.equals(situacao.getPendente())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_PENDENTE);
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel)) {
                registroServidor.setRseDataSaida(dataSaida);
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel)) {
                registroServidor.setRseDataUltSalario(dataUltSalario);
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel)) {
                registroServidor.setRseDataRetorno(dataRetorno);
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel)) {
                registroServidor.setRsePedidoDemissao(pedidoDemissao != null ? (pedidoDemissao ? CodedValues.TPC_SIM : CodedValues.TPC_NAO) : null);
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel)) {
                registroServidor.setRseSalario((salario != null) && !salario.isNaN() ? new BigDecimal(salario) : null);
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel)) {
                registroServidor.setRseProventos((proventos != null) && !proventos.isNaN() ? new BigDecimal(proventos) : null);
            }

            serDelegate.updateRegistroServidor(registroServidor, null, false, false, false, responsavel);
            parametros.put(SERVIDOR_V7_0, serDelegate.buscaServidor(rseCodigo, responsavel));
        }
    }
}
