-- DESENV-17385
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO,TFR_DESCRICAO,TFR_RECURSO,TFR_EXIBE_EDICAO)
VALUES ('campo_tarifacao_por_natureza','Detalhar por natureza de serviço','/relatorios/campos_relatorio/campo_tarifacao_por_natureza.jsp','N');

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO,TFR_DESCRICAO,TFR_RECURSO,TFR_EXIBE_EDICAO)
VALUES ('campo_servico_sem_tarifacao','Listar contratos de serviço que não possuem tarifação','/relatorios/campos_relatorio/campo_servico_sem_tarifacao.jsp','N');

UPDATE  tb_relatorio_filtro
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1
WHERE REL_CODIGO = 'vlr_recebimento' AND RFI_SEQUENCIA >= 5;
        
INSERT INTO tb_relatorio_filtro (REL_CODIGO,TFR_CODIGO,RFI_EXIBE_CSE,RFI_EXIBE_CSA,RFI_EXIBE_COR,RFI_EXIBE_ORG,RFI_EXIBE_SER,RFI_SEQUENCIA,RFI_EXIBE_SUP)
VALUES ('vlr_recebimento','campo_natureza_svc','1','1','0','1','0',5,'1');

UPDATE tb_relatorio_filtro
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2
WHERE REL_CODIGO = 'vlr_recebimento' AND RFI_SEQUENCIA >= 8;
    
INSERT INTO tb_relatorio_filtro (REL_CODIGO,TFR_CODIGO,RFI_EXIBE_CSE,RFI_EXIBE_CSA,RFI_EXIBE_COR,RFI_EXIBE_ORG,RFI_EXIBE_SER,RFI_SEQUENCIA,RFI_EXIBE_SUP)
VALUES ('vlr_recebimento','campo_tarifacao_por_natureza','1','1','0','1','0',8,'1');
    
INSERT INTO tb_relatorio_filtro (REL_CODIGO,TFR_CODIGO,RFI_EXIBE_CSE,RFI_EXIBE_CSA,RFI_EXIBE_COR,RFI_EXIBE_ORG,RFI_EXIBE_SER,RFI_SEQUENCIA,RFI_EXIBE_SUP)
VALUES ('vlr_recebimento','campo_servico_sem_tarifacao','1','1','0','1','0',9,'1');