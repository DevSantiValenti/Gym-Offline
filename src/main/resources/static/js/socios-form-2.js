

document.addEventListener("DOMContentLoaded", function () {

    const selectActividad = document.getElementById("actividad");
    const montoSpan = document.getElementById("montoSpan");
    const inputMonto = document.getElementById("monto");
    const form = document.querySelector("form");
    const submitButton = form ? form.querySelector('button[type="submit"]') : null;
    let formularioEnviado = false;

    function actualizarMonto() {
        if (!selectActividad || !montoSpan || !inputMonto) {
            return;
        }

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

    if (selectActividad) {
        // Ejecutar cuando cambie
        selectActividad.addEventListener("change", actualizarMonto);
    }

    // Ejecutar al cargar la página
    actualizarMonto();

    if (form && submitButton) {
        form.addEventListener("submit", function (event) {
            if (formularioEnviado) {
                event.preventDefault();
                return;
            }

            formularioEnviado = true;
            submitButton.disabled = true;
            submitButton.dataset.textoOriginal = submitButton.textContent;
            submitButton.textContent = "REGISTRANDO...";
        });
    }
});
