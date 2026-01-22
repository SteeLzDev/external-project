alter table tb_termo_adesao
   add TAD_CLASSE_ACAO varchar(100);
 
alter table tb_termo_adesao
   add TAD_EXIBE_APOS_LEITURA char(1) null default 'N';
 
alter table tb_termo_adesao
   add TAD_ENVIA_API_CONSENTIMENTO char(1) null default 'N';
   
 
ALTER TABLE tb_termo_adesao
MODIFY TAD_ENVIA_API_CONSENTIMENTO VARCHAR(40);
 
ALTER TABLE tb_leitura_termo_usuario
ADD LTU_VERSAO_TERMO INT;
 
ALTER TABLE tb_termo_adesao
ADD TAD_VERSAO_TERMO INT;