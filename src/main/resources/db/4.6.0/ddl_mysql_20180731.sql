/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/07/2018 14:19:25                          */
/*==============================================================*/

/*==============================================================*/
/* Table: tb_status_contrato_beneficio                          */
/*==============================================================*/
create table tb_status_contrato_beneficio
(
   SCB_CODIGO           varchar(32) not null,
   SCB_DESCRICAO        varchar(100) not null,
   primary key (SCB_CODIGO)
) ENGINE=InnoDB;

insert into tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) 
  values ('3', 'Ativo'); 


alter table ta_contrato_beneficio
  add SCB_CODIGO varchar(32);

alter table tb_contrato_beneficio
  add SCB_CODIGO varchar(32);


update ta_contrato_beneficio 
  set SCB_CODIGO = '3';

update ta_contrato_beneficio 
  set SCB_CODIGO = '3';


alter table ta_contrato_beneficio
  modify SCB_CODIGO varchar(32) not null;

alter table tb_contrato_beneficio
  modify SCB_CODIGO varchar(32) not null;


alter table ta_contrato_beneficio add constraint FK_R_717 foreign key (SCB_CODIGO)
      references tb_status_contrato_beneficio (SCB_CODIGO) on delete restrict on update restrict;

alter table tb_contrato_beneficio add constraint FK_R_716 foreign key (SCB_CODIGO)
      references tb_status_contrato_beneficio (SCB_CODIGO) on delete restrict on update restrict;


alter table ta_contrato_beneficio
  add CBE_DATA_FIM_VIGENCIA datetime;

alter table tb_contrato_beneficio
  add CBE_DATA_FIM_VIGENCIA datetime;
