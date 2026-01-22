-- DESENV-9339

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('427', '4', 'Relatório de Documentos de Beneficiários por Tipo e Validade', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '427');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR)
VALUES ('205', '2', NULL, 'Documentos de Beneficiários por Tipo e Validade', 1, 43, 'N');

INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('doc_beneficiario_tipo_validade', '427', NULL, 'Relatório de Documentos de Beneficiários por Tipo e Validade', 1, 'N', 'com.zetra.report.reports.RelatorioDocumentoBeneficiarioTipoValidade', 'com.zetra.processamento.ProcessaRelatorioDocumentoBeneficiarioTipoValidade', NULL, 'DocumentoBeneficiarioTipoValidade.jasper', NULL, NULL, NULL, 30, 'N', NULL);

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_tipo_documento', 'Tipo de Documento', '/relatorios/campos_relatorio/campo_tipo_documento.jsp', 'S');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_tipo_documento', '0', '0', '0', '0', '0', 2, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_data_inicio_fim', '0', '0', '0', '0', '0', 1, '90', '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_formato_relatorio', '0', '0', '0', '0', '0', 3, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_agendado', '0', '0', '0', '0', '0', 4, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_data_execucao', '0', '0', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_tipo_agendamento', '0', '0', '0', '0', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('doc_beneficiario_tipo_validade', 'campo_periodicidade', '0', '0', '0', '0', '0', 7, NULL, '1');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14909', '7', '427', '/v3/listarRelatorio', 'tipo', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', "205", 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14910', '7', '427', '/v3/executarRelatorio', 'tipoRelatorio', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14911', '7', '427', '/v3/agendarRelatorio', 'tipo', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14912', '7', '427', '/v3/downloadArquivo', 'subtipo', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14913', '7', '427', '/v3/excluirArquivo', 'subtipo', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO,PAP_CODIGO,FUN_CODIGO,ACR_RECURSO,ACR_PARAMETRO,ACR_OPERACAO,ACR_ATIVO,ACR_BLOQUEIO,ACR_SESSAO,ITM_CODIGO,ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14914', '7', '427', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'doc_beneficiario_tipo_validade', 1, 'S', 'S', null, 'N', '2');
