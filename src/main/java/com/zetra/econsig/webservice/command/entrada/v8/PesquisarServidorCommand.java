package com.zetra.econsig.webservice.command.entrada.v8;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_ADICIONAIS;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.LIMITE_RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.NOME;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_SOBRENOME;
import static com.zetra.econsig.webservice.CamposAPI.STATUS;
import static com.zetra.econsig.webservice.CamposAPI.TEM_CONTRATO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

public class PesquisarServidorCommand extends RequisicaoExternaCommand{
    public PesquisarServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if(TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
            if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_NOME, responsavel)
                    && TextHelper.isNull(parametros.get(NOME))) {
                throw new ZetraException("mensagem.erro.campo.nome.obrigatorio", responsavel);
            } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME, responsavel)
                    && TextHelper.isNull(parametros.get(SER_SOBRENOME))) {
                throw new ZetraException("mensagem.erro.campo.sobrenome.obrigatorio", responsavel);
            } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel)
                    && TextHelper.isNull(parametros.get(SER_CPF))) {
                throw new ZetraException("mensagem.erro.campo.cpf.obrigatorio", responsavel);
            } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC, responsavel)
                    && TextHelper.isNull(parametros.get(SER_DATA_NASC))) {
                throw new ZetraException("mensagem.erro.campo.data.nascimento.obrigatorio", responsavel);
            } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel)
                    && TextHelper.isNull(parametros.get(EST_IDENTIFICADOR))) {
                throw new ZetraException("mensagem.erro.campo.est.identificador.obrigatorio", responsavel);
            } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel)
                    && TextHelper.isNull(parametros.get(ORG_IDENTIFICADOR))) {
                throw new ZetraException("mensagem.erro.campo.org.identificador.obrigatorio", responsavel);
            }  else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA, responsavel)
                    && TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
                throw new ZetraException("mensagem.erro.campo.matricula.obrigatorio", responsavel);
            }
        } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel)
                && TextHelper.isNull(parametros.get(SER_CPF))) {
            throw new ZetraException("mensagem.erro.campo.cpf.obrigatorio", responsavel);
        } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel)
                && TextHelper.isNull(parametros.get(EST_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.erro.campo.est.identificador.obrigatorio", responsavel);
        } else if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel)
                && TextHelper.isNull(parametros.get(ORG_IDENTIFICADOR))) {
            throw new ZetraException("mensagem.erro.campo.org.identificador.obrigatorio", responsavel);
        }


    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final ServidorDelegate serDelegate = new ServidorDelegate();

        final String serCpf = (String) parametros.get(SER_CPF);
        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        final String rseMatricula = (String) parametros.get(RSE_MATRICULA);

        final TransferObject criterios = new CustomTransferObject();
        if (! TextHelper.isNull(parametros.get(NOME))) {
            criterios.setAttribute("NOME", parametros.get(NOME));
        }

        if (! TextHelper.isNull(parametros.get(SER_SOBRENOME))) {
            criterios.setAttribute("SOBRENOME", parametros.get(SER_SOBRENOME));
        }

        if (! TextHelper.isNull(parametros.get(SER_DATA_NASC))) {
            criterios.setAttribute("serDataNascimento", parametros.get(SER_DATA_NASC));
        }

        if (! TextHelper.isNull(parametros.get(TEM_CONTRATO))) {
            criterios.setAttribute("temContrato", parametros.get(TEM_CONTRATO));
        }

        if (! TextHelper.isNull(parametros.get(RSE_TIPO))) {
            criterios.setAttribute("categoria", parametros.get(RSE_TIPO));
        }

        if (! TextHelper.isNull(parametros.get(STATUS))) {
            criterios.setAttribute("status", parametros.get(STATUS));
        }

        if (! TextHelper.isNull(parametros.get(OPERACAO))) {
            criterios.setAttribute("operacao", parametros.get(OPERACAO));
        }
        criterios.setAttribute("responsavel", responsavel);

        List<TransferObject> servidores = new ArrayList<>();
        servidores =  serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), estIdentificador, orgIdentificador, rseMatricula, serCpf,
                                                                        0, (parametros.get(LIMITE_RESULTADO) == null) ? -1 : Integer.parseInt((String) parametros.get(LIMITE_RESULTADO)) , responsavel, true, null, false, null, criterios.getAtributos().isEmpty() ? null : criterios);
        parametros.put(SERVIDORES, servidores);

        if ((servidores == null) || servidores.isEmpty()) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        adicionarDadosServidor(parametros, responsavel);
    }

    private void adicionarDadosServidor(Map<CamposAPI, Object> retorno, AcessoSistema responsavel) throws ServidorControllerException {
        final List<TransferObject> servidores = (List<TransferObject>) parametros.get(SERVIDORES);
        final Map<String, List<TransferObject>> mapDadosAdicionaisServidor = new HashMap<>();
        for (final TransferObject servidor : servidores) {
            final String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
            if (!TextHelper.isNull(serCodigo)) {
                final ServidorDelegate serDelegate = new ServidorDelegate();
                final List<TransferObject> listaDadosAdicionaisServidor = serDelegate.lstDadosServidor(AcaoTipoDadoAdicionalEnum.CONSULTA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST, serCodigo, responsavel);
                mapDadosAdicionaisServidor.put(serCodigo, listaDadosAdicionaisServidor);
            }
        }

        if (!mapDadosAdicionaisServidor.isEmpty()) {
            retorno.put(DADOS_ADICIONAIS, mapDadosAdicionaisServidor);
        }
    }

}