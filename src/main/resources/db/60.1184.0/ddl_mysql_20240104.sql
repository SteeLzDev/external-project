/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     21/12/2023 10:33:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_saldo_devedor_rse                                  */
/*==============================================================*/
create table tb_saldo_devedor_rse
(
   RSE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   SDR_VALOR            decimal(13,2) not null default 0,
   SDR_DATA             datetime not null,
   primary key (RSE_CODIGO, CSA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_saldo_devedor_rse add constraint FK_R_944 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_saldo_devedor_rse add constraint FK_R_945 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

