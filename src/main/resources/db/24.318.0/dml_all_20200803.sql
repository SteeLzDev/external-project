-- DESENV-14176
REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'alterarSenha.ser.email', 
       CASE PSI_VLR WHEN 'S' THEN 'O' ELSE 'N' END 
FROM tb_param_sist_consignante 
WHERE TPC_CODIGO = '493';

REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'alterarSenha.ser.telefone', 
       CASE PSI_VLR WHEN 'S' THEN 'O' ELSE 'N' END 
FROM tb_param_sist_consignante 
WHERE TPC_CODIGO = '611';

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
VALUES ('alterarSenha.ser.celular', 'N');
