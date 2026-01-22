package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.values.CodedValues.TPA_SIM;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.MARGENS;
import static com.zetra.econsig.webservice.CamposAPI.MATRICULA_MULTIPLA;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_INELEGIVEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SRS_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.VALORES_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_LIMITE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleTokenAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoMotivoBloqueioEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarMargemCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar margem</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarMargemCommand extends RequisicaoExternaCommand {

    private String mensagemMargemDisponivel = "";

    public ConsultarMargemCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaPermiteCsaConsultarMargem(parametros);
        validaValorAutorizacao(parametros);
        validaCodigoVerba(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);

        final boolean multiploSer = (!TextHelper.isNull(parametros.get(MATRICULA_MULTIPLA)) ? Boolean.parseBoolean(parametros.get(MATRICULA_MULTIPLA).toString()) : false);

        // Se é consulta múltipla de servidor
        final List<TransferObject> servidores = multiploSer ? (List<TransferObject>) parametros.get(SERVIDORES) : null;
        if (multiploSer && (servidores != null) && !servidores.isEmpty()) {
            int contaServidoresInelegiveis = 0;

            final List<TransferObject> elegiveis = new ArrayList<>();

            // Pesquisa a margem de todos os servidores encontrados
            for (final TransferObject servidor : servidores) {
                final Map<CamposAPI, Object> consulta = consultaMargemMultipla(servidor.getAttribute(Columns.RSE_CODIGO).toString(), parametros);

                if (consulta.containsKey(SERVIDOR_INELEGIVEL)) {
                    contaServidoresInelegiveis++;
                } else {
                    elegiveis.add(servidor);
                    parametros.put(SERVIDORES, elegiveis);
                    setParametros(consulta, parametros, true);
                }
            }

            if (contaServidoresInelegiveis == servidores.size()) {
                parametros.remove(SERVIDORES);
                throw new ZetraException("mensagem.servidor.inelegivel.soap", responsavel);
            }
        } else // Consulta de margem via XML sempre é consulta única
        if ((rseCodigo != null) && !"".equals(rseCodigo)) {
            final Map<CamposAPI, Object> consulta = consultaMargemUnica(parametros);
            setParametros(consulta, parametros, false);
        } else {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }

    protected Map<CamposAPI, Object> consultaMargemUnica(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Map<CamposAPI, Object> retorno = new EnumMap<>(CamposAPI.class);

        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final String svcCodigo = (String) parametros.get(SVC_CODIGO);
        final String serSenha = (String) parametros.get(SER_SENHA);
        final String loginExterno = (String) parametros.get(SER_LOGIN);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String token = (String) parametros.get(TOKEN);
        final Object rseMatricula = parametros.get(RSE_MATRICULA);
        final Object serCpf = parametros.get(SER_CPF);
        final Object orgIdentificador = parametros.get(ORG_IDENTIFICADOR);
        final Object estIdentificador = parametros.get(EST_IDENTIFICADOR);
        final Object adeVlr = parametros.get(ADE_VLR);

        if ((rseCodigo != null) && !"".equals(rseCodigo)) {
            boolean serSenhaObrConsultaMargem = false;
            try {
                serSenhaObrConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);
            } catch (final ParametroControllerException ex) {
                throw ex;
            }

            // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
            if (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
                final String tpaCodigo = responsavel.isCsa() ? CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_CSA : CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_COR;
                try {
                    final String exigeSenha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                    if (TextHelper.isNull(exigeSenha) || TPA_SIM.equalsIgnoreCase(exigeSenha)) {
                        serSenhaObrConsultaMargem = true;
                    } else {
                        serSenhaObrConsultaMargem = false;
                    }
                } catch (final ParametroControllerException ex) {
                    serSenhaObrConsultaMargem = true;
                }
            }

            if (serSenhaObrConsultaMargem && responsavel.isCsa()) {
                serSenhaObrConsultaMargem = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, null, serSenhaObrConsultaMargem, null, responsavel);
            }

            if (serSenhaObrConsultaMargem) {
                validaPresencaSenhaServidor(parametros);
                validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
            } else if ((!TextHelper.isNull(serSenha) || !TextHelper.isNull(token))) {
                final String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
                final boolean csaValidaSenha = (!TextHelper.isNull(tpaCsaValidaSenha) && !CodedValues.TPA_NAO.equals(tpaCsaValidaSenha));

                if (csaValidaSenha) {
                    validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
                }
            }

            try {
                final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);

                final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

                retorno.put(RSE_CODIGO, rseCodigo);
                retorno.put(SER_CODIGO, servidor.getAttribute(Columns.SER_CODIGO));
                retorno.put(ORGAO, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
                retorno.put(ORG_NOME, servidor.getAttribute(Columns.ORG_NOME));
                retorno.put(ESTABELECIMENTO, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
                retorno.put(EST_NOME, servidor.getAttribute(Columns.EST_NOME));
                retorno.put(RSE_TIPO, servidor.getAttribute(Columns.RSE_TIPO));
                retorno.put(SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
                retorno.put(SER_CPF, servidor.getAttribute(Columns.SER_CPF));
                retorno.put(DATA_NASCIMENTO, servidor.getAttribute(Columns.SER_DATA_NASC));
                retorno.put(RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
                retorno.put(RSE_DATA_ADMISSAO, servidor.getAttribute(Columns.RSE_DATA_ADMISSAO));
                retorno.put(RSE_PRAZO, servidor.getAttribute(Columns.RSE_PRAZO));
                retorno.put(SRS_CODIGO, servidor.getAttribute(Columns.SRS_CODIGO));

                retorno.put(SER_NRO_IDT, servidor.getAttribute(Columns.SER_NRO_IDT));
                retorno.put(SER_DATA_IDT, servidor.getAttribute(Columns.SER_DATA_IDT));
                retorno.put(SER_UF_IDT, servidor.getAttribute(Columns.SER_UF_IDT));
                retorno.put(SER_EMISSOR_IDT, servidor.getAttribute(Columns.SER_EMISSOR_IDT));
                retorno.put(SER_CID_NASC, servidor.getAttribute(Columns.SER_CID_NASC));
                retorno.put(SER_NACIONALIDADE, servidor.getAttribute(Columns.SER_NACIONALIDADE));
                retorno.put(SER_SEXO, servidor.getAttribute(Columns.SER_SEXO));

                // Pega a descrição do codigo de estado civil
                final String estadoCivilCodigo = (String) servidor.getAttribute(Columns.SER_EST_CIVIL);
                if (!TextHelper.isNull(estadoCivilCodigo)) {
                    final String serEstCivil = servidorController.getEstCivil(estadoCivilCodigo, responsavel);
                    retorno.put(SER_EST_CIVIL, serEstCivil);
                }

                retorno.put(SER_END, servidor.getAttribute(Columns.SER_END));
                retorno.put(SER_NRO, servidor.getAttribute(Columns.SER_NRO));
                retorno.put(SER_COMPL, servidor.getAttribute(Columns.SER_COMPL));
                retorno.put(SER_BAIRRO, servidor.getAttribute(Columns.SER_BAIRRO));
                retorno.put(SER_CIDADE, servidor.getAttribute(Columns.SER_CIDADE));
                retorno.put(SER_UF, servidor.getAttribute(Columns.SER_UF));
                retorno.put(SER_CEP, servidor.getAttribute(Columns.SER_CEP));
                retorno.put(SER_TEL, servidor.getAttribute(Columns.SER_TEL));
                retorno.put(SER_CELULAR, servidor.getAttribute(Columns.SER_CELULAR));

                retorno.put(RSE_SALARIO, servidor.getAttribute(Columns.RSE_SALARIO));
                retorno.put(RSE_PROVENTOS, servidor.getAttribute(Columns.RSE_PROVENTOS));
                retorno.put(RSE_BANCO, servidor.getAttribute(Columns.RSE_BANCO_SAL));
                retorno.put(RSE_AGENCIA, servidor.getAttribute(Columns.RSE_AGENCIA_SAL));
                retorno.put(RSE_CONTA, servidor.getAttribute(Columns.RSE_CONTA_SAL));
                retorno.put(RSE_DATA_SAIDA, servidor.getAttribute(Columns.RSE_DATA_SAIDA));
                retorno.put(CARGO_CODIGO, servidor.getAttribute(Columns.CRS_CODIGO));
                retorno.put(CARGO_DESCRICAO, servidor.getAttribute(Columns.CRS_DESCRICAO));
                retorno.put(POSTO_CODIGO, servidor.getAttribute(Columns.POS_CODIGO));
                retorno.put(POSTO_DESCRICAO, servidor.getAttribute(Columns.POS_DESCRICAO));

                if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_THA_CODIGO))) {
                    retorno.put(HABITACAO_CODIGO, servidor.getAttribute(Columns.SER_THA_CODIGO));
                    retorno.put(HABITACAO_DESCRICAO, servidorController.getTipoHabitacao((String) servidor.getAttribute(Columns.SER_THA_CODIGO), responsavel));
                }
                if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_NES_CODIGO))) {
                    retorno.put(ESCOLARIDADE_CODIGO, servidor.getAttribute(Columns.SER_NES_CODIGO));
                    retorno.put(ESCOLARIDADE_DESCRICAO, servidorController.getNivelEscolaridade((String) servidor.getAttribute(Columns.SER_NES_CODIGO), responsavel));
                }

                retorno.put(SER_QTD_FILHOS, servidor.getAttribute(Columns.SER_QTD_FILHOS));

                // Recupera margem limite por consignatária
                final BigDecimal margemLimite = getMargemLimite(rseCodigo, csaCodigo);
                retorno.put(VALOR_MARGEM_LIMITE, margemLimite);

                final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
                final boolean exibeMargemViaToken = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_INT_XML_TOKEN_SER, responsavel);
                final Map<String, Object> valoresMargem = new HashMap<>();
                List<MargemTO> margens = null;
                if (exibeMargemViaToken && !TextHelper.isNull(token)) {
                    // Se exibe a margem independente de permissão do usuário e dos parâmetros de exibição de margem na consulta de margem via token,
                    // consulta a margem com a permissão do usuário servidor.
                    final AcessoSistema responsavelSer = recuperaAcessoViaToken(rseCodigo, rseMatricula, serCpf, csaCodigo, orgIdentificador, estIdentificador, token);
                    margens = consultarMargemController.consultarMargem(rseCodigo, (BigDecimal) adeVlr, svcCodigo, csaCodigo, true, serSenha, true, null, responsavelSer);
                } else {
                    margens = consultarMargemController.consultarMargem(rseCodigo, (BigDecimal) adeVlr, svcCodigo, csaCodigo, true, serSenha, true, null, responsavel);
                }

                boolean temAlgumaMargemDisponivel = false;
                for (final MargemTO margem : margens) {
                    if (margem.getMarDescricao() != null) {
                        String mensagem = "";
                        if (margem.getMrsMargemRest() == null) {
                            mensagem = margem.getObservacao();

                            // Margens que não estão disponíveis para visualização
                            valoresMargem.put("TEXTO_MARGEM_" + margem.getMarCodigo(), mensagem);
                            valoresMargem.put("VALOR_MARGEM_" + margem.getMarCodigo(), "");

                            if (margem.temMargemDisponivel()) {
                                temAlgumaMargemDisponivel = true;
                            }

                        } else {
                            mensagem = (!TextHelper.isNull(margem.getObservacao()) ? margem.getObservacao() + " " : "");

                            if (((BigDecimal) adeVlr).compareTo(margem.getMrsMargemRest()) <= 0) {
                                temAlgumaMargemDisponivel = true;
                                mensagem += ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMenorMargemDisponivel", responsavel) + ": " + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                            } else {
                                mensagem += ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMaiorMargemDisponivel", responsavel) + ": " + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                            }

                            // Margens que estão disponíveis para visualização
                            valoresMargem.put("TEXTO_MARGEM_" + margem.getMarCodigo(), mensagem);
                            valoresMargem.put("VALOR_MARGEM_" + margem.getMarCodigo(), NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang()));
                        }
                        margem.setMargemLimite(margemLimite);
                        margem.setObservacao(mensagem);
                    }
                }

                // Se tem alguma margem disponível (caso a consignatária possa visualizar mais de uma margem)
                // informa código de retorno de sucesso, e não erro, mesmo que as demais margens não tenham valor disponível.
                if (temAlgumaMargemDisponivel) {
                    retorno.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMenorMargemDisponivel" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
                } else {
                    retorno.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMaiorMargemDisponivel" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
                }

                // Se tem apenas uma margem visível, o campo será MENSAGEM / VALOR_MARGEM por questão de compatibilidade.
                // Se houver mais de uma, os campos serão TEXTO_MARGEM_1 / VALOR_MARGEM_1, TEXTO_MARGEM_2 / VALOR_MARGEM_2 ...
                if (margens.size() == 1) {
                    final MargemTO margem = margens.get(0);
                    retorno.put(MENSAGEM, valoresMargem.get("TEXTO_MARGEM_" + margem.getMarCodigo()));
                    retorno.put(VALOR_MARGEM, valoresMargem.get("VALOR_MARGEM_" + margem.getMarCodigo()));
                } else {
                    retorno.put(VALORES_MARGEM, valoresMargem);
                }

                retorno.put(MARGEM_SERVIDOR, margens);

                // Incrementa a quantidade de vezes que o token foi utilizado para consultar margem
                if (!TextHelper.isNull(token)) {
                    ControleTokenAcesso.getInstance().incrementarUtilizacaoToken(token);
                }

                return retorno;

            } catch (final ServidorControllerException ex) {
                throw ex;
            }

        } else {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }

    protected Map<CamposAPI, Object> consultaMargemMultipla(String rseCodigo, Map<CamposAPI, Object> parametros) throws ParametroControllerException, ZetraException, ServidorControllerException {
        final Map<CamposAPI, Object> retorno = new EnumMap<>(CamposAPI.class);

        final String svcCodigo = (String) parametros.get(SVC_CODIGO);
        final String serSenha = (String) parametros.get(SER_SENHA);
        final String loginExterno = (String) parametros.get(SER_LOGIN);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String token = (String) parametros.get(TOKEN);
        final Object rseMatricula = parametros.get(RSE_MATRICULA);
        final Object serCpf = parametros.get(SER_CPF);
        final Object orgIdentificador = parametros.get(ORG_IDENTIFICADOR);
        final Object estIdentificador = parametros.get(EST_IDENTIFICADOR);
        final Object adeVlr = parametros.get(ADE_VLR);

        boolean serSenhaObrConsultaMargem = false;
        try {
            serSenhaObrConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            throw ex;
        }

        // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
            final String tpaCodigo = responsavel.isCsa() ? CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_CSA : CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_COR;
            try {
                final String exigeSenha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                if (TextHelper.isNull(exigeSenha) || TPA_SIM.equalsIgnoreCase(exigeSenha)) {
                    serSenhaObrConsultaMargem = true;
                } else {
                    serSenhaObrConsultaMargem = false;
                }
            } catch (final ParametroControllerException ex) {
                serSenhaObrConsultaMargem = true;
            }
        }

        /**
         * Se é consulta múltipla e exige senha, retorna que múltiplos servidores foram encontrados.
         * Não podemos garantir que todos os servidores possuem a mesma senha.
         */
        if (serSenhaObrConsultaMargem) {
            throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
        }
        try {
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);

            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            retorno.put(RSE_CODIGO, rseCodigo);
            retorno.put(SER_CODIGO, servidor.getAttribute(Columns.SER_CODIGO));
            retorno.put(SVC_CODIGO, svcCodigo);
            retorno.put(SER_SENHA, serSenha);
            retorno.put(SER_LOGIN, loginExterno);
            retorno.put(CSA_CODIGO, csaCodigo);
            retorno.put(TOKEN, token);
            retorno.put(RSE_MATRICULA, rseMatricula);
            retorno.put(SER_CPF, serCpf);
            retorno.put(ADE_VLR, adeVlr);
            retorno.put(ORG_IDENTIFICADOR, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
            retorno.put(ORGAO, servidor.getAttribute(Columns.ORG_NOME));
            retorno.put(EST_IDENTIFICADOR, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
            retorno.put(ESTABELECIMENTO, servidor.getAttribute(Columns.EST_NOME));
            retorno.put(RSE_TIPO, servidor.getAttribute(Columns.RSE_TIPO));
            retorno.put(SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
            retorno.put(SER_CPF, servidor.getAttribute(Columns.SER_CPF));
            retorno.put(DATA_NASCIMENTO, servidor.getAttribute(Columns.SER_DATA_NASC));
            retorno.put(RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
            retorno.put(RSE_DATA_ADMISSAO, servidor.getAttribute(Columns.RSE_DATA_ADMISSAO));
            retorno.put(RSE_PRAZO, servidor.getAttribute(Columns.RSE_PRAZO));
            retorno.put(SRS_CODIGO, servidor.getAttribute(Columns.SRS_CODIGO));

            retorno.put(SER_NRO_IDT, servidor.getAttribute(Columns.SER_NRO_IDT));
            retorno.put(SER_DATA_IDT, servidor.getAttribute(Columns.SER_DATA_IDT));
            retorno.put(SER_UF_IDT, servidor.getAttribute(Columns.SER_UF_IDT));
            retorno.put(SER_EMISSOR_IDT, servidor.getAttribute(Columns.SER_EMISSOR_IDT));
            retorno.put(SER_CID_NASC, servidor.getAttribute(Columns.SER_CID_NASC));
            retorno.put(SER_NACIONALIDADE, servidor.getAttribute(Columns.SER_NACIONALIDADE));
            retorno.put(SER_SEXO, servidor.getAttribute(Columns.SER_SEXO));

            // Pega a descrição do codigo de estado civil
            final String estadoCivilCodigo = (String) servidor.getAttribute(Columns.SER_EST_CIVIL);
            if (!TextHelper.isNull(estadoCivilCodigo)) {
                final String serEstCivil = servidorController.getEstCivil(estadoCivilCodigo, responsavel);
                retorno.put(SER_EST_CIVIL, serEstCivil);
            }

            retorno.put(SER_END, servidor.getAttribute(Columns.SER_END));
            retorno.put(SER_NRO, servidor.getAttribute(Columns.SER_NRO));
            retorno.put(SER_COMPL, servidor.getAttribute(Columns.SER_COMPL));
            retorno.put(SER_BAIRRO, servidor.getAttribute(Columns.SER_BAIRRO));
            retorno.put(SER_CIDADE, servidor.getAttribute(Columns.SER_CIDADE));
            retorno.put(SER_UF, servidor.getAttribute(Columns.SER_UF));
            retorno.put(SER_CEP, servidor.getAttribute(Columns.SER_CEP));
            retorno.put(SER_TEL, servidor.getAttribute(Columns.SER_TEL));
            retorno.put(SER_CELULAR, servidor.getAttribute(Columns.SER_CELULAR));

            retorno.put(RSE_SALARIO, servidor.getAttribute(Columns.RSE_SALARIO));
            retorno.put(RSE_PROVENTOS, servidor.getAttribute(Columns.RSE_PROVENTOS));
            retorno.put(RSE_BANCO, servidor.getAttribute(Columns.RSE_BANCO_SAL));
            retorno.put(RSE_AGENCIA, servidor.getAttribute(Columns.RSE_AGENCIA_SAL));
            retorno.put(RSE_CONTA, servidor.getAttribute(Columns.RSE_CONTA_SAL));
            retorno.put(RSE_DATA_SAIDA, servidor.getAttribute(Columns.RSE_DATA_SAIDA));
            retorno.put(CARGO_CODIGO, servidor.getAttribute(Columns.CRS_CODIGO));
            retorno.put(CARGO_DESCRICAO, servidor.getAttribute(Columns.CRS_DESCRICAO));
            retorno.put(POSTO_CODIGO, servidor.getAttribute(Columns.POS_CODIGO));
            retorno.put(POSTO_DESCRICAO, servidor.getAttribute(Columns.POS_DESCRICAO));

            if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_THA_CODIGO))) {
                retorno.put(HABITACAO_CODIGO, servidor.getAttribute(Columns.SER_THA_CODIGO));
                retorno.put(HABITACAO_DESCRICAO, servidorController.getTipoHabitacao((String) servidor.getAttribute(Columns.SER_THA_CODIGO), responsavel));
            }
            if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_NES_CODIGO))) {
                retorno.put(ESCOLARIDADE_CODIGO, servidor.getAttribute(Columns.SER_NES_CODIGO));
                retorno.put(ESCOLARIDADE_DESCRICAO, servidorController.getNivelEscolaridade((String) servidor.getAttribute(Columns.SER_NES_CODIGO), responsavel));
            }

            retorno.put(SER_QTD_FILHOS, servidor.getAttribute(Columns.SER_QTD_FILHOS));

            // Recupera margem limite por consignatária
            final BigDecimal margemLimite = getMargemLimite(rseCodigo, csaCodigo);
            retorno.put(VALOR_MARGEM_LIMITE, margemLimite);

            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            final boolean exibeMargemViaToken = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_INT_XML_TOKEN_SER, responsavel);
            final Map<String, Object> valoresMargem = new HashMap<>();
            List<MargemTO> margens = null;
            if (exibeMargemViaToken && !TextHelper.isNull(token)) {
                // Se exibe a margem independente de permissão do usuário e dos parâmetros de exibição de margem na consulta de margem via token,
                // consulta a margem com a permissão do usuário servidor.
                final AcessoSistema responsavelSer = recuperaAcessoViaToken(rseCodigo, rseMatricula, serCpf, csaCodigo, orgIdentificador, estIdentificador, token);
                margens = consultarMargemController.consultarMargem(rseCodigo, (BigDecimal) adeVlr, svcCodigo, csaCodigo, true, serSenha, true, null, responsavelSer);
            } else {
                margens = consultarMargemController.consultarMargem(rseCodigo, (BigDecimal) adeVlr, svcCodigo, csaCodigo, true, serSenha, true, null, responsavel);
            }

            boolean temAlgumaMargemDisponivel = false;
            for (final MargemTO margem : margens) {
                if (margem.getMarDescricao() != null) {
                    String mensagem = "";
                    if (margem.getMrsMargemRest() == null) {
                        mensagem = margem.getObservacao();

                        // Margens que não estão disponíveis para visualização
                        valoresMargem.put("TEXTO_MARGEM_" + margem.getMarCodigo(), mensagem);
                        valoresMargem.put("VALOR_MARGEM_" + margem.getMarCodigo(), "");

                        if (margem.temMargemDisponivel()) {
                            temAlgumaMargemDisponivel = true;
                        }

                    } else {
                        if (((BigDecimal) adeVlr).compareTo(margem.getMrsMargemRest()) <= 0) {
                            temAlgumaMargemDisponivel = true;
                            mensagem = ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMenorMargemDisponivel", responsavel) + ": " + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                            if (TextHelper.isNull(mensagemMargemDisponivel)) {
                                mensagemMargemDisponivel = mensagem;
                            }
                        } else {
                            mensagem = ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMaiorMargemDisponivel", responsavel) + ": " + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                        }

                        // Margens que estão disponíveis para visualização
                        valoresMargem.put("TEXTO_MARGEM_" + margem.getMarCodigo(), mensagem);
                        valoresMargem.put("VALOR_MARGEM_" + margem.getMarCodigo(), NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang()));
                    }
                    margem.setMargemLimite(margemLimite);
                    margem.addObservacao(mensagem);
                }
            }

            // Se tem alguma margem disponível (caso a consignatária possa visualizar mais de uma margem)
            // informa código de retorno de sucesso, e não erro, mesmo que as demais margens não tenham valor disponível.
            if (temAlgumaMargemDisponivel) {
                retorno.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMenorMargemDisponivel" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
            } else if (parametros.get(COD_RETORNO) == null) {
                retorno.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMaiorMargemDisponivel" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
            }

            // Se tem apenas uma margem visível, o campo será MENSAGEM / VALOR_MARGEM por questão de compatibilidade.
            // Se houver mais de uma, os campos serão TEXTO_MARGEM_1 / VALOR_MARGEM_1, TEXTO_MARGEM_2 / VALOR_MARGEM_2 ...
            if (margens.size() == 1) {
                final MargemTO margem = margens.get(0);
                retorno.put(MENSAGEM, valoresMargem.get("TEXTO_MARGEM_" + margem.getMarCodigo()));
                retorno.put(VALOR_MARGEM, valoresMargem.get("VALOR_MARGEM_" + margem.getMarCodigo()));
            } else {
                retorno.put(VALORES_MARGEM, valoresMargem);
            }
            retorno.put(MARGEM_SERVIDOR, margens);

            if (!TextHelper.isNull(mensagemMargemDisponivel)) {
                retorno.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.valorParcelaMenorMargemDisponivel" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
                retorno.put(MENSAGEM, mensagemMargemDisponivel);
            }

            // Incrementa a quantidade de vezes que o token foi utilizado para consultar margem
            if (!TextHelper.isNull(token)) {
                ControleTokenAcesso.getInstance().incrementarUtilizacaoToken(token);
            }

            return retorno;

        } catch (final ServidorControllerException ex) {
            if ("mensagem.vinculoNaoPermiteConsultarMargem.vinculoCsa".equals(ex.getMessageKey()) || "mensagem.vinculoNaoPermiteConsultarMargem".equals(ex.getMessageKey())) {
                retorno.put(SERVIDOR_INELEGIVEL, true);

                // Incrementa a quantidade de vezes que o token foi utilizado para consultar margem
                if (!TextHelper.isNull(token)) {
                    ControleTokenAcesso.getInstance().incrementarUtilizacaoToken(token);
                }

                return retorno;
            } else {
                throw ex;
            }
        }
    }

    /**
     * Recupera o AcessoSistema do servidor de acordo com o token de acesso passado.
     *
     * @param rseCodigo
     * @param rseMatricula
     * @param serCpf
     * @param csaCodigo
     * @param orgIdentificador
     * @param estIdentificador
     * @param token
     * @return
     * @throws ZetraException
     */
    protected AcessoSistema recuperaAcessoViaToken(String rseCodigo, Object rseMatricula, Object serCpf, String csaCodigo, Object orgIdentificador, Object estIdentificador, String token) throws ZetraException {
        AcessoSistema responsavelSer = null;
        List<TransferObject> lista = null;
        try {
            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            lista = usuDelegate.lstUsuariosSer((String) serCpf, (String) rseMatricula, (String) estIdentificador, (String) orgIdentificador, responsavel);
        } catch (final UsuarioControllerException e) {
            lista = new ArrayList<>();
        }

        if ((lista == null) || lista.isEmpty()) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        boolean achou = false;
        for (final TransferObject to : lista) {
            final String codigo = to.getAttribute(Columns.RSE_CODIGO).toString();
            final String usuario = to.getAttribute(Columns.USU_CODIGO).toString();
            if (codigo.equals(rseCodigo)) {
                try {
                    validaTokenAcesso(codigo, csaCodigo, token);
                    responsavelSer = new AcessoSistema(usuario);
                    responsavelSer.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
                    achou = true;
                } catch (final ZetraException e) {
                    continue;
                }
            }
        }

        if (!achou) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        return responsavelSer;
    }

    protected void setParametros(Map<CamposAPI, Object> servidor, Map<CamposAPI, Object> parametros, boolean multipla) {
        parametros.put(RSE_CODIGO, servidor.get(RSE_CODIGO));
        parametros.put(ORGAO, servidor.get(ORGAO));
        parametros.put(ORG_NOME, servidor.get(ORG_NOME));
        parametros.put(ESTABELECIMENTO, servidor.get(ESTABELECIMENTO));
        parametros.put(EST_NOME, servidor.get(EST_NOME));
        parametros.put(RSE_TIPO, servidor.get(RSE_TIPO));
        parametros.put(SERVIDOR, servidor.get(SERVIDOR));
        parametros.put(SER_CPF, servidor.get(SER_CPF));
        parametros.put(DATA_NASCIMENTO, servidor.get(DATA_NASCIMENTO));
        parametros.put(RSE_MATRICULA, servidor.get(RSE_MATRICULA));
        parametros.put(RSE_DATA_ADMISSAO, servidor.get(RSE_DATA_ADMISSAO));
        parametros.put(RSE_PRAZO, servidor.get(RSE_PRAZO));
        parametros.put(SRS_CODIGO, servidor.get(SRS_CODIGO));

        parametros.put(SER_NRO_IDT, servidor.get(SER_NRO_IDT));
        parametros.put(SER_DATA_IDT, servidor.get(SER_DATA_IDT));
        parametros.put(SER_UF_IDT, servidor.get(SER_UF_IDT));
        parametros.put(SER_EMISSOR_IDT, servidor.get(SER_EMISSOR_IDT));
        parametros.put(SER_CID_NASC, servidor.get(SER_CID_NASC));
        parametros.put(SER_NACIONALIDADE, servidor.get(SER_NACIONALIDADE));
        parametros.put(SER_SEXO, servidor.get(SER_SEXO));
        parametros.put(SER_EST_CIVIL, servidor.get(SER_EST_CIVIL));
        parametros.put(SER_END, servidor.get(SER_END));
        parametros.put(SER_NRO, servidor.get(SER_NRO));
        parametros.put(SER_COMPL, servidor.get(SER_COMPL));
        parametros.put(SER_BAIRRO, servidor.get(SER_BAIRRO));
        parametros.put(SER_CIDADE, servidor.get(SER_CIDADE));
        parametros.put(SER_UF, servidor.get(SER_UF));
        parametros.put(SER_CEP, servidor.get(SER_CEP));
        parametros.put(SER_TEL, servidor.get(SER_TEL));
        parametros.put(SER_CELULAR, servidor.get(SER_CELULAR));

        parametros.put(RSE_SALARIO, servidor.get(RSE_SALARIO));
        parametros.put(RSE_PROVENTOS, servidor.get(RSE_PROVENTOS));
        parametros.put(RSE_DATA_SAIDA, servidor.get(RSE_DATA_SAIDA));
        parametros.put(RSE_BANCO, servidor.get(RSE_BANCO));
        parametros.put(RSE_AGENCIA, servidor.get(RSE_AGENCIA));
        parametros.put(RSE_CONTA, servidor.get(RSE_CONTA));
        if (!TextHelper.isNull(servidor.get(CARGO_CODIGO))) {
            parametros.put(CARGO_CODIGO, servidor.get(CARGO_CODIGO));
        }
        if (!TextHelper.isNull(servidor.get(CARGO_DESCRICAO))) {
            parametros.put(CARGO_DESCRICAO, servidor.get(CARGO_DESCRICAO));
        }
        if (!TextHelper.isNull(servidor.get(HABITACAO_CODIGO))) {
            parametros.put(HABITACAO_CODIGO, servidor.get(HABITACAO_CODIGO));
        }
        if (!TextHelper.isNull(servidor.get(HABITACAO_DESCRICAO))) {
            parametros.put(HABITACAO_DESCRICAO, servidor.get(HABITACAO_DESCRICAO));
        }
        if (!TextHelper.isNull(servidor.get(ESCOLARIDADE_CODIGO))) {
            parametros.put(ESCOLARIDADE_CODIGO, servidor.get(ESCOLARIDADE_CODIGO));
            if (servidor.get(ESCOLARIDADE_DESCRICAO) != null) {
                parametros.put(ESCOLARIDADE_DESCRICAO, servidor.get(ESCOLARIDADE_DESCRICAO));
            }
        }
        parametros.put(POSTO_CODIGO, servidor.get(POSTO_CODIGO));
        parametros.put(POSTO_DESCRICAO, servidor.get(POSTO_DESCRICAO));

        parametros.put(SER_QTD_FILHOS, servidor.get(SER_QTD_FILHOS));
        parametros.put(COD_RETORNO, servidor.get(COD_RETORNO));

        final Map<String, Object> valoresMargem = (Map<String, Object>) servidor.get(VALORES_MARGEM);
        if ((valoresMargem == null) || valoresMargem.isEmpty()) {
            parametros.put(MENSAGEM, servidor.get(MENSAGEM));
            parametros.put(VALOR_MARGEM, servidor.get(VALOR_MARGEM));
        } else {
            parametros.put(VALORES_MARGEM, valoresMargem);
            if (!TextHelper.isNull(mensagemMargemDisponivel)) {
                parametros.put(MENSAGEM, mensagemMargemDisponivel);
            }
        }

        Map<String, List<MargemTO>> margens = (Map<String, List<MargemTO>>) parametros.get(MARGENS);
        if (margens == null) {
            margens = new HashMap<>();
            parametros.put(MARGENS, margens);
        }
        margens.put((String) servidor.get(RSE_CODIGO), (List<MargemTO>) servidor.get(MARGEM_SERVIDOR));

        if (multipla) {
            // Inclui informação da margem na listagem
            List<Map<CamposAPI, Object>> lstInfoMargem = (List<Map<CamposAPI, Object>>) parametros.get(INFO_MARGEM);
            if (lstInfoMargem == null) {
                lstInfoMargem = new ArrayList<>();
                parametros.put(INFO_MARGEM, lstInfoMargem);
            }
            final Map<CamposAPI, Object> margem = new EnumMap<>(CamposAPI.class);
            margem.putAll(servidor);
            lstInfoMargem.add(margem);
        }
    }

    protected void validaPermiteCsaConsultarMargem(Map<CamposAPI, Object> parametros) throws ZetraException {
        if (responsavel.isCsaCor()) {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
            boolean estaBloqueada = (csa != null) && CodedValues.STS_INATIVO.equals(csa.getCsaAtivo());
            if (responsavel.isCor() && !estaBloqueada) {
                // Se é usuário de correspondente e a CSA não está bloqueada, verifica se o COR está bloqueado
                final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(responsavel.getCorCodigo(), responsavel);
                estaBloqueada = (cor != null) && CodedValues.STS_INATIVO.equals(cor.getCorAtivo());
            }
            if (estaBloqueada) {
                // Consignatária/Correspondente está bloqueado, verifica se pelo parâmetro de CSA permite consultar margem via SOAP
                final String tpaPermiteCsaBloqAutomaticoConsultarMargemSoap = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PERMITE_CSA_BLOQUEIO_AUTOMATICO_CONSULTAR_MARGEM_SOAP, responsavel);
                final boolean permiteCsaBloqAutomaticoConsultarMargemSoap = "S".equals(tpaPermiteCsaBloqAutomaticoConsultarMargemSoap);

                if (!permiteCsaBloqAutomaticoConsultarMargemSoap || TipoMotivoBloqueioEnum.BLOQUEIO_MANUAL.getCodigo().equals(csa.getTmbCodigo())) {
                    // Se não permite a consulta ou está bloqueada manualmente, retorna erro
                    throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
                }
            }
        }
    }

    private BigDecimal getMargemLimite(String rseCodigo, String csaCodigo) throws ServidorControllerException {
        BigDecimal margemLimite = null;

        // Paramêtro para margem limite por consignatária
        final Short codMargemLimite = (TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel))) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : CodedValues.INCIDE_MARGEM_NAO;

        // Verifica se pode mostrar margem limite por csa
        if (!CodedValues.INCIDE_MARGEM_NAO.equals(codMargemLimite)) {
            // verifica se a margem cadastrada no parâmetro existe
            final MargemTO marTO = MargemHelper.getInstance().getMargem(codMargemLimite, responsavel);
            if (marTO != null) {
                final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
                final MargemTO margemLimiteDisponivel = consultarMargemController.consultarMargemLimitePorCsa(rseCodigo, csaCodigo, codMargemLimite, null, responsavel);
                margemLimite = margemLimiteDisponivel.getMrsMargemRest();
            }
        }

        return margemLimite;
    }
}
