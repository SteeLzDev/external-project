/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/12/2018 15:05:18                          */
/*==============================================================*/


drop table if exists tb_memoria_calculo_subsidio;

/*==============================================================*/
/* Table: tb_memoria_calculo_subsidio                           */
/*==============================================================*/
create table tb_memoria_calculo_subsidio
(
   MCS_CODIGO           varchar(32) not null,
   CBE_CODIGO           varchar(32) not null,
   MCS_PERIODO          date not null,
   MCS_DATA             datetime not null,
   MCS_VALOR_BENEFICIO  decimal(13,2) not null,
   MCS_VALOR_SUBSIDIO   decimal(13,2) not null,
   MCS_OBS              text not null,
   primary key (MCS_CODIGO)
) ENGINE = InnoDB;

alter table tb_memoria_calculo_subsidio add constraint FK_R_695 foreign key (CBE_CODIGO)
      references tb_contrato_beneficio (CBE_CODIGO) on delete restrict on update restrict;

