-- DESENV-18027
INSERT INTO tb_funcao (fun_codigo, grf_codigo, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR, FUN_LIBERA_MARGEM)
VALUES('526', '4', 'Relatório Gerencial Geral Internacional', 'N', 'N', 'P', 'S', 'N', 'P', 'N', 'N', 'N', 'N');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, TEX_CHAVE, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_CENTRALIZADOR, ITM_IMAGEM)
VALUES('271', '2', NULL, NULL, 'Gerencial Geral Internacional', 1, 20, 'N', 'S', NULL);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16578', '1', '526', '/v3/listarRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', '271', 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16579', '1', '526', '/v3/agendarRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'S', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16580', '1', '526', '/v3/downloadArquivo', 'subtipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16581', '1', '526', '/v3/excluirArquivo', 'subtipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16582', '1', '526', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16583', '7', '526', '/v3/listarRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', '271', 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16584', '7', '526', '/v3/agendarRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'S', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16585', '7', '526', '/v3/downloadArquivo', 'subtipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');
INSERT INTO tb_acesso_recurso(ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16586', '7', '526', '/v3/excluirArquivo', 'subtipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES('16587', '7', '526', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'gerencial_internacional', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES('gerencial_internacional', '526', '3', 'Relatório Gerencial Geral Internacional', 1, 'S', 'com.zetra.econsig.report.reports.RelatorioGerencialGeralInternacional', 'com.zetra.econsig.job.process.ProcessaRelatorioGerencialGeralInternacional', 'com.zetra.econsig.job.jobs.RelatorioGerencialGeralInternacionalJob', 'GerencialGeralInternacional.jasper', NULL, 'GerencialGeralCapaInternacional.jasper, GerencialGeralSumarioInternacional.jasper, GerencialGeralObjetivoInternacional.jasper, GerencialGeralZetraSoftInternacional.jasper, GerencialGeraleConsigInternacional.jasper, GerencialGeraleConsig1Internacional.jasper, GerencialGeralOrgaoPorServidorInternacional.jasper, GerencialGeralServidoresInternacional.jasper, GerencialGeralServidoresPorCargoInternacional.jasper, GerencialGeralMargemInternacional.jasper, GerencialGeralMargem1Internacional.jasper, GerencialGeralFaixaMargem1Internacional.jasper, GerencialGeralComprometimentoMargem1Internacional.jasper, GerencialGeralMargem2Internacional.jasper, GerencialGeralFaixaMargem2Internacional.jasper, GerencialGeralComprometimentoMargem2Internacional.jasper, GerencialGeralMargem3Internacional.jasper, GerencialGeralFaixaMargem3Internacional.jasper, GerencialGeralComprometimentoMargem3Internacional.jasper, GerencialGeralCsaInternacional.jasper, GerencialGeralCorInternacional.jasper, GerencialGeralContratosCategoriaInternacional.jasper, GerencialGeralContratosPorCargoInternacional.jasper, GerencialGeralContratosServicoInternacional.jasper, GerencialGeralInadimplenciaInternacional.jasper, GerencialGeralConsideracoesInternacional.jasper, GerencialGeralTaxasInternacional.jasper, GerencialGeralTaxasEfetivasInternacional.jasper', NULL, 30, 'N', NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_data_execucao', '2', '0', '0', '0', '0', 2, NULL, '2');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_data_periodo', '1', '0', '0', '0', '0', 1, NULL, '1');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_envio_email', '1', '0', '0', '0', '0', 5, NULL, '1');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_formato_relatorio_pdf', '2', '0', '0', '0', '0', 6, NULL, '2');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_periodicidade', '2', '0', '0', '0', '0', 4, NULL, '2');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('gerencial_internacional', 'campo_tipo_agendamento', '2', '0', '0', '0', '0', 3, NULL, '2');
