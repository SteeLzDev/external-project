//usada nas listagens que tenham checkbox e existam colunas que tenham inputs a serem preenchidos manualmente.
//Click no acoes, selecionar
$("[name='selecionaAcaoSelecionar']").click(function() {
	$("table th:first").show();
	$(".ocultarColuna").show();
	$(this).parentsUntil("tbody").toggleClass("table-checked");
	$(this).parentsUntil("tbody").find('input[type="checkbox"]').prop("checked", function(i, val) {
		return !val;
	});
	$("[name='selecionaAcaoSelecionar']").parentsUntil("tr").removeClass("table-checked");
	var qtdCheckboxCheked = $('input[type="checkbox"]').not($("#checkAll")).filter(':checked').length;
	var qtdCheckbox = $('input[type="checkbox"]').not($("#checkAll")).length;
	if (qtdCheckboxCheked == 0) {
		$("table th:first").hide();
		$(".ocultarColuna").hide();
	} else if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAll").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAll").prop('checked', false);
	}
});
  
//Click da linha
$(".selecionarColuna").click(function() {
	$(this).parentsUntil("tbody").toggleClass("table-checked");
	$(this).parent().find('input[type="checkbox"]').prop("checked", function(i, val) {
		$("table th:first").show();
		$(".ocultarColuna").show();
		return !val;
	});
	var qtdCheckboxCheked = $('input[type="checkbox"]').not($("#checkAll")).filter(':checked').length;
	var qtdCheckbox = $('input[type="checkbox"]').not($("#checkAll")).length;
	if (qtdCheckboxCheked == 0) {
		$("table th:first").hide();
		$(".ocultarColuna").hide();
	} else if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAll").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAll").prop('checked', false);
	}
});

//CHECKALL
$("#checkAll").click(function() {$('input[type="checkbox"]').prop("checked",function(i, val) {
		if (i != 0) {
			if ($("#checkAll").is(":checked")) {
				$(this).parentsUntil("tbody",".refLinha").addClass("table-checked");
			} else {
				$(this).parentsUntil("tbody",".refLinha").removeClass("table-checked");
				$("[name='selecionaAcaoSelecionar']").parentsUntil("tr").removeClass("table-checked");
			}
			return $("#checkAll").is(":checked");
		}
	});
		
	if (!$("#checkAll").is(":checked")) {
		$("table th:first").hide();
		$(".ocultarColuna").hide();
	}
});

//Click do check
$("[name='selecionarCheckBox']").click(function() {
	if ($(this).is(":checked")) {
		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
	} else {
		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
	}
	var qtdCheckboxCheked = $('input[type="checkbox"]').not($("#checkAll")).filter(':checked').length;
	var qtdCheckbox = $('input[type="checkbox"]').not($("#checkAll")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAll").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAll").prop('checked', false);
	}
	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
		$("table th:first").hide();
		$(".ocultarColuna").hide();
	}
});

//Oculta colula com checks
function ocultarColuna() {
	$("table th:first").hide();
	$(".ocultarColuna").hide();
}