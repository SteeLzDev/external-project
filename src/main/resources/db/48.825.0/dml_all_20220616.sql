-- DESENV-18046
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO,TFR_DESCRICAO,TFR_RECURSO,TFR_EXIBE_EDICAO)
VALUES ('campo_judicial','Judicial','/relatorios/campos_relatorio/campo_judicial.jsp','N'); 

UPDATE tb_relatorio_filtro
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1
WHERE REL_CODIGO = 'consignacoes' AND RFI_SEQUENCIA >= 14;
        

INSERT INTO tb_relatorio_filtro (REL_CODIGO,TFR_CODIGO,RFI_EXIBE_CSE,RFI_EXIBE_CSA,RFI_EXIBE_COR,RFI_EXIBE_ORG,RFI_EXIBE_SER,RFI_SEQUENCIA,RFI_EXIBE_SUP)
VALUES ('consignacoes','campo_judicial','1','0','0','1','0',14,'1');