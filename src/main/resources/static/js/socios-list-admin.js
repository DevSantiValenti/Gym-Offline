// $(document).ready(function () {
//     $('#sociosTable').DataTable({
//         "language": {
//             "url": "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
//         },
//         "pageLength": 10,
//         "ordering": true,
//         "searching": true,
//         "paging": true,
//         "info": true,
//         "autoWidth": false,
//         "dom": '<"top"f>rt<"bottom"lip><"clear">'
//     });
// });
$(document).ready(() => {
    $("#sociosTable").DataTable({
        // retrieve: true,
        responsive: true,
        order: [[0, "asc"]],
        lengthMenu: [10, 25, 50, 100],
        columns: [
            null,
            // {orderable: false},
            null,
            null,
            {orderable: false},
            null,
            null,
            null,
            { bSearchable: false, orderable: false },
            { bSearchable: false, orderable: false },
            { bSearchable: false, orderable: false }
            // {bSearchable: false},
            // {bSearchable: false},
            // {orderable: false},
            // {orderable: false},
            // {orderable: false},
        ],
        language: {
            // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
            "search": "Buscar",
            "sLengthMenu": "Mostrar _MENU_ registros por página",
            "info": "Mostrando de _START_ a _END_ de _TOTAL_ socios",
            "infoFiltered": " (Filtrado de _MAX_ socios)",
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