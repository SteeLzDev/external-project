-- DESENV-13563
UPDATE tb_tipo_param_sist_consignante
SET TPC_DOMINIO = CONCAT('ESCOLHA[1=E-mail', 0x3b, '2=Tela', 0x3b, '3=E-mail ou Tela', 0x3b, '4=SMS', 0x3b, '5=SMS e Email', 0x3b, '6=E-mail e Tela]')
WHERE TPC_CODIGO = '362';
