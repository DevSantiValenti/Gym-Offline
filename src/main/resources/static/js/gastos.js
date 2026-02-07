$(document).ready(() => {

    const isMobile = window.innerWidth <= 768;

    $("#gastosTable").DataTable({
        responsive: {
            details: {
                type: "inline",
                target: "tr"
            }
        },

        // scrollX: true,
        autoWidth: false,

        order: [[0, "asc"]],

        lengthMenu: [10, 25, 50, 100],
        pageLength: isMobile ? 5 : 10,

        columns: [
            null, // ID
            null, // Nombre
            null, // DNI
            null, // Teléfono
            null, // Actividad
            // null, // Fecha Alta
        ],

        // 👉 Ocultar columnas SOLO en móvil
        // columnDefs: isMobile ? [
        //     { targets: [2], visible: false } // ocultar FECHA en móvil
        // ] : [],


        language: {
            search: "Buscar",
            sLengthMenu: "Mostrar _MENU_ gastos por página",
            info: "Mostrando de _START_ a _END_ de _TOTAL_ gastos",
            infoFiltered: " (Filtrado de _MAX_ gastos)",
            infoEmpty: "No hay coincidencias...",
            zeroRecords: "No hay coincidencias...",
            emptyTable: "No hay coincidencias...",
            paginate: {
                previous: "Anterior",
                next: "Siguiente",
            }
        }
    });
});