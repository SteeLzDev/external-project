package com.zetra.econsig.webservice.command.entrada.v4;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.LIMITE_RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_SOBRENOME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: PesquisarServidorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de pesquisar servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PesquisarServidorCommand extends RequisicaoExternaCommand {

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
        ServidorDelegate serDelegate = new ServidorDelegate();

        String serCpf = (String) parametros.get(SER_CPF);
        String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String rseMatricula = (String) parametros.get(RSE_MATRICULA);

        TransferObject criterios = new CustomTransferObject();
        if (! TextHelper.isNull(parametros.get(NOME))) {
            criterios.setAttribute("NOME", parametros.get(NOME));
        }

        if (! TextHelper.isNull(parametros.get(SER_SOBRENOME))) {
            criterios.setAttribute("SOBRENOME", parametros.get(SER_SOBRENOME));
        }

        if (! TextHelper.isNull(parametros.get(SER_DATA_NASC))) {
            criterios.setAttribute("serDataNascimento", parametros.get(SER_DATA_NASC));
        }
        criterios.setAttribute("responsavel", responsavel);

        List<TransferObject> servidores = new ArrayList<>();
        servidores =  serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), estIdentificador, orgIdentificador, rseMatricula, serCpf,
                                                                        0, (parametros.get(LIMITE_RESULTADO) == null) ? -1 : Integer.parseInt((String) parametros.get(LIMITE_RESULTADO)) , responsavel, true, null, false, null, criterios.getAtributos().isEmpty() ? null : criterios);

        parametros.put(SERVIDORES, servidores);

        if (servidores == null || servidores.isEmpty()) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }
}
