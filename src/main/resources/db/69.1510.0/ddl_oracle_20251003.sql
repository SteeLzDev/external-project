ALTER TABLE tb_termo_adesao
MODIFY tad_envia_api_consentimento VARCHAR2(40);
 
ALTER TABLE tb_leitura_termo_usuario
ADD ltu_versao_termo NUMBER;
 
ALTER TABLE tb_termo_adesao
ADD tad_versao_termo NUMBER;