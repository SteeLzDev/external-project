package com.zetra.econsig.folha.dirf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import com.zetra.econsig.helper.texto.CharsetDetector;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ArquivoDirfParser</p>
 * <p>Description: Parser para ler dados de arquivo de DIRF de acordo com o padrão da Receita Federal do Brasil</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoDirfParser {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoDirfParser.class);

    public ArquivoDirf parse(String nomeArquivo) {
        // Tenta identificar o charset do arquivo de entrada, para que a leitura seja feita sem inconsistências
        String charsetFile = null;
        try {
            charsetFile = CharsetDetector.detect(nomeArquivo);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Caso não tenha detectado o charset do arquivo, assume ISO-8859-1 como padrão
        if (charsetFile == null) {
            charsetFile = "ISO-8859-1".intern();
        }

        BufferedReader input = null;
        try {
            // Abro o arquivo de entrada usando o charset identificado
            input = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo), charsetFile));

            // Resultado do processamento
            ArquivoDirf arquivoDirf = null;

            // Variáveis usadas durante o preenchimento da árvore de objetos
            DeclarantePJ declarante = null;
            Declaracao declaracao = null;
            Beneficiario beneficiario = null;
            LancamentoPrevidenciaComplementar lancamentoPrevidenciaComplementar = null;
            LancamentoPensaoAlimenticia lancamentoPensaoAlimenticia = null;

            // Para cada linha de entrada, identifica o registro a que se refere a linha
            String line = null;
            while ((line = input.readLine()) != null) {
                // O caractere "|" (pipe ou barra vertical: caractere 124 da Tabela ASCII) é usado como delimitador de campo.
                // Para ser usado no split, que recebe uma empressão regular, devemos fazer o escape do caractere
                String[] lineValues = line.split("\\|");

                if (lineValues.length > 0) {
                    // O primeiro campo é o "Identificador de registro"
                    String register = lineValues[0];

                    if (register.equalsIgnoreCase("DIRF")) {
                        // Declaração do imposto sobre a renda retido na fonte
                        arquivoDirf = new ArquivoDirf();

                        // 1 - Ano referência
                        arquivoDirf.setAnoReferencia(Short.valueOf(lineValues[1]));
                        // 2 - Ano-calendário
                        arquivoDirf.setAnoCalendario(Short.valueOf(lineValues[2]));
                        // 3 - Indicador de retificadora
                        arquivoDirf.setRetificadora(lineValues[3]);
                        // 4 - Número do recibo
                        arquivoDirf.setRecibo(lineValues[4]);
                        // 5 - Identificador de estrutura do leiaute
                        arquivoDirf.setLeiaute(lineValues[5]);

                    } else if (register.equalsIgnoreCase("RESPO")) {
                        // Responsável pelo preenchimento

                        // 1 - CPF
                        arquivoDirf.setResponsavelCpf(TextHelper.format(lineValues[1], "000.000.000-00"));
                        // 2 - Nome
                        arquivoDirf.setResponsavelNome(lineValues[2]);
                        // 3 - DDD
                        arquivoDirf.setResponsavelDDDTelefone(lineValues[3]);
                        // 4 - Telefone
                        arquivoDirf.setResponsavelNumeroTelefone(lineValues[4]);
                        // 5 - Ramal
                        arquivoDirf.setResponsavelRamal(lineValues[5]);
                        // 6 - Fax
                        arquivoDirf.setResponsavelFax(lineValues[6]);
                        // 7 - Correio eletrônico
                        arquivoDirf.setResponsavelEmail(lineValues[7]);

                    } else if (register.equalsIgnoreCase("DECPJ")) {
                        // Declarante pessoa jurídica
                        declarante = new DeclarantePJ();
                        arquivoDirf.addDeclarantePJ(declarante);

                        // 1  - CNPJ
                        declarante.setDeclaranteCNPJ(TextHelper.format(lineValues[1], "00.000.000/0000-00"));
                        // 2  - Nome empresarial
                        declarante.setDeclaranteNome(lineValues[2]);
                        // 3  - Natureza do declarante
                        declarante.setDeclaranteNatureza(lineValues[3]);
                        // 4  - CPF responsável perante o CNPJ
                        declarante.setDeclaranteCPFResponsavel(TextHelper.format(lineValues[4], "000.000.000-00"));
                        // 5  - Indicador de sócio ostensivo responsável por sociedade em conta de participação - SCP
                        declarante.setDeclaranteSocioOstensivo(lineValues[5]);
                        // 6  - Indicador de declarante depositário de crédito decorrente de decisão judicial
                        declarante.setDeclaranteDepositarioCreditoJudicial(lineValues[6]);
                        // 7  - Indicador de declarante de instituição administradora ou intermediadora de fundo ou clube de investimento
                        declarante.setDeclaranteAdministradoraFundoInvestimento(lineValues[7]);
                        // 8  - Indicador de declarante de rendimentos pagos a residentes ou domiciliados no exterior
                        declarante.setDeclaranteRendimentosPagosResidentesExterior(lineValues[8]);
                        // 9  - Indicador de plano privado de assistência à saúde - coletivo empresarial
                        declarante.setDeclarantePlanoPrivadoAssistenciaSaude(lineValues[9]);
                        // 10 - Indicador de entidade em que a União detém maioria do capital social sujeito a voto, recebe recursos do Tesouro Nacional e está obrigada a registrar a execução orçamentária no Siafi (IN 1.234/2012, art. 4o, incisos III e IV)
                        declarante.setDeclaranteEntidadeUniaoMajoritario(lineValues[10]);
                        // 11 - Indicador de fundação pública de direito privado instituída pela União, Estados, Municípios ou Distrito Federal
                        declarante.setDeclaranteFundacaoPublica(lineValues[11]);
                        // 12 - Indicador de situação especial da declaração
                        declarante.setSituacaoEspecial(lineValues[12]);
                        // 13 - Data do evento
                        Date dataEvento = null;
                        if (lineValues.length > 13 && lineValues[13] != null) {
                            try {
                                dataEvento = DateHelper.parse(lineValues[13], "yyyyMMdd");
                            } catch (ParseException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }
                        if (dataEvento == null) {
                            LOG.warn("Data evento não informada. Assumindo data atual.");
                            dataEvento = DateHelper.getSystemDate();
                        }
                        declarante.setDataEvento(dataEvento);

                    } else if (register.equalsIgnoreCase("IDREC")) {
                        // Identificação do código de receita
                        declaracao = new Declaracao();
                        declarante.addDeclaracao(declaracao);

                        // 1 - Código de receita
                        declaracao.setCodigoReceita(lineValues[1]);

                    } else if (register.equalsIgnoreCase("BPFDEC")) {
                        // Beneficiário pessoa física do declarante
                        beneficiario = new Beneficiario();
                        declaracao.addBeneficiario(beneficiario);

                        // 1 - CPF
                        beneficiario.setBeneficiarioCpf(TextHelper.format(lineValues[1], "000.000.000-00"));
                        // 2 - Nome
                        beneficiario.setBeneficiarioNome(lineValues[2]);
                        // 3 - Data atribuída pelo laudo da moléstia grave
                        beneficiario.setBeneficiarioDataMolestiaGrave(lineValues[3]);
                        // 4 - Indicador de identificação do alimentando
                        beneficiario.setBeneficiarioIdentificacaoAlimentando(lineValues[4]);
                        // 5 - Indicador de identificação da previdência complementar
                        beneficiario.setBeneficiarioIdentificacaoPrevidenciaComplementar(lineValues[5]);

                    } else if (register.equalsIgnoreCase("INFPA")) {
                        // Informações do beneficiário da pensão alimentícia
                        lancamentoPensaoAlimenticia = new LancamentoPensaoAlimenticia();
                        beneficiario.addLancamentoPensaoAlimenticia(lancamentoPensaoAlimenticia);

                        // 1 - CPF do alimentando
                        if (!TextHelper.isNull(lineValues[1])) {
                            lancamentoPensaoAlimenticia.setAlimentandoCpf(TextHelper.format(lineValues[1], "000.000.000-00"));
                        }
                        // 2 - Data de nascimento
                        lancamentoPensaoAlimenticia.setAlimentandoDataNascimento(lineValues[2]);
                        // 3 - Nome
                        lancamentoPensaoAlimenticia.setAlimentandoNome(lineValues[3]);
                        // 4 - Relação de dependência
                        lancamentoPensaoAlimenticia.setRelacaoDependencia(lineValues[4]);

                    } else if (register.equalsIgnoreCase("INFPC")) {
                        // Informações de Previdência Complementar
                        lancamentoPrevidenciaComplementar = new LancamentoPrevidenciaComplementar();
                        beneficiario.addLancamentoPrevidenciaComplementar(lancamentoPrevidenciaComplementar);

                        // 1 - CNPJ
                        lancamentoPrevidenciaComplementar.setCnpj(lineValues[1]);
                        // 2 - Nome empresarial
                        lancamentoPrevidenciaComplementar.setNome(lineValues[2]);

                    } else if (
                            register.equalsIgnoreCase("CJAA")  || // CJAA  - Compensação de Imposto por Decisão Judicial - Anos Anteriores
                            register.equalsIgnoreCase("CJAC")  || // CJAC  - Compensação de Imposto por Decisão Judicial - Ano-calendário
                            register.equalsIgnoreCase("DAJUD") || // DAJUD - Despesa com ação judicial
                            register.equalsIgnoreCase("ESDJ")  || // ESDJ  - Tributação com Exigibilidade Suspensa - Depósito Judicial
                            register.equalsIgnoreCase("ESDP")  || // ESDP  - Tributação com Exigibilidade Suspensa - Dedução - Dependentes
                            register.equalsIgnoreCase("ESEP")  || // ESEP  - Tributação com Exigibilidade Suspensa - Dedução - Contribuição do ente público patrocinador
                            register.equalsIgnoreCase("ESFA")  || // ESFA  - Tributação com Exigibilidade Suspensa - Dedução - FAPI
                            register.equalsIgnoreCase("ESIR")  || // ESIR  - Tributação com Exigibilidade Suspensa - Imposto sobre a Renda na Fonte
                            register.equalsIgnoreCase("ESPA")  || // ESPA  - Tributação com Exigibilidade Suspensa - Dedução - Pensão Alimentícia
                            register.equalsIgnoreCase("ESPO")  || // ESPO  - Tributação com Exigibilidade Suspensa - Dedução - Previdência Oficial
                            register.equalsIgnoreCase("ESPP")  || // ESPP  - Tributação com Exigibilidade Suspensa - Dedução - Previdência Privada
                            register.equalsIgnoreCase("ESRT")  || // ESRT  - Tributação com Exigibilidade Suspensa - Rendimento Tributável
                            register.equalsIgnoreCase("ESSP")  || // ESSP  - Tributação com Exigibilidade Suspensa - Dedução - Fundo de Previdência do Servidor Público
                            register.equalsIgnoreCase("RIAP")  || // RIAP  - Rendimentos Isentos - Abono Pecuniário
                            register.equalsIgnoreCase("RIBMR") || // RIBMR - Rendimentos Isentos - Bolsa de Estudo Recebida por Médico-residente
                            register.equalsIgnoreCase("RICAP") || // RICAP - Rendimentos Isentos - Complementação de aposentadoria de previdência complementar correspondente às contribuições efetuadas no período de 1º de janeiro de 1989 a 31 de dezembro de 1995
                            register.equalsIgnoreCase("RIDAC") || // RIDAC - Rendimentos Isentos - Diária e Ajuda de Custo
                            register.equalsIgnoreCase("RIIRP") || // RIIRP - Rendimentos Isentos - Indenizações por Rescisão de Contrato de Trabalho, inclusive a título de PDV
                            register.equalsIgnoreCase("RIMOG") || // RIMOG - Rendimentos Isentos - Pensão, Aposentadoria ou Reforma por Moléstia Grave
                            register.equalsIgnoreCase("RIMUN") || // RIMUM - Rendimentos Imunes - art. 4º, inciso III
                            register.equalsIgnoreCase("RIP65") || // RIP65 - Rendimentos Isentos - Parcela Isenta de Aposentadoria para Maiores de 65 anos
                            register.equalsIgnoreCase("RISCP") || // RISCP - Lucros e dividendos pagos ao sócio da sociedade em conta de participação
                            register.equalsIgnoreCase("RISEN") || // RISEN - Rendimentos Isentos - art. 4º, inciso IV
                            register.equalsIgnoreCase("RIVC")  || // ????
                            register.equalsIgnoreCase("RTDP")  || // RTDP  - Rendimentos Tributáveis - Dedução - Dependentes
                            register.equalsIgnoreCase("RTEP")  || // RTEP  - Rendimentos Tributáveis - Dedução - Contribuição do ente público patrocinador
                            register.equalsIgnoreCase("RTFA")  || // RTFA  - Rendimentos Tributáveis - Dedução - FAPI
                            register.equalsIgnoreCase("RTIRF") || // RTIRF - Rendimentos Tributáveis - Imposto sobre a Renda Retido na Fonte
                            register.equalsIgnoreCase("RTPA")  || // RTPA  - Rendimentos Tributáveis - Dedução - Pensão Alimentícia
                            register.equalsIgnoreCase("RTPO")  || // RTPO  - Rendimentos Tributáveis - Dedução - Previdência Oficial
                            register.equalsIgnoreCase("RTPP")  || // RTPP  - Rendimentos Tributáveis - Dedução - Previdência Privada
                            register.equalsIgnoreCase("RTRT")  || // RTRT  - Rendimentos Tributáveis - Rendimento Tributável
                            register.equalsIgnoreCase("RTSP")) {  // RTSP  - Rendimentos Tributáveis - Dedução - Fundo de Previdência do Servidor Público

                        // 1  - Janeiro
                        // 2  - Fevereiro
                        // 3  - Março
                        // 4  - Abril
                        // 5  - Maio
                        // 6  - Junho
                        // 7  - Julho
                        // 8  - Agosto
                        // 9  - Setembro
                        // 10 - Outubro
                        // 11 - Novembro
                        // 12 - Dezembro
                        // 13 - Décimo Terceiro
                        Double[] valoresMensais = Arrays
                                .stream(Arrays.copyOfRange(lineValues, 1, lineValues.length))
                                .map(s -> Double.parseDouble(s) / 100.00)
                                .toArray(Double[]::new);

                        LancamentoDirf lancamento = new LancamentoDirf();
                        lancamento.setTipo(lineValues[0]);
                        lancamento.setValoresMensais(valoresMensais);

                        if (register.equalsIgnoreCase("RTPA") ||
                            register.equalsIgnoreCase("ESPA")) {
                            // Depende de um elemento INFPA
                            lancamentoPensaoAlimenticia.addLancamentoComun(lancamento);

                        } else if (register.equalsIgnoreCase("RTPP") ||
                                   register.equalsIgnoreCase("RTFA") ||
                                   register.equalsIgnoreCase("RTSP") ||
                                   register.equalsIgnoreCase("RTEP") ||
                                   register.equalsIgnoreCase("ESPP") ||
                                   register.equalsIgnoreCase("ESFA") ||
                                   register.equalsIgnoreCase("ESSP") ||
                                   register.equalsIgnoreCase("ESEP")) {
                            // Depende de um elemento INFPC
                            lancamentoPrevidenciaComplementar.addLancamentoComun(lancamento);

                        } else {
                            // Logo associado ao BPFDEC
                            beneficiario.addLancamentoComun(lancamento);
                        }

                    } else if (
                            register.equalsIgnoreCase("RIL96")  || // RIL96 - Rendimentos Isentos Anuais - Lucros e dividendos pagos a partir de 1996
                            register.equalsIgnoreCase("RIPTS")  || // RIPTS - Rendimentos Isentos Anuais - Valores pagos a titular ou sócio ou empresa de pequeno porte, exceto pró-labore e aluguéis
                            register.equalsIgnoreCase("RIRSR")  || // RIRSR - Rendimentos pagos sem retenção do IR na fonte - Lei n. 10.833/2003
                            register.equalsIgnoreCase("RIO")) {    // RIO   - Rendimentos Isentos Anuais - Outros

                        // 1 - Valor pago no ano
                        Double valorAnual = Double.parseDouble(lineValues[1]) / 100.00;

                        LancamentoDirf lancamento = new LancamentoDirf();
                        lancamento.setTipo(lineValues[0]);
                        lancamento.setValorAnual(valorAnual);

                        if (register.equalsIgnoreCase("RIO")) {
                            // 2 - Descrição dos rendimentos isentos - outros
                            lancamento.setDetalhe(lineValues[2]);
                        }

                        // Logo associado ao BPFDEC
                        beneficiario.addLancamentoComun(lancamento);

                    } else if (register.equalsIgnoreCase("INF")) {
                        // Informações complementares para o comprovante de rendimentos
                        // 1 - CPF
                        // 2 - Informações complementares
                        arquivoDirf.addComplemento(TextHelper.format(lineValues[1], "000.000.000-00"), lineValues[2]);

                    } else if (!register.equalsIgnoreCase("FIMDIRF")) {
                        System.err.println("Tipo de registro não reconhecido: " + line);

                    }
                }
            }

            return arquivoDirf;

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }
}
