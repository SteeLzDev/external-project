package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: CalculoDirf</p>
 * <p>Description: Classe responsável pelos cálculos da DIRF com base nos dados coletados do arquivo</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalculoDirf {
    public List<ConteudoDirf> calcular(ArquivoDirf arquivoDirf) {
        List<ConteudoDirf> listaConteudo = new ArrayList<ConteudoDirf>();

        Short declaracaoExercicio = arquivoDirf.getAnoReferencia();
        Short declaracaoAnoCalendario = arquivoDirf.getAnoCalendario();
        String nomeResponsavel = arquivoDirf.getResponsavelNome();

        for (DeclarantePJ declarante : arquivoDirf.getDeclarantesPJ()) {
            String declaranteNome = declarante.getDeclaranteNome();
            String declaranteCnpj = declarante.getDeclaranteCNPJ();
            Date dataEvento = declarante.getDataEvento();

            for (Declaracao declaracao : declarante.getDeclaracoes()) {
                for (Beneficiario beneficiario : declaracao.getBeneficiarios()) {

                    ConteudoDirf conteudo = new ConteudoDirf();
                    conteudo.setDeclaracaoExercicio(declaracaoExercicio);
                    conteudo.setDeclaracaoAnoCalendario(declaracaoAnoCalendario);
                    conteudo.setDeclaranteNome(declaranteNome);
                    conteudo.setDeclaranteCnpj(declaranteCnpj);
                    conteudo.setDeclaracaoData(dataEvento);
                    conteudo.setDeclaracaoResponsavel(nomeResponsavel);
                    conteudo.setBeneficiarioNome(beneficiario.getBeneficiarioNome());
                    conteudo.setBeneficiarioCpf(beneficiario.getBeneficiarioCpf());

                    Double valorItem_3_1 = Double.valueOf(0);
                    Double valorItem_3_2 = Double.valueOf(0);
                    Double valorItem_3_3 = Double.valueOf(0);
                    Double valorItem_3_4 = Double.valueOf(0);
                    Double valorItem_3_5 = Double.valueOf(0);

                    Double valorItem_4_1 = Double.valueOf(0);
                    Double valorItem_4_2 = Double.valueOf(0);
                    Double valorItem_4_3 = Double.valueOf(0);
                    Double valorItem_4_4 = Double.valueOf(0);
                    Double valorItem_4_5 = Double.valueOf(0);
                    Double valorItem_4_6 = Double.valueOf(0);
                    Double valorItem_4_7 = Double.valueOf(0);

                    Double valorItem_5_1 = Double.valueOf(0);
                    Double valorItem_5_2 = Double.valueOf(0);
                    Double valorItem_5_3 = Double.valueOf(0);

                    Double valorItem_6_1 = Double.valueOf(0);
                    Double valorItem_6_2 = Double.valueOf(0);
                    Double valorItem_6_3 = Double.valueOf(0);
                    Double valorItem_6_4 = Double.valueOf(0);
                    Double valorItem_6_5 = Double.valueOf(0);
                    Double valorItem_6_6 = Double.valueOf(0);

                    for (LancamentoDirf lancamento : beneficiario.getLancamentosComuns()) {
                        Double somaValores = lancamento.getValorTotal();

                        if (lancamento.getTipo().contentEquals("RTRT")) {
                            valorItem_3_1 = somaValores;
                            valorItem_5_1 += lancamento.getValorDecimoTerceiro();
                        } else if (lancamento.getTipo().contentEquals("RTPO")) {
                            valorItem_3_2 = somaValores;
                            valorItem_5_1 -= lancamento.getValorDecimoTerceiro();
                        } else if (lancamento.getTipo().contentEquals("RTDP")) {
                            valorItem_5_1 -= lancamento.getValorDecimoTerceiro();
                        } else if (lancamento.getTipo().contentEquals("RTIRF")) {
                            valorItem_3_5 = somaValores;
                            valorItem_5_1 -= lancamento.getValorDecimoTerceiro();
                            valorItem_5_2 = lancamento.getValorDecimoTerceiro();

                        } else if (lancamento.getTipo().contentEquals("RIP65")) {
                            valorItem_4_1 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIDAC")) {
                            valorItem_4_2 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIMOG")) {
                            valorItem_4_3 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIL96")) {
                            valorItem_4_4 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIPTS")) {
                            valorItem_4_5 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIIRP")) {
                            valorItem_4_6 = somaValores;
                        } else if (lancamento.getTipo().contentEquals("RIO")) {
                            valorItem_4_7 = somaValores;
                        }
                    }

                    for (LancamentoPensaoAlimenticia lancamentoPA : beneficiario.getLancamentosPensaoAlimenticia()) {
                        for (LancamentoDirf lancamento : lancamentoPA.getLancamentosComuns()) {
                            Double somaValores = lancamento.getValorTotal();

                            if (lancamento.getTipo().contentEquals("RTPA")) {
                                valorItem_3_4 += somaValores;
                            }
                        }
                    }

                    for (LancamentoPrevidenciaComplementar lancamentoPC : beneficiario.getLancamentosPrevidenciaComplementar()) {
                        for (LancamentoDirf lancamento : lancamentoPC.getLancamentosComuns()) {
                            Double somaValores = lancamento.getValorTotal();

                            if (lancamento.getTipo().contentEquals("RTPP") || lancamento.getTipo().contentEquals("RTFA") || lancamento.getTipo().contentEquals("RTSP") || lancamento.getTipo().contentEquals("RTEP")) {
                                valorItem_3_3 += somaValores;
                            }
                        }
                    }

                    conteudo.setValorItem_3_1(valorItem_3_1);
                    conteudo.setValorItem_3_2(valorItem_3_2);
                    conteudo.setValorItem_3_3(valorItem_3_3);
                    conteudo.setValorItem_3_4(valorItem_3_4);
                    conteudo.setValorItem_3_5(valorItem_3_5);

                    conteudo.setValorItem_4_1(valorItem_4_1);
                    conteudo.setValorItem_4_2(valorItem_4_2);
                    conteudo.setValorItem_4_3(valorItem_4_3);
                    conteudo.setValorItem_4_4(valorItem_4_4);
                    conteudo.setValorItem_4_5(valorItem_4_5);
                    conteudo.setValorItem_4_6(valorItem_4_6);
                    conteudo.setValorItem_4_7(valorItem_4_7);

                    conteudo.setValorItem_5_1(valorItem_5_1);
                    conteudo.setValorItem_5_2(valorItem_5_2);
                    conteudo.setValorItem_5_3(valorItem_5_3);

                    conteudo.setValorItem_6_1(valorItem_6_1);
                    conteudo.setValorItem_6_2(valorItem_6_2);
                    conteudo.setValorItem_6_3(valorItem_6_3);
                    conteudo.setValorItem_6_4(valorItem_6_4);
                    conteudo.setValorItem_6_5(valorItem_6_5);
                    conteudo.setValorItem_6_6(valorItem_6_6);

                    List<String> complementosBeneficiario = arquivoDirf.getComplementoByChave(beneficiario.getBeneficiarioCpf());
                    String complemento = complementosBeneficiario != null && !complementosBeneficiario.isEmpty() ? complementosBeneficiario.stream().collect(Collectors.joining("\n")) : "";
                    conteudo.setComplemento(complemento);

                    listaConteudo.add(conteudo);
                }
            }
        }

        return listaConteudo;
    }
}
