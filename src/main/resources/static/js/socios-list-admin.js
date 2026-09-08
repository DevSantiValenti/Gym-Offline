$(document).ready(() => {
    const isMobile = window.innerWidth <= 768;

    function escaparHtml(valor) {
        return $("<div>").text(valor ?? "-").html();
    }

    function columnaTexto(campo, etiqueta, opciones = {}) {
        return {
            data: campo,
            orderable: opciones.orderable !== false,
            searchable: opciones.searchable !== false,
            render: function (data, type) {
                return type === "display" ? escaparHtml(data) : data;
            },
            createdCell: function (td) {
                td.setAttribute("data-label", etiqueta);
            }
        };
    }

    $("#sociosTable").DataTable({
        processing: true,
        serverSide: true,
        deferRender: true,
        searchDelay: 350,

        ajax: {
            url: "/socios/api/listado",
            type: "GET"
        },

        responsive: isMobile ? false : {
            details: {
                type: "inline",
                target: "tr"
            }
        },

        autoWidth: false,

        order: [[0, "asc"]],

        lengthMenu: [10, 25, 50, 100],
        pageLength: isMobile ? 5 : 10,

        columns: [
            columnaTexto("numero", "ID", { searchable: false }),
            columnaTexto("nombreCompleto", "Nombre"),
            columnaTexto("dni", "DNI"),
            columnaTexto("telefono", "Teléfono"),
            columnaTexto("actividad", "Actividad"),
            columnaTexto("fechaAlta", "Fecha Alta"),
            columnaTexto("fechaVencimiento", "Vencimiento"),
            {
                data: "saldo",
                searchable: false,
                render: function (data, type) {
                    return type === "display" ? escaparHtml(data) : data;
                },
                createdCell: function (td, cellData, rowData) {
                    td.setAttribute("data-label", "Saldo");
                    td.classList.remove("bg-danger", "bg-success", "fw-bold");
                    td.classList.add(rowData.saldoPendiente > 0 ? "bg-danger" : "bg-success", "fw-bold");
                }
            },
            {
                data: "cuotaTexto",
                searchable: false,
                render: function (data, type, rowData) {
                    if (type !== "display" || data === "-") {
                        return escaparHtml(data);
                    }

                    const clase = rowData.cuotaPaga ? "estado-pagado" : "estado-pendiente";
                    return `<span class="${clase}">${escaparHtml(data)}</span>`;
                },
                createdCell: function (td) {
                    td.setAttribute("data-label", "Cuota");
                }
            },
            {
                data: "acciones",
                searchable: false,
                orderable: false,
                className: "acciones",
                render: function (data, type) {
                    return type === "display" ? data : "";
                },
                createdCell: function (td) {
                    td.setAttribute("data-label", "Acciones");
                }
            }
        ],

        // Ocultar columnas secundarias solo en movil para que la tarjeta no quede larga.
        columnDefs: isMobile ? [
            { targets: [3, 5, 6], visible: false } // Telefono, Fecha Alta, Vencimiento
        ] : [],

        language: {
            processing: "Cargando socios...",
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
