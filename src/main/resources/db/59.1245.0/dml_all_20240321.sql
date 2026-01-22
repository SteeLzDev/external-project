-- DESENV-21162
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR, FUN_LIBERA_MARGEM)
VALUES('556', '4', 'Relatório Sintético Gerencial Geral de Consignatária', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('2', '556');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '556');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA) 
VALUES ('286', '2', NULL, 'Sintético Gerencial Geral de Consignatária', 1, 102);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16940', '2', '556', '/v3/listarRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', '286', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16941', '2', '556', '/v3/agendarRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16942', '2', '556', '/v3/downloadArquivo', 'subtipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16943', '2', '556', '/v3/excluirArquivo', 'subtipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16944', '2', '556', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16945', '7', '556', '/v3/listarRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', '286', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16946', '7', '556', '/v3/agendarRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16947', '7', '556', '/v3/downloadArquivo', 'subtipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16948', '7', '556', '/v3/excluirArquivo', 'subtipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16949', '7', '556', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'sintetico_gerencial_csa', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('sintetico_gerencial_csa', '556', '3', 'Relatório Sintético Gerencial Geral de Consignatária', 1, 'S', 'com.zetra.econsig.report.reports.RelatorioSinteticoGerencialGeralCsa', 'com.zetra.econsig.job.process.ProcessaRelatorioSinteticoGerencialGeralCsa', 'com.zetra.econsig.job.jobs.RelatorioSinteticoGerencialGeralCsaJob', 'SinteticoGerencialConsignataria.jasper', NULL, 'SinteticoGerencialConsignatariaCapa.jasper, SinteticoGerencialConsignatariaObjetivo.jasper, SinteticoGerencialConsignatariaZetraSoft.jasper, SinteticoGerencialConsignatariaeConsig.jasper, SinteticoGerencialConsignatariaAverbacao.jasper, SinteticoGerencialConsignatariaAverbacaoGrafico.jasper, SinteticoGerencialConsignatariaAverbacaoApi.jasper, SinteticoGerencialConsignatariaAverbacaoGraficoApi.jasper, SinteticoGerencialConsignatariaMediaCet.jasper, SinteticoGerencialConsignatariaVolumePortabilidade.jasper, SinteticoGerencialConsignatariaVolumePortabilidadeGrafico.jasper, SinteticoGerencialConsignatariaVolumePortabilidadeNse.jasper, SinteticoGerencialConsignatariaBloqDesbloq.jasper, SinteticoGerencialConsignatariaUltimaAtualizacaoCet.jasper, SinteticoGerencialConsignatariaUltimaAtualizacaoCetSvc.jasper, SinteticoGerencialConsignatariaVlrMedioParcelas.jasper, SinteticoGerencialConsignatariaCargosBloqueados.jasper, SinteticoGerencialConsignatariaListCargosBloqueados.jasper, SinteticoGerencialConsignatariaStatusServidores.jasper, SinteticoGerencialConsignatariaDadosUltimoMovimento.jasper, SinteticoGerencialConsignatariaDadoUltimoMovimentoComparado.jasper, SinteticoGerencialConsignatariaConciliacaoOrgao.jasper, SinteticoGerencialConsignatariaInadimplenciaUltMovFin.jasper, SinteticoGerencialConsignatariaIndicadorInsucesso.jasper, SinteticoGerencialConsignatariaParcelasDesconto.jasper, SinteticoGerencialConsignatariaVolumeFinanceiro.jasper', NULL, 30, 'N', NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_csa_multiplo', '0', '2', '0', '0', '0', 2, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_data_execucao', '0', '2', '0', '0', '0', 1, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_envio_email', '0', '1', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_formato_relatorio_pdf', '0', '2', '0', '0', '0', 6, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_periodicidade', '0', '2', '0', '0', '0', 4, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('sintetico_gerencial_csa', 'campo_tipo_agendamento', '0', '2', '0', '0', '0', 3, NULL, '2');

