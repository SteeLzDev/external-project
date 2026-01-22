/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     22/10/2019 13:56:16                          */
/*==============================================================*/

-- dropping foreign keys
select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'ht_coeficiente_desconto'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;

select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'ht_saldo_devedor'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;

select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'tb_coeficiente_desconto'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;

select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'tb_registro_servidor'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;

select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'tb_registro_servidor_validacao'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;

select @sql := CONCAT("alter table ", TABLE_NAME, " drop foreign key ", CONSTRAINT_NAME) 
  from information_schema.KEY_COLUMN_USAGE
 where TABLE_SCHEMA = DATABASE()
   and TABLE_NAME = 'tb_saldo_devedor'
   and COLUMN_NAME = 'BCO_COD_BCO';

prepare stmt1 from @sql;
execute stmt1;
deallocate prepare stmt1;


rename table bct026_bco to tb_banco;

alter table tb_banco
   drop column BCO_COD_ATIVO;

alter table tb_banco
   drop column BCO_INS_SPB;

alter table tb_banco
   drop column BCO_STS_CRP;

alter table tb_banco
   add BCO_ATIVO bool not null default 1;

update tb_banco set BCO_ATIVO = 0 where BCO_ETT = 'S';

alter table tb_banco
   drop column BCO_ETT;

alter table tb_banco
   change column BCO_COD_BCO BCO_CODIGO smallint not null;

alter table tb_banco
   change column BCO_DSC_BCO BCO_DESCRICAO varchar(40) not null;

alter table tb_banco
   modify column BCO_DESCRICAO varchar(40) not null;

alter table tb_banco
   change column BCO_APE_BCO BCO_IDENTIFICADOR varchar(40) not null;

alter table tb_banco
   modify column BCO_IDENTIFICADOR varchar(40) not null;

update tb_banco set BCO_IDENTIFICADOR = lpad(BCO_CODIGO, 3, '0');

-- foreign keys

alter table ht_coeficiente_desconto
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table ht_coeficiente_desconto add constraint FK_R_518 foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table ht_saldo_devedor
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table ht_saldo_devedor add constraint FK_R_525 foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table tb_coeficiente_desconto
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table tb_coeficiente_desconto add constraint FK_TB_COEFI_R_115_BCT026_B foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table tb_registro_servidor
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table tb_registro_servidor add constraint FK_TB_REGIS_R_114_BCT026_B foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table tb_registro_servidor_validacao
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table tb_registro_servidor_validacao add constraint FK_R_438 foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table tb_saldo_devedor
   change column BCO_COD_BCO BCO_CODIGO smallint;

alter table tb_saldo_devedor add constraint FK_TB_SALDO_R_177_BCT026_B foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;
