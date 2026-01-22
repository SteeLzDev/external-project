document.addEventListener("DOMContentLoaded", function() {
	const filtroPor = document.getElementById("FILTRO_TIPO");
	const filtroUsuario = document.getElementById("FILTRO");

	if (filtroPor.value === "09" /*cpf*/) {
		filtroUsuario.addEventListener("input", tratarEntradaCPF);
	}

	function tratarEntradaCPF() {
		filtroUsuario.value = aplicarMascaraCPF(filtroUsuario.value);
	}

	function aplicarMascaraCPF(value) {
		let cpf = value.replace(/\D/g, '');

		cpf = cpf.substring(0, 11);

		cpf = cpf
			.replace(/(\d{3})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d{1,2})$/, '$1-$2');

		return cpf;
	}

	// Detecta a mudança no filtro "Filtro Tipo"
	filtroPor.addEventListener("change", function() {
		if (filtroPor.value === "09" /*cpf*/) {
			filtroUsuario.addEventListener("input", tratarEntradaCPF);
		} else {
			filtroUsuario.removeEventListener("input", tratarEntradaCPF);
		}
	});
});
