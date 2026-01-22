-- DESENV-15938
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 2 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_csa';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 3 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_data_periodo_base';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 4 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_org';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 6 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_svc';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 7 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_agendado';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 8 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_data_execucao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 9 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_tipo_agendamento';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 10 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_periodicidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 11 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_envio_email';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 12 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_formato_relatorio';

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_natureza_csa', 'Natureza de Consignatária', '/relatorios/campos_relatorio/campo_natureza_csa.jsp', 'N');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('estatistico', 'campo_natureza_csa', '1', '0', '0', '0', '0', 1, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('estatistico', 'campo_natureza_svc', '1', '0', '0', '0', '0', 5, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('estatistico', 'campo_status_servidor', '1', '0', '0', '0', '0', 13, '', '1');

UPDATE tb_ordem_relatorio_estatistico SET ORD_ETT_DESCRICAO = 'X% de Custeio da Operação' WHERE ORD_ETT_ORDEM = 11;

INSERT INTO tb_ordem_relatorio_estatistico (ORD_ETT_ORDEM, ORD_ETT_DESCRICAO)
VALUES (12, 'R$ X de Custeio da Operação');

INSERT INTO tb_ordem_relatorio_estatistico (ORD_ETT_ORDEM, ORD_ETT_DESCRICAO)
VALUES (13, 'Custeio da Operação');

INSERT INTO tb_ordem_relatorio_estatistico (ORD_ETT_ORDEM, ORD_ETT_DESCRICAO)
VALUES (14, 'Total Liquidados no Período');

INSERT INTO tb_ordem_relatorio_estatistico (ORD_ETT_ORDEM, ORD_ETT_DESCRICAO)
VALUES (15, 'Total Alterados no Período');