-- DESENV-13522
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) VALUES ('campo_canal', 'Canal de acesso', '/relatorios/campos_relatorio/campo_canal.jsp', 'N');

-- atualiza posição dos campos antigos
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '8'  WHERE REL_CODIGO = 'auditoria' AND TFR_CODIGO = 'campo_tipo_agendamento'; 
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '9'  WHERE REL_CODIGO = 'auditoria' AND TFR_CODIGO = 'campo_periodicidade'; 
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '10' WHERE REL_CODIGO = 'auditoria' AND TFR_CODIGO = 'campo_data_hora';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '11' WHERE REL_CODIGO = 'auditoria' AND TFR_CODIGO = 'campo_formato_relatorio'; 

-- cria os novos relacionamentos do relatorio com os filtros
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('auditoria', 'campo_est', '1', '1', '0', '0', '0', '4', NULL, '1'); 
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('auditoria', 'campo_org', '1', '1', '0', '0', '0', '5', NULL, '1');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('auditoria', 'campo_matricula', '1', '1', '0', '0', '0', '6', NULL, '1');
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('auditoria', 'campo_canal', '1', '1', '0', '0', '0', '7', NULL, '1');
