document.addEventListener("DOMContentLoaded", function () {

    const selectActividad = document.getElementById("actividad");
    const montoSpan = document.getElementById("montoSpan");
    const inputMonto = document.getElementById("monto");

    function actualizarMonto() {
        const option = selectActividad.options[selectActividad.selectedIndex];
        const monto = option.getAttribute("data-monto");

        if (monto) {
            montoSpan.textContent = monto + " ARS";
            inputMonto.value = monto;
        } else {
            montoSpan.textContent = "--";
            inputMonto.value = "";
        }
    }

    // Ejecutar cuando cambie
    selectActividad.addEventListener("change", actualizarMonto);

    // Ejecutar al cargar la página
    actualizarMonto();
});