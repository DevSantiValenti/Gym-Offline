$(document).ready(() => {
    $("#actividadTable").DataTable({
        // retrieve: true,
        responsive: true,
        order: [[0, "asc"]],
        lengthMenu: [5, 10, 20, 50],
        columns: [
            null,
            // {orderable: false},
            null,
            null,
            // {orderable: false},
            // null,
            { bSearchable: false, orderable: false },
            { bSearchable: false, orderable: false }
            // {bSearchable: false},
            // {orderable: false},
        ],
        language: {
            // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
            "search": "Buscar",
            "sLengthMenu": "Mostrar _MENU_ actividades por página",
            "info": "Mostrando de _START_ a _END_ de _TOTAL_ actividades",
            "infoFiltered": " (Filtrado de _MAX_ actividades)",
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