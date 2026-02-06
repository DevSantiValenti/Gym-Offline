
// $(document).ready(() => {
//     $("#sociosTable").DataTable({
//         // retrieve: true,
//         responsive: true,
//         order: [[0, "asc"]],
//         lengthMenu: [10, 25, 50, 100],
//         columns: [
//             null,
//             // {orderable: false},
//             null,
//             null,
//             {orderable: false},
//             null,
//             null,
//             null,
//             { bSearchable: false, orderable: false },
//             { bSearchable: false, orderable: false },
//             { bSearchable: false, orderable: false }
//             // {bSearchable: false},
//             // {bSearchable: false},
//             // {orderable: false},
//             // {orderable: false},
//             // {orderable: false},
//         ],
//         language: {
//             // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
//             "search": "Buscar",
//             "sLengthMenu": "Mostrar _MENU_ registros por página",
//             "info": "Mostrando de _START_ a _END_ de _TOTAL_ socios",
//             "infoFiltered": " (Filtrado de _MAX_ socios)",
//             "infoEmpty": "No hay coincidencias...",
//             "zeroRecords": "No hay nada aquí...",
//             "emptyTable": "No hay nada aquí...",
//             "paginate" : {
//                 "previous" : "Anterior",
//                 "next" : "Siguiente",
//             }
//         },
//     });
// });
$(document).ready(() => {

    const isMobile = window.innerWidth <= 768;

    $("#sociosTable").DataTable({
        responsive: {
            details: {
                type: "inline",
                target: "tr"
            }
        },

        scrollX: true,
        autoWidth: false,

        order: [[0, "asc"]],

        lengthMenu: [10, 25, 50, 100],
        pageLength: isMobile ? 5 : 10,

        columns: [
            null, // ID
            null, // Nombre
            null, // DNI
            { orderable: false }, // Teléfono
            null, // Actividad
            null, // Fecha Alta
            null, // Vencimiento
            { searchable: false, orderable: false }, // Saldo
            { searchable: false, orderable: false }, // Cuota
            { searchable: false, orderable: false }  // Acciones
        ],

        // 👉 Ocultar columnas SOLO en móvil
        columnDefs: isMobile ? [
            { targets: [3, 5, 6], visible: false } // DNI, Tel, Fechas
        ] : [],

        language: {
            search: "Buscar",
            sLengthMenu: "Mostrar _MENU_ socios por página",
            info: "Mostrando de _START_ a _END_ de _TOTAL_ socios",
            infoFiltered: " (Filtrado de _MAX_ socios)",
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
