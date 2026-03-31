$(document).ready(() => {
    $("#tablaIngresos").DataTable({
        // retrieve: true,
        responsive: true,
        order: [[0, "asc"]],
        lengthMenu: [25, 50, 75, 100],
        columns: [
            null,
            // {orderable: false},
            null,
            null,
            // {orderable: false},
            null,
            // { bSearchable: false, orderable: false },
            null
            // { bSearchable: false, orderable: false }
            // {bSearchable: false},
            // {orderable: false},
        ],
        language: {
            // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
            "search": "Buscar",
            "sLengthMenu": "Mostrar _MENU_ ingresos por página",
            "info": "Mostrando de _START_ a _END_ de _TOTAL_ ingresos",
            "infoFiltered": " (Filtrado de _MAX_ ingresos)",
            "infoEmpty": "No hay coincidencias...",
            "zeroRecords": "No hay nada aquí...",
            "emptyTable": "No hay nada aquí...",
            "paginate" : {
                "previous" : "Anterior",
                "next" : "Siguiente",
            }
        },
    });
});