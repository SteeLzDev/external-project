/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/02/2021 14:47:57                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_historico_margem_folha                             */
/*==============================================================*/
create table tb_historico_margem_folha
(
   RSE_CODIGO           varchar(32) not null,
   MAR_CODIGO           smallint not null,
   HMA_PERIODO          date not null,
   HMA_DATA             datetime not null,
   HMA_MARGEM_FOLHA     decimal(13,2) not null,
   primary key (RSE_CODIGO, MAR_CODIGO, HMA_PERIODO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_historico_margem_folha add constraint FK_R_827 foreign key (MAR_CODIGO)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;

alter table tb_historico_margem_folha add constraint FK_R_828 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

